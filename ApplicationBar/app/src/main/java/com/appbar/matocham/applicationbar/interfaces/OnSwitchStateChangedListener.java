package com.appbar.matocham.applicationbar.interfaces;

import com.appbar.matocham.applicationbar.applicationManager.AppInfo;

/**
 * Created by Mateusz on 10.01.2017.
 */

public interface OnSwitchStateChangedListener {
    void switchChanged(AppInfo app, boolean state);
}
