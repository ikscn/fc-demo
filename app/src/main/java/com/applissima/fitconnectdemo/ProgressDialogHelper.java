package com.applissima.fitconnectdemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import io.realm.Realm;

/**
 * Created by ilkerkuscan on 09/06/17.
 */

public class ProgressDialogHelper {

    private final String clsName = "ProgressDialogHelper";
    static AlertDialog dialog;

    public static void create(Activity activity, String message){

        dialog = null;

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) activity.getBaseContext().getSystemService(activity.LAYOUT_INFLATER_SERVICE);
        View progressView = inflater.inflate(R.layout.progress_layout, null);
        TextView progressTextView = (TextView) progressView.findViewById(R.id.progressTextView);
        progressTextView.setText(message);
        dialogBuilder.setView(progressView);

        dialog = dialogBuilder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

    }

    public static void closeDialog(){

        if(dialog!=null){
            dialog.dismiss();
            dialog = null;
        }
    }


}
