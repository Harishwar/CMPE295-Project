package cmpe.alpha.fitwhiz.HelperLibrary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.widget.Toast;

import cmpe.alpha.fitwhiz.lib.AlertType;

/**
 * Created by RKGampa on 2/17/2015.
 */
public class CustomAlert
{
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;
    private boolean result=false;
    private AlertType alertType;
    public boolean getResult()
    {
        return result;
    }

    public void setResult(boolean result)
    {
        this.result = result;
    }

    public boolean createAlert(final Context context, String title, String message,
                            final String positiveMessage, final String negativeMessage, final AlertType alertType)
    {
        this.alertType = alertType;
        alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(title)
                .setCancelable(true)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, enable bluetooth
                        switch (alertType)
                        {
                            case WIFI:
                                context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                break;
                            case MOBILE_DATA:
                                context.startActivity(new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS));
                                break;
                            case MSG:
                                Toast.makeText(context, positiveMessage, Toast.LENGTH_SHORT).show();
                                break;
                            case BLUETOOTH:
                                context.startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                                break;
                            default:
                                Toast.makeText(context, positiveMessage, Toast.LENGTH_SHORT).show();
                        }
                        setResult(true);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        if (negativeMessage != null)
                        {
                            Toast.makeText(context, negativeMessage, Toast.LENGTH_SHORT).show();
                        }
                        dialog.cancel();
                    }
                });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        return result;
    }
}
