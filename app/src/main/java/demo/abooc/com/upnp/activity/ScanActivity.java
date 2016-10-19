package demo.abooc.com.upnp.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.abooc.upnp.Discovery;
import com.abooc.upnp.DlnaManager;
import com.abooc.upnp.model.CDevice;
import com.abooc.upnp.model.DeviceDisplay;
import com.abooc.util.Debug;
import com.abooc.widget.Toast;

import org.fourthline.cling.android.NetworkUtils;
import org.fourthline.cling.model.message.header.DeviceTypeHeader;
import org.fourthline.cling.model.message.header.STAllHeader;
import org.fourthline.cling.model.message.header.ServiceTypeHeader;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.transport.Router;
import org.fourthline.cling.transport.RouterException;

import demo.abooc.com.upnp.DevicesListAdapter;
import demo.abooc.com.upnp.R;
import demo.abooc.com.upnp.UITimer;

/**
 * 扫描设备页
 */
public class ScanActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener {

    public static void launch(Context context) {
        Intent intent = new Intent(context, ScanActivity.class);
        context.startActivity(intent);
    }

    private Discovery mDiscovery;
    private ListView mListView;
    private TextView mEmptyView;
    private DevicesListAdapter mListAdapter;
    private WiFiReceiver iWiFiReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scan);
        mEmptyView = (TextView) findViewById(android.R.id.empty);

        mDiscovery = Discovery.get();
        mDiscovery.addDefaultRegistryListener(new DefaultRegistryListener() {

            @Override
            public void remoteDeviceAdded(Registry registry, final RemoteDevice device) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CDevice cDevice = new CDevice(device);
                        if (cDevice.asService("AVTransport")) {
                            DeviceDisplay deviceDisplay = new DeviceDisplay(cDevice);
                            mListAdapter.add(deviceDisplay);
                            getSupportActionBar().setTitle("扫描设备...，已发现" + mListAdapter.getCount() + "个");

                            Device boundDevice = DlnaManager.getInstance().getBoundDevice();
                            if (device.equals(boundDevice)) {
                                deviceDisplay.setChecked(true);
                            }
                        }
                    }
                });
            }

            @Override
            public void remoteDeviceRemoved(Registry registry, final RemoteDevice device) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DeviceDisplay deviceDisplay = new DeviceDisplay(new CDevice(device));

                        mListAdapter.remove(deviceDisplay);
                        if (mListAdapter.isEmpty()) {
                            if (!iUITimer.isRunning()) {
                                mEmptyView.setText("没有可用的设备");
                            }
                        }
                        getSupportActionBar().setTitle("扫描设备...，已发现" + mListAdapter.getCount() + "个");
                    }
                });
            }
        });

        initUPnPView();

        iWiFiReceiver = new WiFiReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(iWiFiReceiver, intentFilter);
    }

    void initUPnPView() {
        mListView = (ListView) findViewById(android.R.id.list);
        mListView.setEmptyView(mEmptyView);
        mListView.setOnItemClickListener(this);
        mListAdapter = new DevicesListAdapter(this);
        mListView.setAdapter(mListAdapter);
    }

    private void setSubtitle(String subtitle) {
        getSupportActionBar().setSubtitle(subtitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scan, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void turnRouter() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Router router = DlnaManager.getInstance().getBindRouter();
                    if (router.isEnabled()) {
                        Toast.show("关闭");
                        Debug.anchor("disable");
                        router.disable();
                    } else {
                        Toast.show("开启");
                        Debug.anchor("enable");
                        router.enable();
                    }
                } catch (RouterException e) {
                    Debug.e(e.getMessage());
                }
            }
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_switchRouter:
//                NetworkInfo networkInfo = NetworkUtils.getConnectedNetworkInfo(this);
//                Debug.anchor(networkInfo);
//                if (networkInfo != null) {
//                    getSupportActionBar().setSubtitle("当前Wi-Fi：" + networkInfo.getExtraInfo());
//                }
                turnRouter();
                break;
            case R.id.menu_serviceType: // Default
                if (iUITimer.isRunning()) {
                    iUITimer.cancel();
                } else {
                    iUITimer.start();
                }
                break;
            case R.id.menu_serviceType_contentDirectory:
                if (iUITimer.isRunning()) {
                    iUITimer.cancel();
                } else {
                    iUITimer.start();
//                    DeviceType type = new DeviceType("schemas-upnp-org", "MediaServer", 1);
//                    Discovery.get().getUpnpService().getControlPoint().search(new DeviceTypeHeader(type));
                    UDAServiceType type = new UDAServiceType("ContentDirectory", 1);
                    DlnaManager.getInstance().getUpnpService().getControlPoint().search(new ServiceTypeHeader(type));
                }
                break;
            case R.id.menu_deviceType:
                if (iUITimer.isRunning()) {
                    iUITimer.cancel();
                } else {
                    iUITimer.start();
                    DeviceType type = new DeviceType("schemas-upnp-org", "MediaRenderer", 1);
                    DlnaManager.getInstance().getUpnpService().getControlPoint().search(new DeviceTypeHeader(type));
                }
                break;
            case R.id.menu_all:
                if (iUITimer.isRunning()) {
                    iUITimer.cancel();
                } else {
                    iUITimer.start();

                    DlnaManager.getInstance().getUpnpService().getControlPoint().search(new STAllHeader());
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDiscovery.search();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private UITimer iUITimer = new UITimer(2) {
        @Override
        public void onStart() {
            getSupportActionBar().setTitle("扫描设备...");
            mEmptyView.setText("扫描中...");

            Discovery.get().removeAll();
            DlnaManager.getInstance().getUpnpService().getControlPoint().search();
        }

        @Override
        public void onCancel() {
            mEmptyView.setText("搜索被取消");
        }

        @Override
        public void onFinish() {
            Debug.anchor();
            if (mListAdapter.isEmpty()) {
                mEmptyView.setText("未找到可用的设备");
            } else {
                mListAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        DeviceDisplay mDeviceDisplay = mListAdapter.getItem(position);
        mDeviceDisplay.setChecked(true);
        setTitle(mDeviceDisplay.getDevice().getFriendlyName());

        CDevice cDevice = (CDevice) mDeviceDisplay.getDevice();
        Device device = cDevice.getDevice();
        DlnaManager.getInstance().bind(device, null);

        PlayerActivity.launch(this);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(iWiFiReceiver);
    }

    private class WiFiReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                boolean isWifi = NetworkUtils.isWifi(networkInfo);

                if (isWifi && networkInfo.isAvailable()) {
                    switch (networkInfo.getState()) {
                        case CONNECTING:
                            setSubtitle("当前Wi-Fi：连接中...");
                            break;
                        case CONNECTED:
                            setSubtitle("当前Wi-Fi：" + networkInfo.getExtraInfo());
                            break;
                        case DISCONNECTING:

                            setSubtitle("当前Wi-Fi：关闭中...");
                            break;
                        case DISCONNECTED:

                            setSubtitle("当前Wi-Fi：已关闭");
                            break;
                        default:

                            setSubtitle("当前Wi-Fi：未知 " + networkInfo.getState());
                            break;
                    }
                }
            }
        }

        boolean isWifi(WifiInfo wifiInfo) {
            return wifiInfo.getNetworkId() > 0
                    && wifiInfo.getLinkSpeed() > 0;
        }
    }

}
