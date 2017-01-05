package com.example.android.sunshine.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class WeatherListenerService
        extends WearableListenerService
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, DataApi.DataListener {

    public final String LOG_TAG = WeatherListenerService.class.getSimpleName();

    static final String KEY_PREFIX = "/wearable_ready";
    static final String KEY_WEATHER_ID = "KEY_WEATHER_ID";
    static final String KEY_MIN_TEMP = "KEY_MIN_TEMP";
    static final String KEY_MAX_TEMP = "KEY_MAX_TEMP";

    public static final String ACTION_WEATHER_CHANGED = "ACTION_WEATHER_CHANGED";

    private String mNodeId;

    GoogleApiClient mGoogleApiClient;



    @Override
    public void onCreate() {
        super.onCreate();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent dataEvent : dataEvents) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = dataEvent.getDataItem();
                if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    String mMaxTemp = dataMap.getString(KEY_MAX_TEMP);
                    String mMinTemp = dataMap.getString(KEY_MIN_TEMP);
                    String mWeatherId = dataMap.getString(KEY_WEATHER_ID);
                    Intent intent = new Intent("ACTION_WEATHER_CHANGED");
                    intent.putExtra("high-temp", mMaxTemp)
                            .putExtra("low-temp", mMinTemp)
                            .putExtra("sunshine_weather_id", mWeatherId);
                    sendBroadcast(intent);
                }
            } else if (dataEvent.getType() == DataEvent.TYPE_DELETED) {
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Wearable.DataApi.addListener(mGoogleApiClient, this);
        retrieveDeviceNode();
       // sendWearableReadyMessage();
    }

    public void sendWearableReadyMessage() {
        if (mNodeId != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                }
            }).start();
        }
    }

    private void retrieveDeviceNode() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult result =
                        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();

                List<Node> nodes = result.getNodes();

                if (nodes.size() > 0)
                    mNodeId = nodes.get(0).getId();


                Wearable.MessageApi.sendMessage(mGoogleApiClient, mNodeId, KEY_PREFIX, null);
                Log.v(LOG_TAG, "Node ID of phone: " + mNodeId);

            }
        }).start();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(LOG_TAG, "Google API Client was suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.connect();
        Log.d(LOG_TAG, "Google API Client failed");
    }
}