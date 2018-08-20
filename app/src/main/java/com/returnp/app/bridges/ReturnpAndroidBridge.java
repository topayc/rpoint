package com.returnp.app.bridges;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.support.v4.app.ActivityCompat;
import android.view.Display;
import android.webkit.JavascriptInterface;

import com.google.zxing.integration.android.IntentIntegrator;
import com.returnp.app.ReturnPMainActivity;
import com.returnp.app.androidutils.AndroidUtil;
import com.returnp.app.session.ReturnPSession;

import org.json.JSONException;
import org.json.JSONObject;

public class ReturnpAndroidBridge {

    private Context context;
    private ReturnPSession mSession;

    public ReturnpAndroidBridge() {
    }

    public ReturnpAndroidBridge(Context context, ReturnPSession session) {
        this.context = context;
        this.mSession = session;
    }

    @JavascriptInterface
    public void getDeviceSize() {
        Display display = ((ReturnPMainActivity) this.context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        JSONObject json = new JSONObject();
        try {
            json.put("width" , size.x);
            json.put("height" , size.y);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ((ReturnPMainActivity) this.context).setBridgeResponse(null, json.toString());
    }

    @JavascriptInterface
    public void scanQRCode() {
        new IntentIntegrator((Activity) context).initiateScan();
    }

    @JavascriptInterface
    public void setDeivceSession(String userName, String userEmail, String userAutoToken) {
        this.mSession.setUserSession(userName, userEmail, userAutoToken);
        this.setBridgeResponse(null, null);
    }

    @JavascriptInterface
    public void clearDeivceSession() {
        this.mSession.clearUserSession();
        this.setBridgeResponse(null, null);
    }

    @JavascriptInterface
    public void getSessonValue(String key) {
        String value = this.mSession.getSessionValue(key);
        this.setBridgeResponse(null, value );
    }

    @JavascriptInterface
    public void loadUrl(String url) {
        ((ReturnPMainActivity) this.context).loadUrl(url);
    }



    @JavascriptInterface
    public void getPhoneNumber() {
        String phoneNumberCountry = null;
        String phoneNumber = null;
        if (context.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            phoneNumberCountry = AndroidUtil.getPhoneNumber(this.context);
            phoneNumber = "0" + (phoneNumberCountry.substring(phoneNumberCountry.length() - 10, phoneNumberCountry.length()));
            JSONObject json = new JSONObject();
            try {
                json.put("phoneNumber", phoneNumber);
                json.put("phoneNumberCountry", phoneNumberCountry);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ((ReturnPMainActivity) this.context).setBridgeResponse(null, json.toString());
        } else {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_PHONE_STATE}, ReturnPMainActivity.PERMISSION_REQUEST_READ_PHONE_STATE);
        }
    }

    public void setBridgeResponse(String callbackName, String data){
        ((ReturnPMainActivity) this.context).setBridgeResponse(callbackName, data);
    }
}
