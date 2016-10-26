package com.abooc.dlna.media;

import android.os.Build;

import com.abooc.util.Debug;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.ManufacturerDetails;
import org.fourthline.cling.model.meta.ModelDetails;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDN;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

/**
 * Created by author:李瑞宇
 * email:allnet@live.cn
 * on 16/8/28.
 */
public class MyHttpServer extends HttpServer {

    public final static int PORT = 8196;
    private static InetAddress mLocalAddress;

    public static void setLocalAddress(InetAddress localAddress) {
        mLocalAddress = localAddress;
    }

    public static String getAddress() {
        return "http://" + mLocalAddress.getHostAddress() + ":" + PORT;
    }

    public MyHttpServer(int port) throws IOException {
        super(port);
    }

    @Override
    public String filePath(String requestUri) {
        String uri = MediaDao.get().findItem(requestUri);
        Debug.anchor(uri);
        return uri == null ? requestUri : uri;
    }


    public static void startContentDirectory(AndroidUpnpService upnpService) {
        // 启动ContentDirectory服务
        try {
            DeviceType type = new UDADeviceType("MediaServer", 1);

            ManufacturerDetails manufacturerDetails = new ManufacturerDetails(Build.MANUFACTURER);
            ModelDetails modelDetails = new ModelDetails("GNaP", "GNaP MediaServer for Android", "v1");
            DeviceDetails details = new DeviceDetails(android.os.Build.MODEL + "(本机)", manufacturerDetails, modelDetails);


            LocalService service = new AnnotationLocalServiceBinder().read(DLNADirectoryService.class);
            service.setManager(new DefaultServiceManager<>(service, DLNADirectoryService.class));

            UDN udn = new UDN(UUID.nameUUIDFromBytes("GNaP-MediaServer".getBytes()));
            LocalDevice localDevice = new LocalDevice(new DeviceIdentity(udn), type, details, service);

            upnpService.getRegistry().addDevice(localDevice);

        } catch (ValidationException e) {
            e.printStackTrace();
        }

    }
}
