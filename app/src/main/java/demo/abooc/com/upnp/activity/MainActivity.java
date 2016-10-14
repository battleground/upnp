package demo.abooc.com.upnp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.abooc.upnp.Discovery;
import com.abooc.widget.Toast;

import demo.abooc.com.upnp.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toast.init(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Discovery mDiscovery = Discovery.get();
//        mDiscovery.setFilter(new Filter());
        mDiscovery.bindServer(this);
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
//        KidsActivity.launch(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Discovery.get().unbindServer(this);
    }
}
