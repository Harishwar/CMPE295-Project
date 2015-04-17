package cmpe.alpha.fitwhiz.controllers.common;

import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.ti.ble.common.BluetoothLeService;
import com.example.ti.util.Point3D;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cmpe.alpha.fitwhiz.HelperLibrary.CountHelper;
import cmpe.alpha.fitwhiz.HelperLibrary.DateTimeHelper;
import cmpe.alpha.fitwhiz.HelperLibrary.MathHelper;
import cmpe.alpha.fitwhiz.HelperLibrary.NotificationHelper;
import cmpe.alpha.fitwhiz.HelperLibrary.ReadingsAnalyzer;
import cmpe.alpha.fitwhiz.controllers.DashboardActivity;
import cmpe.alpha.fitwhiz.controllers.SensorCurrentFragment;
import cmpe.alpha.fitwhiz.lib.FitwhizApplication;
import cmpe.alpha.fitwhiz.lib.NotificationPriority;
import cmpe.alpha.fitwhiz.models.AccelerometerTableOperations;
import cmpe.alpha.fitwhiz.models.GyroscopeTableOperations;
import cmpe.alpha.fitwhiz.models.HumidityTableOperations;
import cmpe.alpha.fitwhiz.models.MagnetometerTableOperations;
import cmpe.alpha.fitwhiz.models.TemperatureTableOperations;
import cmpe.alpha.fitwhiz.sensortag.BarometerCalibrationCoefficients;
import cmpe.alpha.fitwhiz.sensortag.MagnetometerCalibrationCoefficients;
import cmpe.alpha.fitwhiz.sensortag.Sensor;
import cmpe.alpha.fitwhiz.sensortag.SensorTagGatt;
import cmpe.alpha.fitwhiz.sensortag.SimpleKeysStatus;

/**
 * Created by rajagopalan on 4/11/15.
 */
public class BleUpdatesReceiver extends BroadcastReceiver {
    private Context context;
    private Intent intent;
    private FitwhizApplication application;
    private DecimalFormat decimal = new DecimalFormat("+0.00;-0.00");
    private static final double PA_PER_METER = 12.0;


    // Activity
    public static final String EXTRA_DEVICE = "EXTRA_DEVICE";
    private static final int PREF_ACT_REQ = 0;
    private static final int FWUPDATE_ACT_REQ = 1;

    private SensorCurrentFragment mDeviceView = null;

    // BLE
    private BluetoothLeService mBtLeService = null;
    private BluetoothDevice mBluetoothDevice = null;
    private BluetoothGatt mBtGatt = null;
    private List<BluetoothGattService> mServiceList = null;
    private static final int GATT_TIMEOUT = 250; // milliseconds
    private boolean mServicesRdy = false;
    private boolean mIsReceiving = false;

    // SensorTagGatt
    private List<Sensor> mEnabledSensors = new ArrayList<Sensor>();
    private BluetoothGattService mOadService = null;
    private BluetoothGattService mConnControlService = null;
    private boolean mMagCalibrateRequest = true;
    private boolean mHeightCalibrateRequest = true;
    private boolean mIsSensorTag2;
    private String mFwRev;

    private void setError(String txt) {
        Toast.makeText(context, txt, Toast.LENGTH_LONG).show();
    }

    private void setStatus(String txt) {
        Toast.makeText(context, txt, Toast.LENGTH_SHORT).show();
    }

    private void enableDataCollection(boolean enable) {
        enableSensors(enable);
        enableNotifications(enable);
    }

