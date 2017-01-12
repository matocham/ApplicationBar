package com.appbar.matocham.applicationbar;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Switch;

import com.appbar.matocham.applicationbar.adapters.ApplicationListAdapter;
import com.appbar.matocham.applicationbar.applicationManager.AppInfo;
import com.appbar.matocham.applicationbar.applicationManager.WidgetApplicationsManager;
import com.appbar.matocham.applicationbar.asuncTasks.LoadAppsAsyncTask;
import com.appbar.matocham.applicationbar.widget.BarWidgetProvider;

import java.util.ArrayList;
import java.util.List;

public class AppsDisplayActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener{
    public static final int LOAD_FINISHED = 1;
    boolean firstRun = true;
    ApplicationListAdapter adapter;
    ListView listView;
    List<AppInfo> apps;
    WidgetApplicationsManager widgetAppManager;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == LOAD_FINISHED){
                adapter.clear();
                adapter.addAll((List<AppInfo>) msg.obj);
                widgetAppManager.setAppList((List<AppInfo>) msg.obj);
                adapter.notifyDataSetChanged();
                if(firstRun){
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
        widgetAppManager = new WidgetApplicationsManager(this);

        adapter = new ApplicationListAdapter(this,R.layout.single_item_layout, apps, this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        new LoadAppsAsyncTask(this,handler,Utils.getAllAppsSwitchState(this)).execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AppInfo info = adapter.getItem(position);
        startActivity(info.getLaunchIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_switch_layout, menu);
        MenuItem item = menu.findItem(R.id.myswitch);
        item.setActionView(R.layout.switch_layout);
        Switch swch = (Switch) item.getActionView().findViewById(R.id.all_apps_switch_widget);
        swch.setOnClickListener(this);
        swch.setChecked(Utils.getAllAppsSwitchState(this));
        return true;
    }

    @Override
    public void onClick(View v) {
        Switch swx = (Switch) v;
        if( v.getId() == R.id.all_apps_switch_widget){
            handleAllAppsSwitch(swx);
        } else {
            int position = listView.getPositionForView((View) swx.getParent());
            widgetAppManager.switchChanged(position,swx.isChecked());
        }

    }

    private void handleAllAppsSwitch(Switch v) {
        Switch allApps = v;
        Utils.setAllAppsSwitchState(allApps.isChecked(),this);
        new LoadAppsAsyncTask(this,handler,Utils.getAllAppsSwitchState(this)).execute();
    }

    private void updateWidget(){
        Intent intent = new Intent(this, BarWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), BarWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        sendBroadcast(intent);
    }

    @Override
    protected void onPause() {
        updateAdapter();
        super.onPause();
    }

    private void updateAdapter(){
        int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), BarWidgetProvider.class));
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        appWidgetManager.notifyAppWidgetViewDataChanged(ids,R.id.listView2);
    }
}
