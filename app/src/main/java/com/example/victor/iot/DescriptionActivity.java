package com.example.victor.iot;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONObject;

import DatabaseUtils.DatabaseWriter;

public class DescriptionActivity extends AppCompatActivity {

    DatabaseWriter myDb;
    EditText editRfid;
    Spinner bloodGroupingSpinner;
    EditText editAllergies;
    Spinner statusSpinner;
    EditText entryDateText;
    EditText dischargeDateText;
    Button sendBtn;
    Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_user);
        myDb = new DatabaseWriter(this);

        editRfid = findViewById(R.id.editText);
        bloodGroupingSpinner = findViewById(R.id.blood_spinner2);
        editAllergies = findViewById(R.id.editText2);
        statusSpinner = findViewById(R.id.status_spinner);
        entryDateText = findViewById(R.id.editText4);
        dischargeDateText = findViewById(R.id.editText3);
        sendBtn = findViewById(R.id.button_send);
        backBtn = findViewById(R.id.button_back);
        ArrayAdapter<CharSequence> bloodTypeAdapter = ArrayAdapter.createFromResource(this, R.array.blood_type, R.layout.support_simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(this, R.array.status_type, R.layout.support_simple_spinner_dropdown_item);
        bloodTypeAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        statusAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        bloodGroupingSpinner.setAdapter(bloodTypeAdapter);
        statusSpinner.setAdapter(statusAdapter);

        sendData();
        goBack();
    }

    public void sendData(){

        final String[] bloodGroup = {null};
        final String[]  userStatus = {null};

        sendBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(myDb.userExist(editRfid.getText().toString()).moveToNext()) {

                            JSONObject history = new JSONObject();

                            try{
                                String bloodGroupToSave = "";
                                String userStatusToSave = "";

                                if (bloodGroup[0] != null){
                                    bloodGroupToSave = bloodGroup[0];
                                }
                                if (userStatus[0] != null){
                                    userStatusToSave = userStatus[0];
                                }
                                history.put("bloodGroup", bloodGroupToSave);
                                history.put("userStatus", userStatusToSave);
                                history.put("allergies", editAllergies.getText().toString());
                                history.put("entryDate", entryDateText.getText().toString());
                                history.put("dischargeDate", dischargeDateText.getText().toString());
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                            boolean isInserted = myDb.insertHistory(editRfid.getText().toString(), history);
                            if (isInserted){
                                Toast.makeText(DescriptionActivity.this, "History inserted!", Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(DescriptionActivity.this, "Failed!", Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(DescriptionActivity.this, "User not found!", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );

        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                userStatus[0] = statusSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        bloodGroupingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                bloodGroup[0] = bloodGroupingSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void goBack(){
        backBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(DescriptionActivity.this, MenuActivity.class));
                    }
                }
        );
    }
}
