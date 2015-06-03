package dlv.niva.mx.koalapacer.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by daniellujanvillarreal on 3/27/15.
 */
public class CrawlerDBHelper extends SQLiteOpenHelper {

    private static final String TEXT_TYPE = "TEXT";
    private static final String INT_TYPE = "INT";
    private static final String NUM_TYPE = "NUM";
    private static final String REAL_TYPE = "REAL";
    private final String TAG = "CrawlerDBHelper";

    private static final String COMMA_SEP = ",";
//    private static final String SQL_CREATE_CLICK = "CREATE TABLE IF NOT EXISTS " + CrawlerDB.ClickDB.TABLE_NAME
//            + " (" + CrawlerDB.ClickDB.COLUMN_NAME_ID + " INTEGER PRIMARY KEY, " +
//            CrawlerDB.ClickDB.COLUMN_NAME_DOMAIN +" "+ TEXT_TYPE + COMMA_SEP +" "+
//            CrawlerDB.ClickDB.COLUMN_NAME_NETWORK +" "+ TEXT_TYPE + COMMA_SEP +" "+
//            CrawlerDB.ClickDB.COLUMN_NAME_MEGABYTES +" "+ INT_TYPE + COMMA_SEP +" "+
//            CrawlerDB.ClickDB.COLUMN_NAME_IMAGE +" "+ TEXT_TYPE +" "+ COMMA_SEP +" "+
//            CrawlerDB.ClickDB.COLUMN_NAME_TIMESTAMP +" "+ NUM_TYPE +" "+
//            " );";
//    private static final String SQL_CREATE_REDIRECT = "CREATE TABLE IF NOT EXISTS " + CrawlerDB.RedirectDB.TABLE_NAME
//            + " (" + CrawlerDB.RedirectDB.COLUMN_NAME_ID + " INTEGER PRIMARY KEY, " +
//            CrawlerDB.RedirectDB.COLUMN_NAME_CLICKID +" "+ INT_TYPE + COMMA_SEP +" "+
//            CrawlerDB.RedirectDB.COLUMN_NAME_DOMAIN +" "+ TEXT_TYPE + COMMA_SEP +" "+
//            CrawlerDB.RedirectDB.COLUMN_NAME_URL +" "+ TEXT_TYPE + COMMA_SEP +" "+
//            CrawlerDB.RedirectDB.COLUMN_NAME_TIMESTAMP +" "+ NUM_TYPE +" "+ COMMA_SEP +" "+
//            "FOREIGN KEY("+ CrawlerDB.RedirectDB.COLUMN_NAME_CLICKID+") REFERENCES "
//                + CrawlerDB.ClickDB.TABLE_NAME+"("+ CrawlerDB.ClickDB.COLUMN_NAME_ID+")"+
//            " );";
    private static final String SQL_CREATE_LANDING = "CREATE TABLE IF NOT EXISTS " + CrawlerDB.LandingDB.TABLE_NAME
        + " (" + CrawlerDB.LandingDB.COLUMN_NAME_ID + " INTEGER PRIMARY KEY, " +
        CrawlerDB.LandingDB.COLUMN_NAME_URL +" "+ TEXT_TYPE + COMMA_SEP +" "+
        CrawlerDB.LandingDB.COLUMN_NAME_IDCONTROL +" "+ TEXT_TYPE + COMMA_SEP +" "+
        CrawlerDB.LandingDB.COLUMN_NAME_NETWORK +" "+ TEXT_TYPE + " "+
        " );";

    private static final String SQL_CREATE_LANDINGPARSED = "CREATE TABLE IF NOT EXISTS " + CrawlerDB.LandingParsedDB.TABLE_NAME
            + " (" + CrawlerDB.LandingParsedDB.COLUMN_NAME_ID + " INTEGER PRIMARY KEY, " +
            CrawlerDB.LandingParsedDB.COLUMN_NAME_TIME +" "+ INT_TYPE + COMMA_SEP +" "+
            CrawlerDB.LandingParsedDB.COLUMN_NAME_IMAGETYPE +" "+ TEXT_TYPE + COMMA_SEP +" "+
            CrawlerDB.LandingParsedDB.COLUMN_NAME_IDCONTROL +" "+ INT_TYPE + COMMA_SEP +" "+
            "FOREIGN KEY("+ CrawlerDB.LandingParsedDB.COLUMN_NAME_IDCONTROL+") REFERENCES "
            + CrawlerDB.LandingDB.TABLE_NAME+"("+ CrawlerDB.LandingDB.COLUMN_NAME_IDCONTROL+")"+
            " );";

    private static final String SQL_CREATE_LANDINGCOLORS = "CREATE TABLE IF NOT EXISTS " + CrawlerDB.LandingColorsDB.TABLE_NAME
            + " (" + CrawlerDB.LandingColorsDB.COLUMN_NAME_ID + " INTEGER PRIMARY KEY, " +
            CrawlerDB.LandingColorsDB.COLUMN_NAME_IDCONTROL +" "+ INT_TYPE + COMMA_SEP +" "+
            CrawlerDB.LandingColorsDB.COLUMN_NAME_QUANTITY +" "+ INT_TYPE + COMMA_SEP +" "+
            CrawlerDB.LandingColorsDB.COLUMN_NAME_NUMBER +" "+ INT_TYPE + COMMA_SEP +" "+
            "FOREIGN KEY("+ CrawlerDB.LandingColorsDB.COLUMN_NAME_IDCONTROL+") REFERENCES "
            + CrawlerDB.LandingDB.TABLE_NAME+"("+ CrawlerDB.LandingDB.COLUMN_NAME_IDCONTROL+")"+
            " );";

//    private static final String SQL_DELETE_CLICK =
//            "DROP TABLE IF EXISTS " + CrawlerDB.ClickDB.TABLE_NAME;
//    private static final String SQL_DELETE_REDIRECT =
//            "DROP TABLE IF EXISTS " + CrawlerDB.RedirectDB.TABLE_NAME;
    private static final String SQL_DELETE_LANDING =
            "DROP TABLE IF EXISTS " + CrawlerDB.LandingDB.TABLE_NAME;
    private static final String SQL_DELETE_LANDINGPARSED =
            "DROP TABLE IF EXISTS " + CrawlerDB.LandingParsedDB.TABLE_NAME;
    private static final String SQL_DELETE_LANDINGCOLORS =
            "DROP TABLE IF EXISTS " + CrawlerDB.LandingColorsDB.TABLE_NAME;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Pacer.db";

    public CrawlerDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "starting creation");
//        db.execSQL(SQL_CREATE_CLICK);
        db.execSQL(SQL_CREATE_LANDING);
        db.execSQL(SQL_CREATE_LANDINGPARSED);
        db.execSQL(SQL_CREATE_LANDINGCOLORS);
//        db.execSQL(SQL_CREATE_DOMAIN);
        Log.d(TAG, "finished creation");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL(SQL_DELETE_CLICK);
        db.execSQL(SQL_DELETE_LANDING);
        db.execSQL(SQL_DELETE_LANDINGPARSED);
        db.execSQL(SQL_DELETE_LANDINGCOLORS);
//        db.execSQL(SQL_DELETE_DOMAIN);
        onCreate(db);
    }
}
