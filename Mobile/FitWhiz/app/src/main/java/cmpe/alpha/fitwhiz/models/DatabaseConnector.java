package cmpe.alpha.fitwhiz.models;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by RKGampa on 2/15/2015.
 */
public class DatabaseConnector extends SQLiteOpenHelper
{
    protected static final String DB_NAME = "fit_whiz";
    protected static final String USER_TABLE = "user_table";
    protected static SQLiteDatabase db;


    public static SQLiteDatabase getDb()
    {
        return db;
    }

    public DatabaseConnector(Context context)
    {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE "+USER_TABLE+"(id INTEGER PRIMARY KEY AUTOINCREMENT,sensor_id TEXT, user_type TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP IF EXISTS "+DB_NAME);
        onCreate(db);
    }
    @Override
    public void onOpen(SQLiteDatabase db)
    {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }
}
