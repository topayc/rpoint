package com.returnp.app.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.returnp.app.bridges.ReturnpAndroidBridge;

import org.json.JSONException;
import org.json.JSONObject;

public class ReturnPSession {
    public static String PREF_USER_NAME = "user_name";
    public static String PREF_USER_EMAIL = "user_email";
    public static String PREF_USER_AUTO_TOKEN = "user_autu_token";

    private SharedPreferences sharedPreferences;
    public ReturnPSession(Context context){
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setUserName(String userName){
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString(ReturnPSession.PREF_USER_NAME, userName);
        editor.commit();
    }

    public void setUserEmail(String userEmail){
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString(ReturnPSession.PREF_USER_EMAIL, userEmail);
        editor.commit();
    }

    public void clearUserSession(){
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.remove(ReturnPSession.PREF_USER_NAME);
        editor.remove(ReturnPSession.PREF_USER_EMAIL);
        editor.remove(ReturnPSession.PREF_USER_AUTO_TOKEN);
        editor.commit();
    }

    public void setUserAutoToken(String userAuthToken){
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString(ReturnPSession.PREF_USER_AUTO_TOKEN, userAuthToken);
        editor.commit();
    }

    public void setUserSession(String userName, String userEmail, String userAuthToken){
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString(ReturnPSession.PREF_USER_NAME, userName);
        editor.putString(ReturnPSession.PREF_USER_EMAIL, userEmail);
        editor.putString(ReturnPSession.PREF_USER_AUTO_TOKEN, userAuthToken);
        editor.commit();
    }
    public String getSessionValue(String key){
        return this.sharedPreferences.getString(key, "");
    }

    public JSONObject getUserSession(){
        JSONObject session = new JSONObject();
        try {
            session.put(ReturnPSession.PREF_USER_NAME, this.sharedPreferences.getString(ReturnPSession.PREF_USER_NAME, null));
            session.put(ReturnPSession.PREF_USER_EMAIL, this.sharedPreferences.getString(ReturnPSession.PREF_USER_EMAIL,null));
            session.put(ReturnPSession.PREF_USER_AUTO_TOKEN, this.sharedPreferences.getString(ReturnPSession.PREF_USER_AUTO_TOKEN,null));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return session;
    }

    public String getUserName(){
        return this.sharedPreferences.getString(ReturnPSession.PREF_USER_NAME,null);
    }

    public String getUserEmail(){
        return this.sharedPreferences.getString(ReturnPSession.PREF_USER_EMAIL,null);
    }

    public String getUserAutoToken(){
        return this.sharedPreferences.getString(ReturnPSession.PREF_USER_AUTO_TOKEN,null);
    }
}
