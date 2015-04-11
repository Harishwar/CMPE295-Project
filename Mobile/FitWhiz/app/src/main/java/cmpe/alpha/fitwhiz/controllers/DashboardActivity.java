package cmpe.alpha.fitwhiz.controllers;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ti.ble.common.BluetoothLeService;
import com.example.ti.ble.common.GattInfo;
import com.example.ti.util.Point3D;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import cmpe.alpha.fitwhiz.HelperLibrary.EnableServices;
import cmpe.alpha.fitwhiz.R;
import cmpe.alpha.fitwhiz.lib.FitwhizApplication;
import cmpe.alpha.fitwhiz.sensortag.BarometerCalibrationCoefficients;
import cmpe.alpha.fitwhiz.sensortag.FwUpdateActivity;
import cmpe.alpha.fitwhiz.sensortag.MagnetometerCalibrationCoefficients;
import cmpe.alpha.fitwhiz.sensortag.PreferencesActivity;
import cmpe.alpha.fitwhiz.sensortag.PreferencesFragment;
import cmpe.alpha.fitwhiz.sensortag.Sensor;
import cmpe.alpha.fitwhiz.sensortag.SensorTagGatt;

public class DashboardActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
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

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    //
    // Application implementation
    //
    private void updateSensorList() {
        mEnabledSensors.clear();

        for (int i = 0; i < Sensor.SENSOR_LIST.length; i++) {
            Sensor sensor = Sensor.SENSOR_LIST[i];
            if (isEnabledByPrefs(sensor)) {
                mEnabledSensors.add(sensor);
            }
        }
    }


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter fi = new IntentFilter();
        fi.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        fi.addAction(BluetoothLeService.ACTION_DATA_NOTIFY);
        fi.addAction(BluetoothLeService.ACTION_DATA_WRITE);
        fi.addAction(BluetoothLeService.ACTION_DATA_READ);
        return fi;
    }

    public boolean isSensorTag2() {
        return mIsSensorTag2;
    }

    public String firmwareRevision() {
        return mFwRev;
    }

    boolean isEnabledByPrefs(final Sensor sensor) {
        String preferenceKeyString = "pref_"
                + sensor.name().toLowerCase(Locale.ENGLISH) + "_on";

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);

        Boolean defaultValue = true;
        return prefs.getBoolean(preferenceKeyString, defaultValue);
    }

    BluetoothGattService getOadService() {
        return mOadService;
    }

    BluetoothGattService getConnControlService() {
        return mConnControlService;
    }

    private void startOadActivity() {
        // For the moment OAD does not work on Galaxy S3 (disconnects on parameter update)
        if (Build.MODEL.contains("I9300")) {
            Toast.makeText(this, "OAD not available on this Android device",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (mOadService != null && mConnControlService != null) {
            // Disable sensors and notifications when the OAD dialog is open
            enableDataCollection(false);
            // Launch OAD
            final Intent i = new Intent(this, FwUpdateActivity.class);
            startActivityForResult(i, FWUPDATE_ACT_REQ);
        } else {
            Toast.makeText(this, "OAD not available on this BLE device",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void startPreferenceActivity() {
        // Disable sensors and notifications when the settings dialog is open
        enableDataCollection(false);
        // Launch preferences
        final Intent i = new Intent(this, PreferencesActivity.class);
        i.putExtra(PreferencesActivity.EXTRA_SHOW_FRAGMENT,
                PreferencesFragment.class.getName());
        i.putExtra(PreferencesActivity.EXTRA_NO_HEADERS, true);
        i.putExtra(EXTRA_DEVICE, mBluetoothDevice);
        startActivityForResult(i, PREF_ACT_REQ);
    }

    private void checkOad() {
        // Check if OAD is supported (needs OAD and Connection Control service)
        mOadService = null;
        mConnControlService = null;

        for (int i = 0; i < mServiceList.size()
                && (mOadService == null || mConnControlService == null); i++) {
            BluetoothGattService srv = mServiceList.get(i);
            if (srv.getUuid().equals(GattInfo.OAD_SERVICE_UUID)) {
                mOadService = srv;
            }
            if (srv.getUuid().equals(GattInfo.CC_SERVICE_UUID)) {
                mConnControlService = srv;
            }
        }
    }

    private void discoverServices() {
        if (mBtGatt.discoverServices()) {
            mServiceList.clear();
            setStatus("Service discovery started");
        } else {
            setError("Service discovery start failed");
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

    private void setError(String txt) {
        Toast.makeText(this, txt, Toast.LENGTH_LONG).show();
    }

    private void setStatus(String txt) {
        Toast.makeText(this, txt, Toast.LENGTH_SHORT).show();
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

    private void enableDataCollection(boolean enable) {
        enableSensors(enable);
        enableNotifications(enable);
    }

    /*
     * Calibrating the barometer includes
     *
     * 1. Write calibration code to configuration characteristic. 2. Read
     * calibration values from sensor, either with notifications or a normal read.
     * 3. Use calibration values in formulas when interpreting sensor values.
     */
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

    // Activity result handling
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PREF_ACT_REQ:
                Toast.makeText(this, "Applying preferences", Toast.LENGTH_SHORT).show();
                if (!mIsReceiving) {
                    mIsReceiving = true;
                    registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
                }

                updateSensorList();
                enableDataCollection(true);
                break;
            case FWUPDATE_ACT_REQ:
                // FW update cancelled so resume
                enableDataCollection(true);
                break;
            default:
                setError("Unknown request code");
                break;
        }
    }
    @Override
    protected void onResume() {
        // Log.d(TAG, "onResume");
        super.onResume();
        if (!mIsReceiving) {
            registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
            mIsReceiving = true;
        }
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            int status = intent.getIntExtra(BluetoothLeService.EXTRA_STATUS,
                    BluetoothGatt.GATT_SUCCESS);

            if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    setStatus("Service discovery complete");
                    displayServices();
                    //checkOad();
                    enableDataCollection(true);
                    getFirmwareRevison();
                } else {
                    Toast.makeText(getApplication(), "Service discovery failed",
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
    };

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
                    Toast.makeText(this, "Magnetometer calibrated", Toast.LENGTH_SHORT)
                            .show();
                }
            }

            if (mHeightCalibrateRequest) {
                if (uuidStr.equals(SensorTagGatt.UUID_BAR_DATA.toString())) {
                    Point3D v = Sensor.BAROMETER.convert(value);

                    BarometerCalibrationCoefficients.INSTANCE.heightCalibration = v.x;
                    mHeightCalibrateRequest = false;
                    Toast.makeText(this, "Height measurement calibrated",
                            Toast.LENGTH_SHORT).show();
                }
            }
            mDeviceView.setActivity(this);
            mDeviceView.setApplication((FitwhizApplication)this.getApplication());
            mDeviceView.onCharacteristicChanged(uuidStr, value);
        }
    }

    @Override
    protected void onPause() {
        // Log.d(TAG, "onPause");
        super.onPause();
        if (mIsReceiving) {
            unregisterReceiver(mGattUpdateReceiver);
            mIsReceiving = false;
        }
    }

    private void onCharacteristicsRead(String uuidStr, byte[] value, int status) {
        // Log.i(TAG, "onCharacteristicsRead: " + uuidStr);

        if (uuidStr.equals(SensorTagGatt.UUID_DEVINFO_FWREV.toString())) {
            mFwRev = new String(value, 0, 3);
            Toast.makeText(this, "Firmware revision: " + mFwRev, Toast.LENGTH_LONG).show();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        EnableServices enableServices = new EnableServices(this);
        enableServices.checkBluetooth();

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        Intent intent = getIntent();
     //   startPreferenceActivity();
        // BLE
        mBtLeService = BluetoothLeService.getInstance();
        mBluetoothDevice = intent.getParcelableExtra(EXTRA_DEVICE);
        mServiceList = new ArrayList<BluetoothGattService>();
        // Create GATT object
        mBtGatt = BluetoothLeService.getBtGatt();

        // Start service discovery
        if (!mServicesRdy && mBtGatt != null) {
            if (mBtLeService.getNumServices() == 0)
                discoverServices();
            else {
                displayServices();
                enableDataCollection(true);
            }
        }
        // Determine type of SensorTagGatt
        String deviceName = mBluetoothDevice.getName();
        mIsSensorTag2 = deviceName.equals("SensorTag2");
        if (mIsSensorTag2)
            PreferenceManager.setDefaultValues(this, R.xml.preferences2, false);
        else
            PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        // Log.i(TAG, "Preferences for: " + deviceName);

        // GUI
        mDeviceView = new SensorCurrentFragment();
        //HelpView hw = new HelpView();
       // hw.setParameters("help_device.html", R.layout.fragment_help, R.id.webpage);

        // GATT database
        Resources res = getResources();
        XmlResourceParser xpp = res.getXml(R.xml.gatt_uuid);
        new GattInfo(xpp);

        // Initialize sensor list
        updateSensorList();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.dashboard, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public Fragment newInstance(int sectionNumber)
    {
        final String ARG_SECTION_NUMBER = "section_number";
        Fragment fragment=null;
        Bundle args = new Bundle();
        Log.d("section number:",""+sectionNumber);
        switch (sectionNumber)
        {
            case 1:
                fragment = new ProfileFragment();
                args.putInt(ARG_SECTION_NUMBER, sectionNumber);
                fragment.setArguments(args);
                break;
            case 2:
                fragment = new SensorCurrentFragment();
                args.putInt(ARG_SECTION_NUMBER, sectionNumber);
                fragment.setArguments(args);
                break;
            case 3:
                fragment = new SensorHistoryFragment();
                args.putInt(ARG_SECTION_NUMBER, sectionNumber);
                fragment.setArguments(args);
                break;
            case 4:
                fragment = new DoctorRecommendationFragment();
                args.putInt(ARG_SECTION_NUMBER, sectionNumber);
                fragment.setArguments(args);
                break;
            default:
                fragment = new PlaceholderFragment();
                args.putInt(ARG_SECTION_NUMBER, sectionNumber);
                fragment.setArguments(args);
                break;
        }
        return fragment;
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment
    {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState)
        {
            View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((DashboardActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}