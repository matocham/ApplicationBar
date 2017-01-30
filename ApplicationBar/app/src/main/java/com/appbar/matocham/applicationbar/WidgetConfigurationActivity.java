package com.appbar.matocham.applicationbar;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RemoteViews;

import com.appbar.matocham.applicationbar.applicationManager.AppInfo;
import com.appbar.matocham.applicationbar.applicationManager.Widget;
import com.appbar.matocham.applicationbar.applicationManager.WidgetsManager;
import com.appbar.matocham.applicationbar.asuncTasks.LoadAppsAsyncTask;
import com.appbar.matocham.applicationbar.fragments.WidgetViewFragment;
import com.appbar.matocham.applicationbar.widget.AppBarWidgetProvider;
import com.appbar.matocham.applicationbar.widget.AppBarWidgetService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WidgetConfigurationActivity extends AppCompatActivity {

    int widgetId;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == LoadAppsAsyncTask.LOAD_FINISHED) {
                WidgetViewFragment appsFragment = WidgetViewFragment.getInstance((List<AppInfo>) msg.obj,widgetId);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.apps_fragment,appsFragment).commit();
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_configuration);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            widgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        new LoadAppsAsyncTask(this, handler, true).execute();
        setNegativeResult();
    }

    public void submitConfiguration(View v){
        setWidgetLabel();
        configureWidget();
        setPositiveResult();
        finish();
    }

    private void setNegativeResult(){
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        setResult(RESULT_CANCELED, resultValue);
    }

    private void setPositiveResult(){
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        setResult(RESULT_OK, resultValue);
    }

    private void configureWidget() {
        RemoteViews initialView = AppBarWidgetProvider.prepareViews(this, widgetId);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        appWidgetManager.updateAppWidget(widgetId, initialView);
    }

    private void setWidgetLabel(){
        EditText labelEditText = (EditText) findViewById(R.id.widget_label);
        String label = labelEditText.getText().toString();
        if(label.length() > 0){
            WidgetsManager.setWidgetLabel(label,widgetId,this);
        }
    }
}