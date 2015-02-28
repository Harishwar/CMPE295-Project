package cmpe.alpha.fitwhiz.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by RKGampa on 2/15/2015.
 */
public class UserTableOperations extends DatabaseConnector
{
    ContentValues contentValues = new ContentValues();

    public UserTableOperations(Context context)
    {
        super(context);
    }

    public void insertUser(String sensor_id, String userType)
    {
        SQLiteDatabase db = getWritableDatabase();
        contentValues.put("sensor_id",sensor_id);
        contentValues.put("user_type",userType);
        db.insert(USER_TABLE,"id",contentValues);
        db.close();
    }

    public Cursor getUserDetails(String sensor_id)
    {
        SQLiteDatabase db = getReadableDatabase();
        //Log.d("------input-value",sensor_id+"");
        Cursor cursor = db.rawQuery("SELECT * FROM " + USER_TABLE+" where sensor_id = '" +sensor_id+ "'", null);
        //Log.d("user-data",cursor.moveToFirst()+"");
        db.close();
        return cursor;
    }

    public void getAllUserDetails(String sensor_id)
    {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + USER_TABLE+" where sensor_id = '" +sensor_id + "'", null);
        if(cursor.moveToFirst())
        {
            do
            {
                Log.d("user-type",cursor.getString(cursor.getColumnIndex("user_type")));
                Log.d("sensor-id",cursor.getString(cursor.getColumnIndex("sensor_id")));
            }while (cursor.moveToNext());
        }
        db.close();
    }

    public void deleteUser(String sensor_id)
    {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(USER_TABLE,"dt = "+sensor_id,null);
        db.close();
    }

    public void updateUserType(String sensor_id, String userType)
    {
        SQLiteDatabase db=getWritableDatabase();
        contentValues.put("userType",userType);
        String whereArgs[] = new String[1];
        whereArgs[0]=""+sensor_id;
        db.update(USER_TABLE,contentValues,"sensor_id = ?",whereArgs);
        db.close();
    }

}
