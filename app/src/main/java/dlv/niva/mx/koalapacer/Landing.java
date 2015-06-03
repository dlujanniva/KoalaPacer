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
public class Landing {

    private String mUrl;
    private int mNetwork;
    private int mIdControl;
    private long mStart;
    private long mEnd;
    private long mTime;
    private String mImageType;
    private Bitmap mScreenshot;
    private int errores;

    public Landing(){
        mUrl = "";
        mNetwork = ConnectivityManager.TYPE_WIFI;
        mIdControl = -1;
        errores = 0;
        mImageType = "";
    }

    public Landing(String url, String network, int id){
        mUrl = url;
        if(network.equals("WIFI")){
            mNetwork = ConnectivityManager.TYPE_WIFI;
        }else{
            mNetwork = ConnectivityManager.TYPE_MOBILE;
        }
        mIdControl = id;
        errores = 0;
        mImageType = "";
    }

    public Landing(JSONObject dominio){
        try {
            mUrl = dominio.getString("url");
            if(dominio.getString("network").equals("WIFI")){
                mNetwork = ConnectivityManager.TYPE_WIFI;
            }else{
                mNetwork = ConnectivityManager.TYPE_MOBILE;
            }

            mIdControl = dominio.getInt("id");
            errores = 0;
            mImageType = "";
        }catch(JSONException e){
            Log.e("Landing constructor", "Error creating Landing object", e);
        }
    }

    public String getUrl() {
        return mUrl;
    }

    public String getNetwork() {
        if(mNetwork == ConnectivityManager.TYPE_WIFI){
            return "WIFI";
        }else{
            return "WAP";
        }
    }

    public int getId() {
        return mIdControl;
    }

    public long getTime() {
        long time = (mEnd-mStart)/1000;
        if(time != mTime){
            return time;
        }
        return mTime;
    }

    public void setStart(long start){
        mStart = start;
    }

    public void setEnd(long end){
        mTime = (end-mStart)/1000;
        mEnd = end;
    }

    public int getErrores() {
        return errores;
    }

    public void setErrores(int errores) {
        this.errores = errores;
    }

    public void increaseErrores() {
        this.errores = errores+1;
    }

    public String getImageType() {
        return mImageType;
    }

    public void setImageType(String mImageType) {
        this.mImageType = mImageType;
    }
}
