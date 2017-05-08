package com.abooc.upnp;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.abooc.widget.Toast;

/**
 * Created by dayu on 2017/5/5.
 */

public class UI extends Handler {

    public static final int ITEM_IMAGE = 1;
    public static final int ITEM_MUSIC = 2;
    public static final int ITEM_VIDEO = 3;

    public static class Player {

        public static final int PLAY = 11;
        public static final int PAUSE = 12;
        public static final int STOP = 13;
        public static final int SEEK = 14;
    }

    public UI(){

    }

    public UI(Looper looper){
        super(looper);
    }


    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case ITEM_IMAGE:
                Toast.show("图片");
                break;
            case ITEM_MUSIC:
                Toast.show("音乐");
                break;
            case ITEM_VIDEO:
                Toast.show("视频");
                break;

            case Player.PLAY:
                Toast.show("PLAY");
                break;
            case Player.PAUSE:
                Toast.show("PAUSE");
                break;
            case Player.STOP:
                Toast.show("STOP");
                break;
            case Player.SEEK:
                Toast.show("Seek");
                break;
        }
    }
}
