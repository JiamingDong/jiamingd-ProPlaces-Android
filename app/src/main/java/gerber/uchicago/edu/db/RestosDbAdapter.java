package gerber.uchicago.edu.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import gerber.uchicago.edu.Place;

import java.util.ArrayList;


/**
 * Created by Adam Gerber on 5/12/2014.
 * University of Chicago
 */
public class RestosDbAdapter {

    public static final String COL_ID = "_id";
    public static final String COL_FAV = "fav";
    public static final String COL_NAME = "name";
    public static final String COL_CITY = "city";
    public static final String COL_ADDRESS = "address";
    public static final String COL_PHONE = "phone";
    public static final String COL_YELP = "yelp";
    public static final String COL_IMG_URL = "img_url";
    public static final String COL_TIMESTAMP = "time_stamp";
    public static final String COL_CATEGORY = "category";

    public static final String SORT_NAME_ASC = COL_NAME + " ASC";
    public static final String SORT_FAV_DESC = COL_FAV + " DESC";
    public static final String SORT_NONE = null;

    public static final int INDEX_ID = 0;
    public static final int INDEX_FAV = INDEX_ID + 1;
    public static final int INDEX_NAME = INDEX_ID + 2;
    public static final int INDEX_CITY = INDEX_ID + 3;
    public static final int INDEX_ADDRESS = INDEX_ID + 4;
    public static final int INDEX_PHONE = INDEX_ID + 5;
    public static final int INDEX_YELP = INDEX_ID + 6;
    public static final int INDEX_IMG_URL = INDEX_ID + 7;
    public static final int INDEX_TIMESTAMP = INDEX_ID + 8;
    public static final int INDEX_CATEGORY = INDEX_ID + 9;

    private static final String TAG = "RestosDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "dba_favr";
    private static final String SQLITE_TABLE = "tbl_favr";
    private static final int DATABASE_VERSION = 19;

    private final Context mCtx;

    private static final String DATABASE_CREATE =
            "CREATE TABLE if not exists " + SQLITE_TABLE + " ( " +
                    COL_ID + " INTEGER PRIMARY KEY autoincrement, " +
                    COL_FAV + " INTEGER," +
                    COL_NAME + " TEXT not null, " +
                    COL_CITY + " TEXT not null, " +
                    COL_ADDRESS + " TEXT, " +
                    COL_PHONE + " TEXT, " +
                    COL_YELP + " TEXT, " +
                    COL_IMG_URL + " TEXT, " +
                    COL_TIMESTAMP + " INTEGER, " +
                    COL_CATEGORY + " TEXT" +
                    " ); ";


