package demo.abooc.com.upnp.dmr;

import android.os.Build;

import org.fourthline.cling.binding.LocalServiceBindingException;
import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.model.DefaultServiceManager;
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
import org.fourthline.cling.support.avtransport.lastchange.AVTransportLastChangeParser;
import org.fourthline.cling.support.connectionmanager.ConnectionManagerService;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.support.lastchange.LastChangeAwareServiceManager;
import org.fourthline.cling.support.renderingcontrol.lastchange.RenderingControlLastChangeParser;

import java.net.URI;

/**
 * Created by dayu on 2017/5/4.
 */

public class UPnPCenter2 {

    // These are shared between all "logical" player instances of a single service
    final protected static LastChange avTransportLastChange = new LastChange(new AVTransportLastChangeParser());
    final protected LastChange renderingControlLastChange = new LastChange(new RenderingControlLastChangeParser());

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

        AnnotationLocalServiceBinder serviceBinder = new AnnotationLocalServiceBinder();

        LocalService connectionManagerService = serviceBinder.read(ConnectionManager.class);
        connectionManagerService.setManager(new DefaultServiceManager<>(connectionManagerService, ConnectionManager.class));


        LocalService<AVTransportService> avTransportService = serviceBinder.read(AVTransportService.class);
        LastChangeAwareServiceManager avTransportManager =
                new LastChangeAwareServiceManager<AVTransportService>(
                        avTransportService,
                        new AVTransportLastChangeParser()) {
                    @Override
                    protected AVTransportService createServiceInstance() throws Exception {
                        return new AVTransportService(avTransportLastChange);
                    }
                };
        avTransportService.setManager(avTransportManager);

        LocalService renderingControlService = serviceBinder.read(RenderingControl.class);
        DefaultServiceManager<RenderingControl> rcManager = new DefaultServiceManager<>(renderingControlService, RenderingControl.class);
        renderingControlService.setManager(rcManager);


        LocalService[] services = {
                renderingControlService,
                avTransportService,
                connectionManagerService
        };

        return new LocalDevice(
                new DeviceIdentity(UDN.valueOf(udnString)),
                type,
                details,
                services
        );
    }

    public class ConnectionManager extends ConnectionManagerService {

    }

}
