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

    public SQLiteDatabase write()
    {
        return this.getWritableDatabase();
    }

    public SQLiteDatabase read()
    {
        return this.getReadableDatabase();
    }

    public void insertUser(String sensor_id, String userType)
    {
        db = write();
        contentValues.put("sensor_id",sensor_id);
        contentValues.put("user_type",userType);
        db.insert(USER_TABLE,"id",contentValues);
    }

    public Cursor getUserDetails(String sensor_id)
    {
        db = read();
        //Log.d("------input-value",sensor_id+"");
        Cursor cursor = db.rawQuery("SELECT * FROM " + USER_TABLE+" where sensor_id = '" +sensor_id+ "'", null);
        //Log.d("user-data",cursor.moveToFirst()+"");
        return cursor;
    }

    public void getAllUserDetails(String sensor_id)
    {
        db = read();
        Cursor cursor = db.rawQuery("SELECT * FROM " + USER_TABLE+" where sensor_id = '" +sensor_id + "'", null);
        if(cursor.moveToFirst())
        {
            do
            {
                Log.d("user-type",cursor.getString(cursor.getColumnIndex("user_type")));
                Log.d("sensor-id",cursor.getString(cursor.getColumnIndex("sensor_id")));
            }while (cursor.moveToNext());
        }
    }

    public void deleteUser(String sensor_id)
    {
        db = write();
        db.delete(USER_TABLE,"dt = "+sensor_id,null);
        db.close();
    }

    public void updateUserType(String sensor_id, String userType)
    {
        db=write();
        contentValues.put("userType",userType);
        String whereArgs[] = new String[1];
        whereArgs[0]=""+sensor_id;
        db.update(USER_TABLE,contentValues,"sensor_id = ?",whereArgs);
    }

}
