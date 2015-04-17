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
import java.util.Date;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import cmpe.alpha.fitwhiz.lib.FitwhizApplication;
import cmpe.alpha.fitwhiz.models.AccelerometerTableOperations;
import cmpe.alpha.fitwhiz.models.CountTableOperations;
import cmpe.alpha.fitwhiz.models.GyroscopeTableOperations;
import cmpe.alpha.fitwhiz.models.HumidityTableOperations;
import cmpe.alpha.fitwhiz.models.MagnetometerTableOperations;
import cmpe.alpha.fitwhiz.models.PressureTableOperations;
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
    private FitwhizApplication app;
    public FileOperationsHelper(Context context)
    {
        this.context = context;
        app = (FitwhizApplication)context.getApplicationContext();
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
            /*
            File file = new File(fileLocation);
            file.mkdirs();
            File newFile = new File(fileLocation,filename);
            Writer writer = new BufferedWriter(new FileWriter(newFile));
*/
            //Get Data from DB
            AccelerometerTableOperations accelerometerTableOperations = new AccelerometerTableOperations(context);
            HumidityTableOperations humidityTableOperations = new HumidityTableOperations(context);
            TemperatureTableOperations temperatureTableOperations = new TemperatureTableOperations(context);
            CountTableOperations countTableOperations = new CountTableOperations(context);
            MagnetometerTableOperations magnetometerTableOperations = new MagnetometerTableOperations(context);
            GyroscopeTableOperations gyroscopeTableOperations = new GyroscopeTableOperations(context);
            PressureTableOperations pressureTableOperations = new PressureTableOperations(context);
            long end = System.currentTimeMillis();

            //To get 1 hour period
            long start = end - 3600000;
            String startDate = DateTimeHelper.formatDateTime("yyyy-MM-dd HH:mm:ss",new Date(start));
            String endTime = DateTimeHelper.formatDateTime("yyyy-MM-dd HH:mm:ss",new Date(end));
            //accelerometerTableOperations.insertValue(1.0,2.0,3.0, DateTimeHelper.formatDateTime("yyyy-MM-dd HH:mm:ss", new Date(start+1000)));
            //humidityTableOperations.insertValue(33.0, DateTimeHelper.formatDateTime("yyyy-MM-dd HH:mm:ss", new Date(start+1000)));
            //temperatureTableOperations.insertValue(37.0, DateTimeHelper.formatDateTime("yyyy-MM-dd HH:mm:ss", new Date(start+1000)));
            String sensorId = app.getSensorId();
            double xVal = accelerometerTableOperations.getAggregateForSpecifiedTimeRange(startDate, endTime, "x_val");
            double yVal = accelerometerTableOperations.getAggregateForSpecifiedTimeRange(startDate, endTime, "y_val");
            double zVal = accelerometerTableOperations.getAggregateForSpecifiedTimeRange(startDate, endTime, "z_val");
            double hVal = humidityTableOperations.getAggregateForSpecifiedTimeRange(startDate,endTime,"h_val");
            double ambVal = temperatureTableOperations.getAggregateForSpecifiedTimeRange(startDate,endTime,"amb_val");
            double bodyVal = temperatureTableOperations.getAggregateForSpecifiedTimeRange(startDate,endTime,"body_val");
            double m_xVal = magnetometerTableOperations.getAggregateForSpecifiedTimeRange(startDate, endTime, "x_val");
            double m_yVal = magnetometerTableOperations.getAggregateForSpecifiedTimeRange(startDate, endTime, "y_val");
            double m_zVal = magnetometerTableOperations.getAggregateForSpecifiedTimeRange(startDate, endTime, "z_val");
            double g_xVal = gyroscopeTableOperations.getAggregateForSpecifiedTimeRange(startDate, endTime, "x_val");
            double g_yVal = gyroscopeTableOperations.getAggregateForSpecifiedTimeRange(startDate, endTime, "y_val");
            double g_zVal = gyroscopeTableOperations.getAggregateForSpecifiedTimeRange(startDate, endTime, "z_val");
            double pVal = pressureTableOperations.getAggregateForSpecifiedTimeRange(startDate,endTime,"p_val");
            double stepCount = countTableOperations.getMaxCountForSpecifiedTimeRange(startDate,endTime,"count") - countTableOperations.getMinCountForSpecifiedTimeRange(startDate,endTime,"count");
            //Temporarily prepare JSON
            String json = "{  \n" +
                    "   \"SensorId\":\""+sensorId +"\",\n" +
                    "   \"acc_x\":"+xVal+",\n" +
                    "   \"acc_y\":"+yVal+",\n" +
                    "   \"acc_z\":"+zVal+",\n" +
                    "   \"h_val\":"+hVal+",\n" +
                    "   \"irt_body_val\":"+bodyVal+",\n" +
                    "   \"irt_ambient_val\":"+ambVal+",\n" +
                    "   \"mag_x\":"+m_xVal+",\n" +
                    "   \"mag_y\":"+m_yVal+",\n" +
                    "   \"mag_z\":"+m_zVal+",\n" +
                    "   \"gyro_x\":"+g_xVal+",\n" +
                    "   \"gyro_y\":"+g_yVal+",\n" +
                    "   \"gyro_z\":"+g_zVal+",\n" +
                    "   \"pressure\":"+pVal+",\n" +
                    "   \"StepCount\":"+stepCount+"\n" +
                    "}";
            UpdateDataHelper helper = new UpdateDataHelper(app);
            helper.execute(json,new PropertiesReader(this.context).getProperties("Fitwhiz.properties").getProperty("FileUploadUrl"));
/*
            //Write to the file
            writer.append("{");
            writer.write("\n");
            writer.append("'SensorId':'" + sensorId + "',");
            writer.write("\n");
            writer.append("x_val:'" + xVal+"',");
            writer.write("\n");
            writer.append("y_val:'" + yVal+"',");
            writer.write("\n");
            writer.append("z_val:'" + zVal+"',");
            writer.write("\n");
            writer.append("m_xVal:'" + m_xVal+"',");
            writer.write("\n");
            writer.append("m_yVal:'" + m_yVal+"',");
            writer.write("\n");
            writer.append("m_zVal:'" + m_zVal+"',");
            writer.write("\n");
            writer.append("g_xVal:'" + g_xVal+"',");
            writer.write("\n");
            writer.append("g_yVal:'" + g_yVal+"',");
            writer.write("\n");
            writer.append("g_zVal:'" + g_zVal+"',");
            writer.write("\n");
            writer.append("h_val:'" + hVal+"',");
            writer.write("\n");
            writer.append("t_val:'" + tVal+"',");
            writer.write("\n");
            writer.append("StepCount:'"+stepCount+"',");
            writer.write("\n");
            writer.write("}");
            writer.close();
*/
            return fileLocation+filename;
/*
        } catch (FileNotFoundException e) {
            Log.e("FileWriteHelper",e.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
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
