package demo.com.abooc.upnp.browser;

import com.abooc.dlna.media.dlna.UPnPAVTransport;
import com.abooc.dlna.media.dlna.UPnPBrowse;

import org.fourthline.cling.android.AndroidUpnpService;

/**
 * Created by dayu on 2017/5/15.
 */

public class UPnPPresenter {

    AndroidUpnpService mUPnPService;
    UPnPAVTransport mUPnPAVTransport;
    UPnPBrowse mUPnPBrowse;

    public UPnPPresenter(AndroidUpnpService upnpService) {
        mUPnPService = upnpService;


        mUPnPAVTransport = new UPnPAVTransport(upnpService);
        mUPnPBrowse = new UPnPBrowse(upnpService);

    }

    public AndroidUpnpService getUPnPService() {
        return mUPnPService;
    }

}
