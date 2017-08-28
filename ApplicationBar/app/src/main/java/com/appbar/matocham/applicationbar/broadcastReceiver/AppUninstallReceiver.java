package com.appbar.matocham.applicationbar.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.appbar.matocham.applicationbar.applicationManager.WidgetManager;
import com.appbar.matocham.applicationbar.widget.AppBarWidgetService;

/**
 * Created by Mateusz on 12.01.2017.
 */

public class AppUninstallReceiver extends BroadcastReceiver {

    private static final String TAG = "AppUninstallReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "package removed: " + intent.getDataString());
        String packageName = intent.getDataString().substring(intent.getDataString().indexOf(":") + 1);
        int[] widgetIds = AppBarWidgetService.getAppWidgetIds(context);
        WidgetManager manager = new WidgetManager(context);
        for (int widgetId : widgetIds) {
            if (manager.contains(packageName, widgetId)) {
                manager.markAsRemoved(packageName, widgetId); // eliminates exception throw when creating apps list
            }
        }
        AppBarWidgetService.updateAdapter(context);
    }
}
