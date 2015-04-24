package cmpe.alpha.fitwhiz.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by rajagopalan on 4/5/15.
 */
public class CountTableOperations extends DatabaseConnector {
    protected static final String COUNT_TABLE = "count_table";

    ContentValues contentValues = new ContentValues();

    public CountTableOperations(Context context)
    {
        super(context);
    }

    public void insertValue(double count, String timeStamp)
    {
        try {
            SQLiteDatabase db = getWritableDatabase();
            contentValues.put("timestamp", timeStamp);
            contentValues.put("count", count);
            db.insert(COUNT_TABLE, "id", contentValues);
            db.close();
        }
        catch (Exception ex)
        {
            Log.e(this.getClass().getSimpleName(), ex.toString());
        }
    }

    public double getMaxCountForSpecifiedTimeRange(String datetimeStart, String datetimeEnd, String columnName)
    {
        try {
            String sql = "select MAX("+columnName+") from '" + COUNT_TABLE + "' where timestamp>'" + datetimeStart + "' and timestamp<='" + datetimeEnd + "'";

            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);
            cursor.moveToFirst();
            double count = cursor.getDouble(0);
            db.close();
            return count;
        }
        catch (Exception ex)
        {
            Log.e(this.getClass().getSimpleName(), ex.toString());
            return 0.0;
        }
    }

    public double getMaxCountInTable(String columnName)
    {
        try {
            String sql = "select MAX("+columnName+") from '" + COUNT_TABLE + "' ";

            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);
            cursor.moveToFirst();
            double count = cursor.getDouble(0);
            db.close();
            return count;
        }
        catch (Exception ex)
        {
            Log.e(this.getClass().getSimpleName(), ex.toString());
            return 0.0;
        }
    }

    public double getMinCountForSpecifiedTimeRange(String datetimeStart, String datetimeEnd, String columnName)
    {
        try {
            String sql = "select MIN("+columnName+") from '" + COUNT_TABLE + "' where timestamp>'" + datetimeStart + "' and timestamp<='" + datetimeEnd + "'";

            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);
            cursor.moveToFirst();
            double count = cursor.getDouble(0);
            db.close();
            return count;
        }
        catch (Exception ex)
        {
            Log.e(this.getClass().getSimpleName(), ex.toString());
            return 0.0;
        }
    }

}
