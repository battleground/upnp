package com.abooc.upnp;//package com.baofeng.fengmi.dlna;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.abooc.util.Debug;

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
import org.fourthline.cling.support.model.TransportState;
import org.fourthline.cling.transport.Router;

import java.util.Map;

/**
 * Created by author:李瑞宇
 * email:allnet@live.cn
 * on 16/9/1.
 */
public class DlnaManager {

    public static final String ACTION_DLNA_CONNECTION_CONNECTED = "action.dlna.connection.connected";
    public static final String ACTION_DLNA_CONNECTION_DISCONNECTED = "action.dlna.connection.disconnected";

    protected ServiceConnection UPnPServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mUpnpService = (AndroidUpnpService) service;

            Intent intent = new Intent(ACTION_DLNA_CONNECTION_CONNECTED);
            mContext.sendBroadcast(intent);
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            mUpnpService = null;
            Intent intent = new Intent(ACTION_DLNA_CONNECTION_DISCONNECTED);
            mContext.sendBroadcast(intent);
        }

    };

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
        Debug.debug();
    }

    private Context mContext;

    public void startService(Context context) {
        mContext = context.getApplicationContext();
        Intent intent = new Intent(mContext, AppAndroidUPnPService.class);
        mContext.bindService(intent, UPnPServiceConnection, android.app.Service.BIND_AUTO_CREATE);
    }

    public void stop() {
        mContext.unbindService(UPnPServiceConnection);
        unbound();
    }

    public boolean isOk() {
        return mUpnpService != null;
    }

    public AndroidUpnpService getUpnpService() {
        return mUpnpService;
    }

    public Router getBindRouter() {
        if (isOk()) {
            return mUpnpService.get().getRouter();
        }
        return null;
    }

    public boolean bind(Device device, SimpleSubscriptionCallback callback) {
        if (isOk()) {

            mBoundDevice = device;
            iDeviceIdentity = device.getIdentity();
            mHost = ((RemoteDeviceIdentity) iDeviceIdentity).getDescriptorURL().getHost();
            mSimpleSubscriptionCallback = callback;

            Renderer build = Renderer.build(mUpnpService.getControlPoint(), device);
            RendererPlayer.build(build);
            Service avTransportService = build.getAVTransportService();
            SubscriptionCallback subscriptionEvent = createSubscriptionEvent(avTransportService);
            mUpnpService.getControlPoint().execute(subscriptionEvent);
            return true;
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

    abstract class SimpleSubscriptionCallback extends SubscriptionCallback {

        protected SimpleSubscriptionCallback(Service service) {
            super(service);
        }

        protected SimpleSubscriptionCallback(Service service, int requestedDurationSeconds) {
            super(service, requestedDurationSeconds);
        }

        public abstract void onEventReceived(TransportState state);

        @Override
        public void eventReceived(GENASubscription sub) {
        }

        @Override
        public abstract void failed(GENASubscription subscription, UpnpResponse responseStatus, Exception exception, String defaultMsg);

        @Override
        public abstract void established(GENASubscription subscription);

        @Override
        public abstract void ended(GENASubscription subscription, CancelReason reason, UpnpResponse responseStatus);

        @Override
        public abstract void eventsMissed(GENASubscription subscription, int numberOfMissedEvents);

    }

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
                Debug.e(createDefaultFailureMessage(response, exception));
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

}
