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
 * Created by rajagopalan on 2/22/15.
 */
public class ResultsUpdater extends AsyncTask<String, String, String> {
        private FitwhizApplication fitwhizApplication;
        public ResultsUpdater(FitwhizApplication fitwhizApplication)
        {
            this.fitwhizApplication=fitwhizApplication;
        }

        @Override
        protected String doInBackground(String... params) {
            String sensorId = fitwhizApplication.getSensorId();
            while(sensorId.equalsIgnoreCase(""))
            {
                sensorId = fitwhizApplication.getSensorId();
            }
            String urlString = params[0]+"/v1.0/user/results/?SensorId="+sensorId; // URL to call
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
                fitwhizApplication.setResult_xVal(json.getDouble("acc_x_avg"));
                fitwhizApplication.setResult_yVal(json.getDouble("acc_y_avg"));
                fitwhizApplication.setResult_zVal(json.getDouble("acc_z_avg"));
                fitwhizApplication.setResult_gXVal(json.getDouble("gyro_x_avg"));
                fitwhizApplication.setResult_gYVal(json.getDouble("gyro_y_avg"));
                fitwhizApplication.setResult_gZVal(json.getDouble("gyro_z_avg"));
                fitwhizApplication.setResult_mXVal(json.getDouble("mag_x_avg"));
                fitwhizApplication.setResult_mYVal(json.getDouble("mag_y_avg"));
                fitwhizApplication.setResult_mZVal(json.getDouble("mag_z_avg"));
                fitwhizApplication.setResult_tAmb(json.getDouble("irt_ambient_avg"));
                fitwhizApplication.setResult_tBody(json.getDouble("irt_body_avg"));
                fitwhizApplication.setResult_hVal(json.getDouble("humidity_avg"));
                fitwhizApplication.setResult_pVal(json.getDouble("pressure_avg"));
                Log.d(this.getClass().getSimpleName(),fitwhizApplication.getXVal()+" "+fitwhizApplication.getTVal()+" "+fitwhizApplication.getHVal());

            } catch (JSONException e) {
                Log.e(this.getClass().getSimpleName(), e.toString());
            }
            System.out.println(response);
            Log.e(this.getClass().getSimpleName(),response);
        }
}
