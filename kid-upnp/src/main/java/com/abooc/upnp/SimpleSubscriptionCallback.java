package com.abooc.upnp;

import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.model.gena.CancelReason;
import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.model.TransportState;

public class SimpleSubscriptionCallback extends SubscriptionCallback {

    protected SimpleSubscriptionCallback(Service service) {
        super(service);
    }

    protected SimpleSubscriptionCallback(Service service, int requestedDurationSeconds) {
        super(service, requestedDurationSeconds);
    }

    public void onEventReceived(TransportState state) {

    }

    @Override
    public void eventReceived(GENASubscription sub) {
    }

    @Override
    public void failed(GENASubscription subscription, UpnpResponse responseStatus, Exception exception, String defaultMsg) {

    }

    @Override
    public void established(GENASubscription subscription) {

    }

    @Override
    public void ended(GENASubscription subscription, CancelReason reason, UpnpResponse responseStatus) {

    }

    @Override
    public void eventsMissed(GENASubscription subscription, int numberOfMissedEvents) {

    }

}
