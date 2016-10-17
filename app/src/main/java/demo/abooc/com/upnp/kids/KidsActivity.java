package demo.abooc.com.upnp.kids;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.abooc.upnp.Discovery;
import com.abooc.upnp.Player;
import com.abooc.upnp.PlayerInfo;
import com.abooc.upnp.Renderer;
import com.abooc.upnp.RendererPlayer;
import com.abooc.upnp.extra.OnActionListener;
import com.abooc.upnp.model.CDevice;
import com.abooc.upnp.model.DeviceDisplay;
import com.abooc.util.Debug;
import com.abooc.widget.Toast;

import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.model.gena.CancelReason;
import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.state.StateVariableValue;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportLastChangeParser;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable;
import org.fourthline.cling.support.lastchange.EventedValue;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.support.model.TransportState;
import org.fourthline.cling.support.renderingcontrol.lastchange.ChannelVolume;
import org.fourthline.cling.support.renderingcontrol.lastchange.RenderingControlLastChangeParser;
import org.fourthline.cling.support.renderingcontrol.lastchange.RenderingControlVariable;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import demo.abooc.com.upnp.DevicesListAdapter;
import demo.abooc.com.upnp.R;
import demo.abooc.com.upnp.Tools;
import demo.abooc.com.upnp.UITimer;
import demo.abooc.com.upnp.activity.VideoPlayerView;

public class KidsActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener, View.OnClickListener, Observer {

    public static void launch(Context context) {
        Intent intent = new Intent(context, KidsActivity.class);
        context.startActivity(intent);
    }

    private DevicesListAdapter listAdapter;

    Renderer mRenderer;
    Player mPlayer;


    private Discovery mDiscovery;
    VideoPlayerView mVideoPlayer;

    View mPlayerLayout;
    ListView mListView;
    TextView mTextUri;
    View mSendingView;
    TextView mEmptyView;


    String videoUri = "http://192.168.8.1:8200/MediaItems/31.mkv";
    //    String videoUri = "http://baobab.wdjcdn.com/1457162012752491010143.mp4";
    String imageUri = "http://s.cn.bing.net/az/hprichbg/rb/DiamondHead_ZH-CN8551687099_1920x1080.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toast.init(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        mPlayerLayout = findViewById(R.id.PlayerLayout);
        View player = findViewById(R.id.Player);
        mVideoPlayer = new VideoPlayerView(player);
        mTextUri = (TextView) findViewById(R.id.Uri);
        mSendingView = findViewById(R.id.Sending);
        mEmptyView = (TextView) findViewById(android.R.id.empty);


        mDiscovery = Discovery.get();
        mDiscovery.bindServer(this);

//        mDiscovery.addObserver(new Observer() {
//            @Override
//            public void update(Observable observable, Object data) {
//                ArrayList<DeviceDisplay> list = DevicesCache.getInstance().getList();
//                Debug.anchor(list.size());
//                listAdapter.update(list);
//                if (listAdapter.isEmpty()) {
//                    if (iUITimer.isRunning()) {
//                        mEmptyView.setText("Loading...");
//                    } else {
//                        mEmptyView.setText("没有可用的设备");
//                    }
//                }
//            }
//        });

