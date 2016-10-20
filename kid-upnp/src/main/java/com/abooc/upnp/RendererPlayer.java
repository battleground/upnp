package com.abooc.upnp;

import android.util.Log;

import com.abooc.upnp.extra.OnActionListener;
import com.abooc.upnp.extra.OnGotMediaInfoCallback;
import com.abooc.upnp.extra.OnRendererListener;
import com.abooc.util.Debug;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.avtransport.callback.GetMediaInfo;
import org.fourthline.cling.support.avtransport.callback.GetPositionInfo;
import org.fourthline.cling.support.avtransport.callback.GetTransportInfo;
import org.fourthline.cling.support.avtransport.callback.Pause;
import org.fourthline.cling.support.avtransport.callback.Play;
import org.fourthline.cling.support.avtransport.callback.Seek;
import org.fourthline.cling.support.avtransport.callback.SetAVTransportURI;
import org.fourthline.cling.support.avtransport.callback.Stop;
import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.TransportInfo;
import org.fourthline.cling.support.model.TransportState;
import org.fourthline.cling.support.renderingcontrol.callback.SetMute;

/**
 * 远端媒体播放器
 * <p>
 * Created by author:李瑞宇
 * email:allnet@live.cn
 * on 16/7/12.
 */
public class RendererPlayer implements Runnable, Player, OnActionListener {

    private static final RendererPlayer mOur = new RendererPlayer();
    private Renderer iRenderer;
    private Thread iThread;

    private PlayerInfo mPlayerInfo;

    private OnGotMediaInfoCallback mOnGotMediaInfoCallback;

    public void addOnGotMediaInfoCallback(OnGotMediaInfoCallback callback) {
        mOnGotMediaInfoCallback = callback;
    }

    private RendererPlayer() {
        Debug.debug();
    }

    protected static RendererPlayer build(Renderer renderer) {
        mOur.iRenderer = renderer;
        mOur.mPlayerInfo = renderer.getPlayerInfo();
        return mOur;
    }

    public static RendererPlayer get() {
        return mOur;
    }

    public Renderer getRenderer() {
        return iRenderer;
    }

    private Service getAVTransportService() {
        return iRenderer.getAVTransportService();
    }

    private Service getRenderingControlService() {
        return iRenderer.getRenderingControlService();
    }

    private OnActionListener iTempOnActionListener;

    public void addCallback(OnActionListener listener) {
        iTempOnActionListener = listener;
    }

    private void execute(final ActionCallback actionCallback) {
        onSend();
        iRenderer.execute(actionCallback);
    }

    private OnRendererListener mOnRendererListener = new OnRendererListener.SimpleOnRendererListener();

    public void addOnRendererListener(OnRendererListener listener) {
        mOnRendererListener = listener;
    }

