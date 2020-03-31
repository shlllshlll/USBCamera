package com.shlll.usbcamera;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.serenegiant.widget.UVCCameraTextureView;
import com.shlll.libusbcamera.USBCameraHelper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    private UVCCameraTextureView mUVCCameraView;
    private USBCameraHelper mUSBCameraHelper;


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
                mUSBCameraHelper.saveCapturePicture();
//                throw new RuntimeException("Crash APP");
            }
        });

        mUVCCameraView = (UVCCameraTextureView)findViewById(R.id.camera_view);
        mUSBCameraHelper = new USBCameraHelper(this, mUVCCameraView);

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
        if (id == R.id.action_about) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(getResources().getString(R.string.diag_about_title))
                    .setMessage(getResources().getString(R.string.diag_about_message));

            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (id == R.id.action_usbinfo) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(getResources().getString(R.string.diag_usbinfo_title));

            String diag_str = "";
            final List<UsbDevice> device_list =  mUSBCameraHelper.getDeviceList();

            for (UsbDevice device: device_list) {
                diag_str += "Class:" + device.getDeviceClass()
                        + ",subClass:" + device.getDeviceSubclass()
                        + ",VID:" + device.getVendorId()
                        + ",PID:" + device.getProductId() + "\n";
            }

            builder.setMessage(diag_str);

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

    private void showShortMsg(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        mUSBCameraHelper.destory();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUSBCameraHelper.start();
    }

    @Override
    protected void onStop() {
        mUSBCameraHelper.stop();
        super.onStop();
    }
}
