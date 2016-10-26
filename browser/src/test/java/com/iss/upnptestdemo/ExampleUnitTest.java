package com.iss.upnptestdemo;

import junit.framework.TestCase;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest extends TestCase{
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testUri() throws UnsupportedEncodingException {

        String str = "/a/b c/";
        String encode = URLEncoder.encode(str, "UTF-8");
        URI.create(encode);
    }
}