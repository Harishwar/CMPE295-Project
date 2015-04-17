package cmpe.alpha.fitwhiz.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by RKGampa on 2/15/2015.
 */
public class DatabaseConnector extends SQLiteOpenHelper
{
    protected static final String DB_NAME = "fit_whiz";
    protected static final String USER_TABLE = "user_table";
    protected static final String ACCELEROMETER_TABLE = "accelerometer_table";
    protected static final String HUMIDITY_TABLE = "humidity_table";
    protected static final String TEMPERATURE_TABLE = "temperature_table";
    protected static final String COUNT_TABLE = "count_table";
    protected static final String GYROSCOPE_TABLE = "gyroscope_table";
    protected static final String MAGNETOMETER_TABLE = "magnetometer_table";
    protected static final String PRESSURE_TABLE = "pressure_table";
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
        db.execSQL("CREATE TABLE "+ACCELEROMETER_TABLE+"(id INTEGER PRIMARY KEY AUTOINCREMENT,timestamp date, x_val REAL,y_val REAL,z_val REAL)");
        db.execSQL("CREATE TABLE "+HUMIDITY_TABLE+"(id INTEGER PRIMARY KEY AUTOINCREMENT,timestamp date, h_val REAL)");
        db.execSQL("CREATE TABLE "+TEMPERATURE_TABLE+"(id INTEGER PRIMARY KEY AUTOINCREMENT,timestamp date, amb_val REAL, body_val REAL)");
        db.execSQL("CREATE TABLE "+COUNT_TABLE+"(id INTEGER PRIMARY KEY AUTOINCREMENT,timestamp date, count REAL)");
        db.execSQL("CREATE TABLE "+MAGNETOMETER_TABLE+"(id INTEGER PRIMARY KEY AUTOINCREMENT,timestamp date, x_val REAL,y_val REAL,z_val REAL)");
        db.execSQL("CREATE TABLE "+GYROSCOPE_TABLE+"(id INTEGER PRIMARY KEY AUTOINCREMENT,timestamp date, x_val REAL,y_val REAL,z_val REAL)");
        db.execSQL("CREATE TABLE "+PRESSURE_TABLE+"(id INTEGER PRIMARY KEY AUTOINCREMENT,timestamp date, p_val REAL)");
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
