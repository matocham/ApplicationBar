package com.appbar.matocham.applicationbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Message;
import android.preference.PreferenceManager;

/**
 * Created by Mateusz on 10.01.2017.
 */

public class Utils {
    public static final String WIDGET_APPS_KEY = "matocham.applicationbar.WIDGET_APPS_KEY";

    public static void setAllAppsSwitchState(boolean state, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean("display_all_apps",state).commit();
    }

    public static boolean getAllAppsSwitchState(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("display_all_apps",false);
    }

    public static String getWidgetAppsValues(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(WIDGET_APPS_KEY,"");
    }

    public static void addAppToWidget(String appkey, Context context){
        String apps = getWidgetAppsValues(context);
        if(apps.contains(appkey)){
            return;
        }
        apps+=";"+appkey;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(WIDGET_APPS_KEY,apps).commit();
    }

    public static void removeAppFromWidget(String appkey, Context context){
        String apps = getWidgetAppsValues(context);
        if(!apps.contains(appkey)){
            return;
        }
        apps= apps.replace(appkey,"");
        apps.replace(";;",";");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(WIDGET_APPS_KEY,apps).commit();
    }

    public static String[] getWigetAppsTable(Context context){
        String apps = getWidgetAppsValues(context);
        return apps.split(";");
    }

    public static boolean isWigetApp(String appkey, Context context){
        String apps = getWidgetAppsValues(context);
        return apps.contains(appkey);
    }

    public static ProgressDialog showProgressDialog(Context context, String title, String message) {
        ProgressDialog dialog = ProgressDialog.show(context, title, message, true, false);
        return dialog;
    }

    public static Message createMessage(int what, Object object) {
        Message msg = new Message();
        msg.what = what;
        msg.obj = object;
        return msg;
    }
}
