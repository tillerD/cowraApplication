package com.weltec.dylan.cowraapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dylan on 30/09/2017.
 */

public class Home extends Activity{

    String policeNum;
    List patrolers;
    List events;
    TextView polNum;
    TextView obList;
    TextView driverName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
                
            }
        });
    }

    //Patroler popup window
    public void swapDrivers(View v) {
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
}
