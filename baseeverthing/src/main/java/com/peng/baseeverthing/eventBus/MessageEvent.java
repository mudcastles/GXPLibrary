package com.peng.baseeverthing.eventBus;

/**
 * EventBus消息类
 */
public class MessageEvent {

    private int message = -1;

    public static final int FINISH = 0; //结束activity

    public MessageEvent(int message) {
        this.message = message;
    }

    public int getMessage() {
        return message;
    }

    public void setMessage(int message) {
        this.message = message;
    }
}
