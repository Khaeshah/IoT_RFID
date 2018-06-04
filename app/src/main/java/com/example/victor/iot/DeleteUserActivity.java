package com.example.victor.iot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import DatabaseUtils.DatabaseWriter;

public class DeleteUserActivity extends AppCompatActivity {

    DatabaseWriter myDb;
    EditText rfidToDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user);
        myDb = new DatabaseWriter(this);
        rfidToDelete = findViewById(R.id.labTextDelete);
        checkDeleteListener("TEST");

        try {
            MainActivity.ReadRFIDTask task = new MainActivity.ReadRFIDTask();
            task.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkDeleteListener(String RFID) {
        Boolean rfidIsDeleted = false;
        while (!rfidIsDeleted) {
            if (myDb.userExist(RFID).moveToNext()) {
                Integer deletedRows = myDb.deleteData(RFID);
                if (deletedRows > 0) {
                    Toast.makeText(DeleteUserActivity.this, "RFID Deleted", Toast.LENGTH_LONG).show();
                    rfidIsDeleted = true;
                } else {
                    Toast.makeText(DeleteUserActivity.this, "RFID not Deleted", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
