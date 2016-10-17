package demo.abooc.com.upnp.model;

import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.item.VideoItem;

public class VRVideoItem extends VideoItem {
    public VRVideoItem(int vType, String id, String parentID, String title, String creator, Res... resource) {
        super(id, parentID, title, creator, resource);
        setType(String.valueOf(vType));
        setClazz(CLASS);
    }

    public VRVideoItem setType(String vrType) {
        addProperty(new TYPE(vrType));
        return this;
    }


    static public class TYPE extends DIDLObject.Property<String> implements DIDLObject.Property.DC.NAMESPACE {
        public TYPE(String value) {
            super(value, null);
        }
    }
}