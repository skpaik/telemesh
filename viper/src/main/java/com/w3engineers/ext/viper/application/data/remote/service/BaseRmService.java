/*
package com.w3engineers.ext.viper.application.data.remote.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.w3engineers.ext.strom.util.Text;
import com.w3engineers.ext.viper.IRmCommunicator;
import com.w3engineers.ext.viper.IRmServiceConnection;
import com.w3engineers.ext.viper.R;
import com.w3engineers.ext.viper.application.data.remote.model.BaseMeshData;
import com.w3engineers.ext.viper.application.data.remote.model.MeshAcknowledgement;
import com.w3engineers.ext.viper.application.data.remote.model.MeshData;
import com.w3engineers.ext.viper.application.data.remote.model.MeshPeer;
import com.w3engineers.ext.viper.util.lib.mesh.IMeshCallBack;
import com.w3engineers.ext.viper.util.lib.mesh.MeshConfig;
import com.w3engineers.ext.viper.util.lib.mesh.MeshProviderOld;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;


*/
/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 *//*

// TODO: 8/8/2018 Should override Baservice to stop services properly???
public class BaseRmService extends Service implements IMeshCallBack {

    private List<IRmCommunicator> mIRmCommunicators;
    private MeshProviderOld mMeshProvider;
    private boolean mIsServiceToCloseWithTask;
    private boolean mIsTaskCleared;
    private String mBroadcastActionString;

    @Override
    public void onCreate() {
        super.onCreate();
        mIRmCommunicators = new ArrayList<>();
        mBroadcastActionString = getApplicationContext().getPackageName();

        initMesh();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent != null) {
            String action = intent.getAction();

            if(Text.isNotEmpty(action)) {

                switch (action) {
                    case BaseRmServiceNotificationHelper.ACTION_STOP_SERVICE:

                        shutTheService();

                        break;
                }

            }
        }

