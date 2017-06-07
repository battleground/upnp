package com.abooc.signin;

import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;

import com.abooc.util.Debug;

import java.lang.reflect.Field;

/**
 * Created by dayu on 2017/6/6.
 */

public class KeyboardWatcher implements ViewTreeObserver.OnGlobalLayoutListener {


    private int screenHeight;
    private int keyboardHeight;
    private int statusBarHeight;

    private static final int KEYBOARD_SHOWN = 1;
    private static final int KEYBOARD_HIDDEN = 0;
    private int keyboardRecent = KEYBOARD_HIDDEN;

    private View watcherView;

    public KeyboardWatcher(View watcherView) {
        this.watcherView = watcherView;
        //注册布局变化监听
        watcherView.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    private OnSoftKeyboardStateChangedListener iOnSoftKeyboardStateChangedListener;

    public void setWatcherListener(OnSoftKeyboardStateChangedListener l) {
        iOnSoftKeyboardStateChangedListener = l;
    }

    public boolean isKeyboardShowing() {
        return keyboardRecent == KEYBOARD_SHOWN;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public int getStatusBarHeight() {
        return statusBarHeight;
    }

    @Override
    public void onGlobalLayout() {
        if (statusBarHeight == 0)
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object obj = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = Integer.parseInt(field.get(obj).toString());
                statusBarHeight = watcherView.getContext().getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }

        //判断窗口可见区域大小
        Rect r = new Rect();
        watcherView.getWindowVisibleDisplayFrame(r);

        int height = r.bottom - r.top;
        if (screenHeight == 0) screenHeight = height;


        keyboardHeight = screenHeight - r.bottom;
        String data = "screenHeight:" + screenHeight + ", r.top:" + r.top + ", r.bottom:" + r.bottom + ", 键盘高度:" + keyboardHeight;

        //如果屏幕高度和Window可见区域高度差值大于整个屏幕高度的1/3，则表示软键盘显示中，否则软键盘为隐藏状态。
        int heightDifference = screenHeight - (r.bottom - r.top);
        boolean isKeyboardShowing = heightDifference > screenHeight / 3;
        if (isKeyboardShowing) {
            if (keyboardRecent == KEYBOARD_HIDDEN) {
                Debug.anchor("弹出  " + data);

                if (iOnSoftKeyboardStateChangedListener != null) {
                    iOnSoftKeyboardStateChangedListener.onSoftKeyboardStateChanged(true, keyboardHeight);
                }
            }
            keyboardRecent = KEYBOARD_SHOWN;
        } else {
            if (keyboardRecent == KEYBOARD_SHOWN) {
                Debug.anchor("收起  " + data);

                if (iOnSoftKeyboardStateChangedListener != null) {
                    iOnSoftKeyboardStateChangedListener.onSoftKeyboardStateChanged(false, keyboardHeight);
                }
            }
            keyboardRecent = KEYBOARD_HIDDEN;
        }

    }

    public void destroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            watcherView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        } else {
            watcherView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
        watcherView = null;
        iOnSoftKeyboardStateChangedListener = null;
    }

}
