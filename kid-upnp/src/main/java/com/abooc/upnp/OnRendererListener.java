package com.abooc.upnp;

import org.fourthline.cling.support.model.PositionInfo;

/**
 * Created by author:李瑞宇
 * email:allnet@live.cn
 * on 16/10/13.
 */

public interface OnRendererListener {

    void onRemoteNoMediaPresent();

    void onRemotePlaying();

    void onRemotePaused();

    void onRemoteStopped();

    void onRemotePrepare();

    void onRemoteProgressChanged(PositionInfo positionInfo);

    void onRemoteSeeking();

    void onRemoteVolumeChanged(long volume);

    void onRemoteMuteChanged(boolean mute);


    class SimpleOnRendererListener implements OnRendererListener {

        @Override
        public void onRemoteNoMediaPresent() {

        }

        @Override
        public void onRemotePlaying() {

        }

        @Override
        public void onRemotePaused() {

        }

        @Override
        public void onRemoteStopped() {

        }

        @Override
        public void onRemotePrepare() {

        }

        @Override
        public void onRemoteProgressChanged(PositionInfo positionInfo) {

        }

        @Override
        public void onRemoteSeeking() {

        }

        @Override
        public void onRemoteVolumeChanged(long volume) {

        }

        @Override
        public void onRemoteMuteChanged(boolean mute) {

        }
    }

}
