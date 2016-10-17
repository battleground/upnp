package com.abooc.upnp.extra;

import org.fourthline.cling.support.model.MediaInfo;

/**
 * 取得远端在播媒体信息回调
 */
public interface OnGotMediaInfoCallback {
    void onGotMediaInfo(MediaInfo mediaInfo);
}