        return START_STICKY;
    }

    private void initMesh() {
        MeshConfig meshConfig = new MeshConfig();

        //Please do not modify here, this is an auto generated property from developers
        //local.properties class
        meshConfig.mSsid = getString(R.string.rm_ssid);
        meshConfig.mSuperPeer = getString(R.string.rm_super_peer);
        meshConfig.mPort = getResources().getInteger(R.integer.rm_port_number);
        Timber.d("Local Remote Service initiating with port number:%d ssid:%s", meshConfig.mPort,
                meshConfig.mSsid);

        mMeshProvider = MeshProviderOld.getInstance();
        mMeshProvider.setMeshConfig(meshConfig);
        mMeshProvider.setIMeshCallBack(this);
        mMeshProvider.start(getApplicationContext());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //Any task is available now as connection is being established
        mIsTaskCleared = false;
        return mIRmServiceConnection;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        //Any task is available now as connection is being established
        mIsTaskCleared = false;
    }

    private final IRmServiceConnection.Stub mIRmServiceConnection = new IRmServiceConnection.Stub() {

        @Override
        public List<BaseMeshData> getLivePeers() {
            return mMeshProvider.getLivePeers();
        }

        @Override
        public void setBroadCastActionString(String actionString) throws RemoteException {
            if(Text.isNotEmpty(actionString)) {
                mBroadcastActionString = actionString;
            }
        }

        @Override
        public void setServiceToCloseWithTask(boolean isToCloseWithTask) throws RemoteException {
            mIsServiceToCloseWithTask = isToCloseWithTask;
        }

        @Override
        public void setProfile(byte[] profileInfo, String userId) throws RemoteException {
            if(profileInfo != null) {
                mMeshProvider.setProfileInfo(profileInfo);
            }
        }

        @Override
        public void setProfileInfo(byte[] profileInfo) throws RemoteException {

        }

        @Override
        public long sendMeshData(MeshData meshData) throws RemoteException {
            return mMeshProvider.sendData(meshData);
        }

        @Override
        public void setRmCommunicator(IRmCommunicator iRmCommunicator) throws RemoteException {

            if(iRmCommunicator != null) {
                //Any task is available now as connection is being established
                mIsTaskCleared = false;

                mIRmCommunicators.add(iRmCommunicator);
            }

        }

        @Override
        public void setServiceForeground(boolean isForeGround) throws RemoteException {

            if(isForeGround) {
                new BaseRmServiceNotificationHelper(BaseRmService.this).startForegroundService();
            } else {
                new BaseRmServiceNotificationHelper(BaseRmService.this).stopForegroundService();
            }
        }

        @Override
        public void resetCommunicator(IRmCommunicator iRmCommunicator) throws RemoteException {
            mIRmCommunicators.clear();
        }
        @Override
        public void openRmSettings(){
            mMeshProvider.openRmSettings();
        }

        @Override
        public void stopRmService() throws RemoteException {
            shutTheService();
        }

        @Override
        public void stopMeshProcess() throws RemoteException {

        }

        @Override
        public String getMyId() throws RemoteException {
            return null;
        }
    };

    @Override
    public void onMesh(MeshData meshData) {

        */
/*if (mIsTaskCleared) {//No task is there need to broadcast data

            Intent intent = new Intent(mBroadcastActionString);
            intent.putExtra(getString(R.string.mesh_data), meshData);
            BroadcastUtil.sendUniCast(getApplicationContext(), intent);

        } else {

            for (IRmCommunicator mIRmCommunicator : mIRmCommunicators) {
                if (mIRmCommunicator != null) {
                    try {
                        mIRmCommunicator.onMeshData(meshData);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }*//*


        for(IRmCommunicator iRmCommunicator : mIRmCommunicators) {

            if(iRmCommunicator != null) {
                try {
                    iRmCommunicator.onMeshData(meshData);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void onMesh(MeshAcknowledgement meshAcknowledgement) {

        for(IRmCommunicator iRmCommunicator : mIRmCommunicators) {

            if(iRmCommunicator != null) {
                try {
                    iRmCommunicator.onMeshAcknowledgement(meshAcknowledgement);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

        }

        */
/*for (IRmCommunicator mIRmCommunicator : mIRmCommunicators) {
            if (mIRmCommunicator != null) {
                try {
                    mIRmCommunicator.onMeshAcknowledgement(meshAcknowledgement);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }*//*

    }

    @Override
    public void onProfileInfo(BaseMeshData baseMeshData) {

        for (IRmCommunicator mIRmCommunicator : mIRmCommunicators) {
            if (mIRmCommunicator != null) {
                try {
                    mIRmCommunicator.onProfileInfo(baseMeshData);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onPeerRemoved(MeshPeer meshPeer) {

        for (IRmCommunicator mIRmCommunicator : mIRmCommunicators) {
            if (mIRmCommunicator != null) {
                try {
                    mIRmCommunicator.onPeerRemoved(meshPeer);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onInitSuccess(MeshPeer selfMeshPeer) {

        for (IRmCommunicator mIRmCommunicator : mIRmCommunicators) {
            if (mIRmCommunicator != null) {
                try {
                    mIRmCommunicator.onLibraryInitSuccess();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onInitFailed(int reason) {
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        */
/*if(mIsServiceToCloseWithTask) {
            shutTheService();
        }

        mIsTaskCleared = true;*//*

    }

    private void shutTheService() {

        //Removing notification if present
        new BaseRmServiceNotificationHelper(this).stopForegroundService();

        //Stopping mesh
        if (mMeshProvider != null) {
            mMeshProvider.stop();
            mMeshProvider = null;
        }

        //Stopping service

        //Sending call back to app layer so that corresponding process can be cleared
        for (IRmCommunicator mIRmCommunicator : mIRmCommunicators) {
            if (mIRmCommunicator != null) {
                try {
                    mIRmCommunicator.onServiceDestroy();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        stopSelf();
        Process.killProcess(Process.myPid());
    }
}
*/
