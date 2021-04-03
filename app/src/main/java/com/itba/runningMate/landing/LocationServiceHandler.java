package com.itba.runningMate.landing;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

public class LocationServiceHandler extends Handler {

    public LocationServiceHandler(@NonNull Looper looper) {
        super(looper);
    }

    /*
        Necesito usar mensajes como mecanismo o puedo directamente mandar Runnables
    */
    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
    }

}
