package com.abooc.dmr.dmr;

import com.abooc.util.Debug;

import org.fourthline.cling.binding.annotations.UpnpInputArgument;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.types.UnsignedIntegerTwoBytes;
import org.fourthline.cling.support.model.Channel;
import org.fourthline.cling.support.renderingcontrol.AbstractAudioRenderingControl;
import org.fourthline.cling.support.renderingcontrol.RenderingControlException;

public class RenderingControl extends AbstractAudioRenderingControl {

    @Override
    public boolean getMute(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId, @UpnpInputArgument(name = "Channel") String channelName) throws RenderingControlException {
        Debug.error();
        return false;
    }

    @Override
    public void setMute(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId, @UpnpInputArgument(name = "Channel") String channelName, @UpnpInputArgument(name = "DesiredMute", stateVariable = "Mute") boolean desiredMute) throws RenderingControlException {
        Debug.error();
    }

    @Override
    public UnsignedIntegerTwoBytes getVolume(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId, @UpnpInputArgument(name = "Channel") String channelName) throws RenderingControlException {
        return null;
    }

    @Override
    public void setVolume(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId, @UpnpInputArgument(name = "Channel") String channelName, @UpnpInputArgument(name = "DesiredVolume", stateVariable = "Volume") UnsignedIntegerTwoBytes desiredVolume) throws RenderingControlException {
        Debug.error();
    }

    @Override
    protected Channel[] getCurrentChannels() {
        return new Channel[0];
    }

    @Override
    public UnsignedIntegerFourBytes[] getCurrentInstanceIds() {
        return new UnsignedIntegerFourBytes[0];
    }
}

