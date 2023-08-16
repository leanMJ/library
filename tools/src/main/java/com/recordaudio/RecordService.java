package com.recordaudio;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;



public class RecordService extends Service {

    private RecordAudioServiceBinder mBinder = new RecordAudioServiceBinder();

    public class RecordAudioServiceBinder extends Binder {
        public RecordService getService() {
            return RecordService.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

}
