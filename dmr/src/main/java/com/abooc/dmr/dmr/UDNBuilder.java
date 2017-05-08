package com.abooc.dmr.dmr;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

/**
 * Created by dayu on 2017/5/5.
 */

public class UDNBuilder {

    public static final String UDN_STRING = "aa-bb-cc-dd-ee-ff";

    public static String getMac(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }


    public static String getUdnString(String brand) {
        String udnString = brand + Build.SERIAL;
        return udnString.toUpperCase();
    }

}
