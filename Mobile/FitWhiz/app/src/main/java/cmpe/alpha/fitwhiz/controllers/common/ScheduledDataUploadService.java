package cmpe.alpha.fitwhiz.controllers.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import cmpe.alpha.fitwhiz.HelperLibrary.FileWriteHelper;

/**
 * Created by rajagopalan on 2/21/15.
 */
public class ScheduledDataUploadService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //Write the sensor data to a temporary file
        FileWriteHelper fileWriteHelper = new FileWriteHelper(context);
        String result = fileWriteHelper.WriteDBToFile();
        if(!result.equalsIgnoreCase("Exception"))
        {
            //Compress the file

            //Send to the server
        }
    }
}
