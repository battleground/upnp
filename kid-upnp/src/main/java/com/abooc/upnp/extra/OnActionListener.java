package com.abooc.upnp.extra;

public interface OnActionListener {
    void onSend();

    void onSendFinish(boolean success);

    class SimpleOnActionListener implements OnActionListener {
        public void onSend() {
        }

        public void onSendFinish(boolean success) {
        }
    }
}