package demo.com.abooc.upnp.browser;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.abooc.upnp.model.DeviceDisplay;
import com.abooc.util.Debug;

public class PlayerListFragment extends Fragment {

    private UPnPDirectoryActivity mMainActivity;

    ListView mPlayersListView;
    GeneralAdapter mPlayerListAdapter;


    Handler mPlayersListHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            DeviceDisplay device = (DeviceDisplay) msg.obj;
            switch (msg.what) {
                case HandlerWhat.ADD:
                    int p = mPlayerListAdapter.getPosition(device);
                    if (p >= 0) {
                        // Device already in the list, re-set new value at same
                        // position
                        mPlayerListAdapter.remove(device);
                        mPlayerListAdapter.insert(device, p);
                    } else {
                        mPlayerListAdapter.add(device);
                    }
                    break;
                case HandlerWhat.REMOVE:
                    mPlayerListAdapter.remove(device);
                    break;
            }
        }
    };

    public Handler getHandler() {
        return mPlayersListHandler;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_player_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mPlayersListView = (ListView) view.findViewById(R.id.list);
        mPlayersListView.setOnItemClickListener(iOnItemClickListener);
        mPlayerListAdapter = new GeneralAdapter<DeviceDisplay>(getContext(), android.R.layout.simple_list_item_1, null) {

            @Override
            public void convert(GeneralAdapter.ViewHolder holder, DeviceDisplay item, int position) {
                String text = item.toString();
                holder.setText(android.R.id.text1, text);
            }
        };
        mPlayersListView.setAdapter(mPlayerListAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UPnPDirectoryActivity) {
            mMainActivity = (UPnPDirectoryActivity) context;
        }
    }


    AdapterView.OnItemClickListener iOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
            DeviceDisplay display = (DeviceDisplay) mPlayerListAdapter.getItem(position);
            Debug.anchor(display);

            mMainActivity.setDevices(display);
        }
    };

    @Override
    public void onDetach() {
        super.onDetach();
        mMainActivity = null;
    }

    public void add(DeviceDisplay device) {

    }


    public void clear() {
        mPlayerListAdapter.clear();

    }
}
