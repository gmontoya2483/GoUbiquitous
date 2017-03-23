/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.sunshine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.format.DateFormat;
import android.view.SurfaceHolder;
import android.view.WindowInsets;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Digital watch face with seconds. In ambient mode, the seconds aren't displayed. On devices with
 * low-bit ambient mode, the text is drawn without anti-aliasing in ambient mode.
 */
public class DigitalWatchFaceService extends CanvasWatchFaceService {

    private static final String TAG = "DigitalWatchFaceService";

    private static final Typeface NORMAL_TYPEFACE =Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
    private static final Typeface BOLD_TYPEFACE = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);

    /**
     * Update rate in milliseconds for interactive mode. We update once a second since seconds are
     * displayed in interactive mode.
     */
    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);

    /**
     * Handler message id for updating the time periodically in interactive mode.
     */
    private static final int MSG_UPDATE_TIME = 0;

    @Override
    public Engine onCreateEngine() {

        return new Engine();
    }

    private static class EngineHandler extends Handler {
        private final WeakReference<DigitalWatchFaceService.Engine> mWeakReference;

        public EngineHandler(DigitalWatchFaceService.Engine reference) {
            mWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            DigitalWatchFaceService.Engine engine = mWeakReference.get();
            if (engine != null) {
                switch (msg.what) {
                    case MSG_UPDATE_TIME:
                        engine.handleUpdateTimeMessage();
                        break;
                }
            }
        }
    }

    private class Engine extends CanvasWatchFaceService.Engine {
        final Handler mUpdateTimeHandler = new EngineHandler(this);

        Calendar mCalendar;
        Date mDate;
        SimpleDateFormat mDayOfWeekFormat;
        java.text.DateFormat mDateFormat;

        //device features
        boolean mAmbient;
        boolean mRegisteredTimeZoneReceiver = false;
        boolean mIsRound;

        //Graphic objects
        Paint mBackgroundPaint;
        /*
        Paint mHourPaint;
        Paint mColonPaint;
        Paint mMinutePaint;
        Paint mAmPmPaint;
         */
        Paint mDateTextPaint;
        Paint mTimeTextPaint;
        Paint mIconBitmappaint;
        Paint mMaxTempTextPaint;
        Paint mMinTempTextPaint;


        float mXOffset;
        float mYOffset;
        float mYIconYOffset;
        float mYTimeOffset;
        float mYDateOffset;
        float mYTempOffset;
        float mYLineOffset;
        float mXLineOffset;

        /**
         * Whether the display supports fewer bits for each color in ambient mode. When true, we
         * disable anti-aliasing in ambient mode.
         */
        boolean mLowBitAmbient;


        final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mCalendar.setTimeZone(TimeZone.getDefault());
                invalidate();
            }
        };



        private void initializeWatchFaceElements(Resources resources){

            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(resources.getColor(R.color.background));


            mTimeTextPaint = new Paint();
            mTimeTextPaint.setColor(resources.getColor(R.color.digital_time));
            mTimeTextPaint.setTypeface(NORMAL_TYPEFACE);
            mTimeTextPaint.setAntiAlias(true);

            mDateTextPaint = new Paint();
            mDateTextPaint.setTextSize(resources.getDimension(R.dimen.digital_text_date_size));
            mDateTextPaint.setColor(resources.getColor(R.color.digital_date));
            mDateTextPaint.setTypeface(NORMAL_TYPEFACE);
            mDateTextPaint.setAntiAlias(true);

            mIconBitmappaint=new Paint();

            mMaxTempTextPaint= new Paint();
            mMaxTempTextPaint.setColor(resources.getColor(R.color.digital_maxT));
            mMaxTempTextPaint.setTextSize(resources.getDimension(R.dimen.digital_text_MaxT_size));
            mMaxTempTextPaint.setTypeface(NORMAL_TYPEFACE);
            mMaxTempTextPaint.setAntiAlias(true);


            mMinTempTextPaint= new Paint();
            mMinTempTextPaint.setColor(resources.getColor(R.color.digital_minT));
            mMinTempTextPaint.setTextSize(resources.getDimension(R.dimen.digital_text_MinT_size));
            mMinTempTextPaint.setTypeface(NORMAL_TYPEFACE);
            mMinTempTextPaint.setAntiAlias(true);




            mYOffset = resources.getDimension(R.dimen.digital_y_offset);
            mCalendar = Calendar.getInstance();
            mDate = new Date();

        }


        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(DigitalWatchFaceService.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_VARIABLE)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .setAcceptsTapEvents(true)
                    .build());

            Resources resources = DigitalWatchFaceService.this.getResources();
            initializeWatchFaceElements(resources);

        }



        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            super.onDestroy();
        }

        private Paint createTextPaint(int textColor) {
            Paint paint = new Paint();
            paint.setColor(textColor);
            paint.setTypeface(NORMAL_TYPEFACE);
            paint.setAntiAlias(true);
            return paint;
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {
                registerReceiver();

                // Update time zone in case it changed while we weren't visible.
                mCalendar.setTimeZone(TimeZone.getDefault());
                invalidate();
            } else {
                unregisterReceiver();
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            DigitalWatchFaceService.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            DigitalWatchFaceService.this.unregisterReceiver(mTimeZoneReceiver);
        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            super.onApplyWindowInsets(insets);

            // Load resources that have alternate values for round watches.
            Resources resources = DigitalWatchFaceService.this.getResources();
            mIsRound = insets.isRound();

            if (mIsRound){
                mXOffset=R.dimen.digital_x_offset_round;
                mTimeTextPaint.setTextSize(resources.getDimension(R.dimen.digital_text_size_round));

                mYIconYOffset=-90f;
                mYTimeOffset=0f;
                mYDateOffset=30f;
                mYTempOffset=110f;
                mYLineOffset=50f;
                mXLineOffset=40f;


            }else{

                mXOffset=R.dimen.digital_x_offset;
                mTimeTextPaint.setTextSize(resources.getDimension(R.dimen.digital_text_size));

                mYIconYOffset=-120f;
                mYTimeOffset=0f;
                mYDateOffset=30f;
                mYTempOffset=90f;
                mYLineOffset=50f;
                mXLineOffset=40f;

            }



        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);

        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            if (mAmbient != inAmbientMode) {
                mAmbient = inAmbientMode;
                if (mLowBitAmbient) {
                    mTimeTextPaint.setAntiAlias(!inAmbientMode);
                    mDateTextPaint.setAntiAlias(!inAmbientMode);
                }
                invalidate();
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        /**
         * Captures tap event (and tap type) and toggles the background color if the user finishes
         * a tap.
         */
        @Override
        public void onTapCommand(int tapType, int x, int y, long eventTime) {
            switch (tapType) {
                case TAP_TYPE_TOUCH:
                    // The user has started touching the screen.
                    break;
                case TAP_TYPE_TOUCH_CANCEL:
                    // The user has started a different gesture or otherwise cancelled the tap.
                    break;
                case TAP_TYPE_TAP:
                    // The user has completed the tap gesture.
                    // TODO: Add code to handle the tap gesture.
                    Toast.makeText(getApplicationContext(), R.string.message, Toast.LENGTH_SHORT)
                            .show();
                    break;
            }
            invalidate();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {

            String time;
            String date;
            String maxTemp;
            String minTemp;

            float xPos,yPos;

            // Draw the background.
            if (isInAmbientMode()) {
                canvas.drawColor(Color.BLACK);
            } else {
                canvas.drawRect(0, 0, bounds.width(), bounds.height(), mBackgroundPaint);
            }

            // Draw H:MM in ambient mode or H:MM:SS in interactive mode.
            long now = System.currentTimeMillis();
            mCalendar.setTimeInMillis(now);
            mDate.setTime(now);
            boolean is24Hour = DateFormat.is24HourFormat(DigitalWatchFaceService.this);


            //Draw the weather icon
            if(!isInAmbientMode()) {
                int icon = Utils.getIconResourceForWeatherCondition(300);

                Bitmap weatherIcon = BitmapFactory.decodeResource(getResources(), icon);
                canvas.drawBitmap(weatherIcon,
                        bounds.centerX()-(weatherIcon.getWidth()/2),
                        mYOffset+mYIconYOffset,
                        mIconBitmappaint);

            }


            //Draw the Time
            if (mAmbient){
                time=Utils.getFormatedTime(mCalendar,is24Hour,Utils.TIME_WITHOUT_SECONDS);
            }else{
                time=Utils.getFormatedTime(mCalendar,is24Hour,Utils.TIME_WITHOUT_SECONDS);
            }

            canvas.drawText(time,
                    Utils.centerX(bounds,mTimeTextPaint.measureText(time)),
                    bounds.centerY()+mYTimeOffset,
                    mTimeTextPaint);


            //Draw the Date
            date=Utils.getFormattedDate(getApplicationContext(),mCalendar);
            canvas.drawText(date,
                    Utils.centerX(bounds,mDateTextPaint.measureText(date)),
                    bounds.centerY()+ mYDateOffset,
                    mDateTextPaint);

            //Draw a line
            canvas.drawLine(bounds.centerX()-mXLineOffset,
                    bounds.centerY()+ mYLineOffset,
                    bounds.centerX()+mXLineOffset,
                    bounds.centerY()+ mYLineOffset,
                    mDateTextPaint);


            //Draw the  Max Temp
            maxTemp=Utils.formatTemperature(getApplicationContext(),27);
            canvas.drawText(maxTemp,
                    bounds.centerX()-(mMaxTempTextPaint.measureText(maxTemp)),
                    bounds.centerY()+ mYTempOffset,
                    mMaxTempTextPaint);

            //Draw the  Max Temp
            minTemp=Utils.formatTemperature(getApplicationContext(),15);
            canvas.drawText(minTemp,
                    bounds.centerX(),
                    bounds.centerY()+ mYTempOffset,
                    mMinTempTextPaint);



        }

        /**
         * Starts the {@link #mUpdateTimeHandler} timer if it should be running and isn't currently
         * or stops it if it shouldn't be running but currently is.
         */
        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        /**
         * Returns whether the {@link #mUpdateTimeHandler} timer should be running. The timer should
         * only run when we're visible and in interactive mode.
         */
        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }

        /**
         * Handle updating the time periodically in interactive mode.
         */
        private void handleUpdateTimeMessage() {
            invalidate();
            if (shouldTimerBeRunning()) {
                long timeMs = System.currentTimeMillis();
                long delayMs = INTERACTIVE_UPDATE_RATE_MS
                        - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
            }
        }
    }
}
