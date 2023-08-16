package com.socket;

public interface WebSocket {
    enum READYSTATE {
        NOT_YET_CONNECTED,
        CLOSING,
        CLOSED;

        READYSTATE() {
        }
    }
}
