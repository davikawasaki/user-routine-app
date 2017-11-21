package me.davikawasaki.routineapp.userroutineapp.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

import me.davikawasaki.routineapp.userroutineapp.R;

/**
 * Created by kawasaki on 16/11/17.
 */

public class UtilsGUI {

    public static void modalError(Context context, int textId) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(R.string.txt_warning_title_modal);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setMessage(textId);

        builder.setNeutralButton(R.string.txt_button_ok_modal,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void modalConfirm(Context context,
                                    String msg,
                                    DialogInterface.OnClickListener listener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(R.string.txt_confirmation_title_modal);
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        builder.setMessage(msg);

        builder.setPositiveButton(R.string.txt_button_yes_modal, listener);
        builder.setNegativeButton(R.string.txt_button_no_modal, listener);

        AlertDialog alert = builder.create();
        alert.show();
    }

    public static String checkTextField(Context  context,
                                        EditText editText,
                                        int errorId) {

        String text = editText.getText().toString();

        if (UtilsString.stringEmpty(text)) {
            UtilsGUI.modalError(context, errorId);
            editText.setText(null);
            editText.requestFocus();
            return null;
        } else return text.trim();
    }

}