        initUPnPView();
//        ArrayList<DeviceDisplay> list = DevicesCache.getInstance().getList();
//        listAdapter.update(list);
    }

    private UITimer iUITimer = new UITimer(2) {
        @Override
        public void onStart() {
            mListView.clearChoices();

            mDiscovery.removeAll();
            mDiscovery.getUpnpService().getRegistry().pause();

            mEmptyView.setText("延迟...");
            mEmptyView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onCancel() {
            mEmptyView.setText("搜索被取消");
            mEmptyView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onTick(int total, int tick) {
            if (tick == 1) {
                mDiscovery.getUpnpService().getRegistry().resume();
                mDiscovery.search();
                mEmptyView.setText("开始扫描...");
            }
        }

        @Override
        public void onFinish() {
            Debug.anchor();
            if (listAdapter.isEmpty()) {
                mEmptyView.setText("没有可用的设备");
            } else {
                listAdapter.notifyDataSetChanged();
            }
        }
    };

    void initUPnPView() {
        mListView = (ListView) findViewById(android.R.id.list);
        mListView.setEmptyView(mEmptyView);
        listAdapter = new DevicesListAdapter(this);
        mListView.setAdapter(listAdapter);

        mListView.setOnItemClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.browser, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                if (iUITimer.isRunning()) {
                    iUITimer.cancel();
                } else {
                    iUITimer.start();
                }
                break;
            case R.id.menu_settings:
                break;
            case R.id.menu_quit:
                System.exit(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mDiscovery.unbindServer(this);

        if (mPlayer != null) {
            getRendererPlayer().stopTrack();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                long volume1 = mRenderer.getPlayerInfo().getVolume();
                mRenderer.volume(Math.min(100, volume1 + 10));
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                long volume2 = mRenderer.getPlayerInfo().getVolume();
                mRenderer.volume(Math.max(0, volume2 - 10));
                return true;
            case KeyEvent.KEYCODE_VOLUME_MUTE:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    SubscriptionCallback RenderSubscription;
    SubscriptionCallback PlayerSubscription;


    DeviceDisplay mDeviceDisplay;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        mDeviceDisplay = listAdapter.getItem(position);
        mDeviceDisplay.setChecked(true);

        setTitle(mDeviceDisplay.getDevice().getFriendlyName());
        mVideoPlayer.attachRouterEvent(KidsActivity.this, iOnSeekBarChangeListener);

        mRenderer = Renderer.build(mDiscovery.getUpnpService().getControlPoint(), (CDevice) mDeviceDisplay.getDevice());

        PlayerInfo state = mRenderer.getPlayerInfo();
//        state.setOnProgressUpdateListener(iOnProgressUpdateListener);
        state.addObserver(KidsActivity.this);
        mRenderer.getVolume();
        mRenderer.getMute();

        mPlayer = RendererPlayer.build(mRenderer);
        getRendererPlayer().getPositionInfo();
        getRendererPlayer().getMediaInfo(null);

        RenderSubscription = new SubscriptionCallback(mRenderer.getRenderingControlService()) {
            @Override
            protected void failed(GENASubscription subscription, UpnpResponse responseStatus, Exception exception, String defaultMsg) {
                Debug.anchor();
            }

            @Override
            protected void established(GENASubscription subscription) {
                Debug.anchor();

            }

            @Override
            protected void ended(GENASubscription subscription, CancelReason reason, UpnpResponse responseStatus) {
                Debug.anchor();

            }

            @Override
            protected void eventReceived(GENASubscription subscription) {
                System.out.println("Event: " + subscription.getCurrentSequence().getValue());
                Map<String, StateVariableValue> values = subscription.getCurrentValues();

                Debug.anchor(Tools.toString2(values));

                StateVariableValue variableValue = values.get("LastChange");

                try {

                    LastChange lastChange = new LastChange(new RenderingControlLastChangeParser(),
                            variableValue.toString());

                    final EventedValue<ChannelVolume> volume = lastChange.getEventedValue(0, RenderingControlVariable.Volume.class);
                    Debug.anchor(volume);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void eventsMissed(GENASubscription subscription, int numberOfMissedEvents) {
                Debug.anchor(subscription);

            }
        };

        PlayerSubscription = new SubscriptionCallback(mRenderer.getAVTransportService()) {

            public void established(GENASubscription sub) {
                Debug.anchor("Established: " + sub.getSubscriptionId());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mVideoPlayer.setOnButtonClickListener(KidsActivity.this);
                        mVideoPlayer.setOnSeekBarChangeListener(iOnSeekBarChangeListener);

                        mPlayerLayout.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            protected void failed(GENASubscription subscription, UpnpResponse response, Exception exception, String defaultMsg) {
                Debug.e(createDefaultFailureMessage(response, exception));
                Toast.show("远端没有响应");
            }

            @Override
            public void ended(GENASubscription sub, CancelReason reason, UpnpResponse response) {
                // Reason should be null, or it didn't end regularly
                Debug.anchor();
                Toast.show("订阅结束");
            }

            @Override
            public void eventReceived(GENASubscription sub) {
                System.out.println("Event: " + sub.getCurrentSequence().getValue());
                Map<String, StateVariableValue> values = sub.getCurrentValues();
                Debug.anchor(Tools.toString2(values));

                StateVariableValue variableValue = values.get("LastChange");

                try {
                    LastChange lastChange = new LastChange(new AVTransportLastChangeParser(),
                            variableValue.toString());

                    final AVTransportVariable.TransportState state = lastChange
                            .getEventedValue(0, AVTransportVariable.TransportState.class);

                    if (state != null) {
                        Debug.anchor(state);
                        switch (state.getValue()) {
                            case CUSTOM:
                                break;
                            case NO_MEDIA_PRESENT:
                                // 没有媒体在播放
                                break;
                            case PLAYING:
                                ((RendererPlayer) mPlayer).startTrack();
                                mRenderer.getPlayerInfo().update(TransportState.PLAYING);
                                break;
                            case TRANSITIONING:
                                if (mRenderer.getPlayerInfo().isStop()
                                        || mRenderer.getPlayerInfo().getTransportState() == TransportState.NO_MEDIA_PRESENT) {
                                    mPlayer.onPrepare();
                                } else {
                                    mRenderer.getPlayerInfo().update(TransportState.TRANSITIONING);
                                }
                                break;
                            case PAUSED_PLAYBACK:
                                ((RendererPlayer) mPlayer).stopTrack();
                                mRenderer.getPlayerInfo().update(TransportState.PAUSED_PLAYBACK);
                                break;
                            case STOPPED:
                                ((RendererPlayer) mPlayer).stopTrack();
                                mRenderer.getPlayerInfo().stop();
                                break;
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void eventsMissed(GENASubscription sub, int numberOfMissedEvents) {
                Debug.anchor("Missed events: " + numberOfMissedEvents);
            }
        };

        mDiscovery.getUpnpService().getControlPoint().execute(PlayerSubscription);
        mDiscovery.getUpnpService().getControlPoint().execute(RenderSubscription);

    }

    SeekBar.OnSeekBarChangeListener iOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            switch (seekBar.getId()) {
                case R.id.Seek:
                    String time = ModelUtil.toTimeString(progress);
                    mVideoPlayer.setProgressTime(time);
                    break;
                case R.id.SeekVolume:
                    mVideoPlayer.setVolume(progress);
                    break;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            switch (seekBar.getId()) {
                case R.id.Seek:
//                    mRenderer.getPlayerInfo().setOnProgressUpdateListener(null);
                    mRenderer.getPlayerInfo().update(TransportState.TRANSITIONING);
                    break;
            }

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress = seekBar.getProgress();
            switch (seekBar.getId()) {
                case R.id.SeekVolume:
                    mPlayer.volume(progress);
                    break;
                case R.id.Seek:
                    String time = ModelUtil.toTimeString(progress);
                    mVideoPlayer.setProgressTime(time);

                    ((RendererPlayer) mPlayer).addCallback(onActionListener);
                    mPlayer.seek(time);
//                    mRenderer.getPlayerInfo().setOnProgressUpdateListener(iOnProgressUpdateListener);
                    break;
            }
        }
    };

    private RendererPlayer getRendererPlayer() {
        return ((RendererPlayer) mPlayer);
    }


    private OnActionListener onActionListener = new OnActionListener() {
        @Override
        public void onSend() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSendingView.setVisibility(View.VISIBLE);
                }
            });
        }

        @Override
        public void onSendFinish(boolean success) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSendingView.setVisibility(View.INVISIBLE);
                }
            });
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Play:
                if (mRenderer.getPlayerInfo().isStop()
                        || mRenderer.getPlayerInfo().getTransportState() == TransportState.NO_MEDIA_PRESENT) {
                    getRendererPlayer().addCallback(onActionListener);

                    getRendererPlayer().start(videoUri, "");
                } else {
                    mPlayer.play();
                }

                break;
            case R.id.Pause:
                mPlayer.pause();
                break;
            case R.id.VolumeMute:
//                mPlayer.setMute(!getRendererPlayer().getPlayerState().isMute());
                break;
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        PlayerInfo playerInfo = mRenderer.getPlayerInfo();

        TransportState playState = playerInfo.getTransportState();
        long volume = playerInfo.getVolume();
        boolean mute = playerInfo.isMute();

        updateTitle(playState);
        mVideoPlayer.setState(playState);
        mVideoPlayer.setVolume((int) volume);
        mVideoPlayer.seekVolume((int) volume);
        mVideoPlayer.setMute(mute);

        if (playerInfo.getMediaInfo() != null) {
            mTextUri.setText(playerInfo.getMediaInfo().getCurrentURI());
        }
    }

    private void updateTitle(TransportState state) {
        switch (state) {
            case NO_MEDIA_PRESENT:
                mVideoPlayer.setTitle("没有媒体在播放");
                break;
            case RECORDING:
                mVideoPlayer.setTitle("准备播放...");
                break;
            case PLAYING:
                mVideoPlayer.setTitle("正在播放...");
                break;
            case PAUSED_PLAYBACK:
                mVideoPlayer.setTitle("暂停中...");
                break;
            case STOPPED:
                mVideoPlayer.setTitle("播放已停止");
                break;
            case TRANSITIONING:
                mVideoPlayer.setTitle("Seek...");
                break;
        }
    }
}
