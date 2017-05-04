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

import com.abooc.upnp.DlnaManager;
import com.abooc.upnp.Renderer;
import com.abooc.upnp.RendererPlayer;
import com.abooc.upnp.extra.OnGotMediaInfoCallback;
import com.abooc.upnp.extra.OnRendererListener;
import com.abooc.util.Debug;
import com.abooc.widget.Toast;

import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.TransportState;
import org.fourthline.cling.support.model.item.Item;
import org.fourthline.cling.support.model.item.Photo;
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
    private TextView mMessageView;
    private View mWaitingView;

    private Renderer mRenderer;
    private RendererPlayer mRendererPlayer;
    private RendererInfoView mRendererInfoView;
    private MediaInfoView mMediaInfoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRenderer = Renderer.get();
        mRendererPlayer = RendererPlayer.get();
        mRendererPlayer.startTrack();
        mRendererPlayer.addOnRendererListener(mSimpleOnRendererListener);

        Device boundDevice = DlnaManager.getInstance().getBoundDevice();
        String friendlyName = boundDevice.getDetails().getFriendlyName();
        getSupportActionBar().setSubtitle("连接设备：" + friendlyName);

        setContentView(R.layout.activity_player);
        View player = findViewById(R.id.Player);
        mVideoPlayerView = new VideoPlayerView(player);
        mVideoPlayerView.setOnSeekBarChangeListener(iOnSeekBarChangeListener);
        mVideoPlayerView.attachRouterEvent(this, iOnSeekBarChangeListener);
        mVideoPlayerView.setOnButtonClickListener(this);
        mMessageView = (TextView) findViewById(R.id.MessageView);
        mWaitingView = findViewById(R.id.Waiting);

        mRendererInfoView = new RendererInfoView(findViewById(R.id.RendererInfoView));
        mMediaInfoView = new MediaInfoView(findViewById(R.id.MediaInfoView));

    }

    private OnRendererListener.SimpleOnRendererListener mSimpleOnRendererListener =
            new OnRendererListener.SimpleOnRendererListener() {
                @Override
                public void onRemoteStateChanged(TransportState state) {
                    mMediaInfoView.setState(state.name());

                }

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

                @Override
                public void onRemotePlayEnd() {
                    Debug.error("播放完毕");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mVideoPlayerView.setState(TransportState.STOPPED);
                            mVideoPlayerView.setTitle("没有媒体在播放");
                        }
                    });
                }

                @Override
                public void onRemoteMuteChanged(boolean mute) {
                    mRendererInfoView.setMute(mute);
                }

                @Override
                public void onRemoteVolumeChanged(long volume) {
                    mRendererInfoView.setVolume(volume);
                }
            };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mRenderer = Renderer.get();
        mRendererPlayer = RendererPlayer.get();
        mRendererPlayer.addOnRendererListener(mSimpleOnRendererListener);

        Device boundDevice = DlnaManager.getInstance().getBoundDevice();
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
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_switch:
                ScanActivity.launch(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Device boundDevice = DlnaManager.getInstance().getBoundDevice();
        String friendlyName = boundDevice.getDetails().getFriendlyName();
        mRendererInfoView.setDeviceInfo(friendlyName + "" + boundDevice.getType());


        mRendererPlayer.getMediaInfo(mMediaInfoCallback);
        mRenderer.getMute();

        if (mRenderer.isRenderingControl()) {
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

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Play:
                Device boundDevice = DlnaManager.getInstance().getBoundDevice();
                Renderer.debug(boundDevice);

                if (mRenderer.getPlayerInfo().isStop()
                        || mRenderer.getPlayerInfo().getTransportState() == TransportState.NO_MEDIA_PRESENT) {
                    Debug.anchor();
//                    mRendererPlayer.addCallback(onSendListener);
//                    flyVideo();
                    flyImage(AppTestResources.imageUri);
                } else {
                    Debug.anchor();
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
                mRendererPlayer.stopTrack();
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
            final Item item = UPnP.parseCurrentURIMetaData(mediaInfo.getCurrentURIMetaData());

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (item == null) {
                        getSupportActionBar().setTitle("--");
                        mVideoPlayerView.setState(TransportState.STOPPED);
                        mMediaInfoView.setVisibility(View.GONE);
                        mMediaInfoView.clear();
                        mMessageView.setVisibility(View.VISIBLE);
                        return;
                    }

                    getSupportActionBar().setTitle(item.getTitle());

                    mMessageView.setVisibility(View.GONE);
                    mMediaInfoView.setVisibility(View.VISIBLE);


                    mMediaInfoView.setID(item.getId());
                    mMediaInfoView.setParentID(item.getParentID());
                    mMediaInfoView.setRefID(item.getRefID());
                    mMediaInfoView.setTitle(item.getTitle());
                    mMediaInfoView.setCreatorText(item.getCreator());
//                    mMediaInfoView.setDateText(trackMetaData.getName());
                    mMediaInfoView.setUrlText(mediaInfo.getCurrentURI());
                    mMediaInfoView.setMetadataText(mediaInfo.getCurrentURIMetaData());
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
        mRendererPlayer.stopTrack();
        mVideoPlayerView.setState(TransportState.STOPPED);
    }

    public void onStartEvent(View view) {
        switch (view.getId()) {
            case R.id.ButtonStartVideo:
                flyVideo();
                break;
            case R.id.ButtonStartImage:
                flyImage(AppTestResources.imageUri);
                break;
        }
    }

    public void flyVideo() {
        Res res = UPnP.buildRes("video/vr", AppTestResources.videoUri, AppTestResources.videoUri, 0);
        VRVideoItem videoItem = new VRVideoItem(3, "1", String.valueOf(1), "天空之城[高清国语]", "creator", res);
        String metadata = UPnP.buildMetadataXml(videoItem);
        mRendererPlayer.start(AppTestResources.videoUri, metadata);
    }

    public void flyImage(String url) {
        url = "http://192.168.8.171:8196/ImageItem-226";
        url = "http://images.apple.com/v/home/cx/images/gallery/iphone_square_large.jpg";
        Res res = UPnP.buildRes("image/jpeg", "filePath", url, 0);
        Photo videoItem = new Photo("1", String.valueOf(1), "图来了", null, null, res);
        String metadata = UPnP.buildMetadataXml(videoItem);
        mRendererPlayer.start(url, metadata);
    }

    public void onGetVolumeEvent(View view) {
        if (mRenderer.isRenderingControl()) {
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
