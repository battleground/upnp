package com.abooc.dlna.media.dlna;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.controlpoint.ActionCallback;

/**
 * Created by author:李瑞宇
 * email:allnet@live.cn
 * on 16/8/28.
 */
public class UPnPExecute {

    private AndroidUpnpService mUPnPService;

    public UPnPExecute(AndroidUpnpService upnpService) {
        mUPnPService = upnpService;
    }

    protected void execute(ActionCallback actionCallback) {
        mUPnPService.getControlPoint().execute(actionCallback);
    }

}
