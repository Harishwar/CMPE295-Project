package cmpe.alpha.fitwhiz.HelperLibrary;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Date;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import cmpe.alpha.fitwhiz.controllers.LoginActivity;
import cmpe.alpha.fitwhiz.models.AccelerometerTableOperations;
import cmpe.alpha.fitwhiz.models.HumidityTableOperations;
import cmpe.alpha.fitwhiz.models.TemperatureTableOperations;

/**
 * Created by rajagopalan on 2/21/15.
 */
public class FileOperationsHelper {
    Context context;
    String fileLocation = "";
    PropertiesReader propertiesReader;
    Properties properties;
    private int BUFFER = 1000;
    public FileOperationsHelper(Context context)
    {
        this.context = context;
    }
    public String WriteDBToFile()
    {
        //Delete Dir if already exists
        this.DeleteDirectory("Fitwhiz");
        propertiesReader=new PropertiesReader(context);
        properties=propertiesReader.getProperties("Fitwhiz.properties");
        String filename = "Fitwhiz_backup_"+DateTimeHelper.getDateTime("MM-dd-yy_HH-mm-ss")+".txt";
        //fileLocation = properties.getProperty("UploadFileLocation")
        fileLocation = Environment.getExternalStorageDirectory()+ "/Fitwhiz/";
        try {
            File file = new File(fileLocation);
            file.mkdirs();
            File newFile = new File(fileLocation,filename);
            Writer writer = new BufferedWriter(new FileWriter(newFile));

            //Get Data from DB
            AccelerometerTableOperations accelerometerTableOperations = new AccelerometerTableOperations(context);
            HumidityTableOperations humidityTableOperations = new HumidityTableOperations(context);
            TemperatureTableOperations temperatureTableOperations = new TemperatureTableOperations(context);
            long end = System.currentTimeMillis();

            //To get 1 hour period
            long start = end - 3600000;
            String startDate = DateTimeHelper.formatDateTime("yyyy-MM-dd HH:mm:ss",new Date(start));
            String endTime = DateTimeHelper.formatDateTime("yyyy-MM-dd HH:mm:ss",new Date(end));
            accelerometerTableOperations.insertValue(1.0,2.0,3.0, DateTimeHelper.formatDateTime("yyyy-MM-dd HH:mm:ss", new Date(start+1000)));
            humidityTableOperations.insertValue(33.0, DateTimeHelper.formatDateTime("yyyy-MM-dd HH:mm:ss", new Date(start+1000)));
            temperatureTableOperations.insertValue(37.0, DateTimeHelper.formatDateTime("yyyy-MM-dd HH:mm:ss", new Date(start+1000)));
            double xVal = accelerometerTableOperations.getAggregateForSpecifiedTimeRange(startDate, endTime, "x_val");
            double yVal = accelerometerTableOperations.getAggregateForSpecifiedTimeRange(startDate, endTime, "y_val");
            double zVal = accelerometerTableOperations.getAggregateForSpecifiedTimeRange(startDate, endTime, "z_val");
            double hVal = humidityTableOperations.getAggregateForSpecifiedTimeRange(startDate,endTime,"h_val");
            double tVal = temperatureTableOperations.getAggregateForSpecifiedTimeRange(startDate,endTime,"t_val");

            //Write to the file
            writer.append("SensorId=123");
            writer.append("x_val="+xVal);
            writer.append("y_val="+yVal);
            writer.append("z_val="+zVal);
            writer.append("h_val="+hVal);
            writer.append("t_val="+tVal);
            writer.close();

            return fileLocation+filename;

        } catch (FileNotFoundException e) {
            Log.e("FileWriteHelper",e.toString());
        } catch (IOException e) {
            e.printStackTrace();
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
    public boolean DeleteDirectory(String dirName)
    {
        try
        {
            File file=new File(Environment.getExternalStorageDirectory()+"/"+dirName);
            if(file.isDirectory())
            {
                file.delete();
                return true;
            }
        }
        catch (Exception ex)
        {
            Log.e(getClass().getSimpleName(),ex.toString());
        }
        return false;
    }
}
