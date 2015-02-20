package cmpe.alpha.fitwhiz.controllers;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import cmpe.alpha.fitwhiz.R;
import cmpe.alpha.fitwhiz.models.DatabaseConnector;
import cmpe.alpha.fitwhiz.models.UserTableOperations;

public class LoginActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if(checkLogin())
        {
            Intent dashboardIntent = new Intent(this, DashboardActivity.class);
            this.startActivity(dashboardIntent);
            this.finish();
        }
        if (savedInstanceState == null)
        {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new LoginFragment())
                    .commit();
        }
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
