package cmpe.alpha.fitwhiz.HelperLibrary;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by rajagopalan on 2/21/15.
 */
public class PropertiesReader {
    private Context context;
    private Properties properties;

    public PropertiesReader(Context context) {
        this.context = context;
        properties = new Properties();
    }

    public Properties getProperties(String FileName) {
        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(FileName);
            properties.load(inputStream);

        } catch (IOException e) {
            Log.e("PropertiesReader", e.toString());
        }
        return properties;
    }
}
