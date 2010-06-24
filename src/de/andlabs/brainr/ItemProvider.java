package de.andlabs.brainr;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class ItemProvider extends ContentProvider {

	private static final String TAG = "VokabelHEFT";
	private static final int ITEM = 1;
    private static final int ITEMS = 2;
    private static final int TRANSLATIONS = 3;
	
	private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, "brainr.db", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE items ("
                    + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "text TEXT,"
                    + "sound TEXT,"
                    + "language TEXT"
                    + ");");
            db.execSQL("CREATE TABLE translations ("
                    + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "item_id INTEGER,"
                    + "to_id INTEGER"
                    + ");");
            db.execSQL("INSERT INTO items values(1, 'hallo', '/sdcard/brainr/', 'de');");
            db.execSQL("INSERT INTO items values(2, 'nihao', '/sdcard/brainr/', 'ja');");
            db.execSQL("INSERT INTO items values(3, 'hello', '/sdcard/brainr/', 'en');");
            db.execSQL("INSERT INTO items values(4, 'Danke', '/sdcard/brainr/', 'de');");
            db.execSQL("INSERT INTO items values(5, 'Thank You', '/sdcard/brainr/', 'en');");
            db.execSQL("INSERT INTO items values(6, 'Toll', '/sdcard/brainr/', 'de');");
            db.execSQL("INSERT INTO items values(7, 'Great', '/sdcard/brainr/', 'en');");
            db.execSQL("INSERT INTO translations values(1, 1, 2);");
            db.execSQL("INSERT INTO translations values(2, 1, 3);");
            db.execSQL("INSERT INTO translations values(3, 4, 5);");
            db.execSQL("INSERT INTO translations values(4, 6, 7);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS items");
            db.execSQL("DROP TABLE IF EXISTS translations");
            onCreate(db);
        }
    }

    private static UriMatcher uriMatcher;

    private DatabaseHelper mDBHelper;

	public ItemProvider() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onCreate() {
	    mDBHelper = new DatabaseHelper(getContext());
		return false;
	}

	@Override
	public String getType(Uri arg0) {
		return Item.TYPE;
	}

	// CRUD
	
	@Override
	public Uri insert(Uri uri, ContentValues cv) {
	    if (uriMatcher.match(uri) != ITEMS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;
        if (cv != null) {
            values = new ContentValues(cv);
        } else {
            values = new ContentValues();
        }

        if (values.containsKey(Item.TEXT) == false) {
            values.put(Item.TEXT, "");
        }

        long rowId = mDBHelper.getWritableDatabase().insert("items", null, values);
        if (rowId > 0) {
            Uri itemUri = ContentUris.withAppendedId(Item.URI, rowId);
            getContext().getContentResolver().notifyChange(itemUri, null);
            return itemUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public Cursor query(Uri uri, String[] selection, String arg2, String[] arg3, String arg4) {
	    Log.d(TAG, "Hallo");
	    
	    Cursor c;
	    switch (uriMatcher.match(uri)) {
        case ITEM:
            c =  mDBHelper.getReadableDatabase().query("items", selection, "_id="+uri.getLastPathSegment(), null, null, null, null);
            break;
        case ITEMS:
            c =  mDBHelper.getReadableDatabase().query("items", selection, null, null, null, null, "_id DESC");
            break;
        case TRANSLATIONS:
            c = mDBHelper.getReadableDatabase().query("translations join items on translations.to_id=items._id", 
                                               selection, "item_id="+uri.getPathSegments().get(1), null, null, null, null);
            break; 
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
	    c.setNotificationUri(getContext().getContentResolver(), uri);
	    return c;
	}

	@Override
	public int update(Uri uri, ContentValues cv, String arg2, String[] arg3) {
	    SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int count;
        switch (uriMatcher.match(uri)) {
        case ITEM:
            count = db.update("items", cv, "_id=" + uri.getLastPathSegment(), null);
            break;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
	    SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int count;
        switch (uriMatcher.match(uri)) {

        case ITEM:
            count = db.delete("items", "_id=" + uri.getLastPathSegment()
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
	}
	
	static {
	    uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	    uriMatcher.addURI(Item.AUTHORITY, "items", ITEMS);
	    uriMatcher.addURI(Item.AUTHORITY, "items/#", ITEM);
	    uriMatcher.addURI(Item.AUTHORITY, "items/#/translations", TRANSLATIONS);
	}
	
	
	

}
