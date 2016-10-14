/**
 * Copyright (C) 2013 Aurélien Chabot <aurelien@chabot.fr>
 * <p/>
 * This file is part of DroidUPNP.
 * <p/>
 * DroidUPNP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * DroidUPNP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with DroidUPNP.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.abooc.upnp;

import android.widget.Checkable;

public class DeviceDisplay implements Checkable {

    private final IUPnPDevice device;
    private final boolean extendedInformation;
    private String host;

    public DeviceDisplay(IUPnPDevice device, boolean extendedInformation) {
        this.device = device;
        this.extendedInformation = extendedInformation;
    }

    public DeviceDisplay(IUPnPDevice device) {
        this(device, false);
    }

    public IUPnPDevice getDevice() {
        return device;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        DeviceDisplay that = (DeviceDisplay) o;
        return device.equals(that.device);
    }

    @Override
    public int hashCode() {
        if (device == null)
            return 0;

        return device.hashCode();
    }

    @Override
    public String toString() {
        if (device == null)
            return "";

        String name = getDevice().getFriendlyName();

        if (extendedInformation)
            name += getDevice().getExtendedInformation();

        return name;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return this.host;
    }

    private boolean checked = false;

    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public void toggle() {
        checked = !checked;
    }
}