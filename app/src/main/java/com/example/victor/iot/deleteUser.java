package com.example.victor.iot;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class deleteUser extends AppCompatActivity {
    EditText rfidToDelete;
    Button deleteRfidButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user);

        /*rfidToDelete = findViewById(R.id.labTextDelete);
        checkDeleteListener();*/
    }

    public void checkDeleteListener() {
        deleteRfidButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Continuar amb el projecte
                        Toast.makeText(deleteUser.this, "HOLA PST", Toast.LENGTH_LONG).show();
                        //startActivity(new Intent(MainActivity.this, deleteUser.class));
                    }
                }
        );
    }
}
