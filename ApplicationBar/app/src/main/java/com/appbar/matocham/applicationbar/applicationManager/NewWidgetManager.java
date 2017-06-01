package com.appbar.matocham.applicationbar.applicationManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.appbar.matocham.applicationbar.widget.AppBarWidgetService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by matocham on 30.05.2017.
 */

public class NewWidgetManager {
    private static final String STORAGE_KEY = "matocham.applicationbar.WIDGET_APPS_KEY";
    private static final String TAG = "NewWidgetManager";
    private static final Lock LOCK = new ReentrantLock();

    private JsonConverter converter;
    private List<NewWidget> widgets;
    private Context context;
    private SharedPreferences storage;

    public NewWidgetManager(Context context) {
        converter = new JsonConverter();
        widgets = new ArrayList<>();
        this.context = context;
        storage = PreferenceManager.getDefaultSharedPreferences(context);
        loadFromPrefs();
    }

    private void loadFromPrefs() {
        try {
            int[] widgetsIds = AppBarWidgetService.getAppWidgetIds(context);
            String persistedWidgets = storage.getString(STORAGE_KEY, "[]");
            widgets = converter.readValue(persistedWidgets);
            Log.d(TAG, "Loaded widgets: " + widgets);
            createMissingWidgets(widgetsIds);
            removeObsoleteWidgets(widgetsIds);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createMissingWidgets(int[] widgetsIds) {
        for (int widgetId : widgetsIds) {
            boolean found = false;
            for (NewWidget widget : widgets) {
                if (widget.getId() == widgetId) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                Log.d(TAG, "Widget with id " + widgetId + " is not stored. Adding as new!");
                widgets.add(new NewWidget(widgetId));
                storeInPrefs();
            }
        }
    }

    private void removeObsoleteWidgets(int[] widgetsIds) {
        List<NewWidget> toRemove = new ArrayList<>();
        for (NewWidget widget : widgets) {
            boolean found = false;
            for (int widgetId : widgetsIds) {
                if (widget.getId() == widgetId) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                Log.d(TAG, "Widget with id " + widget.getId() + " is obsolete and will be removed");
                toRemove.add(widget);
            }
        }
        widgets.removeAll(toRemove);
        if (!toRemove.isEmpty()) {
            storeInPrefs();
        }
    }

    private void storeInPrefs() {
        try {
            String widgetsStoreString = converter.writeValueAsString(widgets);
            Log.d(TAG, "Saving dataset");
            storage.edit().putString(STORAGE_KEY, widgetsStoreString).apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addApp(String packageName, int widgetId) {
        lockAndRefresh();
        NewWidget widget = getWidgetById(widgetId);
        if (widget != null) {
            widget.addApp(packageName);
        }
        storeAndReleaseLock();
    }

    @Nullable
    private NewWidget getWidgetById(int widgetId) {
        NewWidget widget = null;
        for (NewWidget wid : widgets) {
            if (wid.getId() == widgetId) {
                widget = wid;
            }
        }
        if (widget == null) {
            Log.d(TAG, "Widget does not exist!");
        }
        return widget;
    }

    public void removeApp(String packageName, int widgetId) {
        lockAndRefresh();
        NewWidget widget = getWidgetById(widgetId);
        if (widget != null) {
            widget.removeApp(packageName);
        }
        storeAndReleaseLock();
    }

    public void markAsRemoved(String packageName, int widgetId) {
        lockAndRefresh();
        NewWidget widget = getWidgetById(widgetId);
        if (widget != null) {
            widget.markAsRemoved(packageName);
        }
        storeAndReleaseLock();
    }

    public void markAsValid(String packageName, int widgetId) {
        lockAndRefresh();
        NewWidget widget = getWidgetById(widgetId);
        if (widget != null) {
            widget.markAsValid(packageName);
        }
        storeAndReleaseLock();
    }

    public boolean isValid(String packageName, int widgetId) {
        loadFromPrefs();
        NewWidget widget = getWidgetById(widgetId);
        if (widget != null) {
            return widget.isValid(packageName);
        }
        return false;
    }

    public void renewIfValid(String packageName, int widgetId) {
        lockAndRefresh();
        NewWidget widget = getWidgetById(widgetId);
        if (widget != null) {
            widget.renewIfValid(packageName);
        }
        storeAndReleaseLock();
    }

    public List<AppElement> getValidApps(int widgetId) {
        loadFromPrefs();
        NewWidget widget = getWidgetById(widgetId);
        if (widget != null) {
            return widget.getValidApps();
        }
        return Collections.EMPTY_LIST;
    }

    public boolean contains(String packageName, int widgetId) {
        loadFromPrefs();
        NewWidget widget = getWidgetById(widgetId);
        if (widget != null) {
            return widget.contains(packageName);
        }
        return false;
    }

    public boolean contains(int widgetId) {
        for (NewWidget widget : widgets) {
            if (widget.getId() == widgetId) {
                return true;
            }
        }
        return false;
    }

    public void remove(int widgetId) {
        lockAndRefresh();
        Log.d(TAG, "Trying to remove widget " + widgetId);
        NewWidget widget = getWidget(widgetId);
        if (widget != null) {
            Log.d(TAG, "Widget " + widgetId + " removed");
            widgets.remove(widget);
        }
        storeAndReleaseLock();
    }

    public NewWidget getWidget(int widgetId) {
        return getWidgetById(widgetId);
    }

    public void store() {
        storeInPrefs();
    }

    public void refresh() {
        loadFromPrefs();
    }

    public void getLock() {
        LOCK.lock();
        Log.d(TAG,"Lock acquired in thread "+Thread.currentThread().getName());
    }

    public void releaseLock() {
        LOCK.unlock();
        Log.d(TAG,"Lock released in thread "+Thread.currentThread().getName());
    }

    public void lockAndRefresh() {
        getLock();
        refresh();
    }

    public void storeAndReleaseLock() {
        store();
        releaseLock();
    }
}
