package com.rodnog.rogermiddenway.restaurantmap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(@Nullable Context context) {
        super(context, Util.DATABASE_NAME, null, Util.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + Util.TABLE_NAME + "(" + Util.LOC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Util.NAME + " TEXT, " + Util.LAT + " INTEGER, " + Util.LONG + " INTEGER);" ;
        db.execSQL(CREATE_USER_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DROP_USER_TABLE = "DROP TABLE IF EXISTS ";
        db.execSQL(DROP_USER_TABLE + Util.TABLE_NAME);
        onCreate(db);
    }

    public long insertRestaurant(Restaurant restaurant) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Util.NAME, restaurant.getName());
        contentValues.put(Util.LAT, restaurant.getLatitude());
        contentValues.put(Util.LONG, restaurant.getLongitude());
        long newRowId = db.insert(Util.TABLE_NAME, null, contentValues);
        db.close();
        return newRowId;
    }


    public List<Restaurant> fetchAllRestaurants(){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectAll = "SELECT * FROM " + Util.TABLE_NAME;
        Cursor cursor = db.rawQuery(selectAll, null);
        List<Restaurant> restaurantList = new ArrayList<>();
        if(cursor.moveToFirst()){
            do {
                Restaurant restaurant = new Restaurant();
                restaurant.setName(cursor.getString(cursor.getColumnIndex(Util.NAME)));
                restaurant.setLatitude(cursor.getDouble(cursor.getColumnIndex(Util.LAT)));
                restaurant.setLongitude(cursor.getDouble(cursor.getColumnIndex(Util.LONG)));
                restaurantList.add(restaurant);
            }
            while(cursor.moveToNext());
            db.close();
        }
        return restaurantList;
    }
}
