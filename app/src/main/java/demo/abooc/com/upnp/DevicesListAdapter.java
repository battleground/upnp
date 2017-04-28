package demo.abooc.com.upnp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.abooc.upnp.model.DeviceDisplay;

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
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.remoter_scanning_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();

        DeviceDisplay item = getItem(position);

        holder.attachData(item);
        return convertView;
    }

    class ViewHolder {

        TextView iNameText;
        ImageView joinView;
        TextView modelText;
        TextView ipText;

        ViewHolder(View convertView) {
            iNameText = (TextView) convertView.findViewById(R.id.name);
            joinView = (ImageView) convertView.findViewById(R.id.checked);
            modelText = (TextView) convertView.findViewById(R.id.model);
            modelText.setVisibility(BaofengSupport.isDebug() ? View.VISIBLE : View.INVISIBLE);
            ipText = (TextView) convertView.findViewById(R.id.ip);
        }

        void attachData(DeviceDisplay device) {
            String name = device.getDevice().getFriendlyName();
            iNameText.setText(name);
            ipText.setText(device.getHost());
            joinView.setVisibility(device.isChecked() ? View.VISIBLE : View.INVISIBLE);
            if (BaofengSupport.isDebug()) {
                modelText.setText("设备：" + BaofengSupport.getType(device.getOriginDevice()));
            }
        }
    }

}