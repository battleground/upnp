package com.abooc.upnp;

import java.util.ArrayList;

/**
 * Created by author:李瑞宇
 * email:allnet@live.cn
 * on 16/8/10.
 */
public class DeviceListCache {

    private static DeviceListCache ourInstance = new DeviceListCache();

    public static DeviceListCache getInstance() {
        return ourInstance;
    }

    private DeviceListCache() {
    }

    private ArrayList<DeviceDisplay> mList = new ArrayList<>();

    public ArrayList<DeviceDisplay> getList() {
        return mList;
    }


    public boolean hasChecked() {
        for (DeviceDisplay deviceDisplay : mList) {
            if (deviceDisplay.isChecked())
                return true;
        }
        return false;
    }

    public DeviceDisplay getCheckedDevice() {
        for (DeviceDisplay deviceDisplay : mList) {
            if (deviceDisplay.isChecked())
                return deviceDisplay;
        }
        return null;
    }

    public boolean clearChecked() {
        for (DeviceDisplay deviceDisplay : mList) {
            if (deviceDisplay.isChecked()) {
                deviceDisplay.setChecked(false);
                return true;
            }
        }
        return false;
    }

}
