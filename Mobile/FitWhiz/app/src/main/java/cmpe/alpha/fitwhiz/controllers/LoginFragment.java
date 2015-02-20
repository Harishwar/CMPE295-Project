package cmpe.alpha.fitwhiz.controllers;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cmpe.alpha.fitwhiz.R;
import cmpe.alpha.fitwhiz.models.UserTableOperations;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class LoginFragment extends Fragment
{
    private UserTableOperations operations;
    private String sensorID;
    private EditText sensorIdEdit;
    private Button login_btn;
    private TextView alert_text_view;

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
        alert_text_view = (TextView)loginFragment.findViewById(R.id.alert_text_view);
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
//      operations.getAllUserDetails("1234A");
        return loginFragment;
    }

    public void goToDashboard()
    {
        setSensorID(sensorIdEdit.getText().toString());
        Cursor cursor = operations.getUserDetails(getSensorID());
        //Log.d("cursor", cursor.getString(cursor.getColumnIndex("sensor_id")));
        if (cursor.getCount()>0)
        {
            cursor.moveToFirst();
            SharedPreferences.Editor loginPref = getActivity().getSharedPreferences("LoginPreference",0).edit();
            loginPref.putBoolean("login",true);
            loginPref.commit();
            Intent dashboardIntent = new Intent(getActivity(), DashboardActivity.class);
            dashboardIntent.putExtra("user_type", cursor.getString(cursor.getColumnIndex("user_type")));
            this.startActivity(dashboardIntent);
            getActivity().finish();
        }
        else
        {
            alert_text_view.setText("Invalid sensor ID");
        }
    }

}
