package com.weltec.dylan.cowraapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
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
        try {
            policeNum = getIntent().getStringExtra("POLICE");
        } catch(Exception e) {
            Toast.makeText(Home.this,
                    "Police Number unable to be retrieved!",
                    Toast.LENGTH_LONG).show();
        }
        try {
            patrolers = getIntent().getStringArrayListExtra("LIST");
        } catch (Exception e) {
            Toast.makeText(Home.this,
                    "Patrolers unable to be retrieved!",
                    Toast.LENGTH_LONG).show();
        }
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
        eventIds = (RadioGroup) findViewById(R.id.eventIdList);
        eventIds.setOrientation(RadioGroup.VERTICAL);
        events = new ArrayList();
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
        Button refreshList = (Button) findViewById(R.id.refresh);
        refreshList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshIds();
            }
        });
        final Button editEvent = (Button) findViewById(R.id.editEventBtn);
        editEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editEvent(v, eventIds);
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
                            //TODO call the method that saves and uploads everything to the database!
                            alertDialog.dismiss();
                            Home.this.finish();
                        }
                    }
                });
    }

    private void displayEvents(List events, RadioGroup group) {
        int num = events.size();
        final RadioButton[] rb = new RadioButton[num];
        group.removeAllViews();
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

    private void editEvent(View v, RadioGroup group) {
        try {
            int index = group.getCheckedRadioButtonId();
            RadioButton button = (RadioButton) findViewById(index);
            String singleId = button.getText().toString();
            String[] data = singleId.split(" ");
            Intent intent = new Intent(Home.this, EditEvent.class);
            intent.putExtra("IDS", data[0]);
            intent.putExtra("LIST", (Serializable) patrolers);
            startActivity(intent);
        } catch (Exception e){
            Toast.makeText(Home.this,
                    "No Event ID Selected!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshIds() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cowra";
        File dir = new File(path);
        dir.mkdirs();
        File file = new File(path, "/Event.txt");
        File file2 = new File(path, "/TimeLoc.txt");
        String[] data = load(file);
        String[] data2 = load(file2);
        List idList = new ArrayList();
        if(data.length > 13) {
            for (int i = 13; i < data.length; i += 13) {
                for(int j = 0; j < data2.length; j += 4) {
                    if(data2[j].toString().contains(data[i])) {
                        idList.add(data[i].toString().replaceAll(",", " ") + "Date/Time: " +
                                data2[j+3].toString().replaceAll(",", " "));
                    }
                }
            }
            displayEvents(idList, eventIds);
        } else {
            Toast.makeText(Home.this,
                    "No events to display!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public static String[] load(File file) {
        FileInputStream fis = null;
        String[] array;
        try
        {
            fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String test;
            int anzahl=0;
            while ((test=br.readLine()) != null)
            {
                anzahl++;
            }
            fis.getChannel().position(0);
            array = new String[anzahl];
            String line;
            int i = 0;
            while((line=br.readLine())!=null)
            {
                array[i] = line;
                i++;
            }
        }
        catch (Exception e) {
            array = new String[0];
        }
        return array;
    }

    @Override
    public void onBackPressed() {
        // do nothing.
    }
}
