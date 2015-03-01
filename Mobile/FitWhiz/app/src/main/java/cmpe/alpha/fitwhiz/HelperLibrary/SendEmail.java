package cmpe.alpha.fitwhiz.HelperLibrary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.util.Collections;

/**
 * Created by RKGampa on 2/27/2015.
 */
public class SendEmail
{
    private Context context;
    public SendEmail(Context context)
    {
        this.context = context;
    }
    public boolean sendMail(String[] recipient, String subject, String message)
    {
        /*String[] TO = {"raj_vrg@hotmail.com"};
        String[] CC = {"sanatom.sjsu@gmail.com","harish.tpl@gmail.com"};*/
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/html");

        emailIntent.putExtra(Intent.EXTRA_EMAIL, recipient);
        //emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        try
        {
            context.startActivity(Intent.createChooser(emailIntent, "Send mail...").setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            Log.i(this.getClass().getSimpleName(), "email sent successfully");
            return true;
        }
        catch (android.content.ActivityNotFoundException ex)
        {
            Log.e(this.getClass().getSimpleName(), "No email client installed. Could not send email");
            return false;
        }
    }
}
