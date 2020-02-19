package com.fazlagida.burak.smartscaleusb;

import android.app.Activity;
import android.app.AlertDialog;

public class Config {

    public static void openDialog(Activity activity, String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle(title);
        alertDialog.setCancelable(false);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "EXIT",
                (dialog, which) -> {
                    dialog.dismiss();
                    activity.finish();
                });
        alertDialog.show();
    }

}
