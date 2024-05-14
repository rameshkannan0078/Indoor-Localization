package com.example.acceleratorpro;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.ar.core.Pose;
import com.google.ar.sceneform.math.Vector3;

public class TriangleDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = TriangleDatabaseHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "triangle.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "triangles";
    private static final String COLUMN_ID = "_id";
    public static final String COLUMN_X = "x";
    public static final String COLUMN_Y = "y";
    public static final String COLUMN_Z = "z";
    public static final String ANCHOR_ID = "anchor_id";
    public static final String TRIANGLE_POINTS="triangle_points";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_X + " REAL NOT NULL, " +
                    COLUMN_Y + " REAL NOT NULL, " +
                    COLUMN_Z + " REAL NOT NULL, " +
                    ANCHOR_ID + " INTEGER NOT NULL, " +
                    TRIANGLE_POINTS + " TEXT NOT NULL);";



    public TriangleDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long insertTriangle(float[] point1, Vector3 point2, Vector3 point3, com.google.ar.core.Anchor anchor, float[] trianglePoints) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_X, point1[0]);
        values.put(COLUMN_Y, point1[1]);
        values.put(COLUMN_Z, point1[2]);
        long rowId = db.insert(TABLE_NAME, null, values);

        values = new ContentValues();
        values.put(COLUMN_X, point2.x);
        values.put(COLUMN_Y, point2.y);
        values.put(COLUMN_Z, point2.z);
        db.insert(TABLE_NAME, null, values);

        values = new ContentValues();
        values.put(COLUMN_X, point3.x);
        values.put(COLUMN_Y, point3.y);
        values.put(COLUMN_Z, point3.z);
        db.insert(TABLE_NAME, null, values);

        values = new ContentValues();
        values.put(COLUMN_X, anchor.getPose().tx());
        values.put(COLUMN_Y, anchor.getPose().ty());
        values.put(COLUMN_Z, anchor.getPose().tz());
        values.put(ANCHOR_ID,1);
        values.put(TRIANGLE_POINTS, arrayToString(trianglePoints));
        db.insert(TABLE_NAME, null, values);

        Log.d(TAG, "Inserted triangle with row id: " + rowId);
        return rowId;
    }

    private String arrayToString(float[] array) {
        StringBuilder builder = new StringBuilder();
        for (float value : array) {
            builder.append(value).append(",");
        }
        if (builder.length() > 0) {
            builder.setLength(builder.length() - 1);
        }
        return builder.toString();
    }


    public Cursor getAllAnchorsAndTriangles() {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {
                COLUMN_ID,
                COLUMN_X,
                COLUMN_Y,
                COLUMN_Z,
                ANCHOR_ID,
                TRIANGLE_POINTS // make sure this column is included in the query
        };
        String orderBy = COLUMN_ID;
        return db.query(TABLE_NAME, columns, null, null, null, null, orderBy);
    }


}
