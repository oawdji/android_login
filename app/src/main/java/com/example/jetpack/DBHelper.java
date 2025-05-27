package com.example.jetpack;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DBNAME = "Login.db";

    public DBHelper(Context context) {
        super(context, "Login.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase MyDB) {
        MyDB.execSQL("CREATE TABLE users(username TEXT PRIMARY KEY, password TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int oldVersion, int newVersion) {
        MyDB.execSQL("DROP TABLE IF EXISTS users");
    }

    public Boolean insertData(String username, String password) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        // 加密密码
        String encryptedPassword = EncryptionUtils.encryptPassword(password);
        contentValues.put("password", encryptedPassword);
        long result = MyDB.insert("users", null, contentValues);
        return result != -1;
    }

    public Boolean checkUsername(String username) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery(
                "SELECT * FROM users WHERE username = ?",
                new String[]{username}
        );
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public Boolean checkUsernamePassword(String username, String password) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        // 加密密码
        String encryptedPassword = EncryptionUtils.encryptPassword(password);
        Cursor cursor = MyDB.rawQuery(
                "SELECT * FROM users WHERE username = ? AND password = ?",
                new String[]{username, encryptedPassword}
        );
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public Boolean isAdmin(String username, String password) {
        // 加密管理员密码
        String encryptedAdminPassword = EncryptionUtils.encryptPassword("admin123");
        String encryptedPassword = EncryptionUtils.encryptPassword(password);
        return username.equals("admin") && encryptedPassword.equals(encryptedAdminPassword);
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
        // 加密新密码
        String encryptedPassword = EncryptionUtils.encryptPassword(newPassword);
        values.put("password", encryptedPassword);
        return db.update("users", values, "username = ?", new String[]{oldUsername}) > 0;
    }

    // 搜索用户
    public Cursor searchUsers(String keyword) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM users WHERE username != 'admin' AND username LIKE ?";
        String[] selectionArgs = new String[]{"%" + keyword + "%"};
        return db.rawQuery(query, selectionArgs);
    }
}
