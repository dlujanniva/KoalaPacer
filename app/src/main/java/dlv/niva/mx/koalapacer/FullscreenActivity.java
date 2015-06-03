package dlv.niva.mx.koalapacer;

import dlv.niva.mx.koalapacer.db.CrawlerDB;
import dlv.niva.mx.koalapacer.db.CrawlerDBHelper;
import dlv.niva.mx.koalapacer.util.SystemUiHider;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *t
 * @see SystemUiHider
 */
public class FullscreenActivity extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    private WebView webView = null;
    private long startLoad;
    private long endLoad;
    private ConnectivityManager connectivityManager;
    private String bluethootMacAddress = null;
    private long lastInstruction;
    private SQLiteDatabase db;
    CrawlerDBHelper mDbHelper;
    private int displayWidth;
    private int displayHeight;

    //landing info
    private String landingUrl = null;
    private String landingNetwork = null;
    private int landingId;
    private final String serverWS = "http://api.robokoala.com/";
    private ArrayList<Landing> alLandings;
    private Landing landing;

    private final String ERROR_CLASE_1 = "1"; //puede seguir corriendo
    private final String ERROR_CLASE_2 = "2"; // volver a pedir landings
    private final String ERROR_CLASE_3 = "3"; // volver a visitar landing
    private final String ERROR_CLASE_4 = "4"; // heartbeat error
    private final String ERROR_CLASE_5 = "5"; // respondToServer error

    private final int mHistSizeNum = 25;
    private final Scalar[] mColorsHue = new Scalar[] {
            new Scalar(255, 0, 0, 255),
            new Scalar(255, 60, 0, 255),
            new Scalar(255, 120, 0, 255),
            new Scalar(255, 180, 0, 255),
            new Scalar(255, 240, 0, 255),
            new Scalar(215, 213, 0, 255),
            new Scalar(150, 255, 0, 255),
            new Scalar(85, 255, 0, 255),
            new Scalar(20, 255, 0, 255),
            new Scalar(0, 255, 30, 255),
            new Scalar(0, 255, 85, 255),
            new Scalar(0, 255, 150, 255),
            new Scalar(0, 255, 215, 255),
            new Scalar(0, 234, 255, 255),
            new Scalar(0, 170, 255, 255),
            new Scalar(0, 120, 255, 255),
            new Scalar(0, 60, 255, 255),
            new Scalar(0, 0, 255, 255),
            new Scalar(64, 0, 255, 255),
            new Scalar(120, 0, 255, 255),
            new Scalar(180, 0, 255, 255),
            new Scalar(255, 0, 255, 255),
            new Scalar(255, 0, 215, 255),
            new Scalar(255, 0, 85, 255),
            new Scalar(255, 0, 0, 255)
    };
    private final Scalar[] mColorsRGB = new Scalar[] {
            new Scalar(200, 0, 0, 255), new Scalar(0, 200, 0, 255), new Scalar(0, 0, 200, 255) };
    private Colors mColors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);
        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.webView);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_2, this, mOpenCVCallBack))
        {
            Log.e("MainActivity", "Cannot connect to OpenCV Manager");
            Toast.makeText(getApplicationContext()
                    , "Couldn't load OpenCV!!!!",Toast.LENGTH_LONG).show();

        }
    }

    /**
     * OpenCV Callback for Initialization
     */
    private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i("BaseLoaderCallback", "OpenCV loaded successfully");
                    Toast.makeText(getApplicationContext()
                            , "Successfull load OpenCV",Toast.LENGTH_LONG).show();
                    // Create and set View
                    // Upon interacting with UI controls, delay any scheduled hide()
                    // operations to prevent the jarring behavior of controls going away
                    // while interacting with the UI.
                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    displayWidth = size.x;
                    displayHeight = size.y;

                    display = null;
                    size = null;

                    landing = new Landing();
                    mColors = new Colors(mColorsHue, landing.getId());

                    connectivityManager =
                            (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                    //deprecated in API 21 -- nuestro target es 19
                    changeNetworkPreference(ConnectivityManager.TYPE_WIFI);
                    new SetupDB().execute();
                } break;
                default: {
                    super.onManagerConnected(status);
                } break;

            }
        }
    };


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    /**
     * Convierte InputStream a String
     * @param inputStream del Server
     * @return String of inputStream
     * @throws java.io.IOException
     */
    public static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
        return result;

    }

    /**
     * PREPARACIÓN
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void prepareWebView(){
        if(webView != null){
            webView.clearCache(true);
            webView.clearHistory();
            webView.freeMemory();
            webView.removeJavascriptInterface("HtmlViewer");
        }

        webView = (WebView) findViewById(R.id.webView);

        webView.setWebChromeClient(
                new WebChromeClient() {
//                    public void onProgressChanged(WebView view, int progress) {
//                        // Activities and WebViews measure progress with different scales.
//                        // The progress meter will automatically disappear when we reach 100%
//                        activity.setProgress(progress * 1000);
//                    }

                    @Override
                    public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                        Log.i("onJsConfirm", "JS confirm detectado ");
                        result.confirm();
                        return true;
                    }

                    @Override
                    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                        Log.i("onJsAlert", "JS prompt detectado ");
                        result.confirm();
                        return true;
                    }

                    @Override
                    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                        Log.i("onJsAlert", "OnCreateWindow detectado ");
                        return true;
                    }

                    @Override
                    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                        Log.i("onJsAlert", "JS alert detectado ");
                        result.confirm();
                        return true;
                    }

                    @Override
                    public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
                        Log.i("onJsBeforeUnload", "JS beforeUnload detectado ");
                        result.confirm();
                        return true;
                    }

                    @Override
                    public boolean onConsoleMessage(@NonNull ConsoleMessage message) {
                        Log.e("onConsoleMessage", "Source::" + message.sourceId()
                                + " Level::" + message.messageLevel()
                                + " Message::" + message.message());
                        return super.onConsoleMessage(message);
                    }
                }
        );

        webView.setPersistentDrawingCache(ViewGroup.PERSISTENT_NO_CACHE);

        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        webView.getSettings().setJavaScriptEnabled(true);
        //JSInterface que sirve de puente entre WebView y Android
        webView.addJavascriptInterface(new JSInterface(this), "HtmlViewer");

        //WebViewClient con listeners para comunicación entre WebView y Android
        webView.setWebViewClient(
                new WebViewClient() {
                    @Override
                    public void onScaleChanged(WebView view, float oldScale, float newScale) {
                        Log.d("WebView", "scale :: " + newScale);
//                        wvScale = newScale;
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                        click.addRatio();
                        Log.i("webView", "PAGE STARTED");
                        landing.setStart(System.currentTimeMillis());
//                                click.getRatioStartedForwardedLoaded());
//                        favicon.recycle();
                    }

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                        //let my WebView load the page
//                        ratioStartedForwardedLoaded = (ratioStartedForwardedLoaded == 0)? 0 : ratioStartedForwardedLoaded - 1;
//                        Log.i(MA+"webView","302 - ratio:: "+ratioStartedForwardedLoaded);
//                        redirects.add(url);
//                        if(!isOnEasyList(url)){
//                            Log.i(MA,"cancel redirect :: "+url);
//                            return true;
//                        }else {
//                        if(url.contains("http://www.minternet.telcel.com/balance/?")){
//                            changeNetworkPreference(ConnectivityManager.TYPE_WIFI);
//                            return true;
//                        }else {
                        Log.i("webView", "302 - redirect");
//                            click.subtractRatio();
//                            Log.i("webView", "302 - ratio:: " + click.getRatioStartedForwardedLoaded());
//                        redirects.add(url);
//                        if(isOnEasyList(url)){
//                            Log.i(MA, "webView :: redirect added to banners");
//
//                            alBanners[0].add(url);
//                            alBanners[1].add(null);
//                            alBanners[2].add(click.getDomainUrl());
//                            alBanners[3].add("");
//                        }

//                            new DbAddRedirect().execute(new Redirect(click.getDomainUrl(), url, System.currentTimeMillis()));
                        return false;
                    }
//                        }


                    @Override
                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//                        Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
                        Log.i("MainActivity-Error", failingUrl + "::" + description);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        landing.setEnd(System.currentTimeMillis());
//                        click.subtractRatio();
//
//                        new TakeScreenshot().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        view.loadUrl("javascript:HtmlViewer.html" +
                                "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>','" + url + "');");
                        Log.i("webView", "onPageFinished :: page finished loading..." + url);
//                        Log.i("webView", "ratio::" + click.getRatioStartedForwardedLoaded());
                    }

                    @Override
                    public void onLoadResource(WebView view, String url) {
//                        Log.i("WebView", "onLoadResource :: url ::"+url);
                    }
                }
        );
//                    @Override
//                    public WebResourceResponse shouldInterceptRequest (WebView view, String url){
//                        Log.i("WebView", "shouldInterceptRequest :: View :: "+view.toString()+" url::"+url);
//                        return null;
//                    }
    }


    /**
     *
     * PEDIR INSTRUCCIONES
     */
    private class GetLandings extends AsyncTask<String, String, String[]>{

        private String TAG = "GetDominios :: ";

        /**
         *
         * @param params --> params[0] == id_device_server
         * @return --> success :: result[0] == String JSONObject
         *             error :: result[0] == codigo_error,
         *                      result[1] == titulo,
         *                      result[2] == mensaje
         */
        @Override
        protected String[] doInBackground(String... params) {
            String[] result = new String[1];
            result[0] = "weird";
            /**
             * ask db for domains
             */
            try {
                HttpClient httpclient = new DefaultHttpClient();
                JSONObject jDevice = new JSONObject();
                HttpPost post = new HttpPost(serverWS+"landing/export");
                post.setHeader("Accept", "application/json");
                post.setHeader("Content-type", "application/json");

                BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
                bluethootMacAddress = myDevice.getAddress();

                jDevice.put("device", bluethootMacAddress);

                Log.i(TAG,jDevice.toString());
                StringEntity requestStringEntity = new StringEntity(jDevice.toString());
                post.setEntity(requestStringEntity);

                HttpResponse responsePost = httpclient.execute(post);
                StatusLine statusLineGet = responsePost.getStatusLine();
                if (statusLineGet.getStatusCode() == HttpStatus.SC_OK) {

                    InputStream inputStream = responsePost.getEntity().getContent();
                    if (inputStream != null) {
                        JSONObject jResponse = new JSONObject(
                                convertInputStreamToString(inputStream));
                        String error = jResponse.getString("error").toLowerCase();
                        String description = jResponse.getString("description").toLowerCase();
                        if(error.equals("0") && description.equals("success")) {

                            int dominiosXvisitar = jResponse.getJSONObject("data").getInt("total");
                            Log.i("GetLandings", "dominios a visitar::"+dominiosXvisitar);
                            if(dominiosXvisitar > 0) {

                                JSONArray dominios = jResponse.getJSONObject("data").getJSONArray("landings");
                                for (int i = 0; i < dominiosXvisitar; i++) {
                                    alLandings.add(
                                            new Landing(dominios.getJSONObject(i)));
                                    new DbAddLanding().execute(
                                            alLandings.get(alLandings.size()-1));
//                                    alDomains.add(new Domain(dominios.getJSONObject(i)));
//                                    new DbAddDomain().execute(alDomains.get(alDomains.size() - 1));
                                }
                                result = new String[1];
                                result[0] = "cool";
                                return result;

                            }else{

                                try{
                                    int sleepMins = jResponse.getJSONObject("data").getInt("sleep");
                                    Thread.currentThread();
                                    Thread.sleep(sleepMins);
                                }catch(InterruptedException e){
                                }
                                result = new String[1];
                                result[0] = "sleep";
                                return result;
                            }

                        }else{
                            result = new String[3];
                            result[0] = ERROR_CLASE_2;
                            result[1] = "GetLandings :: ServerError::"+error+" ServerDescription::"+description;
                            result[2] = TAG;
                            return result;
                        }
                    } else {
                        result = new String[3];
                        result[0] = ERROR_CLASE_2;
                        result[1] = "error";
                        result[2] = "GetLandings :: null inputStream";
                        return result;
                    }
                } else {
                    result = new String[3];
                    result[0] = ERROR_CLASE_2;
                    result[1] = "error :: "+ statusLineGet.getReasonPhrase();
                    result[2] = "GetLandings :: HttpStatus NOT OK";
                    return result;
                }
            } catch (IOException | JSONException e) {
                Log.e("GetLandings", e.toString() + " :: " + e.getMessage());
                result = new String[3];
                result[0] = ERROR_CLASE_2;
                result[1] = "GetLandings" + e.toString();
                result[2] = e.getMessage();
                return result;
            }
        }

        @Override
        protected void onProgressUpdate(String... values){
//            updateNotification(values[0]);
            //toDisplay.setText("" + values[0]);
//            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
//            Log.i(MA+"GetInstrucciones", values[0]);
        }

        @Override
        protected void onPostExecute(String[] result){
//            lastInstruction = System.currentTimeMillis();
//            Log.d("LastInstruction", "timestamp::"+lastInstruction);

//            if(saldo > 5){

//            }
            //all cool
            if(result.length == 1){
                if(result[0].equals("cool")) {
//                    alDomains.add(new Domain("http://www.phantomland.mx/smt/club/enation"));
                    lastInstruction = System.currentTimeMillis();
                    Log.d("LastInstruction", "timestamp::" + lastInstruction);

                    prepareWebView();
                    //prepare new Crawl

                    Log.i("GetLandings", "#Dominios::" + alLandings.size());
//                    new DBAddClick().execute(click);
                    new VisitLanding().execute();

                }else if(result[0].equals("sleep")){
                    new GetLandings().execute();
                }
            }else if(result.length == 3){
                new SendError().execute(result[0], result[1], result[2]);
            }else{
                // unknown
                new SendError().execute(ERROR_CLASE_2,"error","GetInstrucciones::PostExecute::unknown");
            }
        }

    }

    /**
     * Guardar landing en DB interna
     */
    private class DbAddLanding extends AsyncTask<Landing, Void, String[]>
    {

        private final String TAG = "DBAddLanding";

        /**
         *
         * @param params [0] == url
         * @return result
         */
        @Override
        protected String[] doInBackground(Landing... params) {
            String[] result = new String[1];
            try {
//                Log.i(MA, "************************** DBisOpen? :: "+db.isOpen());

                Landing landing = params[0];

                // Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();
                values.put(CrawlerDB.LandingDB.COLUMN_NAME_IDCONTROL, landing.getId());
                values.put(CrawlerDB.LandingDB.COLUMN_NAME_URL, landing.getUrl());
                values.put(CrawlerDB.LandingDB.COLUMN_NAME_NETWORK, landing.getNetwork());

                // Insert the new row, returning the primary key value of the new row
                long newRowId;
                newRowId = db.insertOrThrow(
                        CrawlerDB.LandingDB.TABLE_NAME,
                        null,
                        values);
                result[0] = String.valueOf(newRowId);
                return result;
            }catch(SQLException e){
                result = new String[3];
                result[0] = ERROR_CLASE_1;
                result[1] = TAG+"::"+e.toString();
                result[2] = e.getMessage();
                return result;
            }

        }

        @Override
        protected void onPostExecute(String[] result){
            lastInstruction = System.currentTimeMillis();
            Log.d("LastInstruction", "timestamp::"+lastInstruction);

            if(result.length == 1){
                if(result[0].equals("-1")){
                    Log.e(TAG, "Error guardando landing");
                }else {
                    Log.i(TAG, " ********************** Landing added :: " + result[0]);
                }
//                setDeviceID(result[0]);
                //result == 2 :: null response from server
            }else if(result.length == 3){
                Log.e(TAG, "Error guardando landing");
                new SendError().execute(result[0],result[1],result[2]);
            }
        }

    }

    /**
     * Guardar landing en DB interna
     */
    private class DbAddLandingParsed extends AsyncTask<Landing, Void, String[]>
    {

        private final String TAG = "DBAddLandingParsed";

        /**
         *
         * @param params [0] == url
         * @return result
         */
        @Override
        protected String[] doInBackground(Landing... params) {
            String[] result = new String[1];
            try {
//                Log.i(MA, "************************** DBisOpen? :: "+db.isOpen());

                Landing landingParsed = params[0];

                // Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();
                values.put(CrawlerDB.LandingParsedDB.COLUMN_NAME_IDCONTROL, landingParsed.getId());
                values.put(CrawlerDB.LandingParsedDB.COLUMN_NAME_IMAGETYPE, landingParsed.getImageType());
                values.put(CrawlerDB.LandingParsedDB.COLUMN_NAME_TIME, landingParsed.getTime());

                // Insert the new row, returning the primary key value of the new row
                long newRowId;
                newRowId = db.insertOrThrow(
                        CrawlerDB.LandingParsedDB.TABLE_NAME,
                        null,
                        values);
                result[0] = String.valueOf(newRowId);
                return result;
            }catch(SQLException e){
                result = new String[3];
                result[0] = ERROR_CLASE_1;
                result[1] = TAG+"::"+e.toString();
                result[2] = e.getMessage();
                return result;
            }

        }

        @Override
        protected void onPostExecute(String[] result){
            lastInstruction = System.currentTimeMillis();
            Log.d("LastInstruction", "timestamp::"+lastInstruction);

            if(result.length == 1){
                if(result[0].equals("-1")){
                    Log.e(TAG, "Error guardando landingParsed");
                }else {
                    Log.i(TAG, " ********************** LandingParsed added :: " + result[0]);
                }
//                setDeviceID(result[0]);
                //result == 2 :: null response from server
            }else if(result.length == 3){
                Log.e(TAG, "Error guardando landingParsed");
                new SendError().execute(result[0],result[1],result[2]);
            }
        }

    }


    /**
     * ProcessLandings
     */
    private class VisitLanding extends AsyncTask<Void, Void, String>{

        private String TAG = "VisitLanding";

        @Override
        protected String doInBackground(Void... params) {
            landing = alLandings.get(alLandings.size() - 1);
            mColors = new Colors(mColorsHue, landing.getId());

            if(landing.getNetwork().equals("WIFI")){
                while(getActiveNetwork() != ConnectivityManager.TYPE_WIFI){
                    try{
                        changeNetworkPreference(ConnectivityManager.TYPE_WIFI);
                        Log.i(TAG, "esperando WIFI");
                        Thread.currentThread();
                        Thread.sleep(1000*5);//sleep 5 segundos
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }else{
//                while(getActiveNetwork() != ConnectivityManager.TYPE_MOBILE){
//                    try{
//                        changeNetworkPreference(ConnectivityManager.TYPE_MOBILE);
//                        Log.i(TAG, "esperando 3G");
//                        Thread.currentThread();
//                        Thread.sleep(1000*5);//sleep 5 segundos
//                    }catch(Exception e){
//                        e.printStackTrace();
//                    }
//                }
            }

            return landing.getUrl();
        }

        @Override
        protected void onPostExecute(String nextLanding){
            startLoad = System.currentTimeMillis();
            webView.loadUrl(nextLanding);
        }
    }

    //javascript interface
    @SuppressWarnings("unused")
    class JSInterface{
        private Context ctx;

        JSInterface(Context ctx) {
            this.ctx = ctx;
        }

        @JavascriptInterface
        public void html(final String html, final String url) {
            if(!url.contains("about:blank")) {
                new ProcessLanding().execute(html, url);
            }
        }
    }

    /**
     * Guarda la imagen del dominio a visitar
     */
    private class ProcessLanding extends AsyncTask<String, Void, String[]> {

        String TAG = "ProcessLanding";

        @Override
        protected String[] doInBackground(String... params) {
            String[] result = {"cool"};
            String html = params[0];
            String url = params[1];

            try{
                Thread.currentThread();
                Thread.sleep(5000);
            }catch(InterruptedException e){}

//            Log.i(TAG, "Starting screenshot :: height:" + contentHeight);
//            contentHeight = (int)(contentHeight*wvScale);
//            Log.i(MA, "webview new zoomed height::" + contentHeight);
//            if (contentHeight < 1) {
//                contentHeight = displayHeight;
//            }

            try {
                File dScreenshots = new File(getApplicationContext().
                        getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Screenshots");
                if (dScreenshots.mkdirs() || dScreenshots.exists()) {

                    File fScreenshot = new File(dScreenshots, landing.getId() + ".jpeg");
//                    if (!fScreenshot.exists()) {

                        Bitmap bitmap = Bitmap.createBitmap(
                                displayWidth, displayHeight, Bitmap.Config.RGB_565);
                        Canvas canvas = new Canvas(bitmap);
                        webView.draw(canvas);

                        FileOutputStream fos = null;
                        fos = new FileOutputStream(fScreenshot);

                        boolean saved = bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos);

                        Log.i(TAG, "Screenshot saved" +
                                " :: domain:" + landing.getUrl());
                        fos.flush();
                        fos.close();

                        Mat mScreenshot = new Mat();
                        Utils.bitmapToMat(bitmap, mScreenshot);
//                        Imgproc.calc

                        MatOfInt[] mChannels = new MatOfInt[]
                                { new MatOfInt(0), new MatOfInt(1), new MatOfInt(2) };
                        MatOfInt mHistSize = new MatOfInt(mHistSizeNum);
                        MatOfFloat mRanges = new MatOfFloat(0f, 256f);
                        Mat mMat0 = new Mat();
                        float[] mBuff = new float[mHistSizeNum];
                        //todo

                        Mat hist = new Mat();
                        int thikness = (int) (displayWidth / (mHistSizeNum)) -1;
                        int offset = (int) ((displayWidth - (5*mHistSizeNum + 4*10)*thikness)/2);

//                        org.opencv.core.Point mP1 = new org.opencv.core.Point();
//                        org.opencv.core.Point mP2 = new org.opencv.core.Point();

                        for(int c=0; c<3; c++) {
                            Imgproc.calcHist(Arrays.asList(mScreenshot)
                                    , mChannels[c], mMat0, hist, mHistSize, mRanges);
                            Core.normalize(hist, hist, displayHeight, 0, Core.NORM_INF);
                            hist.get(0, 0, mBuff);
                        }
                        Mat mIntermediateMat = new Mat();
//
//        // Value and Hue
                        Imgproc.cvtColor(mScreenshot, mIntermediateMat, Imgproc.COLOR_RGB2HSV_FULL);
                        // Value
                        Imgproc.calcHist(Arrays.asList(mIntermediateMat)
                                , mChannels[2], mMat0, hist, mHistSize, mRanges);
                        Core.normalize(hist, hist, displayHeight, 0, Core.NORM_INF);
                        hist.get(0, 0, mBuff);
                        // Hue
                        Imgproc.calcHist(Arrays.asList(mIntermediateMat), mChannels[0]
                                , mMat0, hist, mHistSize, mRanges);
                        Core.normalize(hist, hist, displayHeight, 0, Core.NORM_INF);
                        hist.get(0, 0, mBuff);
                        mColors.setQuantities(mBuff);
                        Log.i(TAG, "JSON ... " + mColors.toJSON());

                        new DBAddColors().execute(mColors);
//                        double pixels = 0;
//                        for(int h=1; h<mHistSizeNum; h++) {
//                            mP1.x = mP2.x = offset + ((mHistSizeNum + 10) * h) * thikness;
//                            mP1.x = mP2.x = ((displayWidth/mHistSizeNum) * h) + thikness;
//                            mP1.y = displayHeight/2 - 1;
//                            mP2.y = mP1.y  - ((int)mBuff[h]);
//                            Core.line(mScreenshot, mP1, mP2, mColors.getColor(h), thikness);
//                            Log.i("TakeScreenshot", "hue::" + (Scalar)mColors.getColor(h)
//                                    + " ... number::" + mColors.getQuantity(h));
//                            pixels += (int)mBuff[h];
//                        }
//
//                        mP1.y = 0;
//                        mP2.y = displayHeight/2;
//                        mP1.x = mP2.x = 0;
//                        Core.line(mScreenshot, mP1, mP2, new Scalar(0,0,0, 255), thikness);
//
//                        mP1.x = 0;
//                        mP2.x = displayWidth;
//                        mP1.y = mP2.y = displayHeight/2;
//                        Core.line(mScreenshot, mP1, mP2, new Scalar(0,0,0, 255), thikness);
//                        pixels = pixels;
//                        Log.i("TakeScreenshot", "total pixels::"+pixels);

                        Document jsDominio = Jsoup.parse(params[0]);
                        Element jsImage = jsDominio.getElementsByTag("img").first();
                        String postfix;
                        if(jsImage.attr("src").lastIndexOf(".") != -1) {
                            postfix = jsImage.attr("src")
                                    .substring(jsImage.attr("src").lastIndexOf("."));
                        }else{
                            postfix = "error";
                        }
                        landing.setImageType(postfix);
                        new DbAddLandingParsed().execute(landing);
                        //todo guardar en DB


                        if (!bitmap.isRecycled()) {
                            bitmap.recycle();
                        }
                        dScreenshots = null;
                        canvas = null;
                        bitmap = null;
//                    } else {
//                        fScreenshot = null;
//                    }
                } else {
                    Log.i(TAG, "Could not create images directory");
                }
            } catch (IOException | RuntimeException | JSONException e) {
                result = new String[3];
                result[0] = e.toString();
                result[1] = html;
                result[2] = url;
                Log.e(TAG, "Exception", e);
            }

            return result;
        }

        /**
         * @param result
         */
        protected void onPostExecute(String[] result){
            lastInstruction = System.currentTimeMillis();
            Log.d("LastInstruction", "timestamp::" + lastInstruction);
//            Toast.makeText(getApplicationContext(), "Screenshot!", Toast.LENGTH_SHORT).show();
            if(result.length == 1 && result[0].equals("cool")){
                alLandings.remove(alLandings.size() - 1);

                if(alLandings.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Pacer::Dump time!", Toast.LENGTH_SHORT).show();
                    new DumpToServer().execute();
                }else {
                    new VisitLanding().execute();
                }
            }else{
                new SendError().execute(ERROR_CLASE_1, result[0], result[0]);
                new ProcessLanding().execute(result[1], result[2]);
            }
        }
    }


    /**
     * Guarda los colores de cada landing
     */
    private class DBAddColors extends AsyncTask<Colors, String[], String[]>{

        private final String TAG = "DBAddColor";

        @Override
        protected String[] doInBackground(Colors... params) {
            String[] result = new String[1];
            try {
//                Log.i(MA, "************************** DBisOpen? :: "+db.isOpen());

                Colors colors = params[0];

                for(int i = 0; i< colors.size(); i++) {
                    // Create a new map of values, where column names are the keys
                    ContentValues values = new ContentValues();
                    values.put(CrawlerDB.LandingColorsDB.COLUMN_NAME_IDCONTROL, colors.getIdControl());
                    values.put(CrawlerDB.LandingColorsDB.COLUMN_NAME_NUMBER, colors.getNumber(i) );
                    values.put(CrawlerDB.LandingColorsDB.COLUMN_NAME_QUANTITY, colors.getQuantity(i));

                    // Insert the new row, returning the primary key value of the new row
                    long newRowId;
                    newRowId = db.insertOrThrow(
                            CrawlerDB.LandingColorsDB.TABLE_NAME,
                            null,
                            values);
                    Log.i(TAG, "newRowID::"+newRowId);

                }
                result[0] = colors.size()+"";
                return result;
            }catch(SQLException e){
                result = new String[3];
                result[0] = ERROR_CLASE_1;
                result[1] = TAG+"::"+e.toString();
                result[2] = e.getMessage();
                return result;
            }
        }


        @Override
        protected void onPostExecute(String[] result){
            lastInstruction = System.currentTimeMillis();
            Log.d("LastInstruction", "timestamp::"+lastInstruction);

            if(result.length == 1){
                if(result[0].equals("-1")){
                    Log.e(TAG, "Error guardando colors");
                }else {
                    Log.i(TAG, " ********************** Colors added :: " + result[0]);
                }
//                setDeviceID(result[0]);
                //result == 2 :: null response from server
            }else if(result.length == 3){
                Log.e(TAG, "Error guardando colors");
                new SendError().execute(result[0],result[1],result[2]);
            }
        }
    }

    /**
     * Responder a server
     * - mandar redirects
     * - mandar HITS
     * - update click info
     * dar por terminado el click y empezar uno nuevo
     */
    private class DumpToServer extends AsyncTask<String, String[], String[]>{

        @Override
        protected String[] doInBackground(String... params) {
            /**
             * ejemplo de click
             {
             "click":[{
             "id_click":"#",
             "timestamp":"",
             "endpoint": {
             "dominio":"bla.blabla.com", // dominio endpoint
             "responsable":"texto", // ej. Niva
             "club":"texto", // ej. DjMovil
             "mdsp":"texto",
             "md5banner":"texto"
             }}]}
             **/
            String[] result = new String[1];
            result[0] = "empty";

            Log.i("DumpToServer", "Empezando");

            while(getActiveNetwork() != ConnectivityManager.TYPE_WIFI){
                try{
                    changeNetworkPreference(ConnectivityManager.TYPE_WIFI);
                    Log.i("Respond to server", "Esperando WIFI");
                    Thread.currentThread();
                    Thread.sleep(1000*5);//sleep 5 segundos
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            StringEntity requestStringEntity;
            HttpResponse responsePost;
            HttpClient httpclient = new DefaultHttpClient();

            HttpPost post;
            StatusLine statusLinePost;


            try {
                logConnection();
                /******************* Clicks o Domains *******************************/

                String[] projectionLandings = new String[]{
                        CrawlerDB.LandingParsedDB.COLUMN_NAME_ID,
                        CrawlerDB.LandingParsedDB.COLUMN_NAME_IDCONTROL,
                        CrawlerDB.LandingParsedDB.COLUMN_NAME_IMAGETYPE,
                        CrawlerDB.LandingParsedDB.COLUMN_NAME_TIME,
                };

                // How you want the results sorted in the resulting Cursor
//                String sortOrder =
//                        CrawlerDB.ClickDB.COLUMN_NAME_DOMAIN + " ASC";
//                String selection = CrawlerDB.Redirect.COLUMN_NAME_ID;
                boolean keepGoing = true;
                int j = 0;

                while(keepGoing) {
                    JSONArray jLandings = new JSONArray();

                    j = j + 1;
                    int i = (j-1)*50;
                    String where = CrawlerDB.LandingDB.COLUMN_NAME_ID +" > "+ i
                            +" AND " + CrawlerDB.LandingDB.COLUMN_NAME_ID + " < " + j*50;
                    Log.i("DumpToServer", "WHERE clause :: "+where);

                    Cursor cursorLandings = db.query(
                            CrawlerDB.LandingParsedDB.TABLE_NAME,  // The table to query
                            projectionLandings,                               // The columns to return
                            where,                                // The columns for the WHERE clause
                            null,                            // The values for the WHERE clause
                            null,                                     // don't group the rows
                            null,                                     // don't filter by row groups
                            null                                 // The sort order
                    );

                    if (cursorLandings.getCount() > 0) {
                        cursorLandings.moveToFirst();


                        do { // todo
                            LandingParsed mLandingParsed = new LandingParsed(cursorLandings);
                            JSONObject jLanding = mLandingParsed.toJSON();
                            try {
                                File fScreenshot = new File(getApplicationContext().
                                        getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                                        + "/Screenshots", mLandingParsed.getIdControl() + ".jpeg");
                                if (fScreenshot.exists()) {
                                    FileInputStream fIn = new FileInputStream(fScreenshot);
                                    Bitmap bitmap = null;
                                    bitmap = BitmapFactory.decodeStream(fIn);

                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
                                    byte[] imageBytes = baos.toByteArray();
                                    String sImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                                    jLanding.put("image", sImage);
                                    Log.i("Screenshot", "jDomain with img :: bytes : " + baos.size());

                                    baos.close();
                                    if (!bitmap.isRecycled()) {
                                        bitmap.recycle();
                                    }
                                    bitmap = null;
                                } else {
                                    Log.i("Screenshot", "jDomain IMAGE NOT FOUND ");
                                }
                            }catch(IOException | NullPointerException e){
                                Log.i("DumpToServer","Catch trying to load image");
                            }

                            String whereColors = CrawlerDB.LandingColorsDB.COLUMN_NAME_IDCONTROL +"="
                                    +mLandingParsed.getIdControl();
                            Log.i("DumpToServer", "WHERECOLORS clause :: "+whereColors);

                            String[] projectionLandingColors = new String[]{
                                    CrawlerDB.LandingColorsDB.COLUMN_NAME_IDCONTROL,
                                    CrawlerDB.LandingColorsDB.COLUMN_NAME_NUMBER,
                                    CrawlerDB.LandingColorsDB.COLUMN_NAME_QUANTITY,
                            };

                            Cursor cursorLandingColors = db.query(
                                    CrawlerDB.LandingColorsDB.TABLE_NAME,  // The table to query
                                    projectionLandingColors,                               // The columns to return
                                    whereColors,                                // The columns for the WHERE clause
                                    null,                            // The values for the WHERE clause
                                    null,                                     // don't group the rows
                                    null,                                     // don't filter by row groups
                                    null                                 // The sort order
                            );

                            if (cursorLandingColors.getCount() > 0) {
                                cursorLandingColors.moveToFirst();
                                Colors mLandingColors = new Colors();
                                do{
                                    mLandingColors.add(cursorLandingColors);
                                }while(cursorLandingColors.moveToNext());
                                jLanding.put("colors", mLandingColors.toJSON());
                            }

                            jLandings.put(jLanding);
                        } while (cursorLandings.moveToNext());
                        cursorLandings.close();

                        /******************** Join All ***************************************/

                        if (jLandings.length() != 0) {
                            post = new HttpPost(serverWS + "landings/register");
                            post.setHeader("Accept", "application/json");
                            post.setHeader("Content-type", "application/json");
                            JSONObject jRequest = new JSONObject();
                            jRequest.put("landings", jLandings);
                            jRequest.put("device", bluethootMacAddress);

                            Log.i("DumpToServer", "jRequest :: "+ jRequest.toString());
                            requestStringEntity = new StringEntity(jRequest.toString());

                            post.setEntity(requestStringEntity);

                            responsePost = httpclient.execute(post);
                            statusLinePost = responsePost.getStatusLine();
                            if (statusLinePost.getStatusCode() == HttpStatus.SC_OK) {

                                InputStream inputStream = responsePost.getEntity().getContent();
                                if (inputStream != null) {
                                    JSONObject jResponse = new JSONObject(
                                            convertInputStreamToString(inputStream));

                                    int error = jResponse.getInt("error");
                                    String description = jResponse.getString("description").toLowerCase();
                                    Log.i("DumpToServer", "jResponse :: error:"
                                            + error + " description:" + description);
                                    if (error == 0 && description.equals("success")) {
                                        Log.d("EasyList", "getting EasyList");
                                        String sEasyList = jResponse.getJSONObject("data")
                                                .getString("easyList");
                                        String sBlackList = jResponse.getJSONObject("data")
                                                .getString("needleStack");

                                        result = new String[2];
                                        result[0] = sEasyList;
                                        result[1] = sBlackList;
                                    } else {

                                        result = new String[3];
                                        result[0] = ERROR_CLASE_5;
                                        result[1] = "DumpToServer :: error :: " + error;
                                        result[2] = description;
                                        return result;

                                    }
                                } else {
                                    result = new String[3];
                                    result[0] = ERROR_CLASE_5;
                                    result[1] = "Clicks :: error";
                                    result[2] = "DumpToServer - null response form server";
                                    return result;
                                }
                            } else {
                                result = new String[3];
                                result[0] = ERROR_CLASE_5;
                                result[1] = "Request not ok - DumpToServer";
                                result[2] = "status code :: " + statusLinePost.getStatusCode();
                                return result;
                            }
                        } else {
                            result = new String[1];
                            result[0] = "cool";
                        }//if jdomains != null


                    } else {// end if cursorClicks != null
                        keepGoing = false;
                    }
                }//end while(keepGoing)
            } catch (JSONException | RuntimeException | IOException e) {
                Log.e("DumpToServer", e.toString()+"::"+e.getMessage(), e);
                result = new String[3];
                result[0] = ERROR_CLASE_5;
                result[1] = "DumpToServer :: CLicks :: "+e.toString();
                result[2] = e.getMessage();
                return result;
            }
            return result;
        }

        @Override
        protected void onProgressUpdate(String[]... progress){
            String[] error = progress[0];
            new SendError().execute(error[0], error[1], error[2]);
        }

        @Override
        protected void onPostExecute(String[] result) {
            lastInstruction = System.currentTimeMillis();
            Log.d("LastInstruction", "timestamp::"+lastInstruction);

            // result == 1 :: all cool proceed
            if(result.length == 1){
                Log.i("DumpToServer", "onPostExecute :: DONE :: empty DB");

                landing.setErrores(0);
//                Log.i("DumpToServer", "onPostExecute :: DONE");

                new GetLandings().execute();
            }else if(result.length == 2){
                landing.setErrores(0);
                Log.i("DumpToServer", "onPostExecute :: DONE");
                //toDisplay.setText("Preparando nuevo Crawl");
                new DbRemoveAll().execute();
                //result == 2 :: null response from server
            }else if(result.length == 3){
                new SendError().execute(result[0],result[1],result[2]);
            }else{
                Log.e("DumpToServer", "onPostExecute - Algo raro pasó...");
                new SendError().execute(ERROR_CLASE_5,
                        "Error","onPostExecute-DumpToServer :: unknown error");
            }
//            dumpRun();
        }

        private void logConnection(){
            switch (getActiveNetwork()) {
                case ConnectivityManager.TYPE_WIFI:
                    Log.i("logConnection", "LOG :: WIFI :: ");
//                    mClick.setNetwork("WIFI");
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    Log.i("logConnection", "LOG :: WAP :: ");
//                    mClick.setNetwork("WAP");
                    break;
                default:
                    Log.i("logConnection", "LOG :: DEFAULT :: ");
//                    mClick.setNetwork("WIFI");
                    break;
            }
        }
    }

    /**
     * ERRORES
     *
     */
    private class SendError extends AsyncTask<String, String, String[]>{

        @Override
        protected void onPreExecute(){
            Log.e("SendError", "preExecute SendError");
//            //toDisplay.setText("Sending Error ... "+dominio);
        }

        /**
         *
         * @param params :: params[0] --> clase error (1,2,3)
         *               :: params[1] --> error title
         *               :: params[2] --> error message
         * @return :: cool - not cool
         */
        @Override
        protected String[] doInBackground(String... params) {
            String[] result;
            JSONObject jError = new JSONObject();
            HttpResponse responsePost;
            HttpClient httpclient = new DefaultHttpClient();

            String clase = params[0];
            String titulo = params[1];
            String mensaje = params[2];

            if(params != null){
                try {

                    HttpPost post = new HttpPost(serverWS+"error/new");
                    post.setHeader("Accept", "application/json");
                    post.setHeader("Content-type", "application/json");

                    /**
                     * { "device":[{
                     *   "address":"xx:xx:xx:xx",
                     *   "name":"nombre",
                     *   }]}
                     **/
//                    jError.put("domain", landing.getUrl());
                    if(landing.getErrores() == 5){
                        titulo = titulo + " -- && 5 errores seguidos -- closing App";
                    }
                    jError.put("description", titulo);
                    jError.put("message", mensaje);
                    jError.put("device", bluethootMacAddress);


                    Log.e("Error","request:: "+jError.toString());
                    StringEntity requestStringEntity = new StringEntity(jError.toString());
                    post.setEntity(requestStringEntity);

                    responsePost = httpclient.execute(post);
                    StatusLine statusLinePost = responsePost.getStatusLine();
                    if(statusLinePost.getStatusCode() == HttpStatus.SC_OK){

                        InputStream inputStream = responsePost.getEntity().getContent();
                        if(inputStream != null) {
                            JSONObject jResponse = new JSONObject(
                                    convertInputStreamToString(inputStream));
                            Log.d("SendError", "response :: "+jResponse.toString());
                            int error = jResponse.getInt("error");
                            String description = jResponse.getString("description").toLowerCase();
                            if(error == 0 && description.equals("success")){
                                result = new String[2];
                                result[0] = "cool";
                                result[1] = clase;
                                return result;
                            }else{
                                result = new String[2];
                                result[0] = "error :: "+description;
                                result[1] = clase;
                                return result;
                            }

                        }else {
                            result = new String[3];
                            result[0] = clase;
                            result[1] = "error";
                            result[2] = "SendError - null response form server";
                            return result;
                        }
                    }else{
                        result = new String[3];
                        result[0] = clase;
                        result[1] = "SendError :: Request not ok";
                        result[2] = "status code :: "+statusLinePost.getStatusCode();
                        return result;
                    }
                } catch (JSONException | IOException e) {
                    result = new String[3];
                    result[0] = clase;
                    result[1] = titulo+ " <<<<<-- 2nd Error--->>>>>>"+ e.toString();
                    result[2] = mensaje + " <<<<<-- 2nd Error--->>>>>>"+ e.getMessage();
                    return result;
                }
            }
            result = new String[3];
            result[0] = clase;
            result[1] = titulo;
            result[2] = mensaje;
            return result;
        }

        @Override
        protected void onPostExecute(String[] result){
            landing.increaseErrores();
            if(landing.getErrores() >= 5){
                Log.e("Send Error","onPostExcecute :: Terminando App... muchos errores seguidos...");
                finish();
                return;
            }
            if(result.length == 2 || result.length == 3){  //result[0] == response :: result[1] == clase error
//                    new PrepareNewCrawl().execute();
                if(result[1].equals(ERROR_CLASE_2)){
                    new GetLandings().execute();
                }else if(result[1].equals(ERROR_CLASE_3)){
                    new VisitLanding().execute();
                }else if(result[1].equals(ERROR_CLASE_5)){
                    new DumpToServer().execute();// no necesitamos controlar este.. por eso no se hace instancia
                }
            }
        }
    }

    /**
     *
     */
    private class SetupDB extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            mDbHelper = new CrawlerDBHelper(getApplicationContext());
            db = mDbHelper.getWritableDatabase();
            mDbHelper.onCreate(db); //crear tablas si no existen

            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            alLandings = new ArrayList<Landing>();
            new GetLandings().execute();
        }
    }

    /**
     * Borrar DB interna para nuevo ciclo
     */
    private class DbRemoveAll extends AsyncTask<Void, Void, String[]>{
        private String TAG = "DBRemoveAll";

        /**
         *
         * @param params void
         * @return result
         */
        @Override
        protected String[] doInBackground(Void... params) {
            String[] result = new String[1];
            try {
                mDbHelper.onUpgrade(db, 1, 1);

                File dScreenshots = new File(getApplicationContext().
                        getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Screenshots");
                if (dScreenshots.mkdirs() || dScreenshots.exists()) {
                    File[] list = dScreenshots.listFiles();
                    for (File screenshot : list) {
                        Log.i(TAG, "Removing all :: Deleted screenshot ?? :: "+screenshot.delete());
                    }
                }

                result[0] = "cool";
                return result;

            }catch(Exception e){
                result = new String[3];
                result[0] = ERROR_CLASE_1;
                result[1] = "DBRemoveAll :: "+e.toString();
                result[2] = e.getMessage();
                return result;
            }
        }

        @Override
        protected void onPostExecute(String[] result){
            lastInstruction = System.currentTimeMillis();
            Log.d("LastInstruction", "timestamp::"+lastInstruction);

            if(result.length == 1){
                Log.i(TAG, "Info dropped");
                Log.i(TAG, "Getting new Domains");
                alLandings = new ArrayList<Landing>();
                new GetLandings().execute();

            }else if(result.length == 3){
                new SendError().execute(result[0],result[1],result[2]);
            }
        }

    }


    /**
     * Cambia preferencia de red
     * @param preference ConnectivityManager.TYPE_WIFI o ""_MOBILE
     */
    private void changeNetworkPreference(int preference){
        connectivityManager.setNetworkPreference(preference);
        if(preference == ConnectivityManager.TYPE_WIFI){
            Toast.makeText(getApplicationContext(), "WIFI", Toast.LENGTH_SHORT).show();
            WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            wifiManager.reconnect();

        }else {
            Toast.makeText(getApplicationContext(), "MOBILE", Toast.LENGTH_SHORT).show();

            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            wifiManager.disconnect();
        }
    }

    /**
     * Obtain Active Network
     * @return ActiveNetwork (int) or -1 if null
     */
    private int getActiveNetwork(){
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null) {
            switch (networkInfo.getType()) {
                case ConnectivityManager.TYPE_WIFI:
//                    mClick.setNetwork("WIFI");
                    return ConnectivityManager.TYPE_WIFI;
//                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    return ConnectivityManager.TYPE_MOBILE;
//                    break;
                default:
                    return ConnectivityManager.TYPE_WIFI;
//                    break;
            }
        }else{
            return -1;
        }
    }

}
