package cmpe.alpha.fitwhiz.HelperLibrary;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

import cmpe.alpha.fitwhiz.lib.FitwhizApplication;

/**
 * Created by rajagopalan on 4/7/15.
 */
public class UpdateDataHelper extends AsyncTask<String, String, String> {
    private OnTaskCompleted listener;
    private FitwhizApplication app;
    public UpdateDataHelper(FitwhizApplication fitwhizApplication)
    {
        this.app=fitwhizApplication;
    }

    @Override
    protected String doInBackground(String... params) {
        String json = params[0];
        String urlString = params[1]+"/v1.0/user/sensor_details"; // URL to call

        try {
            JSONObject jsonObject = new JSONObject(json);

            HttpClient httpclient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost(new URI(urlString));

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(jsonObject.toString());

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            InputStream inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            String result;
            if(inputStream != null)
                return convertInputStreamToString(inputStream);
            else
                return "Exception";

        } catch (Throwable t) {
            Log.e(this.getClass().getSimpleName(), "Could not parse malformed JSON: \"" + json + "\"");
            return "Exception";
        }
    }
    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
    protected  void onPostExecute(String response)
    {
        if(response=="Exception")
        {
            Log.e(this.getClass().getSimpleName(),"Exception occurred while uploading json data");
        }
        Log.i(this.getClass().getSimpleName(),"json data uploaded successfully");

    }

}
