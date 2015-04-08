package cmpe.alpha.fitwhiz.controllers.common;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.widget.Toast;

import cmpe.alpha.fitwhiz.HelperLibrary.CountHelper;
import cmpe.alpha.fitwhiz.HelperLibrary.DateTimeHelper;
import cmpe.alpha.fitwhiz.lib.FitwhizApplication;
import cmpe.alpha.fitwhiz.HelperLibrary.MathHelper;
import cmpe.alpha.fitwhiz.HelperLibrary.ReadingsAnalyzer;
import cmpe.alpha.fitwhiz.models.AccelerometerTableOperations;
import cmpe.alpha.fitwhiz.models.HumidityTableOperations;
import cmpe.alpha.fitwhiz.models.TemperatureTableOperations;

/**
 * Created by rajagopalan on 2/22/15.
 */
public class SensorService extends Service implements SensorEventListener {

    private FitwhizApplication fitwhizApplication;
    CountHelper countHelper;
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        ReadingsAnalyzer readingsAnalyzer = new ReadingsAnalyzer(fitwhizApplication);
        if(sensorEvent.sensor.getType()==Sensor.TYPE_AMBIENT_TEMPERATURE)
        {
            String timestamp = DateTimeHelper.getDefaultFormattedDateTime();
            double t_val = sensorEvent.values[0];
            TemperatureTableOperations temperatureTableOperations = new TemperatureTableOperations(this);
            temperatureTableOperations.insertValue(t_val, timestamp);
            fitwhizApplication.setTVal(t_val);
            readingsAnalyzer.analyzeTemperature(t_val);
        }

        if(sensorEvent.sensor.getType()==Sensor.TYPE_RELATIVE_HUMIDITY)
        {
            String timestamp = DateTimeHelper.getDefaultFormattedDateTime();
            double h_val = sensorEvent.values[0];
            HumidityTableOperations humidityTableOperations = new HumidityTableOperations(this);
            humidityTableOperations.insertValue(h_val, timestamp);
            fitwhizApplication.setHVal(h_val);
            readingsAnalyzer.analyzeHumidity(h_val);
        }
        if(sensorEvent.sensor.getType()==Sensor.TYPE_LINEAR_ACCELERATION)
        {
            String timestamp = DateTimeHelper.getDefaultFormattedDateTime();
            double x_val = Double.parseDouble(String.valueOf(sensorEvent.values[0]));
            double y_val = Double.parseDouble(String.valueOf(sensorEvent.values[0]));
            double z_val = Double.parseDouble(String.valueOf(sensorEvent.values[0]));
            AccelerometerTableOperations accelerometerTableOperations = new AccelerometerTableOperations(this);
            accelerometerTableOperations.insertValue(x_val, y_val, z_val, timestamp);
            fitwhizApplication.setXVal(x_val);
            fitwhizApplication.setYVal(y_val);
            fitwhizApplication.setZVal(z_val);
            readingsAnalyzer.analyzeAcceleration(MathHelper.getResultantAcceleration(x_val,y_val,z_val));
            countHelper.AnalyzeValues(x_val,y_val,z_val);
        }
    }

    public void onStart(Intent intent, int startid)
    {
        fitwhizApplication = (FitwhizApplication) this.getApplication();
        countHelper = new CountHelper(fitwhizApplication,getApplicationContext());
        //Getting count and accelerometer sensors
        SensorManager sm=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        Sensor temperatureSensor=sm.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        Sensor humiditySensor = sm.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        Sensor accelerometerSensor = sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if(temperatureSensor!=null)
        {
            Toast.makeText(this, "Temperature sensor available", Toast.LENGTH_SHORT).show();
            sm.registerListener(this, temperatureSensor, SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
        }
        if(humiditySensor!=null)
        {
            Toast.makeText(this, "Humidity Sensor is available", Toast.LENGTH_SHORT).show();
            sm.registerListener(this, humiditySensor, SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
        }
        if(accelerometerSensor!=null)
        {
            Toast.makeText(this, "Accelerometer Sensor is available", Toast.LENGTH_SHORT).show();
            sm.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //TODO

    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO
        return null;
    }
}
