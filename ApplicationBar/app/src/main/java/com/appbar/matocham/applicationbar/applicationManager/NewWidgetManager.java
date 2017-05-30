package com.appbar.matocham.applicationbar.applicationManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.appbar.matocham.applicationbar.widget.AppBarWidgetService;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by matocham on 30.05.2017.
 */

public class NewWidgetManager {
    private static final String STORAGE_KEY = "matocham.applicationbar.WIDGET_APPS_KEY";
    private static final String TAG = "NewWidgetManager";

    private ObjectMapper converter;
    private List<NewWidget> widgets;
    private Context context;
    private SharedPreferences storage;

    public NewWidgetManager(Context context) {
        converter = new ObjectMapper();
        widgets = new ArrayList<>();
        this.context = context;
        storage = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private void loadFromPrefs() {
        try {
            int[] widgetsIds = AppBarWidgetService.getAppWidgetIds(context);
            String persistedWidgets = storage.getString(STORAGE_KEY, "[]");
            Log.d(TAG, "Loaded string: " + persistedWidgets);
            widgets = converter.readValue(persistedWidgets, ArrayList.class);

            for (int widgetId : widgetsIds) {
                boolean found = false;
                for (NewWidget widget : widgets) {
                    if (widget.getId() == widgetId) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    widgets.add(new NewWidget(widgetId));
                    storeInPrefs();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void storeInPrefs() {
        try {
            String widgetsStoreString = converter.writeValueAsString(widgets);
            Log.d(TAG, "String to save: " + widgetsStoreString);
            storage.edit().putString(STORAGE_KEY, widgetsStoreString).apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addApp(String packageName, int widgetId) {
        loadFromPrefs();
        NewWidget widget = getWidgetById(widgetId);
        if (widget != null) {
            widget.addApp(packageName);
        }
        storeInPrefs();
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
        loadFromPrefs();
        NewWidget widget = getWidgetById(widgetId);
        if (widget != null) {
            widget.removeApp(packageName);
        }
        storeInPrefs();
    }

    public void markAsRemoved(String packageName, int widgetId) {
        loadFromPrefs();
        NewWidget widget = getWidgetById(widgetId);
        if (widget != null) {
            widget.markAsRemoved(packageName);
        }
        storeInPrefs();
    }

    public void markAsValid(String packageName, int widgetId) {
        loadFromPrefs();
        NewWidget widget = getWidgetById(widgetId);
        if (widget != null) {
            widget.markAsValid(packageName);
        }
        storeInPrefs();
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
        loadFromPrefs();
        NewWidget widget = getWidgetById(widgetId);
        if (widget != null) {
            widget.renewIfValid(packageName);
        }
        storeInPrefs();
    }

    public List<AppElement> getValidApps(int widgetId) {
        loadFromPrefs();
        NewWidget widget = getWidgetById(widgetId);
        if (widget != null) {
            return widget.getValidApps();
        }
        return Collections.EMPTY_LIST;
    }

    public boolean contains(String packageName, int widgetId){
        loadFromPrefs();
        NewWidget widget = getWidgetById(widgetId);
        if (widget != null) {
            return widget.contains(packageName);
        }
        return false;
    }
}
