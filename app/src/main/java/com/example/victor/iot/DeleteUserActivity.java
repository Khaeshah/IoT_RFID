package com.example.victor.iot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import DatabaseUtils.DatabaseWriter;

import static com.example.victor.iot.MainActivity.SCANNED_RFID_LIST;

public class DeleteUserActivity extends AppCompatActivity {

    DatabaseWriter myDb;
    EditText rfidToDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user);
        myDb = new DatabaseWriter(this);
        rfidToDelete = findViewById(R.id.labTextDelete);
        checkDeleteListener(SCANNED_RFID_LIST);

        try {
            MainActivity.ReadRFIDTask task = new MainActivity.ReadRFIDTask();
            task.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkDeleteListener(List<String> rfids) {
        Boolean rfidIsDeleted = false;

        for (String rfid : rfids){
            //Delete
        }


        System.out.println(SCANNED_RFID_LIST);

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
}
