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
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

public class WeatherListenerService
        extends WearableListenerService
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, DataApi.DataListener {

    public final String LOG_TAG = WeatherListenerService.class.getSimpleName();

    public static final String WEATHER_DATA = "/weather-data";
    public static final String HIGH_TEMP = "high-temp";
    public static final String LOW_TEMP = "low-temp";
    public static final String WEATHER_ID = "sunshine_weather_id";

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
        Log.v(LOG_TAG, "DataMap item made it to watch: ");
        Toast.makeText(this, "onDataChanged", Toast.LENGTH_LONG).show();
        for (DataEvent dataEvent : dataEvents) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = dataEvent.getDataItem();
                if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    String mMaxTemp = dataMap.getString(HIGH_TEMP);
                    String mMinTemp = dataMap.getString(LOW_TEMP);
                    String mWeatherId = dataMap.getString(WEATHER_ID);
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
        Toast.makeText(this, "onConnect listener service", Toast.LENGTH_LONG).show();
        Log.d(LOG_TAG, "Google API Client was connected");
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