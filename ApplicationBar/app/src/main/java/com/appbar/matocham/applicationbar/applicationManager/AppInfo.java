package com.appbar.matocham.applicationbar.applicationManager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
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

    private AppInfo(){ // private constructor to prevent creation without using factory methods

    }

    public static ArrayList<AppInfo> getApplications(Context context, boolean getSysPackages) {
        ArrayList<AppInfo> apps = getInstalledApps(context, getSysPackages); /* false = no system packages */
        final int max = apps.size();
        for (int i=0; i<max; i++) {
            apps.get(i).prettyPrint();
        }
        return apps;
    }

    private static ArrayList<AppInfo> getInstalledApps(Context context, boolean getSysPackages) {
        ArrayList<AppInfo> res = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();
        List<ActivityInfo> appsInfo = getInstalledApps(packageManager);
        for(int i=0;i<appsInfo.size();i++) {
            ActivityInfo appI = appsInfo.get(i);
            if ((!getSysPackages) && isSystemPackage(appI)) {
                continue ;
            }
            AppInfo newInfo = getAppInfo(packageManager, appI);
            if(newInfo!=null){
                res.add(newInfo);
            }
        }
        Collections.sort(res);
        return res;
    }

    private static List<ActivityInfo> getInstalledApps(PackageManager packageManager){
        List<ResolveInfo> foundEntries = queryForApps(packageManager, null);
        if (foundEntries.isEmpty()) {
            return new ArrayList<>();
        }
        List<ActivityInfo> foundApps = new ArrayList<>();

        for(ResolveInfo inf : foundEntries){
            foundApps.add(inf.activityInfo);
        }
        return foundApps;
    }

    public static AppInfo getAppInfo(PackageManager packageManager, ActivityInfo appI) {
        AppInfo newInfo = new AppInfo();
        newInfo.setAppLabel(appI.loadLabel(packageManager).toString());
        newInfo.setPackageName(appI.packageName);
        newInfo.setIcon(appI.loadIcon(packageManager));

        return newInfo;
    }

    public static AppInfo getAppInfo(PackageManager packageManager, String packageName) {
        List<ResolveInfo> foundEntries = queryForApps(packageManager,packageName);
        if (foundEntries.isEmpty()) {
            return null;
        }
        return getAppInfo(packageManager, foundEntries.get(0).activityInfo);
    }

    /**
     * creates intent with ACTION_MAIN and category CATEGORY_LAUNCHER to get all Activities
     * witch specified package path that can be started using intent
     * @return
     */
    private static List<ResolveInfo> queryForApps(PackageManager packageManager, String packageName){
        List<ResolveInfo> foundEntries;
        Intent intentToResolve = new Intent(Intent.ACTION_MAIN);
        intentToResolve.addCategory(Intent.CATEGORY_LAUNCHER);
        intentToResolve.setPackage(packageName);
        foundEntries = packageManager.queryIntentActivities(intentToResolve, 0);
        if (foundEntries == null || foundEntries.size() <= 0) {
            return new ArrayList<>();
        }
        return foundEntries;
    }

    private static boolean isSystemPackage(ActivityInfo appInfo) {
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

    public void setAppLabel(String appLabel) {
        this.appLabel = appLabel;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    private void prettyPrint() {
        Log.d(AppInfo.class.getSimpleName(), appLabel + "\t" + packageName);
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
