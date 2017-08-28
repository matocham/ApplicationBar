package com.appbar.matocham.applicationbar.applicationManager;

import android.util.Log;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matocham on 30.05.2017.
 */

public class Widget {
    private static final String TAG = "Widget";
    private List<AppElement> applications;
    private String label;
    private int id;

    public Widget() {
        // constructor used by Jackson
    }

    public Widget(int id) {
        this.id = id;
        label = id + "";
        applications = new ArrayList<>();
    }

    public void addApp(String packageName) {
        Log.d(TAG, "Adding app " + packageName + " to widget " + id);
        AppElement existingApp = getApp(packageName);
        if (existingApp != null) {
            Log.d(TAG, "App already exists. App will be renewed");
            existingApp.setRemoveTimestamp(0);
        } else {
            Log.d(TAG, "App is new!");
            AppElement element = new AppElement(packageName, 0);
            applications.add(element);
        }
        Log.d(TAG, "State after add: " + applications);
    }

    public void removeApp(String packageName) {
        Log.d(TAG, "Removing app " + packageName + " from widget " + id);
        AppElement temp = new AppElement(packageName, 0);
        applications.remove(temp);
        Log.d(TAG, "State after remove: " + applications);
    }

    public void markAsRemoved(String packageName) {
        AppElement appElement = getApp(packageName);
        if (appElement != null) {
            Log.d(TAG, "Marking app " + appElement + " as removed in widget " + id);
            appElement.markAsRemoved();
        }
        Log.d(TAG, "State after markAsRemoved: " + applications);
    }

    private AppElement getApp(String packageName) {
        Log.d(TAG, "Looking for app " + packageName + " in widget " + id);
        int index = applications.indexOf(new AppElement(packageName, 0));
        if (index == -1) {
            Log.d(TAG, "App " + packageName + " not found in widget " + id);
            return null;
        }
        Log.d(TAG, "App " + packageName + " found in widget " + id);
        return applications.get(index);
    }

    public void markAsValid(String packageName) {
        AppElement appElement = getApp(packageName);
        if (appElement != null) {
            Log.d(TAG, "Marking app " + packageName + " as valid ing widget " + id);
            appElement.markAsValid();
        }
        Log.d(TAG, "State after markAsValid: " + applications);
    }

    @JsonIgnore
    public boolean isValid(String packageName) {
        AppElement appElement = getApp(packageName);
        if (appElement != null) {
            return !appElement.isRemoved();
        }
        return false;
    }

    public void renewIfValid(String packageName) {
        Log.d(TAG, "Trying to renew app " + packageName + " in widget " + id);
        AppElement appElement = getApp(packageName);
        if (appElement != null && appElement.isRemoved()) {
            if (appElement.isObsolote()) {
                Log.d(TAG, "App " + packageName + " is obsolete and will be removed from widget " + id);
                applications.remove(appElement);
            } else {
                Log.d(TAG, "App " + packageName + " renewed in widget " + id);
                appElement.markAsValid();
            }
        }
        Log.d(TAG, "State after renewIfValid: " + applications);
    }

    @JsonIgnore
    public List<AppElement> getValidApps() {
        List<AppElement> valid = new ArrayList<>();
        for (AppElement element : applications) {
            if (!element.isRemoved()) {
                valid.add(element);
            }
        }
        Log.d(TAG, "List of valid apps in widget " + id + ": " + valid);
        return valid;
    }

    public boolean contains(String packageName) {
        AppElement element = new AppElement(packageName, 0);
        return applications.contains(element);
    }

    public boolean cleanup() {
        List<AppElement> toRemove = new ArrayList<>();
        for (AppElement element : applications) {
            if (element.isObsolote()) {
                toRemove.add(element);
            }
        }
        applications.removeAll(toRemove);
        return !toRemove.isEmpty();
    }

    public List<AppElement> getApplications() {
        return applications;
    }

    public void setApplications(List<AppElement> applications) {
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

    @Override
    public String toString() {
        return applications.toString() + " id:" + id + " label:" + label;
    }
}
