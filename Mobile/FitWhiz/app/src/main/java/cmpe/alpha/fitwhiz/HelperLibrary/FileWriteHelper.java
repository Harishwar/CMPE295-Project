package cmpe.alpha.fitwhiz.HelperLibrary;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * Created by rajagopalan on 2/21/15.
 */
public class FileWriteHelper {
    Context context;
    String fileLocation = "";
    PropertiesReader propertiesReader;
    Properties properties;
    public FileWriteHelper(Context context)
    {
        this.context = context;
    }
    public String WriteDBToFile()
    {
        propertiesReader=new PropertiesReader(context);
        properties=propertiesReader.getProperties("Fitwhiz.properties");
        String filename = "Fitwhiz_backup_"+DateTimeHelper.getDateTime()+".txt";
        //fileLocation = properties.getProperty("UploadFileLocation")
        fileLocation = Environment.getExternalStorageDirectory()+ File.separator+filename;
        try {
            FileOutputStream fos = context.openFileOutput(fileLocation, Context.MODE_PRIVATE);
            //TODO Get rows from db

            //TODO write all data to the file

            return fileLocation;

        } catch (FileNotFoundException e) {
            Log.e("FileWriteHelper",e.toString());
        }
        return "Exception";
    }
}
