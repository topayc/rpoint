package com.tophappyworld.returnpapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.tophappyworld.returnpapp.androidutils.AndroidUtil;
import com.tophappyworld.returnpapp.bridges.ReturnpAndroidBridge;
import com.tophappyworld.returnpapp.session.ReturnPSession;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ReturnPMainActivity extends AppCompatActivity {
    public static String TAG = "ReturnPMainActivity";
    public ReturnpAndroidBridge mReturnpAndroidBridge;
    public AndroidUtil androidUtil;
    public WebView mWebView;
    public Handler handler = new Handler();
    private Toast toast;
    private ReturnPSession mSession;
    private long backKeyPressedTime = 0;

    public static int ACTIVITY_STARTING = 0;
    public static int ACTIVITY_RUNNING = 1;
    public static int ACTIVITY_EXITING = 2;
    public int mActivityStatus = 0;

    public static boolean  INITIAL_LOADING = true;

    //public static final String PRODUCT_INIT_URL = "http://tm.returnp.com";

    public static final String PRODUCT_MAIN_URL = "https://www.returnp.com";
    public static final String PRODUCT_INIT_URL = "https://www.returnp.com/m/main/index.do";
    public static final String PRODUCT_GIFT_CARD_DETAIL_URL = "https://www.returnp.com/m/giftCard/giftCardDetail.do";
    public static final String PRODUCT_POINT_CODE_URL = "https://www.returnp.com/m/pointCoupon/index.do";
    public static final String PRODUCT_INTRO_URL = "https://www.returnp.com/m/intro/intro.do";
    public static final int PRODUCT_MODE = 1;

    public static final String DEVELOP_MAIN_URL = "http://211.254.212.90:9868";
    public static final String DEVELOP_INIT_URL = "http://211.254.212.90:9868/m/main/index.do";
    public static final String DEVELOP_GIFT_CARD_DETAIL_URL = "http://211.254.212.90:9868/m/giftCard/giftCardDetail.do";
    public static final String DEVELOP_POINT_CODE_URL = "https://www.returnp.com/m/pointCoupon/index.do";
    public static final String DEVELOP_INTRO_URL = "http://211.254.212.90:9868/m/intro/intro.do";
    public static final int DEVELOP_MODE = 2;

    public static final String LOCAL_MAIN_URL = "http://192.168.123.142:9090";
    public static final String LOCAL_URL_SUFFIX = "http://192.168.123.142:9090/";
    public static final String DEV_URL_SUFFIX = "http://211.254.212.90:9868";
    public static final String LOCAL_INIT_URL = "http://192.168.123.142:9090/m/main/index.do";
    public static final String LOCAL_GIFT_CARD_DETAIL_URL = "http://192.168.123.142:9090/m/giftCard/giftCardDetail.do";
    public static final String LOCAL_POINT_CODE_URL = "https://www.returnp.com/m/pointCoupon/index.do";
    public static final String LOCAL_INTRO_URL = "http://192.168.123.142:9090/m/intro/intro.do";
    public static final int LOCAL_MODE = 3;


    //public static final String PRODUCT_JOIN_URL = "http://192.168.219.174:9090/m/main/index.do";
    private static String INIT_URL = null;
    private static String GIFT_CARD_DETAIL_URL = null;
    private static String INTRO_URL = null;
    private static String MAIN_URL = null;
    private static int RUN_MODE;

    public static final String BRIDGE_NAME = "returnpAndroidBridge";

    /* 퍼미션 요청 상수*/
    public static final int PERMISSION_REQUEST_CAMERA = 1;
    public static final int PERMISSION_REQUEST_READ_PHONE_STATE = 2;
    public static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 3;
    public static final int PERMISSION_REQUEST_READ_CONTACTS = 4;
    public static final int PERMISSION_REQUEST_SEND_SMS = 5;
    public static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE= 6;
    public static final int PERMISSION_REQUEST_NEED_ALL= 1000;
    public static final int  PICK_IMAGE_REQ_CODE = 3001;
    public static final int  PICK_CAMERA_REQ_CODE = 3002;

    public static String SELECT_IMAGE_JS_CALLBACK = "bridge.updateImage";

    public static final String INTENT_PROTOCOL_START = "intent:";
    public static final String INTENT_PROTOCOL_INTENT = "#Intent;";
    public static final String INTENT_PROTOCOL_END = ";end;";
    public static final String GOOGLE_PLAY_STORE_PREFIX = "market://details?id=";

    public static final String[] pemissions   = new String[]{
            Manifest.permission.INTERNET,Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE
    };

    public static  final String USER_AGENT = " APP_RETURNP_Android";
    public static final String HEADER_USER_AUTH_TOKEN =  "user_auth_token";

    private ValueCallback<Uri> filePathCallbackNormal;
    private ValueCallback<Uri[]> filePathCallbackLollipop;
    public final static int FILECHOOSER_NORMAL_REQ_CODE = 2001;
    public final static int FILECHOOSER_LOLLIPOP_REQ_CODE = 2002;
    private Uri cameraImageUri = null;

    public String mustPushtokenSending ;
    public static final String VERSION = "26";


    public ReturnPSession getSession(){
        return this.mSession;
    }

    public ReturnpAndroidBridge getmeturnpAndroidBridge(){
        return this.mReturnpAndroidBridge;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*
        Intent intent = new Intent(ReturnPMainActivity.this, SplashActivity.class);
        startActivity(intent);
        */
        super.onCreate(savedInstanceState);
        this.mActivityStatus = ReturnPMainActivity.ACTIVITY_STARTING;
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(Color.BLACK);

        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_return_pmain);

        //startActivity(new Intent(this, SplashActivity.class));

        this.initActivity();
        this.initWebView();

        /* 운영, 개발, 로컬 환경에 따라 알맞은 값을 세팅*/
        this.RUN_MODE =  ReturnPMainActivity.PRODUCT_MODE;
        this.INIT_URL =  ReturnPMainActivity.PRODUCT_INIT_URL;
        this.GIFT_CARD_DETAIL_URL = ReturnPMainActivity.PRODUCT_GIFT_CARD_DETAIL_URL;
        this.INTRO_URL = ReturnPMainActivity.PRODUCT_INTRO_URL;
        this.MAIN_URL = ReturnPMainActivity.PRODUCT_MAIN_URL;

        /*앱 실행 환경이 운영 모드가 아닐 경우에만 디버깅 기능 활성화 */
        if (this.RUN_MODE != ReturnPMainActivity.PRODUCT_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }

        this.mSession.setData(ReturnPSession.PREF_VERSION, ReturnPMainActivity.VERSION);

        Intent intent = getIntent();
        String pushCode;
        if (intent != null) {
            String link = intent.getStringExtra("link");
            if (link != null && link.length() > 0) {
                this.INIT_URL =  ReturnPMainActivity.MAIN_URL + link;
            }else {
                pushCode = intent.getStringExtra("pushCode");
                if (pushCode != null) {
                    switch (pushCode) {
                        case "1":
                            this.INIT_URL = ReturnPMainActivity.GIFT_CARD_DETAIL_URL + "?myGiftCardNo=" + intent.getStringExtra("myGiftCardNo");
                            break;
                    }
                }
            }
        }


        /*추천인에 의한 앱 설치 및 초기 실행시에 초기 회원 가입 페이지로 이동*/
        //if (this.mSession.getSessionValue(ReturnPSession.PREF_RECOMMENDER_INST).equals("") &&
          //      ( this.mSession.getSessionValue(ReturnPSession.PREF_RECOMMENDER_EMAIL).equals("")) &&
            //    ( this.mSession.getSessionValue(ReturnPSession.PREF_RECOMMENDER_EMAIL).length() > 1)){
            //this.INIT_URL =  ReturnPMainActivity.PRODUCT_JOIN_URL;
        //}else {
            /* 추천인에 의한 앱 설치 및 초기 실행이라고 하더라도, 바로 회원 가입을 하지 않을 수 있기 때문에
            * 해당 Preferenece 정보는 유지하며, 회원 가입 완료후에야 해당 정보를 삭제함
            * */

        //}

        ((ReturnpAppliaction) getApplication()).setMainActivity(this);
        this.mustPushtokenSending = this.mSession.getSessionValue(ReturnPSession.PREF_MUST_PHSU_TOKEN_SEND);
        String userAuthToken = ReturnPMainActivity.this.mSession.getUserAutoToken();
        Map<String, String> headerMap  = new HashMap<String, String>();
        headerMap.put(ReturnPMainActivity.HEADER_USER_AUTH_TOKEN, userAuthToken);

        String firstRun = this.mSession.getData(ReturnPSession.PREF_FIRST_RUN, "Y");
        if (firstRun.equals("Y")){
            this.mSession.setData(ReturnPSession.PREF_FIRST_RUN, "N");
            mWebView.loadUrl(ReturnPMainActivity.INTRO_URL,headerMap);
        }else {
            mWebView.loadUrl(ReturnPMainActivity.INIT_URL,headerMap);
        }
        checkVerify();
    }


    private void initActivity() {
        this.mSession = new ReturnPSession(this);
        this.mReturnpAndroidBridge = new ReturnpAndroidBridge(this, this.mSession);
    }

    @TargetApi(16)
    protected void fixNewAndroid(WebView webView) {
        try {
            webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
            webView.getSettings().setAllowFileAccessFromFileURLs(true);
        } catch(NullPointerException e) {
        }
    }


    private void initWebView() {
        mWebView = (WebView) findViewById(R.id.webView);
        //mWebView.clearCache(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE );
        mWebView.getSettings().setSaveFormData(true);
        mWebView.getSettings().setSupportZoom(false);
        mWebView.getSettings().setBuiltInZoomControls(false);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setGeolocationEnabled(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        String userAgent = mWebView.getSettings().getUserAgentString();
        mWebView.getSettings().setUserAgentString( userAgent + ReturnPMainActivity.USER_AGENT);

        if (Build.VERSION.SDK_INT >= 27){
            mWebView.getSettings().setSafeBrowsingEnabled(false);
        }

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN){
            fixNewAndroid(mWebView);
        }

        /*
        if (Build.VERSION.SDK_INT >= 16){
            webSettings.setAllowFileAccess(true);
            webSettings.setAllowUniversalAccessFromFileURLs(true);
        }

        if (Build.VERSION.SDK_INT >= 21){
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }*/

        mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);
        mWebView.setNetworkAvailable(true);
        mWebView.setWebChromeClient(new WebChromeClient());

        mWebView.addJavascriptInterface(this.mReturnpAndroidBridge, ReturnPMainActivity.BRIDGE_NAME);
        mWebView.setWebViewClient(new WebViewClient() {

            // For Android < 3.0
            public void openFileChooser( ValueCallback<Uri> uploadMsg) {
                Log.d("MainActivity", "3.0 <");
                openFileChooser(uploadMsg, "");
            }
            // For Android 3.0+
            public void openFileChooser( ValueCallback<Uri> uploadMsg, String acceptType) {
                Log.d("MainActivity", "3.0+");
                filePathCallbackNormal = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                // i.setType("video/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_NORMAL_REQ_CODE);
            }
            // For Android 4.1+
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                Log.d("MainActivity", "4.1+");
                openFileChooser(uploadMsg, acceptType);
            }

            // For Android 5.0+
            public boolean onShowFileChooser(
                    WebView webView, ValueCallback<Uri[]> filePathCallback,
                    WebChromeClient.FileChooserParams fileChooserParams) {
                Log.d("MainActivity", "5.0+");
                if (filePathCallbackLollipop != null) {
                    filePathCallbackLollipop.onReceiveValue(null);
                    filePathCallbackLollipop = null;
                }
                filePathCallbackLollipop = filePathCallback;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                // i.setType("video/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_LOLLIPOP_REQ_CODE);

                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(final WebView view, final String url){
                /*JSONObject json = new JSONObject();
                Log.d(TAG, "WebViwew onPageFinished");
                String sendToken = ReturnPMainActivity.this.mSession.getSessionValue(ReturnPSession.PREF_MUST_PHSU_TOKEN_SEND);
                try {
                    if (ReturnPMainActivity.INITIAL_LOADING == true) {
                        if (ReturnPMainActivity.splashActivity != null) {
                            ReturnPMainActivity.splashActivity.handler.sendEmptyMessage(1);
                            ReturnPMainActivity.INITIAL_LOADING = false;
                        }
                    }
                }catch(Exception e){

                }
*/
                super.onPageFinished(view, url);
                view.invalidate();
            }
            /*
            * 이 메서드는 사용자가 클릭하여 이동하는 경우는 물론 소스상에서 보여지는 자동 연결 및
            * 다운의 경우에도 호출
            * 모든 경우의 주소 이동에 대하여 처리해야 함
            * */
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(ReturnPMainActivity.LOCAL_URL_SUFFIX) ||
                        url.startsWith(ReturnPMainActivity.DEV_URL_SUFFIX) ||
                        url.startsWith("http://www.returnp.com")  ||
                        url.startsWith("https://www.returnp.com")){
                    String userAuthToken = ReturnPMainActivity.this.mSession.getUserAutoToken();
                    Map<String, String> headerMap  = new HashMap<String, String>();
                    headerMap.put(ReturnPMainActivity.HEADER_USER_AUTH_TOKEN, userAuthToken);
                    ReturnPMainActivity.this.mWebView.loadUrl(url, headerMap);
                    return true;
                }else {
                    if (url.startsWith(INTENT_PROTOCOL_START)) {
                        final int customUrlStartIndex = INTENT_PROTOCOL_START.length();
                        final int customUrlEndIndex = url.indexOf(INTENT_PROTOCOL_INTENT);
                        if (customUrlEndIndex < 0) {
                            return false;
                        } else {
                            final String customUrl = url.substring(customUrlStartIndex, customUrlEndIndex);
                            try {
                                Intent k1 = new Intent(Intent.ACTION_VIEW, Uri.parse(customUrl));
                                k1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getBaseContext().startActivity(k1);
                            } catch (ActivityNotFoundException e) {
                                final int packageStartIndex = customUrlEndIndex + INTENT_PROTOCOL_INTENT.length();
                                final int packageEndIndex = url.indexOf(INTENT_PROTOCOL_END);

                                final String packageName = url.substring(packageStartIndex, packageEndIndex < 0 ? url.length() : packageEndIndex);
                                Intent kakaoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_PLAY_STORE_PREFIX + packageName));
                                getBaseContext().startActivity(kakaoIntent);
                            }
                            return true;
                        }
                    }else {
                        if (url.contains("play.google.com") || url.contains("market")) {
                            goUpdateStore(url);

                        }else {
                            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(i);
                        }
                        return true;
                    }
                }
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    String userAuthToken = ReturnPMainActivity.this.mSession.getUserAutoToken();
                    request.getRequestHeaders().put(ReturnPMainActivity.HEADER_USER_AUTH_TOKEN, userAuthToken);
                }

                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(final WebView view, final String url) {
                String userAuthToken = ReturnPMainActivity.this.mSession.getUserAutoToken();
                Map<String, String> headerMap  = new HashMap<String, String>();
                return super.shouldInterceptRequest(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                switch (errorCode) {
                    case ERROR_AUTHENTICATION: break;          // 서버에서 사용자 인증 실패
                    case ERROR_BAD_URL: break;                 // 잘못된 URL
                    case ERROR_CONNECT: break;                 // 서버로 연결 실패
                    case ERROR_FAILED_SSL_HANDSHAKE: break;    // SSL handshake 수행 실패
                    case ERROR_FILE: break;                    // 일반 파일 오류
                    case ERROR_FILE_NOT_FOUND: break;          // 파일을 찾을 수 없습니다
                    case ERROR_HOST_LOOKUP: break;             // 서버 또는 프록시 호스트 이름 조회 실패
                    case ERROR_IO: break;                      // 서버에서 읽거나 서버로 쓰기 실패
                    case ERROR_PROXY_AUTHENTICATION: break;    // 프록시에서 사용자 인증 실패
                    case ERROR_REDIRECT_LOOP:
                        break;           // 너무 많은 리디렉션
                    case ERROR_TIMEOUT: break;                 // 연결 시간 초과
                    case ERROR_TOO_MANY_REQUESTS: break;       // 페이지 로드중 너무 많은 요청 발생
                    case ERROR_UNKNOWN: break;                 // 일반 오류
                    case ERROR_UNSUPPORTED_AUTH_SCHEME: break; // 지원되지 않는 인증 체계
                    case ERROR_UNSUPPORTED_SCHEME: break;      // URI가 지원되지 않는 방식
                }
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.mActivityStatus = ReturnPMainActivity.ACTIVITY_RUNNING;
        Intent intent = getIntent();
        String pushCode;
        if (intent != null) {
            pushCode =  intent.getStringExtra("pushCode");
            if (pushCode != null) {
                switch(pushCode){
                    case "1":
                        String url = ReturnPMainActivity.GIFT_CARD_DETAIL_URL+ "?myGiftCardNo=" + intent.getStringExtra("myGiftCardNo");
                        String userAuthToken = ReturnPMainActivity.this.mSession.getUserAutoToken();
                        Map<String, String> headerMap  = new HashMap<String, String>();
                        headerMap.put(ReturnPMainActivity.HEADER_USER_AUTH_TOKEN, userAuthToken);
                        mWebView.loadUrl(url,headerMap);
                        break;
                }
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.mActivityStatus = ReturnPMainActivity.ACTIVITY_EXITING;
    }

    public void setBridgeResponse(String jsCallback, final String result){
        final String jsCallbackName = jsCallback != null ?  jsCallback : "bridge.jsBridgeCallback";
        handler.post(new Runnable() {
            @Override
            public void run() {
                ReturnPMainActivity.this.mWebView.loadUrl("javascript:" + jsCallbackName + "('" + result+ "')");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            this.setBridgeResponse("bridge.jsBridgeCallback", result.getContents());
            if (result.getContents() == null || result.getContents().trim().length() == 0) {
                this.showGuide(this.getResources().getString(R.string.qr_sacn_error));
            }

        }


        if (requestCode == PICK_CAMERA_REQ_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Bundle extras = data.getExtras();
                    //Bitmap imageBitmap = Bitmap.createScaledBitmap((Bitmap) extras.get("data"), 160, 160, true);
                    Bitmap imageBitmap = (Bitmap) extras.get("data");

                    new AsyncTask<Bitmap, Void, String>() {
                        @Override
                        protected String doInBackground(Bitmap... params) {
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            params[0].compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                            String base64Image = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
                            return "image/png:" + base64Image;
                        }

                        @Override
                        protected void onPostExecute(String result) {
                            ReturnPMainActivity.this.setBridgeResponse("bridge.updateCameraImage", result);
                        }
                    }.execute(imageBitmap);
                }
            }
        }

        if (requestCode == PICK_IMAGE_REQ_CODE ) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    final Uri uri = data.getData();
                    new AsyncTask<Uri, Void, String>() {
                        @Override
                        protected String doInBackground(Uri... params) {
                            Bitmap resizeBitmap=null;
                            try {
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                BitmapFactory.decodeStream(ReturnPMainActivity.this.getContentResolver().openInputStream(uri), null, options); // 1번

                                int width = options.outWidth;
                                int height = options.outHeight;
                                int samplesize = 1;

                                while (true) {//2번
                                    if (width / 2 < 400 || height / 2 < 400)
                                        break;
                                    width /= 2;
                                    height /= 2;
                                    samplesize *= 2;
                                }

                                options.inSampleSize = samplesize;
                                Bitmap bitmap = BitmapFactory.decodeStream(ReturnPMainActivity.this.getContentResolver().openInputStream(uri), null, options); //3번
                                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                                String base64Image = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
                                return "image/png:" + base64Image;
                            }catch (FileNotFoundException ee){

                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(String result) {
                            ReturnPMainActivity.this.setBridgeResponse("bridge.updateImage", result);
                        }
                    }.execute(uri);

                }
            }
        }

        if (resultCode == RESULT_OK) {
            if (requestCode == FILECHOOSER_NORMAL_REQ_CODE) {
                if (filePathCallbackNormal == null) return ;
                Uri result = (data == null || resultCode != RESULT_OK) ? null : data.getData();
                filePathCallbackNormal.onReceiveValue(result);
                filePathCallbackNormal = null;
            } else if (requestCode == FILECHOOSER_LOLLIPOP_REQ_CODE) {
                if (filePathCallbackLollipop == null) return ;
                filePathCallbackLollipop.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
                filePathCallbackLollipop = null;
            }
        } else {
            if (filePathCallbackLollipop != null) {
                filePathCallbackLollipop.onReceiveValue(null);
                filePathCallbackLollipop = null;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getMimeType(Uri uri) {
        ContentResolver cR = getContentResolver();
        String type = cR.getType(uri);
        return type;
    }

    private File uriToFile(Uri uri) {
        String filePath = "";
        final String[] imageColumns = {MediaStore.Images.Media.DATA };
        String scheme = uri.getScheme();

        if ( scheme.equalsIgnoreCase("content") ) {
            Cursor imageCursor = getContentResolver().query(uri, imageColumns, null, null, null);
            if (imageCursor.moveToFirst()) {
                filePath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
        } else {
            filePath = uri.getPath();
        }
        File file = new File( filePath );
        return file;
    }

    public String fileToString(File file) {
        String fileString = "";
        try {
            FileInputStream inputStream = new FileInputStream(file);
            ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();

            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = inputStream.read(buf)) != -1) {
                byteOutStream.write(buf, 0, len);
            }

            byte[] fileArray = byteOutStream.toByteArray();
            fileString = new String(Base64.encodeToString(fileArray, 0));
            inputStream.close();
            byteOutStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileString;
    }

    //권한 획득 여부 확인
    @TargetApi(Build.VERSION_CODES.M)
    public void checkVerify() {

        if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, PERMISSION_REQUEST_NEED_ALL);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Build.VERSION.SDK_INT >= 23) {
            if (requestCode == ReturnPMainActivity.PERMISSION_REQUEST_NEED_ALL){
                Boolean perm = true;
                for (int i = 0; i < grantResults.length; i++ ) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                        perm = false;
                        break;
                    }
                }
                if (!perm) {
                    new AlertDialog.Builder(this).setTitle("알림").setMessage(this.getResources().getString(R.string.request_auth))
                            .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            }).setCancelable(false).show();;
                }
            }
            else {
                JSONObject obj = new JSONObject();
                String permissionName = this.getPemissionName(requestCode);

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);

                    /*앱 실행 도중 , 큐알코드 스캐너를 위한 카메라 권한 획득으로, 이후 해당 브릿지 함수 호출*/
                    if (requestCode == ReturnPMainActivity.PERMISSION_REQUEST_CAMERA){
                        mReturnpAndroidBridge.scanQRCode();
                        return;
                    }

                    try {
                        obj.put("result", "100");
                        obj.put("permissionState", String.valueOf(grantResults[0]));
                        obj.put("permissionName", permissionName);

                        if (requestCode == ReturnPMainActivity.PERMISSION_REQUEST_READ_PHONE_STATE) {
                            this.setBridgeResponse(null, obj.toString());
                        }

                        if (requestCode == ReturnPMainActivity.PERMISSION_REQUEST_ACCESS_FINE_LOCATION) {
                            this.setBridgeResponse(null, obj.toString());
                        }

                        if (requestCode == ReturnPMainActivity.PERMISSION_REQUEST_READ_CONTACTS) {
                            this.setBridgeResponse(null, obj.toString());
                        }

                        if (requestCode == ReturnPMainActivity.PERMISSION_REQUEST_ACCESS_FINE_LOCATION) {
                            this.setBridgeResponse(null, obj.toString());
                        }

                        if (requestCode == ReturnPMainActivity.PERMISSION_REQUEST_SEND_SMS) {
                            this.setBridgeResponse(null, obj.toString());
                        }

                        if (requestCode == ReturnPMainActivity.PERMISSION_REQUEST_READ_EXTERNAL_STORAGE) {
                            this.setBridgeResponse(null, obj.toString());
                        }
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else {
                    try {
                        obj.put("result", "100");
                        obj.put("permissionState", String.valueOf(grantResults[0]));
                        obj.put("permissionName", permissionName);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    this.setBridgeResponse(null, obj.toString());
                }
            }

        }
    }

    protected String getPemissionName(int code){
        String name = "";
        switch(code) {
            case ReturnPMainActivity.PERMISSION_REQUEST_CAMERA:
                name = this.getResources().getString(R.string.camera);
                break;
            case ReturnPMainActivity.PERMISSION_REQUEST_READ_PHONE_STATE:
                name = this.getResources().getString(R.string.phone);
                break;
            case ReturnPMainActivity.PERMISSION_REQUEST_ACCESS_FINE_LOCATION:
                name = this.getResources().getString(R.string.gps);
                break;
        }
        return name;
    }

    @Override
    public void onBackPressed() {
        String url = this.mWebView.getOriginalUrl();
        if (this.mWebView.getOriginalUrl().equalsIgnoreCase(ReturnPMainActivity.INIT_URL) ||
                this.mWebView.getOriginalUrl().equalsIgnoreCase(ReturnPMainActivity.INIT_URL + "/main/index.do")) {
            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis();
                showGuide(this.getResources().getString(R.string.finish_back));
                return;
            }
            if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                finish();
                toast.cancel();
            }
            super.onBackPressed();
        }else if(this.mWebView.canGoBack()){
            this.mWebView.goBack();
        }else{
            super.onBackPressed();
        }
    }
    public void goUpdateStore(String url){
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(i);
        finish();

    }
    public void showGuide(String message) {
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void loadUrl(String url){
        this.mWebView.loadUrl("javascript:document.location=" + url );

    }

    private void runCamera(boolean _isCapture) {
        if (!_isCapture)
        {// 갤러리 띄운다.
            Intent pickIntent = new Intent(Intent.ACTION_PICK);
            pickIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            pickIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            String pickTitle = "사진 가져올 방법을 선택하세요.";
            Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);

            startActivityForResult(chooserIntent, FILECHOOSER_LOLLIPOP_REQ_CODE);
            return;
        }

        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        File path = getFilesDir();
        File file = new File(path, "fokCamera.png");
        // File 객체의 URI 를 얻는다.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            String strpa = getApplicationContext().getPackageName();
            cameraImageUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", file);
        }
        else
        {
            cameraImageUri = Uri.fromFile(file);
        }
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);

        if (!_isCapture)
        { // 선택팝업 카메라, 갤러리 둘다 띄우고 싶을 때..
            Intent pickIntent = new Intent(Intent.ACTION_PICK);
            pickIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            pickIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            String pickTitle = "사진 가져올 방법을 선택하세요.";
            Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);

            // 카메라 intent 포함시키기..
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{intentCamera});
            startActivityForResult(chooserIntent, FILECHOOSER_LOLLIPOP_REQ_CODE);
        }
        else
        {// 바로 카메라 실행..
            startActivityForResult(intentCamera, FILECHOOSER_LOLLIPOP_REQ_CODE);
        }
    }
}
