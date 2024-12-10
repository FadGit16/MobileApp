package com.example.feedmypet.DBconnector;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "pet_shop";
    private static final int DATABASE_VERSION = 3; // Increment this if you make changes

    private static final String CREATE_PRODUCT_TABLE = "CREATE TABLE products (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "product_name TEXT," +
            "price TEXT," +
            "brand TEXT," +
            "type TEXT," +
            "age TEXT," +
            "description TEXT," +
            "image BLOB);";

    private static final String CREATE_USER_TABLE = "CREATE TABLE users (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "username TEXT," +
            "password TEXT," +
            "email TEXT," +
            "usertype TEXT," +
            "profile_pic BLOB);";

    private static final String CREATE_CART_TABLE = "CREATE TABLE cart (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "product_id INTEGER," +
            "product_name TEXT," +
            "product_price DOUBLE," +
            "quantity INTEGER," +
            "FOREIGN KEY(product_id) REFERENCES products(id));";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DatabaseHelper", "Creating database tables");
        db.execSQL(CREATE_PRODUCT_TABLE);
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_CART_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE users ADD COLUMN profile_pic BLOB");
        }
        db.execSQL("DROP TABLE IF EXISTS cart");
        db.execSQL("DROP TABLE IF EXISTS products");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }
}
