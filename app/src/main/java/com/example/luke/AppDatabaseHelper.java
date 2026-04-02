package com.example.luke;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class AppDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "luke.db";
    private static final int DB_VERSION = 2;

    public static final String TABLE_POINTS = "points";
    public static final String TABLE_PRODUCTS = "products";

    public AppDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_POINTS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "point_name TEXT NOT NULL, " +
                "city TEXT NOT NULL, " +
                "address TEXT NOT NULL UNIQUE, " +
                "latitude REAL NOT NULL, " +
                "longitude REAL NOT NULL, " +
                "contact_name TEXT NOT NULL, " +
                "contact_phone TEXT NOT NULL, " +
                "email TEXT NOT NULL, " +
                "password TEXT NOT NULL, " +
                "work_hours TEXT NOT NULL"
                + ")");

        db.execSQL("CREATE TABLE " + TABLE_PRODUCTS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "point_address TEXT NOT NULL, " +
                "name TEXT NOT NULL, " +
                "description TEXT NOT NULL, " +
                "total_quantity INTEGER NOT NULL, " +
                "collected_quantity INTEGER NOT NULL, " +
                "urgency TEXT NOT NULL"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POINTS);
        onCreate(db);
    }

    public long upsertPoint(PointProfile profile) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLE_POINTS, new String[]{"id"}, "address=?", new String[]{profile.getAddress()}, null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();

        ContentValues values = new ContentValues();
        values.put("point_name", profile.getPointName());
        values.put("city", profile.getCity());
        values.put("address", profile.getAddress());
        values.put("latitude", profile.getLatitude());
        values.put("longitude", profile.getLongitude());
        values.put("contact_name", profile.getContactName());
        values.put("contact_phone", profile.getContactPhone());
        values.put("email", profile.getEmail());
        values.put("password", profile.getPassword());
        values.put("work_hours", profile.getWorkHours());

        if (exists) {
            return db.update(TABLE_POINTS, values, "address=?", new String[]{profile.getAddress()});
        }
        return db.insert(TABLE_POINTS, null, values);
    }

    public boolean updatePoint(String originalAddress, PointProfile profile) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("point_name", profile.getPointName());
        values.put("city", profile.getCity());
        values.put("address", profile.getAddress());
        values.put("latitude", profile.getLatitude());
        values.put("longitude", profile.getLongitude());
        values.put("contact_name", profile.getContactName());
        values.put("contact_phone", profile.getContactPhone());
        values.put("email", profile.getEmail());
        values.put("password", profile.getPassword());
        values.put("work_hours", profile.getWorkHours());
        return db.update(TABLE_POINTS, values, "address=?", new String[]{originalAddress}) > 0;
    }

    public PointProfile getPointByAddress(String address) {
        if (address == null) return null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_POINTS, null, "address=?", new String[]{address}, null, null, null);
        try {
            if (cursor.moveToFirst()) return cursorToPoint(cursor);
            return null;
        } finally {
            cursor.close();
        }
    }

    public PointProfile getPointByName(String pointName) {
        if (pointName == null) return null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_POINTS, null, "LOWER(point_name)=LOWER(?) OR LOWER(contact_name)=LOWER(?)", new String[]{pointName, pointName}, null, null, null);
        try {
            if (cursor.moveToFirst()) return cursorToPoint(cursor);
            return null;
        } finally {
            cursor.close();
        }
    }

    public List<PointProfile> getAllPoints() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_POINTS, null, null, null, null, null, "id ASC");
        List<PointProfile> result = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                result.add(cursorToPoint(cursor));
            }
        } finally {
            cursor.close();
        }
        return result;
    }

    public List<PointProfile> getPointsByCity(String city) {
        if (city == null || city.isEmpty() || "Все города".equals(city)) {
            return getAllPoints();
        }
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_POINTS, null, "city=?", new String[]{city}, null, null, "id ASC");
        List<PointProfile> result = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                result.add(cursorToPoint(cursor));
            }
        } finally {
            cursor.close();
        }
        return result;
    }

    public long insertProduct(String pointAddress, Product product) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("point_address", pointAddress);
        values.put("name", product.getName());
        values.put("description", product.getDescription());
        values.put("total_quantity", product.getTotalQuantity());
        values.put("collected_quantity", product.getCollectedQuantity());
        values.put("urgency", product.getUrgency());
        long id = db.insert(TABLE_PRODUCTS, null, values);
        product.setId(id);
        product.setPointAddress(pointAddress);
        return id;
    }

    public List<Product> getProductsForPoint(String pointAddress) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCTS, null, "point_address=?", new String[]{pointAddress}, null, null, "id ASC");
        List<Product> result = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                result.add(cursorToProduct(cursor));
            }
        } finally {
            cursor.close();
        }
        return result;
    }

    public Product getProductById(long id) {
        if (id <= 0) return null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCTS, null, "id=?", new String[]{String.valueOf(id)}, null, null, null);
        try {
            if (cursor.moveToFirst()) return cursorToProduct(cursor);
            return null;
        } finally {
            cursor.close();
        }
    }

    public boolean updateProduct(Product product) {
        if (product.getId() <= 0) return false;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("point_address", product.getPointAddress());
        values.put("name", product.getName());
        values.put("description", product.getDescription());
        values.put("total_quantity", product.getTotalQuantity());
        values.put("collected_quantity", product.getCollectedQuantity());
        values.put("urgency", product.getUrgency());
        return db.update(TABLE_PRODUCTS, values, "id=?", new String[]{String.valueOf(product.getId())}) > 0;
    }

    public boolean deleteProduct(long id) {
        if (id <= 0) return false;
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_PRODUCTS, "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean isPointTableEmpty() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_POINTS, null);
        try {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0) == 0;
            }
            return true;
        } finally {
            cursor.close();
        }
    }

    private PointProfile cursorToPoint(Cursor cursor) {
        return new PointProfile(
                cursor.getString(cursor.getColumnIndexOrThrow("point_name")),
                cursor.getString(cursor.getColumnIndexOrThrow("city")),
                cursor.getString(cursor.getColumnIndexOrThrow("address")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("latitude")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("longitude")),
                cursor.getString(cursor.getColumnIndexOrThrow("contact_name")),
                cursor.getString(cursor.getColumnIndexOrThrow("contact_phone")),
                cursor.getString(cursor.getColumnIndexOrThrow("email")),
                cursor.getString(cursor.getColumnIndexOrThrow("password")),
                cursor.getString(cursor.getColumnIndexOrThrow("work_hours"))
        );
    }

    private Product cursorToProduct(Cursor cursor) {
        Product product = new Product(
                cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("point_address")),
                cursor.getString(cursor.getColumnIndexOrThrow("name")),
                cursor.getString(cursor.getColumnIndexOrThrow("description")),
                cursor.getInt(cursor.getColumnIndexOrThrow("total_quantity")),
                cursor.getInt(cursor.getColumnIndexOrThrow("collected_quantity")),
                cursor.getString(cursor.getColumnIndexOrThrow("urgency"))
        );
        return product;
    }
}
