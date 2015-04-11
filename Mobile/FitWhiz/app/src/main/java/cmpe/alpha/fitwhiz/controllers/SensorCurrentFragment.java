package cmpe.alpha.fitwhiz.controllers;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ti.util.Point3D;

import java.text.DecimalFormat;

import cmpe.alpha.fitwhiz.HelperLibrary.DateTimeHelper;
import cmpe.alpha.fitwhiz.HelperLibrary.ReadingsAnalyzer;
import cmpe.alpha.fitwhiz.R;
import cmpe.alpha.fitwhiz.lib.FitwhizApplication;
import cmpe.alpha.fitwhiz.models.AccelerometerTableOperations;
import cmpe.alpha.fitwhiz.models.HumidityTableOperations;
import cmpe.alpha.fitwhiz.models.TemperatureTableOperations;
import cmpe.alpha.fitwhiz.sensortag.Sensor;
import cmpe.alpha.fitwhiz.sensortag.SensorTagGatt;

/**
 * A simple {@link Fragment} subclass.
 */
public class SensorCurrentFragment extends Fragment
{
    private static final String ARG_SECTION_NUMBER = "section_number";

    private  FitwhizApplication application;
    private DecimalFormat decimal = new DecimalFormat("+0.00;-0.00");

    private Activity thisActivity;

    private TextView accVal;
    private TextView tVal;
    private TextView hVal;


    private double magnetometer=0,gyroscope=0,humidity=0,temperature=0,pressure=0,accelerometer=0;

    public double getMagnetometer() {
        return magnetometer;
    }

    public void setMagnetometer(double magnetometer) {
        this.magnetometer = magnetometer;
    }

    public double getGyroscope() {
        return gyroscope;
    }

