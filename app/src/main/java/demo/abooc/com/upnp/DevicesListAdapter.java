package demo.abooc.com.upnp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.abooc.upnp.DeviceDisplay;

import java.util.List;

/**
 * Created by author:李瑞宇
 * email:allnet@live.cn
 * on 16/8/9.
 */
public class DevicesListAdapter extends BaseAdapter {

    private Context mContext;
    private List<DeviceDisplay> mList;

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
        TextView name = (TextView) view.findViewById(R.id.name);
        TextView friendlyName = (TextView) view.findViewById(R.id.friendlyName);
        TextView serialNumber = (TextView) view.findViewById(R.id.serialNumber);
        TextView JsonText = (TextView) view.findViewById(R.id.Json);

        name.setText(display.getDevice().getFriendlyName());
        friendlyName.setText(display.getDevice().getModelName());
        serialNumber.setText(display.getHost());
        JsonText.setText(display.getDevice().getModelName());
    }
}
