package cmpe.alpha.fitwhiz.HelperLibrary;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;

import cmpe.alpha.fitwhiz.R;
import cmpe.alpha.fitwhiz.lib.NotificationPriority;

/**
 * Created by rajagopalan on 2/22/15.
 */
public class NotificationHelper {
    private Context _context = null;
    //TODO Receive the information and give notifications
    public NotificationHelper(Context context)
    {
        this._context = context;
    }

    public boolean SendNotification(String title, String message, PendingIntent pIntent, NotificationPriority priority)
    {
        try{
            Notification notification = new Notification.Builder(_context.getApplicationContext())
                    .setContentTitle(title)
                    .setContentText(message).setSmallIcon(R.drawable.ic_launcher)
                    .setContentIntent(pIntent).build();
            NotificationManager notificationManager = (NotificationManager) _context.getSystemService(_context.NOTIFICATION_SERVICE);
            switch (priority)
            {
                case EMERGENCY:notification.flags |= Notification.PRIORITY_MAX;break;
                case HIGH:notification.flags |= Notification.PRIORITY_HIGH;break;
                case LOW:notification.flags |= Notification.PRIORITY_LOW;break;
                case MEDIUM:
                    default:notification.flags |= Notification.PRIORITY_DEFAULT;break;
            }
            notificationManager.notify(0, notification);
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

}
