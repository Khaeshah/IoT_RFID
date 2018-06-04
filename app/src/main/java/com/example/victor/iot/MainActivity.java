package com.example.victor.iot;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.w3c.dom.Document;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import DatabaseUtils.DatabaseWriter;

public class MainActivity extends AppCompatActivity {

    DatabaseWriter myDb;
    EditText editName;
    EditText editMail;
    EditText editPassword;
    EditText editRfid;
    Button sendBtn;
    Button getInfoBtn;
    Button deleteUserBtn;
    Button scanBtn;

    private static final String UNI_URL = "http://192.168.2.152:3161/devices";
    private static List<String> id = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDb = new DatabaseWriter(this);

        editName = findViewById(R.id.editText_name);
        editMail = findViewById(R.id.editText5_mail);
        editPassword = findViewById(R.id.editText6_psw);
        editRfid = findViewById(R.id.editText5_rfid);
        sendBtn = findViewById(R.id.button);
        getInfoBtn = findViewById(R.id.bGetInfo);
        deleteUserBtn = findViewById(R.id.bDeleteUser);
        scanBtn = findViewById(R.id.buttonScan);
        sendData();
        checkData();
        deleteRfid();
        try {
            ReadRFIDTask task = new ReadRFIDTask();
            task.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(id);
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
                            Toast.makeText(MainActivity.this, "Inserted!", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    public void checkData(){
        getInfoBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Cursor res = myDb.getAllData();
                        if(res.getCount() == 0) {
                            // show message
                            showMessage("Error","Nothing found");
                            return;
                        }
                        StringBuffer buffer = new StringBuffer();
                        while (res.moveToNext()) {
                            buffer.append("Username :"+ res.getString(1)+"\n");
                            buffer.append("Mail :"+ res.getString(2)+"\n\n");
                            buffer.append("RFID :"+ res.getString(4)+"\n\n");
                        }
                        showMessage("Data",buffer.toString());
                    }
                }
        );
    }
    public void deleteRfid() {
        deleteUserBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, DeleteUserActivity.class));
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

    static class ReadRFIDTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            try{
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                URL url = new URL(UNI_URL);
                InputStream inputStream = url.openStream();
                Document document = db.parse(inputStream);

                if (document != null){
                    return document.getElementsByTagName("id").item(0).getFirstChild().getNodeValue();
                }
            }catch (Exception e){
                System.out.println("ERROR: " + e.getMessage());
            }
            return null;
        }
    }
}
