package com.abooc.dlna.media.dlna;

import com.abooc.util.Debug;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.avtransport.callback.Play;
import org.fourthline.cling.support.avtransport.callback.SetAVTransportURI;

public class UPnPAVTransport extends UPnPExecute {

    public UPnPAVTransport(AndroidUpnpService upnpService) {
        super(upnpService);
    }

    public void start(final Service service, String uri, String metadata) {

        Debug.anchor(uri + "\n" + metadata);



        execute(new SetAVTransportURI(service, uri, metadata) {
            @Override
            public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String s) {

            }

            @Override
            public void success(ActionInvocation invocation) {
                play(service);
            }
        });
    }

    public void play(Service service) {
        execute(new Play(service) {
            @Override
            public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String s) {

            }

            @Override
            public void success(ActionInvocation invocation) {

            }
        });
    }


}