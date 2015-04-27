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
import android.widget.TextView;

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
    protected int _splashTime = 8000;
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
    private TextView fact;
    private int max = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);
        fact = (TextView)findViewById(R.id.fact_txt_view);

        int random = (int)(Math.random()*max);
        displayFact(random);
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

    public void displayFact(int randomNumber)
    {
        System.out.println("random number="+randomNumber);
        switch(randomNumber)
        {
            case 0:
                fact.setText("“The Institute of Medicine determined that an Adequate Water Intake(AI)\n"+
                        "for men = 13 cups (3 liters) a day. \n" +
                        "for women = 9 cups (2.2 liters) a day.”");
                break;
            case 1:
                fact.setText("“Every day, your heart creates enough energy to drive a truck for 20 miles (32 km).”");
                break;
            case 2:
                fact.setText("“Inadequate sleep can increase your chances of weight gain and trigger " +
                        "the production of gherlin, a hormone that causes hunger.”");
                break;
            case 3:
                fact.setText("“Muscle can burn more calories at rest than fat. One pound of muscle " +
                        "burns an extra 50 calories a day. So, eat healthy and try to build muscle.”");
                break;
            case 4:
                fact.setText("“Your brain uses 20% of the total oxygen and blood in your body.”");
                break;
            case 5:
                fact.setText("“A human baby has 60 more bones than an adult.”");
                break;
            case 6:
                fact.setText("“Humans shed about 600,000 particles of skin every hour.”");
                break;
            case 7:
                fact.setText("“Your nose can remember 50,000 different scents.”");
                break;
            case 8:
                fact.setText("“When awake, the human brain produces enough electricity to power a small light bulb.”");
                break;
            case 9:
                fact.setText("“There are more than 100 types of cancers; any part of the body can be affected.”");
                break;
            case 10:
                fact.setText("“When you take one step, you are using up to 200 muscles.”");
                break;
            case 11:
                fact.setText("“The highest recorded body temperature in a human being was a fever of 115.7°F (46.5°C).”");
                break;
            case 12:
                fact.setText("“Your taste buds are replaced every 10 days.");
                break;
            default:
                fact.setText("“Your bones are composed of 31% water and are stronger than steel.”");
                break;
        }
    }
}
