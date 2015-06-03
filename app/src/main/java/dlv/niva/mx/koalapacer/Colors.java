package dlv.niva.mx.koalapacer;

import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.opencv.core.Scalar;

import java.util.ArrayList;

import dlv.niva.mx.koalapacer.db.CrawlerDB;

/**
 * Created by daniellujanvillarreal on 6/2/15.
 */
public class Colors {

    private ArrayList<Color> mColors;
    private int mIdControl;

    public Colors(Scalar[] colors, int idControl){
        mColors = new ArrayList<Color>();
        for(int i = 0; i < colors.length; i++){
            mColors.add(new Color(i, colors[i]));
        }
        mIdControl = idControl;
    }

    public void setQuantities(float[] quantities){
        for(int i = 0; i< quantities.length; i++){
            mColors.get(i).setQuantity((int) quantities[i]);
        }
    }

    public Colors(){
        mColors = new ArrayList<Color>();
        mIdControl = -1;
    }
    public void add(Cursor cursor){
        mColors.add(new Color(cursor));
    }

    public JSONArray toJSON() throws JSONException{
        JSONArray colors = new JSONArray();
        for(int i = 0; i< mColors.size(); i++){
            colors.put(mColors.get(i).getId(), mColors.get(i).getQuantity());
        }
        return colors;
    }

    public int size(){
        return mColors.size();
    }

    public Scalar getColor(int i){
        return mColors.get(i).getColor();
    }

    public int getQuantity(int i){
        return mColors.get(i).getQuantity();
    }

    public int getNumber(int i){
        return mColors.get(i).getId();
    }

    public int getIdControl() {
        return mIdControl;
    }

    public void setIdControl(int mIdControl) {
        this.mIdControl = mIdControl;
    }

    private class Color{
        private int mQuantity;
        private int mId;
        private Scalar mColor;

        public Color(int id, Scalar color){
            mId = id;
            mColor = color;
        }

        public Color(Cursor cursor){
            mId = cursor.getInt(
                    cursor.getColumnIndexOrThrow(CrawlerDB.LandingColorsDB.COLUMN_NAME_NUMBER)
            );
            mQuantity = cursor.getInt(
                    cursor.getColumnIndexOrThrow(CrawlerDB.LandingColorsDB.COLUMN_NAME_QUANTITY)
            );
        }

        public void setQuantity(int quantity) {
            this.mQuantity = quantity;
        }

        public Scalar getColor() {
            return mColor;
        }
        public int getQuantity() {
            return mQuantity;
        }

        public int getId() {
            return mId;
        }
    }

}
