package cmpe.alpha.fitwhiz.HelperLibrary;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by rajagopalan on 2/21/15.
 */
public class DateTimeHelper {
    public static String getDateTime(String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                format, Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String formatDateTime(String format, Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                format, Locale.getDefault());
        return dateFormat.format(date);
    }

    public static String getDefaultFormattedDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
