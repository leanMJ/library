package com.recordaudio;

import static android.content.Context.BIND_AUTO_CREATE;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;


import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;


public class RecordAudioUtils {
    private static String TAG=RecordAudioUtils.class.getSimpleName();
    private static RecordAudioUtils INSTANCE;
    private static Context mContext;
    private int mType;//0未使用 其他对应类型使用
    private String mId;
    private Intent intent;
    private static volatile int serviceBindStatus = -1;//-1未绑定 0绑定中 1绑定成功
    private String mStartRecordTime, mRecordTime = "00:00:00";
    private MyHandler myHandler;
    private int number = 0;
    private TimeContextListener mTimeContextListener;//录制持续时间监听

    private static class MyHandler extends Handler {

        private final WeakReference<RecordAudioUtils> mAudioServiceUtils;

        public MyHandler(RecordAudioUtils audioServiceUtils) {
            mAudioServiceUtils = new WeakReference<>(audioServiceUtils);
        }

        @Override
        public void handleMessage(Message msg) {
            RecordAudioUtils audioServiceUtils = mAudioServiceUtils.get();
            switch (msg.what) {
                case 1:
                    audioServiceUtils.number = audioServiceUtils.number + 1;
                    audioServiceUtils.mRecordTime = audioServiceUtils.createTime();
                    if (null != audioServiceUtils.mTimeContextListener) {
                        Log.d(TAG,audioServiceUtils.mRecordTime);
                        audioServiceUtils.mTimeContextListener.onTimeContextListener(audioServiceUtils.mStartRecordTime, audioServiceUtils.mRecordTime, audioServiceUtils.number);
                    }
                    sendEmptyMessageDelayed(1, 1000);
                    break;
            }
        }
    }

    public static RecordAudioUtils getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (RecordAudioUtils.class) {
                INSTANCE = new RecordAudioUtils();
                mContext = context.getApplicationContext();
            }
        }
        return INSTANCE;
    }

    RecordAudioUtils() {
        myHandler = new MyHandler(this);
    }


    public int getType() {
        return mType;
    }

    public String getId() {
        return mId;
    }

    /**
     * 绑定服务
     */
    public void bindService(int type, String id) {
        this.mType = type;
        this.mId = id;
        Log.d(TAG,"绑定服务"+ type+ serviceBindStatus);
        if (0 == serviceBindStatus || 1 == serviceBindStatus) {
            Log.d(TAG,0 == serviceBindStatus ? "服务连接中！" : "服务已连接");
            return;
        }
        number=0;
        serviceBindStatus = 0;
        intent = new Intent(mContext, RecordService.class);
        mContext.bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    /**
     * 绑定服务
     */
    public void bindService(int type, String institutionId, TimeContextListener timeContextListener) {
        this.mType = type;
        this.mId = institutionId;
        this.mTimeContextListener = timeContextListener;
        Log.d(TAG,"绑定服务"+ type+ serviceBindStatus);
        if (0 == serviceBindStatus || 1 == serviceBindStatus) {
            Log.d(TAG,0 == serviceBindStatus ? "服务连接中！" : "服务已连接");
            return;
        }
        serviceBindStatus = 0;
        intent = new Intent(mContext, RecordService.class);
        mContext.bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    /**
     * 解绑服务
     */
    public void unbindService() {
        Log.d(TAG,"解绑服务"+ serviceBindStatus);
        mType = 0;
        if (-1 == serviceBindStatus) {
            Log.d(TAG,"服务未连接");
            return;
        }
        myHandler.removeCallbacksAndMessages(null);
        serviceBindStatus = -1;
        mStartRecordTime = "";
        number = 0;
        mRecordTime = "00:00:00";
        mContext.unbindService(serviceConnection);
    }

    /**
     * 重新绑定服务时使用
     */
   public void zeroType(){
        this.mType=0;
    }

    /**
     * 录制持续时间
     */
    public void setTimeContextListener(TimeContextListener timeContextListener) {
        this.mTimeContextListener = timeContextListener;
    }


    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceBindStatus = 1;
            mStartRecordTime = new SimpleDateFormat("yyyy.MM.dd HH:mm").format(new Date(System.currentTimeMillis()));
            Message msg = new Message();
            msg.what = 1;
            myHandler.sendMessageDelayed(msg, 1000);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    private String createTime() {
        StringBuffer string = new StringBuffer();
        int hour = number / 3600;
        string.append(hour > 9 ? hour : "0" + hour);
        string.append(":");
        int minute = (number % 3600) / 60;
        string.append(minute > 9 ? minute : "0" + minute);
        string.append(":");
        int second = number % 60;
        string.append(second > 9 ? second : "0" + second);
        return string.toString();
    }
}
