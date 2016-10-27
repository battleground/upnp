package com.abooc.upnp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import com.abooc.upnp.model.CDevice;
import com.abooc.upnp.model.DeviceDisplay;
import com.abooc.util.Debug;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.android.NetworkUtils;
import org.fourthline.cling.model.message.header.ServiceTypeHeader;
import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.RemoteDeviceIdentity;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by author:李瑞宇
 * email:allnet@live.cn
 * on 16/7/14.
 */
public class Discovery extends DefaultRegistryListener {

    protected AndroidUpnpService mUPnPService;

    private UPnPServiceConnectionReceiver mUPnPServiceReceiver;
    private WiFi mWiFi = new WiFi();

    private static Discovery mOur = new Discovery();

    private Discovery() {
        mUPnPServiceReceiver = new UPnPServiceConnectionReceiver();
    }

    public static Discovery get() {
        return mOur;
    }

    public AndroidUpnpService getUpnpService() {
        return mUPnPService;
    }

    private class UPnPServiceConnectionReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            mUPnPService = DlnaManager.getInstance().getUpnpService();
            switch (intent.getAction()) {
                case DlnaManager.ACTION_DLNA_CONNECTION_CONNECTED:
                    mUPnPService.getRegistry().addListener(Discovery.this);

                    search();
                    break;
                case DlnaManager.ACTION_DLNA_CONNECTION_DISCONNECTED:
                    mUPnPService.getRegistry().removeListener(Discovery.this);
                    mUPnPService.getRegistry().removeAllRemoteDevices();
                    break;
            }
        }
    }

    public void init(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DlnaManager.ACTION_DLNA_CONNECTION_CONNECTED);
        intentFilter.addAction(DlnaManager.ACTION_DLNA_CONNECTION_DISCONNECTED);
        Context applicationContext = context.getApplicationContext();
        applicationContext.registerReceiver(mUPnPServiceReceiver, intentFilter);

        IntentFilter wifiFilter = new IntentFilter();
        wifiFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        applicationContext.registerReceiver(mWiFi, wifiFilter);
    }

    public void exit(Context context) {
        try {
            Context applicationContext = context.getApplicationContext();
            applicationContext.unregisterReceiver(mUPnPServiceReceiver);
            applicationContext.unregisterReceiver(mWiFi);
        } catch (Exception e) {

        }
    }

    public void removeAll() {
        if (mUPnPService != null) {
            if (DlnaManager.getInstance().hasBound()) {
                DeviceIdentity boundIdentity = DlnaManager.getInstance().getBoundIdentity();
                Collection<RemoteDevice> devices = mUPnPService.getRegistry().getRemoteDevices();
                for (RemoteDevice device : devices) {
                    if (!device.getIdentity().equals(boundIdentity)) {
                        mUPnPService.getRegistry().removeDevice(device);
                    }
                }
            } else {
                mUPnPService.getRegistry().removeAllRemoteDevices();
            }
        }
    }

    public ArrayList<DeviceDisplay> getList() {
        if (mUPnPService != null) {
            Collection<RemoteDevice> devices = mUPnPService.getRegistry().getRemoteDevices();
            ArrayList<DeviceDisplay> list = new ArrayList<>();
            for (RemoteDevice device : devices) {
                DeviceDisplay display = new DeviceDisplay(new CDevice(device));
                DeviceIdentity boundIdentity = DlnaManager.getInstance().getBoundIdentity();
                if (device.getIdentity().equals(boundIdentity)) {
                    display.setChecked(true);
                }
                RemoteDeviceIdentity identity = device.getIdentity();
                String host = identity.getDescriptorURL().getHost();
                display.setHost(host);
                list.add(display);
            }
            return list;
        }
        return null;
    }

    public void search() {
        if (mUPnPService != null) {
            Collection<RemoteDevice> devices = mUPnPService.getRegistry().getRemoteDevices();
            for (RemoteDevice device : devices) {
                remoteDeviceAdded(mUPnPService.getRegistry(), device);
            }

            mUPnPService.getControlPoint().search(new ServiceTypeHeader(iUDAServiceType));
        }
    }

    private UDAServiceType iUDAServiceType = new UDAServiceType("AVTransport");

    public void addDefaultRegistryListener(DefaultRegistryListener listener) {
        mDefaultRegistryListener = listener;
    }

    private DefaultRegistryListener mDefaultRegistryListener;

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

    private boolean isOn = true;

    private class WiFi extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Debug.anchor(intent.toString());
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                boolean isWifi = NetworkUtils.isWifi(networkInfo);
                if (isWifi && networkInfo.isAvailable()) {
                    switch (networkInfo.getState()) {
                        case CONNECTED:
                            if (!isOn) {
                                isOn = true;
                                DlnaManager.turnOnRouter();
                            }
                            break;
                        case DISCONNECTED:
                            if (isOn) {
                                isOn = false;
                                if (mUPnPService != null) {
                                    mUPnPService.getRegistry().removeAllRemoteDevices();
                                }
                                DlnaManager.turnOffRouter();
                            }
                            break;
                    }
                }
            }
        }
    }

}
