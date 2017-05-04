package com.abooc.upnp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.abooc.util.Debug;
import com.abooc.widget.Toast;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.model.gena.CancelReason;
import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.gena.RemoteGENASubscription;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.RemoteDeviceIdentity;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.state.StateVariableValue;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportLastChangeParser;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.transport.Router;
import org.fourthline.cling.transport.RouterException;

import java.util.Map;

/**
 * Created by author:李瑞宇
 * email:allnet@live.cn
 * on 16/9/1.
 */
public class DlnaManager implements ServiceConnection {

    @Override
    public void onServiceConnected(ComponentName className, IBinder service) {
        mUpnpService = (AndroidUpnpService) service;
        Discovery.get().setUPnPService(mUpnpService);
        Discovery.startListener(mUpnpService);
    }

    @Override
    public void onServiceDisconnected(ComponentName className) {
        Discovery.stopListener(mUpnpService);
        mUpnpService = null;
    }

    private AndroidUpnpService mUpnpService;
    private Device mBoundDevice;
    private String mBindSubscriptionId;
    private String mHost;
    private DeviceIdentity iDeviceIdentity;

    private boolean hasBound;


    private static DlnaManager ourInstance = new DlnaManager();

    public static DlnaManager getInstance() {
        return ourInstance;
    }

    private DlnaManager() {
    }

    private Context mContext;

    public void startService(Context context, Class<? extends android.app.Service> serviceClass) {
        mContext = context.getApplicationContext();
        Intent intent = new Intent(mContext, serviceClass);
        mContext.bindService(intent, this, android.app.Service.BIND_AUTO_CREATE);
    }

    public void stop() {
        unbound();

        try {
            mContext.unbindService(this);
        } catch (Exception e) {

        }
    }

    public void shutdown() {
        if (isOk())
            mUpnpService.getRegistry().shutdown();
    }

    public boolean isOk() {
        return mUpnpService != null;
    }

    public AndroidUpnpService getUpnpService() {
        return mUpnpService;
    }

    public Router getBindRouter() {
        if (isOk())
            return mUpnpService.get().getRouter();
        return null;
    }

    public boolean bind(Device device, SimpleSubscriptionCallback callback) {
        if (isOk()) {
            Renderer renderer = Renderer.build(mUpnpService.getControlPoint(), device);
            RendererPlayer.build(renderer);

            if (renderer.isRenderingControl()
                    && renderer.isAVTransport()) {
                Service avTransportService = renderer.getAVTransportService();
                SubscriptionCallback subscriptionEvent = createSubscriptionEvent(avTransportService);
                mUpnpService.getControlPoint().execute(subscriptionEvent);

                mBoundDevice = device;
                iDeviceIdentity = device.getIdentity();
                mHost = ((RemoteDeviceIdentity) iDeviceIdentity).getDescriptorURL().getHost();
                mSimpleSubscriptionCallback = callback;
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public void unbound() {
        if (mBoundDevice != null
                && mBoundDevice.getIdentity().equals(iDeviceIdentity)) {
            mBoundDevice = null;
            iDeviceIdentity = null;
            mBindSubscriptionId = null;
            mHost = null;
            hasBound = false;

            RemoteGENASubscription remoteSubscription = mUpnpService.getRegistry().getRemoteSubscription(mBindSubscriptionId);
            if (remoteSubscription != null) {
                mUpnpService.getRegistry().removeRemoteSubscription(remoteSubscription);
            }
        }
    }

    public String getBoundIp() {
        return mHost;
    }

    public DeviceIdentity getBoundIdentity() {
        return iDeviceIdentity;
    }

    public boolean hasBound() {
        return hasBound;
    }

    public Device getBoundDevice() {
        return mBoundDevice;
    }

    public boolean isBound(Device device) {
        return device.getIdentity().equals(iDeviceIdentity);
    }

    private SimpleSubscriptionCallback mSimpleSubscriptionCallback;

    private SubscriptionCallback createSubscriptionEvent(Service avTransport) {
        return new SubscriptionCallback(avTransport) {

            @Override
            public void established(GENASubscription sub) {
                hasBound = true;
                mBindSubscriptionId = sub.getSubscriptionId();
                Debug.anchor("Established: " + mBindSubscriptionId);

                if (mSimpleSubscriptionCallback != null) {
                    mSimpleSubscriptionCallback.established(sub);
                }
            }

            @Override
            protected void failed(GENASubscription subscription, UpnpResponse response, Exception exception, String defaultMsg) {
                Debug.error(createDefaultFailureMessage(response, exception));
                hasBound = false;
                if (mSimpleSubscriptionCallback != null) {
                    mSimpleSubscriptionCallback.failed(subscription, response, exception, defaultMsg);
                }
            }

            @Override
            public void ended(GENASubscription sub, CancelReason reason, UpnpResponse response) {
                // Reason should be null, or it didn't end regularly
                Debug.anchor();
                hasBound = false;
                if (mSimpleSubscriptionCallback != null) {
                    mSimpleSubscriptionCallback.ended(sub, reason, response);
                }
            }

            @Override
            public void eventReceived(GENASubscription sub) {
                if (mSimpleSubscriptionCallback != null) {
                    mSimpleSubscriptionCallback.eventReceived(sub);
                }
                Map<String, StateVariableValue> values = sub.getCurrentValues();

                StateVariableValue variableValue = values.get("LastChange");

                try {
                    LastChange lastChange = new LastChange(new AVTransportLastChangeParser(), variableValue.toString());

                    final AVTransportVariable.TransportState state = lastChange
                            .getEventedValue(0, AVTransportVariable.TransportState.class);

                    if (state != null) {
                        Debug.anchor(state);
                        if (mSimpleSubscriptionCallback != null) {
                            mSimpleSubscriptionCallback.onEventReceived(state.getValue());
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
    }


    public static void turnOnRouter() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Router router = DlnaManager.getInstance().getBindRouter();
                    if (router != null) {
                        Debug.anchor("enable");
                        router.enable();
                    }
                } catch (RouterException e) {
                    Debug.error(e.getMessage());
                }
            }
        }).start();
    }

    public static void turnOffRouter() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Router router = DlnaManager.getInstance().getBindRouter();
                    if (router != null) {
                        Debug.anchor("disable");
                        router.disable();
                    }
                } catch (RouterException e) {
                    Debug.error(e.getMessage());
                }
            }
        }).start();
    }

    public static void asyncSwitchRouter() {
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
                    Debug.error(e.getMessage());
                }
            }
        }).start();
    }

}
