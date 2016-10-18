package demo.abooc.com.upnp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.abooc.upnp.Renderer;
import com.abooc.upnp.RendererBuilder;
import com.abooc.upnp.RendererPlayer;
import com.abooc.upnp.extra.OnGotMediaInfoCallback;
import com.abooc.upnp.extra.OnRendererListener;
import com.abooc.util.Debug;
import com.abooc.widget.Toast;

import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable;
import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.TransportState;
import org.fourthline.cling.support.model.item.Item;
import org.fourthline.cling.support.renderingcontrol.callback.GetVolume;

import demo.abooc.com.upnp.AppTestResources;
import demo.abooc.com.upnp.R;
import demo.abooc.com.upnp.UPnP;
import demo.abooc.com.upnp.model.VRVideoItem;

/**
 * 播放控制页
 */
public class PlayerActivity extends AppCompatActivity
        implements View.OnClickListener {

    public static void launch(Context context) {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    private VideoPlayerView mVideoPlayerView;
    private TextView mTextUri;
    private View mWaitingView;

    private Renderer mRenderer;
    private RendererPlayer mRendererPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRenderer = Renderer.get();
        mRendererPlayer = RendererPlayer.build(mRenderer);
        mRendererPlayer.startTrack();
        mRendererPlayer.addOnRendererListener(mSimpleOnRendererListener);

        Device boundDevice = RendererBuilder.get().getBoundDevice();
        String friendlyName = boundDevice.getDetails().getFriendlyName();
        getSupportActionBar().setSubtitle("连接设备：" + friendlyName);

        setContentView(R.layout.activity_player);
        View player = findViewById(R.id.Player);
        mVideoPlayerView = new VideoPlayerView(player);
        mVideoPlayerView.setOnSeekBarChangeListener(iOnSeekBarChangeListener);
        mVideoPlayerView.attachRouterEvent(this, iOnSeekBarChangeListener);
        mVideoPlayerView.setOnButtonClickListener(this);
        mTextUri = (TextView) findViewById(R.id.Uri);
        mWaitingView = findViewById(R.id.Waiting);

    }

    private OnRendererListener.SimpleOnRendererListener mSimpleOnRendererListener =
            new OnRendererListener.SimpleOnRendererListener() {
                @Override
                public void onRemotePlaying() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mVideoPlayerView.setState(TransportState.PLAYING);
                            mVideoPlayerView.setTitle("正在播放...");
                        }
                    });
                }

                @Override
                public void onRemotePaused() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mVideoPlayerView.setState(TransportState.PAUSED_PLAYBACK);
                            mVideoPlayerView.setTitle("暂停中...");
                        }
                    });
                }

                @Override
                public void onRemoteStopped() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mVideoPlayerView.setState(TransportState.STOPPED);
                            mVideoPlayerView.setTitle("没有媒体在播放");
                        }
                    });
                }

                @Override
                public void onRemoteProgressChanged(final PositionInfo positionInfo) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mVideoPlayerView.setPositionInfo(positionInfo);
                        }
                    });
                }
            };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mVideoPlayerView.setState(TransportState.NO_MEDIA_PRESENT);
        mVideoPlayerView.setTitle("投屏");

        mRenderer = Renderer.get();
        mRendererPlayer = RendererPlayer.build(mRenderer);
        mRendererPlayer.addOnRendererListener(mSimpleOnRendererListener);

        Device boundDevice = RendererBuilder.get().getBoundDevice();
        String friendlyName = boundDevice.getDetails().getFriendlyName();
        getSupportActionBar().setSubtitle("连接设备：" + friendlyName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.player, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_switch:
                ScanActivity.launch(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRendererPlayer.getPositionInfo();
        mRendererPlayer.getMediaInfo(mMediaInfoCallback);
        mRenderer.getMute();

        mRenderer.execute(new GetVolume(mRenderer.getRenderingControlService()) {
            @Override
            public void received(ActionInvocation invocation, final int volume) {
                Debug.anchor(volume);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mVideoPlayerView.setVolume(volume);
                        mVideoPlayerView.seekVolume(volume);
                    }
                });
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Play:
                if (mRenderer.getPlayerInfo().isStop()
                        || mRenderer.getPlayerInfo().getTransportState() == TransportState.NO_MEDIA_PRESENT) {
//                    mRendererPlayer.addCallback(onSendListener);

                    Res res = UPnP.buildRes("video/*", AppTestResources.videoUri, AppTestResources.videoUri, 0);
                    VRVideoItem videoItem = new VRVideoItem(0, "1", String.valueOf(1), "天空之城[高清国语]", "creator", res);
                    String metadata = UPnP.buildMetadataXml(videoItem);

                    mRendererPlayer.start(AppTestResources.videoUri, metadata);
                } else {
                    mRendererPlayer.play();
                }
                mVideoPlayerView.setState(TransportState.PLAYING);
                break;
            case R.id.Pause:
                mRendererPlayer.pause();
                mVideoPlayerView.setState(TransportState.PAUSED_PLAYBACK);
                break;
            case R.id.Stop:
                Toast.show("即将停止...");
                mRendererPlayer.stop();
                mVideoPlayerView.setState(TransportState.STOPPED);
                break;
            case R.id.VolumeMute:
                boolean mute = !mRenderer.getPlayerInfo().isMute();
                mRenderer.setMute(mute);
                mVideoPlayerView.setMute(mute);
                break;
        }
    }

    private OnGotMediaInfoCallback mMediaInfoCallback = new OnGotMediaInfoCallback() {
        @Override
        public void onGotMediaInfo(final MediaInfo mediaInfo) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setMediaInfo(mediaInfo);
                }
            });
        }
    };

    private SeekBar.OnSeekBarChangeListener iOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            switch (seekBar.getId()) {
                case R.id.Seek:
                    String time = ModelUtil.toTimeString(progress);
                    mVideoPlayerView.setProgressTime(time);
                    break;
                case R.id.SeekVolume:
                    mVideoPlayerView.setVolume(progress);
                    break;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            switch (seekBar.getId()) {
                case R.id.Seek:
                    mRenderer.getPlayerInfo().update(TransportState.TRANSITIONING);
                    break;
            }

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress = seekBar.getProgress();
            switch (seekBar.getId()) {
                case R.id.SeekVolume:
                    mRenderer.setVolume(progress);
                    break;
                case R.id.Seek:
                    String time = ModelUtil.toTimeString(progress);
                    mVideoPlayerView.setProgressTime(time);

                    mRendererPlayer.seek(time);
                    break;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                long volume1 = mRenderer.getPlayerInfo().getVolume() + 10;
                mRenderer.setVolume(Math.min(100, volume1));
                mVideoPlayerView.setVolume(volume1);
                mVideoPlayerView.seekVolume(volume1);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                long volume2 = mRenderer.getPlayerInfo().getVolume() - 10;
                mRenderer.setVolume(Math.max(0, volume2));
                mVideoPlayerView.setVolume(volume2);
                mVideoPlayerView.seekVolume(volume2);
                return true;
            case KeyEvent.KEYCODE_VOLUME_MUTE:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setMediaInfo(MediaInfo mediaInfo) {
        String currentURIMetaData = mediaInfo.getCurrentURIMetaData();
        AVTransportVariable.CurrentTrackMetaData trackMetaData = new AVTransportVariable.CurrentTrackMetaData(currentURIMetaData);
        mTextUri.setText(trackMetaData.getName() + ", " + mediaInfo.getCurrentURI());

        Item item = UPnP.parseCurrentURIMetaData(mediaInfo.getCurrentURIMetaData());
        if (item != null) {
            getSupportActionBar().setTitle(item.getTitle());
        }
    }

    private void updateStateMessage(TransportState state) {
        switch (state) {
            case NO_MEDIA_PRESENT:
                mVideoPlayerView.setTitle("没有媒体在播放");
                break;
            case RECORDING:
                mVideoPlayerView.setTitle("准备播放...");
                break;
            case PLAYING:
                mVideoPlayerView.setTitle("正在播放...");
                break;
            case PAUSED_PLAYBACK:
                mVideoPlayerView.setTitle("暂停中...");
                break;
            case STOPPED:
                mVideoPlayerView.setTitle("播放已停止");
                break;
            case TRANSITIONING:
                mVideoPlayerView.setTitle("Seek...");
                break;
        }
    }

    public void onStopEvent(View view) {
        mRendererPlayer.stop();
        mVideoPlayerView.setState(TransportState.STOPPED);
    }

    public void onStartEvent(View view) {
        Res res = UPnP.buildRes("video/vr", AppTestResources.videoUri, AppTestResources.videoUri, 0);
        VRVideoItem videoItem = new VRVideoItem(3, "1", String.valueOf(1), "天空之城[高清国语]", "creator", res);
        String metadata = UPnP.buildMetadataXml(videoItem);
        mRendererPlayer.start(AppTestResources.videoUri, metadata);
    }

    public void onGetVolumeEvent(View view) {
        mRenderer.execute(new GetVolume(mRenderer.getRenderingControlService()) {
            @Override
            public void received(ActionInvocation invocation, final int volume) {
                Debug.anchor(volume);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRenderer.getPlayerInfo().updateVolume(volume);
                        mVideoPlayerView.setVolume(volume);
                    }
                });
            }

            @Override
            public void failure(ActionInvocation arg0, UpnpResponse arg1, String arg2) {
            }
        });
    }

    public void onGetMediaInfoEvent(View view) {
        mRendererPlayer.getMediaInfo(mMediaInfoCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRendererPlayer.stop();
        mRendererPlayer.stopTrack();
        mVideoPlayerView.setState(TransportState.STOPPED);
    }
}
