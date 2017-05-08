package demo.abooc.com.upnp.activity;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.abooc.plugin.about.AboutActivity;
import com.abooc.upnp.Discovery;
import com.abooc.upnp.DlnaManager;

import demo.abooc.com.upnp.dmr.UDNBuilder;
import demo.abooc.com.upnp.dmr.UPnPCenter2;

import com.abooc.util.Debug;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.registry.Registry;

import demo.abooc.com.upnp.AppAndroidUPnPService;
import demo.abooc.com.upnp.R;
import demo.abooc.com.upnp.kids.KidsActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DlnaManager.getInstance().setServiceConnection(new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                try {
                    Debug.error();


                    String udnString = UDNBuilder.getUdnString("upnp-dmr-");

                    Registry registry = ((AndroidUpnpService) service).getRegistry();
                    LocalDevice localDevice = UPnPCenter2.createDevice(udnString);
                    registry.addDevice(localDevice);
                } catch (ValidationException e) {
                    Debug.error();
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        });
        DlnaManager.getInstance().startService(this, AppAndroidUPnPService.class);
        Discovery.get().registerWiFiReceiver(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void onLaunchScan(View view) {
        ScanActivity.launch(this);
    }

    public void onLaunchKids(View view) {
        KidsActivity.launch(this);
    }

    public void onLaunchAbout(View view) {
        AboutActivity.launch(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DlnaManager.getInstance().stop();
        Discovery.get().unregisterWiFiReceiver(this);
    }

}
