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

import cmpe.alpha.fitwhiz.controllers.ProfileFragment;
import cmpe.alpha.fitwhiz.controllers.SensorHistoryFragment;

/**
 * Created by rajagopalan on 2/22/15.
 */
public class ResultsUpdater extends AsyncTask<String, String, String> {
        private SensorHistoryFragment sensorHistoryFragment;
        public ResultsUpdater(SensorHistoryFragment sensorHistoryFragment)
        {
            this.sensorHistoryFragment=sensorHistoryFragment;
        }

        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0]+"/v1.0/user/results/?SensorId=21B"; // URL to call
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
            FitwhizApplication app = ((FitwhizApplication)sensorHistoryFragment.getActivity().getApplication());
            try {
                JSONObject json = new JSONObject(response);
                app.setResult_xVal(json.getDouble("xValue"));
                app.setResult_yVal(json.getDouble("yValue"));
                app.setResult_zVal(json.getDouble("zValue"));
                app.setResult_hVal(json.getDouble("hValue"));
                app.setResult_tVal(json.getDouble("tValue"));
                Log.d(this.getClass().getSimpleName(),app.getXVal()+" "+app.getTVal()+" "+app.getHVal());

            } catch (JSONException e) {
                Log.e(this.getClass().getSimpleName(), e.toString());
            }
            System.out.println(response);
            Log.e(this.getClass().getSimpleName(),response);
        }
}
