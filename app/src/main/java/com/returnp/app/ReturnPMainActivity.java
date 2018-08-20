package com.returnp.app;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.returnp.app.androidutils.AndroidUtil;
import com.returnp.app.bridges.ReturnpAndroidBridge;
import com.returnp.app.session.ReturnPSession;

import java.util.HashMap;
import java.util.Map;

public class ReturnPMainActivity extends AppCompatActivity {

    public  String TAG = "ReturnPMainActivity";
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

    //public static final String INIT_URL = "http://returnp.com/main/index.do";
    public static final String INIT_URL = "http://1.220.50.226:9090/main/index.do";
    //public static final String INIT_URL = "http://192.168.0.29:8080/main/index.do";
    public static final String BRIDGE_NAME = "returnpAndroidBridge";

    public static final int PERMISSION_REQUEST_CAMERA = 1;
    public static final int PERMISSION_REQUEST_READ_PHONE_STATE = 2;

    public static  final String USER_AGENT = " APP_RETURNP_Android";
    public static final String HEADER_USER_AUTH_TOKEN =  "user_auth_token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mActivityStatus = ReturnPMainActivity.ACTIVITY_STARTING;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_return_pmain);

        this.initActivity();
        this.initWebView();

        mWebView.loadUrl(ReturnPMainActivity.INIT_URL);
    }

    private void initActivity() {
        this.mReturnpAndroidBridge = new ReturnpAndroidBridge(this, this.mSession);
    }

    private void initWebView() {
        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.clearCache(true);
        mWebView.getSettings().setAppCacheEnabled(false);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setSaveFormData(true);
        mWebView.getSettings().setSupportZoom(false);
        mWebView.getSettings().setBuiltInZoomControls(false);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setGeolocationEnabled(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        String userAgent = mWebView.getSettings().getUserAgentString();
        mWebView.getSettings().setUserAgentString( userAgent + ReturnPMainActivity.USER_AGENT);

        if (Build.VERSION.SDK_INT >= 27){
            mWebView.getSettings().setSafeBrowsingEnabled(false);
        }

     /*   if (Build.VERSION.SDK_INT >= 16){
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

        if (this.mReturnpAndroidBridge == null)
            this.mReturnpAndroidBridge = new ReturnpAndroidBridge(this, this.mSession);

        mWebView.addJavascriptInterface(this.mReturnpAndroidBridge, ReturnPMainActivity.BRIDGE_NAME);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(final WebView view, final String url){
                super.onPageFinished(view, url);
                view.invalidate();
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                /*String userAuthToken = ReturnPMainActivity.this.mSession.getUserAutoToken();
                Map<String, String> headerMap  = new HashMap<String, String>();
                if (!"".endsWith(userAuthToken.trim())) {
                    headerMap.put(ReturnPMainActivity.HEADER_USER_AUTH_TOKEN, userAuthToken);
                }
                mWebView.loadUrl(url, headerMap);
                */
                mWebView.loadUrl(url);
                return true;
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
                    case ERROR_REDIRECT_LOOP: break;           // 너무 많은 리디렉션
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
            if (result.getContents() == null || result.getContents().trim().length() == 0){
                this.showGuide(this.getResources().getString(R.string.qr_sacn_error));
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Build.VERSION.SDK_INT >= 23) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
                if (requestCode == ReturnPMainActivity.PERMISSION_REQUEST_CAMERA){
                    mReturnpAndroidBridge.scanQRCode();
                }
                if (requestCode == ReturnPMainActivity.PERMISSION_REQUEST_READ_PHONE_STATE){
                    mReturnpAndroidBridge.getPhoneNumber();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (this.mWebView.getOriginalUrl().equalsIgnoreCase(ReturnPMainActivity.INIT_URL)) {
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

    public void showGuide(String message) {
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void loadUrl(String url){
        this.mWebView.loadUrl("javascript:document.location=" + url );

    }
}
