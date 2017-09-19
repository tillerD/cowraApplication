package com.weltec.dylan.cowraapplication;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List patrolers;
    EditText driver;
    EditText driverId;
    EditText observer;
    EditText ob1;
    EditText policeNum;
    EditText kms;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        //get text fields
        patrolers = new ArrayList();
        driver = (EditText) findViewById(R.id.driverNameField);
        driverId = (EditText) findViewById(R.id.driverIDField);
        observer = (EditText) findViewById(R.id.ob1NameField);
        ob1 = (EditText) findViewById(R.id.ob1IDField);
        policeNum = (EditText) findViewById(R.id.policeJobNumID);
        kms = (EditText) findViewById(R.id.vecStartField);
        //AddUser button listener
        Button addUser = (Button) findViewById(R.id.addUser);
        addUser.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                patrolerPopUp(v);
            }
        });
        //StartPatrol button listener
        final Button startBtn = (Button) findViewById(R.id.startButton);
        startBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startBtn(v);
            }
        });
    }
    //Patroler popup window
    public void patrolerPopUp(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        //Create edit fields for the pop up window
        LinearLayout layout = new LinearLayout(MainActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText name = new EditText(MainActivity.this);
        name.setHint("Observer Name:");
        layout.addView(name);
        final EditText patId = new EditText(MainActivity.this);
        patId.setHint("Observer ID:");
        layout.addView(patId);
        //Set the layout of the popup window
        alert.setTitle("Add Patroler")
                .setCancelable(false)
                .setView(layout)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        final AlertDialog alertDialog = alert.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.length() <= 0) {
                    Toast.makeText(MainActivity.this, "Observer Name required!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    List info = new ArrayList();
                    info.add(name.getText());
                    info.add(patId.getText());
                    patrolers.add(info);
                    Toast.makeText(MainActivity.this, "Patroler added!",
                            Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }
            }
        });
    }
    //startPatrol button
    public void startBtn(View v){
        if((driver.length() > 0) && (observer.length() > 0)
                && (policeNum.length() > 0) && (kms.length() > 0)) {
            if(driverId.length() > 0) {
                List driverInfo = new ArrayList();
                driverInfo.add(driver.getText());
                driverInfo.add(driverId.getText());
                patrolers.add(0, driverInfo);
            } else {
                patrolers.add(0, driver.getText());
            }
            if(ob1.length() > 0) {
                List ob1Info = new ArrayList();
                ob1Info.add(observer.getText());
                ob1Info.add(ob1.getText());
                patrolers.add(1, ob1Info);
            } else {
                patrolers.add(1, ob1.getText());
            }
            checkLogin(driver, observer);
        } else {
            if(driver.length() <= 0) {
                Toast.makeText(MainActivity.this, "Driver Name is required!",
                        Toast.LENGTH_LONG).show();
                driverId.setText("");
            } else if(observer.length() <= 0) {
                Toast.makeText(MainActivity.this, "Observer 1 Name is required!",
                        Toast.LENGTH_LONG).show();
                ob1.setText("");
            } else if(policeNum.length() <= 0) {
                Toast.makeText(MainActivity.this, "Police Job Number is required!",
                        Toast.LENGTH_LONG).show();
            } else if(kms.length() <= 0) {
                Toast.makeText(MainActivity.this, "Start Kms is required!",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
    public void checkLogin(EditText name1, EditText name2)
    {
        String login = "login:login";
        String[] firstName = new String[0];
        String[] secondName = new String[0];
        String type[] = new String[0];
        type = login.split(":");
        try {
            firstName = name1.getText().toString().split(" ");
            secondName = name2.getText().toString().split(" ");
        } catch(Exception e) {
            Toast.makeText(MainActivity.this,
                    "Name fields must incldue a first and last name!" + e,
                    Toast.LENGTH_LONG).show();
        }
        try {
            BackgroundWorker backgroundWorker = new BackgroundWorker(this);
            backgroundWorker.execute(type, firstName, secondName);
        } catch(Exception e) {
            Toast.makeText(MainActivity.this,
                    "Error connecting to the database!" + e,
                    Toast.LENGTH_LONG).show();
        }
    }
}