    public RestosDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    //open
    public void open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();

    }

    //close
    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }

    //CREATE
    public long createResto(Place resto) {

        ContentValues values = new ContentValues();

        values.put(COL_FAV, resto.getFavorite());
        values.put(COL_NAME, resto.getName());
        values.put(COL_CITY, resto.getCity());
        values.put(COL_ADDRESS, resto.getAddress());
        values.put(COL_PHONE, resto.getPhone());
        values.put(COL_YELP, resto.getYelp());
        values.put(COL_IMG_URL, resto.getImageUrl());
        values.put(COL_TIMESTAMP, resto.getTimeStamp());
        values.put(COL_CATEGORY, resto.getCategory());


        //returns the id of the newly created resto
        return mDb.insert(SQLITE_TABLE, null, values);

    }


    //READ
    public Place fetchRestoById(int id) {

        Cursor cursor = mDb.query(SQLITE_TABLE, new String[]{COL_ID, COL_FAV,
                        COL_NAME, COL_CITY, COL_ADDRESS, COL_PHONE, COL_YELP, COL_IMG_URL, COL_TIMESTAMP, COL_CATEGORY}, COL_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null
        );
        if (cursor != null)
            cursor.moveToFirst();

        Place resto = new Place(
                cursor.getInt(INDEX_ID),
                cursor.getInt(INDEX_FAV),
                cursor.getString(INDEX_NAME),
                cursor.getString(INDEX_CITY),
                cursor.getString(INDEX_ADDRESS),
                cursor.getString(INDEX_PHONE),
                cursor.getString(INDEX_YELP),
                cursor.getString(INDEX_IMG_URL),
                cursor.getLong(INDEX_TIMESTAMP),
                cursor.getString(INDEX_CATEGORY)
        );

        return resto;
    }

    public Cursor fetchAllRestos(String strSort) {

        Cursor mCursor = mDb.query(SQLITE_TABLE, new String[]{COL_ID, COL_FAV,
                        COL_NAME, COL_CITY, COL_ADDRESS, COL_PHONE, COL_YELP, COL_IMG_URL, COL_TIMESTAMP, COL_CATEGORY},
                null, null, null, null, strSort
        );


        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor fetchRestoByCategory(String category) {

        Cursor cursor = mDb.query(SQLITE_TABLE, new String[]{COL_ID, COL_FAV,
                        COL_NAME, COL_CITY, COL_ADDRESS, COL_PHONE, COL_YELP, COL_IMG_URL, COL_TIMESTAMP, COL_CATEGORY}, COL_CATEGORY + "=?",
                new String[]{category}, null, null, null, null
        );
        if (cursor != null)
            cursor.moveToFirst();

        return cursor;
    }

    public Cursor fetchRestoByName(String name) {

        Cursor cursor = mDb.query(SQLITE_TABLE, new String[]{COL_ID, COL_FAV,
                        COL_NAME, COL_CITY, COL_ADDRESS, COL_PHONE, COL_YELP, COL_IMG_URL, COL_TIMESTAMP, COL_CATEGORY}, COL_NAME + " LIKE ?",
                new String[]{"%"+name+"%"}, null, null, null, null
        );
        if (cursor != null)
            cursor.moveToFirst();

        return cursor;
    }

    //UPDATE
    public void updateResto(Place resto) {

        ContentValues values = new ContentValues();

        values.put(COL_FAV, resto.getFavorite());
        values.put(COL_NAME, resto.getName());
        values.put(COL_CITY, resto.getCity());
        values.put(COL_ADDRESS, resto.getAddress());
        values.put(COL_PHONE, resto.getPhone());
        values.put(COL_YELP, resto.getYelp());
        values.put(COL_IMG_URL, resto.getImageUrl());
        values.put(COL_TIMESTAMP, resto.getTimeStamp());
        values.put(COL_CATEGORY, resto.getCategory());


         mDb.update(SQLITE_TABLE, values,
                COL_ID + "=?", new String[]{String.valueOf(resto.getId())});

    }


    //DELETE
    public void deleteRestoById(int nId) {

        mDb.delete(SQLITE_TABLE, COL_ID + "=?", new String[]{String.valueOf(nId)});

    }


    public void deleteAllRestos() {

         mDb.delete(SQLITE_TABLE, null, null);


    }




    public void insertSomeRestos() {
        long timestamp = System.currentTimeMillis();

        ArrayList<Place> restos = new ArrayList<Place>();
        restos.add( new Place(0, "The Gage", "Chicago", "24 S. Michigan Ave", "3123724243", "http://www.yelp.com/biz/the-gage-chicago", "http://3.bp.blogspot.com/_fr_ZFOsr3a8/TBoSrzXzPWI/AAAAAAAAHNQ/dcm-ZfTSGgU/s1600/Gage+exterior.jpg", timestamp, "Beauty and Spas"));
        restos.add( new Place(1, "Stetsons Modern Steak + Sushi", "Chicago", "151 E Wacker Dr", "3122394491", "http://www.yelp.com/biz/stetsons-modern-steak-sushi-chicago-2", "http://www.opentable.com/img/pdThumbnails/1729.jpg", timestamp, "Health and Medical"));
        restos.add( new Place(0, "Cai", "Chicago", "2100 S Archer Ave", "3123266888", "http://www.yelp.com/biz/cai-chicago", "http://media.timeout.com/images/100903221/60/45/image.jpg", timestamp, "Food"));

        for (Place resto : restos) {
            createResto(resto);
        }

    }


    //static inner class
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.w(TAG, DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE);
            onCreate(db);
        }
    }

}