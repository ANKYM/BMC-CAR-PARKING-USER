package com.omkar.bmccarparkinguser.Helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class ConnectionDetector {

    //region comment code
//    private Context _context;
//    public static boolean isInternetConnection(Context _context) {
//
//        boolean connected;
//        ConnectivityManager connectivityManager = (ConnectivityManager) _context.getSystemService(_context.CONNECTIVITY_SERVICE);
//        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
//                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
//            connected = true;
//        } else {
//            connected = false;
//        }
//        return connected;
//    }
    //endregion
    public static boolean isInternetConnection(Context _context) {
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }
}
