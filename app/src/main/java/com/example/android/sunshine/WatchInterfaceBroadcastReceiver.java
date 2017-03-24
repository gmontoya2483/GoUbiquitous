package com.example.android.sunshine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;


/**
 * Created by montoya on 24.03.2017.
 */

public class WatchInterfaceBroadcastReceiver extends BroadcastReceiver implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;

    @Override
    public void onReceive(Context context, Intent intent) {


        mGoogleApiClient=new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .addConnectionCallbacks (this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();

        //ToDO compare with this -> https://github.com/passiondroid/SunshineApp/tree/master
        //TODO create a shared preference value with the new temperarures and icon id


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
