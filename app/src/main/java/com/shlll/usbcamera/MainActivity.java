package com.shlll.usbcamera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.usb.UsbDevice;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.serenegiant.usb.DeviceFilter;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.UVCCamera;
import com.serenegiant.utils.HandlerThreadHandler;
import com.serenegiant.widget.SimpleUVCCameraTextureView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    private final Object mSync = new Object();
    private USBMonitor mUSBMonitor;
    private UVCCamera mUVCCamera;
    private SimpleUVCCameraTextureView mUVCCameraView;
    // for open&start / stop&close camera preview
    private ImageButton mCameraButton;
    private Surface mPreviewSurface;

    /** Event Handler */
    private Handler mWorkerHandler;
    private long mWorkerThreadID = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mWorkerHandler == null) {
            mWorkerHandler = HandlerThreadHandler.createHandler(TAG);
            mWorkerThreadID = mWorkerHandler.getLooper().getThread().getId();
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCapturePicture();
            }
        });

        mUVCCameraView = (SimpleUVCCameraTextureView)findViewById(R.id.camera_view);
        mUVCCameraView.setAspectRatio(UVCCamera.DEFAULT_PREVIEW_WIDTH / (float)UVCCamera.DEFAULT_PREVIEW_HEIGHT);

        mUSBMonitor = new USBMonitor(this, mOnDeviceConnectListener);

        checkFilePermission();
    }

    private final USBMonitor.OnDeviceConnectListener mOnDeviceConnectListener = new USBMonitor.OnDeviceConnectListener() {
        @Override
        public void onAttach(final UsbDevice device) {
            showShortMsg(getResources().getString(R.string.msg_usb_device_attached));
            requestPermission();
        }

        @Override
        public void onConnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock, final boolean createNew) {
            releaseCamera();
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    final UVCCamera camera = new UVCCamera();
                    camera.open(ctrlBlock);

                    if (mPreviewSurface != null) {
                        mPreviewSurface.release();
                        mPreviewSurface = null;
                    }
                    try {
                        camera.setPreviewSize(UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT, UVCCamera.FRAME_FORMAT_MJPEG);
                    } catch (final IllegalArgumentException e) {
                        // fallback to YUV mode
                        try {
                            camera.setPreviewSize(UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT, UVCCamera.DEFAULT_PREVIEW_MODE);
                        } catch (final IllegalArgumentException e1) {
                            camera.destroy();
                            return;
                        }
                    }
                    final SurfaceTexture st = mUVCCameraView.getSurfaceTexture();
                    if (st != null) {
                        mPreviewSurface = new Surface(st);
                        camera.setPreviewDisplay(mPreviewSurface);
//						camera.setFrameCallback(mIFrameCallback, UVCCamera.PIXEL_FORMAT_RGB565/*UVCCamera.PIXEL_FORMAT_NV21*/);
                        camera.startPreview();
                    }
                    synchronized (mSync) {
                        mUVCCamera = camera;
                    }
                }
            }, 0);
        }

        @Override
        public void onDisconnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock) {
            releaseCamera();
        }

        @Override
        public void onDettach(final UsbDevice device) {
            showShortMsg(getResources().getString(R.string.msg_usb_device_detached));
        }

        @Override
        public void onCancel(final UsbDevice device) {
        }
    };

    /**
     * Run runnable specified on worker thread
     * the same runnable that is unexecuted is cancelled (executed only later)
     * @param task
     * @param delayMillis
     */
    protected final synchronized void queueEvent(final Runnable task, final long delayMillis) {
        if ((task == null) || (mWorkerHandler == null)) return;
        try {
            mWorkerHandler.removeCallbacks(task);
            if (delayMillis > 0) {
                mWorkerHandler.postDelayed(task, delayMillis);
            } else if (mWorkerThreadID == Thread.currentThread().getId()) {
                task.run();
            } else {
                mWorkerHandler.post(task);
            }
        } catch (final Exception e) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(getResources().getString(R.string.diag_about_title))
                    .setMessage(getResources().getString(R.string.diag_about_message));

            AlertDialog dialog = builder.create();
            dialog.show();
        }

        return true;
    }

    private void checkFilePermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    showShortMsg(getResources().getString(R.string.msg_permissiondeny));
                    MainActivity.this.finish();
                }
                return;
            }
        }
    }

    public void requestPermission() {
        final List<DeviceFilter> filter = DeviceFilter.getDeviceFilters(MainActivity.this, com.shlll.libusbcamera.R.xml.device_filter);
        final List<UsbDevice> deviceList = mUSBMonitor.getDeviceList(filter);

        if (deviceList == null || deviceList.size() == 0) {
            return;
        }

        if (mUSBMonitor != null) {
            mUSBMonitor.requestPermission(deviceList.get(0));
        }
    }

    private void saveCapturePicture() {
        if (mUVCCamera == null) {
            showShortMsg(getResources().getString(R.string.msg_camera_open_fail));
            return;
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
        String imageFileName = timeStamp + ".jpg";
        String storagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "USBCamera";
        File storageDir = new File(storagePath);
        if (!storageDir.exists()) {
            storageDir.mkdir();
        }
        String imageFilePath = storagePath + File.separator + imageFileName;
        Log.d(TAG, imageFilePath);

        Bitmap bitmap = mUVCCameraView.getBitmap();

        try {
            File file = new File(imageFilePath);
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        galleryAddPic(imageFilePath);
        showShortMsg(getResources().getString(R.string.msg_capturesaved));
    }

    private void galleryAddPic(String photoPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(photoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


    private void showShortMsg(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    private synchronized void releaseCamera() {
        synchronized (mSync) {
            if (mUVCCamera != null) {
                try {
                    mUVCCamera.setStatusCallback(null);
                    mUVCCamera.setButtonCallback(null);
                    mUVCCamera.close();
                    mUVCCamera.destroy();
                } catch (final Exception e) {
                    //
                }
                mUVCCamera = null;
            }
            if (mPreviewSurface != null) {
                mPreviewSurface.release();
                mPreviewSurface = null;
            }
        }
    }

    @Override
    protected void onDestroy() {
        synchronized (mSync) {
            releaseCamera();

            if (mUSBMonitor != null) {
                mUSBMonitor.destroy();
                mUSBMonitor = null;
            }
        }
        mUVCCameraView = null;
        mCameraButton = null;
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUSBMonitor.register();
        synchronized (mSync) {
            if (mUVCCamera != null) {
                mUVCCamera.startPreview();
            }
        }
    }

    @Override
    protected void onStop() {
        synchronized (mSync) {
            if (mUVCCamera != null) {
                mUVCCamera.stopPreview();
            }
            if (mUSBMonitor != null) {
                mUSBMonitor.unregister();
            }
        }
        super.onStop();
    }
}
