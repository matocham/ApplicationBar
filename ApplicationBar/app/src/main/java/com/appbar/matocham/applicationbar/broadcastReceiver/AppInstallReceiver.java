package com.appbar.matocham.applicationbar.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.appbar.matocham.applicationbar.applicationManager.WidgetsManager;
import com.appbar.matocham.applicationbar.widget.AppBarWidgetService;

/**
 * Created by Mateusz on 18.04.2017.
 */

public class AppInstallReceiver extends BroadcastReceiver {

    public static final String TAG = "AppInstallReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "package installed: " + intent.getDataString());
        String packageName = intent.getDataString().substring(intent.getDataString().indexOf(":") + 1);
        int[] widgetIds = AppBarWidgetService.getAppWidgetIds(context);
        WidgetsManager manager = WidgetsManager.withContext(context).loadWidgets();
        for (int widgetId : widgetIds) {
            manager.markAsFreshOrDelete(packageName, widgetId);
            AppBarWidgetService.updateAdapter(context);
        }
    }
}
