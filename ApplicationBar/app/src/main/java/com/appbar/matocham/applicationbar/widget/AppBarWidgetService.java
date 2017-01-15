package com.appbar.matocham.applicationbar.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

import com.appbar.matocham.applicationbar.R;

import java.util.Arrays;

/**
 * Created by Mateusz on 11.01.2017.
 */

public class AppBarWidgetService extends RemoteViewsService{

    public static final String TAG = "AppBarWidgetService";

    public static void updateAdapter(Context context) {
        int ids[] = getAppWidgetIds(context);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        appWidgetManager.notifyAppWidgetViewDataChanged(ids, R.id.listView2);
    }

    public static void updateWidget(Context context) {
        Intent intent = new Intent(context, BarWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int ids[] = getAppWidgetIds(context);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }

    public static int[] getAppWidgetIds(Context context) {
        int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, BarWidgetProvider.class));
        Log.e(TAG,"Widget ids list: " + Arrays.toString(ids));
        return ids;
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new AppBarWidgetRemoteViewsFactory(this.getApplicationContext(),intent);
    }
}
