package cmpe.alpha.fitwhiz.controllers;


import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cmpe.alpha.fitwhiz.lib.FitwhizApplication;
import cmpe.alpha.fitwhiz.HelperLibrary.PropertiesReader;
import cmpe.alpha.fitwhiz.HelperLibrary.ResultsUpdater;
import cmpe.alpha.fitwhiz.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SensorHistoryFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

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

    public SensorHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View sensorHistoryFragment = inflater.inflate(R.layout.fragment_sensor_history, container, false);
        FitwhizApplication application=(FitwhizApplication)this.getActivity().getApplication();
        ResultsUpdater resultsUpdater=new ResultsUpdater(application);
        resultsUpdater.execute(new PropertiesReader(sensorHistoryFragment.getContext()).getProperties("Fitwhiz.properties").getProperty("FileUploadUrl"));
        ((TextView)sensorHistoryFragment.findViewById(R.id.historyTemperatureValue)).setText(application.getResult_tVal()+"");
        ((TextView)sensorHistoryFragment.findViewById(R.id.humidityHistoryValue)).setText(application.getResult_hVal()+"");
        ((TextView)sensorHistoryFragment.findViewById(R.id.historyAccelerometerValue)).setText(Math.ceil(application.getResult_xVal())+","+Math.ceil(application.getResult_yVal())+","+ Math.ceil(application.getResult_zVal()));


        return sensorHistoryFragment;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        ((DashboardActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

}
