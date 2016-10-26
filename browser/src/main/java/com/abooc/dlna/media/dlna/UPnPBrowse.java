package com.abooc.dlna.media.dlna;

import android.os.Handler;
import android.os.Message;

import com.abooc.util.Debug;
import demo.com.abooc.upnp.browser.HandlerWhat;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.model.action.ActionException;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.support.contentdirectory.callback.Browse;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.item.Item;

import java.net.URI;

/**
 * Created by author:李瑞宇
 * email:allnet@live.cn
 * on 16/8/28.
 */
public class UPnPBrowse extends UPnPExecute {


    static UPnPBrowse mOur;

    Handler mContentHandler;
    Service mService;

    public UPnPBrowse(AndroidUpnpService upnpService) {
        super(upnpService);
        mOur = this;
    }

    public static UPnPBrowse get() {
        return mOur;
    }


    public void build(Service service) {
        mService = service;
    }

    public Service getService() {
        return mService;
    }

    public void setUIHandler(Handler handler) {
        mContentHandler = handler;
    }

    public void browse(Service service, String containerId, BrowseFlag flag) {
        execute(new Browse(service, containerId, flag) {

            private String getImportUri(DIDLObject didlObject) {
                Res firstResource = didlObject.getFirstResource();
                if (firstResource != null) {
                    URI importUri = firstResource.getImportUri();
                    return (importUri == null ? "" : importUri.toString());
                }

                return null;
            }

            @Override
            public void received(ActionInvocation actionInvocation, DIDLContent didlContent) {
                try {
                    mContentHandler.sendEmptyMessage(HandlerWhat.CLEAR_ALL);
                    StringBuffer buffer = new StringBuffer();
                    for (Container container : didlContent.getContainers()) {
                        buffer.append("container:" + container.getTitle() + "\n");
                        Message.obtain(mContentHandler, HandlerWhat.ADD, container).sendToTarget();
                    }
                    Debug.anchor(buffer.toString());

                    if (buffer.length() > 0) {
                        buffer.delete(0, buffer.length() - 1);
                    }
                    for (Item item : didlContent.getItems()) {
                        buffer.append(item.getTitle() + ", " + item.getFirstResource().getValue() + "\n");
                        Message.obtain(mContentHandler, HandlerWhat.ADD, item).sendToTarget();
                    }
                    Debug.anchor(buffer.toString());
                } catch (Exception e) {
                    Debug.e("Creating DIDL tree nodes failed: " + e);
                    actionInvocation.setFailure(new ActionException(
                            ErrorCode.ACTION_FAILED,
                            "Can't create list childs: " + e, e));
                    failure(actionInvocation, null);
                }
            }

            @Override
            public void updateStatus(Status status) {
                Debug.anchor(status.getDefaultMessage());

            }

            @Override
            public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String s) {

                Debug.anchor(s);
            }
        });
    }

}
