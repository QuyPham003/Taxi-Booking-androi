package com.example.doancuoiky;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "TaxiBooking.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_USERS = "Users";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_NAME = "name"; // Thêm cột tên khách hàng

    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PHONE + " TEXT UNIQUE, " +
                    COLUMN_PASSWORD + " TEXT, " +
                    COLUMN_NAME + " TEXT" + // Thêm cột tên khách hàng
                    ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Thêm người dùng mới vào cơ sở dữ liệu
    public boolean addUser(String phone, String password, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_NAME, name); // Thêm tên vào cơ sở dữ liệu

        long result = db.insert(TABLE_USERS, null, values);
        db.close();

        return result != -1;
    }

    // Kiểm tra người dùng đã tồn tại hay chưa
    public boolean checkUser(String phone, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS +
                        " WHERE " + COLUMN_PHONE + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{phone, password});

        boolean userExists = cursor.getCount() > 0; // Returns true if user exists
        cursor.close();
        return userExists;
    }

    // Lấy tên người dùng dựa trên số điện thoại
    public String getUserName(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_NAME + " FROM " + TABLE_USERS +
                        " WHERE " + COLUMN_PHONE + "=?",
                new String[]{phone});

        String userName = "";
        if (cursor != null && cursor.moveToFirst()) {
            userName = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
        }
        cursor.close();
        return userName;
    }

    // Cập nhật tên người dùng trong cơ sở dữ liệu
    public boolean updateUserName(String phone, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, newName); // Cập nhật tên

        int result = db.update(TABLE_USERS, values, COLUMN_PHONE + "=?", new String[]{phone});
        db.close();

        return result > 0; // Trả về true nếu cập nhật thành công
    }

    // Cập nhật mật khẩu người dùng trong cơ sở dữ liệu
    public boolean updateUserPassword(String phone, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, newPassword); // Cập nhật mật khẩu

        int result = db.update(TABLE_USERS, values, COLUMN_PHONE + "=?", new String[]{phone});
        db.close();

        return result > 0; // Trả về true nếu cập nhật thành công
    }
}
