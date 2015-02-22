package cmpe.alpha.fitwhiz.HelperLibrary;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;

import java.util.Objects;

/**
 * Created by rajagopalan on 2/21/15.
 */
public class ProfileUpdater extends AsyncTask<Object, Object, Object> {
    private OnTaskCompleted listener;
    public ProfileUpdater(OnTaskCompleted listener)
    {
        this.listener = listener;
    }

    @Override
    protected HttpResponse doInBackground(Object... objects) {
        return null;
    }

    protected  void onPostExecute(HttpResponse response)
    {
        listener.OntaskCompleted(response);
    }
}
