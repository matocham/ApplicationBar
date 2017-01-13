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
    WidgetAppsManager widgetAppManager;
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

        apps = new ArrayList<>();
        widgetAppManager = new WidgetAppsManager(this);

        adapter = new ApplicationListAdapter(this, R.layout.single_item_layout, apps, this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        new LoadAppsAsyncTask(this, handler, true).execute();
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
        widgetAppManager.switchChanged(adapter.getItem(position), swx.isChecked());
    }

    @Override
    protected void onPause() {
        AppBarWidgetService.updateAdapter(this);
        super.onPause();
    }

}
