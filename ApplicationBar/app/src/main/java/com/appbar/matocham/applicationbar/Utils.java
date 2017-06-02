package com.appbar.matocham.applicationbar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Mateusz on 10.01.2017.
 */

public class Utils {

    public static AlertDialog getProgressDialog(Context context, String title, String message) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        ViewGroup view = (ViewGroup) layoutInflater.inflate(R.layout.progress_dialog_layout, null);
        AlertDialog progressDialog = new AlertDialog.Builder(context)
                .setView(view)
                .setTitle(title)
                .setCancelable(false)
                .create();
        TextView messageView = (TextView) view.findViewById(R.id.dialog_message);
        messageView.setText(message);
        return progressDialog;
    }

    public static Message createMessage(int what, Object object) {
        Message msg = new Message();
        msg.what = what;
        msg.obj = object;
        return msg;
    }

    public static void showInfoDialog(Context context, String title, String message) {
        AlertDialog alertDialog = getSimpleDialog(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, context.getString(R.string.dialog_positive_button),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private static AlertDialog getSimpleDialog(Context context) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        return alertDialog;
    }

    public static int getSoftButtonsBarSizePort(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }

}
