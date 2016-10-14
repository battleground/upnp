package demo.abooc.com.upnp;

import android.os.Handler;
import android.os.Message;

import com.abooc.util.Debug;

/**
 * 便于在Timer中操作UI
 */
public abstract class UITimer extends Handler {

    private int second = 0;
    private int secondMax = 0;
    private boolean isRunning = false;

    /**
     * 初始化Timer总时间
     *
     * @param seconds Timer定时器的总时间，单位秒
     */
    public UITimer(int seconds) {
        secondMax = seconds;
    }

    public boolean start(int seconds) {
        secondMax = seconds;
        return start();
    }

    public boolean start() {
        removeMessages(0);
        second = 0;
        isRunning = true;

        onStart();
        return super.sendEmptyMessageDelayed(0, 1000);
    }

    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void handleMessage(Message msg) {
        second++;
        Debug.anchor(second);
        if (second < secondMax) {
            isRunning = true;
            onTick(secondMax, second);
            super.sendEmptyMessageDelayed(0, 1000);
        } else {
            isRunning = false;
            onFinish();
        }
    }

    public void cancel() {
        removeMessages(0);
        second = 0;
        isRunning = false;

        onCancel();
    }

    /**
     * Timer开始，UI线程
     */
    public void onStart() {

    }

    /**
     * Timer，UI线程
     */
    public void onTick(int total, int tick) {

    }

    /**
     * Timer被打断，UI线程
     */
    public void onCancel() {

    }

    /**
     * Timer执行结束，UI线程
     */
    abstract public void onFinish();

}