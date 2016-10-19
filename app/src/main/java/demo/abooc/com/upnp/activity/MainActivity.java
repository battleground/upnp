package demo.abooc.com.upnp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.abooc.upnp.Discovery;
import com.abooc.upnp.DlnaManager;
import com.abooc.widget.Toast;

import demo.abooc.com.upnp.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toast.init(this);
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
//        KidsActivity.launch(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DlnaManager.getInstance().stop();
        Discovery.get().exit(this);
    }
}
