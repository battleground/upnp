package com.abooc.dmr;

import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.abooc.dmr.dmr.UDNBuilder;
import com.abooc.dmr.dmr.UPnPCenter2;
import com.abooc.upnp.Discovery;
import com.abooc.upnp.DlnaManager;
import com.abooc.upnp.UI;
import com.abooc.util.Debug;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.registry.Registry;

public class MainActivity extends AppCompatActivity {


    class Viewer {

        TextView titleText;
        TextView typeText;
        TextView volumeText;
        TextView statusText;
        SeekBar seekBar;

        Viewer(View view) {
            titleText = (TextView) view.findViewById(R.id.title);
            typeText = (TextView) view.findViewById(R.id.type);
            volumeText = (TextView) view.findViewById(R.id.volume);
            statusText = (TextView) view.findViewById(R.id.status);
            seekBar = (SeekBar) view.findViewById(R.id.Seek);
        }

        public void setType(String status) {
            typeText.setText(status);
        }

        public void setStatus(String status) {
            statusText.setText(status);
        }

        public void seek(String target) {
            statusText.setText(target);
        }

    }

    Viewer iViewer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iViewer = new Viewer(getWindow().getDecorView());

        UPnPCenter2.getInstance().setUI(new UI() {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case ITEM_IMAGE:
                        iViewer.setType("图片");
                        iViewer.titleText.setText((String) msg.obj);
                        break;
                    case ITEM_MUSIC:
                        iViewer.setType("音乐");
                        iViewer.titleText.setText((String) msg.obj);
                        break;
                    case ITEM_VIDEO:
                        iViewer.setType("视频");
                        iViewer.titleText.setText((String) msg.obj);
                        break;

                    case Player.PLAY:
                        iViewer.setStatus("PLAY");
                        break;
                    case Player.PAUSE:
                        iViewer.setStatus("PAUSE");
                        break;
                    case Player.STOP:
                        iViewer.setStatus("STOP");
                        break;
                    case Player.SEEK:
                        String target = (String) msg.obj;
                        iViewer.seek(target);
                        break;
                }
            }
        });


        DlnaManager.getInstance().setServiceConnection(new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                try {
                    Debug.error();

                    String udnString = UDNBuilder.getUdnString("abooc-dmr-");

                    Registry registry = ((AndroidUpnpService) service).getRegistry();
                    LocalDevice localDevice = UPnPCenter2.getInstance().createDevice(udnString);
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
        DlnaManager.getInstance().startService(this, AndroidUpnpServiceImpl.class);
        Discovery.get().registerWiFiReceiver(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        DlnaManager.getInstance().stop();
        Discovery.get().unregisterWiFiReceiver(this);
    }

}
