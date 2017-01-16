package com.appbar.matocham.applicationbar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
    private static final String TAG = "AppsDisplayActivity";
    ViewPager widgetViews;
    private Toolbar toolbar;
    private TabLayout tabLayout;

    WidgetFragmentsAdapter adapter;
    TextView noWidgetsView;
    int[] widgetIds;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == LOAD_FINISHED) {
                adapter.setWidgets(widgetIds);
                adapter.setApplications((List<AppInfo>) msg.obj);
                adapter.notifyDataSetChanged();
                AppBarWidgetService.updateWidget(AppsDisplayActivity.this);
                widgetViews.setAdapter(adapter);
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
        Log.d(TAG,"creating activity");
        setContentView(R.layout.activity_apps_display);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        widgetViews = (ViewPager) findViewById(R.id.pager);
        widgetViews.setPageTransformer(true, new ZoomOutPageTransformer());
        noWidgetsView = (TextView) findViewById(R.id.no_widget_message);
        adapter = new WidgetFragmentsAdapter(getSupportFragmentManager(),new ArrayList<AppInfo>(),new int[0]);
        widgetViews.setAdapter(adapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(widgetViews);
    }

    @Override
    protected void onPause() {
        AppBarWidgetService.updateAdapter(this);
        super.onPause();
    }

}
