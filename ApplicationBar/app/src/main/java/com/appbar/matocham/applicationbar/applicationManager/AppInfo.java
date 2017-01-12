package com.appbar.matocham.applicationbar.applicationManager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.appbar.matocham.applicationbar.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Mateusz on 10.01.2017.
 */

public class AppInfo implements  Comparable<AppInfo>{

    private String appname;
    private String pname;
    private Drawable icon;
    private Intent launchIntent;

    private void prettyPrint() {
        Log.d(AppInfo.class.getName(),appname + "\t" + pname);
    }

    public static ArrayList<AppInfo> getApplications(Context context, boolean getSysPackages) {
        ArrayList<AppInfo> apps = getInstalledApps(getSysPackages, context); /* false = no system packages */
        Collections.sort(apps);
        final int max = apps.size();
        for (int i=0; i<max; i++) {
            apps.get(i).prettyPrint();
        }
        return apps;
    }

    private static ArrayList<AppInfo> getInstalledApps(boolean getSysPackages, Context context) {
        ArrayList<AppInfo> res = new ArrayList<AppInfo>();
        PackageManager packageManager = context.getPackageManager();
        List<ApplicationInfo> appsInfo = packageManager.getInstalledApplications(0);
        for(int i=0;i<appsInfo.size();i++) {
            ApplicationInfo appI = appsInfo.get(i);
            if ((!getSysPackages) && isSystemPackage(appI)) {
                continue ;
            }
            AppInfo newInfo = new AppInfo();
            newInfo.appname = appI.loadLabel(packageManager).toString();
            newInfo.pname = appI.packageName;
            newInfo.icon = appI.loadIcon(packageManager);
            newInfo.launchIntent = packageManager.getLaunchIntentForPackage(appI.packageName);
            res.add(newInfo);
        }
        return res;
    }

    private static boolean isSystemPackage(ApplicationInfo appInfo) {
        return ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true : false;
    }

    public String getAppname() {
        return appname;
    }

    public String getPname() {
        return pname;
    }

    public Drawable getIcon() {
        return icon;
    }

    public Intent getLaunchIntent() {
        return launchIntent;
    }

    public static List<AppInfo> getMarkedApps(Context context) {
        String[] apps = Utils.getWigetAppsTable(context);
        PackageManager packageManager = context.getPackageManager();
        return null;
    }

    @Override
    public int compareTo(AppInfo another) {
        return appname.compareTo(another.appname);
    }

    @Override
    public String toString() {
        return pname+"."+appname;
    }
}
