package cmpe.alpha.fitwhiz.controllers;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.ti.ble.common.BluetoothLeService;

import cmpe.alpha.fitwhiz.HelperLibrary.AllergiesHelper;
import cmpe.alpha.fitwhiz.HelperLibrary.ProfileUpdater;
import cmpe.alpha.fitwhiz.HelperLibrary.PropertiesReader;
import cmpe.alpha.fitwhiz.HelperLibrary.RecommendationsHelper;
import cmpe.alpha.fitwhiz.HelperLibrary.ResultsUpdater;
import cmpe.alpha.fitwhiz.R;
import cmpe.alpha.fitwhiz.lib.FitwhizApplication;
import cmpe.alpha.fitwhiz.sensortag.LicenseDialog;
import cmpe.alpha.fitwhiz.sensortag.MainActivity;

public class LoginActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FitwhizApplication application = (FitwhizApplication)this.getApplication();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("firstrun", true)) {
            onLicense();
            prefs.edit().putBoolean("firstrun", false).commit();
        }
        if(checkLogin())
        {
            SharedPreferences p = getSharedPreferences("SensorId",0);
            application.setSensorId(p.getString("SensorId",""));
            BluetoothLeService service = BluetoothLeService.getInstance();
            if(service == null)
            {
                Intent i = new Intent(this, MainActivity.class);
                i.putExtra("Activity","MainActivity");
                if(i.resolveActivity(this.getPackageManager())!=null)
                {
                    startActivity(i);
                }
            }
            else {
                service.close();
                Intent i = new Intent(this, MainActivity.class);
                i.putExtra("Activity","MainActivity");
                if(i.resolveActivity(this.getPackageManager())!=null)
                {
                    startActivity(i);
                }
            }

            //Call Profile Updater
            ProfileUpdater profileUpdater = new ProfileUpdater(application);
            profileUpdater.execute(new PropertiesReader(this).getProperties("Fitwhiz.properties").getProperty("FileUploadUrl"));

            RecommendationsHelper rh = new RecommendationsHelper(application);
            rh.execute(new PropertiesReader(this).getProperties("Fitwhiz.properties").getProperty("FileUploadUrl"));

            AllergiesHelper ah = new AllergiesHelper(application);
            ah.execute(new PropertiesReader(this).getProperties("Fitwhiz.properties").getProperty("FileUploadUrl"));

            ResultsUpdater resultsUpdater=new ResultsUpdater(application);
            resultsUpdater.execute(new PropertiesReader(this).getProperties("Fitwhiz.properties").getProperty("FileUploadUrl"));

            this.finish();
        }
        if (savedInstanceState == null)
        {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new LoginFragment())
                    .commit();
        }
    }
    private void onLicense() {
        final Dialog dialog = new LicenseDialog(this);
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }

    public boolean checkLogin()
    {
        SharedPreferences loginPreference = getSharedPreferences("LoginPreference",0);
        return loginPreference.getBoolean("login",false);
    }

    /**
     * A login fragment containing a simple view.
     */
    /*public static class LoginFragment extends Fragment
    {
        private UserTableOperations operations;
        private String sensorID;
        private EditText sensorIdEdit;
        private Button login_btn;

        public LoginFragment()
        {

        }

        public String getSensorID()
        {
            return sensorID;
        }

        public void setSensorID(String sensorID)
        {
            this.sensorID = sensorID;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState)
        {
            View loginFragment = inflater.inflate(R.layout.fragment_login, container, false);
            login_btn = (Button)loginFragment.findViewById(R.id.login_btn);
            login_btn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                 goToDashboard();
                }
            });
            sensorIdEdit = (EditText)loginFragment.findViewById(R.id.sensor_id_edit);
            operations = new UserTableOperations(getActivity());
            operations.insertUser("21B","user");
//          operations.getAllUserDetails("1234A");
            return loginFragment;
        }

        public void goToDashboard()
        {
            setSensorID(sensorIdEdit.getText().toString());
            Cursor cursor = operations.getUserDetails(getSensorID());
            Log.d("cursor",cursor.getString(cursor.getColumnIndex("sensor_id")));
            if (cursor != null)
            {
                Intent dashboardIntent = new Intent(getActivity(), DashboardActivity.class);
                dashboardIntent.putExtra("user_type", cursor.getString(cursor.getColumnIndex("user_type")));
                this.startActivity(dashboardIntent);
            }
        }

    }*/
}
