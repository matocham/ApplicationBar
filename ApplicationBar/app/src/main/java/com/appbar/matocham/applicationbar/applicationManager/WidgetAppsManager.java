package com.appbar.matocham.applicationbar.applicationManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.appbar.matocham.applicationbar.interfaces.OnSwitchStateChangedListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Mateusz on 10.01.2017
 *
 * Class to manage adding and deleting apps from widget. Most methods are static and require context in parameters
 * , only one listen to switch changes in list
 */

public class WidgetAppsManager implements OnSwitchStateChangedListener {
    public static final String WIDGET_APPS_KEY = "matocham.applicationbar.WIDGET_APPS_KEY";
    public static final String TAG="WidgetAppsManager";
    private Context context;

    public WidgetAppsManager(Context context) {
        this.context = context;
    }

    /**
     * get apps added to display in widget
     * @param context
     * @return
     */
    public static List<AppInfo> getMarkedApps(Context context) {
        String[] apps = getWigetAppsTable(context);
        PackageManager packageManager = context.getPackageManager();
        List<AppInfo> widgetApps = new ArrayList<>();
        for (String app : apps) {
            try {
                AppInfo aInfo = AppInfo.getAppInfo(packageManager, packageManager.getApplicationInfo(app, 0));
                widgetApps.add(aInfo);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                removeAppFromWidget(app, context);
            }
        }

        Collections.sort(widgetApps);
        return widgetApps;
    }

    //****************** edit widget apps ************************
    public static void addAppToWidget(String appkey, Context context) {
        String apps = getWidgetAppsValues(context);
        if (apps.contains(appkey)) {
            return;
        }
        if(apps.length()!=0){
            apps += ";";
        }
        apps += appkey;
        Log.e(TAG,"adding "+appkey+" result "+apps);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(WIDGET_APPS_KEY, apps).commit();
    }

    public static void removeAppFromWidget(String appkey, Context context) {
        String apps = getWidgetAppsValues(context);
        if (!apps.contains(appkey)) {
            return;
        }
        apps = apps.replace(appkey, "");
        if(apps.startsWith(";")){
            apps = apps.substring(1);
        }
        apps = apps.replace(";;", ";");
        Log.e(TAG,"removing "+appkey+" result "+apps);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(WIDGET_APPS_KEY, apps).commit();
    }

    //****************** get widget apps ************************
    public static String getWidgetAppsValues(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(WIDGET_APPS_KEY, "");
    }


    public static String[] getWigetAppsTable(Context context) {
        String apps = getWidgetAppsValues(context);
        return apps.split(";");
    }

    public static boolean isWigetApp(String appkey, Context context) {
        String apps = getWidgetAppsValues(context);
        Log.e(TAG,"is widget app? "+apps.contains(appkey));
        return apps.contains(appkey);
    }

    //*************************************************************

    @Override
    public void switchChanged(AppInfo app, boolean state) {
        if (state) {
            addAppToWidget(app.toString(), context);
        } else {
            removeAppFromWidget(app.toString(), context);
        }
    }
}
