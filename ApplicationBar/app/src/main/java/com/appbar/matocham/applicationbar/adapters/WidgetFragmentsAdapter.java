package com.appbar.matocham.applicationbar.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.appbar.matocham.applicationbar.applicationManager.AppInfo;
import com.appbar.matocham.applicationbar.applicationManager.WidgetsManager;
import com.appbar.matocham.applicationbar.fragments.WidgetViewFragment;
import com.appbar.matocham.applicationbar.widget.AppBarWidgetService;

import java.util.List;

/**
 * Created by Mateusz on 16.01.2017.
 */

public class WidgetFragmentsAdapter extends FragmentStatePagerAdapter{

    public static final String TAG = "WidgetFragmentsAdapter";
    int[] widgets;
    List<AppInfo> applications;
    WidgetsManager widgetsManager;

    public WidgetFragmentsAdapter(List<AppInfo> applications, Context context) {
        super(((AppCompatActivity)context).getSupportFragmentManager()); // TODO edit!
        widgetsManager = WidgetsManager.getInstance(context);
        this.widgets = AppBarWidgetService.getAppWidgetIds(context);
        this.applications = applications;
        Log.d(TAG,"Created new fragment");
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = widgetsManager.getWidget(widgets[position]).getLabel();
        if(title.length()==0){
            title = "Widget "+widgets[position];
        }
        return title;
    }

    @Override
    public Fragment getItem(int position) {
        WidgetViewFragment fragment = WidgetViewFragment.getInstance(applications,widgets[position]);
        return fragment;
    }

    @Override
    public int getCount() {
        return widgets.length;
    }

    public void setWidgets(int[] widgets) {
        this.widgets = widgets;
    }

    public void setApplications(List<AppInfo> applications) {
        this.applications = applications;
    }
}
