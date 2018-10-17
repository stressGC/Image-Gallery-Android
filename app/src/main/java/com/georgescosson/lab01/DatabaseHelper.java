package com.georgescosson.lab01;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Georges on 16/10/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "lab01";

    // Table Names
    private static final String DB_TABLE = "images";

    // column names
    private static final String KEY_NAME = "image_name";
    private static final String KEY_IMAGE = "image_data";

    // Table create statement
    private static final String CREATE_TABLE_IMAGE = "CREATE TABLE " + DB_TABLE + "("+
            KEY_NAME + " TEXT," +
            KEY_IMAGE + " BLOB);";

    // Get all entries statement
    private static final String GET_ALL_ENTRIES = "SELECT * FROM " + DB_TABLE + ";";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void addEntry(GalleryImage newImage) throws SQLiteException {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new  ContentValues();

        DatabaseBitmapUtils utils = new DatabaseBitmapUtils();
        byte[] image = utils.getBytes(newImage.getImage());

        cv.put(KEY_NAME, newImage.getTitle());
        cv.put(KEY_IMAGE, image);
        database.insert( DB_TABLE, null, cv);

        Log.d("debug", "Your image "+newImage.getTitle()+" has been saved to db");

        String message = "";

        for(GalleryImage gi : getEntries()){
            message += "\n->" + gi.getTitle();
        }

        Log.d("debug", "Entries are now : " + message);
    }

    public ArrayList<GalleryImage> getEntries(){
        SQLiteDatabase database = this.getWritableDatabase();
        DatabaseBitmapUtils utils = new DatabaseBitmapUtils();

        Cursor cursor = database.rawQuery(GET_ALL_ENTRIES, null);
        ArrayList<GalleryImage> images = new ArrayList<GalleryImage>();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                byte[] image = cursor.getBlob(cursor.getColumnIndex(KEY_IMAGE));
                String title = cursor.getString(cursor.getColumnIndex(KEY_NAME));
                Bitmap bitmap = utils.getImage(image);
                images.add(new GalleryImage(title, bitmap));
                cursor.moveToNext();
            }
        }

        for (GalleryImage image : images){
            Log.d("debug", image.toString());
        }

        return images;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating table
        db.execSQL(CREATE_TABLE_IMAGE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);

        // create new table
        onCreate(db);
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE  FROM " + DB_TABLE);
        Log.d("debug", "deleting all rows...");
    }
}