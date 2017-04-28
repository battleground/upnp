package demo.abooc.com.upnp;

import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.android.AndroidUpnpServiceConfiguration;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;

public class AppAndroidUPnPService extends AndroidUpnpServiceImpl {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected UpnpServiceConfiguration createConfiguration() {
        return new AndroidUpnpServiceConfiguration() {
            @Override
            public int getRegistryMaintenanceIntervalMillis() {
                // 维护时间
                return 7000;
            }

//			@Override
//			public ServiceType[] getExclusiveServiceTypes() {
//				// 过滤要搜索的服务类型
//				return new ServiceType[] { new UDAServiceType("RenderingControl", 1) };
//			}
        };
    }

}