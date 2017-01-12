package com.appbar.matocham.applicationbar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

import com.appbar.matocham.applicationbar.widget.BarWidgetProvider;

import java.util.Arrays;

/**
 * Created by Mateusz on 10.01.2017.
 */

public class Utils {

    public static ProgressDialog showProgressDialog(Context context, String title, String message) {
        ProgressDialog dialog = ProgressDialog.show(context, title, message, true, false);
        return dialog;
    }

    public static Message createMessage(int what, Object object) {
        Message msg = new Message();
        msg.what = what;
        msg.obj = object;
        return msg;
    }

    public static int[] getAppWidgetIds(Activity activity) {
        int[] ids = AppWidgetManager.getInstance(activity.getApplication()).getAppWidgetIds(new ComponentName(activity.getApplication(), BarWidgetProvider.class));
        Log.e("UTILS","Widget ids list: " + Arrays.toString(ids));
        return ids;
    }
}
