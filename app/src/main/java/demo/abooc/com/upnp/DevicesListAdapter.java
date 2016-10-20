package demo.abooc.com.upnp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.abooc.upnp.model.DeviceDisplay;

import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.RemoteDeviceIdentity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by author:李瑞宇
 * email:allnet@live.cn
 * on 16/8/9.
 */
public class DevicesListAdapter extends BaseAdapter {

    private Context mContext;
    private List<DeviceDisplay> mList = new ArrayList<>();

    public DevicesListAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public DeviceDisplay getItem(int position) {
        if (position >= getCount()) return null;
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void update(List objects) {
        mList = objects;
        notifyDataSetChanged();
    }

    public List<DeviceDisplay> getList() {
        return mList;
    }

    public void add(DeviceDisplay device) {
        mList.add(device);
        notifyDataSetChanged();
    }

    public void remove(DeviceDisplay device) {
        mList.remove(device);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.simple_list_item_2, null);
        }

        DeviceDisplay device = getItem(position);
        if (device != null)
            attach(device, convertView);
        return convertView;
    }

    void attach(DeviceDisplay display, View view) {
        CheckedTextView nameText = (CheckedTextView) view.findViewById(R.id.name);
        TextView IPText = (TextView) view.findViewById(R.id.IP);
        nameText.setText(display.getDevice().getFriendlyName());
        RemoteDeviceIdentity identity = (RemoteDeviceIdentity) display.getOriginDevice().getIdentity();
        IPText.setText(identity.getDescriptorURL().getHost());
        nameText.setChecked(display.isChecked());
    }
}
