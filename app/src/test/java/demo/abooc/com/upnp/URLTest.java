package demo.abooc.com.upnp;

import org.junit.Test;
import org.seamless.util.URIUtil;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by dayu on 2017/5/8.
 */

public class URLTest {


    @Test
    public void test_url_2() throws MalformedURLException, URISyntaxException {

        URL url = new URL("http", "192.168.1.1", 3333, "/aa/a.xml");
        out(url.toString());
    }


    @Test
    public void test_uri() throws MalformedURLException, URISyntaxException {

        String subURL = "AVTransport//scpd.xml";
        String base = "http://192.168.232.144:1746/" + subURL;

        URL allURL = URI.create(base).toURL();

        String host = allURL.getHost();
        String protocol = allURL.getProtocol();
        int port = allURL.getPort();
        String path = allURL.getPath();

        out(base + "\n\n"
                + "host:" + host + "\n"
                + "protocol:" + protocol + "\n"
                + "port:" + port + "\n"
                + "path:" + path + "\n"
        );


        String newBase = protocol + "://" + host + ":" + port;
        String newUrl = newBase + path;

        out("newBase:" + newBase + "\n"
                + "newUrl:" + newUrl + "\n");

        try {
            URI uri = allURL.toURI();
            String uriHost = uri.getHost();
            String uriScheme = uri.getScheme();
            int uriPort = uri.getPort();
            String uriPath = uri.getPath();

            out("uriHost:" + uriHost + "\n"
                    + "uriScheme:" + uriScheme + "\n"
                    + "uriPort:" + uriPort + "\n"
                    + "uriPath:" + uriPath + "\n"
            );
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


    }


    @Test
    public void test_url() throws MalformedURLException {
        String base = "http://192.168.232.144:1746/";
        String subURL = "AVTransport//scpd.xml";

        URI subRUi = URI.create(subURL);
        System.out.println("原：" + subRUi);

        URI baseUri = URI.create(base);
        URL baseURL = baseUri.toURL();
        URL absoluteURL = URIUtil.createAbsoluteURL(baseURL, subRUi);
        System.out.println("URIUtil：" + absoluteURL);
        System.out.println();

        URI allUri = URI.create(base + subURL);
        System.out.println("allUri: " + allUri.toString());
        System.out.println("resolve allURi: " + baseUri.resolve(subRUi));


    }

    void out(Object obj) {
        System.out.println(obj);
    }

}
