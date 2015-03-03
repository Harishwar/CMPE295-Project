package cmpe.alpha.fitwhiz.HelperLibrary;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import cmpe.alpha.fitwhiz.HelperLibrary.CustomAlert;
import cmpe.alpha.fitwhiz.lib.AlertType;

/**
 * Created by RKGampa on 2/17/2015.
 */
public class EnableServices
{
    private BluetoothAdapter bluetoothAdapter;
    private Context context;
    private ConnectivityManager connectivityManager;
    private boolean isWifiEnabled;
    public EnableServices(Context context)
    {
        this.context = context;
    }

    public boolean checkBluetooth()
    {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter==null)
        {
            Toast.makeText(context,"No bluetooth for the device",Toast.LENGTH_SHORT).show();
            return false;
        }
        else
        {
            if (!bluetoothAdapter.isEnabled())
            {
                CustomAlert customAlert = new CustomAlert();
                customAlert.createAlert(context,"Enable Bluetooth",
                        "Application needs access to Bluetooth",
                        null,null,AlertType.BLUETOOTH);
            }
            return true;
        }
    }
    public void checkInternet()
    {
        connectivityManager =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager!=null)
        {
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null)
                for (int i = 0; i < networkInfo.length; i++)
                    if (networkInfo[i] != null && networkInfo[i].isConnected())
                    {
                        Log.i(this.getClass().getSimpleName(),"Internet Connection Available");
                    }
        }
        isWifiEnabled = checkWifi();
    }
    public boolean checkWifi()
    {
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        if(!wifiManager.isWifiEnabled())
        {
            CustomAlert customAlert = new CustomAlert();
            customAlert.createAlert(context,"Enable Wifi","Please enable Wifi",null,null, AlertType.WIFI);
            return true;
        }
        return false;
    }
    public void checkMobileData()
    {
        CustomAlert customAlert = new CustomAlert();
        customAlert.createAlert(context, "Enable Mobile Data", "Please enable Mobile Data ", null, null,AlertType.MOBILE_DATA);
    }
}
