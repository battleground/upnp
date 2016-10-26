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
    public void encoder() {
        String str = "中国";
        String str_GBK = "�й�";
        String origin = "���\b���������������V*J-.�)Q�2�QJ�OIU�230�\u0005���*ݻ\u0017������";

        try {
            byte[] bytes = str_GBK.getBytes();
            System.out.println(
                    toString(bytes) + "\n"
                            + ": " + new String(bytes, "GBK") + "\n"
                            + ": " + new String(str_GBK.getBytes("GBK"), "UTF-8") + "\n"
                    + ": " + new String(origin.getBytes("UTF-8"), "gzip") + "\n"
                            + ": " + new String(str.getBytes("GB2312"), "GB2312") + "\n"
            );

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    String toString(byte[] bytes) {
        StringBuffer buffer = new StringBuffer();
        for (byte b : bytes) {
            buffer.append(b).append(",");
        }
        return buffer.toString();
    }


    @Test
    public void sort() {

        String origin = "���\b���������������V*J-.�)Q�2�QJ�OIU�230�\u0005���*ݻ\u0017������";
        String encode = "\\ufffd\\ufffd\\ufffd\\u0008\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\u0056\\u002a\\u004a\\u002d\\u002e\\ufffd\\u0029\\u0051\\ufffd\\u0032\\ufffd\\u0051\\u004a\\ufffd\\u004f\\u0049\\u0055\\ufffd\\u0032\\u0033\\u0030\\ufffd\\u0005\\ufffd\\ufffd\\ufffd\\u002a\\u077b\\u0017\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd";

        try {
//            String utf_8_origin = new String(origin.getBytes(), "UTF-8");
//            String gbk_origin = new String(origin.getBytes(), "GBK");
//
//            String utf_8 = new String(encode.getBytes(), "UTF-8");
//            String gbk = new String(encode.getBytes(), "GBK");

            String gb2312 = new String(encode.getBytes(), "GB2312");


            String utf_8_origin = URLDecoder.decode(origin, "UTF-8");
            String gbk_origin = URLDecoder.decode(origin, "GBK");
            String utf_8 = URLDecoder.decode(encode, "UTF-8");
            String gbk = URLDecoder.decode(encode, "GBK");


            System.out.println(
                    "utf_8_origin:" + utf_8_origin + "\n"
                            + "gbk_origin:" + gbk_origin + "\n"
                            + "utf_8:" + utf_8 + "\n"
                            + "gbk:" + gbk + "\n"
                            + "gb2312:" + gb2312 + "\n"
            );

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }
}