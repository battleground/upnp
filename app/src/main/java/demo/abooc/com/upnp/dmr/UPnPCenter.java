package demo.abooc.com.upnp.dmr;

import android.os.Build;

import com.abooc.util.Debug;

import org.fourthline.cling.binding.LocalServiceBindingException;
import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.binding.annotations.UpnpAction;
import org.fourthline.cling.binding.annotations.UpnpInputArgument;
import org.fourthline.cling.binding.annotations.UpnpOutputArgument;
import org.fourthline.cling.binding.annotations.UpnpService;
import org.fourthline.cling.binding.annotations.UpnpServiceId;
import org.fourthline.cling.binding.annotations.UpnpServiceType;
import org.fourthline.cling.binding.annotations.UpnpStateVariable;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.ManufacturerDetails;
import org.fourthline.cling.model.meta.ModelDetails;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDN;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.URI;

/**
 * Created by dayu on 2017/5/4.
 */

public class UPnPCenter {

    public static final String UDN_STRING = UDNBuilder.UDN_STRING;

    public static LocalDevice createDevice(String udnString)
            throws ValidationException, LocalServiceBindingException {

        DeviceType type = new UDADeviceType("MediaRenderer");

        DeviceDetails details =
                new DeviceDetails(
                        Build.BRAND + " " + Build.MODEL,
                        new ManufacturerDetails("DLNA/UPnP/Cling"),
                        new ModelDetails("Abooc Inc.", "www.abooc.com", "v1"),
                        URI.create("http://abooc.com")
                );

        LocalService RenderingControlService = new AnnotationLocalServiceBinder().read(RenderingControl.class);
        LocalService AVTransportService = new AnnotationLocalServiceBinder().read(AVTransport.class);
        LocalService ConnectionManagerService = new AnnotationLocalServiceBinder().read(ConnectionManager.class);
//        RenderingControlService.setManager(new DefaultServiceManager<>(RenderingControlService, RenderingControl.class));
//        AVTransportService.setManager(new DefaultServiceManager<>(AVTransportService, AVTransport.class));

        LocalService[] services = {
                RenderingControlService,
                AVTransportService,
                ConnectionManagerService
        };


        return new LocalDevice(
                new DeviceIdentity(UDN.valueOf(udnString)),
                type,
                details,
//                createDefaultDeviceIcon(),
                services
        );
    }

    @UpnpService(
            serviceId = @UpnpServiceId("RenderingControl"),
            serviceType = @UpnpServiceType(value = "RenderingControl", version = 1)
    )
    public class RenderingControl {

        private final PropertyChangeSupport propertyChangeSupport;

        public RenderingControl() {
            this.propertyChangeSupport = new PropertyChangeSupport(this);
        }

        public PropertyChangeSupport getPropertyChangeSupport() {
            return propertyChangeSupport;
        }

        @UpnpStateVariable(defaultValue = "0", sendEvents = false)
        private boolean target = false;

        @UpnpStateVariable(defaultValue = "0")
        private boolean status = false;

        @UpnpAction
        public void setTarget(@UpnpInputArgument(name = "NewTargetValue") boolean newTargetValue) {
            boolean targetOldValue = target;
            target = newTargetValue;
            boolean statusOldValue = status;
            status = newTargetValue;

            // These have no effect on the UPnP monitoring but it's JavaBean compliant
            getPropertyChangeSupport().firePropertyChange("target", targetOldValue, target);
            getPropertyChangeSupport().firePropertyChange("status", statusOldValue, status);

            // This will send a UPnP event, it's the name of a state variable that sends events
            getPropertyChangeSupport().firePropertyChange("Status", statusOldValue, status);
        }

        @UpnpAction(out = @UpnpOutputArgument(name = "RetTargetValue"))
        public boolean getTarget() {
            return target;
        }

        @UpnpAction(out = @UpnpOutputArgument(name = "ResultStatus"))
        public boolean getStatus() {
            return status;
        }
    }

    @UpnpService(
            serviceId = @UpnpServiceId("AVTransport"),
            serviceType = @UpnpServiceType(value = "AVTransport", version = 1)
    )
    public class AVTransport {

        private final PropertyChangeSupport propertyChangeSupport;

        public AVTransport() {
            this.propertyChangeSupport = new PropertyChangeSupport(this);
            propertyChangeSupport.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    Debug.error();
                }
            });
        }

        public PropertyChangeSupport getPropertyChangeSupport() {
            return propertyChangeSupport;
        }

        @UpnpStateVariable(defaultValue = "0", sendEvents = false)
        private boolean target = false;

        @UpnpStateVariable(defaultValue = "0")
        private boolean status = false;

        @UpnpAction
        public void setTarget(@UpnpInputArgument(name = "NewTargetValue") boolean newTargetValue) {
            boolean targetOldValue = target;
            target = newTargetValue;
            boolean statusOldValue = status;
            status = newTargetValue;

            // These have no effect on the UPnP monitoring but it's JavaBean compliant
            getPropertyChangeSupport().firePropertyChange("target", targetOldValue, target);
            getPropertyChangeSupport().firePropertyChange("status", statusOldValue, status);

            // This will send a UPnP event, it's the name of a state variable that sends events
            getPropertyChangeSupport().firePropertyChange("Status", statusOldValue, status);
        }

        @UpnpAction(out = @UpnpOutputArgument(name = "RetTargetValue"))
        public boolean getTarget() {
            return target;
        }

        @UpnpAction(out = @UpnpOutputArgument(name = "ResultStatus"))
        public boolean getStatus() {
            return status;
        }
    }

    @UpnpService(
            serviceId = @UpnpServiceId("ConnectionManager"),
            serviceType = @UpnpServiceType(value = "ConnectionManager", version = 1)
    )
    public class ConnectionManager {

        private final PropertyChangeSupport propertyChangeSupport;

        public ConnectionManager() {
            this.propertyChangeSupport = new PropertyChangeSupport(this);
        }

        public PropertyChangeSupport getPropertyChangeSupport() {
            return propertyChangeSupport;
        }

        @UpnpStateVariable(defaultValue = "0", sendEvents = false)
        private boolean target = false;

        @UpnpStateVariable(defaultValue = "0")
        private boolean status = false;

        @UpnpAction
        public void setTarget(@UpnpInputArgument(name = "NewTargetValue") boolean newTargetValue) {
            boolean targetOldValue = target;
            target = newTargetValue;
            boolean statusOldValue = status;
            status = newTargetValue;

            // These have no effect on the UPnP monitoring but it's JavaBean compliant
            getPropertyChangeSupport().firePropertyChange("target", targetOldValue, target);
            getPropertyChangeSupport().firePropertyChange("status", statusOldValue, status);

            // This will send a UPnP event, it's the name of a state variable that sends events
            getPropertyChangeSupport().firePropertyChange("Status", statusOldValue, status);
        }

        @UpnpAction(out = @UpnpOutputArgument(name = "RetTargetValue"))
        public boolean getTarget() {
            return target;
        }

        @UpnpAction(out = @UpnpOutputArgument(name = "ResultStatus"))
        public boolean getStatus() {
            return status;
        }
    }

}
