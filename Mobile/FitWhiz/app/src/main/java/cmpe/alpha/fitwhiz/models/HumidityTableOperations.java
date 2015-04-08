package cmpe.alpha.fitwhiz.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by rajagopalan on 2/21/15.
 */
public class HumidityTableOperations extends DatabaseConnector {
    protected static final String HUMIDITY_TABLE = "humidity_table";

    ContentValues contentValues = new ContentValues();

    public HumidityTableOperations(Context context)
    {
        super(context);
    }

    public void insertValue(double val, String timeStamp)
    {
        try {
            SQLiteDatabase db = getWritableDatabase();
            contentValues.put("timestamp", timeStamp);
            contentValues.put("h_val", val);
            db.insert(HUMIDITY_TABLE, "id", contentValues);
            db.close();
        }
        catch (Exception ex)
        {
            Log.e(this.getClass().getSimpleName(), ex.toString());
        }
    }

    public double getAggregateForSpecifiedTimeRange(String datetimeStart, String datetimeEnd, String columnName)
    {
        try
        {
            String sql = "select COUNT("+columnName+"), TOTAL("+columnName+") from "+HUMIDITY_TABLE+" where timestamp>'"+datetimeStart+"' and timestamp<='"+datetimeEnd+"'";

            SQLiteDatabase db=getReadableDatabase();
            Cursor cursor = db.rawQuery(sql,null);
            cursor.moveToFirst();
            double count = cursor.getDouble(0);
            double sum = cursor.getDouble(1);
            db.close();
            return (sum/count);
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
            String sql = "delete from "+HUMIDITY_TABLE+" where timestamp>"+datetimeStart+" and timestamp<"+datetimeEnd;
            return true;
        }
        catch (Exception ex)
        {
            Log.e(this.getClass().getSimpleName(), ex.toString());
            return false;
        }
    }
}
