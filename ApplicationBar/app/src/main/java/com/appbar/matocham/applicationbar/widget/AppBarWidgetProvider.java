package com.appbar.matocham.applicationbar.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.appbar.matocham.applicationbar.R;
import com.appbar.matocham.applicationbar.applicationManager.NewWidgetManager;

/**
 * Created by Mateusz on 10.01.2017.
 */

public class AppBarWidgetProvider extends AppWidgetProvider {
    public static final String LAUNCH_APP_ACTION = "com.appbar.matocha.applicationbar.LAUNCH_APP_ACTION";
    public static final String APP_ID = "com.appbar.matocha.applicationbar.LAUNCH_APP_ID";
    public static final String WIDGET_ID = "com.appbar.matocha.applicationbar.WIDGET_ID";
    public static final String TAG="AppBarWidgetProvider";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(LAUNCH_APP_ACTION)) {
            String packageName = intent.getStringExtra(APP_ID);
            int widgetId = intent.getIntExtra(WIDGET_ID, -1);
            Intent starAppIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            if (starAppIntent != null) {
                context.startActivity(starAppIntent);
            } else {
                NewWidgetManager widgetsManager = new NewWidgetManager(context);
                widgetsManager.markAsRemoved(packageName, widgetId); // eliminates exception throw when creating apps list
                AppBarWidgetService.updateAdapter(context);
            }
        }
        super.onReceive(context, intent);
    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        Log.d(TAG,"Updating widgets");
        // update each of the app widgets with the remote adapter
        for (int i = 0; i < appWidgetIds.length; ++i) {

            RemoteViews rv = prepareViews(context,appWidgetIds[i]);
            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    public static RemoteViews prepareViews(Context context, int appWidgetId){
        Intent intent = new Intent(context, AppBarWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_bar_layout);

        rv.setRemoteAdapter(appWidgetId, R.id.listView2, intent);
        rv.setEmptyView(R.id.listView2, R.id.empty_view);

        Intent launchAppIntent = new Intent(context, AppBarWidgetProvider.class);
        launchAppIntent.setAction(AppBarWidgetProvider.LAUNCH_APP_ACTION);
        launchAppIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, launchAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        rv.setPendingIntentTemplate(R.id.listView2, toastPendingIntent);

        return rv;
    }
}
