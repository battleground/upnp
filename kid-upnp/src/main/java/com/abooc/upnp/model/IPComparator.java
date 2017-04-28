package com.abooc.upnp.model;

import java.util.Comparator;

public class IPComparator implements Comparator<DeviceDisplay> {

    @Override
    public int compare(DeviceDisplay lhs, DeviceDisplay rhs) {
        String ipThis = lhs.getHost().substring(lhs.getHost().lastIndexOf(".") + 1);
        Integer intThis = Integer.valueOf(ipThis);

        String anotherHost = rhs.getHost();
        String ipAnother = anotherHost.substring(anotherHost.lastIndexOf(".") + 1);
        Integer intAnother = Integer.valueOf(ipAnother);

        if (intThis > intAnother) {
            return 1;
        } else if (intThis < intAnother) {
            return -1;
        } else {
            return 0;
        }
    }
}