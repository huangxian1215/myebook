package com.example.myebook;

import android.app.Application;
import android.content.SharedPreferences;

public class MainApplication extends Application {
    private static MainApplication mApp;
    public static MainApplication getInstance(){return mApp;}
    public static String gCode = "utf-8";
    @Override
    public void onCreate(){
        super.onCreate();
        mApp = this;
    }

    //先只支持返回string
    public String getLocalStore(String key){
        SharedPreferences shareinfo = getSharedPreferences("myebook", MODE_PRIVATE);
        SharedPreferences.Editor editor = shareinfo.edit();
        return shareinfo.getString(key, "");
    }
    //先只支持string
    public void setLocalStore(String key, String value){
        SharedPreferences shareinfo = getSharedPreferences("myebook", MODE_PRIVATE);
        SharedPreferences.Editor editor = shareinfo.edit();
        editor.putString(key, value);
        editor.commit();
    }
}
