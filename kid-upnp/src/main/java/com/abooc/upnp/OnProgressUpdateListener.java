package com.abooc.upnp;

import org.fourthline.cling.support.model.PositionInfo;

/**
 * Created by author:李瑞宇
 * email:allnet@live.cn
 * on 16/7/13.
 */
public interface OnProgressUpdateListener {
    void onChanged(PositionInfo positionInfo);
}
