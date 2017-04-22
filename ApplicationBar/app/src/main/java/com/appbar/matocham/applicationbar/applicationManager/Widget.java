package com.appbar.matocham.applicationbar.applicationManager;

import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Mateusz on 15.01.2017.
 */

public class Widget {

    public static final String ID_FIELD_NAME = "ID";
    public static final String LABEL_FIELD_NAME = "LABEL";
    public static final String APPLICATIONS_FIELD_NAME = "APPLICATIONS";
    public static final String APP_LIST_SEPARATOR = ";";
    public static final String WIDGET_APPS_KEY_PREFIX = "matocham.applicationbar.WIDGET_APPS_KEY_";
    public static final String TAG = "Widget";
    private CopyOnWriteArrayList<AppElement> applications;
    private String label;
    private int id;

    public Widget() {
        Log.d(TAG, "CREATING EMPTY WIDGET");
        applications = new CopyOnWriteArrayList<>();
        label = "";
    }

    ;

    public Widget(int id) {
        this();
        Log.d("WIDGET", "CREATING WIDGET with id");
        this.id = id;
        label = "Widget " + id;
    }

    public Widget(SharedPreferences preferences, int id) {
        Log.d("WIDGET", "CREATING wIDGET from prefs");
        this.id = preferences.getInt(getStorageKey(WIDGET_APPS_KEY_PREFIX, ID_FIELD_NAME, id), -1);
        this.label = preferences.getString(getStorageKey(WIDGET_APPS_KEY_PREFIX, LABEL_FIELD_NAME, id), "");
        String apps = preferences.getString(getStorageKey(WIDGET_APPS_KEY_PREFIX, APPLICATIONS_FIELD_NAME, id), "");
        restoreAppsList(apps);
        Log.d(TAG, id + " " + label + " " + applications.toString());
    }

    public Widget(String label, int id) {
        this(id);
        this.label = label;
        Log.d(TAG, "CREATING WIDGET with label");
    }

    public void dispose(SharedPreferences container) {
        container.edit().remove(getStorageKey(WIDGET_APPS_KEY_PREFIX, ID_FIELD_NAME, id))
                .remove(getStorageKey(WIDGET_APPS_KEY_PREFIX, APPLICATIONS_FIELD_NAME, id))
                .remove(getStorageKey(WIDGET_APPS_KEY_PREFIX, LABEL_FIELD_NAME, id)).apply();
    }

    public void store(SharedPreferences container) {
        Log.d(TAG, "storing widget " + applications.toString());
        String storableApps = getAppsStorageStrig();
        container.edit().putString(getStorageKey(WIDGET_APPS_KEY_PREFIX, LABEL_FIELD_NAME, id), label)
                .putString(getStorageKey(WIDGET_APPS_KEY_PREFIX, APPLICATIONS_FIELD_NAME, id), storableApps)
                .putInt(getStorageKey(WIDGET_APPS_KEY_PREFIX, ID_FIELD_NAME, id), id).apply();
    }

    public void addApp(String appPackageName) {
        if (!applications.contains(appPackageName)) {
            applications.add(new AppElement(appPackageName, 0));
        }
    }

    public void deleteApp(String appPackageName) {
        applications.remove(new AppElement(appPackageName, 0));
    }

    public void markAsDeleted(String appPackageName) {
        if (isWigetApp(appPackageName)) {
            AppElement appElement = applications.get(applications.indexOf(new AppElement(appPackageName, 0)));
            appElement.setDeleteTimestamp(System.currentTimeMillis());
            Log.d(TAG,"App "+appPackageName+" marked as deleted "+appElement.getDeleteTimestamp());
        }
    }

    public void markAsFreshOrDelete(String appPackageName) {
        boolean hasElement = applications.contains(new AppElement(appPackageName, 0));
        Log.d(TAG, "app "+appPackageName+" belongs to widget? "+hasElement);
        if (hasElement) {
            AppElement appElement = applications.get(applications.indexOf(new AppElement(appPackageName, 0)));
            if (appElement.isObsolote()) {
                deleteApp(appPackageName);
                Log.d(TAG,"Deleted "+appPackageName);
            } else {
                Log.d(TAG,"Updated "+appPackageName);
                appElement.setDeleteTimestamp(0);
            }
        }
    }

    public boolean isWigetApp(String appPackageName) {
        boolean hasElement = applications.contains(new AppElement(appPackageName, 0));
        if (!hasElement) {
            return false;
        }
        AppElement appElement = applications.get(applications.indexOf(new AppElement(appPackageName, 0)));
        return !appElement.isDeleted();
    }

    private String getAppsStorageStrig() {
        StringBuilder apps = new StringBuilder();
        for (AppElement appPackage : applications) {
            apps.append(appPackage.toString()).append(APP_LIST_SEPARATOR);
        }
        if (apps.length() > 0) {
            apps.deleteCharAt(apps.length() - 1);
        }
        return apps.toString();
    }

    private void restoreAppsList(String appPackages) {
        applications = new CopyOnWriteArrayList<>();
        Log.d(TAG, "Packages string: "+appPackages);
        String[] splittedApps = appPackages.split(APP_LIST_SEPARATOR);
        for (String appElement : splittedApps) {
            AppElement element = new AppElement(appElement);
            applications.add(element);
        }
    }

    public List<AppElement> getApplications() {
        return applications;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private static String getStorageKey(String prefix, String field, int id) {
        return prefix + field + id;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Widget)) {
            return false;
        }

        Widget otherWdget = (Widget) o;
        return this.id == otherWdget.id;
    }

    @Override
    public int hashCode() {
        return id * 13;
    }
}
