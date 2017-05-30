package com.appbar.matocham.applicationbar.applicationManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.appbar.matocham.applicationbar.widget.AppBarWidgetService;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.CollectionType;
import org.codehaus.jackson.map.type.TypeFactory;

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
            Log.d(TAG, "Loaded string: " + persistedWidgets);
            widgets = converter.readValue(persistedWidgets);
            Log.d(TAG,"After conversion: "+widgets);
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
        loadFromPrefs();
        Log.d(TAG, "Trying to remove widget " + widgetId);
        NewWidget widget = getWidget(widgetId);
        if (widget != null) {
            Log.d(TAG, "Widget " + widgetId + " removed");
            widgets.remove(widget);
        }
        storeInPrefs();
    }

    public NewWidget getWidget(int widgetId) {
        return getWidgetById(widgetId);
    }

    public void store() {
        storeInPrefs();
    }
}
