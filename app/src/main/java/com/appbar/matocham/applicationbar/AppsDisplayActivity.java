package com.appbar.matocham.applicationbar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appbar.matocham.applicationbar.adapters.WidgetFragmentsAdapter;
import com.appbar.matocham.applicationbar.applicationManager.AppInfo;
import com.appbar.matocham.applicationbar.asyncTasks.LoadAppsAsyncTask;
import com.appbar.matocham.applicationbar.fragments.EditWidgetDialogFragment;
import com.appbar.matocham.applicationbar.fragments.WidgetViewFragment;
import com.appbar.matocham.applicationbar.interfaces.OnDialogDissmissListener;
import com.appbar.matocham.applicationbar.widget.AppBarWidgetService;

import java.util.List;

public class AppsDisplayActivity extends AppCompatActivity implements OnDialogDissmissListener {
    private static final String TAG = "AppsDisplayActivity";
    private ViewPager widgetViews;
    private Toolbar toolbar;
    private TabLayout tabLayout;

    private WidgetFragmentsAdapter adapter;
    private TextView noWidgetsView;
    private int[] widgetIds;
    private LoadAppsAsyncTask loadAppsAsyncTask;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == LoadAppsAsyncTask.LOAD_FINISHED) {
                adapter.setWidgets(widgetIds);
                adapter.setApplications((List<AppInfo>) msg.obj);
                adapter.notifyDataSetChanged();
                AppBarWidgetService.updateWidget(AppsDisplayActivity.this);
                widgetViews.setAdapter(adapter);
                showWidgets();
            }
            super.handleMessage(msg);
        }
    };

    private void loadWidgets() {
        widgetIds = AppBarWidgetService.getAppWidgetIds(this);
        if (widgetIds.length > 0) {
            loadAppsAsyncTask = new LoadAppsAsyncTask(this, handler, true);
            loadAppsAsyncTask.execute();
            AppBarWidgetService.updateWidget(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (AppBarWidgetService.getAppWidgetIds(this).length > 0) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.edit_menu, menu);
            menu.findItem(R.id.edit_widget);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.edit_widget) {
            showEditDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadWidgets();
        Log.d(TAG, "creating activity");
        setContentView(R.layout.activity_apps_display);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        widgetViews = (ViewPager) findViewById(R.id.pager);
        widgetViews.setPageTransformer(true, new ZoomOutPageTransformer());
        noWidgetsView = (TextView) findViewById(R.id.no_widget_message);
        adapter = new WidgetFragmentsAdapter(getSupportFragmentManager(), this);
        widgetViews.setAdapter(adapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(widgetViews);

        configureTabsListener();
    }

    private void configureTabsListener() {
        LinearLayout tabStrip = (LinearLayout) tabLayout.getChildAt(0);
        tabStrip.setOnHierarchyChangeListener(getTabsHierarchyListener());
    }

    @NonNull
    private ViewGroup.OnHierarchyChangeListener getTabsHierarchyListener() {
        return new ViewGroup.OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View view, View view1) {
                view1.setOnLongClickListener(getTabLongClickListener());
                LinearLayout tabStrip = (LinearLayout) view;
                view1.setTag(tabStrip.getChildCount());
            }

            @Override
            public void onChildViewRemoved(View view, View view1) {
            }
        };
    }

    @NonNull
    private View.OnLongClickListener getTabLongClickListener() {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AppsDisplayActivity.this.showEditDialog((Integer) view.getTag() - 1);
                Log.d(TAG, "view with tag " + view.getTag() + " clicked");
                return true;
            }
        };
    }

    @Override
    protected void onPause() {
        AppBarWidgetService.updateAdapter(this);
        super.onPause();
    }

    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        EditWidgetDialogFragment loginDialogFragment = EditWidgetDialogFragment.getInstance(this, getCurrentWidgetIndex());
        loginDialogFragment.show(fm, "fragment_edit");
    }

    private void showEditDialog(int index) {
        FragmentManager fm = getSupportFragmentManager();
        EditWidgetDialogFragment loginDialogFragment = EditWidgetDialogFragment.getInstance(this, getWidgetIdAtIndex(index));
        loginDialogFragment.show(fm, "fragment_edit");
    }

    public int getCurrentWidgetIndex() {
        return getWidgetIdAtIndex(widgetViews.getCurrentItem());
    }

    private int getWidgetIdAtIndex(int index) {
        WidgetViewFragment fragment = (WidgetViewFragment) adapter.getItem(index);
        int widgetId = fragment.getWidgetId();
        return widgetId;
    }

    @Override
    public void dialogDissmissed() {
        refreshTabs();
    }

    public void refreshTabs() {
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        if (loadAppsAsyncTask != null && !loadAppsAsyncTask.isCancelled()) {
            loadAppsAsyncTask.cancel(true);
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "entering onResume");
        int previousWidgetsCount = widgetIds.length;
        widgetIds = AppBarWidgetService.getAppWidgetIds(this);
        adapter.setWidgets(widgetIds);
        Log.d(TAG, "Previous widgets amount: " + previousWidgetsCount);
        Log.d(TAG, "Current widgets amount: " + widgetIds.length);

        if (widgetIds.length == 0) {
            Log.d(TAG, "Widgets will be hidden");
            hideWidgets();
        } else {
            if (previousWidgetsCount == 0) {
                Log.d(TAG, "Widgets will be reloaded");
                loadWidgets();
            } else {
                Log.d(TAG, "Widgets will be shown");
                showWidgets();
            }
        }
        refreshTabs();
        super.onResume();
    }

    private void showWidgets() {
        widgetViews.setVisibility(View.VISIBLE);
        noWidgetsView.setVisibility(View.GONE);
    }

    private void hideWidgets() {
        widgetViews.setVisibility(View.GONE);
        noWidgetsView.setVisibility(View.VISIBLE);
    }
}
