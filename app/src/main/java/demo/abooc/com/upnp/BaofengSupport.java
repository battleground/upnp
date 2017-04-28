package demo.abooc.com.upnp;

import android.text.TextUtils;

import com.abooc.util.Debug;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.RemoteDeviceIdentity;

/**
 * Created by author:李瑞宇
 * email:allnet@live.cn
 * on 16/8/8.
 */
public class BaofengSupport {

    static String BAOFENG_TV = "baofengtv";
    static String BAOFENG_VR = "baofengtv:vr";
    static String BAOFENG_TV_3_0 = "{\"BFTV\":3.0,\"port\":21367}";

    private static boolean DEBUG = true;

    public static void debug(boolean debug) {
        DEBUG = debug;
    }

    public static boolean isDebug() {
        return DEBUG;
    }

    public static boolean isBaofengTV(Device device) {
//        return isBaofengTV_1_0(device) || isBaofengTV_3_0(device);
        DeviceDetails details = device.getDetails();
        String manufacturer = details.getManufacturerDetails().getManufacturer();
        Debug.anchor(manufacturer + ", " + getHost(device));
        return manufacturer != null && (manufacturer.toLowerCase().contains("baofeng") || manufacturer.contains("BFTV"));
    }

    public static boolean isBaofengTV_1_0(Device device) {
        DeviceDetails details = device.getDetails();
        String manufacturer = details.getManufacturerDetails().getManufacturer();
        Debug.anchor(manufacturer + ", " + getHost(device));
        return BAOFENG_TV.equalsIgnoreCase(manufacturer) || BAOFENG_VR.equalsIgnoreCase(manufacturer);
    }

    public static boolean isBaofengTV_3_0(Device device) {
        DeviceDetails details = device.getDetails();
        String manufacturer = details.getManufacturerDetails().getManufacturer();
        return manufacturer != null && manufacturer.startsWith("{\"BFTV");
    }

    public static boolean isSupportVR(Device device) {
        String manufacturer = device.getDetails().getManufacturerDetails().getManufacturer();
        return BAOFENG_VR.equalsIgnoreCase(manufacturer);
    }

    public static String getHost(Device device) {
        RemoteDeviceIdentity identity = (RemoteDeviceIdentity) device.getIdentity();
        String host = identity.getDescriptorURL().getHost();
        return host;
    }

    public static String getType(Device device) {
        if (device == null || device.getDetails() == null
                || device.getDetails().getManufacturerDetails() == null) {
            return null;
        }
        String type;
        String manufacturer = device.getDetails().getManufacturerDetails().getManufacturer();
        if (TextUtils.isEmpty(manufacturer)) {
            return null;
        }

        boolean baofeng = manufacturer.toLowerCase().contains("baofeng") || manufacturer.contains("BFTV");

        if (baofeng) {
            if (manufacturer.contains("baofengtv")) {
                if (manufacturer.contains("baofengtv:vr")) {
                    type = "暴风电视 V1.0， 支持VR";
                } else {
                    type = "暴风电视 V1.0";
                }
            } else if (manufacturer.contains("BFTV")) {
                type = "暴风电视 V3.0";
            } else {
                type = "暴风电视";
            }
        } else {
            type = manufacturer;
        }
        return type;
    }


//    public static void isSupport(@NonNull String server, Callback callback) {
//        server = "http://" + server + ":" + OkSender.PORT_REMOTER;
//        System.out.println(server + "[POST:" + "{}" + "]");
//
//        RequestBody body = RequestBody.create(OkSender.JSON, "{}");
//        Request request = new Request.Builder()
//                .url(server)
//                .post(body)
//                .build();
//
//        OkHttpClient httpClient = new OkHttpClient();
//        httpClient.newCall(request).enqueue(callback);
//    }

}