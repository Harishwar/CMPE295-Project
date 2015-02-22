package cmpe.alpha.fitwhiz.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import cmpe.alpha.fitwhiz.HelperLibrary.DateTimeHelper;


/**
 * Created by rajagopalan on 2/21/15.
 */
public class AccelerometerTableOperations extends DatabaseConnector {
    protected static final String ACCELEROMETER_TABLE = "accelerometer_table";

    ContentValues contentValues = new ContentValues();

    public AccelerometerTableOperations(Context context)
    {
        super(context);
    }

    public SQLiteDatabase writer()
    {
        return this.getWritableDatabase();
    }

    public SQLiteDatabase reader()
    {
        return this.getReadableDatabase();
    }

    public void insertValue(double xVal, double yVal, double zVal, String timeStamp)
    {
        try {
            db = writer();
            contentValues.put("timestamp", timeStamp);
            contentValues.put("x_val", xVal);
            contentValues.put("y_val", yVal);
            contentValues.put("z_val", zVal);
            db.insert(ACCELEROMETER_TABLE, "id", contentValues);
        }
        catch (Exception ex)
        {
            Log.e(this.getClass().getSimpleName(), ex.toString());
        }
    }

    public double getAggregateForSpecifiedTimeRange(String datetimeStart, String datetimeEnd, String columnName)
    {
        try {
            String sql = "select count(*), TOTAL(" + columnName + ") from '" + ACCELEROMETER_TABLE + "' where timestamp>'" + datetimeStart + "' and timestamp<'" + datetimeEnd + "'";

            db = reader();
            Cursor cursor = db.rawQuery(sql, null);
            cursor.moveToFirst();
            double count = cursor.getDouble(0);
            double sum = cursor.getDouble(1);
            return (sum / count);
        }
        catch (Exception ex)
        {
            Log.e(this.getClass().getSimpleName(), ex.toString());
            return 0.0;
        }
    }

    public boolean deleteRecordsForSpecifiedTimeRange(String datetimeStart, String datetimeEnd)
    {
        try{
            String sql = "delete from "+ACCELEROMETER_TABLE+" where timestamp>"+datetimeStart+" and timestamp<"+datetimeEnd;
            return true;
        }
        catch (Exception ex)
        {
            Log.e(this.getClass().getSimpleName(),ex.toString());
            return false;
        }
    }
}
