package com.appbar.matocham.applicationbar.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Mateusz on 11.01.2017.
 */

public class AppBarWidgetService extends RemoteViewsService{

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new AppBarWidgetRemoteViewsFactory(this.getApplicationContext(),intent);
    }
}
