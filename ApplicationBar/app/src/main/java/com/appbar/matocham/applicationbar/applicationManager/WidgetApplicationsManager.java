package com.appbar.matocham.applicationbar.applicationManager;

import android.content.Context;

import com.appbar.matocham.applicationbar.Utils;
import com.appbar.matocham.applicationbar.interfaces.OnSwitchStateChangedListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mateusz on 10.01.2017.
 */

public class WidgetApplicationsManager implements OnSwitchStateChangedListener {
    private Context context;
    private List<AppInfo> appList;

    public WidgetApplicationsManager(Context context) {
        this.context = context;
        appList = new ArrayList<>();
    }

    public WidgetApplicationsManager(Context context, List<AppInfo> appList) {
        this.context = context;
        this.appList = appList;
    }

    @Override
    public void switchChanged(int position, boolean state) {
        AppInfo app = appList.get(position);
        if(state){
            Utils.addAppToWidget(app.toString(), context);
        }else{
            Utils.removeAppFromWidget(app.toString(),context);
        }
    }

    public void setAppList(List<AppInfo> appList) {
        this.appList = appList;
    }
}
