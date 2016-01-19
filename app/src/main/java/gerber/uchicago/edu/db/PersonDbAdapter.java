package gerber.uchicago.edu.db;

/**
 * Created by Jiaming on 2015/6/8.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import gerber.uchicago.edu.Person;

public class PersonDbAdapter {

    public static final String COL_ID = "_id";
    public static final String COL_NAME = "name";
    public static final String COL_CITY = "city";
    public static final String COL_ADDRESS = "address";
    public static final String COL_PHONE = "phone";
    public static final String COL_EMAIL = "email";
    public static final String COL_IMG_URL = "img_url";
    public static final String COL_TIMESTAMP = "time_stamp";
    public static final String COL_CATEGORY = "category";
    public static final String COL_ALTPHONE = "alt_phone";

    public static final String SORT_NAME_ASC = COL_NAME + " ASC";
    public static final String SORT_NONE = null;


    public static final int INDEX_ID = 0;
    public static final int INDEX_EMAIL = INDEX_ID + 1;
    public static final int INDEX_NAME = INDEX_ID + 2;
    public static final int INDEX_CITY = INDEX_ID + 3;
    public static final int INDEX_ADDRESS = INDEX_ID + 4;
    public static final int INDEX_PHONE = INDEX_ID + 5;
    public static final int INDEX_ALTPHONE = INDEX_ID + 6;
    public static final int INDEX_IMG_URL = INDEX_ID + 7;
    public static final int INDEX_TIEMSTAMP = INDEX_ID + 8;
    public static final int INDEX_CATE = INDEX_ID + 9;


    private static final String TAG = "personDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "dba_favr";
    private static final String SQLITE_TABLE = "tbl_person";
    private static final int DATABASE_VERSION = 19;

    private final Context mCtx;

    private static final String DATABASE_CREATE =
            "CREATE TABLE if not exists " + SQLITE_TABLE + " ( " +
                    COL_ID + " INTEGER PRIMARY KEY autoincrement, " +
                    COL_EMAIL + " TEXT, " +
                    COL_NAME + " TEXT not null, " +
                    COL_CITY + " TEXT not null, " +
                    COL_ADDRESS + " TEXT, " +
                    COL_PHONE + " TEXT, " +
                    COL_ALTPHONE + " TEXT, " +
                    COL_IMG_URL + " TEXT, " +
                    COL_TIMESTAMP + " INTEGER, " +
                    COL_CATEGORY + " TEXT" +
                    " ); "
            ;


    public PersonDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    //open
    public void open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        mDb.execSQL(DATABASE_CREATE);
    }

    //close
    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }



    //CREATE
    public long createResto(Person person) {

        ContentValues values = new ContentValues();

        values.put(COL_EMAIL, person.getEmail());
        values.put(COL_NAME, person.getName());
        values.put(COL_CITY, person.getCity());
        values.put(COL_ADDRESS, person.getAddress());
        values.put(COL_PHONE, person.getPhone());
        values.put(COL_ALTPHONE, person.getAltPhone());
        values.put(COL_IMG_URL, person.getImageUrl());
        values.put(COL_TIMESTAMP, person.getTimeStamp());
        values.put(COL_CATEGORY, person.getCategory());


        //returns the id of the newly created resto
        return mDb.insert(SQLITE_TABLE, null, values);
    }


    //READ
    public Person fetchRestoById(int id) {

        Cursor cursor = mDb.query(SQLITE_TABLE, new String[]{COL_ID, COL_EMAIL,
                        COL_NAME, COL_CITY, COL_ADDRESS, COL_PHONE, COL_ALTPHONE, COL_IMG_URL, COL_TIMESTAMP, COL_CATEGORY}, COL_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null
        );
        if (cursor != null)
            cursor.moveToFirst();

        Person person = new Person(
                cursor.getInt(INDEX_ID),
                cursor.getString(INDEX_NAME),
                cursor.getString(INDEX_CITY),
                cursor.getString(INDEX_ADDRESS),
                cursor.getString(INDEX_PHONE),
                cursor.getString(INDEX_ALTPHONE),
                cursor.getString(INDEX_IMG_URL),
                cursor.getLong(INDEX_TIEMSTAMP),
                cursor.getString(INDEX_CATE),
                cursor.getString(INDEX_EMAIL)
        );

        return person;
    }

    public Cursor fetchRestoByCategory(String category) {

        Cursor cursor = mDb.query(SQLITE_TABLE, new String[]{COL_ID, COL_EMAIL,
                        COL_NAME, COL_CITY, COL_ADDRESS, COL_PHONE, COL_ALTPHONE, COL_IMG_URL, COL_TIMESTAMP, COL_CATEGORY}, COL_CATEGORY + "=?",
                new String[]{category}, null, null, null, null
        );
        if (cursor != null)
            cursor.moveToFirst();

        return cursor;
    }

    public Cursor fetchPresonByName(String name) {

        Cursor cursor = mDb.query(SQLITE_TABLE, new String[]{COL_ID, COL_EMAIL,
                        COL_NAME, COL_CITY, COL_ADDRESS, COL_PHONE, COL_ALTPHONE, COL_IMG_URL, COL_TIMESTAMP, COL_CATEGORY}, COL_NAME + " LIKE ?",
                new String[]{"%"+name+"%"}, null, null, null, null
        );
        if (cursor != null)
            cursor.moveToFirst();

        return cursor;
    }


    public Cursor fetchAllRestos(String strSort) {

        Cursor mCursor = mDb.query(SQLITE_TABLE, new String[]{
                        COL_ID, COL_EMAIL, COL_NAME, COL_CITY, COL_ADDRESS, COL_PHONE, COL_ALTPHONE, COL_IMG_URL, COL_TIMESTAMP, COL_CATEGORY
                },
                null, null, null, null, strSort
        );


        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }


    //UPDATE
    public void updateResto(Person person) {

        ContentValues values = new ContentValues();

        values.put(COL_EMAIL, person.getEmail());
        values.put(COL_NAME, person.getName());
        values.put(COL_CITY, person.getCity());
        values.put(COL_ADDRESS, person.getAddress());
        values.put(COL_PHONE, person.getPhone());
        values.put(COL_ALTPHONE, person.getAltPhone());
        values.put(COL_IMG_URL, person.getImageUrl());
        values.put(COL_TIMESTAMP, person.getTimeStamp());
        values.put(COL_CATEGORY, person.getCategory());

        mDb.update(SQLITE_TABLE, values,
                COL_ID + "=?", new String[]{String.valueOf(person.getId())});

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

        ArrayList<Person> persons = new ArrayList<Person>();
        persons.add( new Person(0, "The Gage", "Chicago", "24 S. Michigan Ave", "3123724243", "312132131", "http://3.bp.blogspot.com/_fr_ZFOsr3a8/TBoSrzXzPWI/AAAAAAAAHNQ/dcm-ZfTSGgU/s1600/Gage+exterior.jpg",timestamp,"arts", "bof@uchicago.edu"));
        persons.add( new Person(1, "Stetsons Modern Steak + Sushi", "Chicago", "151 E Wacker Dr", "3122394491", "312132131", "http://www.opentable.com/img/pdThumbnails/1729.jpg",timestamp,"sports", "bof@uchicago.edu"));
        persons.add( new Person(0, "Cai", "Chicago", "2100 S Archer Ave", "3123266888", "3123266888", "http://media.timeout.com/images/100903221/60/45/image.jpg",timestamp,"travel", "bof@chicago.edu"));

        for (Person person : persons) {
            //createResto(person);
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