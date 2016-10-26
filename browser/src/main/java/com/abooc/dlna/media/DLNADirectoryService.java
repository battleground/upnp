package com.abooc.dlna.media;

import com.abooc.util.Debug;

import org.fourthline.cling.support.contentdirectory.AbstractContentDirectoryService;
import org.fourthline.cling.support.contentdirectory.ContentDirectoryErrorCode;
import org.fourthline.cling.support.contentdirectory.ContentDirectoryException;
import org.fourthline.cling.support.contentdirectory.DIDLParser;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.BrowseResult;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.SortCriterion;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.item.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by author:李瑞宇
 * email:allnet@live.cn
 * on 16/8/25.
 */
public class DLNADirectoryService extends AbstractContentDirectoryService {


    @Override
    public BrowseResult browse(String objectID, BrowseFlag browseFlag,
                               String filter, long firstResult, long maxResults,
                               SortCriterion[] orderBy) throws ContentDirectoryException {

        Debug.anchor("objectID:" + objectID + ", browseFlag:" + browseFlag);

        DIDLContent didlContent = new DIDLContent();

        try {
            List<Item> items = new ArrayList<>();
            switch (objectID) {
                case AppRootContainer.ROOT_ID:
                    List<Container> containers = AppRootContainer.getInstance().getContainers();

                    for (Container container : containers) {
                        didlContent.addContainer(container);
                    }
                    break;
                case AppRootContainer.ALL_ID:
                    items = MediaDao.get().queryAllItems();
                    for (Item item : items) {
                        didlContent.addItem(item);
                    }

                    break;
                case AppRootContainer.VIDEO_ID:
                    items = MediaDao.get().queryVideoItems();
                    for (Item item : items) {
                        didlContent.addItem(item);
                    }

                    break;
                case AppRootContainer.AUDIO_ID:
                    items = MediaDao.get().getAudioItems();
                    for (Item item : items) {
                        didlContent.addItem(item);
                    }

                    break;
                case AppRootContainer.IMAGE_ID:
                    items = MediaDao.get().getImageItems();
                    for (Item item : items) {
                        didlContent.addItem(item);
                    }

                    break;
            }

            String result = new DIDLParser().generate(didlContent);
            long count = didlContent.getCount();
            long totalMatches = didlContent.getCount();
            return new BrowseResult(result, count, totalMatches);
        } catch (Exception e) {
            throw new ContentDirectoryException(
                    ContentDirectoryErrorCode.CANNOT_PROCESS, e.toString());
        }
    }


}
