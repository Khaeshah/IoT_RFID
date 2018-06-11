package com.example.victor.iot;

import android.annotation.SuppressLint;
import android.content.Context;
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

import static com.example.victor.iot.ActivityUtils.showMessage;
import static com.example.victor.iot.ScanUserActivity.UNI_URL;

public class DeleteUserActivity extends AppCompatActivity {

    static DatabaseWriter myDb;
    Button deleteBtn;
    Button backBtn;
    EditText editRfid;
    ScheduledRfidRemoverTask st;
    Context context = this;
    private static String SCANNER_ID = "";
    public static String INVENTORY_URL = "";
    Timer time = new Timer();
    Integer timeOut = 0;

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
            st = new ScheduledRfidRemoverTask();
            time.schedule(st, 0, 8000);
        } catch (Exception e) {
            showMessage("Error", e.getMessage(), context);
        }
    }

    public void deleteSingleUser(String finalRfidToDelete){
        finalRfidToDelete = finalRfidToDelete.trim();
        if(myDb.userExist(finalRfidToDelete).moveToNext()) {
            Integer deletedRows = myDb.deleteData(finalRfidToDelete);
            if (deletedRows > 0) {
                Toast.makeText(DeleteUserActivity.this, finalRfidToDelete + "\nDeleted!", Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(DeleteUserActivity.this, finalRfidToDelete + "\nUser not found.", Toast.LENGTH_LONG).show();
        }
    }

    public void deleteUser(){
        deleteBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String finalRfidToDelete = editRfid.getText().toString();
                        deleteSingleUser(finalRfidToDelete.trim());
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
                        startActivity(new Intent(DeleteUserActivity.this, MenuActivity.class));
                    }
                }
        );
    }

    class ScheduledRfidRemoverTask extends TimerTask {

        RemoverTask task;

        @SuppressLint("StaticFieldLeak")
        private final class RemoverTask extends AsyncTask<String, Void, String>{

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
                        if(elements.getLength() > 0){
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
                Toast.makeText(DeleteUserActivity.this, "Network Error. Please enter RFID manually", Toast.LENGTH_LONG).show();
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
            task = new RemoverTask();
            task.execute();
        }
    }
}
