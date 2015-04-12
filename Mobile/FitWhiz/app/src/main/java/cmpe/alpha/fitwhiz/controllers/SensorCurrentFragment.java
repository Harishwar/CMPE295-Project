package cmpe.alpha.fitwhiz.controllers;


import android.app.Activity;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ti.util.Point3D;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.DecimalFormat;
import java.util.ArrayList;

import cmpe.alpha.fitwhiz.HelperLibrary.CountHelper;
import cmpe.alpha.fitwhiz.HelperLibrary.DateTimeHelper;
import cmpe.alpha.fitwhiz.HelperLibrary.MathHelper;
import cmpe.alpha.fitwhiz.HelperLibrary.NotificationHelper;
import cmpe.alpha.fitwhiz.HelperLibrary.ReadingsAnalyzer;
import cmpe.alpha.fitwhiz.R;
import cmpe.alpha.fitwhiz.lib.FitwhizApplication;
import cmpe.alpha.fitwhiz.lib.NotificationPriority;
import cmpe.alpha.fitwhiz.lib.SensorType;
import cmpe.alpha.fitwhiz.lib.TextViewType;
import cmpe.alpha.fitwhiz.models.AccelerometerTableOperations;
import cmpe.alpha.fitwhiz.models.GyroscopeTableOperations;
import cmpe.alpha.fitwhiz.models.HumidityTableOperations;
import cmpe.alpha.fitwhiz.models.MagnetometerTableOperations;
import cmpe.alpha.fitwhiz.models.TemperatureTableOperations;
import cmpe.alpha.fitwhiz.sensortag.BarometerCalibrationCoefficients;
import cmpe.alpha.fitwhiz.sensortag.Sensor;
import cmpe.alpha.fitwhiz.sensortag.SensorTagGatt;
import cmpe.alpha.fitwhiz.sensortag.SimpleKeysStatus;

/**
 * A simple {@link Fragment} subclass.
 */
