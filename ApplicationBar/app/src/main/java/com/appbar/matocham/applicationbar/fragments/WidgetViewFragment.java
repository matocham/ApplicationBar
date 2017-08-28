package com.appbar.matocham.applicationbar.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appbar.matocham.applicationbar.R;
import com.appbar.matocham.applicationbar.adapters.ApplicationListAdapter;
import com.appbar.matocham.applicationbar.applicationManager.AppInfo;
import com.appbar.matocham.applicationbar.applicationManager.WidgetManager;
import com.appbar.matocham.applicationbar.interfaces.AdapterItemInteractionListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mateusz on 16.01.2017.
 */

public class WidgetViewFragment extends Fragment implements AdapterItemInteractionListener {
    private static final String TAG = "WidgetViewFragment";
    private List<AppInfo> availableApplications;
    private int widgetId;
    private ApplicationListAdapter adapter;
    private Context context;
    private WidgetManager widgetsManager;
    private RecyclerView recyclerView;

    public static WidgetViewFragment getInstance(List<AppInfo> applications, int widgetId) {
        WidgetViewFragment instance = new WidgetViewFragment();
        instance.availableApplications = applications;
        instance.widgetId = widgetId;
        Log.d(TAG, "creating fragment by instance");
        return instance;
    }

    public WidgetViewFragment() {
        Log.d(TAG, "Calling constructor");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        widgetsManager = new WidgetManager(context);
        if (availableApplications == null) {
            availableApplications = new ArrayList<>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.recyclerView = (RecyclerView) inflater.inflate(
                R.layout.app_list_fragment_layout, container, false);

        adapter = new ApplicationListAdapter(context, availableApplications, this);
        adapter.setWidgetId(widgetId);
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }

    public int getWidgetId() {
        return widgetId;
    }

    @Override
    public void switchChanged(boolean state, AppInfo appInfo) {
        if (state) {
            widgetsManager.addApp(appInfo.toString(), widgetId);
        } else {
            widgetsManager.removeApp(appInfo.toString(), widgetId);
        }
    }

    @Override
    public void itemClicked(int position, AppInfo appInfo) {
        startActivity(context.getPackageManager().getLaunchIntentForPackage(appInfo.getPackageName()));
    }
}
