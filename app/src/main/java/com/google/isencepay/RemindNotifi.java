package com.google.isencepay;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class RemindNotifi extends Service {
    public RemindNotifi() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}