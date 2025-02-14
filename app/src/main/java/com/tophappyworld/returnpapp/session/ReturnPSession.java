package com.tophappyworld.returnpapp.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

public class ReturnPSession {
    public static String PREF_MUST_PHSU_TOKEN_SEND= "must_push_token_send";
    public static String PREF_OS = "os";
    public static String PREF_PUSH_TOKEN= "push_token";
    public static String PREF_USER_NAME = "user_name";
    public static String PREF_USER_EMAIL = "user_email";
    public static String PREF_USER_AUTH_TOKEN = "user_auth_token";
    public static String PREF_RECOMMENDER_INST = "recommneder_inst";
    public static String PREF_RECOMMENDER_EMAIL = "recommneder_email";
    public static String PREF_FIRST_RUN = "first_run";
    public static String PREF_VERSION= "version";

    private SharedPreferences sharedPreferences;
    public ReturnPSession(Context context){
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setData(String key, String value){
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

   public String getData(String key, String defaultValue){
       return this.sharedPreferences.getString(key, defaultValue);
   }

    public void removeData(String key){
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.remove(key);
        editor.commit();
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
        editor.remove(ReturnPSession.PREF_USER_AUTH_TOKEN);
        editor.commit();
    }

    public void setUserAutoToken(String userAuthToken){
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString(ReturnPSession.PREF_USER_AUTH_TOKEN, userAuthToken);
        editor.commit();
    }

    public void setUserSession(String userName, String userEmail, String userAuthToken){
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString(ReturnPSession.PREF_USER_NAME, userName);
        editor.putString(ReturnPSession.PREF_USER_EMAIL, userEmail);
        editor.putString(ReturnPSession.PREF_USER_AUTH_TOKEN, userAuthToken);
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
            session.put(ReturnPSession.PREF_USER_AUTH_TOKEN, this.sharedPreferences.getString(ReturnPSession.PREF_USER_AUTH_TOKEN,null));
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
        return this.sharedPreferences.getString(ReturnPSession.PREF_USER_AUTH_TOKEN,null);
    }
}
