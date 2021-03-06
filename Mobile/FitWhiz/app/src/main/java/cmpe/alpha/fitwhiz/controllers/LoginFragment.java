package cmpe.alpha.fitwhiz.controllers;


import android.app.Fragment;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ti.ble.common.BluetoothLeService;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import cmpe.alpha.fitwhiz.HelperLibrary.AllergiesHelper;
import cmpe.alpha.fitwhiz.HelperLibrary.NotificationHelper;
import cmpe.alpha.fitwhiz.HelperLibrary.ProfileUpdater;
import cmpe.alpha.fitwhiz.HelperLibrary.PropertiesReader;
import cmpe.alpha.fitwhiz.HelperLibrary.RecommendationsHelper;
import cmpe.alpha.fitwhiz.HelperLibrary.ResultsUpdater;
import cmpe.alpha.fitwhiz.R;
import cmpe.alpha.fitwhiz.lib.FitwhizApplication;
import cmpe.alpha.fitwhiz.lib.NotificationPriority;
import cmpe.alpha.fitwhiz.models.UserTableOperations;
import cmpe.alpha.fitwhiz.sensortag.MainActivity;

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
    private ProgressDialog progressDialog = null;
    private View loginFragment;
    private FitwhizApplication application;
    private Button buttonSensorTag;

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
        loginFragment = inflater.inflate(R.layout.fragment_login, container, false);
        alert_text_view = (TextView)loginFragment.findViewById(R.id.alert_text_view);
        login_btn = (Button)loginFragment.findViewById(R.id.login_btn);
        login_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try {
                    goToDashboard();
                } catch (URISyntaxException e) {
                    Log.e(this.getClass().getSimpleName(), e.getMessage());
                }
            }
        });
        application = (FitwhizApplication)this.getActivity().getApplication();
        sensorIdEdit = (EditText)loginFragment.findViewById(R.id.sensor_id_edit);
        //Initialize progressDialog
        progressDialog = new ProgressDialog(loginFragment.getContext());
        return loginFragment;
    }

    public void goToDashboard() throws URISyntaxException {
        sensorID=sensorIdEdit.getText().toString();
        //Start the progressDialog before authenticating
        progressDialog.setMessage("Logging in...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        //TODO Check for network connectivity before issuing the REST call

        //This is to allow networking on MainThread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //Call the service and authenticate
        String urlString = new PropertiesReader(loginFragment.getContext()).getProperties("Fitwhiz.properties").getProperty("FileUploadUrl")+"/v1.0/user/login/?SensorId="+sensorID; // URL to call
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;
        try {
            URI uri = new URI(urlString);
            response = httpclient.execute(new HttpGet(uri));
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                responseString = out.toString();
                out.close();
                JSONObject json = new JSONObject(responseString);
                String message;
                try{
                    message = json.getString("result");
                }
                catch (Exception ex)
                {
                    message = "Exception";
                }
                if(message.equalsIgnoreCase("Success"))
                {
                    application.setUserId(json.getString("user_id"));
                    progressDialog.setMessage("Loading your dashboard");
                    //Login Successful
                    //Set Shared Pref
                    SharedPreferences.Editor loginPref = getActivity().getSharedPreferences("LoginPreference",0).edit();
                    loginPref.putBoolean("login",true);
                    loginPref.commit();
                    //Set Application SensorId value
                    SharedPreferences.Editor sensorId = getActivity().getSharedPreferences("SensorId",0).edit();
                    sensorId.putString("SensorId",this.sensorID);
                    sensorId.apply();
                    application.setSensorId(this.sensorID);
                    //Call Profile Updater
                    ProfileUpdater profileUpdater = new ProfileUpdater(application);
                    profileUpdater.execute(new PropertiesReader(loginFragment.getContext()).getProperties("Fitwhiz.properties").getProperty("FileUploadUrl"));

                    RecommendationsHelper rh = new RecommendationsHelper(application);
                    rh.execute(new PropertiesReader(loginFragment.getContext()).getProperties("Fitwhiz.properties").getProperty("FileUploadUrl"));

                    AllergiesHelper ah = new AllergiesHelper(application);
                    ah.execute(new PropertiesReader(loginFragment.getContext()).getProperties("Fitwhiz.properties").getProperty("FileUploadUrl"));

                    ResultsUpdater resultsUpdater=new ResultsUpdater(application);
                    resultsUpdater.execute(new PropertiesReader(loginFragment.getContext()).getProperties("Fitwhiz.properties").getProperty("FileUploadUrl"));

                    //Redirect to dashboard
                    Intent dashboardIntent = new Intent(getActivity(), DashboardActivity.class);
                    progressDialog.hide();
                    progressDialog.dismiss();
                    String desc = "Welcome to Fitwhiz. Your Login was successsful. Contact the developers for any assistance";
                    new NotificationHelper(this.getActivity().getApplicationContext()).SendNotification("test", "test", PendingIntent.getActivity(this.getActivity().getApplicationContext(), 0, new Intent(this.getActivity().getApplicationContext(),DashboardActivity.class),0), NotificationPriority.HIGH,desc);
                    if(BluetoothLeService.getInstance() == null)
                    {
                        Intent i = new Intent(getActivity(), MainActivity.class);
                        if(i.resolveActivity(getActivity().getPackageManager())!=null)
                        {
                            startActivity(i);
                        }
                    }
                    else
                    {
                        this.startActivity(dashboardIntent);
                    }
                    getActivity().finish();
                }
                else
                {
                    progressDialog.hide();
                    progressDialog.dismiss();
                    startActivity(new Intent(getActivity(), LoginFailureActivity.class));
                }
            } else{
                //Closes the connection.
                progressDialog.setMessage("Something is wrong");
                response.getEntity().getContent().close();
                progressDialog.hide();
                progressDialog.dismiss();
                throw new IOException(statusLine.getReasonPhrase());
            }
        }
        catch (Exception ex)
        {
            //Login Failure
            Log.e(this.getClass().getSimpleName(), ex.getMessage().toString());
            startActivity(new Intent(this.getActivity(),this.getActivity().getClass()));/*
            //For debug purpose, to skip login
            Intent dashboardIntent = new Intent(getActivity(), DashboardActivity.class);
            if(BluetoothLeService.getInstance() == null)
            {
                Intent i = new Intent(getActivity(), MainActivity.class);
                if(i.resolveActivity(getActivity().getPackageManager())!=null)
                {
                    startActivity(i);
                }
            }
            else
            {
                this.startActivity(dashboardIntent);
            }*/
        }
    }

}
