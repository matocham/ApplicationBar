package com.appbar.matocham.applicationbar.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.appbar.matocham.applicationbar.applicationManager.AppInfo;
import com.appbar.matocham.applicationbar.R;
import com.appbar.matocham.applicationbar.applicationManager.NewWidgetManager;

import java.util.List;

/**
 * Created by Mateusz on 10.01.2017.
 */

public class ApplicationListAdapter extends ArrayAdapter<AppInfo>{
    private int layoutId;
    private int widgetId;
    View.OnClickListener switchListener;
    NewWidgetManager widgetsManager;

    public ApplicationListAdapter(Context context, int resource, List<AppInfo> apps, View.OnClickListener listener) {
        super(context, resource, apps);

        widgetsManager = new NewWidgetManager(context);
        this.switchListener = listener;
        this.layoutId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(layoutId, null);
        }

        AppInfo appInfo = getItem(position);

        if (appInfo != null) {
            ImageView appImage = (ImageView) view.findViewById(R.id.app_icon);
            TextView appName = (TextView) view.findViewById(R.id.app_name);
            Switch inWidget = (Switch) view.findViewById(R.id.display_in_widget);

            if (appImage != null) {
                appImage.setImageDrawable(appInfo.getIcon());
            }

            if (appName != null) {
                appName.setText(appInfo.getAppLabel());
            }

            if( inWidget != null){
                inWidget.setOnClickListener(switchListener);
                inWidget.setChecked(widgetsManager.contains(appInfo.toString(),widgetId));
            }
        }
        return view;
    }

    public void setWidgetId(int widgetId) {
        this.widgetId = widgetId;
    }
}
