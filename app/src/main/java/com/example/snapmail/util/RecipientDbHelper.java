package com.example.snapmail.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.snapmail.model.Recipient;

import java.util.ArrayList;
import java.util.List;

public class RecipientDbHelper extends SQLiteOpenHelper {

    // 数据库常量
    private static final String DATABASE_NAME = "snapmail.db";
    private static final int DATABASE_VERSION = 3;

    // 表名和列名
    private static final String TABLE_RECIPIENTS = "recipients";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_REMARK = "remark";

    // 创建表的SQL语句
    private static final String CREATE_TABLE_RECIPIENTS = "CREATE TABLE " +
            TABLE_RECIPIENTS + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_EMAIL + " TEXT NOT NULL UNIQUE, " +
            COLUMN_REMARK + " TEXT);";

    public RecipientDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建收件人表
        db.execSQL(CREATE_TABLE_RECIPIENTS);
        
        // 创建发件人表（如果不存在）
        db.execSQL("CREATE TABLE IF NOT EXISTS senders(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email TEXT NOT NULL, " +
                "password TEXT NOT NULL, " +
                "smtp_host TEXT NOT NULL, " +
                "smtp_port INTEGER NOT NULL, " +
                "is_default INTEGER DEFAULT 0);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // 创建发件人表
            db.execSQL("CREATE TABLE IF NOT EXISTS senders(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "email TEXT NOT NULL, " +
                    "password TEXT NOT NULL, " +
                    "smtp_host TEXT NOT NULL, " +
                    "smtp_port INTEGER NOT NULL, " +
                    "is_default INTEGER DEFAULT 0);");
        }
        
        if (oldVersion < 3) {
            // 添加备注字段，如果不存在的话
            try {
                db.execSQL("ALTER TABLE " + TABLE_RECIPIENTS + " ADD COLUMN " + COLUMN_REMARK + " TEXT");
            } catch (Exception e) {
                // 如果字段已存在，忽略错误
            }
        }
    }

    /**
     * 插入新收件人
     */
    public long insertRecipient(Recipient recipient) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, recipient.getEmail());
        values.put(COLUMN_REMARK, recipient.getRemark());

        // 插入行
        long id = db.insert(TABLE_RECIPIENTS, null, values);
        db.close();
        return id;
    }

    /**
     * 获取所有收件人
     */
    public List<Recipient> getAllRecipients() {
        List<Recipient> recipientList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_RECIPIENTS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // 遍历所有行并添加到列表
        if (cursor.moveToFirst()) {
            do {
                Recipient recipient = new Recipient(
                        cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_REMARK))
                );
                recipientList.add(recipient);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return recipientList;
    }

    /**
     * 删除收件人
     */
    public void deleteRecipient(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RECIPIENTS, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }
}