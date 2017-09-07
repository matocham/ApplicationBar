package com.appbar.matocham.applicationbar.interfaces;

import com.appbar.matocham.applicationbar.applicationManager.AppInfo;

/**
 * Created by matocham on 02.06.2017.
 */

public interface AdapterItemInteractionListener {
    void itemClicked(int position, AppInfo appInfo);

    void switchChanged(boolean state, AppInfo appInfo);
}
