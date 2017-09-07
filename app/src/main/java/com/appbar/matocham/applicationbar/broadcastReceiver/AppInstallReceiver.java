package com.appbar.matocham.applicationbar.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.appbar.matocham.applicationbar.applicationManager.WidgetManager;
import com.appbar.matocham.applicationbar.widget.AppBarWidgetService;

/**
 * Created by Mateusz on 18.04.2017.
 */

public class AppInstallReceiver extends BroadcastReceiver {

    private static final String TAG = "AppInstallReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "package installed: " + intent.getDataString() + " action: " + intent.getAction());
        String packageName = intent.getDataString().substring(intent.getDataString().indexOf(":") + 1);
        int[] widgetIds = AppBarWidgetService.getAppWidgetIds(context);
        WidgetManager manager = new WidgetManager(context);
        for (int widgetId : widgetIds) {
            manager.renewIfValid(packageName, widgetId);
        }
        AppBarWidgetService.updateAdapter(context);
    }
}
