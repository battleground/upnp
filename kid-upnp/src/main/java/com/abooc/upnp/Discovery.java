package com.abooc.upnp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;

import com.abooc.util.Debug;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.android.NetworkUtils;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.transport.Router;
import org.fourthline.cling.transport.RouterException;

import java.util.Collection;

/**
 * Created by author:李瑞宇
 * email:allnet@live.cn
 * on 16/7/14.
 */
public class Discovery {

    protected AndroidUpnpService mUPnPService;

    protected ServiceConnection UPnPServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mUPnPService = (AndroidUpnpService) service;
            mUPnPService.getRegistry().addListener(registryListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            mUPnPService.getRegistry().removeAllRemoteDevices();
            mUPnPService.getRegistry().removeListener(registryListener);
            mUPnPService = null;
        }

    };

    private static Discovery mOur = new Discovery();

    private WifiReceiver iWifiReceiver;
    private Context mContext;

    private Discovery() {
        iWifiReceiver = new WifiReceiver();
    }

    public static Discovery get() {
        return mOur;
    }

    public void bindServer(Context context) {
        mContext = context;
        context.bindService(new Intent(context, AppAndroidUPnPService.class), UPnPServiceConnection, Context.BIND_AUTO_CREATE);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        context.registerReceiver(iWifiReceiver, intentFilter);
    }

    public void unbindServer(Context context) {
        try {
            context.unbindService(UPnPServiceConnection);
            mContext.unregisterReceiver(iWifiReceiver);
        } catch (Exception e) {

        }
    }

    public AndroidUpnpService getUpnpService() {
        return mUPnPService;
    }

    public void removeAll() {
        mUPnPService.getRegistry().removeAllRemoteDevices();
    }

    public void search() {
        Collection<RemoteDevice> devices = mUPnPService.getRegistry().getRemoteDevices();
        for (RemoteDevice device : devices) {
            registryListener.remoteDeviceAdded(mUPnPService.getRegistry(), device);
        }
        mUPnPService.getControlPoint().search();
    }

    public void addDefaultRegistryListener(DefaultRegistryListener listener) {
        mDefaultRegistryListener = listener;
    }

    private DefaultRegistryListener mDefaultRegistryListener = new DefaultRegistryListener();

    private DefaultRegistryListener registryListener = new DefaultRegistryListener() {

        @Override
        public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
            super.remoteDeviceAdded(registry, device);
            if (mDefaultRegistryListener != null) {
                mDefaultRegistryListener.remoteDeviceAdded(registry, device);
            }
        }

        @Override
        public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
            super.remoteDeviceRemoved(registry, device);
            if (mDefaultRegistryListener != null) {
                mDefaultRegistryListener.remoteDeviceRemoved(registry, device);
            }
        }
    };

    private class WifiReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                boolean isWifi = NetworkUtils.isWifi(networkInfo);
                if (isWifi && networkInfo.isAvailable() && mUPnPService != null) {
                    switch (networkInfo.getState()) {
                        case CONNECTED:
                            turnOnRouter();
                            break;
                        case DISCONNECTED:
                            removeAll();
                            turnOffRouter();
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


    private void turnOnRouter() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Router router = mUPnPService.get().getRouter();
                    Debug.anchor("enable");
                    router.enable();
                } catch (RouterException e) {
                    Debug.e(e.getMessage());
                }
            }
        }).start();
    }

    private void turnOffRouter() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Router router = mUPnPService.get().getRouter();
                    Debug.anchor("disable");
                    router.disable();
                } catch (RouterException e) {
                    Debug.e(e.getMessage());
                }
            }
        }).start();
    }

}
