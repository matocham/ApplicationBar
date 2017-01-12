package com.appbar.matocham.applicationbar.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.appbar.matocham.applicationbar.R;
import com.appbar.matocham.applicationbar.applicationManager.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mateusz on 11.01.2017.
 */

public class AppBarWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    Context context;
    List<AppInfo> markedApps = new ArrayList<>();

    public AppBarWidgetRemoteViewsFactory(Context context, Intent intent) {
        this.context = context;
    }

    @Override
    public void onCreate() {
        markedApps = AppInfo.getMarkedApps(context);
    }

    @Override
    public void onDataSetChanged() {
        markedApps = AppInfo.getMarkedApps(context);
    }

    @Override
    public void onDestroy() {
        markedApps.clear();
    }

    @Override
    public int getCount() {
        return markedApps.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews item = new RemoteViews(context.getPackageName(), R.layout.widget_single_item_layout);
        AppInfo appInfo = markedApps.get(position);
        item.setImageViewBitmap(R.id.app_icon_widget, drawableToBitmap(appInfo.getIcon()));

        Bundle extras = new Bundle();
        extras.putString(BarWidgetProvider.APP_ID, appInfo.getPname());
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);

        item.setOnClickFillInIntent(R.id.app_icon_widget,fillInIntent);
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

    private Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
