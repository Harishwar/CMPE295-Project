package cmpe.alpha.fitwhiz.controllers;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import java.util.Properties;

import cmpe.alpha.fitwhiz.HelperLibrary.PropertiesReader;
import cmpe.alpha.fitwhiz.R;
import cmpe.alpha.fitwhiz.controllers.common.ScheduledCountUpdateService;
import cmpe.alpha.fitwhiz.controllers.common.ScheduledDataUploadService;
import cmpe.alpha.fitwhiz.controllers.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class SplashScreen extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    protected boolean _active = true;
    protected int _splashTime = 5000;
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    private static Intent alarmIntent = null,countIntent = null;
    private static PendingIntent pendingIntent = null,pendingIntent_count=null;
    private static AlarmManager alarmManager = null;
    private PropertiesReader propertiesReader;
    private Properties properties;
    private int uploadInterval, countUpdateInterval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        //Read properties file for uploadInterval
        propertiesReader=new PropertiesReader(this);
        properties=propertiesReader.getProperties("Fitwhiz.properties");
        uploadInterval=Integer.parseInt(properties.getProperty("UploadInterval","3600"));

        //Set the ScheduledDataUploadService
        alarmIntent = new Intent(this, ScheduledDataUploadService.class);
        pendingIntent = pendingIntent.getBroadcast(this.getApplicationContext(),0, alarmIntent,0);
        alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, (uploadInterval*60*1000),(uploadInterval*60*1000),pendingIntent);

        Thread splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (_active && (waited < _splashTime)) {
                        sleep(100);
                        if (_active) {
                            waited += 100;
                        }
                    }
                } catch (Exception e) {

                } finally {

                    startActivity(new Intent(SplashScreen.this,
                            LoginActivity.class));
                    finish();
                }
            };
        };
        splashTread.start();

        //Start service for capturing sensor data
        //this.startService(new Intent(this, SensorService.class));
/*        //Update Profile page
        ProfileUpdater profileUpdater = new ProfileUpdater((FitwhizApplication)this.getApplication());
        profileUpdater.execute(new PropertiesReader(this.getApplicationContext()).getProperties("Fitwhiz.properties").getProperty("FileUploadUrl"));

        //Update History page
        ResultsUpdater resultsUpdater = new ResultsUpdater((FitwhizApplication)this.getApplication());
        resultsUpdater.execute(new PropertiesReader(this.getApplicationContext()).getProperties("Fitwhiz.properties").getProperty("FileUploadUrl"));

        //Test Notification
        PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(),DashboardActivity.class),0);
        NotificationHelper helper = new NotificationHelper(getApplicationContext());
        helper.SendNotification("FitWhiz", "BOOM", pIntent, NotificationPriority.EMERGENCY,"");
*/
        //Set the ScheduledCountUpdateService
        countUpdateInterval=Integer.parseInt(properties.getProperty("CountUpdateInterval","3600"));
        countIntent = new Intent(this, ScheduledCountUpdateService.class);
        pendingIntent_count = pendingIntent_count.getBroadcast(this.getApplicationContext(),0, countIntent,0);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, (countUpdateInterval*60*1000),(countUpdateInterval*60*1000),pendingIntent_count);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
