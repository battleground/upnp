package demo.abooc.com.upnp;

import org.fourthline.cling.support.model.Res;
import org.junit.Test;
import org.seamless.util.MimeType;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.StringTokenizer;

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

    @Test
    public void uriEncode() throws UnsupportedEncodingException {

        String one = "http://www.abooc.com";
        String two = "/tu pian/中()，,.@$#!~`' 文.jpg";

        String encode = URLEncoder.encode(two, "UTF-8");
        System.out.println(encode);


        String decode = URLDecoder.decode(two, "UTF-8");
        System.out.println(decode);


        URI uri = URI.create(one + encode);

        try {
            System.out.println("path:" + uri.getPath() + "\n"
                    + "Scheme:" + uri.getScheme() + "\n"
                    + "Host:" + uri.getHost() + "\n"
                    + "toString:" + uri.toString() + "\n"
                    + "toURL:" + uri.toURL().toString()
            );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }


    /**
     * For example: "an+example%20string" -> "an example string"
     *
     * @throws InterruptedException
     */
    @Test
    public void encode() throws InterruptedException {

//        String str = "/tupian/中文.jpg";
        String str = "an example string";
        String encodeUri = encodeUri(str);

        System.out.println(encodeUri);

        String decodePercent = decodePercent("an+example%20string");

        System.out.println(decodePercent);


    }


    /**
     * URL-encodes everything between "/"-characters. Encodes spaces as '%20'
     * instead of '+'.
     */
    private String encodeUri(String uri) {
        String newUri = "";
        StringTokenizer st = new StringTokenizer(uri, "/ ", true);
        while (st.hasMoreTokens()) {
            String tok = st.nextToken();
            if (tok.equals("/"))
                newUri += "/";
            else if (tok.equals(" "))
                newUri += "%20";
            else {
                newUri += URLEncoder.encode(tok);
                // For Java 1.4 you'll want to use this instead:
                // try { newUri += URLEncoder.encode( tok, "UTF-8" ); } catch (
                // java.io.UnsupportedEncodingException uee ) {}
            }
        }
        return newUri;
    }


    /**
     * Decodes the percent encoding scheme. <br/>
     * For example: "an+example%20string" -> "an example string"
     */
    private String decodePercent(String str) throws InterruptedException {
        try {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                switch (c) {
                    case '+':
                        sb.append(' ');
                        break;
                    case '%':
                        sb.append((char) Integer.parseInt(
                                str.substring(i + 1, i + 3), 16));
                        i += 2;
                        break;
                    default:
                        sb.append(c);
                        break;
                }
            }
            return sb.toString();
        } catch (Exception e) {
            String error = "BAD REQUEST: Bad percent-encoding.";
            return error;
        }
    }


    @Test
    public void sort() {


    }
}