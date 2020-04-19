package com.shlll.usbcamera;

import android.Manifest;
import android.animation.Animator;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.serenegiant.widget.UVCCameraTextureView;
import com.shlll.libusbcamera.USBCameraHelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.function.Function;


public class MainActivity extends AppCompatActivity {
    private UVCCameraTextureView mUVCCameraView;
    private USBCameraHelper mUSBCameraHelper;
    boolean isRightFABOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String[] permission = new String[] {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.VIBRATE
        };
        PermissionsUtils.getInstance().chekPermissions(this, permission, permissionsResult);

        mUVCCameraView = (UVCCameraTextureView)findViewById(R.id.camera_view);
        FrameLayout frame_layout = findViewById(R.id.camera_view_frame);
        mUSBCameraHelper = new USBCameraHelper(this, mUVCCameraView, frame_layout);

        FloatingActionButton fab_capture = findViewById(R.id.fab_capture),
                fab_pos = findViewById(R.id.fab_pos),
                fab_front_up = findViewById(R.id.fab_front_up),
                fab_front_down = findViewById(R.id.fab_front_down),
                fab_front_right = findViewById(R.id.fab_front_right),
                fab_front_left = findViewById(R.id.fab_front_left),
                fab_back_up = findViewById(R.id.fab_back_up),
                fab_back_down = findViewById(R.id.fab_back_down),
                fab_back_right = findViewById(R.id.fab_back_right),
                fab_back_left = findViewById(R.id.fab_back_left);
        LinearLayout fab_front_right_layout = findViewById(R.id.fab_front_right_layout),
                fab_front_left_layout = findViewById(R.id.fab_front_left_layout),
                fab_front_down_layout = findViewById(R.id.fab_front_down_layout),
                fab_front_up_layout = findViewById(R.id.fab_front_up_layout),
                fab_back_right_layout = findViewById(R.id.fab_back_right_layout),
                fab_back_left_layout = findViewById(R.id.fab_back_left_layout),
                fab_back_down_layout = findViewById(R.id.fab_back_down_layout),
                fab_back_up_layout = findViewById(R.id.fab_back_up_layout);
        TextView  fab_front_right_text = findViewById(R.id.fab_front_right_text),
                fab_front_left_text = findViewById(R.id.fab_front_left_text),
                fab_front_down_text = findViewById(R.id.fab_front_down_text),
                fab_front_up_text = findViewById(R.id.fab_front_up_text),
                fab_back_right_text = findViewById(R.id.fab_back_right_text),
                fab_back_left_text = findViewById(R.id.fab_back_left_text),
                fab_back_down_text = findViewById(R.id.fab_back_down_text),
                fab_back_up_text = findViewById(R.id.fab_back_up_text);

