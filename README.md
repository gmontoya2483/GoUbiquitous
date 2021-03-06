# GoUbiquitous

* [About](#about)
* [What will I learn](#what-will-i-learn)
* [Required Tasks check list](#required-tasks-check-list)
* [Rubric check list](#rubric-check-list)
* [Screenshots](#screenshots)
* [Implementation](#implementation)
* [Using the Emulator](#using-the-emulator)
* [Todo](#todo)


## About

This project is part of the [Udacity Android Developer Nanodegree](https://www.udacity.com/course/android-developer-nanodegree-by-google--nd801) under the name - Project P6: Go Ubiquitous.

### Project Overview:
In this project, I  will build a wearable watch face for Sunshine to run on an Android Wear device.

### Why this Project?
Android Wear is an exciting way to integrate your app more directly into users’ lives. As a new developer, it will be important for you to understand how to perform this integration. This project gives you an opportunity to design a companion app for Sunshine, tying it to a watch face in order to enrich the experience.

## What will I learn?

Through this project, I Will:

* Understand the fundamentals of Android Wear.
* Design for multiple watch form factors.
* Communicate between a mobile device and a wearable device.


## Required tasks check list

- [x] Clone/Download a zip file of the [Sunshine repository](https://github.com/udacity/ud851-Sunshine/tree/student).
- [x] Build your code starting from the project in the **S12.04-Solution-ResourceQualifiers** folder.
- [x] Spend time exploring the samples built into Android Studio, looking for ways to incorporate that functionality into an Android Wear watchface.


## Rubric Check list
- [x] App works on both round and square face watches.
- [x] App displays the current time.
- [x] App displays the high and low temperatures.
- [x] App displays a graphic that summarizes the day’s weather (e.g., a sunny image, rainy image, cloudy image, etc.).
- [x] Watch face obtains weather data by talking with a copy of Sunshine running on a device paired to the watch.
- [x] App conforms to common standards found in the [Android Nanodegree General Project Guidelines](http://udacity.github.io/android-nanodegree-guidelines/core.html).


## Screenshots

<img src="screenshots/preview_phone.png" height="400" alt="Phone"/>
<img src="screenshots/preview_phone_detail.png" height="400" alt="Phone details"/>
<img src="screenshots/preview_digital_circular.png" height="200" alt="Round Wearable"/>
<img src="screenshots/preview_digital.png" height="200" alt="Square Wearable"/>

  
  
## Implementation
  
  
Adding the wear application to the Project
------------------------------------------

1 - Right click on the project: **New -> Module**

<img src="screenshots/adding_w_new_module.png" height="300" alt="New Module"/>
  
2 - Select **Android Wear Module** and click on **Next**

<img src="screenshots/adding_w_new_android_wear_module.png" height="300" alt="New wear Module"/>
  
3 - Enter the Application and the module names and click on **Next**. Ensure the package is the same uses by the Phone Application.
  
4 - Select **Watch Face** and click on **Next**

<img src="screenshots/adding_w_watchface.png" height="300" alt="Watch face"/>
  
5 - Enter the service name and and select **Digital** as Style 

<img src="screenshots/adding_w_new_digital.png" height="300" alt="Watch face"/>
  
  
Linking the Phone application with the Wear application
-------------------------------------------------------

1 - Softaware package. 
    Provide the new module the same package as the application.  

```java
    package com.example.android.sunshine;
```
  

2 - Add the dependency to the wear application within the phone app Gradle.
```
dependencies {
    
    wearApp project(':wear')
}
```
    
3 - All the permissions needed by the wear application have to be provided to the phone Application as well in the ```Manifest.xml```  

```xml
<uses-permission android:name="android.permission.WAKE_LOCK" />
``` 
  
  
Sending weather information from the phone to the wearable
----------------------------------------------------------

* **WatchInterface** class (app module)

```java
    public class WatchInterface implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {
    
    }
```

*  Constructor

```java
    public WatchInterface(Context context) {
            mContext=context;
            getLastWeatherInformation();
        }
```


*  Public Constants 

```java
   public static final String NOTIFICATION_PATH="/wear_face";
   public static final String HIGH_TEMP_KEY="high_temp";
   public static final String LOW_TEMP_KEY="low_temp";
   public static final String WEATHER_ID_KEY="weather_id";
    
    
  public static final float DEFAULT_HIGH_TEMP=999;
  public static final float DEFAULT_LOW_TEMP=999;
  public static final int DEFAULT_WEATHER_ID=999;
```

*  Public Methods

   -  ```boolean weatherHasChanged()```  
     
   Return **true** if the weather conditions have changed since last time they were informed to the wearable.  
   Return **false** if the weather conditions have not changed since last time they were informed to the wearable.    
   
   - ```boolean notifyWearable()```
   
   This method sends to the wearable the weather information. (i.e High and Low formatted temperatures and the weather condition ID.
   
   Return **true** if the notification was sent. It doesn´t mean that the notification was received by the wearable.  
   Return **false** if the notification was not sent.
   
*  How to use it:

    - Within the **SunshineSyncTask** class 

```java
    synchronized public static void syncWeather(Context context) {
    
            try {
            
                   //All the steps to retrieve the weather forecast information
                   // It includes getting the data and storing it into the database.
                   
                   /*
                    *Notify the wearable the new weather conditions
                    * In order to avoid sending notifications to the wearable anytime the SyncTask runs it is verified if the weather conditions has changed.
                    */
                    WatchInterface watchInterface=new WatchInterface(context);
                          if (watchInterface.weatherHasChanged()){
                                watchInterface.notifyWearable();
                           }
            }
    }
```
  

Receiving weather information from the phone
--------------------------------------------

* **WatchInterService** class (wear module)  

```java
    public class WatchInterfaceService extends WearableListenerService {
    
    }
```

*  Public Constants  

```java
    public static final String NOTIFICATION_PATH="/wear_face";
    public static final String HIGH_TEMP_KEY="high_temp";
    public static final String LOW_TEMP_KEY="low_temp";
    public static final String WEATHER_ID_KEY="weather_id";
    
    public static final String NOT_FOUND_HIGH_TEMP="ND";
    public static final String NOT_FOUND_LOW_TEMP="ND";
    public static final int NOT_FOUND_WEATHER_ID=999;
```

   
   
*  Public Methods

   -  ```static String getHighTemp(Context context)```  
     
        Return the last ***high temperature*** received.  
        Return ```"ND"``` if the wearable hasn´t received any weather information yet.
   
   -  ```static String getLowTemp(Context context)```  
        
        Return the last ***Low temperature*** received.  
        Return ```"ND"``` if the wearable hasn´t received any weather information yet.
        
   -  ```static String getWeatherId(Context context)```  
             
       Return the last ***Weather Id*** received.  
       Return ```999``` if the wearable hasn´t received any weather information yet.
       
*  ```AndroidManifest.xlm``` (wearable)

```xml
<application>
    <service
       android:name=".WatchInterfaceService"
       android:enabled="true"
       android:exported="true">
                <intent-filter>
                    <action android:name="com.google.android.gms.wearable.MESSAGE_RECEIVED" />
                    <data android:scheme="wear" android:host="*" android:path="/wear_face"/>
                </intent-filter>
    
                <intent-filter>
                    <action android:name="com.google.android.gms.wearable.DATA_CHANGED" />
    
                    <data
                        android:host="*"
                        android:scheme="wear"
                        />
                </intent-filter>
    
    
            </service>

</application>
```
   

*   How to use it:

    - Within the **DigitalWatchFaceService** class -> **Engine** class

```java
    public void onDraw(Canvas canvas, Rect bounds) {
        
                        //
                        //
                        //
        
        mHighTemp=WatchInterfaceService.getHighTemp(getApplicationContext());
        mLowTemp=WatchInterfaceService.getLowTemp(getApplicationContext());
        mWeatherId=WatchInterfaceService.getWeatherId(getApplicationContext());
        
        
        
        //Check if we get a weather ID temp from the phone app before drawing
        if(mWeatherId!=WatchInterfaceService.NOT_FOUND_WEATHER_ID){
            //Draw the weather icon
            if(!isInAmbientMode()) {
                int icon = Utils.getIconResourceForWeatherCondition(mWeatherId);
                Bitmap weatherIcon = BitmapFactory.decodeResource(getResources(), icon);
                canvas.drawBitmap(weatherIcon,
                bounds.centerX()-(weatherIcon.getWidth()/2),
                mYOffset+mYIconYOffset,
                mIconBitmappaint);
        
            }
        }
        
        
                        //
                        //
                        //
      
        
        //Check if we get a pair high/ Min temp from the phone app before drawing
        if (!mHighTemp.equals(WatchInterfaceService.NOT_FOUND_HIGH_TEMP) && !mLowTemp.equals(WatchInterfaceService.NOT_FOUND_LOW_TEMP)){
            //Draw the  Max Temp
            canvas.drawText(mHighTemp,
                bounds.centerX()-(mMaxTempTextPaint.measureText(mHighTemp)),
                bounds.centerY()+ mYTempOffset,
                mMaxTempTextPaint);
            
             //Draw the  Min Temp
             canvas.drawText(mLowTemp,
                bounds.centerX(),
                bounds.centerY()+ mYTempOffset,
                mMinTempTextPaint);
        }
        
                        //
                        //
                        //
        
    }
```
     
     
## Using the Emulator

*  Install the "Android wear" application in your phone.
*  Turn on the Android wear emulator.
*  Connect the connect the phone with the emulator by using the Android wear application.
*  While the connection is being established, run the following command in the terminal console:  

```
adb -d forward tcp:5601 tcp:5601
```

In general the adb tool is located in:
```
C:\Users\XXXXX\AppData\Local\Android\sdk\platform-tools\
```




