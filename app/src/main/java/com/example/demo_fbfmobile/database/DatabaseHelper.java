package com.example.demo_fbfmobile.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "food_db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_LIKES = "likes";
    public static final String COLUMN_FOOD_ID = "food_id";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_IS_LIKED = "is_liked";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_LIKES + " (" +
                COLUMN_FOOD_ID + " TEXT, " +
                COLUMN_USER_ID + " TEXT, " +
                COLUMN_IS_LIKED + " INTEGER, " +
                "PRIMARY KEY (" + COLUMN_FOOD_ID + ", " + COLUMN_USER_ID + "))";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIKES);
        onCreate(db);
    }

    // Thêm trạng thái like vào cơ sở dữ liệu
    public void setLikeStatus(String foodId, String userId, boolean isLiked) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FOOD_ID, foodId);
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_IS_LIKED, isLiked ? 1 : 0);

        // Kiểm tra nếu món ăn và user đã có trong bảng, nếu có thì update, nếu không thì insert mới
        int rowsAffected = db.update(TABLE_LIKES, values, COLUMN_FOOD_ID + " = ? AND " + COLUMN_USER_ID + " = ?", new String[]{foodId, userId});
        if (rowsAffected == 0) {
            db.insert(TABLE_LIKES, null, values);
        }
        db.close();
    }

    // Lấy trạng thái like của món ăn đối với một user cụ thể
    public boolean getLikeStatus(String foodId, String userId) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_LIKES, new String[]{COLUMN_IS_LIKED},
                COLUMN_FOOD_ID + " = ? AND " + COLUMN_USER_ID + " = ?", new String[]{foodId, userId},
                null, null, null);

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(COLUMN_IS_LIKED);
                    return index != -1 && cursor.getInt(index) == 1;
                } else {
                    ContentValues values = new ContentValues();
                    values.put(COLUMN_USER_ID, userId);
                    values.put(COLUMN_FOOD_ID, foodId);
                    values.put(COLUMN_IS_LIKED, 0);
                    db.insert(TABLE_LIKES, null, values);
                    return false;
                }
            } finally {
                cursor.close();
            }
        }
        return false;
    }

}

