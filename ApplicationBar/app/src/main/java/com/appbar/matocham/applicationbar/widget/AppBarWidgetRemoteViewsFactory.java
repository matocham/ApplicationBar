package com.appbar.matocham.applicationbar.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.appbar.matocham.applicationbar.R;
import com.appbar.matocham.applicationbar.applicationManager.AppElement;
import com.appbar.matocham.applicationbar.applicationManager.AppInfo;
import com.appbar.matocham.applicationbar.applicationManager.NewWidgetManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Mateusz on 11.01.2017.
 */

public class AppBarWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    public static final String TAG = "AppBarWidgetFactory";
    Context context;
    List<AppInfo> markedApps = new ArrayList<>();
    int widgetId;

    NewWidgetManager widgetsManager;

    public AppBarWidgetRemoteViewsFactory(Context context, Intent intent) {
        this.context = context;
        widgetsManager = new NewWidgetManager(context);
        this.widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        Log.d(TAG, "WidgetID= " + widgetId);
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "Creating new widget - widget "+widgetId);
        markedApps = convert(widgetsManager.getValidApps(widgetId));
        Log.d(TAG, "Fetched " + markedApps.size() + " apps");
    }

    @Override
    public void onDataSetChanged() {
        Log.d(TAG, "Refreshing dataset - widget "+widgetId);
        markedApps = convert(widgetsManager.getValidApps(widgetId));
        Log.d(TAG, "Fetched " + markedApps.size() + " apps");
    }

    @Override
    public void onDestroy() {
        markedApps.clear();
        widgetsManager.remove(widgetId);
        Log.d(TAG, "Destroying widget with id " + widgetId);
    }

    @Override
    public int getCount() {
        return markedApps.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if(position >= markedApps.size()){
            Log.e(TAG,"Dataset is obsolete. Renewing and sending refresh request to widgets!");
            position = markedApps.size()-1; // double last element
            markedApps =  convert(widgetsManager.getValidApps(widgetId)); // refresh dataset as precaution only - it will be refreshed during dataset changed event
            AppBarWidgetService.updateAdapter(context); // send refresh evnet to widgets
        }

        RemoteViews item = new RemoteViews(context.getPackageName(), R.layout.widget_item);
        AppInfo appInfo = markedApps.get(position);
        item.setImageViewBitmap(R.id.app_icon_widget, drawableToBitmap(appInfo.getIcon()));
        Bundle extras = new Bundle();
        extras.putString(AppBarWidgetProvider.APP_ID, appInfo.getPackageName());
        extras.putInt(AppBarWidgetProvider.WIDGET_ID, widgetId);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);

        item.setOnClickFillInIntent(R.id.app_icon_widget, fillInIntent);
        return item;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private List<AppInfo> convert(List<AppElement> apps) {
        PackageManager packageManager = context.getPackageManager();
        List<AppInfo> converted = new ArrayList<>();

        for (AppElement app : apps) {
            AppInfo aInfo = AppInfo.getAppInfo(packageManager, app.getName());
            if (aInfo != null) {
                converted.add(aInfo);
            }
        }
        Collections.sort(converted);
        return converted;
    }

}