    private void enableSensors(boolean f) {
        final boolean enable = f;

        for (Sensor sensor : mEnabledSensors) {
            UUID servUuid = sensor.getService();
            UUID confUuid = sensor.getConfig();

            // Skip keys
            if (confUuid == null)
                break;

            if (!mIsSensorTag2) {
                // Barometer calibration
                if (confUuid.equals(SensorTagGatt.UUID_BAR_CONF) && enable) {
                    calibrateBarometer();
                }
            }

            BluetoothGattService serv = mBtGatt.getService(servUuid);
            if (serv != null) {
                BluetoothGattCharacteristic charac = serv.getCharacteristic(confUuid);
                byte value = enable ? sensor.getEnableSensorCode()
                        : Sensor.DISABLE_SENSOR_CODE;
                if (mBtLeService.writeCharacteristic(charac, value)) {
                    mBtLeService.waitIdle(GATT_TIMEOUT);
                } else {
                    setError("Sensor config failed: " + serv.getUuid().toString());
                    break;
                }
            }
        }
    }

    private void calibrateBarometer() {
        if (mIsSensorTag2)
            return;

        UUID servUuid = Sensor.BAROMETER.getService();
        UUID configUuid = Sensor.BAROMETER.getConfig();
        BluetoothGattService serv = mBtGatt.getService(servUuid);
        BluetoothGattCharacteristic config = serv.getCharacteristic(configUuid);

        // Write the calibration code to the configuration registers
        mBtLeService.writeCharacteristic(config, Sensor.CALIBRATE_SENSOR_CODE);
        mBtLeService.waitIdle(GATT_TIMEOUT);
        BluetoothGattCharacteristic calibrationCharacteristic = serv
                .getCharacteristic(SensorTagGatt.UUID_BAR_CALI);
        mBtLeService.readCharacteristic(calibrationCharacteristic);
        mBtLeService.waitIdle(GATT_TIMEOUT);
    }

    private void enableNotifications(boolean f) {
        final boolean enable = f;

        for (Sensor sensor : mEnabledSensors) {
            UUID servUuid = sensor.getService();
            UUID dataUuid = sensor.getData();
            BluetoothGattService serv = mBtGatt.getService(servUuid);
            if (serv != null) {
                BluetoothGattCharacteristic charac = serv.getCharacteristic(dataUuid);

                if (mBtLeService.setCharacteristicNotification(charac, enable)) {
                    mBtLeService.waitIdle(GATT_TIMEOUT);
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    setError("Sensor notification failed: " + serv.getUuid().toString());
                    break;
                }
            }
        }
    }


