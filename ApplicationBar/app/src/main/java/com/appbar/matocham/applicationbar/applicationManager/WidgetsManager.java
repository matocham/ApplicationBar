package com.appbar.matocham.applicationbar.applicationManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.appbar.matocham.applicationbar.widget.AppBarWidgetService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mateusz on 10.01.2017
 * <p>
 * Class to manage adding and deleting apps from widget. Most methods are static and require context in parameters
 * , only one listen to switch changes in list
 */

public class WidgetsManager {
    public static final String TAG = "WidgetsManager";

    private static Map<Integer, Widget> widgets = new HashMap<>();

    public static void loadWidgets(Context context) {
        loadWidgets(AppBarWidgetService.getAppWidgetIds(context), context);
    }

    public static void loadWidgets(int[] widgetId, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        for (int id : widgetId) {
            Widget widget = retrive(preferences, id);
            if (widget != null) {
                widgets.put(id, widget);
                Log.d(TAG, "Loaded widget with id " + id);
            }
        }
    }

    public static void add(int widgetId, Context context) {
        Widget initialWidget = new Widget(PreferenceManager.getDefaultSharedPreferences(context), widgetId);
        if(initialWidget.getId() == -1){
            initialWidget = new Widget(widgetId);
            initialWidget.store(PreferenceManager.getDefaultSharedPreferences(context));
        }
        widgets.put(widgetId, initialWidget);
    }

    public static void addAppToWidget(String appkey, int widgetId, Context context) {
        Widget widget = getWidget(widgetId);
        if (widget == null) {
            widget = new Widget(widgetId);
        }
        widget.addApp(appkey);
        Log.d(TAG, "Adding app " + appkey + " result is " + widget.getApplications().toString());
        widget.store(PreferenceManager.getDefaultSharedPreferences(context));
    }

    public static void setWidgetLabel(String label, int widgetId, Context context){
        Widget widget = getWidget(widgetId);
        widget.setLabel(label);
        widget.store(PreferenceManager.getDefaultSharedPreferences(context));
    }

    public static void disposeWidget(int widgetId, Context context){
        Widget widget = getWidget(widgetId);
        widget.dispose(PreferenceManager.getDefaultSharedPreferences(context));
    }

    public static void removeAppFromWidget(String appkey, int widgetId, Context context) {
        Widget widget = getWidget(widgetId);
        if (widget == null) {
            return;
        }
        widget.deleteApp(appkey);
        Log.d(TAG,"removing app "+appkey+" result is "+widget.getApplications().toString());
        widget.store(PreferenceManager.getDefaultSharedPreferences(context));
    }

    private static Widget retrive(SharedPreferences container, int widgetId) {
        Widget widget = new Widget(container, widgetId);
        if (widget.getId() == -1) {
            return null;
        }
        return widget;
    }

    public static boolean hasWidget(int widgetId){
        return widgets.containsKey(widgetId);
    }

    public static boolean isWidgetApp(String packageName, int widgetId) {
        Widget widget = getWidget(widgetId);
        Log.d(TAG,packageName+ " for widget "+widgetId+" is widget app?");
        if (widget != null) {
            return widget.isWigetApp(packageName);
        }
        return false;
    }

    public static List<AppInfo> getMarkedApps(Context context, int widgetId) {
        Widget widget = getWidget(widgetId);
        if (widget == null) {
            return new ArrayList<>();
        }

        PackageManager packageManager = context.getPackageManager();
        List<AppInfo> widgetApps = new ArrayList<>();
        for (String app : widget.getApplications()) {
            AppInfo aInfo = AppInfo.getAppInfo(packageManager, app);
            if (aInfo != null) {
                widgetApps.add(aInfo);
            }
        }

        Collections.sort(widgetApps);
        return widgetApps;
    }

    public static Widget getWidget(int widgetId) {
        if(!widgets.containsKey(widgetId)){ // TODO validate if required
            widgets.put(widgetId,new Widget(widgetId));
        }
        return widgets.get(widgetId);
    }
}
