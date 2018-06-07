package com.example.victor.iot;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import DatabaseUtils.DatabaseWriter;

public class DeleteUserActivity extends AppCompatActivity {

    static DatabaseWriter myDb;
    public static List <String> SCANNED_RFID_LIST;
    static Timer time = new Timer(); // Instantiate Timer Object
    static boolean isTimeRunning = true;
    Button deleteBtn;
    Button backBtn;
    EditText editRfid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user);
        myDb = new DatabaseWriter(this);
        // rfidToDelete = findViewById(R.id.labTextDelete);
        SCANNED_RFID_LIST = new ArrayList<>();
        deleteBtn = findViewById(R.id.button6);
        backBtn = findViewById(R.id.button7);
        editRfid = findViewById(R.id.editText5_rfid2);
        deleteSingleUser();
        goBack();

        try {
            /*MainActivity.ReadRFIDTask task = new MainActivity.ReadRFIDTask();
            task.execute();*/
            ScanInventoryTask scanTask = new ScanInventoryTask();
            scanTask.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onPause() {
        super.onPause();
        time.cancel();
        time.purge();
        isTimeRunning = false;
        System.out.println("HE PAUSAT EL DELETE");
    }

    protected void onResume() {
        super.onResume();
        time = new Timer();
        isTimeRunning = true;
        System.out.println("HE RESUMIT EL DELETE");
    }



    public void deleteRfids(List<String> rfids) {
        try {
            for (String rfid : rfids) {
                deleteSingleUser();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //System.out.println(SCANNED_RFID_LIST);

        // DELETE WORKING NO BORRAR
        /*while (!rfidIsDeleted) {
            if (myDb.userExist(RFID).moveToNext()) {
                Integer deletedRows = myDb.deleteData(RFID);
                if (deletedRows > 0) {
                    Toast.makeText(DeleteUserActivity.this, "RFID Deleted", Toast.LENGTH_LONG).show();
                    rfidIsDeleted = true;
                } else {
                    Toast.makeText(DeleteUserActivity.this, "RFID not Deleted", Toast.LENGTH_LONG).show();
                }
            }
        }*/
    }
    public static void setDeleteUserScanList(List<String> sl) {
        SCANNED_RFID_LIST.clear();
        SCANNED_RFID_LIST.addAll(sl);
        System.out.println(SCANNED_RFID_LIST);
    }

    public void deleteSingleUser(){

        deleteBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String finalRfidToDelete = editRfid.getText().toString();

                        if(myDb.userExist(finalRfidToDelete).moveToNext()) {
                            Integer deletedRows = myDb.deleteData(finalRfidToDelete);
                            if (deletedRows > 0) {
                                Toast.makeText(DeleteUserActivity.this, "Deleted!", Toast.LENGTH_LONG).show();
                            }
                        }else {
                            Toast.makeText(DeleteUserActivity.this, "User not found!", Toast.LENGTH_LONG).show();
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
                        startActivity(new Intent(DeleteUserActivity.this, MenuActivity.class));
                    }
                }
        );
    }
}
