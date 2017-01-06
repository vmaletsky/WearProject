package com.example.android.sunshine.app.sync;

import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by v.maletskiy on 06.01.2017.
 */
public class ListenerService extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        SunshineSyncAdapter.syncImmediately(this);
    }
}
