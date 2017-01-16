package com.appbar.matocham.applicationbar.applicationManager;

import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Mateusz on 15.01.2017.
 */

public class Widget {

    public static final String ID_FIELD_NAME = "ID";
    public static final String LABEL_FIELD_NAME = "LABEL";
    public static final String APPLICATIONS_FIELD_NAME = "APPLICATIONS";
    public static final String APP_LIST_SEPARATOR = ";";
    public static final String WIDGET_APPS_KEY_PREFIX = "matocham.applicationbar.WIDGET_APPS_KEY_";
    private List<String> applications;
    private String label;
    private int id;

    public Widget() {
        Log.d("WIDGET","CREATING EMPTY WIDGET");
        applications = new ArrayList<>();
        label="";
    }

    ;

    public Widget(int id) {
        this();
        Log.d("WIDGET","CREATING WIDGET with id");
        this.id = id;
    }

    public Widget(SharedPreferences preferences, int id) {
        Log.d("WIDGET","CREATING wIDGET from prefs");
        this.id = preferences.getInt(getStorageKey(WIDGET_APPS_KEY_PREFIX, ID_FIELD_NAME, id), -1);
        this.label = preferences.getString(getStorageKey(WIDGET_APPS_KEY_PREFIX, LABEL_FIELD_NAME, id), "");
        String apps = preferences.getString(getStorageKey(WIDGET_APPS_KEY_PREFIX, APPLICATIONS_FIELD_NAME, id), "");
        restoreAppsList(apps);
        Log.d("WIDGET",id+" "+label+" "+applications.toString());
    }

    public Widget(String label, int id) {
        this(id);
        this.label = label;
        Log.d("WIDGET","CREATING WIDGET with label");
    }

    public void dispose(SharedPreferences container) {
        container.edit().remove(getStorageKey(WIDGET_APPS_KEY_PREFIX, ID_FIELD_NAME, id))
                .remove(getStorageKey(WIDGET_APPS_KEY_PREFIX, APPLICATIONS_FIELD_NAME, id))
                .remove(getStorageKey(WIDGET_APPS_KEY_PREFIX, LABEL_FIELD_NAME, id)).commit();
    }

    public void store(SharedPreferences container) {
        Log.d("WIDGET","storing widget "+applications.toString());
        String storableApps = getAppsStorageStrig();
        container.edit().putString(getStorageKey(WIDGET_APPS_KEY_PREFIX, LABEL_FIELD_NAME, id), label)
                .putString(getStorageKey(WIDGET_APPS_KEY_PREFIX, APPLICATIONS_FIELD_NAME, id), storableApps)
                .putInt(getStorageKey(WIDGET_APPS_KEY_PREFIX, ID_FIELD_NAME, id), id).commit();
    }

    public void addApp(String appPackageName) {
        if (!applications.contains(appPackageName)) {
            applications.add(appPackageName);
        }
    }

    public void deleteApp(String appPackageName) {
        applications.remove(appPackageName);
    }

    public boolean isWigetApp(String appPackageName) {
        return applications.contains(appPackageName);
    }

    private String getAppsStorageStrig() {
        StringBuilder apps = new StringBuilder();
        for (String appPackage : applications) {
            apps.append(appPackage).append(APP_LIST_SEPARATOR);
        }
        if (apps.length() > 0) {
            apps.deleteCharAt(apps.length() - 1);
        }
        return apps.toString();
    }

    private void restoreAppsList(String appPackages) {
        String[] splittedApps = appPackages.split(APP_LIST_SEPARATOR);
        applications = new ArrayList<>(Arrays.asList(splittedApps));
    }

    public List<String> getApplications() {
        return applications;
    }

    public void setApplications(List<String> applications) {
        this.applications = applications;
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
