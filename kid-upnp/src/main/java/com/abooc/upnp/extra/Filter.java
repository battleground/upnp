package com.abooc.upnp.extra;

import org.fourthline.cling.model.meta.Device;

/**
 * Created by author:李瑞宇
 * email:allnet@live.cn
 * on 16/7/13.
 */
public interface Filter {

    boolean check(Device device);

}
