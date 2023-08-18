package com.socket;

import android.os.Handler;
import android.util.Log;

import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;


public class MySocketClientUtils {
    public boolean isNeedReConnect;
    private final static String TAG = MySocketClientUtils.class.getSimpleName();
    public volatile boolean isConnect;
    public MyWebSocketClient client;
    private static final long CLOSE_RECON_TIME = 3 * 1000;//连接断开或者连接错误立即重连
    private static MySocketClientUtils INSTANCE;
    private boolean isReConnect;
    private boolean IS_SHOW_LOG = false;
    private URI uri;

    public static MySocketClientUtils getInstance() {
        if (INSTANCE == null) {
            synchronized (MySocketClientUtils.class) {
                INSTANCE = new MySocketClientUtils();
            }
        }
        return INSTANCE;

    }

    MySocketClientUtils() {
    }

    public void setShowLog(boolean isShow) {
        this.IS_SHOW_LOG = isShow;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    //初始化WebSocket
    public void initSocketClient() {
        client = null;
        if (null == uri) {
            return;
        }
        client = new MyWebSocketClient(uri) {

            @Override
            public void onMessage(ByteBuffer bytes) {
                super.onMessage(bytes);
                if (IS_SHOW_LOG) {
                    Log.d(TAG, "WebSocketService收到的消息ByteBuffer" + bytes.toString());
                }
            }

            @Override
            public void onMessage(String message) {
                //message就是接收到的消息
                if (IS_SHOW_LOG) {
                    Log.d(TAG, "WebSocketService收到的消息" + message);
                }
            }

            @Override
            public void onOpen(ServerHandshake handShakeData) {//在webSocket连接开启时调用
                if (IS_SHOW_LOG) {
                    Log.d(TAG, "WebSocket------ 连接成功");
                }
                isConnect = true;
                mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);//开启心跳检测
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {//在连接断开时调用
                if (IS_SHOW_LOG) {
                    Log.d(TAG, "onClose() 连接断开_reason：" + reason + isNeedReConnect);
                }
                isConnect = false;
                if (isNeedReConnect) {
                    mHandler.postDelayed(heartBeatRunnable, CLOSE_RECON_TIME);//开启心跳检测
                } else {
                    mHandler.removeCallbacks(heartBeatRunnable);
                }
            }

            @Override
            public void onError(Exception ex) {//在连接出错时调用
                ex.printStackTrace();
                if (IS_SHOW_LOG) {
                    Log.d(TAG, "onError() 连接出错：" + ex.getMessage());
                }
                isConnect = false;
                mHandler.removeCallbacks(heartBeatRunnable);
                mHandler.postDelayed(heartBeatRunnable, CLOSE_RECON_TIME);//开启心跳检测
            }
        };
        connect();
    }

    /**
     * 发送消息
     */
    public void sendMsg(String msg) {
        if (null != client) {
            if (!isConnect) {
                return;
            }
            if (IS_SHOW_LOG) {
                Log.d(TAG, "发送浏览记录：" + msg);
            }
            try {
                client.send(msg);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送消息
     */
    public void sendMsg(byte[] msg) {
        if (null != client) {
            if (IS_SHOW_LOG) {
                Log.d(TAG, "发送的消息：" + new String(msg));
            }
            try {
                client.send(msg);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 连接WebSocket
     */
    private void connect() {
        if (client == null) {
            return;
        }
        if (isReConnect) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (client == null) {
                    return;
                }
                isReConnect = true;
                Log.d(TAG, client.getReadyState().name());
                if (!client.isOpen()) {
                    ReadyState readyState=   client.getReadyState();
                    if(null==readyState){
                        if (IS_SHOW_LOG) {
                            Log.d(TAG, "readyState isNull");
                        }
                        return;
                    }
                    String readyStateName = readyState.name();
                    if(null==readyStateName){
                        if (IS_SHOW_LOG) {
                            Log.d(TAG, "readyStateName isNull");
                        }
                        return;
                    }
                    if (readyStateName.equals(ReadyState.NOT_YET_CONNECTED.name())) {
                        if (client == null) {
                            return;
                        }
                        if (IS_SHOW_LOG) {
                            Log.d(TAG, "开启连接");
                        }
//                        closeConnect();
//                        initSocketClient();
                        try {
                            client.connectBlocking();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
//
                    } else if (readyStateName.equals(ReadyState.CLOSING.name())
                            || readyStateName.equals(ReadyState.CLOSED.name())) {
                        if (client == null) {
                            return;
                        }
                        if (IS_SHOW_LOG) {
                            Log.d(TAG, "开启重连连接");
                        }
                        closeConnect();
                        initSocketClient();
//                        try {
//                            client.reconnectBlocking();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }

                    } else {
                        try {
                            if (client == null) {
                                return;
                            }
                            if (IS_SHOW_LOG) {
                                Log.d(TAG, "开启重连连接");
                            }
                            closeConnect();
                            initSocketClient();
//                            try {
//                                client.connectBlocking();
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (IS_SHOW_LOG) {
                                Log.d("TAG", "webSocket 连接错误 --- " + e.toString());
                            }
                        }
                    }
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException exception) {
                    isReConnect = false;
                    exception.printStackTrace();
                }
                isReConnect = false;
            }
        }).start();
//        try {
//            if (!client.isOpen()) {
//                if (client.getReadyState().name().equals(ReadyState.NOT_YET_CONNECTED.name())) {
//                    try {
//                        new Thread() {
//                            @Override
//                            public void run() {
//                                if (IS_SHOW_LOG) {
//                                    Log.d(TAG, "开启连接");
//                                }
//                                try {
//                                    if (client == null) {
//                                        return;
//                                    }
//                                    if (!client.isOpen()) {
//                                        client.connectBlocking();
//                                    }
//                                } catch (IllegalStateException e) {
//                                    e.printStackTrace();
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }.start();
//
//                    } catch (IllegalStateException e) {
//                        if (IS_SHOW_LOG) {
//                            Log.d("TAG", "webSocket 连接错误 --- " + e);
//                        }
//                    }
//
//                } else if (client.getReadyState().name().equals(ReadyState.CLOSING.name())
//                        || client.getReadyState().name().equals(ReadyState.CLOSED.name())) {
//
//                    new Thread() {
//                        @Override
//                        public void run() {
//                            if (client == null) {
//                                return;
//                            }
//                            if (IS_SHOW_LOG) {
//                                Log.d(TAG, "开启重连连接");
//                            }
//                            closeConnect();
//                            initSocketClient();
////                        try {
////                            client.reconnectBlocking();
////                        } catch (InterruptedException e) {
////                            e.printStackTrace();
////                        }
//                        }
//                    }.start();
//
//                } else {
//                    try {
//                        new Thread() {
//                            @Override
//                            public void run() {
//                                if (IS_SHOW_LOG) {
//                                    Log.d(TAG, "开启重连连接");
//                                }
//                                closeConnect();
//                                initSocketClient();
////                            try {
////                                client.connectBlocking();
////                            } catch (InterruptedException e) {
////                                e.printStackTrace();
////                            }
//                            }
//                        }.start();
//                    } catch (IllegalStateException e) {
//                        e.printStackTrace();
//                        if (IS_SHOW_LOG) {
//                            Log.d("TAG", "webSocket 连接错误 --- " + e.toString());
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (IS_SHOW_LOG) {
//                Log.d("TAG", "webSocket 连接错误 --- " + e.toString());
//            }
//        }
    }

    /**
     * 断开连接
     */
    public void closeConnect() {
        if (null != mHandler) {
            mHandler.removeCallbacks(heartBeatRunnable);
            mHandler.removeCallbacksAndMessages(null);
        }
        try {
            if (null != client) {
                client.close();
                client = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    -------------------------------------WebSocket心跳检测------------------------------------------------
    private static final long HEART_BEAT_RATE = 20 * 1000;//每隔10秒进行一次对长连接的心跳检测
    private Handler mHandler = new Handler();
    private Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            if (client != null) {
                if (client.isClosed()) {
                    reconnectWs();
                    if (IS_SHOW_LOG) {
                        Log.d(TAG, "心跳包检测WebSocket连接状态：已关闭");
                    }
                } else if (client.isOpen()) {
                    if (IS_SHOW_LOG) {
                        Log.d(TAG, "心跳包检测WebSocket连接状态：已连接");
                    }
                    byte[] data = new byte[1];
                    data[0] = 0x3f;
                    sendMsg(data);
                } else {
                    if (IS_SHOW_LOG) {
                        Log.d(TAG, "心跳包检测WebSocket连接状态：已断开");
                    }
                }
            } else {
                if (IS_SHOW_LOG) {
                    Log.d(TAG, "心跳包检测WebSocket连接状态：client已为空，重新初始化连接");
                }
            }
            //每隔一定的时间，对长连接进行一次心跳检测
            mHandler.postDelayed(this, HEART_BEAT_RATE);
        }
    };

    /**
     * 开启重连
     */
    public void reconnectWs() {
        mHandler.removeCallbacks(heartBeatRunnable);
        connect();
    }
}
