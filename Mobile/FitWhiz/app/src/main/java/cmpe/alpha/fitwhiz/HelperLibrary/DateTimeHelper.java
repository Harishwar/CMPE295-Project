package cmpe.alpha.fitwhiz.HelperLibrary;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by rajagopalan on 2/21/15.
 */
public class DateTimeHelper {
    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "MM-dd-yy HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
