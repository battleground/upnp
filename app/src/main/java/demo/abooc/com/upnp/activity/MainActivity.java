package demo.abooc.com.upnp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.abooc.plugin.about.About;
import com.abooc.plugin.about.AboutActivity;
import com.abooc.upnp.Discovery;
import com.abooc.upnp.DlnaManager;
import com.abooc.widget.Toast;

import demo.abooc.com.upnp.R;
import demo.abooc.com.upnp.kids.KidsActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toast.init(this);
        About.defaultAbout(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DlnaManager.getInstance().startService(this);
        Discovery.get().init(this);
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
        Discovery.get().exit(this);
    }

}
