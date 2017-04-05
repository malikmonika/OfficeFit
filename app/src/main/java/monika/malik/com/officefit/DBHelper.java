package monika.malik.com.officefit;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper {
    public static final String NOTE_ID = "id";
    public static final String NOTE_PROPERTY= "property";
    public static final String NOTE_VALUE = "value";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "OFFICEFIT.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE = "fit";

    private static final String CREATE_TABLE = "create table "
            + TABLE + " (" + NOTE_ID
            + " integer primary key autoincrement, " + NOTE_PROPERTY
            + " text not null, "+ NOTE_VALUE + " text not null )";

    private final Context mCtx;

    private class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE);
            db.execSQL(CREATE_TABLE);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE);
            onCreate(db);
        }
    }

    public void Reset() {
        mDbHelper.onUpgrade(this.mDb, 1, 1);
    }

    public DBHelper(Context ctx) {
        mCtx = ctx;
        mDbHelper = new DatabaseHelper(mCtx);
    }

    public void close() {
        mDbHelper.close();
    }

    public void updateorInsertValue(String key,String value) throws SQLException {
        mDb = mDbHelper.getWritableDatabase();
        String[] columnArgs = {NOTE_PROPERTY,NOTE_VALUE};
        String query = NOTE_PROPERTY+"=?";
        String[] args = {key};
        Cursor cursor = mDb.query(TABLE,
                columnArgs,
                query,
                args,
                null,
                null,
                null);
        if(!cursor.moveToFirst())
        {
            ContentValues cv = new ContentValues();
            cv.put(NOTE_PROPERTY, key);
            cv.put(NOTE_VALUE, value);
            mDb.insert(TABLE, null, cv);
        }
        else
        {
            ContentValues cv = new ContentValues();
            cv.put(NOTE_PROPERTY, key);
            cv.put(NOTE_VALUE, value);
            mDb.update(TABLE, cv, NOTE_PROPERTY + "=?", new String[]{key});

        }
        mDb.close();
    }

    public String retriveValue(String key) throws SQLException {
        mDb = mDbHelper.getWritableDatabase();
        Cursor cur = mDb.query(true, TABLE, new String[]{NOTE_PROPERTY,
                NOTE_VALUE},NOTE_PROPERTY+"=?", new String[]{key}, null, null, null, null);
        String retVal = null;
        if(cur.moveToFirst())
        {
            retVal = cur.getString(cur.getColumnIndex(NOTE_VALUE));
        }
        cur.close();
        mDb.close();
        return retVal;
    }

    public void removeValue(String key)
    {
        mDb = mDbHelper.getWritableDatabase();
        Cursor cur = mDb.query(true, TABLE, new String[]{NOTE_PROPERTY,
                NOTE_VALUE}, NOTE_PROPERTY+"=?", new String[]{key}, null, null, null, null);
        if(!cur.moveToFirst()) {
            cur.close();
            return;
        }
        mDb.delete(TABLE,NOTE_PROPERTY+"='"+key+"'",null);
        cur.close();
        mDb.close();
    }

    public boolean firstBoot()
    {
        mDb = mDbHelper.getReadableDatabase();
        Cursor cur = mDb.query(true, TABLE, new String[]{NOTE_PROPERTY,
                NOTE_VALUE}, NOTE_PROPERTY+"=?", new String[]{"boot"}, null, null, null, null);
        if(!cur.moveToFirst()) {
            cur.close();
            return true;
        }
        cur.close();
        mDb.close();
        return false;
    }

}
