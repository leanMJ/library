package com.socket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;


public class MyWebSocketClient extends WebSocketClient {

    public MyWebSocketClient(URI serverUri) {
        super(serverUri);
    }


    /**
     * 在webSocket连接开启时调用
     * @param serverHandshake
     */
    @Override
    public void onOpen(ServerHandshake serverHandshake) {

    }

    /**
     * 接收到消息时调用
     * @param message
     */
    @Override
    public void onMessage(String message) {

    }

    /**
     * 在连接断开时调用
     * @param code
     * @param reason
     * @param remote
     */
    @Override
    public void onClose(int code, String reason, boolean remote) {

    }

    /**
     * 在连接出错时调用
     * @param ex
     */
    @Override
    public void onError(Exception ex) {

    }
}
