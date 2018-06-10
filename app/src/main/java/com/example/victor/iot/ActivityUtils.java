package com.example.victor.iot;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

public class ActivityUtils extends AppCompatActivity{

    public static void showMessage(String title, String Message, Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }
}
