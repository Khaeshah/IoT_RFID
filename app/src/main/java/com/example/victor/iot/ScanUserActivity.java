package com.example.victor.iot;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import DatabaseUtils.DatabaseWriter;
import static com.example.victor.iot.ActivityUtils.showMessage;

public class ScanUserActivity extends AppCompatActivity {

    private static final String UNI_URL = "http://192.168.2.152:3161/devices";
    private static final String SIM_URL = "http://192.168.139.1:3161/devices";
    private static String SCANNER_ID = "";
    public static String INVENTORY_URL = "";
    static DatabaseWriter myDb;

    Button scanBtn;
    Button backBtn;
    EditText editRfid;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_user);
        myDb = new DatabaseWriter(this);
        scanBtn = findViewById(R.id.button6);
        backBtn = findViewById(R.id.button7);
        editRfid = findViewById(R.id.editText5_rfid2);

        scanUser();
        goBack();
        try {
            ScanUserActivity.ReadRFIDTask task = new ScanUserActivity.ReadRFIDTask();
            task.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class ReadRFIDTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            try{
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                URL url = new URL(SIM_URL);
                InputStream inputStream = url.openStream();
                Document document = db.parse(inputStream);

                if (document != null){
                    String id = document.getElementsByTagName("id").item(0).getFirstChild().getNodeValue();
                    SCANNER_ID = id;
                    INVENTORY_URL = SIM_URL + "/" + SCANNER_ID + "/inventory";
                    return id;
                }
            }catch (Exception e){
                System.out.println("ERROR: " + e.getMessage());
            }
            return null;
        }
    }

    public void scanUser(){

        scanBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String finalRfidToScan = editRfid.getText().toString();

                        if(myDb.userExist(finalRfidToScan).moveToNext()) {
                            Cursor scanRows = myDb.getUser(finalRfidToScan);

                            if(scanRows.getCount() == 0) {
                                // show message
                                showMessage("Error","Nothing found", context);
                                return;
                            }
                            StringBuilder buffer = new StringBuilder();
                            while (scanRows.moveToNext()) {
                                try {
                                    if (scanRows.getString(0) != null){
                                        JSONObject history = new JSONObject(scanRows.getString(0));
                                        buffer.append("Blood Grouping:").append(history.get("bloodGroup")).append("\n");
                                        buffer.append("User Status:").append(history.get("userStatus")).append("\n");
                                        buffer.append("Allergies:").append(history.get("allergies")).append("\n");
                                        buffer.append("Entry Date:").append(history.get("entryDate")).append("\n");
                                        buffer.append("Discharge Date:").append(history.get("dischargeDate")).append("\n");
                                    }else{
                                        showMessage("WARNING","History is empty", context);
                                        return;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                            showMessage("History",buffer.toString(), context);
                        }else {
                            Toast.makeText(ScanUserActivity.this, "User not found!", Toast.LENGTH_LONG).show();
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
                        startActivity(new Intent(ScanUserActivity.this, MenuActivity.class));
                    }
                }
        );
    }
}
