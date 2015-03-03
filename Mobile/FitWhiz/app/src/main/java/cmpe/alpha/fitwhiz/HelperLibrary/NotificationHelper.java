package cmpe.alpha.fitwhiz.HelperLibrary;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

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

    public boolean SendNotification(String title, String message, PendingIntent pIntent, NotificationPriority priority, String description)
    {
        try{
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(_context.getApplicationContext())
                    .setContentTitle(title)
                    .setContentText(message).setSmallIcon(R.drawable.notification_icon)
                    .setContentIntent(pIntent);
            if(!description.isEmpty())
            {
                NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
                style.bigText(description);
                notificationBuilder.setStyle(style);
            }
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(_context);
            Notification notification = notificationBuilder.build();

            switch (priority)
            {
                case EMERGENCY:notification.flags |= Notification.PRIORITY_MAX;break;
                case HIGH:notification.flags |= Notification.PRIORITY_HIGH;break;
                case LOW:notification.flags |= Notification.PRIORITY_LOW;break;
                case MEDIUM:
                    case UNKNOWN:
                    default:notification.flags |= Notification.PRIORITY_DEFAULT;break;
            }
            notificationManager.notify(1, notification);
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

}