    private void displayServices() {
        mServicesRdy = true;

        try {
            mServiceList = mBtLeService.getSupportedGattServices();
        } catch (Exception e) {
            e.printStackTrace();
            mServicesRdy = false;
        }

        // Characteristics descriptor readout done
        if (!mServicesRdy) {
            setError("Failed to read services");
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("I am getting it");
        this.context = context;
        this.intent = intent;
        this.application = (FitwhizApplication)context.getApplicationContext();
                final String action = intent.getAction();
                int status = intent.getIntExtra(BluetoothLeService.EXTRA_STATUS,
                        BluetoothGatt.GATT_SUCCESS);

                if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        //setStatus("Service discovery complete");
                        //displayServices();
                        //checkOad();
                        //enableDataCollection(true);
                        //getFirmwareRevison();
                    } else {
                        Toast.makeText(context, "Service discovery failed",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                } else if (BluetoothLeService.ACTION_DATA_NOTIFY.equals(action)) {
                    // Notification
                    byte[] value = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                    String uuidStr = intent.getStringExtra(BluetoothLeService.EXTRA_UUID);
                    onCharacteristicChanged(uuidStr, value);
                } else if (BluetoothLeService.ACTION_DATA_WRITE.equals(action)) {
                    // Data written
                    String uuidStr = intent.getStringExtra(BluetoothLeService.EXTRA_UUID);
                    onCharacteristicWrite(uuidStr, status);
                } else if (BluetoothLeService.ACTION_DATA_READ.equals(action)) {
                    // Data read
                    String uuidStr = intent.getStringExtra(BluetoothLeService.EXTRA_UUID);
                    byte[] value = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                    onCharacteristicsRead(uuidStr, value, status);
                }

                if (status != BluetoothGatt.GATT_SUCCESS) {
                    setError("GATT error code: " + status);
                }
            }

private void getFirmwareRevison() {
        UUID servUuid = SensorTagGatt.UUID_DEVINFO_SERV;
        UUID charUuid = SensorTagGatt.UUID_DEVINFO_FWREV;
        BluetoothGattService serv = mBtGatt.getService(servUuid);
        BluetoothGattCharacteristic charFwrev = serv.getCharacteristic(charUuid);

        // Write the calibration code to the configuration registers
        mBtLeService.readCharacteristic(charFwrev);
        mBtLeService.waitIdle(GATT_TIMEOUT);

        }

        void calibrateMagnetometer() {
        MagnetometerCalibrationCoefficients.INSTANCE.val.x = 0.0;
        MagnetometerCalibrationCoefficients.INSTANCE.val.y = 0.0;
        MagnetometerCalibrationCoefficients.INSTANCE.val.z = 0.0;

        mMagCalibrateRequest = true;
        }

        void calibrateHeight() {
        mHeightCalibrateRequest = true;
        }

private void onCharacteristicWrite(String uuidStr, int status) {
        // Log.d(TAG, "onCharacteristicWrite: " + uuidStr);
        }

private void onCharacteristicChanged(String uuidStr, byte[] value) {
        if (mDeviceView != null) {
        if (mMagCalibrateRequest) {
        if (uuidStr.equals(SensorTagGatt.UUID_MAG_DATA.toString())) {
        Point3D v = Sensor.MAGNETOMETER.convert(value);

        MagnetometerCalibrationCoefficients.INSTANCE.val = v;
        mMagCalibrateRequest = false;
        Toast.makeText(context, "Magnetometer calibrated", Toast.LENGTH_SHORT)
        .show();
        }
        }

        if (mHeightCalibrateRequest) {
        if (uuidStr.equals(SensorTagGatt.UUID_BAR_DATA.toString())) {
        Point3D v = Sensor.BAROMETER.convert(value);

        BarometerCalibrationCoefficients.INSTANCE.heightCalibration = v.x;
        mHeightCalibrateRequest = false;
        Toast.makeText(context, "Height measurement calibrated",
        Toast.LENGTH_SHORT).show();
        }
        }
            /*
        mDeviceView.setActivity(this);
        mDeviceView.setApplication((FitwhizApplication)this.getApplication());
        mDeviceView.V(uuidStr, value);*/
            valuesUpdated(uuidStr, value);
        }
        }

    private void valuesUpdated(String uuidStr, byte[] rawValue) {
        Point3D v;
        String msg;
        ReadingsAnalyzer readingsAnalyzer = new ReadingsAnalyzer((FitwhizApplication)context.getApplicationContext());
//        SensorCurrentFragment fragment = (SensorCurrentFragment)this.getFragmentManager().findFragmentById(R.id.fragment_sensor_current);


        if (uuidStr.equals(SensorTagGatt.UUID_ACC_DATA.toString())) {
            v = Sensor.ACCELEROMETER.convert(rawValue);
            msg = decimal.format(v.x) + "\n" + decimal.format(v.y) + "\n"
                    + decimal.format(v.z) + "\n";
            AccelerometerTableOperations accelerometerTableOperations = new AccelerometerTableOperations(context);
            accelerometerTableOperations.insertValue(v.x, v.y, v.z, DateTimeHelper.getDefaultFormattedDateTime());
            application.setXVal(v.x);
            application.setYVal(v.y);
            application.setZVal(v.z);
            System.out.println(application.getXVal() + " " + application.getYVal() + " " + application.getZVal());
            readingsAnalyzer.analyzeAcceleration(MathHelper.getResultantAcceleration(v.x, v.y, v.z));
            CountHelperThread cht = new CountHelperThread(v.x,v.y,v.z,application,context);
            ///     ( (TextView)fragment.getActivity().findViewById(R.id.Accelerometer_current_val)).setText(new DecimalFormat("00.00").format(v.x));
            cht.run();
        }

        if (uuidStr.equals(SensorTagGatt.UUID_MAG_DATA.toString())) {

            v = Sensor.MAGNETOMETER.convert(rawValue);
            msg = decimal.format(v.x) + "\n" + decimal.format(v.y) + "\n"
                    + decimal.format(v.z) + "\n";
            //mMagValue.setText(msg);*/
            application.setM_xVal(v.x);
            application.setM_yVal(v.y);
            application.setM_zVal(v.z);
            MagnetometerTableOperations magnetometerTableOperations = new MagnetometerTableOperations(context);
            magnetometerTableOperations.insertValue(v.x, v.y, v.z, DateTimeHelper.getDefaultFormattedDateTime());
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
            GyroscopeTableOperations gyroscopeTableOperations = new GyroscopeTableOperations(context);
            gyroscopeTableOperations.insertValue(v.x, v.y, v.z, DateTimeHelper.getDefaultFormattedDateTime());
        }

        if (uuidStr.equals(SensorTagGatt.UUID_IRT_DATA.toString())) {
            v = Sensor.IR_TEMPERATURE.convert(rawValue);
            msg = decimal.format(v.x);
            //mAmbValue.setText(msg);
            //msg = decimal.format(v.y) + "\n";
            TemperatureTableOperations temperatureTableOperations = new TemperatureTableOperations(context);
            temperatureTableOperations.insertValue(v.x,v.y, DateTimeHelper.getDefaultFormattedDateTime());
            application.setTVal(Double.parseDouble(msg));
            application.setAmbTemp(v.x);
            application.setBodyTemp(v.y);
            //readingsAnalyzer.analyzeTemperature(Double.parseDouble(msg));
            readingsAnalyzer.analyzeBodyTemperature(v.y);
            readingsAnalyzer.analyzeAmbientTemperature(v.x);
        }

        if (uuidStr.equals(SensorTagGatt.UUID_HUM_DATA.toString())) {
            v = Sensor.HUMIDITY.convert(rawValue);
            msg = decimal.format(v.x);
            HumidityTableOperations humidityTableOperations = new HumidityTableOperations(context);
            humidityTableOperations.insertValue(Double.parseDouble(msg), DateTimeHelper.getDefaultFormattedDateTime());
            application.setHVal(Double.parseDouble(msg));
            readingsAnalyzer.analyzeHumidity(Double.parseDouble(msg));
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
        }

        if (uuidStr.equals(SensorTagGatt.UUID_KEY_DATA.toString())) {
            int keys = rawValue[0];
            SimpleKeysStatus s;
            final int imgBtn;
            s = Sensor.SIMPLE_KEYS.convertKeys((byte) (keys&3));
            PendingIntent pIntent = PendingIntent.getActivity(context, 0, new Intent(context,DashboardActivity.class),0);
            NotificationHelper helper = new NotificationHelper(context);
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


    private void onCharacteristicsRead(String uuidStr, byte[] value, int status) {
        // Log.i(TAG, "onCharacteristicsRead: " + uuidStr);

        if (uuidStr.equals(SensorTagGatt.UUID_DEVINFO_FWREV.toString())) {
        mFwRev = new String(value, 0, 3);
        Toast.makeText(context, "Firmware revision: " + mFwRev, Toast.LENGTH_LONG).show();
        }

        if (mIsSensorTag2)
        return;

        if (uuidStr.equals(SensorTagGatt.UUID_BAR_CALI.toString())) {
        // Sanity check
        if (value.length != 16)
        return;

        // Barometer calibration values are read.
        List<Integer> cal = new ArrayList<Integer>();
        for (int offset = 0; offset < 8; offset += 2) {
        Integer lowerByte = (int) value[offset] & 0xFF;
        Integer upperByte = (int) value[offset + 1] & 0xFF;
        cal.add((upperByte << 8) + lowerByte);
        }

        for (int offset = 8; offset < 16; offset += 2) {
        Integer lowerByte = (int) value[offset] & 0xFF;
        Integer upperByte = (int) value[offset + 1];
        cal.add((upperByte << 8) + lowerByte);
        }

        BarometerCalibrationCoefficients.INSTANCE.barometerCalibrationCoefficients = cal;
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
        }



