package com.weltec.dylan.cowraapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dylan on 30/09/2017.
 */

public class Home extends Activity{

    private String policeNum;
    private List patrolers;
    private List events;
    private TextView polNum;
    private TextView obList;
    private TextView driverName;
    private RadioGroup eventIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.end_patrol);
        policeNum = getIntent().getStringExtra("POLICE");
        patrolers = getIntent().getStringArrayListExtra("LIST");
        polNum = (TextView) findViewById(R.id.curPoliceNum);
        polNum.setText("Police Job #" + policeNum);
        driverName = (TextView) findViewById(R.id.divName);
        driverName.setText("Driver Name: " + patrolers.get(0).toString());
        obList = (TextView) findViewById(R.id.observerList);
        for(Object row : patrolers) {
            if(row.toString().contains(patrolers.get(0).toString())){
            } else {
                obList.append(row.toString() + "\n");
            }
        }
        try {
            events = getIntent().getStringArrayListExtra("IDS");
            eventIds = (RadioGroup) findViewById(R.id.eventIdList);
            eventIds.setOrientation(RadioGroup.VERTICAL);
            displayEvents(events, eventIds);
        } catch (Exception e) {
            events = new ArrayList();
        }
        Button swapDriver = (Button) findViewById(R.id.swapDriverBtn);
        swapDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapDrivers(v);
            }
        });
        Button endPatrol = (Button) findViewById(R.id.endEventBtn);
        endPatrol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOutPopUp(v);
            }
        });
        Button createEvent = (Button) findViewById(R.id.newEventBtn);
        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newEvent(v);
            }
        });
    }

    //Patroler popup window
    private void swapDrivers(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(Home.this);
        //Create edit fields for the pop up window
        LinearLayout layout = new LinearLayout(Home.this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        final Spinner patSpin = new Spinner(v.getContext());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Home.this,
                android.R.layout.simple_spinner_item,
                patrolers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        patSpin.setAdapter(adapter);
        layout.addView(patSpin);
        //Set the layout of the popup window
        alert.setTitle("Swap Driver")
                .setCancelable(false)
                .setView(layout)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Swap", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String text = patSpin.getSelectedItem().toString();
                        driverName.setText("Driver Name: " + text);
                        obList.setText("");
                        for(Object row : patrolers) {
                            if(row.toString().contains(text)){
                            } else {
                                obList.append(row.toString() + "\n");
                            }
                        }
                        Toast.makeText(Home.this,
                                "Driver swapped!",
                                Toast.LENGTH_LONG).show();
                    }
                });
        final AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    //Patroler popup window
    private void logOutPopUp(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(Home.this);
        //Create edit fields for the pop up window
        LinearLayout layout = new LinearLayout(Home.this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        final EditText kms = new EditText(Home.this);
        kms.setHint("Finishing KMs:");
        layout.addView(kms);
        //Set the layout of the popup window
        alert.setTitle("Log Out - End KMs")
                .setCancelable(false)
                .setView(layout)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        final AlertDialog alertDialog = alert.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (kms.length() <= 0) {
                            Toast.makeText(Home.this, "Finishing KMs required!",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Home.this, "Uploading!",
                                    Toast.LENGTH_SHORT).show();
                            //TODO call the method that saves and uploade everything to the database!
                            Intent intent = new Intent(Home.this, SignIn.class);
                            startActivity(intent);
                            alertDialog.dismiss();
                        }
                    }
                });
    }

    private void displayEvents(List events, RadioGroup group) {
        int num = events.size();
        final RadioButton[] rb = new RadioButton[num];
        for(int i=0; i<num; i++) {
            rb[i] = new RadioButton(this);
            rb[i].setText(events.get(i).toString());
            rb[i].setId(i);
            group.addView(rb[i]);
        }
    }

    private void newEvent(View v) {
        Intent intent = new Intent(Home.this, NewEvent.class);
        intent.putExtra("IDS", (Serializable) events);
        intent.putExtra("LIST", (Serializable) patrolers);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        // do nothing.
    }
}
