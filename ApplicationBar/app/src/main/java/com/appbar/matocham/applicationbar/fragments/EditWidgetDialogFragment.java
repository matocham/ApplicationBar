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

import com.appbar.matocham.applicationbar.OnDialogDissmissListener;
import com.appbar.matocham.applicationbar.R;
import com.appbar.matocham.applicationbar.applicationManager.WidgetsManager;

/**
 * Created by Mateusz on 05.11.2016.
 * handle user logging
 */
public class EditWidgetDialogFragment extends DialogFragment implements View.OnFocusChangeListener {

    private EditText widgetNameEditText;
    TextView errorMessage;
    Button ok, cancel;
    int widgetId;
    OnDialogDissmissListener listener;
    WidgetsManager widgetsManager;

    public static EditWidgetDialogFragment getInstance(OnDialogDissmissListener listener, int widgetId) {
        EditWidgetDialogFragment instance = new EditWidgetDialogFragment();
        instance.listener = listener;
        instance.widgetId = widgetId;
        return instance;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        widgetsManager = WidgetsManager.getInstance(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_widget_label, container);

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
                    widgetsManager.setWidgetLabel(widgetLabel, widgetId);
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
