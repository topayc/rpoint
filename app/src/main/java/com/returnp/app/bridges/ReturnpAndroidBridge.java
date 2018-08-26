package com.returnp.app.bridges;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Display;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.returnp.app.AnyOrientationCaptureActivity;
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
        IntentIntegrator integrato = new IntentIntegrator((Activity) context);
        integrato.setCaptureActivity(AnyOrientationCaptureActivity.class);
        integrato.addExtra("PROMPT_MESSAGE", "QRCode를 사각형안에 비춰 주세요");
        integrato.setOrientationLocked(false);
        integrato.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrato.setCameraId(0);
        integrato.setBeepEnabled(true);
        integrato.setBarcodeImageEnabled(true);
        integrato.initiateScan();
    }

    @JavascriptInterface
    public void toast(String message) {
        Toast toast = Toast.makeText(this.context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    @JavascriptInterface
    public void setDeviceSession(String session) {
        Log.d("setDeviceSession", session);
        String userName = session.split(":")[0];
        String userEmail= session.split(":")[1];
        String userAutoToken = session.split(":")[2];
        this.mSession.setUserSession(userName, userEmail, userAutoToken);
        this.setBridgeResponse(null, null);
    }

    @JavascriptInterface
    public void clearDeviceSession() {
        this.mSession.clearUserSession();
        this.setBridgeResponse(null, null);
    }

    @JavascriptInterface
    public void getSessionValue(String key) {
        String value = null;
        if (key.equals("PREF_ALL_SESSION")) {
            value = this.mSession.getUserSession().toString();
        }else {
            value = this.mSession.getSessionValue(key);
        }
        this.setBridgeResponse(null, value );
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
