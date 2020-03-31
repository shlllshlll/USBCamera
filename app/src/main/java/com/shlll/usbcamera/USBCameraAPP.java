package com.shlll.usbcamera;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import org.acra.ACRA;
import org.acra.annotation.AcraCore;
import org.acra.annotation.AcraNotification;

@AcraCore(buildConfigClass = BuildConfig.class,
        reportSenderFactoryClasses = AcraFileSenderFactory.class)
@AcraNotification(resText = R.string.acra_notification_text,
        resTitle = R.string.acra_notification_title,
        resChannelName = R.string.acra_notification_channel)
public class USBCameraAPP extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        ACRA.DEV_LOGGING = true;

        // The following line triggers the initialization of ACRA
        ACRA.init(this);
    }
}
