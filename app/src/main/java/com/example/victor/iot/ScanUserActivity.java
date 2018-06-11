package com.example.victor.iot;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import DatabaseUtils.DatabaseWriter;
import static com.example.victor.iot.ActivityUtils.showMessage;

public class ScanUserActivity extends AppCompatActivity {

    public static final String UNI_URL = "http://192.168.2.152:3161/devices";
    private static final String SIM_URL = "http://192.168.139.1:3161/devices";
    //public static final String HOUSE_URL = "http://192.168.1.35:3161/devices";

    private static String SCANNER_ID = "";
    public static String INVENTORY_URL = "";
    static DatabaseWriter myDb;

    Button scanBtn;
    Button backBtn;
    EditText editRfid;
    Context context = this;
    ScheduledRfidScannerTask st;
    Timer time = new Timer();
    Integer timeOut = 0;

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
            st = new ScheduledRfidScannerTask();
            time.schedule(st, 0, 8000);
        } catch (Exception e) {
            showMessage("Error", e.getMessage(), context);
        }
    }

    public void scanSingleRfid(String rfid){

        rfid = rfid.trim();
        if(myDb.userExist(rfid).moveToNext()) {
            Cursor scanRows = myDb.getUser(rfid);

            if(scanRows.getCount() == 0) {
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
                        showMessage("WARNING", rfid + ".\nHistory is empty.", context);
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            showMessage("History",buffer.toString(), context);
        }else {
            Toast.makeText(ScanUserActivity.this, rfid + "\nUser not found.", Toast.LENGTH_LONG).show();
        }
    }

    public void scanUser(){
        scanBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String finalRfidToScan = editRfid.getText().toString();
                        scanSingleRfid(finalRfidToScan.trim());
                    }
                }
        );
    }

    public void goBack(){
        backBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(st != null){
                            st.cancel();
                        }
                        startActivity(new Intent(ScanUserActivity.this, MenuActivity.class));
                    }
                }
        );
    }

    class ScheduledRfidScannerTask extends TimerTask {

        ScannerTask task;

        @SuppressLint("StaticFieldLeak")
        private final class ScannerTask extends AsyncTask<String, Void, String>{

            List<String> rfidList = new ArrayList<>();
            @Override
            protected String doInBackground(String... strings) {
                try{
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    URL url = new URL(UNI_URL);
                    InputStream inputStream = url.openStream();
                    Document document = db.parse(inputStream);

                    if (document != null){
                        SCANNER_ID = document.getElementsByTagName("id").item(0).getFirstChild().getNodeValue();
                        INVENTORY_URL = UNI_URL + "/" + SCANNER_ID + "/inventory";
                    }

                    url = new URL(INVENTORY_URL);
                    inputStream = url.openStream();
                    document = db.parse(inputStream);

                    NodeList elements = document.getElementsByTagName("epc");
                    if (elements != null) {
                        if (elements.getLength() > 0){
                            timeOut = 0;
                            for(int i = 0; i < elements.getLength(); ++i) {
                                rfidList.add(elements.item(i).getFirstChild().getNodeValue());
                            }
                        }else{
                            if(timeOut > 2){
                                task.cancel(true);
                            }
                            timeOut++;
                        }
                    }

                    if(!rfidList.isEmpty()){
                        return rfidList.toString();
                    }
                    return null;
                }catch (Exception e){
                    System.out.println("ERROR: " + e.getMessage());
                    task.cancel(true);
                }
                return rfidList.toString();
            }

            @Override
            protected void onCancelled(String s) {
                time.cancel();
                time.purge();
                Toast.makeText(ScanUserActivity.this, "Network Error. Please enter RFID manually", Toast.LENGTH_LONG).show();
            }

            @Override
            protected void onPostExecute(String response) {
                if(response != null){
                    String[] rfids = response.replace("[","").replace("]","").split(",");
                    for(String rfid : rfids){
                        scanSingleRfid(rfid);
                    }
                }
            }
        }

        public void run() {
            task = new ScannerTask();
            task.execute();
        }
    }
}
