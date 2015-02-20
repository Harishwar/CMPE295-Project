package cmpe.alpha.fitwhiz.controllers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

/**
 * Created by RKGampa on 2/17/2015.
 */
public class CustomAlert
{
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;
    private boolean result=false;
    public boolean getResult()
    {
        return result;
    }

    public void setResult(boolean result)
    {
        this.result = result;
    }

    public boolean createAlert(final Context context, String title, String message,
                            final String positiveMessage, final String negativeMessage)
    {
        alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(title)
                .setCancelable(true)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, enable bluetooth
                        if (positiveMessage != null)
                        {
                            Toast.makeText(context, positiveMessage, Toast.LENGTH_SHORT).show();
                        }
                        setResult(true);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        if (negativeMessage != null) {
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
