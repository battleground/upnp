package com.abooc.upnp;

import com.abooc.upnp.extra.OnActionListener;
import com.abooc.util.Debug;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.support.avtransport.callback.GetPositionInfo;
import org.fourthline.cling.support.renderingcontrol.callback.GetMute;
import org.fourthline.cling.support.renderingcontrol.callback.SetMute;
import org.fourthline.cling.support.renderingcontrol.callback.SetVolume;

/**
 * Created by author:李瑞宇
 * email:allnet@live.cn
 * on 16/7/21.
 */
public class Renderer {

    private PlayerInfo mPlayerInfo;
    private ControlPoint mControlPoint;

    private OnActionListener iOnActionListener;

    private static Renderer mOur = new Renderer();

    private Device mDevice;


    private Renderer() {
        Debug.debugClass();
    }

    protected static Renderer build(ControlPoint controlPoint, Device device) {
        mOur.init(controlPoint, device);
        return mOur;
    }

    private void init(ControlPoint controlPoint, Device device) {
        mPlayerInfo = new PlayerInfo();
        mControlPoint = controlPoint;
        mDevice = device;
    }

    public static Renderer get() {
        return mOur;
    }

    public Service getAVTransportService() {
        return mDevice.findService(new UDAServiceType("AVTransport"));
    }

    public Service getRenderingControlService() {
        UDAServiceType renderingControl = new UDAServiceType("RenderingControl");
        return mDevice.findService(renderingControl);
    }

    public boolean isRenderingControl() {
        return getRenderingControlService() != null;
    }

    public boolean isAVTransport() {
        return getAVTransportService() != null;
    }

    public boolean isMediaRenderer() {
        UDADeviceType deviceType = new UDADeviceType("MediaRenderer");
        Device[] devices = mDevice.findDevices(deviceType);
        return devices != null;
    }

    public PlayerInfo getPlayerInfo() {
        return mPlayerInfo;
    }

    public void execute(ActionCallback actionCallback) {
        if (!(actionCallback instanceof GetPositionInfo)) {
            String boundIp = DlnaManager.getInstance().getBoundIp();
            Debug.anchor(boundIp + ":" + actionCallback.getActionInvocation().getAction());
        }
        isSending = true;
        if (iOnActionListener != null) {
            iOnActionListener.onSend();
        }
        mControlPoint.execute(actionCallback);
    }


    private boolean isSending;

    public void setSending(boolean sending) {
        isSending = sending;
    }

    public boolean isSending() {
        return isSending;
    }


    public void setVolume(final long volume) {
        if (!isRenderingControl()) return;
        Debug.anchor(volume);
        execute(new SetVolume(getRenderingControlService(), volume) {
            @Override
            public void success(ActionInvocation invocation) {
                mPlayerInfo.updateVolume(volume);

                isSending = false;
                if (iOnActionListener != null) {
                    iOnActionListener.onSendFinish(true);
                }
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                if (iOnActionListener != null) {
                    iOnActionListener.onSendFinish(false);
                }
            }
        });
    }

    public void getMute() {
        if (!isRenderingControl()) return;
        execute(new GetMute(getRenderingControlService()) {
            @Override
            public void received(ActionInvocation arg0, boolean arg1) {
                mPlayerInfo.updateMute(arg1);

                isSending = false;
                if (iOnActionListener != null) {
                    iOnActionListener.onSendFinish(true);
                }
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                if (iOnActionListener != null) {
                    iOnActionListener.onSendFinish(false);
                }
            }
        });
    }

    public void setMute(final boolean desiredMute) {
        if (!isRenderingControl()) return;
        Debug.anchor(desiredMute);
        execute(new SetMute(getRenderingControlService(), desiredMute) {
            @Override
            public void success(ActionInvocation invocation) {
                mPlayerInfo.updateMute(desiredMute);
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
            }
        });
    }


    public static void debug(RemoteDevice device) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(device.getDetails().getFriendlyName()).append("\n");
        buffer.append("services:").append("\n");
        RemoteService[] services = device.getServices();
//        Service[] services = device.findServices();
        for (Service service : services) {
            String type = service.getServiceType().getType();
            buffer.append(service.getClass().getName() + ", " + type).append("\n");
        }
        buffer.append("\n");

        UDAServiceType renderingControl = new UDAServiceType("RenderingControl");
        UDADeviceType deviceType = new UDADeviceType("MediaRenderer");

        buffer.append("devices:").append("\n");
        Device[] devices = device.findDevices(renderingControl);
        for (Device d : devices) {
            buffer.append(d.getClass().getName() + ", " + d.getType()).append("\n");
        }
        Debug.anchor(buffer.toString());

    }

}