public class SensorCurrentFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    private FitwhizApplication application;
    private DecimalFormat decimal = new DecimalFormat("+0.00;-0.00");

    private Activity thisActivity;
    private TextView accVal;
    private TextView tVal;
    private TextView hVal;

    public static LineChart getmChart() {
        return mChart;
    }

    public static void setmChart(LineChart mChart) {
        SensorCurrentFragment.mChart = mChart;
    }

    private static LineChart mChart;
    /*

        private double magnetometer = 0, gyroscope = 0, humidity = 0, temperature = 0, pressure = 0, accelerometer = 0;
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
    */

    public boolean setValues(SensorType key)
    {
        switch (key)
        {
            case ACCELEROMETER:
                LineChart mChart =getmChart();
                if(mChart==null)
                {
                    return false;
                }

                // no description text
                mChart.setDescription("");
                mChart.setNoDataTextDescription("You need to provide data for the chart.");

                // enable value highlighting
                mChart.setHighlightEnabled(true);

                // enable touch gestures
                mChart.setTouchEnabled(true);

                // enable scaling and dragging
                mChart.setDragEnabled(true);
                mChart.setScaleEnabled(true);
                mChart.setPinchZoom(true);

                mChart.setHighlightIndicatorEnabled(false);
                String[] x= new String[2];
                x[0] = "Average";
                x[1] = "current";
                double[] y = new double[2];
                if(application.getResult_hVal()==0)
                {
                    y[0] = 1000;
                }
                else {
                    y[0] = application.getResult_hVal();
                }
                y[1] = application.getHVal();
                // add data
                setData(x, y);
                return true;
        }
        return false;
    }

    private void setData(String[] x, double[] y) {

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < x.length; i++) {
            xVals.add(x[i]);
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < y.length; i++) {
            yVals.add(new Entry(Float.parseFloat(y[i]+""), i));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, "Humidity");
        // set1.setFillAlpha(110);
        // set1.setFillColor(Color.RED);

        // set the line to be drawn like this "- - - - - -"
        set1.enableDashedLine(10f, 5f, 0f);
        set1.setColor(Color.RED);
        set1.setCircleColor(Color.GREEN);
        set1.setLineWidth(1f);
        set1.setCircleSize(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        //set1.setFillAlpha(65);
        //set1.setFillColor(Color.BLACK);
//        set1.setDrawFilled(true);
        // set1.setShader(new LinearGradient(0, 0, 0, mChart.getHeight(),
        // Color.BLACK, Color.WHITE, Shader.TileMode.MIRROR));

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        // set data
        getmChart().setData(data);
    }

    public SensorCurrentFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    private View view;

    public TextView getAcc() {
        return this.acc;
    }

    public static void setAcc(TextView a) {
        acc = a;
    }

    private static TextView acc;

    public static TextView getMag() {
        return mag;
    }

    public static void setMag(TextView mag) {
        SensorCurrentFragment.mag = mag;
    }

    public static TextView getHum() {
        return hum;
    }

    public static void setHum(TextView hum) {
        SensorCurrentFragment.hum = hum;
    }

    public static void setPressure(TextView pressure) {
        SensorCurrentFragment.pressure = pressure;
    }

    public static TextView getGyro() {
        return gyro;
    }

    public static void setGyro(TextView gyro) {
        SensorCurrentFragment.gyro = gyro;
    }

    public static TextView getLux() {
        return lux;
    }

    public static void setLux(TextView lux) {
        SensorCurrentFragment.lux = lux;
    }

    public static TextView getIrt_amb() {
        return irt_amb;
    }

    public static void setIrt_amb(TextView irt_amb) {
        SensorCurrentFragment.irt_amb = irt_amb;
    }

    public static TextView getIrt_body() {
        return irt_body;
    }

    public static void setIrt_body(TextView irt_body) {
        SensorCurrentFragment.irt_body = irt_body;
    }

    private static TextView mag;
    private static TextView hum;

    public static TextView getPressure() {
        return pressure;
    }

    private static TextView pressure;
    private static TextView gyro;
    private static TextView lux;
    private static TextView irt_amb;
    private static TextView irt_body;

    public static TextView getStepCount() {
        return stepCount;
    }

    public static void setStepCount(TextView stepCount) {
        SensorCurrentFragment.stepCount = stepCount;
    }

    private static TextView stepCount;




    private static final double PA_PER_METER = 12.0;

    public void setActivity(Activity act) {
        thisActivity = act;
    }

    public void setApplication(FitwhizApplication app) {
        application = app;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View sensorCurrentFragment = inflater.inflate(R.layout.fragment_sensor_current, container, false);
        application = (FitwhizApplication) this.getActivity().getApplication();
        TextView accelerometerView = (TextView) sensorCurrentFragment.findViewById(R.id.Accelerometer_current_val);
        TextView humidityView = (TextView) sensorCurrentFragment.findViewById(R.id.humidity_current_val);
        TextView magView = (TextView) sensorCurrentFragment.findViewById(R.id.magnet_current);
        TextView gyroView = (TextView) sensorCurrentFragment.findViewById(R.id.Gyroscope_current_val);
        TextView pressureView = (TextView) sensorCurrentFragment.findViewById(R.id.pressure_current);
        TextView temperatureView = (TextView) sensorCurrentFragment.findViewById(R.id.temperature_current_val);
        TextView stepCountView = (TextView) sensorCurrentFragment.findViewById(R.id.stepCountValue);
        mChart = (LineChart) sensorCurrentFragment.findViewById(R.id.chart1);
        accelerometerView.setText(application.getXVal() + "-" + application.getYVal() + "-" + application.getZVal());
        temperatureView.setText(application.getAmbTemp() + "");
        humidityView.setText(application.getHVal() + "");
        gyroView.setText(application.getG_xVal() + "-" + application.getG_yVal() + "-" + application.getG_zVal());
        magView.setText(decimal.format(application.getM_xVal()) + "," + decimal.format(application.getM_yVal()) + "," + decimal.format(application.getM_zVal()));
        pressureView.setText(application.getP_val()+" at h: "+application.getP_val());
        setAcc(accelerometerView);
        setGyro(gyroView);
        setMag(magView);
        setIrt_body(temperatureView);
        setPressure(pressureView);
        setHum(humidityView);
        setIrt_amb(temperatureView);
        setStepCount(stepCountView);
        getStepCount().setText(application.getCount()+"");
        setValues(SensorType.ACCELEROMETER);
        return sensorCurrentFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((DashboardActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
        thisActivity = activity;
    }


    public boolean setValues(TextViewType key,String value)
    {
        switch (key)
        {
            case ACCELEROMETER:
                if(getAcc() != null)
                {
                    getAcc().setText(value);
                }
                return true;
            case HUMIDITY:
                if(getHum()!=null)
                {
                    getHum().setText(value);
                }
                return true;
            case MAGNETOMETER:
                if(getMag() != null)
                {
                    getMag().setText(value);
                }
                return true;
            case PRESSURE:
                if(getPressure()!=null)
                {
                    getPressure().setText(value);
                }
                return true;
            case IRT_AMBIENT:
                if(getIrt_amb() != null)
                {
                    getIrt_amb().setText(value);
                }
                return true;
            case IRT_BODY:
                if(getIrt_amb()!=null)
                {
                    getIrt_body().setText(value);
                }
                return true;
            case LUXOMETER:

                if(getLux() != null)
                {
                    getLux().setText(value);
                }
                return true;
            case GYROSCOPE:
                if(getGyro()!=null)
                {
                    getGyro().setText(value);
                }
                return true;
            case STEPCOUNT:
                if(getStepCount()!=null)
                {
                    getStepCount().setText(value);
                }
                return true;
            default:
                return false;
        }
    }

    /**
     * Handle changes in sensor values
     */
    public void onCharacteristicChanged(String uuidStr, byte[] rawValue) {
        Point3D v;
        String msg;
        ReadingsAnalyzer readingsAnalyzer = new ReadingsAnalyzer(this.application);
//        SensorCurrentFragment fragment = (SensorCurrentFragment)this.getFragmentManager().findFragmentById(R.id.fragment_sensor_current);


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
            readingsAnalyzer.analyzeAcceleration(MathHelper.getResultantAcceleration(v.x, v.y, v.z));
            setValues(TextViewType.ACCELEROMETER,msg.replace('\n',','));
            CountHelperThread cht = new CountHelperThread(v.x,v.y,v.z,application,thisActivity.getApplicationContext());
       ///     ( (TextView)fragment.getActivity().findViewById(R.id.Accelerometer_current_val)).setText(new DecimalFormat("00.00").format(v.x));
            cht.run();
            setValues(TextViewType.STEPCOUNT,application.getCount()+"");
        }

        if (uuidStr.equals(SensorTagGatt.UUID_MAG_DATA.toString())) {

            v = Sensor.MAGNETOMETER.convert(rawValue);
            msg = decimal.format(v.x) + "\n" + decimal.format(v.y) + "\n"
                    + decimal.format(v.z) + "\n";
            //mMagValue.setText(msg);*/
            application.setM_xVal(v.x);
            application.setM_yVal(v.y);
            application.setM_zVal(v.z);
            Activity activity = thisActivity;
            MagnetometerTableOperations magnetometerTableOperations = new MagnetometerTableOperations(activity.getApplicationContext());
            magnetometerTableOperations.insertValue(v.x, v.y, v.z, DateTimeHelper.getDefaultFormattedDateTime());
            setValues(TextViewType.MAGNETOMETER,msg.replace('\n',','));
        }

        if (uuidStr.equals(SensorTagGatt.UUID_OPT_DATA.toString())) {
            v = Sensor.LUXOMETER.convert(rawValue);
            msg = decimal.format(v.x) + "";
            setValues(TextViewType.LUXOMETER,msg);
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
            Activity activity = thisActivity;
            GyroscopeTableOperations gyroscopeTableOperations = new GyroscopeTableOperations(activity.getApplicationContext());
            gyroscopeTableOperations.insertValue(v.x, v.y, v.z, DateTimeHelper.getDefaultFormattedDateTime());
            setValues(TextViewType.GYROSCOPE,msg.replace("\n",","));
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
            //setValues(TextViewType.IRT_BODY,decimal.format(v.y));
            setValues(TextViewType.IRT_AMBIENT,decimal.format(v.x));

        }

        if (uuidStr.equals(SensorTagGatt.UUID_HUM_DATA.toString())) {
            v = Sensor.HUMIDITY.convert(rawValue);
            msg = decimal.format(v.x);
            Activity activity = thisActivity;
            HumidityTableOperations humidityTableOperations = new HumidityTableOperations(activity.getApplicationContext());
            humidityTableOperations.insertValue(Double.parseDouble(msg), DateTimeHelper.getDefaultFormattedDateTime());
            application.setHVal(Double.parseDouble(msg));
            readingsAnalyzer.analyzeHumidity(Double.parseDouble(msg));
            setValues(TextViewType.HUMIDITY,msg);
        }

        if (uuidStr.equals(SensorTagGatt.UUID_BAR_DATA.toString())) {
            v = Sensor.BAROMETER.convert(rawValue);

			double h = (v.x - BarometerCalibrationCoefficients.INSTANCE.heightCalibration)
			    / PA_PER_METER;
			h = (double) Math.round(-h * 10.0) / 10.0;
			msg = decimal.format(v.x / 100.0f) + "\n" + h;
			//mBarValue.setText(msg);
            application.setP_Hval(h);
            application.setP_val(Double.parseDouble(decimal.format(v.x/100.0f)));
            setValues(TextViewType.PRESSURE,msg);
        }

        if (uuidStr.equals(SensorTagGatt.UUID_KEY_DATA.toString())) {
            int keys = rawValue[0];
            SimpleKeysStatus s;
            final int imgBtn;
            s = Sensor.SIMPLE_KEYS.convertKeys((byte) (keys&3));
            PendingIntent pIntent = PendingIntent.getActivity(thisActivity.getApplicationContext(), 0, new Intent(thisActivity.getApplicationContext(),DashboardActivity.class),0);
            NotificationHelper helper = new NotificationHelper(thisActivity.getApplicationContext());
            switch (s) {
                case OFF_ON:
                   // imgBtn = R.drawable.buttonsoffon;
                    helper.SendNotification("Right", "Right Button pressed", pIntent, NotificationPriority.LOW,"");
                    break;
                case ON_OFF:
                    //imgBtn = R.drawable.buttonsonoff;
                    helper.SendNotification("Left", "Left Button pressed", pIntent, NotificationPriority.LOW,"");
                    break;
                case ON_ON:
                  //  imgBtn = R.drawable.buttonsonon;
                    helper.SendNotification("Both", "Both Buttons pressed", pIntent, NotificationPriority.LOW,"");
                    break;
                default:
                  //  imgBtn = R.drawable.buttonsoffoff;
                    //helper.SendNotification("Both", "Both Buttons pressed", pIntent, NotificationPriority.LOW,"");
                    break;
            }
        }
    }

    public class CountHelperThread implements Runnable{
        private double x, y, z;
        private FitwhizApplication app;
        private Context context;
        public CountHelperThread(double x, double y, double z, FitwhizApplication app, Context context)
        {
            this.app=app;
            this.context = context;
            this.x=x;
            this.y=y;
            this.z=z;
        }

        public void run()
        {
            CountHelper ch = new CountHelper(app,context);
            ch.AnalyzeValues(x,y,z);
        }
    }

    @Override
    public void onActivityCreated(Bundle b)
    {
        super.onActivityCreated(b);
        System.out.println("I am created");
    }

}