    public void setGyroscope(double gyroscope) {
        this.gyroscope = gyroscope;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getAccelerometer() {
        return accelerometer;
    }

    public void setAccelerometer(double accelerometer) {
        this.accelerometer = accelerometer;
    }

    public SensorCurrentFragment()
    {
        // Required empty public constructor
    }

    public void setActivity(Activity act)
    {
        thisActivity = act;
    }

    public void setApplication(FitwhizApplication app)
    {
        application = app;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View sensorCurrentFragment = inflater.inflate(R.layout.fragment_sensor_current, container, false);
        application = (FitwhizApplication) this.getActivity().getApplication();
        TextView accelerometerView = (TextView)sensorCurrentFragment.findViewById(R.id.Accelerometer_current_val);
        TextView humidityView = (TextView)sensorCurrentFragment.findViewById(R.id.humidity_current_val);
        TextView magView = (TextView)sensorCurrentFragment.findViewById(R.id.magnet_current);
        TextView gyroView = (TextView)sensorCurrentFragment.findViewById(R.id.sensor_gyro_text_live);

        TextView temperatureView = (TextView)sensorCurrentFragment.findViewById(R.id.temperature_current_val);
        accelerometerView.setText(application.getXVal()+"-"+application.getYVal()+"-"+application.getZVal());
        temperatureView.setText(application.getAmbTemp()+"");
        humidityView.setText(application.getHVal()+"");
        gyroView.setText(application.getG_xVal()+"-"+application.getG_yVal()+"-"+application.getG_zVal());
        magView.setText(application.getM_xVal()+"-"+application.getM_yVal()+"-"+application.getM_zVal());
        return sensorCurrentFragment;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        ((DashboardActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
        thisActivity = activity;
    }

    /**
     * Handle changes in sensor values
     * */
    public void onCharacteristicChanged(String uuidStr, byte[] rawValue) {
        Point3D v;
        String msg;
        ReadingsAnalyzer readingsAnalyzer = new ReadingsAnalyzer(this.application);


        if (uuidStr.equals(SensorTagGatt.UUID_ACC_DATA.toString())) {
            v = Sensor.ACCELEROMETER.convert(rawValue);
            msg = decimal.format(v.x) + "\n" + decimal.format(v.y) + "\n"
                    + decimal.format(v.z) + "\n";
            Activity activity = thisActivity;
            AccelerometerTableOperations accelerometerTableOperations = new AccelerometerTableOperations(activity.getApplicationContext());
            accelerometerTableOperations.insertValue(v.x, v.y, v.z, DateTimeHelper.getDefaultFormattedDateTime());
            application.setXVal(v.x);
            application.setYVal(v.y);
            application.setZVal(v.z);
            System.out.println(application.getXVal()+" "+application.getYVal()+" "+application.getZVal());
        }

        if (uuidStr.equals(SensorTagGatt.UUID_MAG_DATA.toString())) {

			v = Sensor.MAGNETOMETER.convert(rawValue);
			msg = decimal.format(v.x) + "\n" + decimal.format(v.y) + "\n"
			    + decimal.format(v.z) + "\n";
			//mMagValue.setText(msg);*/
           application.setM_xVal(v.x);
            application.setM_yVal(v.y);
            application.setM_zVal(v.z);
        }

        if (uuidStr.equals(SensorTagGatt.UUID_OPT_DATA.toString())) {
			v = Sensor.LUXOMETER.convert(rawValue);
			msg = decimal.format(v.x) + "\n";
			//mLuxValue.setText(msg);*/
        }

        if (uuidStr.equals(SensorTagGatt.UUID_GYR_DATA.toString())) {
			v = Sensor.GYROSCOPE.convert(rawValue);
			msg = decimal.format(v.x) + "\n" + decimal.format(v.y) + "\n"
			    + decimal.format(v.z) + "\n";
			//mGyrValue.setText(msg);*/
            application.setG_xVal(v.x);
            application.setG_yVal(v.y);
            application.setG_zVal(v.z);
        }

        if (uuidStr.equals(SensorTagGatt.UUID_IRT_DATA.toString())) {
            v = Sensor.IR_TEMPERATURE.convert(rawValue);
            msg = decimal.format(v.x);
            //mAmbValue.setText(msg);
            //msg = decimal.format(v.y) + "\n";
            Activity activity = thisActivity;
            TemperatureTableOperations temperatureTableOperations = new TemperatureTableOperations(activity.getApplicationContext());
            temperatureTableOperations.insertValue(Double.parseDouble(msg), DateTimeHelper.getDefaultFormattedDateTime());
            application.setTVal(Double.parseDouble(msg));
            application.setAmbTemp(v.x);
            application.setBodyTemp(v.y);
            readingsAnalyzer.analyzeTemperature(Double.parseDouble(msg));
        }

        if (uuidStr.equals(SensorTagGatt.UUID_HUM_DATA.toString())) {
            v = Sensor.HUMIDITY.convert(rawValue);
            msg = decimal.format(v.x);
            Activity activity = thisActivity;
            HumidityTableOperations humidityTableOperations = new HumidityTableOperations(activity.getApplicationContext());
            humidityTableOperations.insertValue(Double.parseDouble(msg), DateTimeHelper.getDefaultFormattedDateTime());
            application.setHVal(Double.parseDouble(msg));
            readingsAnalyzer.analyzeHumidity(Double.parseDouble(msg));
        }

        if (uuidStr.equals(SensorTagGatt.UUID_BAR_DATA.toString())) {/*
			v = Sensor.BAROMETER.convert(rawValue);

			double h = (v.x - BarometerCalibrationCoefficients.INSTANCE.heightCalibration)
			    / PA_PER_METER;
			h = (double) Math.round(-h * 10.0) / 10.0;
			msg = decimal.format(v.x / 100.0f) + "\n" + h;
			mBarValue.setText(msg);
        }

        if (uuidStr.equals(SensorTagGatt.UUID_KEY_DATA.toString())) {
            int keys = rawValue[0];
            SimpleKeysStatus s;
            final int imgBtn;
            s = Sensor.SIMPLE_KEYS.convertKeys((byte) (keys&3));

            switch (s) {
                case OFF_ON:
                    imgBtn = R.drawable.buttonsoffon;
                    break;
                case ON_OFF:
                    imgBtn = R.drawable.buttonsonoff;
                    break;
                case ON_ON:
                    imgBtn = R.drawable.buttonsonon;
                    break;
                default:
                    imgBtn = R.drawable.buttonsoffoff;
                    break;
            }*/
        }
    }

}
