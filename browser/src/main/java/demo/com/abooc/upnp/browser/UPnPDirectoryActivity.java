package demo.com.abooc.upnp.browser;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.abooc.dlna.media.AppRootContainer;
import com.abooc.dlna.media.MediaDao;
import com.abooc.dlna.media.MyHttpServer;
import com.abooc.dlna.media.UITimer;
import com.abooc.plugin.about.About;
import com.abooc.plugin.about.AboutActivity;
import com.abooc.upnp.model.CDevice;
import com.abooc.upnp.model.DeviceDisplay;
import com.abooc.util.Debug;
import com.abooc.widget.Toast;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.android.NetworkUtils;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.transport.Router;
import org.fourthline.cling.transport.RouterException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import demo.com.abooc.upnp.browser.utils.IPUtil;

public class UPnPDirectoryActivity extends AppCompatActivity {


    UPnPPresenter mUPnPPresenter;

    LibrariesFragment iLibrariesFragment;
    PlayerListFragment iPlayerListFragment;
    ContentListFragment iContentListFragment;

    private WifiReceiver iWifiReceiver = new WifiReceiver();
    private DeviceDisplay device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.init(this.getApplicationContext());
        About.defaultAbout(this);

        setContentView(R.layout.activity_upnp_directory);

        try {
            InetAddress localIpAddress = IPUtil.getLocalIpAddress(this);
            MyHttpServer.setLocalAddress(localIpAddress);

            // start http server
            new MyHttpServer(MyHttpServer.PORT);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            System.err.println("Couldn't start server:\n" + ioe);
            ioe.printStackTrace();
            System.exit(-1);
        }


        iLibrariesFragment = (LibrariesFragment) getSupportFragmentManager().findFragmentById(R.id.LibrariesFragment);
        iPlayerListFragment = (PlayerListFragment) getSupportFragmentManager().findFragmentById(R.id.PlayerListFragment);
        iContentListFragment = (ContentListFragment) getSupportFragmentManager().findFragmentById(R.id.ContentListFragment);

        Intent service = new Intent(getApplicationContext(), AndroidUpnpServiceImpl.class);
        bindService(service, iServiceConnection, android.app.Service.BIND_AUTO_CREATE);


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(iWifiReceiver, intentFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_about:
                AboutActivity.launch(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onRefresh(View view) {
        if (iUITimer.isRunning()) {
            iUITimer.cancel();
        } else {
            iUITimer.start();
        }
    }

    private UITimer iUITimer = new UITimer(3) {
        @Override
        public void onStart() {
            Debug.anchor();
            Toast.show("开始扫描...");

            AndroidUpnpService uPnPService = mUPnPPresenter.getUPnPService();
            uPnPService.getRegistry().removeAllRemoteDevices();

            uPnPService.getControlPoint().search();
        }

        @Override
        public void onCancel() {
            Toast.show("搜索被取消");
        }

        @Override
        public void onFinish() {
            Debug.anchor();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbindService(iServiceConnection);
        unregisterReceiver(iWifiReceiver);
    }

    private ServiceConnection iServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Debug.anchor(name);
            AndroidUpnpService upnpService = (AndroidUpnpService) service;
            mUPnPPresenter = new UPnPPresenter(upnpService);
            iContentListFragment.setPresenter(mUPnPPresenter);

            upnpService.getControlPoint().search();

            upnpService.getRegistry().addListener(iDefaultRegistryListener);

            MyHttpServer.startContentDirectory(upnpService);

            MediaDao.init(getApplicationContext(), MyHttpServer.getAddress());
            AppRootContainer.init();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    DefaultRegistryListener iDefaultRegistryListener = new DefaultRegistryListener() {

        @Override
        public void deviceAdded(Registry registry, Device device) {
            Service directoryService = device.findService(new UDAServiceType("ContentDirectory"));
            if (directoryService != null) {
                CDevice cDevice = new CDevice(device);
                DeviceDisplay d = new DeviceDisplay(cDevice);
                Message.obtain(iLibrariesFragment.getHandler(), HandlerWhat.ADD, d).sendToTarget();
            }

            Service aVTransportService = device.findService(new UDAServiceType("AVTransport"));
            if (aVTransportService != null) {
                CDevice cDevice = new CDevice(device);
                DeviceDisplay d = new DeviceDisplay(cDevice);
                Message.obtain(iPlayerListFragment.getHandler(), HandlerWhat.ADD, d).sendToTarget();
            }

        }

        @Override
        public void deviceRemoved(Registry registry, Device device) {
            CDevice cDevice = new CDevice(device);
            DeviceDisplay d = new DeviceDisplay(cDevice);
            Message.obtain(iLibrariesFragment.getHandler(), HandlerWhat.REMOVE, d).sendToTarget();
            Message.obtain(iPlayerListFragment.getHandler(), HandlerWhat.REMOVE, d).sendToTarget();
        }

    };


    public void setDevice(DeviceDisplay device) {
        this.device = device;
    }

    public DeviceDisplay getDevice() {
        return this.device;
    }

    private class WifiReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                boolean isWifi = NetworkUtils.isWifi(networkInfo);
                if (isWifi && networkInfo.isAvailable()) {
                    switch (networkInfo.getState()) {
                        case CONNECTED:

                            if (!isOn) {
                                isOn = true;

                                if (mUPnPPresenter != null) {
                                    Router router = mUPnPPresenter.getUPnPService().get().getRouter();
                                    turnOnRouter(router);
                                }


                                try {
                                    InetAddress localIpAddress = IPUtil.getLocalIpAddress(UPnPDirectoryActivity.this);
                                    MyHttpServer.setLocalAddress(localIpAddress);
                                } catch (UnknownHostException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                        case DISCONNECTED:
                            if (isOn) {
                                isOn = false;

                                if (mUPnPPresenter != null) {
                                    Router router = mUPnPPresenter.getUPnPService().get().getRouter();
                                    turnOffRouter(router);
                                }
                                iPlayerListFragment.clear();
                                iContentListFragment.clear();
                            }
                            break;
                    }
                }

            }
        }

        boolean isWifiEnable(WifiInfo wifiInfo) {
            return wifiInfo.getNetworkId() > 0
                    && wifiInfo.getLinkSpeed() > 0;
        }
    }

    private boolean isOn = true;


    private void turnOnRouter(final Router router) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Debug.anchor("enable");
                    router.enable();
                } catch (RouterException e) {
                    Debug.error(e.getMessage());
                }
            }
        }).start();
    }

    private void turnOffRouter(final Router router) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Debug.anchor("disable");
                    router.disable();
                } catch (RouterException e) {
                    Debug.error(e.getMessage());
                }
            }
        }).start();
    }

}
