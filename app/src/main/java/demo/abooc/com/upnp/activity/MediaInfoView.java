package demo.abooc.com.upnp.activity;

import android.view.View;
import android.widget.TextView;

import demo.abooc.com.upnp.R;

/**
 * Created by author:李瑞宇
 * email:allnet@live.cn
 * on 16/10/20.
 */

public class MediaInfoView {

    private View rootView;

    private TextView mIDText;
    private TextView mParentIDText;
    private TextView mRefIDText;

    private TextView mStateText;
    private TextView mTitleText;
    private TextView mCreatorText;
    private TextView mDateText;
    private TextView mUrlText;
    private TextView mSpeedText;
    private TextView mMetadataText;

    public MediaInfoView(View root) {
        rootView = root;

        mIDText = (TextView) root.findViewById(R.id.ID);
        mParentIDText = (TextView) root.findViewById(R.id.ParentID);
        mRefIDText = (TextView) root.findViewById(R.id.RefID);

        mStateText = (TextView) root.findViewById(R.id.TransportState);
        mTitleText = (TextView) root.findViewById(R.id.Title);
        mCreatorText = (TextView) root.findViewById(R.id.Creator);
        mDateText = (TextView) root.findViewById(R.id.Date);
        mUrlText = (TextView) root.findViewById(R.id.Url);
        mSpeedText = (TextView) root.findViewById(R.id.Speed);
        mMetadataText = (TextView) root.findViewById(R.id.METADATA);
    }

    public void setVisibility(int visibility) {
        rootView.setVisibility(visibility);
    }

    public void setID(String id) {
        mIDText.setText(id);

    }

    public void setParentID(String parentID) {
        mParentIDText.setText(parentID);

    }

    public void setRefID(String refID) {
        mRefIDText.setText(refID);

    }

    public void setState(String state) {
        mStateText.setText(state);
    }

    public void setTitle(String title) {
        int length = title.length();
        if (length <= 16) {
            mTitleText.setTextSize(22);
        } else if (length > 16 && length <= 30) {
            mTitleText.setTextSize(16);
        } else if (length >= 30) {
            mTitleText.setTextSize(14);
        }
        mTitleText.setText(title);
    }

    public void setCreatorText(String creator) {
        mCreatorText.setText(creator);

    }

    public void setDateText(String date) {
        mDateText.setText(date);

    }

    public void setUrlText(String url) {
        mUrlText.setText(url);

    }

    public void setSpeedText(String speed) {
        mSpeedText.setText(speed);

    }

    public void setMetadataText(String metadata) {

        metadata = metadata.replace(">", ">\n");

        mMetadataText.setText(metadata);

    }

    public void clear() {
        mIDText.setText(null);
        mParentIDText.setText(null);
        mRefIDText.setText(null);

        mStateText.setText(null);
        mTitleText.setText(null);
        mCreatorText.setText(null);
        mDateText.setText(null);
        mUrlText.setText(null);
        mSpeedText.setText(null);
        mMetadataText.setText(null);

    }

}
