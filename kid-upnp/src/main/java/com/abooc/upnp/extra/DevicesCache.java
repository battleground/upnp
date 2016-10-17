package com.abooc.upnp.extra;

import com.abooc.upnp.model.DeviceDisplay;

import java.util.ArrayList;

/**
 * Created by author:李瑞宇
 * email:allnet@live.cn
 * on 16/8/10.
 */
public class DevicesCache {

    private static DevicesCache ourInstance = new DevicesCache();

    public static DevicesCache getInstance() {
        return ourInstance;
    }

    private DevicesCache() {
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
