package com.appbar.matocham.applicationbar.applicationManager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Mateusz on 10.01.2017.
 */

public class AppInfo implements  Comparable<AppInfo>{

    private String appLabel;
    private String packageName;
    private Drawable icon;
    private Intent launchIntent;

    private void prettyPrint() {
        Log.d(AppInfo.class.getSimpleName(), appLabel + "\t" + packageName);
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
            AppInfo newInfo = getAppInfo(packageManager, appI);
            if(newInfo!=null){
                res.add(newInfo);
            }
        }
        return res;
    }

    public static AppInfo getAppInfo(PackageManager packageManager, ApplicationInfo appI) {
        if(packageManager.getLaunchIntentForPackage(appI.packageName) == null){
            return null;
        }
        AppInfo newInfo = new AppInfo();
        newInfo.setAppLabel(appI.loadLabel(packageManager).toString());
        newInfo.setPackageName(appI.packageName);
        newInfo.setIcon(appI.loadIcon(packageManager));
        newInfo.setLaunchIntent(packageManager.getLaunchIntentForPackage(appI.packageName));

        return newInfo;
    }

    private static boolean isSystemPackage(ApplicationInfo appInfo) {
        return ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true : false;
    }

    public String getAppLabel() {
        return appLabel;
    }

    public String getPackageName() {
        return packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public Intent getLaunchIntent() {
        return launchIntent;
    }

    public void setAppLabel(String appLabel) {
        this.appLabel = appLabel;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public void setLaunchIntent(Intent launchIntent) {
        this.launchIntent = launchIntent;
    }

    @Override
    public int compareTo(AppInfo another) {
        return appLabel.compareTo(another.appLabel);
    }

    @Override
    public String toString() {
        return packageName;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof AppInfo)){
            return false;
        }

        AppInfo otherApp = (AppInfo) o;
        return packageName.equals(otherApp.packageName);
    }

    @Override
    public int hashCode() {
        return packageName.hashCode();
    }
}
