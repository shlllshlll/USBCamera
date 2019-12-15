package com.shlll.usbcamera;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.design.widget.FloatingActionButton;

import com.serenegiant.usb.Size;
import com.serenegiant.usb.common.AbstractUVCCameraHandler;
import com.serenegiant.usb.widget.CameraViewInterface;
import com.jiangdg.usbcamera.UVCCameraHelper;
import com.jiangdg.usbcamera.utils.FileUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private UVCCameraHelper mCameraHelper;
    private CameraViewInterface mUVCCameraView;
    private boolean isRequest;
    private boolean isPreview;
    private boolean getPermisson = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        // step.1 initialize UVCCameraHelper
        mUVCCameraView = (CameraViewInterface) findViewById(R.id.camera_view);
        mUVCCameraView.setCallback(mCallback);
        mCameraHelper = UVCCameraHelper.getInstance();
        mCameraHelper.setDefaultFrameFormat(UVCCameraHelper.FRAME_FORMAT_MJPEG);
        mCameraHelper.initUSBMonitor(this, mUVCCameraView, listener);

        checkFilePermission();
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
        if (id == R.id.action_resolution) {
            if (mCameraHelper == null || !mCameraHelper.isCameraOpened()) {
                showShortMsg(getResources().getString(R.string.msg_camera_open_fail));
                return false;
            }

            showResolutionListDialog();
        } else if (id == R.id.action_focus) {
            if (mCameraHelper == null || !mCameraHelper.isCameraOpened()) {
                showShortMsg(getResources().getString(R.string.msg_camera_open_fail));
                return false;
            }
            mCameraHelper.startCameraFoucs();
            showShortMsg(getResources().getString(R.string.msg_focusing));
        }

        return true;
    }

    private void showResolutionListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getResources().getString(R.string.action_resolution));

        String[] resolutionList = getResolutionList();

        builder.setItems(resolutionList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final String resolution = resolutionList[i];
                String[] tmp = resolution.split("x");
                if (tmp != null && tmp.length >= 2) {
                    int width = Integer.valueOf(tmp[0]);
                    int height = Integer.valueOf(tmp[1]);
                    Log.d(TAG, "Resolution:" + String.valueOf(width) + "x" + String.valueOf(height));
                    mCameraHelper.updateResolution(width, height);
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private String[] getResolutionList() {
        List<Size> list = mCameraHelper.getSupportedPreviewSizes();
        List<String> resolutions = null;
        if (list != null && list.size() != 0) {
            resolutions = new ArrayList<>();
            for (Size size : list) {
                if (size != null) {
                    resolutions.add(size.width + "x" + size.height);
                }
            }
        }

        String[] resolutionArray = resolutions.toArray(new String[0]);

        return resolutionArray;
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

    private void saveCapturePicture() {
        if (mCameraHelper == null || !mCameraHelper.isCameraOpened()) {
            showShortMsg(getResources().getString(R.string.msg_camera_open_fail));
            return;
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
        String imageFileName = timeStamp + UVCCameraHelper.SUFFIX_JPEG;
        String storagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "USBCamera";
        File storageDir = new File(storagePath);
        if (!storageDir.exists()) {
            storageDir.mkdir();
        }
        String imageFilePath = storagePath + File.separator + imageFileName;
        Log.d(TAG, imageFilePath);
        mCameraHelper.capturePicture(imageFilePath, new AbstractUVCCameraHandler.OnCaptureListener() {
            @Override
            public void onCaptureResult(String picPath) {
                galleryAddPic(imageFilePath);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showShortMsg(getResources().getString(R.string.msg_capturesaved));
                    }
                });
                Log.d(TAG, "ImageSaved:"+imageFilePath);
            }
        });
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

    private CameraViewInterface.Callback mCallback = new CameraViewInterface.Callback() {
        @Override
        public void onSurfaceCreated(CameraViewInterface view, Surface surface) {
            // must have
            if (!isPreview && mCameraHelper.isCameraOpened()) {
                mCameraHelper.startPreview(mUVCCameraView);
                isPreview = true;
            }
        }

        @Override
        public void onSurfaceChanged(CameraViewInterface view, Surface surface, int width, int height) {

        }

        @Override
        public void onSurfaceDestroy(CameraViewInterface view, Surface surface) {
            // must have
            if (isPreview && mCameraHelper.isCameraOpened()) {
                mCameraHelper.stopPreview();
                isPreview = false;
            }
        }
    };

    private UVCCameraHelper.OnMyDevConnectListener listener = new UVCCameraHelper.OnMyDevConnectListener() {

        @Override
        public void onAttachDev(UsbDevice device) {
            // request open permission
            if (!isRequest) {
                isRequest = true;
                if (mCameraHelper != null) {
                    mCameraHelper.requestPermission(0);
                }
            }
        }

        @Override
        public void onDettachDev(UsbDevice device) {
            // close camera
            if (isRequest) {
                isRequest = false;
                mCameraHelper.closeCamera();
                showShortMsg(device.getDeviceName() + " is out");
            }
        }

        @Override
        public void onConnectDev(UsbDevice device, boolean isConnected) {
            if (!isConnected) {
                showShortMsg("fail to connect,please check resolution params");
                isPreview = false;
            } else {
                isPreview = true;
                showShortMsg("connecting");
            }
        }

        @Override
        public void onDisConnectDev(UsbDevice device) {
            showShortMsg("disconnecting");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FileUtils.releaseFile();
        // step.4 release uvc camera resources
        if (mCameraHelper != null) {
            mCameraHelper.release();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // step.2 register USB event broadcast
        if (mCameraHelper != null) {
            mCameraHelper.registerUSB();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // step.3 unregister USB event broadcast
        if (mCameraHelper != null) {
            mCameraHelper.unregisterUSB();
        }
    }
}