        fab_front_up_layout.animate().setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (!isRightFABOpen) {
                    fab_front_right_text.setVisibility(View.GONE);
                    fab_front_left_text.setVisibility(View.GONE);
                    fab_front_down_text.setVisibility(View.GONE);
                    fab_front_up_text.setVisibility(View.GONE);
                    fab_back_right_text.setVisibility(View.GONE);
                    fab_back_left_text.setVisibility(View.GONE);
                    fab_back_down_text.setVisibility(View.GONE);
                    fab_back_up_text.setVisibility(View.GONE);
                } else {
                    fab_front_up.setVisibility(View.VISIBLE);
                    fab_front_down.setVisibility(View.VISIBLE);
                    fab_front_right.setVisibility(View.VISIBLE);
                    fab_front_left.setVisibility(View.VISIBLE);
                    fab_back_up.setVisibility(View.VISIBLE);
                    fab_back_down.setVisibility(View.VISIBLE);
                    fab_back_right.setVisibility(View.VISIBLE);
                    fab_back_left.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isRightFABOpen) {
                    fab_front_up.setVisibility(View.GONE);
                    fab_front_down.setVisibility(View.GONE);
                    fab_front_right.setVisibility(View.GONE);
                    fab_front_left.setVisibility(View.GONE);
                    fab_back_up.setVisibility(View.GONE);
                    fab_back_down.setVisibility(View.GONE);
                    fab_back_right.setVisibility(View.GONE);
                    fab_back_left.setVisibility(View.GONE);
                } else {
                    fab_front_right_text.setVisibility(View.VISIBLE);
                    fab_front_left_text.setVisibility(View.VISIBLE);
                    fab_front_down_text.setVisibility(View.VISIBLE);
                    fab_front_up_text.setVisibility(View.VISIBLE);
                    fab_back_right_text.setVisibility(View.VISIBLE);
                    fab_back_left_text.setVisibility(View.VISIBLE);
                    fab_back_down_text.setVisibility(View.VISIBLE);
                    fab_back_up_text.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        Function<Boolean, Boolean> animatePosFabMenu = (open)-> {
            if(isRightFABOpen == open)
                return false;

            if(!isRightFABOpen) {
                isRightFABOpen = true;
                fab_front_up_layout.animate().translationY(-getResources().getDimension(R.dimen.fab_menu_1));
                fab_back_up_layout.animate().translationY(-getResources().getDimension(R.dimen.fab_menu_2));
                fab_front_left_layout.animate().translationY(-getResources().getDimension(R.dimen.fab_menu_3));
                fab_back_left_layout.animate().translationY(-getResources().getDimension(R.dimen.fab_menu_4));
                fab_front_right_layout.animate().translationY(-getResources().getDimension(R.dimen.fab_menu_5));
                fab_back_right_layout.animate().translationY(-getResources().getDimension(R.dimen.fab_menu_6));
                fab_front_down_layout.animate().translationY(-getResources().getDimension(R.dimen.fab_menu_7));
                fab_back_down_layout.animate().translationY(-getResources().getDimension(R.dimen.fab_menu_8));
            } else {
                isRightFABOpen = false;
                fab_front_up_layout.animate().translationY(0);
                fab_back_up_layout.animate().translationY(0);
                fab_front_left_layout.animate().translationY(0);
                fab_back_left_layout.animate().translationY(0);
                fab_front_right_layout.animate().translationY(0);
                fab_back_right_layout.animate().translationY(0);
                fab_front_down_layout.animate().translationY(0);
                fab_back_down_layout.animate().translationY(0);
            }

            return true;
        };

        mUSBCameraHelper.setOnCameraButtonListener(new USBCameraHelper.OnCameraButtonListener() {
            @Override
            public void onCameraButton() {
                mUSBCameraHelper.saveCapturePicture();
            }
        });

        mUVCCameraView.setOnTransStateChangeListener(new UVCCameraTextureView.OnTransStateChangeListener() {
            @Override
            public void onTransStateChange(UVCCameraTextureView.TransState state) {
                switch (state) {
                    case CUSTOM:
                        fab_pos.setImageResource(R.drawable.ic_none);
                        fab_pos.setRotation(0);
                        return;
                    case BACK_UP:
                        fab_pos.setImageResource(R.drawable.ic_cam_back);
                        fab_pos.setRotation(0);
                        break;
                    case FRONT_UP:
                        fab_pos.setImageResource(R.drawable.ic_cam_front);
                        fab_pos.setRotation(0);
                        break;
                    case BACK_DOWN:
                        fab_pos.setImageResource(R.drawable.ic_cam_back);
                        fab_pos.setRotation(180);
                        break;
                    case FRONT_DOWN:
                        fab_pos.setImageResource(R.drawable.ic_cam_front);
                        fab_pos.setRotation(180);
                        break;
                    case BACK_LEFT:
                        fab_pos.setImageResource(R.drawable.ic_cam_back);
                        fab_pos.setRotation(270);
                        break;
                    case FRONT_LEFT:
                        fab_pos.setImageResource(R.drawable.ic_cam_front);
                        fab_pos.setRotation(270);
                        break;
                    case BACK_RIGHT:
                        fab_pos.setImageResource(R.drawable.ic_cam_back);
                        fab_pos.setRotation(90);
                        break;
                    case FRONT_RIGHT:
                        fab_pos.setImageResource(R.drawable.ic_cam_front);
                        fab_pos.setRotation(90);
                        break;
                }
            }
        });

        frame_layout.setOnClickListener((view)->{
            animatePosFabMenu.apply(false);
        });

        fab_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUSBCameraHelper.saveCapturePicture();
            }
        });

        fab_pos.setOnClickListener((view)->animatePosFabMenu.apply(true ^ isRightFABOpen));
        fab_front_up.setOnClickListener((view)->mUSBCameraHelper.setPosition(UVCCameraTextureView.TransState.FRONT_UP));
        fab_front_down.setOnClickListener((view)->mUSBCameraHelper.setPosition(UVCCameraTextureView.TransState.FRONT_DOWN));
        fab_front_left.setOnClickListener((view)->mUSBCameraHelper.setPosition(UVCCameraTextureView.TransState.FRONT_LEFT));
        fab_front_right.setOnClickListener((view)->mUSBCameraHelper.setPosition(UVCCameraTextureView.TransState.FRONT_RIGHT));
        fab_back_up.setOnClickListener((view)->mUSBCameraHelper.setPosition(UVCCameraTextureView.TransState.BACK_UP));
        fab_back_down.setOnClickListener((view)->mUSBCameraHelper.setPosition(UVCCameraTextureView.TransState.BACK_DOWN));
        fab_back_left.setOnClickListener((view)->mUSBCameraHelper.setPosition(UVCCameraTextureView.TransState.BACK_LEFT));
        fab_back_right.setOnClickListener((view)->mUSBCameraHelper.setPosition(UVCCameraTextureView.TransState.BACK_RIGHT));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsUtils.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
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

    private PermissionsUtils.IPermissionsResult permissionsResult = new PermissionsUtils.IPermissionsResult() {
        @Override
        public void passPermissons() {
        }

        @Override
        public void forbitPermissons() {
        }
    };
}
