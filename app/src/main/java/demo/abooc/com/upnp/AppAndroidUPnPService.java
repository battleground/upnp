package demo.abooc.com.upnp;

import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.android.AndroidUpnpServiceConfiguration;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.UDAServiceType;

public class AppAndroidUPnPService extends AndroidUpnpServiceImpl {

    @Override
    public void onCreate() {
        super.onCreate();
    }
//
//    @Override
    protected UpnpServiceConfiguration createConfiguration() {
        return new AndroidUpnpServiceConfiguration() {
//            @Override
//            public int getRegistryMaintenanceIntervalMillis() {
//                // 维护时间
//                return 7000;
//            }
//
//            @Override
//            public UpnpHeaders getDescriptorRetrievalHeaders(RemoteDeviceIdentity identity) {
//                if (identity.getUdn().getIdentifierString().equals(UDN_STRING)) {
//                    UpnpHeaders headers = new UpnpHeaders();
//                    headers.add(UpnpHeader.Type.USER_AGENT.getHttpName(), "MyCustom/Agent");
//                    headers.add("X-Custom-Header", "foo");
//                    return headers;
//                }
//                return null;
//            }
//
//            @Override
//            public UpnpHeaders getEventSubscriptionHeaders(RemoteService service) {
//                if (service.getServiceType().implementsVersion(new UDAServiceType("Foo", 1))) {
//                    UpnpHeaders headers = new UpnpHeaders();
//                    headers.add("X-Custom-Header", "bar");
//                    return headers;
//                }
//                return null;
//            }

//            @Override
//            public UpnpHeaders getEventSubscriptionHeaders(RemoteService service) {
//                if (service.getServiceType().implementsVersion(new UDAServiceType("Foo", 1))) {
//                    UpnpHeaders headers = new UpnpHeaders();
//                    headers.add("X-Custom-Header", "bar");
//                    return headers;
//                }
//                return null;
//            }

            @Override
            public ServiceType[] getExclusiveServiceTypes() {
                // 过滤要搜索的服务类型
                return new ServiceType[]{
                        new UDAServiceType("RenderingControl"),
                        new UDAServiceType("AVTransport")
                };
//                return new ServiceType[0];
            }
        };
    }

}