package com.abooc.upnp;

import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.TransportInfo;
import org.fourthline.cling.support.model.TransportState;


interface Info {
    boolean isPlaying();

    boolean isPause();

    boolean isStop();

    boolean isTransitioning();

}

/**
 * 负责记录远端媒体播放状态、媒体信息、播放进度。
 * <p>
 * Created by author:李瑞宇
 * email:allnet@live.cn
 * on 16/7/12.
 */
public class PlayerInfo implements Info {

    private long mVolume;
    private boolean mMute = false;

    // / Track info
    private PositionInfo positionInfo = new PositionInfo();
    private MediaInfo mediaInfo = new MediaInfo();
    private TransportInfo transportInfo = new TransportInfo();

    PlayerInfo() {
    }

    @Override
    public boolean isPlaying() {
        return getTransportState() == TransportState.PLAYING;
    }

    @Override
    public boolean isPause() {
        return getTransportState() == TransportState.PAUSED_PLAYBACK;
    }

    @Override
    public boolean isStop() {
        return getTransportState() == TransportState.STOPPED;
    }

    @Override
    public boolean isTransitioning() {
        return getTransportState() == TransportState.TRANSITIONING;
    }

    public TransportState getTransportState() {
        return transportInfo.getCurrentTransportState();
    }

    public TransportInfo getTransportInfo() {
        return transportInfo;
    }

    public void updateTransportInfo(TransportInfo transportInfo) {
        this.transportInfo = transportInfo;
    }

    public PositionInfo getPositionInfo() {
        return positionInfo;
    }

    public void updatePositionInfo(PositionInfo positionInfo) {
        this.positionInfo = positionInfo;
    }

    public boolean isMute() {
        return mMute;
    }

    public long getVolume() {
        return mVolume;
    }

    public void updateVolume(long volume) {
        this.mVolume = volume;
    }

    public MediaInfo getMediaInfo() {
        return mediaInfo;
    }

    public void updateMediaInfo(MediaInfo mediaInfo) {
        this.mediaInfo = mediaInfo;
    }

    /**
     * 更新状态
     *
     * @param state
     */
    public void update(TransportState state) {
        transportInfo = new TransportInfo(state);
    }

    public void stop() {
        transportInfo = new TransportInfo(TransportState.STOPPED);
    }

    public void updateSeek(String progress) {
        positionInfo.setRelTime(progress.toString());
    }

    public void updateMute(boolean mute) {
        mMute = mute;
    }

    public void clear() {
        positionInfo = new PositionInfo();
        mediaInfo = new MediaInfo();
        transportInfo = new TransportInfo();

    }
}
