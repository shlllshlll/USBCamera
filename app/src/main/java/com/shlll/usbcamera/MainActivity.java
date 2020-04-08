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
    boolean isLeftFABOpen = false;
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
                fab_transform = findViewById(R.id.fab_transform),
                fab_mirror = findViewById(R.id.fab_mirror),
                fab_rotate_right = findViewById(R.id.fab_rotate_right),
                fab_rotate_left = findViewById(R.id.fab_rotate_left),
                fab_up = findViewById(R.id.fab_up),
                fab_down = findViewById(R.id.fab_down),
                fab_right = findViewById(R.id.fab_right),
                fab_left = findViewById(R.id.fab_left),
                fab_pos = findViewById(R.id.fab_pos);
        LinearLayout fab_mirror_layout = findViewById(R.id.fab_mirror_layout),
                fab_rotate_right_layout = findViewById(R.id.fab_rotate_right_layout),
                fab_rotate_left_layout = findViewById(R.id.fab_rotate_left_layout),
                fab_right_layout = findViewById(R.id.fab_right_layout),
                fab_left_layout = findViewById(R.id.fab_left_layout),
                fab_down_layout = findViewById(R.id.fab_down_layout),
                fab_up_layout = findViewById(R.id.fab_up_layout);
        TextView fab_mirror_text = findViewById(R.id.fab_mirror_text),
                fab_rotate_right_text = findViewById(R.id.fab_rotate_right_text),
                fab_rotate_left_text = findViewById(R.id.fab_rotate_left_text),
                fab_right_text = findViewById(R.id.fab_right_text),
                fab_left_text = findViewById(R.id.fab_left_text),
                fab_down_text = findViewById(R.id.fab_down_text),
                fab_up_text = findViewById(R.id.fab_up_text);

        fab_mirror_layout.animate().setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (!isLeftFABOpen) {
                    fab_mirror_text.setVisibility(View.GONE);
                    fab_rotate_right_text.setVisibility(View.GONE);
                    fab_rotate_left_text.setVisibility(View.GONE);

                } else {
                    fab_mirror.setVisibility(View.VISIBLE);
                    fab_rotate_right.setVisibility(View.VISIBLE);
                    fab_rotate_left.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isLeftFABOpen) {
                    fab_mirror.setVisibility(View.GONE);
                    fab_rotate_right.setVisibility(View.GONE);
                    fab_rotate_left.setVisibility(View.GONE);
                } else {
                    fab_mirror_text.setVisibility(View.VISIBLE);
                    fab_rotate_right_text.setVisibility(View.VISIBLE);
                    fab_rotate_left_text.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        fab_right_layout.animate().setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (!isRightFABOpen) {
                    fab_right_text.setVisibility(View.GONE);
                    fab_left_text.setVisibility(View.GONE);
                    fab_down_text.setVisibility(View.GONE);
                    fab_up_text.setVisibility(View.GONE);

                } else {
                    fab_right.setVisibility(View.VISIBLE);
                    fab_left.setVisibility(View.VISIBLE);
                    fab_down.setVisibility(View.VISIBLE);
                    fab_up.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isRightFABOpen) {
                    fab_right.setVisibility(View.GONE);
                    fab_left.setVisibility(View.GONE);
                    fab_down.setVisibility(View.GONE);
                    fab_up.setVisibility(View.GONE);
                } else {
                    fab_right_text.setVisibility(View.VISIBLE);
                    fab_left_text.setVisibility(View.VISIBLE);
                    fab_down_text.setVisibility(View.VISIBLE);
                    fab_up_text.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        Function<Boolean, Boolean> animateTransformFabMenu = (open)-> {
            if(isLeftFABOpen == open)
                return false;

            if(!isLeftFABOpen) {
                isLeftFABOpen = true;
                fab_mirror_layout.animate().translationY(-getResources().getDimension(R.dimen.fab_menu_3));
                fab_rotate_right_layout.animate().translationY(-getResources().getDimension(R.dimen.fab_menu_2));
                fab_rotate_left_layout.animate().translationY(-getResources().getDimension(R.dimen.fab_menu_1));
                fab_transform.setImageResource(R.drawable.ic_x);
            } else {
                isLeftFABOpen = false;
                fab_mirror_layout.animate().translationY(0);
                fab_rotate_right_layout.animate().translationY(0);
                fab_rotate_left_layout.animate().translationY(0);
                fab_transform.setImageResource(R.drawable.ic_transform);
            }

            return true;
        };

        Function<Boolean, Boolean> animatePosFabMenu = (open)-> {
            if(isRightFABOpen == open)
                return false;

            if(!isRightFABOpen) {
                isRightFABOpen = true;
                fab_right_layout.animate().translationY(-getResources().getDimension(R.dimen.fab_menu_1));
                fab_left_layout.animate().translationY(-getResources().getDimension(R.dimen.fab_menu_2));
                fab_down_layout.animate().translationY(-getResources().getDimension(R.dimen.fab_menu_3));
                fab_up_layout.animate().translationY(-getResources().getDimension(R.dimen.fab_menu_4));
            } else {
                isRightFABOpen = false;
                fab_right_layout.animate().translationY(0);
                fab_left_layout.animate().translationY(0);
                fab_down_layout.animate().translationY(0);
                fab_up_layout.animate().translationY(0);
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
                        break;
                    case RIGHT:
                        fab_pos.setImageResource(R.drawable.ic_right);
                        break;
                    case LEFT:
                        fab_pos.setImageResource(R.drawable.ic_left);
                        break;
                    case UP:
                        fab_pos.setImageResource(R.drawable.ic_up);
                        break;
                    case DOWN:
                        fab_pos.setImageResource(R.drawable.ic_down);
                        break;
                }
            }
        });

        frame_layout.setOnClickListener((view)->{
            animateTransformFabMenu.apply(false);
            animatePosFabMenu.apply(false);
        });

        fab_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUSBCameraHelper.saveCapturePicture();
//                throw new RuntimeException("Crash APP");
            }
        });

        fab_transform.setOnClickListener((view)->animateTransformFabMenu.apply(true ^ isLeftFABOpen));
        fab_pos.setOnClickListener((view)->animatePosFabMenu.apply(true ^ isRightFABOpen));
        fab_mirror.setOnClickListener((view)->mUSBCameraHelper.toggleMirror());
        fab_rotate_right.setOnClickListener((view)->mUSBCameraHelper.rightRotate());
        fab_rotate_left.setOnClickListener((view)->mUSBCameraHelper.leftRotate());
        fab_up.setOnClickListener((view)->mUSBCameraHelper.setPosition(UVCCameraTextureView.TransState.UP));
        fab_down.setOnClickListener((view)->mUSBCameraHelper.setPosition(UVCCameraTextureView.TransState.DOWN));
        fab_left.setOnClickListener((view)->mUSBCameraHelper.setPosition(UVCCameraTextureView.TransState.LEFT));
        fab_right.setOnClickListener((view)->mUSBCameraHelper.setPosition(UVCCameraTextureView.TransState.RIGHT));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //就多一个参数this
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