    public void start(String uri, String metadata) {
        if (!iRenderer.isAVTransport()) return;
        Debug.anchor(uri + "\n metadata:" + metadata);
        execute(new SetAVTransportURI(getAVTransportService(), uri, metadata) {
            @Override
            public void success(ActionInvocation invocation) {
                play();
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {

                onSendFinish(false);
            }
        });
    }

    @Override
    public void onPrepare() {
        mPlayerInfo.update(TransportState.RECORDING);
    }

    @Override
    public void play() {
        if (!iRenderer.isAVTransport()) return;
        execute(new Play(getAVTransportService()) {
            @Override
            public void success(ActionInvocation invocation) {
                mOnRendererListener.onRemotePlaying();
                mOnRendererListener.onRemoteStateChanged(TransportState.PLAYING);
                mPlayerInfo.update(TransportState.PLAYING);
                Debug.anchor("success");

                onSendFinish(true);
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {

                Debug.e(invocation);
                onSendFinish(false);
            }
        });
    }

    @Override
    public void pause() {
        if (!iRenderer.isAVTransport()) return;
        execute(new Pause(getAVTransportService()) {
            @Override
            public void success(ActionInvocation invocation) {
                mOnRendererListener.onRemotePaused();
                mOnRendererListener.onRemoteStateChanged(TransportState.PAUSED_PLAYBACK);
                mPlayerInfo.update(TransportState.PAUSED_PLAYBACK);

                onSendFinish(true);
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {

                onSendFinish(false);
            }
        });
    }

    @Override
    public void volume(final long volume) {
        iRenderer.setVolume(volume);
    }

    @Override
    public void stop() {
        if (!iRenderer.isAVTransport()) return;
        execute(new Stop(getAVTransportService()) {
            @Override
            public void success(ActionInvocation invocation) {
                mOnRendererListener.onRemoteStopped();
                mOnRendererListener.onRemoteStateChanged(TransportState.STOPPED);
                onSendFinish(true);
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {

                onSendFinish(false);
            }
        });
    }

    /**
     * 进度调动
     *
     * @param progress 目标时间，格式HH:mm:ss
     */
    @Override
    public void seek(final String progress) {
        if (!iRenderer.isAVTransport()) return;
        Debug.anchor(progress);

        execute(new Seek(getAVTransportService(), progress.toString()) {
            @Override
            public void success(ActionInvocation invocation) {
                mPlayerInfo.updateSeek(progress);
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                onSendFinish(false);
            }
        });
    }

    @Override
    public void setMute(boolean mute) {
        if (!iRenderer.isRenderingControl()) return;
        final boolean b = mute;
        Debug.anchor("mute:" + mute);
        execute(new SetMute(getRenderingControlService(), mute) {
            @Override
            public void success(ActionInvocation invocation) {
                mOnRendererListener.onRemoteMuteChanged(b);
                mPlayerInfo.updateMute(b);

                onSendFinish(true);

            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                onSendFinish(false);
            }
        });

    }

    private boolean hasCallEnd = false;

    public void getPositionInfo() {
        if (!iRenderer.isAVTransport()) return;
        execute(new GetPositionInfo(getAVTransportService()) {
            @Override
            public void received(ActionInvocation invocation, PositionInfo positionInfo) {
                mOnRendererListener.onRemoteProgressChanged(positionInfo);

                long duration = positionInfo.getTrackDurationSeconds();
                long progress = positionInfo.getTrackElapsedSeconds();
                if (duration > 0
                        && duration >= progress
                        && (duration - progress) <= toleranceSeconds) {
                    if (!hasCallEnd) {
                        hasCallEnd = true;
                        mOnRendererListener.onRemotePlayEnd();
                    }
                } else {
                    hasCallEnd = false;
                }
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                Debug.e(operation.toString());
                onSendFinish(false);
            }
        });
    }

    public void getMediaInfo(final OnGotMediaInfoCallback callback) {
        if (!iRenderer.isAVTransport()) return;
        execute(new GetMediaInfo(getAVTransportService()) {
            @Override
            public void received(ActionInvocation invocation, MediaInfo mediaInfo) {
                callback.onGotMediaInfo(mediaInfo);
                mPlayerInfo.updateMediaInfo(mediaInfo);
                onSendFinish(true);
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
            }
        });
    }

    public void getTransportInfo() {
        if (!iRenderer.isAVTransport()) return;
        execute(new GetTransportInfo(getAVTransportService()) {
            @Override
            public void received(ActionInvocation invocation, TransportInfo transportInfo) {
                TransportState transportState = transportInfo.getCurrentTransportState();
                mOnRendererListener.onRemoteStateChanged(transportState);
                handState(transportState);
                mPlayerInfo.update(transportState);
                mPlayerInfo.updateTransportInfo(transportInfo);
                onSendFinish(true);

            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
            }
        });
    }

    private void handState(TransportState state) {
        Debug.anchor(state);
        switch (state) {
            case NO_MEDIA_PRESENT:
                mOnRendererListener.onRemoteNoMediaPresent();
                break;
            case RECORDING:
                mOnRendererListener.onRemotePrepare();
                break;
            case PLAYING:
                mOnRendererListener.onRemotePlaying();
                break;
            case PAUSED_PLAYBACK:
                mOnRendererListener.onRemotePaused();
                break;
            case STOPPED:
                mOnRendererListener.onRemoteStopped();
                break;
            case TRANSITIONING:
                mOnRendererListener.onRemoteSeeking();
                break;
        }
    }

    @Override
    public void onSend() {
        iRenderer.setSending(true);
        if (iTempOnActionListener != null) {
            iTempOnActionListener.onSend();
        }
    }

    @Override
    public void onSendFinish(boolean success) {
        iRenderer.setSending(false);
        if (iTempOnActionListener != null) {
            iTempOnActionListener.onSendFinish(success);
            iTempOnActionListener = null;
        }
    }

    @Override
    public void finalize() {
        Debug.e(this);
    }

    private boolean isRunning;

    public void startTrack() {
        isRunning = true;
        iThread = new Thread(this);
        iThread.start();
        Debug.anchor();
    }

    public void stopTrack() {
        isRunning = false;
        if (iThread != null)
            iThread.interrupt();
        iThread = null;
        Debug.anchor();
    }

    /**
     * 持久心跳，
     * 不断获取远端播放的最新状态，包括：
     * 播放状态、
     * 在播媒体信息
     */
    @Override
    public void run() {
        Debug.anchor();
        Thread thisThread = Thread.currentThread();
        try {
            int count = 0;
            while (isRunning) {
                count++;

                if (mPlayerInfo.isPlaying()) {
                    getPositionInfo();
                }

                getTransportInfo();

                if ((count % 6) == 0) {
                    count = 0;
                    getMediaInfo(mOnGotMediaInfoCallback);
                }

                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            Log.i(getClass().getSimpleName(), "State updater interrupt, new state " + ((mPlayerInfo.isPause()) ? "pause" : "start"));
        }
    }

    private static int toleranceSeconds = 2;

    /**
     * 播放完毕误差值
     *
     * @param seconds 单位秒
     */
    public static void setEndTolerance(int seconds) {
        toleranceSeconds = seconds;
    }
}
