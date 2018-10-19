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

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "lab01";
    private static final String DB_TABLE_IMAGES = "images";

    // column names
    private static final String Key_INDEX = "image_id";
    private static final String Key_TITLE = "image_name";
    private static final String Key_TAGS = "image_tags";
    private static final String KEY_IMAGE = "image_data";

    // Table create statement
    private static final String CREATE_TABLE_IMAGES = "CREATE TABLE " + DB_TABLE_IMAGES + "("+
            Key_INDEX + " INTEGER PRIMARY KEY," +
            Key_TITLE + " TEXT," +
            Key_TAGS + " TEXT," +
            KEY_IMAGE + " BLOB);";

    // SQL REQUESTS
    private static final String GET_ALL_ENTRIES = "SELECT * FROM " + DB_TABLE_IMAGES + ";";
    private static final String GET_ENTRY_FROM_INDEX = "SELECT * FROM " + DB_TABLE_IMAGES + " WHERE " + Key_INDEX + " = ";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public long addEntry(GalleryImage newImage) throws SQLiteException {
        SQLiteDatabase database = this.getWritableDatabase();
        DatabaseBitmapUtils utils = new DatabaseBitmapUtils();

        byte[] image = utils.getBytes(newImage.getImage());

        ContentValues cv = new  ContentValues();
        cv.put(Key_TITLE, newImage.getTitle());
        cv.put(KEY_IMAGE, image);
        cv.put(Key_TAGS, newImage.getTags());

        long index = database.insert(DB_TABLE_IMAGES, null, cv);

        String message = "";

        for(GalleryImage gi : getEntries()){
            message += "\n->" + gi.toString();
        }

        return index;
    }

    public ArrayList<GalleryImage> getEntries(){
        SQLiteDatabase database = this.getWritableDatabase();
        DatabaseBitmapUtils utils = new DatabaseBitmapUtils();

        Cursor cursor = database.rawQuery(GET_ALL_ENTRIES, null);
        ArrayList<GalleryImage> images = new ArrayList<GalleryImage>();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                byte[] image = cursor.getBlob(cursor.getColumnIndex(KEY_IMAGE));
                String title = cursor.getString(cursor.getColumnIndex(Key_TITLE));
                String tags = cursor.getString(cursor.getColumnIndex(Key_TAGS));
                Bitmap bitmap = utils.getImage(image);
                images.add(new GalleryImage(title, tags, bitmap));
                cursor.moveToNext();
            }
        }

        return images;
    }

    public GalleryImage getEntry(long index){

        SQLiteDatabase database = this.getWritableDatabase();
        DatabaseBitmapUtils utils = new DatabaseBitmapUtils();

        Cursor cursor = database.rawQuery(GET_ENTRY_FROM_INDEX + String.valueOf(index) + ";", null);
        GalleryImage galleryImage = new GalleryImage("", "", null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                byte[] image = cursor.getBlob(cursor.getColumnIndex(KEY_IMAGE));
                String title = cursor.getString(cursor.getColumnIndex(Key_TITLE));
                String tags = cursor.getString(cursor.getColumnIndex(Key_TAGS));
                Bitmap bitmap = utils.getImage(image);
                galleryImage.setImage(bitmap);
                galleryImage.setTags(tags);
                galleryImage.setTitle(title);
                cursor.moveToNext();
            }
        }
        if (galleryImage.getTitle() != "") {
            galleryImage.print();
            return galleryImage;
        } else {
            return null;
        }

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_IMAGES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_IMAGES);

        // create new table
        onCreate(db);
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE  FROM " + DB_TABLE_IMAGES);
        Log.d("debug", "deleting all rows...");
    }
}