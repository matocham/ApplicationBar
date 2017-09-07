package com.appbar.matocham.applicationbar.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.appbar.matocham.applicationbar.R;
import com.appbar.matocham.applicationbar.applicationManager.WidgetManager;
import com.appbar.matocham.applicationbar.interfaces.OnDialogDissmissListener;

/**
 * Created by Mateusz on 05.11.2016.
 * handle user logging
 */
public class EditWidgetDialogFragment extends DialogFragment implements View.OnFocusChangeListener {

    private EditText widgetNameEditText;
    private TextView errorMessage;
    private Button ok, cancel;
    private int widgetId;
    private OnDialogDissmissListener listener;
    private WidgetManager widgetsManager;

    public static EditWidgetDialogFragment getInstance(OnDialogDissmissListener listener, int widgetId) {
        EditWidgetDialogFragment instance = new EditWidgetDialogFragment();
        instance.listener = listener;
        instance.widgetId = widgetId;
        return instance;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        widgetsManager = new WidgetManager(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_label_dialog, container);

        findWidgets(view);
        configureOkButton();
        configureCancelButton();
        configureEditText();
        return view;
    }

    private void findWidgets(View view) {
        ok = (Button) view.findViewById(R.id.okButton);
        cancel = (Button) view.findViewById(R.id.cancelButton);
        widgetNameEditText = (EditText) view.findViewById(R.id.widgetNameEditText);
        errorMessage = (TextView) view.findViewById(R.id.error_message);
    }

    private void configureEditText() {
        widgetsManager.refresh();
        widgetNameEditText.setText(widgetsManager.getWidget(widgetId).getLabel());
        widgetNameEditText.setOnFocusChangeListener(this);
        widgetNameEditText.getBackground().setColorFilter(getContext().getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
    }

    private void configureOkButton() {
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (widgetNameEditText.length() == 0) {
                    errorMessage.setVisibility(View.VISIBLE);
                } else {
                    String widgetLabel = widgetNameEditText.getText().toString().trim();
                    widgetsManager.lockAndRefresh();
                    widgetsManager.getWidget(widgetId).setLabel(widgetLabel);
                    widgetsManager.storeAndReleaseLock();
                    listener.dialogDissmissed();
                    dismiss();
                }
            }
        });
    }

    private void configureCancelButton() {
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(R.string.widget_edit_title);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_Window;
        dialog.getWindow().setTitleColor(getContext().getResources().getColor(R.color.colorPrimaryDark));
        return dialog;
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }
}
