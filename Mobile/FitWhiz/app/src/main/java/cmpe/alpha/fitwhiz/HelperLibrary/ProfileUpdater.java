package cmpe.alpha.fitwhiz.HelperLibrary;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

import cmpe.alpha.fitwhiz.lib.FitwhizApplication;

/**
 * Created by rajagopalan on 2/21/15.
 */
public class ProfileUpdater extends AsyncTask<String, String, String> {
    private OnTaskCompleted listener;
    private FitwhizApplication app;
    public ProfileUpdater(FitwhizApplication fitwhizApplication)
    {
     this.app=fitwhizApplication;
    }

    @Override
    protected String doInBackground(String... params) {
        String sensorId = app.getSensorId();
        while(sensorId.equalsIgnoreCase(""))
        {
            sensorId = app.getSensorId();
            //return "Exception";
        }
        String urlString = params[0]+"/v1.0/user/profile/?SensorId="+sensorId; // URL to call
        String result = "";

        // HTTP Get
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            response = httpclient.execute(new HttpGet(new URI(urlString)));
            StatusLine statusLine = response.getStatusLine();
            URL url = new URL(urlString);
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                responseString = out.toString();
                out.close();
                return responseString;
            } else{
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }
    }

    protected  void onPostExecute(String response)
    {
        try {
            JSONObject json = new JSONObject(response);
            app.setBloodType(json.getString("BloodType"));
            app.setPhoneNumber(json.getString("PhoneNumber"));
            app.setWeight(json.getString("Weight"));
            app.setFirstName(json.getString("FirstName"));
            app.setGender(json.getString("Gender"));
            app.setLastName(json.getString("LastName"));
            app.setAddress(json.getString("Address"));
            app.setHeight(json.getString("Height"));
            Log.d(this.getClass().getSimpleName(),app.getFirstName()+" "+app.getLastName());

        } catch (JSONException e) {
            Log.e(this.getClass().getSimpleName(),e.toString());
        }
        System.out.println(response);
        Log.e(this.getClass().getSimpleName(),response);
    }
}
