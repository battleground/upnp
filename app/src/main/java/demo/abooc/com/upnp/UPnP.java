package demo.abooc.com.upnp;

import org.fourthline.cling.support.contentdirectory.DIDLParser;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.item.Item;
import org.seamless.util.MimeType;

import java.net.URI;

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
}
