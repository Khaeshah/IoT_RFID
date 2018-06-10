package com.example.victor.iot;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import DatabaseUtils.DatabaseWriter;

import static com.example.victor.iot.ScanUserActivity.HOUSE_URL;
import static com.example.victor.iot.ScanUserActivity.INVENTORY_URL;

public class DeleteUserActivity extends AppCompatActivity {

    static DatabaseWriter myDb;
    Button deleteBtn;
    Button backBtn;
    EditText editRfid;
    CheckConnection task;
    ScheduledRfidRemoverTask st;
    private static String SCANNER_ID = "";
    public static String INVENTORY_URL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user);
        myDb = new DatabaseWriter(this);
        deleteBtn = findViewById(R.id.button6);
        backBtn = findViewById(R.id.button7);
        editRfid = findViewById(R.id.editText5_rfid2);

        deleteUser();
        goBack();

        try {
            task = new CheckConnection();
            task.execute();
            Thread.sleep(1000);
            Timer time = new Timer();
            st = new ScheduledRfidRemoverTask();
            time.schedule(st, 0, 8000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class CheckConnection extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            try{
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                URL url = new URL(HOUSE_URL);
                InputStream inputStream = url.openStream();
                Document document = db.parse(inputStream);

                if (document != null){
                    String id = document.getElementsByTagName("id").item(0).getFirstChild().getNodeValue();
                    SCANNER_ID = id;
                    INVENTORY_URL = HOUSE_URL + "/" + SCANNER_ID + "/inventory";
                    return id;
                }
            }catch (Exception e){
                System.out.println("ERROR: " + e.getMessage());
            }
            return null;
        }
    }

    class ScheduledRfidRemoverTask extends TimerTask {

        ScheduledRfidRemoverTask1 test;

        final class ScheduledRfidRemoverTask1 extends AsyncTask<String, Void, String>{

            List<String> rfidList = new ArrayList<>();

            @Override
            protected String doInBackground(String... strings) {
                try{
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    URL url = new URL(INVENTORY_URL);
                    InputStream inputStream = url.openStream();
                    Document document = db.parse(inputStream);

                    NodeList elements = document.getElementsByTagName("epc");
                    if (elements != null) {
                        for(int i = 0; i < elements.getLength(); ++i) {
                            rfidList.add(elements.item(i).getFirstChild().getNodeValue());
                        }
                    }

                    if(!rfidList.isEmpty()){
                        return rfidList.toString();
                    }
                    return null;
                }catch (Exception e){
                    System.out.println("ERROR: " + e.getMessage());
                }
                return rfidList.toString();
            }

            @Override
            protected void onPostExecute(String response) {
                if(response != null){
                    String[] rfids = response.replace("[","").replace("]","").split(",");
                    for(String rfid : rfids){
                        deleteSingleUser(rfid);
                    }
                }
            }
        }

        public void run() {
            test = new ScheduledRfidRemoverTask1();
            test.execute();
        }
    }


    public void deleteSingleUser(String finalRfidToDelete){
        if(myDb.userExist(finalRfidToDelete).moveToNext()) {
            Integer deletedRows = myDb.deleteData(finalRfidToDelete);
            if (deletedRows > 0) {
                Toast.makeText(DeleteUserActivity.this, "Deleted!", Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(DeleteUserActivity.this, "User not found!", Toast.LENGTH_LONG).show();
        }
    }


    public void deleteUser(){
        deleteBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String finalRfidToDelete = editRfid.getText().toString();
                        deleteSingleUser(finalRfidToDelete);
                    }
                }
        );
    }

    public void goBack(){
        backBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        task.cancel(true);
                        st.cancel();
                        startActivity(new Intent(DeleteUserActivity.this, MenuActivity.class));
                    }
                }
        );
    }
}
