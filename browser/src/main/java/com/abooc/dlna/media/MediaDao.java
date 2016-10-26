package com.abooc.dlna.media;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.PersonWithRole;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.item.ImageItem;
import org.fourthline.cling.support.model.item.Item;
import org.fourthline.cling.support.model.item.MusicTrack;
import org.fourthline.cling.support.model.item.VideoItem;
import org.seamless.util.MimeType;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import demo.com.abooc.upnp.browser.utils.DurationUtil;

/**
 * Created by author:李瑞宇
 * email:allnet@live.cn
 * on 16/8/25.
 */
public class MediaDao {

    private static MediaDao mOur = new MediaDao();
    private static String mResAddress = "";
    private static ContentResolver mContentResolver;

    private MediaDao() {
    }

    public static MediaDao get() {
        return mOur;
    }

    public static void init(Context ctx, String serverAddress) {
        mContentResolver = ctx.getContentResolver();
        mResAddress = serverAddress;
    }

    private HashMap<String, String> cacheMap = new HashMap<>();

    public String findItem(String uri) {
        return cacheMap.get(uri);
    }


    public String buildUrl(DIDLObject.Class clazz, String target) {
        String fullUrl = target;
        switch (clazz.getValue()) {
            case "object.item":
                fullUrl = mResAddress + "/" + AppRootContainer.ITEM_PREFIX + target;
                break;
            case "object.item.imageItem":
            case "object.item.imageItem.photo":
                fullUrl = mResAddress + "/" + AppRootContainer.IMAGE_PREFIX + target;
                break;
            case "object.item.videoItem":
            case "object.item.videoItem.movie":
                fullUrl = mResAddress + "/" + AppRootContainer.VIDEO_PREFIX + target;
                break;
            case "object.item.audioItem":
            case "object.item.audioItem.musicTrack":
                fullUrl = mResAddress + "/" + AppRootContainer.AUDIO_PREFIX + target;
                break;
        }
        return fullUrl;
    }

    public ArrayList<Item> queryAllItems() {
        ArrayList<Item> items = new ArrayList<>();

        String[] videoColumns = {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.TITLE,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_MODIFIED,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.SIZE
        };


        Cursor iCursor = mContentResolver.query(MediaStore.getMediaScannerUri(), videoColumns, null, null, null);

        if (iCursor != null) {
            while (iCursor.moveToNext()) {
                int id = iCursor.getInt(iCursor.getColumnIndex(MediaStore.Files.FileColumns._ID));
                String title = iCursor.getString(iCursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE));
                String filePath = iCursor.getString(iCursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                int duration = iCursor.getInt(iCursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED));
                String mimeType = iCursor.getString(iCursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE));
                long size = iCursor.getLong(iCursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE));
                String durationString = DurationUtil.toMilliTimeString(duration);

                DIDLObject.Class clazz = new DIDLObject.Class("object.item");
                String url = buildUrl(clazz, String.valueOf(id));
                Item item = new Item(String.valueOf(id), "0", title, "creator", clazz);
                items.add(item);
            }
        }
        return items;
    }

    public ArrayList<Item> queryVideoItems() {
        ArrayList<Item> items = new ArrayList<>();

        String[] videoColumns = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.ARTIST,
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.RESOLUTION
        };


        Cursor iCursor = mContentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoColumns, null, null, null);

        if (iCursor != null) {
            while (iCursor.moveToNext()) {
                int id = iCursor.getInt(iCursor.getColumnIndex(MediaStore.Video.Media._ID));
                String title = iCursor.getString(iCursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                String filePath = iCursor.getString(iCursor.getColumnIndex(MediaStore.Video.Media.DATA));
                String creator = iCursor.getString(iCursor.getColumnIndex(MediaStore.Video.Media.ARTIST));
                String mimeType = iCursor.getString(iCursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE));
                long size = iCursor.getLong(iCursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                long duration = iCursor.getLong(iCursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                String resolution = iCursor.getString(iCursor.getColumnIndex(MediaStore.Video.Media.RESOLUTION));
                String durationString = DurationUtil.toMilliTimeString(duration);
                String url = buildUrl(VideoItem.CLASS, String.valueOf(id));
                String filename = filePath.substring(filePath.lastIndexOf("/") + 1);


                Res res = buildRes(mimeType, filePath, url, size);
                res.setDuration(durationString);
                res.setResolution(resolution);

                VideoItem videoItem = new VideoItem(String.valueOf(id), AppRootContainer.VIDEO_ID, filename, creator, res);
                items.add(videoItem);

                cacheMap.put("/" + id, filePath);
            }
        }
        return items;
    }


    public ArrayList<Item> getAudioItems() {
        ArrayList<Item> items = new ArrayList<>();

        String[] audioColumns = {MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.MIME_TYPE,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM};

        Cursor cur = mContentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, audioColumns, null, null, null);

        if (cur != null) {
            while (cur.moveToNext()) {
                int id = cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media._ID));
                String title = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String filePath = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA));
                String creator = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String mimeType = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE));
                long size = cur.getLong(cur.getColumnIndex(MediaStore.Audio.Media.SIZE));
                long duration = cur.getLong(cur.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String album = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String filename = filePath.substring(filePath.lastIndexOf("/") + 1);


                String url = buildUrl(MusicTrack.CLASS, String.valueOf(id));
                String durationString = DurationUtil.toMilliTimeString(duration);

                Res res = buildRes(mimeType, filePath, url, size);
                res.setDuration(durationString);
                MusicTrack musicTrack = new MusicTrack(String.valueOf(id), AppRootContainer.AUDIO_ID, filename, creator, album,
                        new PersonWithRole(creator, "Performer"), res);
                items.add(musicTrack);

                cacheMap.put("/" + id, filePath);
            }
        }
        return items;
    }

    public ArrayList<Item> getImageItems() {
        ArrayList<Item> items = new ArrayList<>();

        String[] imageColumns = {MediaStore.Images.Media._ID,
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.SIZE};

        Cursor cur = mContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns, null, null, null);

        if (cur != null) {
            while (cur.moveToNext()) {
                int id = cur.getInt(cur.getColumnIndex(MediaStore.Images.Media._ID));
                String title = cur.getString(cur.getColumnIndex(MediaStore.Images.Media.TITLE));
                String creator = "unknown";
                String filePath = cur.getString(cur.getColumnIndex(MediaStore.Images.Media.DATA));
                String mimeType = cur.getString(cur.getColumnIndex(MediaStore.Images.Media.MIME_TYPE));
                long size = cur.getLong(cur.getColumnIndex(MediaStore.Images.Media.SIZE));
                String url = buildUrl(ImageItem.CLASS, String.valueOf(id));
                String filename = filePath.substring(filePath.lastIndexOf("/") + 1);

                Res res = buildRes(mimeType, filePath, url, size);
                ImageItem imageItem = new ImageItem(String.valueOf(id), AppRootContainer.IMAGE_ID, filename, creator, res);
                items.add(imageItem);

                cacheMap.put("/" + id, filePath);
            }
        }
        return items;
    }

    private Res buildRes(String mimeType, String filePath, String url, long size) {
        Res res = new Res(new MimeType(mimeType.substring(0, mimeType.indexOf('/')),
                mimeType.substring(mimeType.indexOf('/') + 1)), size, url);
        try {
            String encode = URLEncoder.encode(filePath, "UTF-8");
            res.setImportUri(URI.create(encode));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return res;
    }

}
