package edu.scu.mmalik1.photonotes;

/**
 * Created by abhimanyusingh on 5/7/16.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class Db_Helper {
    public static final String NOTE_ID = "id";
    public static final String NOTE_TEXT = "name";
    public static final String NOTE_PHOTO = "photo";
    public static final String NOTE_LAT = "lat";
    public static final String NOTE_LONG = "long";
    public static final String NOTE_VOICE = "voice";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "NotesDB.db";
    private static final int DATABASE_VERSION = 1;

    private static final String PhotoNotes_TABLE = "Notes";

    private static final String CREATE_PhotoNotes_TABLE = "create table "
            + PhotoNotes_TABLE + " (" + NOTE_ID
            + " integer primary key autoincrement, " + NOTE_PHOTO
            + " text not null, "+ NOTE_TEXT + " text not null, "+ NOTE_LAT
            + " text not null, " + NOTE_LONG + " text not null, "+ NOTE_VOICE + " text not null)";

    private final Context mCtx;

    private class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL("DROP TABLE IF EXISTS " + PhotoNotes_TABLE);
            db.execSQL(CREATE_PhotoNotes_TABLE);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + PhotoNotes_TABLE);
            onCreate(db);
        }
    }

    public void Reset() {
        mDbHelper.onUpgrade(this.mDb, 1, 1);
    }

    public Db_Helper(Context ctx) {
        mCtx = ctx;
        mDbHelper = new DatabaseHelper(mCtx);
    }

    public Db_Helper open() throws SQLException {
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public void insertNotesDetails(Notes note) {
        ContentValues cv = new ContentValues();
        cv.put(NOTE_PHOTO, note.getFilename());
        cv.put(NOTE_TEXT, note.getText());
        cv.put(NOTE_LAT, note.getlocLat());
        cv.put(NOTE_LONG, note.getLocLong());
        cv.put(NOTE_VOICE, note.getRecVoice());

        mDb.insert(PhotoNotes_TABLE, null, cv);
    }

    public List<Notes> retriveNoteDetails() throws SQLException {
        Cursor cur = mDb.query(true, PhotoNotes_TABLE, new String[]{NOTE_PHOTO,
                NOTE_TEXT,NOTE_LAT,NOTE_LONG,NOTE_VOICE}, null, null, null, null, null, null);
        List<Notes> returnList = new ArrayList<Notes>();
        if(!cur.moveToFirst())
            return null;
        do {
            String photo = cur.getString(cur.getColumnIndex(NOTE_PHOTO));
            String text = cur.getString(cur.getColumnIndex(NOTE_TEXT));
            String lat = cur.getString(cur.getColumnIndex(NOTE_LAT));
            String lon = cur.getString(cur.getColumnIndex(NOTE_LONG));
            String voice = cur.getString(cur.getColumnIndex(NOTE_VOICE));

            Notes n = new Notes(text,photo,lat,lon,voice);
            returnList.add(n);
        }while( (cur.moveToNext()));
        cur.close();
        return returnList;
    }

    public Notes retriveNote(int position) throws SQLException {
        Cursor cur = mDb.query(true, PhotoNotes_TABLE, new String[]{NOTE_PHOTO,
                NOTE_TEXT,NOTE_LAT,NOTE_LONG,NOTE_VOICE}, null, null, null, null, null, null);
        Notes returnList = null;
        if(cur.moveToPosition(position))
        {
                String photo = cur.getString(cur.getColumnIndex(NOTE_PHOTO));
                String name = cur.getString(cur.getColumnIndex(NOTE_TEXT));
            String lat = cur.getString(cur.getColumnIndex(NOTE_LAT));
            String lon = cur.getString(cur.getColumnIndex(NOTE_LONG));
            String voice = cur.getString(cur.getColumnIndex(NOTE_VOICE));

            returnList =new Notes(name, photo,lat,lon,voice);
        }
        cur.close();
        return returnList;
    }

    public void removeNote(String filename)
    {
        Cursor cur = mDb.query(true, PhotoNotes_TABLE, new String[]{NOTE_PHOTO,
                NOTE_TEXT,NOTE_LAT,NOTE_LONG,NOTE_VOICE}, NOTE_PHOTO+"=?", new String[]{filename}, null, null, null, null);
        if(cur.moveToFirst())
        {
            System.out.println(cur.getString(cur.getColumnIndex(NOTE_TEXT)));
        }
        mDb.delete(PhotoNotes_TABLE,NOTE_PHOTO+"='"+filename+"'",null);
    }

    public void moveNote(String oldfile, String newfile)
    {
        Cursor cur = mDb.query(true, PhotoNotes_TABLE, new String[]{NOTE_ID,NOTE_PHOTO,
                NOTE_TEXT,NOTE_LAT,NOTE_LONG,NOTE_VOICE}, NOTE_PHOTO+"=?", new String[]{newfile}, null, null, null, null);
        if(cur.moveToFirst()) {
            Notes newPosition = new Notes(cur.getString(cur.getColumnIndex(NOTE_TEXT)),
                    cur.getString(cur.getColumnIndex(NOTE_PHOTO)),cur.getString(cur.getColumnIndex(NOTE_LAT)),
                    cur.getString(cur.getColumnIndex(NOTE_LONG)),cur.getString(cur.getColumnIndex(NOTE_VOICE)));
            int newpos = cur.getInt(cur.getColumnIndex(NOTE_ID));
            cur = mDb.query(true, PhotoNotes_TABLE, new String[]{NOTE_ID,NOTE_PHOTO,
                    NOTE_TEXT,NOTE_LAT,NOTE_LONG,NOTE_VOICE},
                    NOTE_PHOTO + "=?", new String[]{oldfile}, null, null, null, null);
            if(cur.moveToFirst()) {
                Notes oldPosition = new Notes(cur.getString(cur.getColumnIndex(NOTE_TEXT)),
                        cur.getString(cur.getColumnIndex(NOTE_PHOTO)),cur.getString(cur.getColumnIndex(NOTE_LAT)),
                        cur.getString(cur.getColumnIndex(NOTE_LONG)),cur.getString(cur.getColumnIndex(NOTE_VOICE)));
                int oldpos = cur.getInt(cur.getColumnIndex(NOTE_ID));
                ContentValues cv = new ContentValues();
                cv.put(NOTE_PHOTO, newPosition.getFilename());
                cv.put(NOTE_TEXT, newPosition.getText());
                cv.put(NOTE_LAT, newPosition.getlocLat());
                cv.put(NOTE_LONG, newPosition.getLocLong());
                cv.put(NOTE_VOICE, newPosition.getRecVoice());

                mDb.update(PhotoNotes_TABLE, cv, NOTE_ID + "=?", new String[]{String.valueOf(oldpos)});
                cv.put(NOTE_PHOTO, oldPosition.getFilename());
                cv.put(NOTE_TEXT, oldPosition.getText());
                cv.put(NOTE_LAT, oldPosition.getlocLat());
                cv.put(NOTE_LONG, oldPosition.getLocLong());
                cv.put(NOTE_VOICE, oldPosition.getRecVoice());

                mDb.update(PhotoNotes_TABLE, cv, NOTE_ID + "=?", new String[]{String.valueOf(newpos)});
            }
        }
    }
}