//package org.upnp;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.net.wifi.WifiManager;
//import android.os.CountDownTimer;
//
//import com.abooc.util.Debug;
//
///**
// * Created by author:李瑞宇
// * email:allnet@live.cn
// * on 16/7/14.
// */
//public class WifiReceiver extends BroadcastReceiver {
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        Debug.anchor(intent.toString());
//        if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
//            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);
//
//            switch (wifiState) {
//                case WifiManager.WIFI_STATE_DISABLING:
//                    Debug.anchor("WiFi 关闭中...");
//                    break;
//                case WifiManager.WIFI_STATE_DISABLED:
//                    Debug.anchor("WiFi 不可用");
////                    Debug.anchor(mUPnPService.getRegistry().getDevices());
//
////                    unbindServer();
//
////                        if (closeYes) {
////                            closeYes = false;
////                            openYes = true;
////
////                            deviceList.clear();
////                            if (mOnDiscoverListener != null) {
////                                mOnDiscoverListener.onDiscoveryChanged(null);
////                            }
////                            Debug.anchor("停止扫描");
////                            timer.cancel();
////                            stop();
////                        }
//
//                    timer.cancel();
//                    break;
//                case WifiManager.WIFI_STATE_ENABLING:
//                    Debug.anchor("WiFi 开启...");
//                    break;
//                case WifiManager.WIFI_STATE_ENABLED:
//                    Debug.anchor("WiFi 可用");
//
//                    timer.cancel();
//                    timer.start();
//
////                        if (openYes) {
////                            openYes = false;
////                            closeYes = true;
////
////                            if (!running) {
////                                Debug.anchor("扫描程序即将启动...");
////                                timer.start();
////                            }
////                        }
//                    break;
//            }
//        }
//
//    }
//
//
//    private CountDownTimer timer = new CountDownTimer(4 * 1000, 1000) {
//        @Override
//        public void onTick(long millisUntilFinished) {
//            Debug.anchor("扫描程序即将启动..." + (millisUntilFinished / 1000));
//        }
//
//        @Override
//        public void onFinish() {
//            Debug.anchor("开始检索设备...");
////            bindServer();
//        }
//    };
//}
