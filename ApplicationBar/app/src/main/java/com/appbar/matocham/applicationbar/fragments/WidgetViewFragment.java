package com.appbar.matocham.applicationbar.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.appbar.matocham.applicationbar.R;
import com.appbar.matocham.applicationbar.adapters.ApplicationListAdapter;
import com.appbar.matocham.applicationbar.applicationManager.AppInfo;
import com.appbar.matocham.applicationbar.applicationManager.WidgetAppsManager;

import java.util.List;

/**
 * Created by Mateusz on 16.01.2017.
 */

public class WidgetViewFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    List<AppInfo> availableApplications;
    int widgetId;
    ApplicationListAdapter adapter;
    ListView listView;
    Context context;

    public static WidgetViewFragment getInstance(List<AppInfo> applications, int widgetId) {
        WidgetViewFragment instance = new WidgetViewFragment();
        instance.availableApplications = applications;
        instance.widgetId = widgetId;
        return instance;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.single_widget_layout, container, false);

        listView = (ListView) rootView.findViewById(R.id.listView1);
        adapter = new ApplicationListAdapter(context, R.layout.single_item_layout, availableApplications, this);
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
            WidgetAppsManager.addAppToWidget(app.toString(), widgetId, context);
        } else {
            WidgetAppsManager.removeAppFromWidget(app.toString(), widgetId, context);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*AppInfo info = adapter.getItem(position);
        startActivity(getPackageManager().getLaunchIntentForPackage(info.getPackageName()));*/
    }
}