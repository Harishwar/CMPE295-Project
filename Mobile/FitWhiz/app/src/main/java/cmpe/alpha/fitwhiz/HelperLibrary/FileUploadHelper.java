package cmpe.alpha.fitwhiz.HelperLibrary;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by rajagopalan on 2/21/15.
 */
public class FileUploadHelper extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            String urlString = params[0]; // URL to call
            String filePath = params[1];
            String result = "";
            File file = new File(filePath);
            try {
                HttpClient httpclient = new DefaultHttpClient();

                HttpPost httppost = new HttpPost(urlString);

                InputStreamEntity reqEntity = new InputStreamEntity(
                        new FileInputStream(file), -1);
                reqEntity.setContentType("binary/octet-stream");
                reqEntity.setChunked(true); // Send in multiple parts if needed
                httppost.setEntity(reqEntity);
                HttpResponse response = httpclient.execute(httppost);
                return response.getStatusLine().getReasonPhrase();

            } catch (Exception e) {
                Log.e("FileUploadHelper",e.toString());
                return "Exception";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("FromOnPostExecute", result);
        }
}
