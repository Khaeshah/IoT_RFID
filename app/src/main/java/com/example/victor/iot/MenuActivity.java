package com.example.victor.iot;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import DatabaseUtils.DatabaseWriter;

import static com.example.victor.iot.ActivityUtils.showMessage;

public class MenuActivity extends AppCompatActivity {

    DatabaseWriter myDb;
    Button createUserBtn;
    Button deleteUserBtn;
    Button scanUserBtn;
    Button checkUsersBtn;
    Button descriptionBtn;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        myDb = new DatabaseWriter(this);
        createUserBtn = findViewById(R.id.button3);
        deleteUserBtn = findViewById(R.id.button4);
        scanUserBtn = findViewById(R.id.button2);
        checkUsersBtn = findViewById(R.id.button5);
        descriptionBtn = findViewById(R.id.buttonDescription);
        createUser();
        checkData();
        deleteRfid();
        scanUser();
        insertUserDescription();
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
                            showMessage("Error","Nothing found", context);
                            return;
                        }
                        StringBuilder buffer = new StringBuilder();
                        while (res.moveToNext()) {
                            buffer.append("Username :").append(res.getString(1)).append("\n");
                            buffer.append("Mail :").append(res.getString(2)).append("\n\n");
                            buffer.append("RFID :").append(res.getString(4)).append("\n\n");
                            buffer.append("Description: ").append(res.getString(5)).append("\n\n");
                        }
                        showMessage("User",buffer.toString(), context);
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

    public void insertUserDescription(){
        descriptionBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MenuActivity.this, DescriptionActivity.class));
                    }
                }
        );
    }
}
