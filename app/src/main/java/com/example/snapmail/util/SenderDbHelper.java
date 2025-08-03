package com.example.snapmail.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.snapmail.model.Sender;

import java.util.ArrayList;
import java.util.List;

public class SenderDbHelper extends SQLiteOpenHelper {

    // 数据库常量
    private static final String DATABASE_NAME = "snapmail.db";
    private static final int DATABASE_VERSION = 3;

    // 表名和列名
    private static final String TABLE_SENDERS = "senders";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_SMTP_HOST = "smtp_host";
    private static final String COLUMN_SMTP_PORT = "smtp_port";
    private static final String COLUMN_IS_DEFAULT = "is_default";

    // 创建表的SQL语句
    private static final String CREATE_TABLE_SENDERS = "CREATE TABLE " +
            TABLE_SENDERS + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_EMAIL + " TEXT NOT NULL, " +
            COLUMN_PASSWORD + " TEXT NOT NULL, " +
            COLUMN_SMTP_HOST + " TEXT NOT NULL, " +
            COLUMN_SMTP_PORT + " INTEGER NOT NULL, " +
            COLUMN_IS_DEFAULT + " INTEGER DEFAULT 0);";

    public SenderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建收件人表
        db.execSQL("CREATE TABLE recipients(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email TEXT NOT NULL UNIQUE);");
        
        // 创建发件人表
        db.execSQL(CREATE_TABLE_SENDERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // 创建发件人表
            db.execSQL(CREATE_TABLE_SENDERS);
        }
    }

    /**
     * 插入新发件人
     */
    public long insertSender(Sender sender) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, sender.getEmail());
        values.put(COLUMN_PASSWORD, sender.getPassword());
        values.put(COLUMN_SMTP_HOST, sender.getSmtpHost());
        values.put(COLUMN_SMTP_PORT, sender.getSmtpPort());
        values.put(COLUMN_IS_DEFAULT, sender.isDefault() ? 1 : 0);

        // 插入行
        long id = db.insert(TABLE_SENDERS, null, values);
        db.close();
        return id;
    }

    /**
     * 获取所有发件人
     */
    public List<Sender> getAllSenders() {
        List<Sender> senderList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_SENDERS + " ORDER BY " + COLUMN_IS_DEFAULT + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // 遍历所有行并添加到列表
        if (cursor.moveToFirst()) {
            do {
                Sender sender = new Sender(
                        cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_SMTP_HOST)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_SMTP_PORT)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_IS_DEFAULT)) == 1
                );
                senderList.add(sender);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return senderList;
    }

    /**
     * 获取默认发件人
     */
    public Sender getDefaultSender() {
        String selectQuery = "SELECT * FROM " + TABLE_SENDERS + " WHERE " + COLUMN_IS_DEFAULT + " = 1 LIMIT 1";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        Sender sender = null;
        if (cursor.moveToFirst()) {
            sender = new Sender(
                    cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_SMTP_HOST)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_SMTP_PORT)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_IS_DEFAULT)) == 1
            );
        }

        cursor.close();
        db.close();
        return sender;
    }

    /**
     * 删除发件人
     */
    public void deleteSender(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SENDERS, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    /**
     * 设置默认发件人
     */
    public void setDefaultSender(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        // 先清除所有默认标记
        ContentValues clearValues = new ContentValues();
        clearValues.put(COLUMN_IS_DEFAULT, 0);
        db.update(TABLE_SENDERS, clearValues, null, null);
        
        // 设置新的默认发件人
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_DEFAULT, 1);
        db.update(TABLE_SENDERS, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        
        db.close();
    }
} 