package com.tophappyworld.returnpapp.bridges;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Display;
import android.webkit.JavascriptInterface;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.tophappyworld.returnpapp.AnyOrientationCaptureActivity;
import com.tophappyworld.returnpapp.ReturnPMainActivity;
import com.tophappyworld.returnpapp.androidutils.AndroidUtil;
import com.tophappyworld.returnpapp.session.ReturnPSession;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ReturnpAndroidBridge {

    private Context context;
    private ReturnPSession mSession;

    public ReturnpAndroidBridge() {}

    public ReturnpAndroidBridge(Context context, ReturnPSession session) {
        this.context = context;
        this.mSession = session;
    }

    @JavascriptInterface
    public void checkPermission(String pemission) {
        JSONObject json = new JSONObject();
        int result = 0;
        try {
            if (pemission.equals("READ_PHONE_STATE")) {
                result = this.context.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE);
            }
            else if (pemission.equals("CAMERA")) {
                result = this.context.checkCallingOrSelfPermission(Manifest.permission.CAMERA);
            }
            else if (pemission.equals("READ_CONTACTS")) {
                result = this.context.checkCallingOrSelfPermission(Manifest.permission.READ_CONTACTS);
            }
            else if (pemission.equals("ACCESS_FINE_LOCATION")) {
                result = this.context.checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            else if (pemission.equals("SEND_SMS")) {
                result = this.context.checkCallingOrSelfPermission(Manifest.permission.SEND_SMS);
            }
            else if (pemission.equals("READ_EXTERNAL_STORAGE")) {
                result = this.context.checkCallingOrSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            else {
                result = -1;
            }
            json.put("result", "100");
            json.put("permissionName", pemission);
            json.put("permissionState", String.valueOf(result));
        } catch (JSONException e) {
            e.printStackTrace();
            try {
                json.put("result", "300");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }finally{
            ((ReturnPMainActivity) this.context).setBridgeResponse(null, json.toString());
        }
    }

    @JavascriptInterface
    public void requestAllPermission() {
        String[] permissions = new String[]{
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET
        };

    }

    @JavascriptInterface
    public void requestPermission(String pemission) {
        if (pemission.equals("READ_PHONE_STATE")) {
            ActivityCompat.requestPermissions(
                    (Activity)this.context,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    ReturnPMainActivity.PERMISSION_REQUEST_READ_PHONE_STATE);
        }

        if (pemission.equals("CAMERA")) {
            ActivityCompat.requestPermissions(
                    (Activity)this.context,
                    new String[]{Manifest.permission.CAMERA},
                    ReturnPMainActivity.PERMISSION_REQUEST_CAMERA);
        }

        if (pemission.equals("READ_CONTACTS")) {
            ActivityCompat.requestPermissions(
                    (Activity)this.context,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    ReturnPMainActivity.PERMISSION_REQUEST_READ_CONTACTS);
        }

        if (pemission.equals("ACCESS_FINE_LOCATION")) {
            ActivityCompat.requestPermissions(
                    (Activity)this.context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ReturnPMainActivity.PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (pemission.equals("SEND_SMS")) {
            ActivityCompat.requestPermissions(
                    (Activity)this.context,
                    new String[]{Manifest.permission.SEND_SMS},
                    ReturnPMainActivity.PERMISSION_REQUEST_SEND_SMS);
        }

        if (pemission.equals("READ_EXTERNAL_STORAGE")) {
            ActivityCompat.requestPermissions(
                    (Activity)this.context,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    ReturnPMainActivity.PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
        }

    }

    @JavascriptInterface
    public void sendSMS(String smsData){
        SmsManager sms = SmsManager.getDefault();
        JSONObject re = new JSONObject();
        try {
            JSONArray smsArr = new JSONArray(smsData);
            JSONObject target;
            for (int i = 0 ; i <smsArr.length();i++){
                target = smsArr.getJSONObject(i);
                sms.sendTextMessage(
                        target.getString("phoneNumber").replaceAll("/","").trim(), null, target.getString("message"), null, null);
            }
            re.put("result", "100");
        }catch (JSONException e) {
            e.printStackTrace();
            try{
                re.put("result", "300");
            }catch (JSONException e1) {
                e1.printStackTrace();
            }
        }finally{
            this.setBridgeResponse(null, re.toString());
        }
    }

    @JavascriptInterface
    public void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        ((ReturnPMainActivity)this.context).startActivityForResult(intent, ((ReturnPMainActivity)this.context).PICK_IMAGE_REQ_CODE);
    }

    @JavascriptInterface
    public void selectCameraImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ((ReturnPMainActivity)this.context).startActivityForResult(intent, ((ReturnPMainActivity)this.context).PICK_CAMERA_REQ_CODE);
    }


    @JavascriptInterface
    public void afterJoinComplete() {
        JSONObject json = new JSONObject();
        try {
            this.mSession.removeData(ReturnPSession.PREF_RECOMMENDER_INST);
            this.mSession.removeData(ReturnPSession.PREF_RECOMMENDER_EMAIL);
            json.put("result", "100");
        }catch(JSONException e){
            e.printStackTrace();
            try {
                json.put("result", "300");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

        }finally {
            this.setBridgeResponse(null, json.toString());
        }
    }

    @JavascriptInterface
    public void getMyLocation(){
        LocationManager locationManager = (LocationManager)this.context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        JSONObject  locationJson = new JSONObject();

        try {
            String bestProvider = locationManager.getBestProvider(criteria, true);
            Location location = locationManager.getLastKnownLocation(bestProvider);
            locationJson.put("result", "100");
            locationJson.put("latitude", location.getLatitude());
            locationJson.put("longitude", location.getLongitude());
        }catch(JSONException e ){
            e.printStackTrace();
            try {
                locationJson.put("result", "300");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }finally{
            this.setBridgeResponse(null, locationJson.toString());
        }

    }

    @JavascriptInterface
    public void getDeviceContacts() {
        Uri people = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        // 검색할 컬럼 정하기
        String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        @SuppressWarnings("deprecation")
        Cursor cursor = this.context.getContentResolver().query(
                people,
                projection,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);    // 전화번호부 가져오기

        int end = cursor.getCount();    // 전화번호부의 갯수 세기

        JSONArray contactArr = new JSONArray();

        String[] name = new String[end]; // 전화번호부의 이름을 저장할 배열 선언
        String[] phone = new String[end];

        int count = 0;
        HashMap<String, Boolean> phoneCacheMap = new HashMap<String, Boolean>();
        if(cursor.moveToFirst())
        {
            // 컬럼명으로 컬럼 인덱스 찾기
            int idIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID);
            int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

            JSONObject contact;
            do
            {
                if (phoneCacheMap.containsKey(cursor.getString(phoneIndex)) &&
                        phoneCacheMap.get(cursor.getString(phoneIndex)) == true){
                    continue;
                }
                contact = new JSONObject();
                try {
                    contact.put("name" , cursor.getString(nameIndex));
                    contact.put("phoneNumber" , cursor.getString(phoneIndex));
                    contactArr.put(contact);
                    phoneCacheMap.put(cursor.getString(phoneIndex), true);
                }catch (JSONException e) {
                    e.printStackTrace();
                 }
            } while(cursor.moveToNext() || count > end);
        }
        cursor.close();
        this.setBridgeResponse(null, contactArr.toString());
    }

    @JavascriptInterface
    public void getDeviceSize() {
        Display display = ((ReturnPMainActivity) this.context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        JSONObject json = new JSONObject();
        try {
            json.put("result" , "100");
            json.put("width" , size.x);
            json.put("height" , size.y);
        } catch (JSONException e) {
            e.printStackTrace();
            try {
                json.put("result" , "300");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }finally{
            ((ReturnPMainActivity) this.context).setBridgeResponse(null, json.toString());
        }
    }

    @JavascriptInterface
    public void  getDeviceResolution() {

        Display display = ((ReturnPMainActivity) this.context).getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);

        JSONObject json = new JSONObject();
        try {
            json.put("result" , "100");
            json.put("deviceHeight" , point.y);
            json.put("deviceWidth" , point.x);
        } catch (JSONException e) {
            e.printStackTrace();
            try {
                json.put("result" , "300");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }finally{
            ((ReturnPMainActivity) this.context).setBridgeResponse(null, json.toString());
        }
    }


    @JavascriptInterface
    public void scanQRCode() {
        IntentIntegrator integrato = new IntentIntegrator((Activity) context);
        integrato.setCaptureActivity(AnyOrientationCaptureActivity.class);
        integrato.addExtra("PROMPT_MESSAGE", "QR 코드를 사각형안에 비춰 주세요");
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
        JSONObject json = new JSONObject();
        try {
            JSONObject pJson = new JSONObject(session);
            String userName = pJson.get("userName").toString();
            String userEmail = pJson.get("userEmail").toString();
            String userAutoToken = pJson.get("userAuthToken").toString();

            this.mSession.setUserSession(userName, userEmail, userAutoToken);
            json.put("result","100");
        } catch (JSONException e){
            e.printStackTrace();
            try {
                json.put("result","300");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }finally {
            this.setBridgeResponse(null, json.toString());

        }
    }

    @JavascriptInterface
    public void getSesssionAndDeviceInfo() {
        JSONObject sessionObj   = this.mSession.getUserSession();
        String phoneNumberCountry = null;
        String phoneNumber = null;

        if (context.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            phoneNumberCountry = AndroidUtil.getPhoneNumber(this.context);
            phoneNumber = "0" + (phoneNumberCountry.substring(phoneNumberCountry.length() - 10, phoneNumberCountry.length()));
            try {
                sessionObj.put("result", "100");
                sessionObj.put("phoneNumber", phoneNumber);
                sessionObj.put("phoneNumberCountry", phoneNumberCountry);
            } catch (JSONException e) {
                e.printStackTrace();
                try {
                    sessionObj.put("result", "300");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }finally{
                this.setBridgeResponse(null, sessionObj.toString());
            }
        } else {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_PHONE_STATE}, ReturnPMainActivity.PERMISSION_REQUEST_READ_PHONE_STATE);
        }

}

    @JavascriptInterface
    public void clearDeviceSession() {
        JSONObject json   = this.mSession.getUserSession();
        try {
            this.mSession.clearUserSession();
            json.put("result","100");
        } catch (Exception e) {
            e.printStackTrace();
            try {
                json.put("result","300");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }finally{
            this.setBridgeResponse(null, json.toString());
        }
    }

    @JavascriptInterface
    public void getSessionValue(String key) {
        JSONObject json   = new JSONObject();
        try {
            json.put("result","100");
            if (key.equals("PREF_ALL_SESSION")) {
                json = this.mSession.getUserSession();
            }else {
                json.put(key, this.mSession.getSessionValue(key));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            try {
                json.put("result","300");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }finally{
            this.setBridgeResponse(null, json.toString() );
        }
    }

    @JavascriptInterface
    public void getData(String key, String defaultValue) {
        JSONObject json   = new JSONObject();
        try {
            json.put("result","100");
            json.put(key, this.mSession.getData(key, defaultValue));
        } catch (Exception e) {
            try {
                json.put("result","300");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }finally{
            this.setBridgeResponse(null, json.toString());
        }
    }

    @JavascriptInterface
    public void setPushToken() {
        JSONObject json = new JSONObject();
        try {
            json.put("result","100");
            json.put("token", this.mSession.getSessionValue(ReturnPSession.PREF_PUSH_TOKEN));
            json.put("os", "android");
            ((ReturnPMainActivity) ReturnpAndroidBridge.this.context).setBridgeResponse("sendPushTokenToServer", json.toString());
        }catch(JSONException e) {
            try {
                json.put("result", "300");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void getPhoneNumber() {
        String phoneNumberCountry = null;
        String phoneNumber = null;
        JSONObject json = new JSONObject();
        if (context.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            phoneNumberCountry = AndroidUtil.getPhoneNumber(this.context);
            phoneNumber = "0" + (phoneNumberCountry.substring(phoneNumberCountry.length() - 10, phoneNumberCountry.length()));
            try {
                json.put("result", "100");
                json.put("phoneNumber", phoneNumber);
                json.put("phoneNumberCountry", phoneNumberCountry);
            } catch (JSONException e) {
                e.printStackTrace();
                try {
                    json.put("result", "300");
                }catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }finally{
                ((ReturnPMainActivity) this.context).setBridgeResponse(null, json.toString());
            }
        } else {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_PHONE_STATE}, ReturnPMainActivity.PERMISSION_REQUEST_READ_PHONE_STATE);
        }
    }

    public void setBridgeResponse(String callbackName, String data){
        ((ReturnPMainActivity) this.context).setBridgeResponse(callbackName, data);
    }

}
