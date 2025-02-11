package com.w3engineers.unicef.util.helper;
 
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.w3engineers.ext.strom.App;
import com.w3engineers.unicef.telemesh.data.analytics.AnalyticsApi;
import com.w3engineers.unicef.telemesh.data.analytics.AnalyticsDataHelper;
import com.w3engineers.unicef.telemesh.data.broadcast.Util;
import com.w3engineers.unicef.telemesh.data.helper.RmDataHelper;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;

import java.io.File;

public class BulletinTimeScheduler {

    @SuppressLint("StaticFieldLeak")
    private static BulletinTimeScheduler bulletinTimeScheduler = new BulletinTimeScheduler();
    private Context context;
    protected int DEFAULT = 0, WIFI = 1, DATA = 2, AP = 3;

    private BulletinTimeScheduler() {
        context = App.getContext();
    }

    @NonNull
    public static BulletinTimeScheduler getInstance() {
        return bulletinTimeScheduler;
    }

    public void connectivityRegister() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(new NetworkCheckReceiver(), intentFilter);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean isMobileDataEnable() {
        int state = getNetworkState();
        if (state == DATA) {
            return true;
        } else {
            Util.cancelJob(context);
            return false;
        }
    }

    protected int getNetworkState() {
        ConnectivityManager connectivitymanager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = connectivitymanager.getAllNetworkInfo();

        for (NetworkInfo netInfo : networkInfo) {

            /*if (netInfo.getTypeName().equalsIgnoreCase("WIFI"))
                if (netInfo.isConnected())
                    return WIFI;*/
            if (netInfo.getTypeName().equalsIgnoreCase("MOBILE"))
                if (netInfo.isConnected())
                    return DATA;
        }
        return 0;
    }

    public class NetworkCheckReceiver extends BroadcastReceiver {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onReceive(@NonNull Context context, @NonNull Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

                boolean noConnectivity = intent.getBooleanExtra(
                        ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

                if (!noConnectivity) {
                    int state = getNetworkState();
                    if (state == DATA) {
                        RmDataHelper.getInstance().sendPendingAck();
                        resetScheduler(context);

                        if (!Constants.IS_LOG_UPLOADING_START) {
                            Constants.IS_LOG_UPLOADING_START = true;
                            RmDataHelper.getInstance().uploadLogFile();
                        }

                    }
                } else {
                    // No action needed
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void resetScheduler(@NonNull Context context) {
        Util.cancelJob(context);
        Util.scheduleJob(context);
    }

    @NonNull
    public NetworkCheckReceiver getReceiver() {
        return new NetworkCheckReceiver();
    }

   /* private void uploadLogFile() {
        Log.d("ParseFileUpload", "Upload file call");
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File(sdCard.getAbsolutePath() +
                "/MeshRnD");
        File[] files = directory.listFiles();

        AnalyticsDataHelper.getInstance().sendLogFileInServer(files[4], "testUser", Constants.getDeviceName());
    }*/
}
