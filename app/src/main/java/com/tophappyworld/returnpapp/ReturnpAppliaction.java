package com.tophappyworld.returnpapp;

import android.app.Activity;
import android.app.Application;

public class ReturnpAppliaction extends Application {
    private Activity act;
    public void setMainActivity(Activity act){
        this.act = act;
    }

    public Activity getMainActivity(){
        return act;
    }
}

