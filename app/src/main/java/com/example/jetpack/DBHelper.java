package com.example.jetpack;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DBNAME = "Login.db";  // 修正：atatic → static

    public DBHelper(Context context) {
        super(context, "Login.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase MyDB) {
        MyDB.execSQL("CREATE TABLE users(username TEXT PRIMARY KEY, password TEXT)");  // 修正：Table → TABLE（规范写法）
    }

    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int oldVersion, int newVersion) {
        MyDB.execSQL("DROP TABLE IF EXISTS users");  // 修正：drop Table → DROP TABLE（规范写法）
    }

    public Boolean insertData(String username, String password) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("password", password);
        long result = MyDB.insert("users", null, contentValues);
        return result != -1;  // 简化返回逻辑
    }

    public Boolean checkUsername(String username) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery(
                "SELECT * FROM users WHERE username = ?",  // 修正：+ → *，user → users
                new String[]{username}
        );
        boolean exists = cursor.getCount() > 0;
        cursor.close();  // 必须关闭Cursor，避免内存泄漏
        return exists;
    }

    public Boolean checkUsernamePassword(String username, String password) {
        SQLiteDatabase MyDB = this.getWritableDatabase();  // 修正：MyDb → MyDB（大小写统一）
        Cursor cursor = MyDB.rawQuery(
                "SELECT * FROM users WHERE username = ? AND password = ?",  // 修正：+ → *，user → users
                new String[]{username, password}
        );
        boolean exists = cursor.getCount() > 0;
        cursor.close();  // 必须关闭Cursor
        return exists;
    }
    public Boolean isAdmin(String username, String password) {
        return username.equals("admin") && password.equals("admin123");
    }

    // 获取所有用户
    public Cursor getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM users WHERE username != 'admin'", null);
    }

    // 删除用户
    public Boolean deleteUser(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("users", "username = ?", new String[]{username}) > 0;
    }

    // 更新用户
    public Boolean updateUser(String oldUsername, String newUsername, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", newUsername);
        values.put("password", newPassword);
        return db.update("users", values, "username = ?", new String[]{oldUsername}) > 0;
    }
}
