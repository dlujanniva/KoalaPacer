package dlv.niva.mx.koalapacer;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import dlv.niva.mx.koalapacer.db.CrawlerDB;

/**
 * Created by daniellujanvillarreal on 5/29/15.
 */
public class LandingParsed {

    private int mIdControl;
    private long mTime;
    private String mImgType;

    public LandingParsed(Cursor cursor){
        this.mIdControl = cursor.getInt(
                cursor.getColumnIndexOrThrow(CrawlerDB.LandingParsedDB.COLUMN_NAME_IDCONTROL)
        );
        this.mTime = cursor.getInt(
                cursor.getColumnIndexOrThrow(CrawlerDB.LandingParsedDB.COLUMN_NAME_TIME)
        );
        this.mImgType = cursor.getString(
                cursor.getColumnIndexOrThrow(CrawlerDB.LandingParsedDB.COLUMN_NAME_IMAGETYPE)
        );
    }

    public int getIdControl(){
        return mIdControl;
    }

    public JSONObject toJSON() throws JSONException{
        JSONObject landing = new JSONObject();
        landing.put("id", mIdControl);
        landing.put("loadTime", mTime);
        landing.put("imageType", mImgType);
        return  landing;
    }
}
