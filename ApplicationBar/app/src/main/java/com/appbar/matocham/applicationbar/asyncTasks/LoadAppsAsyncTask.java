package com.appbar.matocham.applicationbar.asyncTasks;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import com.appbar.matocham.applicationbar.R;
import com.appbar.matocham.applicationbar.Utils;
import com.appbar.matocham.applicationbar.applicationManager.AppInfo;

import java.util.List;

/**
 * Created by Mateusz on 10.01.2017.
 */

public class LoadAppsAsyncTask extends AsyncTask<Void, Void, List<AppInfo>> {
    public static final int LOAD_FINISHED = 1;
    Context context;
    Handler handler;
    boolean mode;
    Dialog progress;

    public LoadAppsAsyncTask(Context context, Handler handler, boolean mode) {
        this.context = context;
        this.handler = handler;
        this.mode = mode;
    }

    @Override
    protected List<AppInfo> doInBackground(Void... params) {
        return AppInfo.getApplications(context,mode);
    }

    @Override
    protected void onPreExecute() {
        progress = Utils.getProgressDialog(context,context.getString(R.string.app_loading_dialog_title), context.getString(R.string.app_loading_dialog_msg));
        progress.show();
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(List<AppInfo> appInfos) {
        handler.sendMessage(Utils.createMessage(LOAD_FINISHED, appInfos));
        progress.dismiss();
        super.onPostExecute(appInfos);
    }
}
