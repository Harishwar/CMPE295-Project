package cmpe.beta.fitwhiz.test;


import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;

import cmpe.alpha.fitwhiz.R;
import cmpe.alpha.fitwhiz.controllers.LoginActivity;

/**
 * Created by RKGampa on 4/26/2015.
 */
public class TestLoginActivity extends ActivityInstrumentationTestCase2<LoginActivity>
{
    private EditText sensorId;
    private Button loginButton;
    private LoginActivity loginActivity;
    private Intent launchIntent;

    public TestLoginActivity()
    {
        super(LoginActivity.class);
    }

//    public TestLoginActivity(Class<LoginActivity> activityClass)
//    {
//        super(LoginActivity.class);
//    }

    protected void setUp() throws Exception
    {
        super.setUp();
        loginActivity = (LoginActivity)getActivity();
    }

    public void testLayout()
    {
        assertNotNull(loginActivity.findViewById(R.id.login_btn));
        loginButton = (Button) loginActivity.findViewById(R.id.login_btn);
        assertEquals("Incorrect label of the button", "Login", loginButton.getText());
        sensorId = (EditText)loginActivity.findViewById(R.id.sensor_id_edit);
        sensorId.setText("408618");
    }

//    public void testIntentTriggerViaOnClick()
//    {
//        loginButton.performClick();
//
//        // TouchUtils cannot be used, only allowed in
//        // InstrumentationTestCase or ActivityInstrumentationTestCase2
//
//        // Check the intent which was started
//        Intent triggeredIntent = getStartedActivityIntent();
//        assertNotNull("Intent was null", triggeredIntent);
//        String activityName = triggeredIntent.getExtras().getString("Activity");
//
//        assertEquals("Incorrect data passed via the intent",
//                "MainActivity", activityName);
//    }
}
