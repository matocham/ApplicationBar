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
@Deprecated
public class WidgetsManager {
    public static final String TAG = "WidgetsManager";
    private static WidgetsManager instance;

    private  Map<Integer, Widget> widgets = new HashMap<>();
    private Context context;

    public static WidgetsManager getInstance(){
        if( instance == null){
            instance = new WidgetsManager();
        }
        return  instance;
    }

    private WidgetsManager(){}

    public static WidgetsManager withContext(Context context){
        WidgetsManager manager = getInstance();
        manager.context = context;
        return manager;
    }

    public WidgetsManager loadWidgets() {
        loadWidgets(AppBarWidgetService.getAppWidgetIds(context));
        return this;
    }

    public void loadWidgets(int[] widgetId) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        for (int id : widgetId) {
            Widget widget = retrive(preferences, id);
            if (widget != null) {
                widgets.put(id, widget);
                Log.d(TAG, "Loaded widget with id " + id);
            }
        }
    }

    public void add(int widgetId) {
        Widget initialWidget = new Widget(PreferenceManager.getDefaultSharedPreferences(context), widgetId);
        if(initialWidget.getId() == -1){
            initialWidget = new Widget(widgetId);
            initialWidget.store(PreferenceManager.getDefaultSharedPreferences(context));
        }
        widgets.put(widgetId, initialWidget);
    }

    public void addAppToWidget(String appkey, int widgetId) {
        Widget widget = getWidget(widgetId);
        if (widget == null) {
            widget = new Widget(widgetId);
        }
        widget.addApp(appkey);
        Log.d(TAG, "Adding app " + appkey + " result is " + widget.getApplications().toString());
        widget.store(PreferenceManager.getDefaultSharedPreferences(context));
    }

    public void setWidgetLabel(String label, int widgetId){
        Widget widget = getWidget(widgetId);
        widget.setLabel(label);
        widget.store(PreferenceManager.getDefaultSharedPreferences(context));
    }

    public void disposeWidget(int widgetId){
        Widget widget = getWidget(widgetId);
        widget.dispose(PreferenceManager.getDefaultSharedPreferences(context));
    }

    public void removeAppFromWidget(String appkey, int widgetId) {
        Widget widget = getWidget(widgetId);
        if (widget == null) {
            return;
        }
        widget.deleteApp(appkey);
        Log.d(TAG,"removing app "+appkey+" result is "+widget.getApplications().toString());
        widget.store(PreferenceManager.getDefaultSharedPreferences(context));
    }

    private Widget retrive(SharedPreferences container, int widgetId) {
        Widget widget = new Widget(container, widgetId);
        if (widget.getId() == -1) {
            return null;
        }
        return widget;
    }

    public boolean hasWidget(int widgetId){
        return widgets.containsKey(widgetId);
    }

    public boolean isWidgetApp(String packageName, int widgetId) {
        Widget widget = getWidget(widgetId);
        Log.d(TAG,packageName+ " for widget "+widgetId+" is widget app?");
        if (widget != null) {
            return widget.isWigetApp(packageName);
        }
        return false;
    }

    public List<AppInfo> getMarkedApps(int widgetId) {
        Widget widget = getWidget(widgetId);
        if (widget == null) {
            return new ArrayList<>();
        }

        PackageManager packageManager = context.getPackageManager();
        List<AppInfo> widgetApps = new ArrayList<>();
        for (AppElement app : widget.getApplications()) {
            AppInfo aInfo = null;
            if (!app.isDeleted()) {
                aInfo = AppInfo.getAppInfo(packageManager, app.getName());
            } else {
                if(app.isObsolote()){
                    Log.d(TAG,"App "+app.getName()+" is obsolote and will be removed");
                    removeAppFromWidget(app.getName(),widgetId);
                }
            }
            if (aInfo != null) {
                widgetApps.add(aInfo);
            }
        }

        Collections.sort(widgetApps);
        return widgetApps;
    }

    public void markAsDeleted(String packageName, int widgetId){
        Widget widget = getWidget(widgetId);
        if (widget == null) {
            return;
        }
        widget.markAsDeleted(packageName);
        Log.d(TAG,"marking as removed app "+packageName+" result is "+widget.getApplications().toString());
        widget.store(PreferenceManager.getDefaultSharedPreferences(context));
    }

    public void markAsFreshOrDelete(String packageName, int widgetId){
        Widget widget = getWidget(widgetId);
        if (widget == null) {
            return;
        }
        Log.d(TAG,"marking as removed or updated app "+packageName);
        widget.markAsFreshOrDelete(packageName);
        Log.d(TAG,"marking as removed or updated app "+packageName+" result is "+widget.getApplications().toString());
        widget.store(PreferenceManager.getDefaultSharedPreferences(context));
    }

    public Widget getWidget(int widgetId) {
        if(!widgets.containsKey(widgetId)){ // TODO validate if required
            widgets.put(widgetId,new Widget(widgetId));
        }
        return widgets.get(widgetId);
    }
}
