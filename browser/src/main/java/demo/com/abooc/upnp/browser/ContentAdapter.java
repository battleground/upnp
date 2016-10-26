package demo.com.abooc.upnp.browser;

import android.content.Context;

import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.item.AudioItem;
import org.fourthline.cling.support.model.item.ImageItem;
import org.fourthline.cling.support.model.item.Movie;
import org.fourthline.cling.support.model.item.MusicTrack;
import org.fourthline.cling.support.model.item.Photo;
import org.fourthline.cling.support.model.item.VideoItem;

import java.util.List;

class ContentAdapter extends GeneralAdapter<DIDLObject> {

    public ContentAdapter(Context ctx, int resource, List<DIDLObject> data) {
        super(ctx, resource, data);
    }

    @Override
    public void convert(ViewHolder holder, DIDLObject item, int position) {
        if (item instanceof Container) {
            holder.setImageResource(R.id.iv_icon,
                    R.drawable.file_folder_icon);
        } else {
            String fileUri = item.getFirstResource().getValue();

            if (item instanceof VideoItem
                    || item instanceof Movie) {
                holder.setImageResource(R.id.iv_icon,
                        R.drawable.file_video_icon);
            }
            if (item instanceof AudioItem
                    || item instanceof MusicTrack) {
                holder.setImageResource(R.id.iv_icon,
                        R.drawable.file_audio_icon);
            }
            if (item instanceof ImageItem
                    || item instanceof Photo) {
                holder.setImageResource(R.id.iv_icon,
                        R.drawable.file_image_icon);
            } else {
                holder.setImageResource(R.id.iv_icon,
                        R.drawable.file_other_icon);
            }
        }
        holder.setText(R.id.tv_title, item.getTitle());
    }

}