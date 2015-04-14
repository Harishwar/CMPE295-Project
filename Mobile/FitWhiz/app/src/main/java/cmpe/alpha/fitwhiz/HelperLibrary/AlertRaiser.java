package cmpe.alpha.fitwhiz.HelperLibrary;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import cmpe.alpha.fitwhiz.lib.FitwhizApplication;

/**
 * Created by rajagopalan on 4/13/15.
 */
public class AlertRaiser extends AsyncTask<String,String,String > {
    FitwhizApplication app;
    public AlertRaiser(FitwhizApplication fitwhizApplication)
    {
        this.app=fitwhizApplication;
    }

    @Override
    protected String doInBackground(String... strings) {
        String msg = strings[1];
        String sensorId = app.getSensorId();


            sensorId = app.getSensorId();

        String url = strings[0]+"?SensorId="+sensorId;

        JSONObject obj = new JSONObject();
        try {
            obj.put("SensorId",sensorId);
            obj.put("message",msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            HttpPost httpPost = new HttpPost(new URI(url));
            httpPost.setEntity(new StringEntity(obj.toString()));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            HttpResponse response = new DefaultHttpClient().execute(httpPost);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}
