package com.github.lyue.bmwchallenge;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by yue_liang on 2017/4/6.
 */

public class UpstreamNetworkMonitor {
    private static NetworkMonitor nm;
    public static class NetworkMonitor extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager) context.
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo activeInfo = manager.getActiveNetworkInfo();
            if (activeInfo != null && activeInfo.isConnected()) {
                LocationLoader.get().
                        load("http://localsearch.azurewebsites.net/api/Locations");
            }
        }
    }
    public static void registerNetworkMonitor(Context context) {
        if (nm == null) {
            nm = new NetworkMonitor();
        }
        context.registerReceiver(nm, new IntentFilter("android.net.conn" +
                ".CONNECTIVITY_CHANGE"));
    }
    public static void unregisterNetworkMonitor(Context context) {
        context.unregisterReceiver(nm);
    }
}
