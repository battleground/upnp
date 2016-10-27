package demo.abooc.com.upnp.activity;

import android.view.View;
import android.widget.TextView;

import demo.abooc.com.upnp.R;

/**
 * Created by author:李瑞宇
 * email:allnet@live.cn
 * on 16/10/20.
 */

public class RendererInfoView {

    private View rootView;

    private TextView mIPAddressText;
    private TextView mDeviceInfoText;
    private TextView mIsMuteText;

    private TextView mVolumeValueText;
    private TextView mVolumeMaxText;
    private TextView mVolumeMinText;

    public RendererInfoView(View root) {
        rootView = root;

        mIPAddressText = (TextView) root.findViewById(R.id.IPAddress);
        mDeviceInfoText = (TextView) root.findViewById(R.id.DeviceInfo);
        mIsMuteText = (TextView) root.findViewById(R.id.VolumeValue);

        mVolumeValueText = (TextView) root.findViewById(R.id.VolumeValue);
        mVolumeMaxText = (TextView) root.findViewById(R.id.VolumeMaxValue);
        mVolumeMinText = (TextView) root.findViewById(R.id.VolumeMinValue);

    }

    public void setVisibility(int visibility) {
        rootView.setVisibility(visibility);
    }

    public void setIpAddress(String ipAddress) {
        mIPAddressText.setText(ipAddress);
    }

    public void setDeviceInfo(String deviceInfo) {
        mDeviceInfoText.setText(deviceInfo);
    }

    public void setMute(boolean mute) {
        mIsMuteText.setText(Boolean.valueOf(mute).toString());
    }

    public void setVolume(long volume) {
        mVolumeValueText.setText(String.valueOf(volume));
    }

    public void setVolumeMax(String volumeMax) {
        mVolumeMaxText.setText(volumeMax);
    }

    public void setVolumeMin(String volumeMin) {
        mVolumeMinText.setText(volumeMin);
    }

    public void clear() {
        mIPAddressText.setText(null);
        mDeviceInfoText.setText(null);
        mIsMuteText.setText(null);

        mVolumeValueText.setText(null);
        mVolumeMaxText.setText(null);
        mVolumeMinText.setText(null);
    }

}
