package com.abooc.upnp;

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
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.state.StateVariableValue;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportLastChangeParser;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.support.model.TransportState;

import java.util.Map;

/**
 * 负责绑定远端设备，订阅远端播放状态事件通知
 * <p>
 * Created by author:李瑞宇
 * email:allnet@live.cn
 * on 16/10/13.
 */

public class RendererBuilder {

    private Device mBoundDevice;
    private DeviceIdentity mIdentity;
    private String mBindSubscriptionId;
    private AndroidUpnpService mUpnpService;

    private static RendererBuilder mOur = new RendererBuilder();

    private RendererBuilder() {

    }

    public static RendererBuilder get() {
        return mOur;
    }

    public void bind(AndroidUpnpService upnpService, Device device, SimpleSubscriptionCallback callback) {
        mUpnpService = upnpService;
        mBoundDevice = device;
        mIdentity = device.getIdentity();
        mSimpleSubscriptionCallback = callback;

        SubscriptionCallback(upnpService);
    }

    private boolean hasBound;

    public boolean hasBound() {
        return hasBound;
    }

    public Device getBoundDevice() {
        return mBoundDevice;
    }

    public boolean isBound(Device device) {
        return device.getIdentity().equals(mIdentity);
    }

    public void unbind(AndroidUpnpService upnpService, Device device) {
        if (device.getIdentity().equals(mIdentity)) {
            mIdentity = null;
            RemoteGENASubscription remoteSubscription = upnpService.getRegistry().getRemoteSubscription(mBindSubscriptionId);
            if (remoteSubscription != null) {
                upnpService.getRegistry().removeRemoteSubscription(remoteSubscription);
            }
        }
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

    private void SubscriptionCallback(AndroidUpnpService upnpService) {
        UDAServiceType serviceType = new UDAServiceType("AVTransport");
        Service avTransport = mBoundDevice.findService(serviceType);

        SubscriptionCallback subscription = new SubscriptionCallback(avTransport) {

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
                Toast.show("远端没有响应");
                hasBound = false;
                if (mSimpleSubscriptionCallback != null) {
                    mSimpleSubscriptionCallback.failed(subscription, response, exception, defaultMsg);
                }
            }

            @Override
            public void ended(GENASubscription sub, CancelReason reason, UpnpResponse response) {
                // Reason should be null, or it didn't end regularly
                Debug.anchor();
                Toast.show("订阅结束");
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

        upnpService.getControlPoint().execute(subscription);
    }
}
