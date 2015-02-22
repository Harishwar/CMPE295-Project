package cmpe.alpha.fitwhiz.controllers;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cmpe.alpha.fitwhiz.HelperLibrary.FitwhizApplication;
import cmpe.alpha.fitwhiz.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SensorCurrentFragment extends Fragment
{
    private  FitwhizApplication application;

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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View sensorCurrentFragment = inflater.inflate(R.layout.fragment_sensor_current, container, false);
        application=(FitwhizApplication)this.getActivity().getApplication();
        TextView accelerometerView = (TextView)sensorCurrentFragment.findViewById(R.id.Accelerometer_current_val);
        TextView humidityView = (TextView)sensorCurrentFragment.findViewById(R.id.humidity_current_val);
        TextView temperatureView = (TextView)sensorCurrentFragment.findViewById(R.id.temperature_current_val);
        accelerometerView.setText(application.getXVal()+"-"+application.getYVal()+"-"+application.getZVal());
        temperatureView.setText(application.getTVal()+"");
        humidityView.setText(application.getHVal()+"");

        return sensorCurrentFragment;
    }


}
