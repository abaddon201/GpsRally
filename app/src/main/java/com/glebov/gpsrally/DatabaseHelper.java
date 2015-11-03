package com.glebov.gpsrally;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by glebov on 02.11.15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "Routes.DatabaseHelper";

    public static final String KEY_ROWID = "_id";
    public static final String KEY_ROUTE_NUM = "routenum";
    public static final String KEY_NUM = "num";
    public static final String KEY_DISTANCE = "distance";
    public static final String KEY_FULL_DISTANCE = "full_distance";
    public static final String KEY_TIME = "time";
    public static final String KEY_AVGSPEED = "avgspeed";

    private static final String DATABASE_NAME = "Routes";
    private static final String SQLITE_TABLE = "Route";
    private static final int DATABASE_VERSION = 1;

    private SQLiteDatabase mDb;

    private static final String DATABASE_CREATE =
            "CREATE TABLE if not exists " + SQLITE_TABLE + " (" +
                    KEY_ROWID + " integer PRIMARY KEY autoincrement," +
                    KEY_ROUTE_NUM + "," +
                    KEY_NUM + "," +
                    KEY_FULL_DISTANCE + "," +
                    KEY_DISTANCE + "," +
                    KEY_AVGSPEED + "," +
                    KEY_TIME + "," +
                    " UNIQUE (" + KEY_ROUTE_NUM + "," + KEY_NUM + "));";

    private final Context mCtx;

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mCtx = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.w(TAG, DATABASE_CREATE);
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE);
        onCreate(db);
    }

    public void open() {
        mDb = getWritableDatabase();
    }

    public Cursor fetchRoute() {
        Cursor mCursor = mDb.query(SQLITE_TABLE, new String[]{KEY_ROWID,
                        KEY_NUM, KEY_FULL_DISTANCE, KEY_DISTANCE, KEY_AVGSPEED, KEY_TIME},
                null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public long createRouteEntry(int num, int full_distance, int distance, int avg_speed, int time) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ROUTE_NUM, 0);
        initialValues.put(KEY_NUM, num);
        initialValues.put(KEY_FULL_DISTANCE, full_distance);
        initialValues.put(KEY_DISTANCE, distance);
        initialValues.put(KEY_AVGSPEED, avg_speed);
        initialValues.put(KEY_TIME, time);

        return mDb.insert(SQLITE_TABLE, null, initialValues);
    }

    public boolean deleteAllRoutes() {
        int doneDelete = mDb.delete(SQLITE_TABLE, null, null);
        Log.w(TAG, Integer.toString(doneDelete));
        return doneDelete > 0;
    }

    public void insertSome() {
        createRouteEntry(1, 1000, 1000, 30, 40);
        createRouteEntry(2, 2000, 1000, 60, 20);
        createRouteEntry(3, 3000, 100, 90, 10);
        createRouteEntry(4, 3100, 900, 10, 80);
    }
}
