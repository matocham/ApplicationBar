package com.appbar.matocham.applicationbar.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.appbar.matocham.applicationbar.applicationManager.AppInfo;
import com.appbar.matocham.applicationbar.applicationManager.WidgetsManager;
import com.appbar.matocham.applicationbar.fragments.WidgetViewFragment;

import java.util.List;

/**
 * Created by Mateusz on 16.01.2017.
 */

public class WidgetFragmentsAdapter extends FragmentStatePagerAdapter{

    public static final String TAG = "WidgetFragmentsAdapter";
    int[] widgets;
    List<AppInfo> applications;

    public WidgetFragmentsAdapter(FragmentManager fm, List<AppInfo> applications, int[] widgets) {
        super(fm);
        this.widgets = widgets;
        this.applications = applications;
        Log.d(TAG,"Created new fragment");
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = WidgetsManager.getWidget(widgets[position]).getLabel();
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
