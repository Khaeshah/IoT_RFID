package com.example.victor.iot;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import DatabaseUtils.DatabaseWriter;

public class MenuActivity extends AppCompatActivity {

    DatabaseWriter myDb;
    Button createUserBtn;
    Button deleteUserBtn;
    Button scanUserBtn;
    Button checkUsersBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        myDb = new DatabaseWriter(this);
        createUserBtn = findViewById(R.id.button3);
        deleteUserBtn = findViewById(R.id.button4);
        scanUserBtn = findViewById(R.id.button2);
        checkUsersBtn = findViewById(R.id.button5);

        createUser();
        checkData();
        deleteRfid();
        scanUser();
    }

    public void createUser(){
        createUserBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MenuActivity.this, CreateUserActivity.class));
                    }
                }
        );
    }

    public void deleteRfid() {
        deleteUserBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MenuActivity.this, DeleteUserActivity.class));
                    }
                }
        );
    }

    public void checkData(){
        checkUsersBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Cursor res = myDb.getAllData();
                        if(res.getCount() == 0) {
                            // show message
                            showMessage("Error","Nothing found");
                            return;
                        }
                        StringBuilder buffer = new StringBuilder();
                        while (res.moveToNext()) {
                            buffer.append("Username :").append(res.getString(1)).append("\n");
                            buffer.append("Mail :").append(res.getString(2)).append("\n\n");
                            buffer.append("RFID :").append(res.getString(4)).append("\n\n");
                        }
                        showMessage("Data",buffer.toString());
                    }
                }
        );
    }

    public void scanUser() {
        scanUserBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MenuActivity.this, ScanUserActivity.class));
                    }
                }
        );
    }



    public void showMessage(String title,String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }
}
