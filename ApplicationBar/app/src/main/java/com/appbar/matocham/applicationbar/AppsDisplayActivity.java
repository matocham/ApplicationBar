package com.appbar.matocham.applicationbar;

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
import com.appbar.matocham.applicationbar.widget.AppBarWidgetService;

import java.util.ArrayList;
import java.util.List;

public class AppsDisplayActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    public static final int LOAD_FINISHED = 1;
    ApplicationListAdapter adapter;
    ListView listView;
    List<AppInfo> apps;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == LOAD_FINISHED) {
                adapter.clear();
                adapter.addAll((List<AppInfo>) msg.obj);
                adapter.notifyDataSetChanged();
                AppBarWidgetService.updateWidget(AppsDisplayActivity.this);
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_display);
        listView = (ListView) findViewById(R.id.listView1);

        int[] widgetIds = AppBarWidgetService.getAppWidgetIds(this);
        if(widgetIds.length>0){
            apps = new ArrayList<>();

            adapter = new ApplicationListAdapter(this, R.layout.single_item_layout, apps, this);
            adapter.setWidgetId(widgetIds[0]);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);
            new LoadAppsAsyncTask(this, handler, true).execute();
            WidgetAppsManager.loadWidgets(this);
            AppBarWidgetService.updateWidget(AppsDisplayActivity.this);
        } else {
            findViewById(R.id.no_widget_message).setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*AppInfo info = adapter.getItem(position);
        startActivity(getPackageManager().getLaunchIntentForPackage(info.getPackageName()));*/
    }

    @Override
    public void onClick(View v) {
        Switch swx = (Switch) v;
        int position = listView.getPositionForView((View) swx.getParent());
        boolean state = swx.isChecked();
        AppInfo app = adapter.getItem(position);
        int[] widgetIds = AppBarWidgetService.getAppWidgetIds(this);
        if (widgetIds.length > 0) {
            if (state) {
                WidgetAppsManager.addAppToWidget(app.toString(), widgetIds[0], this);
            } else {
                WidgetAppsManager.removeAppFromWidget(app.toString(), widgetIds[0], this);
            }
        }
    }

    @Override
    protected void onPause() {
        AppBarWidgetService.updateAdapter(this);
        super.onPause();
    }

}
