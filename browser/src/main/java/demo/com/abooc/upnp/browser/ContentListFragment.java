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
import android.widget.TextView;

import com.abooc.dlna.media.dlna.UPnPBrowse;
import com.abooc.upnp.model.DeviceDisplay;
import com.abooc.util.Debug;
import com.abooc.widget.Toast;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.support.contentdirectory.DIDLParser;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.item.Item;

import java.net.URI;

public class ContentListFragment extends Fragment {

    ContentAdapter mContentAdapter;
    ListView mContentListView;
    Container mCurrContainer;
    TextView mFilepathTextView;


    Handler mContentHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HandlerWhat.ADD:
                    DIDLObject item = (DIDLObject) msg.obj;
                    mContentAdapter.add(item);
                    break;
                case HandlerWhat.CLEAR_ALL:
                    mContentAdapter.clear();
                    break;
            }
        }
    };

    public Handler getHandler() {
        return mContentHandler;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    UPnPPresenter mUPnPPresenter;

    public void setPresenter(UPnPPresenter presenter) {
        mUPnPPresenter = presenter;
        presenter.mUPnPBrowse.setUIHandler(getHandler());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        View viewById = view.findViewById(R.id.GoBack);
        viewById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGoBack(v);
            }
        });

        mFilepathTextView = (TextView) view.findViewById(R.id.filepath);
        mContentListView = (ListView) view.findViewById(R.id.list);
        mContentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DIDLObject item = mContentAdapter.getItem(position);
                if (item instanceof Container) {
                    mCurrContainer = (Container) item;

                    Service CDService = UPnPBrowse.get().getService();
                    UPnPBrowse.get().browse(CDService, item.getId(), BrowseFlag.DIRECT_CHILDREN);


                    String path = mFilepathTextView.getText().toString();
                    mFilepathTextView.setText(path + "/" + item.getTitle());
                } else {

                    Res resource = item.getFirstResource();
                    URI importUri = resource.getImportUri();
                    Debug.anchor(item.getTitle() + "\n"
                            + importUri.getPath() + "\n"
                            + resource.getValue());

                    UPnPDirectoryActivity activity = (UPnPDirectoryActivity) getActivity();
                    DeviceDisplay display = activity.getDevice();

                    if (display != null) {
                        Debug.anchor(display);
                        Device device = display.getOriginDevice();

                        String uri = resource.getValue();

                        Debug.anchor(uri);

                        Service service = device.findService(new UDAServiceType("AVTransport"));

                        DIDLContent didlContent = new DIDLContent();
                        didlContent.addItem((Item) item);
                        DIDLParser parser = new DIDLParser();
                        String metadata = "";
                        try {
                            metadata = parser.generate(didlContent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mUPnPPresenter.mUPnPAVTransport.start(service, uri, metadata);

                    } else {
                        Debug.anchor();
                        Toast.show("请选择播放设备");
                    }
                }
            }
        });
        mContentAdapter = new ContentAdapter(getContext(), R.layout.item_content, null);
        mContentAdapter.setMaxCount(200);
        mContentListView.setAdapter(mContentAdapter);
    }


    public void onGoBack(View view) {
        String path = mFilepathTextView.getText().toString();

        if (path.contains("/")) {
            path = path.substring(0, path.lastIndexOf("/"));
            mFilepathTextView.setText(path);

            Service CDService = UPnPBrowse.get().getService();
            UPnPBrowse.get().browse(CDService, mCurrContainer.getParentID(), BrowseFlag.DIRECT_CHILDREN);

        }
    }

    public void clear() {
        mContentAdapter.clear();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
