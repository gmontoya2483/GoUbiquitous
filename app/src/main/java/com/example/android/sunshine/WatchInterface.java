package com.example.android.sunshine;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.sunshine.data.WeatherContract;
import com.example.android.sunshine.utilities.SunshineDateUtils;
import com.example.android.sunshine.utilities.SunshineWeatherUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;


/**
 * Created by montoya on 24.03.2017.
 */

public class WatchInterface implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    private static final String LOG_TAG=WatchInterface.class.getSimpleName();

    public static final String NOTIFICATION_PATH="/wear_face";
    public static final String HIGH_TEMP_KEY="high_temp";
    public static final String LOW_TEMP_KEY="low_temp";
    public static final String WEATHER_ID_KEY="weather_id";


    public static final float DEFAULT_HIGH_TEMP=999;
    public static final float DEFAULT_LOW_TEMP=999;
    public static final int DEFAULT_WEATHER_ID=999;

    private static final String[] WEATHER_NOTIFICATION_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
    };

    private static final int INDEX_WEATHER_ID = 0;
    private static final int INDEX_MAX_TEMP = 1;
    private static final int INDEX_MIN_TEMP = 2;


    private GoogleApiClient mGoogleApiClient;
    private Context mContext;
    private double mHighTemp=DEFAULT_HIGH_TEMP;
    private double mLowTemp=DEFAULT_LOW_TEMP;
    private int mWeatherIcon=DEFAULT_WEATHER_ID;




    public WatchInterface(Context context) {


        mContext=context;
        getLastWeatherInformation();

    }


    private boolean sendNotificationToWearable(String highTemp, String lowTemp, int weatherID){

        final boolean[] result = {false};


        mGoogleApiClient=new GoogleApiClient.Builder(mContext)
                .addApi(Wearable.API)
                .addConnectionCallbacks (this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();




        PutDataMapRequest putDataMapRequest=PutDataMapRequest.create(NOTIFICATION_PATH);

        putDataMapRequest.getDataMap().putString(HIGH_TEMP_KEY,highTemp);
        putDataMapRequest.getDataMap().putString(LOW_TEMP_KEY,lowTemp);
        putDataMapRequest.getDataMap().putInt(WEATHER_ID_KEY,weatherID);

        PutDataRequest request=putDataMapRequest.asPutDataRequest();
        Wearable.DataApi.putDataItem(mGoogleApiClient,request)
                .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
                        if(!dataItemResult.getStatus().isSuccess()){
                            //Fail to send
                            Log.e(LOG_TAG,mContext.getString(R.string.err_sent_to_wearable));
                            result[0] =false;

                        }else{
                            //Sent
                            Log.i(LOG_TAG,mContext.getString(R.string.sent_to_wearable));
                            result[0] =true;

                        }

                    }
                });

        return result[0];
    }





    private void saveLastSentValues(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat(HIGH_TEMP_KEY,(float) mHighTemp);
        editor.putFloat(LOW_TEMP_KEY,(float) mHighTemp);
        editor.putInt(WEATHER_ID_KEY,mWeatherIcon);
        editor.apply();

    }

    //This method is invoqued SunshineSyncTask to check if the weather condition has changed
    public boolean weatherHasChanged(){

        float savedHighTemp;
        float savedLowTemp;
        int savedWeatherId;

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        savedHighTemp=sp.getFloat(HIGH_TEMP_KEY,DEFAULT_HIGH_TEMP);
        savedLowTemp=sp.getFloat(LOW_TEMP_KEY,DEFAULT_LOW_TEMP);
        savedWeatherId=sp.getInt(WEATHER_ID_KEY,DEFAULT_WEATHER_ID);

        if (savedHighTemp!=(float) mHighTemp){
            return true;
        }else if (savedLowTemp!=(float) mLowTemp){
            return true;
        }else if(savedWeatherId!=mWeatherIcon){
            return true;
        }else{
            return false;
        }


    }


    //This method is invoked SunshineSyncTask to trigger the notification
    public boolean notifyWearable(){

        long highTemp;
        long lowTemp;
        String formattedHigh;
        String formattedLow;

        boolean result;


        //Format the temperatures before bein sent to the wearable
        highTemp=Math.round(mHighTemp);
        lowTemp=Math.round(mLowTemp);

        formattedHigh= SunshineWeatherUtils.formatTemperature(mContext,highTemp);
        formattedLow= SunshineWeatherUtils.formatTemperature(mContext,lowTemp);

        if (sendNotificationToWearable(formattedHigh,formattedLow,mWeatherIcon)){
            saveLastSentValues();
            result=true;

        }else{
            result=false;
        }

        return result;
    }



    private void getLastWeatherInformation(){

        /* Build the URI for today's weather in order to show up to date data in notification */
        Uri todaysWeatherUri = WeatherContract.WeatherEntry
                .buildWeatherUriWithDate(SunshineDateUtils.normalizeDate(System.currentTimeMillis()));

        Cursor todayWeatherCursor = mContext.getContentResolver().query(
                todaysWeatherUri,
                WEATHER_NOTIFICATION_PROJECTION,
                null,
                null,
                null);

        if (todayWeatherCursor.moveToFirst()) {

            /* Weather ID as returned by API, used to identify the icon to be used */
            mWeatherIcon = todayWeatherCursor.getInt(INDEX_WEATHER_ID);
            mHighTemp = todayWeatherCursor.getDouble(INDEX_MAX_TEMP);
            mLowTemp = todayWeatherCursor.getDouble(INDEX_MIN_TEMP);
        }

        todayWeatherCursor.close();

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
