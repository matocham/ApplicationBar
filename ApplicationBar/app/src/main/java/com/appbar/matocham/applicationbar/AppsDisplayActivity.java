package com.appbar.matocham.applicationbar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.appbar.matocham.applicationbar.adapters.WidgetFragmentsAdapter;
import com.appbar.matocham.applicationbar.applicationManager.AppInfo;
import com.appbar.matocham.applicationbar.applicationManager.WidgetAppsManager;
import com.appbar.matocham.applicationbar.asuncTasks.LoadAppsAsyncTask;
import com.appbar.matocham.applicationbar.widget.AppBarWidgetService;

import java.util.ArrayList;
import java.util.List;

public class AppsDisplayActivity extends AppCompatActivity {
    public static final int LOAD_FINISHED = 1;
    List<AppInfo> apps;
    ViewPager widgetViews;
    WidgetFragmentsAdapter adapter;
    TextView noWidgetsView;
    int[] widgetIds;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == LOAD_FINISHED) {
                apps = (List<AppInfo>) msg.obj;
                //adapter = new WidgetFragmentsAdapter(getSupportFragmentManager(), apps, widgetIds);
                adapter.setWidgets(widgetIds);
                adapter.setApplications(apps);
                adapter.notifyDataSetChanged();
                //widgetViews.setAdapter(adapter);
                AppBarWidgetService.updateWidget(AppsDisplayActivity.this);
                widgetViews.setVisibility(View.VISIBLE);
                noWidgetsView.setVisibility(View.GONE);
            }
            super.handleMessage(msg);
        }
    };

    private void loadWidgets() {
        widgetIds = AppBarWidgetService.getAppWidgetIds(this);
        if (widgetIds.length > 0) {
            new LoadAppsAsyncTask(this, handler, true).execute();
            WidgetAppsManager.loadWidgets(this);
            AppBarWidgetService.updateWidget(this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadWidgets();
        setContentView(R.layout.activity_apps_display);
        apps = new ArrayList<>();
        widgetViews = (ViewPager) findViewById(R.id.pager);
        widgetViews.setPageTransformer(true, new ZoomOutPageTransformer());
        noWidgetsView = (TextView) findViewById(R.id.no_widget_message);
        adapter = new WidgetFragmentsAdapter(getSupportFragmentManager(),apps,new int[0]);
        widgetViews.setAdapter(adapter);
    }

    @Override
    protected void onPause() {
        AppBarWidgetService.updateAdapter(this);
        super.onPause();
    }

}
