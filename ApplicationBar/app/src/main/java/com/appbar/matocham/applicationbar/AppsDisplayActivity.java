package com.appbar.matocham.applicationbar;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Switch;

import com.appbar.matocham.applicationbar.adapters.ApplicationListAdapter;
import com.appbar.matocham.applicationbar.applicationManager.AppInfo;
import com.appbar.matocham.applicationbar.applicationManager.WidgetAppsManager;
import com.appbar.matocham.applicationbar.asuncTasks.LoadAppsAsyncTask;
import com.appbar.matocham.applicationbar.widget.BarWidgetProvider;

import java.util.ArrayList;
import java.util.List;

public class AppsDisplayActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    public static final int LOAD_FINISHED = 1;
    boolean firstRun = true;
    ApplicationListAdapter adapter;
    ListView listView;
    List<AppInfo> apps;
    WidgetAppsManager widgetAppManager;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == LOAD_FINISHED) {
                adapter.clear();
                adapter.addAll((List<AppInfo>) msg.obj);
                adapter.notifyDataSetChanged();
                if (firstRun) {
                    firstRun = false;
                    updateWidget();
                }
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_display);

        listView = (ListView) findViewById(R.id.listView1);

        apps = new ArrayList<>();
        widgetAppManager = new WidgetAppsManager(this);

        adapter = new ApplicationListAdapter(this, R.layout.single_item_layout, apps, this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        new LoadAppsAsyncTask(this, handler, true).execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AppInfo info = adapter.getItem(position);
        startActivity(info.getLaunchIntent());
    }

    @Override
    public void onClick(View v) {
        Switch swx = (Switch) v;
        int position = listView.getPositionForView((View) swx.getParent());
        widgetAppManager.switchChanged(adapter.getItem(position), swx.isChecked());
    }

    private void updateWidget() {
        Intent intent = new Intent(this, BarWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int ids[] = Utils.getAppWidgetIds(this);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
    }

    @Override
    protected void onPause() {
        updateAdapter();
        super.onPause();
    }

    private void updateAdapter() {
        int ids[] = Utils.getAppWidgetIds(this);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        appWidgetManager.notifyAppWidgetViewDataChanged(ids, R.id.listView2);
    }
}
