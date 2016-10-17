package com.abooc.upnp.extra;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.types.UDAServiceType;

import java.util.concurrent.Callable;

/**
 * Created by author:李瑞宇
 * email:allnet@live.cn
 * on 16/7/13.
 */
public class Filter implements Callable<Boolean> {

    private Device mDevice;

    public Filter build(Device device) {
        mDevice = device;
        return this;
    }

    @Override
    public Boolean call() {
//        return (mDevice.findService(new UDAServiceType("RenderingControl")) != null);
        return (mDevice.findService(new UDAServiceType("AVTransport")) != null);
    }
}
