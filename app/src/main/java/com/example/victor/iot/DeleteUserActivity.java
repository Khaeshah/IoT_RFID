package com.example.victor.iot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import DatabaseUtils.DatabaseWriter;

public class DeleteUserActivity extends AppCompatActivity {

    static DatabaseWriter myDb;
    EditText rfidToDelete;
    public static List <String> SCANNED_RFID_LIST;
    static Timer time = new Timer(); // Instantiate Timer Object
    static boolean isTimeRunning = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user);
        myDb = new DatabaseWriter(this);
        // rfidToDelete = findViewById(R.id.labTextDelete);
        SCANNED_RFID_LIST = new ArrayList<>();

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



    public static void deleteRfids(List<String> rfids) {
        try {
            for (String rfid : rfids) {
                if(myDb.userExist(rfid).moveToNext()) {
                    Integer deletedRows = myDb.deleteData(rfid);
                    if (deletedRows > 0) {
                        //Toast.makeText(.this,"RFID Deleted: " + rfid, Toast.LENGTH_LONG).show();
                        //Toast.makeText(DeleteUserActivity.this, "RFID Deleted: " + rfid, Toast.LENGTH_LONG).show();
                        System.out.println("SA BORRAT " + rfid);
                    }
                    else {
                        System.out.println("NO SA BORRAO RES");
                        //Toast.makeText(DeleteUserActivity.this, "NO DELETED", Toast.LENGTH_LONG).show();
                    }
                }
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
}
