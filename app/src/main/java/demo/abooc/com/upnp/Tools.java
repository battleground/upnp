package demo.abooc.com.upnp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

public class Tools {
    public static String formatToTime(long second) {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
        return format.format(new Date(second * 1000));
    }

    public static String toString(Map<Object, ? extends Object> map) {

        StringBuffer buffer = new StringBuffer();
        Iterator<Object> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            buffer.append(key + " -> ").append(map.get(key)).append("\n");
        }
        return buffer.toString();
    }

    public static String toString2(Map<String, ? extends Object> map) {

        StringBuffer buffer = new StringBuffer();
        Iterator<String> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            buffer.append(key + " -> ").append(map.get(key)).append("\n");
        }
        return buffer.toString();
    }
}