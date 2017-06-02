package com.appbar.matocham.applicationbar.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.appbar.matocham.applicationbar.R;
import com.appbar.matocham.applicationbar.applicationManager.AppInfo;
import com.appbar.matocham.applicationbar.applicationManager.NewWidgetManager;
import com.appbar.matocham.applicationbar.interfaces.AdapterItemInteractionListener;

import java.util.List;

/**
 * Created by matocham on 02.06.2017.
 */

public class ApplicationListAdapter extends RecyclerView.Adapter<ApplicationListAdapter.AppViewHolder> {
    private int widgetId;
    AdapterItemInteractionListener itemListener;
    NewWidgetManager widgetsManager;
    List<AppInfo> apps;

    public ApplicationListAdapter(Context context, List<AppInfo> apps, AdapterItemInteractionListener itemListener) {
        widgetsManager = new NewWidgetManager(context);
        this.itemListener = itemListener;
        this.apps = apps;
    }

    @Override
    public AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.app_item_layout_element, parent, false);
        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AppViewHolder holder, final int position) {
        holder.setAppInfo(apps.get(position));
        holder.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemListener != null) {
                    itemListener.itemClicked(position, apps.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    public class AppViewHolder extends RecyclerView.ViewHolder {
        private AppInfo appInfo;
        private final View view;
        private final TextView appName;
        private final ImageView appIcon;
        private final Switch inWidget;

        public AppViewHolder(View view) {
            super(view);
            this.view = view;
            appName = (TextView) view.findViewById(R.id.app_name);
            appIcon = (ImageView) view.findViewById(R.id.app_icon);
            inWidget = (Switch) view.findViewById(R.id.display_in_widget);
        }

        public void setAppInfo(final AppInfo appInfo) {
            this.appInfo = appInfo;
            appName.setText(appInfo.getAppLabel());
            appIcon.setImageDrawable(appInfo.getIcon());
            inWidget.setChecked(widgetsManager.contains(appInfo.toString(), widgetId));
            inWidget.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemListener != null) {
                        itemListener.switchChanged(((Switch) v).isChecked(), appInfo);
                    }
                }
            });
        }

        public View getView() {
            return view;
        }
    }

    public void setWidgetId(int widgetId) {
        this.widgetId = widgetId;
    }
}