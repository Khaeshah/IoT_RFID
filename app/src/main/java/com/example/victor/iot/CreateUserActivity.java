package com.example.victor.iot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import DatabaseUtils.DatabaseWriter;

public class CreateUserActivity extends AppCompatActivity {

    DatabaseWriter myDb;
    EditText editName;
    EditText editMail;
    EditText editPassword;
    EditText editRfid;
    Button sendBtn;
    Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        myDb = new DatabaseWriter(this);

        editName = findViewById(R.id.editText_name);
        editMail = findViewById(R.id.editText5_mail);
        editPassword = findViewById(R.id.editText6_psw);
        editRfid = findViewById(R.id.editText5_rfid);
        sendBtn = findViewById(R.id.button_send);
        backBtn = findViewById(R.id.button_back);
        sendData();
        goBack();
    }

    public void sendData(){
        sendBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isInserted = myDb.insertUser(
                                editName.getText().toString(),
                                editMail.getText().toString(),
                                editPassword.getText().toString(),
                                editRfid.getText().toString());
                        if (isInserted){
                            Toast.makeText(CreateUserActivity.this, "Inserted!", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(CreateUserActivity.this, "Failed!", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    public void goBack(){
        backBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(CreateUserActivity.this, MenuActivity.class));
                    }
                }
        );
    }
}
