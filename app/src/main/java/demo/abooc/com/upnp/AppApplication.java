package demo.abooc.com.upnp;

import android.app.Application;

import com.abooc.upnp.Discovery;
import com.abooc.upnp.DlnaManager;

import com.abooc.plugin.about.About;
import com.abooc.util.Debug;
import com.abooc.widget.Toast;

/**
 * Created by dayu on 2017/2/22.
 */

public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.init(this);
        About.defaultAbout(this);
    }

    @Override
    public void onLowMemory() {
        Debug.error("*****************");
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        Debug.error("*****************");
        super.onTerminate();
    }

    @Override
    public void onTrimMemory(int level) {
        Debug.error("*****************");
        super.onTrimMemory(level);
    }


}
