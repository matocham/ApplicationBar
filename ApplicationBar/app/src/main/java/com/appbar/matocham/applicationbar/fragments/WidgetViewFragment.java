package com.appbar.matocham.applicationbar.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Switch;

import com.appbar.matocham.applicationbar.R;
import com.appbar.matocham.applicationbar.adapters.ApplicationListAdapter;
import com.appbar.matocham.applicationbar.applicationManager.AppInfo;
import com.appbar.matocham.applicationbar.applicationManager.NewWidgetManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mateusz on 16.01.2017.
 */

public class WidgetViewFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    public static final String TAG = "WidgetViewFragment";
    List<AppInfo> availableApplications;
    int widgetId;
    ApplicationListAdapter adapter;
    ListView listView;
    Context context;
    NewWidgetManager widgetsManager;

    public static WidgetViewFragment getInstance(List<AppInfo> applications, int widgetId) {
        WidgetViewFragment instance = new WidgetViewFragment();
        instance.availableApplications = applications;
        instance.widgetId = widgetId;
        Log.d(TAG,"creating fragment by instance");
        return instance;
    }

    public WidgetViewFragment() {
        Log.d(TAG,"Calling constructor");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        widgetsManager = new NewWidgetManager(context);
        if(availableApplications == null){
            availableApplications = new ArrayList<>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.app_list_fragment_layout, container, false);

        listView = (ListView) rootView.findViewById(R.id.listView1);
        adapter = new ApplicationListAdapter(context, R.layout.app_item_layout_element, availableApplications, this);
        adapter.setWidgetId(widgetId);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        Switch swx = (Switch) v;
        int position = listView.getPositionForView((View) swx.getParent());
        boolean state = swx.isChecked();
        AppInfo app = adapter.getItem(position);
        if (state) {
            widgetsManager.addApp(app.toString(), widgetId);
        } else {
            widgetsManager.removeApp(app.toString(), widgetId);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AppInfo info = adapter.getItem(position);
        startActivity(context.getPackageManager().getLaunchIntentForPackage(info.getPackageName()));
    }

    public int getWidgetId() {
        return widgetId;
    }
}
