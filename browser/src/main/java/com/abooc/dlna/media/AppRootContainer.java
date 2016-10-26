package com.abooc.dlna.media;

import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.WriteStatus;
import org.fourthline.cling.support.model.container.Container;

/**
 * Created by author:李瑞宇
 * email:allnet@live.cn
 * on 16/8/26.
 */
public class AppRootContainer extends Container {

    public final static String ROOT_ID = "0";
    public final static String ALL_ID = "-1";
    public final static String VIDEO_ID = "1";
    public final static String AUDIO_ID = "2";
    public final static String IMAGE_ID = "3";
    public final static String ITEM_PREFIX = "item-";
    public final static String VIDEO_PREFIX = "video-item-";
    public final static String AUDIO_PREFIX = "audio-item-";
    public final static String IMAGE_PREFIX = "image-item-";

    private static AppRootContainer ourInstance = new AppRootContainer();

    public static AppRootContainer getInstance() {
        return ourInstance;
    }

    private AppRootContainer() {
        this.setId(ROOT_ID);
        this.setParentID(ROOT_ID);
        this.setTitle("GNaP MediaServer root directory");
        this.setCreator("GNaP Media Server");
        this.setRestricted(true);
        this.setSearchable(true);
        this.setWriteStatus(WriteStatus.NOT_WRITABLE);
    }

    public static Container createContainer(String id, String parentId, String title) {
        Container container = new Container();
        container.setClazz(new DIDLObject.Class("object.container"));
        container.setId(id);
        container.setParentID(parentId);
        container.setTitle(title);
        container.setCreator("GNaP MediaServer");
        container.setRestricted(true);
        container.setWriteStatus(WriteStatus.NOT_WRITABLE);
        return container;
    }

    public static void init() {
        ourInstance.addContainer(ourInstance.createContainer(ALL_ID, ROOT_ID, "全部"));
        ourInstance.addContainer(ourInstance.createContainer(VIDEO_ID, ROOT_ID, "视频"));
        ourInstance.addContainer(ourInstance.createContainer(AUDIO_ID, ROOT_ID, "音乐"));
        ourInstance.addContainer(ourInstance.createContainer(IMAGE_ID, ROOT_ID, "图片"));
    }
}
