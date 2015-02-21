package cmpe.alpha.fitwhiz.HelperLibrary;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by rajagopalan on 2/21/15.
 */
public class FileWriteHelper {
    Context context;
    String fileLocation = "";
    PropertiesReader propertiesReader;
    Properties properties;
    private int BUFFER = 1000;
    public FileWriteHelper(Context context)
    {
        this.context = context;
    }
    public String WriteDBToFile()
    {
        propertiesReader=new PropertiesReader(context);
        properties=propertiesReader.getProperties("Fitwhiz.properties");
        String filename = "Fitwhiz_backup_"+DateTimeHelper.getDateTime("MM-dd-yy_HH-mm-ss")+".txt";
        //fileLocation = properties.getProperty("UploadFileLocation")
        fileLocation = Environment.getExternalStorageDirectory()+ File.separator+filename;
        try {
            FileInputStream fis = new FileInputStream (new File(fileLocation));  // 2nd line
            // FileOutputStream fos = context.openFileOutput(fileLocation, Context.MODE_PRIVATE);
            //TODO Get rows from db

            //TODO write all data to the file

            return fileLocation;

        } catch (FileNotFoundException e) {
            Log.e("FileWriteHelper",e.toString());
        }
        return "Exception";
    }
    public String CompressFile(String zipFileLocation, String normalFileLocation)
    {
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFileLocation);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            byte data[] = new byte[BUFFER];


                Log.v("Compress", "Adding: " + normalFileLocation);
                FileInputStream fi = new FileInputStream(normalFileLocation);
                origin = new BufferedInputStream(fi, BUFFER);

                ZipEntry entry = new ZipEntry(normalFileLocation.substring(normalFileLocation.lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;

                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            out.close();
            return zipFileLocation;
            }
        catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return "Exception";
    }
}
