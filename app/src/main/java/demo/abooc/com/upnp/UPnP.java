package demo.abooc.com.upnp;

import com.abooc.util.Debug;

import org.fourthline.cling.support.contentdirectory.DIDLParser;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.item.Item;
import org.seamless.util.MimeType;

import java.net.URI;
import java.util.List;

/**
 * Created by author:李瑞宇
 * email:allnet@live.cn
 * on 16/8/12.
 */
public class UPnP {

    public static String buildMetadataXml(Item item) {
        DIDLContent didlContent = new DIDLContent();
        didlContent.addItem(item);
        DIDLParser parser = new DIDLParser();
        String metadata = "";
        try {
            metadata = parser.generate(didlContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return metadata;
    }

    public static Res buildRes(String mimeType, String filePath, String url, long size) {
        Res res = new Res(new MimeType(mimeType.substring(0, mimeType.indexOf('/')),
                mimeType.substring(mimeType.indexOf('/') + 1)), size, url);
        res.setImportUri(URI.create(url));
        return res;
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

//            String itemId = item.getId();
//            String refID = item.getRefID();
//            String parentID = item.getParentID();
//            boolean restricted = item.isRestricted();

//            Debug.anchor(
//                    "itemId:" + itemId + "\n"
//                            + "refID:" + refID + "\n"
//                            + "parentID:" + parentID + "\n"
//                            + "是否保密:" + restricted + "\n"
//                            + ToString.toString(items));
            return item;
        } catch (Exception e) {
            Debug.e(e);
        }
        return null;
    }

}
