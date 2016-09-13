package com.tubiapp.demochatxmpp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tubiapp.demochatxmpp.Items.Message_Model;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = DatabaseHelper.class.getName();

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "message_database";

    // Table Names
    private static final String TABLE_MESSAGE_HISTORY = "message_history";
    private static final String TABLE_OFFLINE_MESSAGE = "offline_message";

    private static final String KEY_MESSAGE_ID = "m_id";
    private static final String KEY_MESSAGE_FROM = "m_from";
    private static final String KEY_MESSAGE_TO = "m_to";
    private static final String KEY_MESSAGE_TEXT = "m_text";
    private static final String KEY_MESSAGE_TIMESTAMP = "m_timestamp";

    private static final String KEY_OFFLINE_MESSAGE_ID = "m_offline_id";
    private static final String KEY_OFFLINE_MESSAGE_FROM = "m_offline_from";
    private static final String KEY_OFFLINE_MESSAGE_TO = "m_offline_to";
    private static final String KEY_OFFLINE_MESSAGE_TEXT = "m_offline_text";
    private static final String KEY_OFFLINE_MESSAGE_TIMESTAMP = "m_offline_timestamp";


    // Table Create Statements
    // Todo table create statement
    private static final String CREATE_TABLE_MESSAGE_HISTORY = "CREATE TABLE "
            + TABLE_MESSAGE_HISTORY
            + "("
            + KEY_MESSAGE_ID + " INTEGER PRIMARY KEY,"
            + KEY_MESSAGE_FROM + " TEXT,"
            + KEY_MESSAGE_TO + " TEXT,"
            + KEY_MESSAGE_TEXT + " TEXT,"
            + KEY_MESSAGE_TIMESTAMP + " TEXT"
            + ")";

    private static final String CREATE_TABLE_MESSAGE_OFFLINE = "CREATE TABLE "
            + TABLE_OFFLINE_MESSAGE
            + "("
            + KEY_OFFLINE_MESSAGE_ID + " INTEGER PRIMARY KEY,"
            + KEY_OFFLINE_MESSAGE_FROM + " TEXT,"
            + KEY_OFFLINE_MESSAGE_TO + " TEXT,"
            + KEY_OFFLINE_MESSAGE_TEXT + " TEXT,"
            + KEY_OFFLINE_MESSAGE_TIMESTAMP + " TEXT"
            + ")";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_MESSAGE_HISTORY);
        db.execSQL(CREATE_TABLE_MESSAGE_OFFLINE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OFFLINE_MESSAGE);

        onCreate(db);
    }


    public void addMessage(Message_Model message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_MESSAGE_FROM, message.getMessage_from());
        values.put(KEY_MESSAGE_TO, message.getMessage_to());
        values.put(KEY_MESSAGE_TEXT, message.getMessage_text());
        values.put(KEY_MESSAGE_TIMESTAMP, message.getMessage_time());
        db.insert(TABLE_MESSAGE_HISTORY, null, values);
        db.close();
    }

    public void addMessage_Offline(Message_Model message) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_OFFLINE_MESSAGE_FROM, message.getMessage_from());
        values.put(KEY_OFFLINE_MESSAGE_TO, message.getMessage_to());
        values.put(KEY_OFFLINE_MESSAGE_TEXT, message.getMessage_text());
        values.put(KEY_OFFLINE_MESSAGE_TIMESTAMP, message.getMessage_time());

        db.insert(TABLE_OFFLINE_MESSAGE, null, values);
        db.close();
    }

    // Getting single contact
    Message_Model getMessage(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_MESSAGE_HISTORY, new String[]{KEY_MESSAGE_ID,
                        KEY_MESSAGE_FROM, KEY_MESSAGE_TO, KEY_MESSAGE_TEXT, KEY_MESSAGE_TIMESTAMP}, KEY_MESSAGE_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Message_Model message = new Message_Model(cursor.getString(0),
                cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));

        return message;
    }

    public List<Message_Model> getMessage_by_id(Message_Model message) {
        List<Message_Model> contactList = new ArrayList<Message_Model>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_MESSAGE_HISTORY
                + " WHERE ( "
                + KEY_MESSAGE_FROM + " =  '" + message.getMessage_from()
                + "' OR "
                + KEY_MESSAGE_FROM + " = '" + message.getMessage_to()
                + "' ) AND ( "
                + KEY_MESSAGE_TO + " = '" + message.getMessage_from()
                + "' OR "
                + KEY_MESSAGE_TO + " = '" + message.getMessage_to()
                + "' )";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                Message_Model messagedata = new Message_Model();
                messagedata.setMessage_id(cursor.getString(0));
                messagedata.setMessage_from(cursor.getString(1));
                messagedata.setMessage_to(cursor.getString(2));
                messagedata.setMessage_text(cursor.getString(3));
                message.setMessage_time(cursor.getString(4));

                contactList.add(messagedata);
            } while (cursor.moveToNext());
        }


        return contactList;
    }

    public List<Message_Model> getOfflineMessage_by_id(Message_Model message) {
        List<Message_Model> contactList = new ArrayList<Message_Model>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + CREATE_TABLE_MESSAGE_OFFLINE
                + " WHERE ( "
                + KEY_OFFLINE_MESSAGE_FROM + " =  '" + message.getMessage_from()
                + "' OR "
                + KEY_OFFLINE_MESSAGE_FROM + " = '" + message.getMessage_to()
                + "' ) AND ( "
                + KEY_OFFLINE_MESSAGE_TO + " = '" + message.getMessage_from()
                + "' OR "
                + KEY_OFFLINE_MESSAGE_TO + " = '" + message.getMessage_to()
                + "' )";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                Message_Model messagedata = new Message_Model();
                messagedata.setMessage_id(cursor.getString(0));
                messagedata.setMessage_from(cursor.getString(1));
                messagedata.setMessage_to(cursor.getString(2));
                messagedata.setMessage_text(cursor.getString(3));
                message.setMessage_time(cursor.getString(4));

                contactList.add(messagedata);
            } while (cursor.moveToNext());
        }

        return contactList;
    }

    // Getting All Contacts
    public List<Message_Model> getAllMessage() {
        List<Message_Model> contactList = new ArrayList<Message_Model>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_MESSAGE_HISTORY;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Message_Model message = new Message_Model();
                message.setMessage_id(cursor.getString(0));
                message.setMessage_from(cursor.getString(1));
                message.setMessage_to(cursor.getString(2));
                message.setMessage_text(cursor.getString(3));
                message.setMessage_time(cursor.getString(4));
                // Adding contact to list
                contactList.add(message);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

    public List<Message_Model> getAll_OfflineMessage() {
        List<Message_Model> offline_message = new ArrayList<Message_Model>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_OFFLINE_MESSAGE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Message_Model message = new Message_Model();
                message.setMessage_id(cursor.getString(0));
                message.setMessage_from(cursor.getString(1));
                message.setMessage_to(cursor.getString(2));
                message.setMessage_text(cursor.getString(3));
                message.setMessage_time(cursor.getString(4));
                // Adding contact to list
                offline_message.add(message);
            } while (cursor.moveToNext());
        }

        // return contact list
        return offline_message;
    }

    // Updating single contact
    public int updateMessage(Message_Model contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_MESSAGE_FROM, contact.getMessage_from());
        values.put(KEY_MESSAGE_TO, contact.getMessage_to());
        values.put(KEY_MESSAGE_TEXT, contact.getMessage_text());
        values.put(KEY_MESSAGE_TIMESTAMP, contact.getMessage_time());

        // updating row
        return db.update(TABLE_MESSAGE_HISTORY, values, KEY_MESSAGE_ID + " = ?",
                new String[]{String.valueOf(contact.getMessage_id())});
    }

    // Deleting single contact
    public void deleteMessage(Message_Model contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MESSAGE_HISTORY, KEY_MESSAGE_ID + " = ?",
                new String[]{String.valueOf(contact.getMessage_id())});
        db.close();
    }

    public void delete_OfflineMessage() {

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_OFFLINE_MESSAGE);
        db.close();
    }


    // Getting contacts Count
    public int getMessageCount() {
        String countQuery = "SELECT  * FROM " + TABLE_MESSAGE_HISTORY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }

    public int getOfflineMessageCount() {
        String countQuery = "SELECT  * FROM " + TABLE_OFFLINE_MESSAGE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

 /*   public int getOfflineMessageCount() {
        String countQuery = "SELECT  * FROM " + TABLE_OFFLINE_MESSAGE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }*/


}