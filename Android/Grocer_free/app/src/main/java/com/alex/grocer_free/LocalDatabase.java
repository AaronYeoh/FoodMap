package com.alex.grocer_free;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by alex on 16/07/15.
 */
public class LocalDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "location2db.db";
    private static final String TABLE_LOCATION = "locations2";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_LAT_ADDRESS = "lat";
    private static final String COLUMN_LNG_ADDRESS = "lng";
    private static final String COLUMN_ITEM_TYPE = "item_type";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_IMAGE = "image";

    public LocalDatabase(Context context){
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
        db.execSQL("CREATE TABLE " + TABLE_LOCATION + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_LAT_ADDRESS + " DOUBLE, " +
                COLUMN_LNG_ADDRESS + " DOUBLE, " +
                COLUMN_ITEM_TYPE + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_IMAGE + " BLOB" +
                ")");
        populateDatabase();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion == 1) {
            // Wipe older tables if existed
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
            // Create tables again
            onCreate(db);
        }
    }

    public void insertItem(Item markerItem){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_LAT_ADDRESS, markerItem.getLat());
        cv.put(COLUMN_LNG_ADDRESS, markerItem.getLng());
        cv.put(COLUMN_ITEM_TYPE, markerItem.getItemType());




        cv.put(COLUMN_DESCRIPTION, markerItem.getDesc() + "," + getCurrentDate());
        cv.put(COLUMN_IMAGE, markerItem.getImage());

        long rowId = db.insertOrThrow(TABLE_LOCATION, null, cv);
        db.close();
    }

    public ArrayList<Item> getAllItems(){
        ArrayList<Item> itemList = new ArrayList<Item>();

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_LOCATION;
        Cursor c = db.rawQuery(query, null);

        if(c.moveToFirst()){
            do{
                Item item = new Item();
                item.setId(c.getInt(0));
                item.setLat(c.getDouble(1));
                item.setLng(c.getDouble(2));
                item.setItemType(c.getString(3));
                item.setDesc(c.getString(4));
                if(c.isNull(5)){

                } else {
                    item.setImage(c.getBlob(5));
                }
                itemList.add(item);
            } while(c.moveToNext());
        }
        if (c != null)
            c.close();

        return itemList;
    }




    private String getCurrentDate(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    private void populateDatabase(){
        SQLiteDatabase db = this.getWritableDatabase();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("grocer");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {

                    Log.d("score", "Retrieved " + scoreList.size() + " scores");
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }
}
