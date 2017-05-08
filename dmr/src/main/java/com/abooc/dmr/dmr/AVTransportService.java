package com.abooc.dmr.dmr;

import android.os.Looper;
import android.os.Message;

import com.abooc.upnp.UI;
import com.abooc.util.Debug;

import org.fourthline.cling.binding.annotations.UpnpInputArgument;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.avtransport.AVTransportException;
import org.fourthline.cling.support.avtransport.AbstractAVTransportService;
import org.fourthline.cling.support.contentdirectory.DIDLParser;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.DeviceCapabilities;
import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.TransportAction;
import org.fourthline.cling.support.model.TransportInfo;
import org.fourthline.cling.support.model.TransportSettings;
import org.fourthline.cling.support.model.item.Item;
import org.fourthline.cling.support.model.item.MusicTrack;
import org.fourthline.cling.support.model.item.Photo;
import org.fourthline.cling.support.model.item.VideoItem;

import java.util.List;

public class AVTransportService extends AbstractAVTransportService {

    UI iUI;

    protected AVTransportService() {

    }

    protected AVTransportService(LastChange lastChange) {
        super(lastChange);

    }

    public void setUI(UI ui) {
        iUI = ui;
        if (ui == null) {
            Looper.prepare();
            iUI = new UI(Looper.getMainLooper());
        }
    }

    @Override
    public void setAVTransportURI(
            @UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId,
            @UpnpInputArgument(name = "CurrentURI", stateVariable = "AVTransportURI") String currentURI,
            @UpnpInputArgument(name = "CurrentURIMetaData", stateVariable = "AVTransportURIMetaData")
                    String currentURIMetaData) throws AVTransportException {

        Debug.error(currentURI + "\n" + currentURIMetaData);


        Item item = parseCurrentURIMetaData(currentURIMetaData);
        Class<? extends Item> itemClass = item.getClass();

        String title = item.getTitle();
        Debug.anchor(Thread.currentThread().getName() + " " + itemClass.toString() + " " + title);

        if (Photo.class.getSimpleName().equals(itemClass.getSimpleName())) {
            Message message = Message.obtain(iUI, UI.ITEM_IMAGE, title);
            iUI.sendMessage(message);

        } else if (VideoItem.class.getSimpleName().equals(itemClass.getSimpleName())) {
            Message message = Message.obtain(iUI, UI.ITEM_VIDEO, title);
            iUI.sendMessage(message);

        } else if (MusicTrack.class.getSimpleName().equals(itemClass.getSimpleName())) {
            Message message = Message.obtain(iUI, UI.ITEM_MUSIC, title);
            iUI.sendMessage(message);

        }

    }

    private static DIDLParser mDIDLParser = new DIDLParser();

    public static Item parseCurrentURIMetaData(String xml) {
        if (xml == null || "".equals(xml)) return null;
//        Debug.anchor(xml);
        try {
            DIDLContent didlContent = mDIDLParser.parse(xml);
            List<Item> items = didlContent.getItems();
            if (items.isEmpty()) return null;
            Item item = items.get(0);
            return item;
        } catch (Exception e) {
            Debug.error(e);
        }
        return null;
    }

    @Override
    public void setNextAVTransportURI(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId, @UpnpInputArgument(name = "NextURI", stateVariable = "AVTransportURI") String nextURI, @UpnpInputArgument(name = "NextURIMetaData", stateVariable = "AVTransportURIMetaData") String nextURIMetaData) throws AVTransportException {

    }

    @Override
    public MediaInfo getMediaInfo(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        Debug.error(Thread.currentThread().getName());
        return null;
    }

    @Override
    public TransportInfo getTransportInfo(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        return null;
    }

    @Override
    public PositionInfo getPositionInfo(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        Debug.error(Thread.currentThread().getName());
        return null;
    }

    @Override
    public DeviceCapabilities getDeviceCapabilities(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        return null;
    }

    @Override
    public TransportSettings getTransportSettings(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        return null;
    }

    @Override
    public void stop(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId) throws AVTransportException {

        Debug.error(Thread.currentThread().getName());
        iUI.sendEmptyMessage(UI.Player.STOP);
    }

    @Override
    public void play(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId, @UpnpInputArgument(name = "Speed", stateVariable = "TransportPlaySpeed") String speed) throws AVTransportException {

        Debug.error(Thread.currentThread().getName());
        iUI.sendEmptyMessage(UI.Player.PLAY);
    }

    @Override
    public void pause(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId) throws AVTransportException {

        Debug.error(Thread.currentThread().getName());
        iUI.sendEmptyMessage(UI.Player.PAUSE);
    }

    @Override
    public void record(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId) throws AVTransportException {

    }

    @Override
    public void seek(
            @UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId,
            @UpnpInputArgument(name = "Unit", stateVariable = "A_ARG_TYPE_SeekMode") String unit,
            @UpnpInputArgument(name = "Target", stateVariable = "A_ARG_TYPE_SeekTarget") String target) throws AVTransportException {

        Debug.error(Thread.currentThread().getName() + " unit:" + unit + " target:" + target);
        Message message = Message.obtain(iUI, UI.Player.SEEK, target);
        iUI.sendMessage(message);
    }

    @Override
    public void next(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId) throws AVTransportException {

    }

    @Override
    public void previous(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId) throws AVTransportException {

    }

    @Override
    public void setPlayMode(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId, @UpnpInputArgument(name = "NewPlayMode", stateVariable = "CurrentPlayMode") String newPlayMode) throws AVTransportException {

    }

    @Override
    public void setRecordQualityMode(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes instanceId, @UpnpInputArgument(name = "NewRecordQualityMode", stateVariable = "CurrentRecordQualityMode") String newRecordQualityMode) throws AVTransportException {

    }

    @Override
    protected TransportAction[] getCurrentTransportActions(UnsignedIntegerFourBytes instanceId) throws Exception {
        return new TransportAction[0];
    }

    @Override
    public UnsignedIntegerFourBytes[] getCurrentInstanceIds() {
        return new UnsignedIntegerFourBytes[0];
    }
}