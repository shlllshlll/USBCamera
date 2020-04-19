/*
 *  UVCCamera
 *  library and sample to access to UVC web camera on non-rooted Android device
 *
 * Copyright (c) 2014-2017 saki t_saki@serenegiant.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *  All files in the folder are under this Apache License, Version 2.0.
 *  Files in the libjpeg-turbo, libusb, libuvc, rapidjson folder
 *  may have a different license, see the respective files.
 */

package com.serenegiant.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.TextureView;
import android.widget.FrameLayout;

/**
 * change the view size with keeping the specified aspect ratio.
 * if you set this view with in a FrameLayout and set property "android:layout_gravity="center",
 * you can show this view in the center of screen and keep the aspect ratio of content
 * XXX it is better that can set the aspect raton a a xml property
 */
public class UVCCameraTextureView extends TextureView {
	public enum TransState {
		CUSTOM,
		FRONT_UP,
		FRONT_DOWN,
		FRONT_RIGHT,
		FRONT_LEFT,
		BACK_UP,
		BACK_DOWN,
		BACK_RIGHT,
		BACK_LEFT
	};
	private static final String TAG = "UVCCameraTextureView";
	private int mLayoutWidth, mLayoutHeight;
	private float mAspectRatio, mLayoutAspectRatio;
	private int mRotation = 0, mScaleX = 1, mScaleY = 1;
	private TransState mTransState = TransState.FRONT_UP;
	private OnTransStateChangeListener mListener = null;

	public interface OnTransStateChangeListener {
		public void onTransStateChange(TransState state);
	}

	public void setOnTransStateChangeListener(OnTransStateChangeListener listener) {
		mListener = listener;
	}

	public UVCCameraTextureView(final Context context) {
		this(context, null, 0);
	}

	public UVCCameraTextureView(final Context context, final AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public UVCCameraTextureView(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setAspectRatio(final float aspectRatio, final int layoutWidth, final int layoutHeight) {
		mAspectRatio = aspectRatio;
		mLayoutWidth = layoutWidth;
		mLayoutHeight = layoutHeight;
		mLayoutAspectRatio = layoutHeight / (float)layoutWidth;
		setHeightWidth();

//		Log.i(TAG, "Radio:" + String.valueOf(mAspectRatio) + ",LayoutRadio:" + String.valueOf(mLayoutAspectRatio));
	}

	public void rightRotate() {
		mRotation += 90;
		if (mRotation == 360)
			mRotation = 0;
		setHeightWidth();
		setRotation(mRotation);
		setTransState(TransState.CUSTOM);
	}

	public void leftRotate() {
		mRotation -= 90;
		if (mRotation == -90)
			mRotation = 270;
		setHeightWidth();
		setRotation(mRotation);
		setTransState(TransState.CUSTOM);
	}

	public void toggleMirror() {
		if (mRotation == 0 || mRotation == 180) {
			mScaleX = -mScaleX;
			setScaleX(mScaleX);
		} else {
			mScaleY = -mScaleY;
			setScaleY(mScaleY);
		}
		setTransState(TransState.CUSTOM);
	}

	public void setPosition(TransState state) {
		boolean mirrored = false;

		switch (state) {
			case CUSTOM:
				return;
			case BACK_UP:
				mRotation = 0;
				mScaleX = -1;
				mScaleY = 1;
				break;
			case FRONT_UP:
				mRotation = 0;
				mScaleX = 1;
				mScaleY = 1;
				break;
			case BACK_DOWN:
				mRotation = 180;
				mScaleX = -1;
				mScaleY = 1;
				break;
			case FRONT_DOWN:
				mRotation = 180;
				mScaleX = 1;
				mScaleY = 1;
				break;
			case BACK_LEFT:
				mRotation = 270;
				mScaleX = 1;
				mScaleY = -1;
				break;
			case FRONT_LEFT:
				mRotation = 270;
				mScaleX = 1;
				mScaleY = 1;
				break;
			case BACK_RIGHT:
				mRotation = 90;
				mScaleX = 1;
				mScaleY = -1;
				break;
			case FRONT_RIGHT:
				mRotation = 90;
				mScaleX = 1;
				mScaleY = 1;
				break;
		}

		setHeightWidth();
		setRotation(mRotation);
		setScaleX(mScaleX);
		setScaleY(mScaleY);
		setTransState(state);
	}

	@Override
	public Bitmap getBitmap() {
		Bitmap origin = super.getBitmap();

		int width = origin.getWidth();
		int height = origin.getHeight();
		Matrix matrix = new Matrix();
		matrix.postScale(getScaleX(), getScaleY());
		matrix.postRotate(mRotation);

		Bitmap resmap = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);

		return resmap;
	}

	private void setTransState(TransState state) {
		if (mTransState != state) {
			mTransState = state;
			if (mListener != null)
				mListener.onTransStateChange(state);
		}
	}

	private void setHeightWidth() {
		int height, width;

		FrameLayout.LayoutParams params;
		if (mRotation == 0 || mRotation == 180) {
			width = mLayoutWidth;
			height = (int) (mLayoutWidth / mAspectRatio);
		} else {
			if (mLayoutAspectRatio > mAspectRatio) {
				height = mLayoutWidth;
				width = (int)(mLayoutWidth * mAspectRatio);
			} else {
				height = (int)(mLayoutHeight / mAspectRatio);
				width = mLayoutHeight;
			}
		}

		Log.i(TAG, "height:" + String.valueOf(height) + ",width:"+ String.valueOf(width));
		setLayoutParams(new FrameLayout.LayoutParams(width, height, Gravity.CENTER));
	}

}
