package cmpe.alpha.fitwhiz.controllers.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Properties;

import cmpe.alpha.fitwhiz.HelperLibrary.FileOperationsHelper;
import cmpe.alpha.fitwhiz.HelperLibrary.FileUploadHelper;
import cmpe.alpha.fitwhiz.HelperLibrary.PropertiesReader;

/**
 * Created by rajagopalan on 2/21/15.
 */
public class ScheduledDataUploadService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //Read Properties file
        PropertiesReader propertiesReader = new PropertiesReader(context);
        Properties properties= propertiesReader.getProperties("Fitwhiz.properties");
        String serverUrl = properties.getProperty("FileUploadUrl");
        //Write the sensor data to a file
        FileOperationsHelper fileOperationsHelper = new FileOperationsHelper(context);
        String result = fileOperationsHelper.WriteDBToFile();
        if(!result.equalsIgnoreCase("Exception"))
        {
            //Compress the file
            int index = result.lastIndexOf(".");
            String fileLocation = result.substring(0,index-1);
            String zipFileLocation = fileLocation.substring(0)+".zip";
            String zipResult = fileOperationsHelper.CompressFile(zipFileLocation, result);
            if(!zipResult.equalsIgnoreCase("Exception"))
            {
                //Send to the server
                FileUploadHelper fileUploadHelper = new FileUploadHelper();
                fileUploadHelper.execute(serverUrl,zipFileLocation);
            }
        }
    }
}
