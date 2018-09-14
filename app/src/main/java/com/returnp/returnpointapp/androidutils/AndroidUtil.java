package com.returnp.returnpointapp.androidutils;

import android.content.Context;
import android.telephony.TelephonyManager;

public class AndroidUtil {
    public static String getPhoneNumber(Context context) {
        String phoneNumber = null;
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            if (telephony.getLine1Number() != null) {
                phoneNumber = telephony.getLine1Number();
            } else {
                if (telephony.getSimSerialNumber() != null) {
                    phoneNumber = telephony.getSimSerialNumber();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return phoneNumber;
    }
}
