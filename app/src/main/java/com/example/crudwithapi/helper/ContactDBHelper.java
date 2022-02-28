package com.example.crudwithapi.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.crudwithapi.model.contact;
import java.io.File;

public class ContactDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "contactDB";
    private static final String TABLE_CONTACT = "contact";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PHONE = "phone";

    SQLiteDatabase db;

    public ContactDBHelper (Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String createTable = "CREATE TABLE " + TABLE_CONTACT
        + "(" + COLUMN_ID + " TEXT PRIMARY KEY,"
        + COLUMN_NAME + " TEXT NOT NULL,"
        + COLUMN_EMAIL + " TEXT NOT NULL,"
        + COLUMN_PHONE + " TEXT NOT NULL)";

        db.execSQL(createTable);
        Log.w("ContactDBHelper", "Create version " + DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACT);
        onCreate(db);
        Log.w("ContactDBHelper", "Upgrade version " + oldVersion + " to " + newVersion);
    }

    public String databaseExist(Context context) {
        File dbFile = context.getDatabasePath(DATABASE_NAME);

        if (dbFile.exists()) {
            return "Database Path" + dbFile.getAbsolutePath();
        }
        else{
            return "";
        }
    }

    public void onOpen(){
        super.onOpen(db);
        db = this.getWritableDatabase();
    }

    @Override
    public synchronized void close(){
        super.close();
    }

    public Cursor getAllContact(String txtSearch){
        String query = "SELECT * FROM " + TABLE_CONTACT
            + " WHERE " + COLUMN_NAME + " LIKE \"%" + txtSearch + "%\""
            + " OR " + COLUMN_EMAIL + " LIKE \"%" + txtSearch + "%\""
            + " OR " + COLUMN_PHONE + " LIKE \"%" + txtSearch + "%\"";

        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public contact getContactByID(String id){
        String query = "SELECT * FROM " + TABLE_CONTACT
                + " WHERE " + COLUMN_ID + " = \"" + id + "\"";

        Cursor cursor = db.rawQuery(query, null);
        contact mycontact = new contact();

        if (cursor.moveToFirst()){
            cursor.moveToFirst();
            mycontact.setID(cursor.getString(0));
            mycontact.setName(cursor.getString(1));
            mycontact.setEmail(cursor.getString(2));
            mycontact.setPhone(cursor.getString(3));
            cursor.close();
        }
        else{
            mycontact = null;
        }

        return mycontact;
    }

    public long insertContact(contact mycontact){
        ContentValues content = new ContentValues();
        content.put(COLUMN_ID, mycontact.getID());
        content.put(COLUMN_NAME, mycontact.getName());
        content.put(COLUMN_EMAIL, mycontact.getEmail());
        content.put(COLUMN_PHONE, mycontact.getPhone());

        long newRowId = db.insert(TABLE_CONTACT, null, content);
        return newRowId;
    }

    public long updateContact(String id, contact mycontact){
        ContentValues content = new ContentValues();
        content.put(COLUMN_NAME, mycontact.getName());
        content.put(COLUMN_EMAIL, mycontact.getEmail());
        content.put(COLUMN_PHONE, mycontact.getPhone());

        long oldRowId = db.update(TABLE_CONTACT, content, COLUMN_ID + " = \"" + id + "\"", null);
        return oldRowId;
    }

    public boolean deleteContact(String id){
        boolean result = false;
        String query = "SELECT * FROM " + TABLE_CONTACT + " WHERE " + COLUMN_ID + " = \"" + id + "\"";

        Cursor cursor = db.rawQuery(query, null);
        contact mycontact = new contact();
        if (cursor.moveToFirst()){
            mycontact.setID(cursor.getString(0));
            db.delete(TABLE_CONTACT, COLUMN_ID + " = ? ", new String[]{ String.valueOf(mycontact.getID()) });
            cursor.close();
            result = true;
        }

        return result;
    }
}
