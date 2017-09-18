package com.weltec.dylan.cowraapplication;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.view.View.OnClickListener;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static android.R.attr.button;

public class MainActivity extends AppCompatActivity {
    List patrolers = new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
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
                    Toast.makeText(MainActivity.this, "Name must be entered!",
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
        EditText driver = (EditText) findViewById(R.id.driverNameField);
        EditText driverId = (EditText) findViewById(R.id.driverIDField);
        EditText observer = (EditText) findViewById(R.id.ob1NameField);
        EditText ob1 = (EditText) findViewById(R.id.ob1IDField);
        EditText policeNum = (EditText) findViewById(R.id.policeJobNumID);
        EditText kms = (EditText) findViewById(R.id.vecStartField);
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
}