package com.shlll.usbcamera;

import android.content.Context;

import androidx.annotation.NonNull;

import org.acra.config.CoreConfiguration;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderFactory;

public class AcraFileSenderFactory implements ReportSenderFactory {
    @Override
    public ReportSender create(Context context, CoreConfiguration config) {
        return new AcraFileSender();
    }

    @Override
    public boolean enabled(@NonNull CoreConfiguration coreconfig) {
        return true;
    }
}
