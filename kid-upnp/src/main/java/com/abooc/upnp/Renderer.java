package com.abooc.upnp;

import com.abooc.upnp.extra.OnActionListener;
import com.abooc.upnp.model.CDevice;
import com.abooc.util.Debug;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.support.renderingcontrol.callback.GetMute;
import org.fourthline.cling.support.renderingcontrol.callback.GetVolume;
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

    private static Renderer mOur;
    private Device mDevice;


    private Renderer(ControlPoint controlPoint, CDevice device) {
        Debug.debug();
        mPlayerInfo = new PlayerInfo();
        mControlPoint = controlPoint;
        mDevice = device.getDevice();
    }

    public static Renderer build(ControlPoint controlPoint, CDevice device) {
        return mOur = new Renderer(controlPoint, device);
    }

    public static Renderer get() {
        return mOur;
    }

    public Service getAVTransportService() {
        return mDevice.findService(new UDAServiceType("AVTransport"));
    }

    public Service getRenderingControlService() {
        return mDevice.findService(new UDAServiceType("RenderingControl"));
    }

    public boolean isRenderingControl() {
        return getRenderingControlService() != null;
    }

    public boolean isAVTransport() {
        return getAVTransportService() != null;
    }

    public PlayerInfo getPlayerInfo() {
        return mPlayerInfo;
    }

    public void execute(ActionCallback actionCallback) {
//        if (!(actionCallback instanceof GetPositionInfo)) {
//            Debug.anchor(actionCallback.getActionInvocation().getAction());
//        }
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


    public void volume(final long volume) {
        if (!isRenderingControl()) return;
        Debug.anchor(volume);
        execute(new SetVolume(getRenderingControlService(), volume) {
            @Override
            public void success(ActionInvocation invocation) {
                mPlayerInfo.setVolume(volume);

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

    public void getVolume() {
        if (!isRenderingControl()) return;
        execute(new GetVolume(getRenderingControlService()) {
            @Override
            public void received(ActionInvocation invocation, int volume) {
                Debug.anchor(volume);
                mPlayerInfo.setVolume(volume);

                isSending = false;
                if (iOnActionListener != null) {
                    iOnActionListener.onSendFinish(true);
                }
            }

            @Override
            public void failure(ActionInvocation arg0, UpnpResponse arg1, String arg2) {
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
                mPlayerInfo.setMute(arg1);

                isSending = false;
                if (iOnActionListener != null) {
                    iOnActionListener.onSendFinish(true);
                }
            }

            @Override
            public void failure(ActionInvocation arg0, UpnpResponse arg1, String arg2) {
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
                mPlayerInfo.setMute(desiredMute);
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
            }
        });
    }


}
