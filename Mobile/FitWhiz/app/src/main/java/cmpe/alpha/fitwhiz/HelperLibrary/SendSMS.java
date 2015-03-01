package cmpe.alpha.fitwhiz.HelperLibrary;

import android.content.Context;
import android.telephony.SmsManager;
import android.util.Log;

/**
 * Created by RKGampa on 2/27/2015.
 */
public class SendSMS
{
    private Context context;
    public SendSMS(Context context)
    {
        this.context = context;
    }
    public boolean sendSMS(String phoneNumber, String message)
    {
        try
        {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Log.i(this.getClass().getSimpleName(), "SMS sent successfully");
            return true;
        }
        catch (Exception e)
        {
            Log.e(this.getClass().getSimpleName(),"Failed to send SMS");
            return false;
        }
    }
}
