package com.alex.grocer_free;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
        db.execSQL("CREATE TABLE " + TABLE_LOCATION + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_LAT_ADDRESS + " DOUBLE, " +
                COLUMN_LNG_ADDRESS + " DOUBLE, " +
                COLUMN_ITEM_TYPE + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_IMAGE + " BLOB" +
                ")");
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

    public Item getItemByLatLng(double lat, double lng){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_LOCATION + " WHERE " +
                "(" + "(" + COLUMN_LAT_ADDRESS + "=" + lat + ") AND " +
                        "(" + COLUMN_LNG_ADDRESS + "=" + lng + ")" +
                ")";
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        Item item = new Item();
        item.setDesc(cursor.getString(4));
        item.setItemType(cursor.getString(3));
        item.setImage(cursor.getBlob(5));
        cursor.close();
        return item;
    }


    public void updateItem(LatLng position, String update){

        update = update + "," + getCurrentDate();

        SQLiteDatabase db = this.getWritableDatabase();
        Item item = getItemByLatLng(position.latitude, position.longitude);

        item.setDesc(item.getDesc().concat("###" + update));

        String query = "(" + COLUMN_LAT_ADDRESS + "=" + position.latitude + ") AND " +
                "(" + COLUMN_LNG_ADDRESS + "=" + position.longitude + ")";


        ContentValues args = new ContentValues();
        args.put(COLUMN_DESCRIPTION, item.getDesc());
        db.update(TABLE_LOCATION, args, query, null);
    }

    private String getCurrentDate(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }
}
