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

import com.abooc.dlna.media.AppRootContainer;
import com.abooc.dlna.media.dlna.UPnPBrowse;
import com.abooc.upnp.model.DeviceDisplay;
import com.abooc.util.Debug;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.container.Container;

public class LibrariesFragment extends Fragment {

    ListView mLibrariesListView;
    GeneralAdapter mLibrariesListAdapter;
    private UPnPDirectoryActivity mMainActivity;


    Handler mLibrariesHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            DeviceDisplay dd = (DeviceDisplay) msg.obj;
            switch (msg.what) {
                case HandlerWhat.ADD:
                    int p = mLibrariesListAdapter.getPosition(dd);
                    if (p >= 0) {
                        // Device already in the list, re-set new value at same
                        // position
                        mLibrariesListAdapter.remove(dd);
                        mLibrariesListAdapter.insert(dd, p);
                    } else {
                        mLibrariesListAdapter.add(dd);
                    }
                    break;
                case HandlerWhat.REMOVE:
                    mLibrariesListAdapter.remove(dd);
                    break;
            }
        }
    };

    public Handler getHandler() {
        return mLibrariesHandler;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_libraries, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mLibrariesListView = (ListView) view.findViewById(R.id.list);
        mLibrariesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
                DeviceDisplay display = (DeviceDisplay) mLibrariesListAdapter.getItem(position);
                Device device = display.getOriginDevice();
                Debug.anchor(display);

                Service mContentDirectoryService = device.findService(new UDAServiceType("ContentDirectory"));
                UPnPBrowse.get().build(mContentDirectoryService);
//                if (device instanceof LocalDevice) {
//                    Container 视频 = AppRootContainer.createContainer(AppRootContainer.VIDEO_ID, AppRootContainer.ROOT_ID, "视频");
//                    Container 音乐 = AppRootContainer.createContainer(AppRootContainer.AUDIO_ID, AppRootContainer.ROOT_ID, "音乐");
//                    Container 图片 = AppRootContainer.createContainer(AppRootContainer.IMAGE_ID, AppRootContainer.ROOT_ID, "图片");
//
//                    mContentAdapter.clear();
//                    mContentAdapter.add(视频);
//                    mContentAdapter.add(音乐);
//                    mContentAdapter.add(图片);
//                } else {
                if (mContentDirectoryService != null) {
                    Container rootContainer = AppRootContainer.getInstance();
                    UPnPBrowse.get().browse(mContentDirectoryService, rootContainer.getId(), BrowseFlag.DIRECT_CHILDREN);
                } else {
                    Debug.error();
                }
//                }
            }
        });

        mLibrariesListAdapter = new GeneralAdapter<DeviceDisplay>(getContext(), android.R.layout.simple_list_item_1, null) {

            @Override
            public void convert(GeneralAdapter.ViewHolder holder, DeviceDisplay item, int position) {
                String text = item.toString();
                holder.setText(android.R.id.text1, text);
            }
        };
        mLibrariesListView.setAdapter(mLibrariesListAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UPnPDirectoryActivity) {
            mMainActivity = (UPnPDirectoryActivity) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mMainActivity = null;
    }

}
