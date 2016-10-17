package demo.abooc.com.upnp;

import org.fourthline.cling.support.model.Res;
import org.junit.Test;
import org.seamless.util.MimeType;

import demo.abooc.com.upnp.model.VRVideoItem;

import static junit.framework.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }


    @Test
    public void metadataXml() {
        MimeType mimeType = new MimeType("video", "vr");
        Res res = UPnP.buildRes(mimeType.toString(), AppTestResources.videoUri, AppTestResources.videoUri, 0);
        VRVideoItem videoItem = new VRVideoItem(3, "1", String.valueOf(1), "天空之城[高清国语]", "creator", res);

        String metadata = UPnP.buildMetadataXml(videoItem);

        System.out.print(metadata);
    }

}