package cmpe.alpha.fitwhiz.controllers.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cmpe.alpha.fitwhiz.HelperLibrary.DateTimeHelper;
import cmpe.alpha.fitwhiz.lib.FitwhizApplication;
import cmpe.alpha.fitwhiz.models.CountTableOperations;

/**
 * Created by rajagopalan on 4/5/15.
 */
public class ScheduledCountUpdateService extends BroadcastReceiver {
    private FitwhizApplication fitwhizApplication;

    @Override
    public void onReceive(Context context, Intent intent) {
        fitwhizApplication = (FitwhizApplication) context.getApplicationContext();
        double count = fitwhizApplication.getCount();
        String timestamp = DateTimeHelper.getDefaultFormattedDateTime();
        CountTableOperations countTableOperations = new CountTableOperations(context);
        countTableOperations.insertValue(count, timestamp);
    }
}