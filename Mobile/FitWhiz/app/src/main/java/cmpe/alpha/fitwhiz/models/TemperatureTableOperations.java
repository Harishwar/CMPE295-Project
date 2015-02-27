package cmpe.alpha.fitwhiz.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;

import cmpe.alpha.fitwhiz.HelperLibrary.DateTimeHelper;

/**
 * Created by rajagopalan on 2/21/15.
 */
public class TemperatureTableOperations extends DatabaseConnector {
    protected static final String TEMPERATURE_TABLE = "temperature_table";

        ContentValues contentValues = new ContentValues();

        public TemperatureTableOperations(Context context)
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

        public void insertValue(double val, String timeStamp)
        {
            try {
                db = writer();
                contentValues.put("timestamp", timeStamp);
                contentValues.put("t_val", val);
                db.insert(TEMPERATURE_TABLE, "id", contentValues);
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
                String sql = "select COUNT("+columnName+"), TOTAL("+columnName+") from '"+TEMPERATURE_TABLE+"' where timestamp>'"+datetimeStart+"' and timestamp<'"+datetimeEnd+"'";
                db=reader();
                Cursor cursor = db.rawQuery(sql,null);
                cursor.moveToFirst();
                double count = cursor.getDouble(0);
                double sum = cursor.getDouble(1);
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
                String sql = "delete from "+TEMPERATURE_TABLE+" where timestamp>"+datetimeStart+" and timestamp<"+datetimeEnd;
                return true;
            }
            catch (Exception ex)
            {
                Log.e(this.getClass().getSimpleName(), ex.toString());
                return false;
            }
        }
    }
