package cmpe.alpha.fitwhiz.controllers;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by RKGampa on 2/17/2015.
 */
public class EnableServices
{
    private BluetoothAdapter bluetoothAdapter;
    private Context context;

    public EnableServices(Context context)
    {
        this.context = context;
    }

    public void checkBluetooth()
    {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter==null)
        {
            Toast.makeText(context,"No bluetooth for the device",Toast.LENGTH_SHORT).show();
        }
        else
        {
            if (!bluetoothAdapter.isEnabled())
            {
                CustomAlert customAlert = new CustomAlert();
                boolean result = customAlert.createAlert(context,"Enable Bluetooth","Application needs access to Bluetooth",
                        null,null);
                if (!result)
                {
                    bluetoothAdapter.enable();
                }
            }
        }
    }
}
