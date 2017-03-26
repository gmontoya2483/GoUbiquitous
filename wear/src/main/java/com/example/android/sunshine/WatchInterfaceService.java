package com.example.android.sunshine;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

public class WatchInterfaceService extends WearableListenerService {

    //Used for the interface
    public static final String NOTIFICATION_PATH="/wear_face";
    public static final String HIGH_TEMP_KEY="high_temp";
    public static final String LOW_TEMP_KEY="low_temp";
    public static final String WEATHER_ID_KEY="weather_id";

    public static final String NOT_FOUND_HIGH_TEMP="ND";
    public static final String NOT_FOUND_LOW_TEMP="ND";
    public static final int NOT_FOUND_WEATHER_ID=999;


    private String mHighTemp;
    private String mLowTemp;
    private int mWeatherId;




    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {


        for (DataEvent dataEvent:dataEventBuffer){
            if (dataEvent.getType()==DataEvent.TYPE_CHANGED){
                DataMap dataMap= DataMapItem.fromDataItem(dataEvent.getDataItem()).getDataMap();
                String path=dataEvent.getDataItem().getUri().getPath();
                if (path.equals(NOTIFICATION_PATH)){

                    if (dataMap.containsKey(HIGH_TEMP_KEY)){
                        mHighTemp = dataMap.getString(HIGH_TEMP_KEY);
                    }else{
                        mHighTemp=NOT_FOUND_HIGH_TEMP;
                    }


                    if (dataMap.containsKey(LOW_TEMP_KEY)){
                        mLowTemp = dataMap.getString(LOW_TEMP_KEY);
                    }else{
                        mLowTemp=NOT_FOUND_LOW_TEMP;
                    }

                    if (dataMap.containsKey(WEATHER_ID_KEY)){
                        mWeatherId = dataMap.getInt(WEATHER_ID_KEY);
                    }else{
                        mWeatherId=NOT_FOUND_WEATHER_ID;
                    }

                    saveReceivedValues();
                }
            }
        }
    }



    public static String getHighTemp(Context context){
         SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(HIGH_TEMP_KEY,NOT_FOUND_HIGH_TEMP);
    }

    public static String getLowTemp(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(LOW_TEMP_KEY,NOT_FOUND_LOW_TEMP);
    }

    public static int getWeatherId(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(WEATHER_ID_KEY,NOT_FOUND_WEATHER_ID);
    }


    private void saveReceivedValues(){
        Context context=getApplicationContext();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(HIGH_TEMP_KEY,mHighTemp);
        editor.putString(LOW_TEMP_KEY,mLowTemp);
        editor.putInt(WEATHER_ID_KEY,mWeatherId);
        editor.apply();

    }





}
