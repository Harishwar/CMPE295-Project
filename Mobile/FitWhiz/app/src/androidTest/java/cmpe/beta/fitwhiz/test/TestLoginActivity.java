package cmpe.beta.fitwhiz.test;


import android.app.Instrumentation;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.widget.Button;
import android.widget.EditText;

import cmpe.alpha.fitwhiz.R;
import cmpe.alpha.fitwhiz.controllers.LoginActivity;
import cmpe.alpha.fitwhiz.sensortag.MainActivity;

/**
 * Created by RKGampa on 4/26/2015.
 */
public class TestLoginActivity extends ActivityInstrumentationTestCase2<LoginActivity>
{
    private static final long TIMEOUT_IN_MS = 10*1000 ;
    private EditText sensorId;
    private Button loginButton;
    private LoginActivity loginActivity;
    private Intent launchIntent;

    public TestLoginActivity()
    {
        super(LoginActivity.class);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        setActivityInitialTouchMode(false);
        loginActivity = (LoginActivity)getActivity();
    }

    public void testLogin()
    {
        assertNotNull(loginActivity.findViewById(R.id.login_btn));
        loginButton = (Button) loginActivity.findViewById(R.id.login_btn);
        assertEquals("Incorrect label of the button", "Login", loginButton.getText());
        sensorId = (EditText)loginActivity.findViewById(R.id.sensor_id_edit);
//        sensorId.requestFocus();
//        sensorId.setText("408618");

        // Set up an ActivityMonitor
        Instrumentation.ActivityMonitor receiverActivityMonitor =
                getInstrumentation().addMonitor(MainActivity.class.getName(),
                        null, false);

        // Send string input value
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                sensorId.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync("408618");
        getInstrumentation().waitForIdleSync();

        // Validate that ReceiverActivity is started
        TouchUtils.clickView(this, loginButton);
        MainActivity mainActivity = (MainActivity)
                receiverActivityMonitor.waitForActivityWithTimeout(TIMEOUT_IN_MS);
        assertNotNull("ReceiverActivity is null", mainActivity);
        assertEquals("Monitor for ReceiverActivity has not been called",
                1, receiverActivityMonitor.getHits());
        assertEquals("Activity is of wrong type",
                MainActivity.class, mainActivity.getClass());

        // Remove the ActivityMonitor
        getInstrumentation().removeMonitor(receiverActivityMonitor);
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

}
