package com.abooc.upnp;

/**
 * Created by author:李瑞宇
 * email:allnet@live.cn
 * on 16/7/12.
 */
public interface Player {

    void play();

    void onPrepare();

    void pause();

    void stop();

    void volume(long volume);

    void seek(String progress);

    void setMute(boolean mute);
}
