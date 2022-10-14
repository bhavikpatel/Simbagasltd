package com.track.cylinderdelivery;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

public class MySingalton {
    private static MySingalton mInstance= null;

  // public String URL="https://admin.simbagas.com";
   //public String URL="http://test.hdvivah.in";
    public String URL="http://qa.simbagas.com";


    protected MySingalton(){}

    public static synchronized MySingalton getInstance() {
        if(null == mInstance){
            mInstance = new MySingalton();
        }
        return mInstance;
    }
    public static String convertString(String data){
        if(data.isEmpty() || data.equals("null")){
            return "";
        }else {
            return data;
        }
    }
    public static void showOkDilog(Context context, String message,String title){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }
}
