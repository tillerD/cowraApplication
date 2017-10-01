package com.weltec.dylan.cowraapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dylan on 30/09/2017.
 */

public class NewEvent extends Activity {

    private Date currentTime;
    private List events;
    private List patrolers;
    private String id;
    private double latitude;
    private double longitude;
    private Spinner spotter;
    private Spinner cats;
    private TextView eventId;
    private TextView lat;
    private TextView lon;
    private TextView time;
    private final Integer LOCATION = 0x1;
    private LocationManager manage;
    private LocationListener listener;
    private HashMap people;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_event);
        currentTime = Calendar.getInstance().getTime();
        events = getIntent().getStringArrayListExtra("IDS");
        patrolers = getIntent().getStringArrayListExtra("LIST");
        id = createID(currentTime);
        eventId = (TextView) findViewById(R.id.eventIdField);
        eventId.setText(id);
        lat = (TextView) findViewById(R.id.latField);
        lon = (TextView) findViewById(R.id.lonField);
        time = (TextView) findViewById(R.id.timefield);
        time.setText(getTime(currentTime));
        getLocation();
        lat.setText("Lat: " + Double.toString(latitude));
        lon.setText("Lon: " + Double.toString(longitude));
        people = new HashMap();
        spotter = (Spinner) findViewById(R.id.spotterSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(NewEvent.this,
                android.R.layout.simple_spinner_item,
                patrolers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spotter.setAdapter(adapter);
        cats = (Spinner) findViewById(R.id.eventSpinner);
        ArrayAdapter<CharSequence> adapt = ArrayAdapter.createFromResource(NewEvent.this,
                R.array.category,
                android.R.layout.simple_spinner_item);
        adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cats.setAdapter(adapt);
        Button pubBtn = (Button) findViewById(R.id.publiBtn);
        pubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publicPopUp(v);
            }
        });
        Button propBtn = (Button) findViewById(R.id.PropertyBtn);
        propBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                propertyPopUp(v);
            }
        });
        Button vehBtn = (Button) findViewById(R.id.VehicleBtn);
        vehBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        final Button submitEvent = (Button) findViewById(R.id.eventBtn);
        submitEvent.setText("Submit Event");
        submitEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitEvent();
            }
        });
        Button cancel = (Button) findViewById(R.id.cancelBtn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelEvent();;
            }
        });
    }

    private String createID(Date time) {
        String id = Integer.toString(time.getMonth()) + Integer.toString(time.getDay())
                + Integer.toString(time.getYear()) + Integer.toString(time.getHours())
                + Integer.toString(time.getMinutes()) + Integer.toString(time.getSeconds());
        return id;
    }

    private String getTime(Date time) {
        String id = Integer.toString(time.getHours()) + ":"
                + Integer.toString(time.getMinutes());
        return id;
    }

    private void getLocation() {
        manage = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
        }
        manage.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
        Location loc = manage.getLastKnownLocation(LocationManager.NETWORK_PROVIDER.toString());
        longitude = loc.getLongitude();
        latitude = loc.getLatitude();
    }

    //Public popup window
    private void publicPopUp(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(NewEvent.this);
        //Create edit fields for the pop up window
        LinearLayout layout = new LinearLayout(NewEvent.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText desc = new EditText(NewEvent.this);
        desc.setHint("Person Description:");
        desc.setHeight(50);
        final CheckBox box = new CheckBox(NewEvent.this);
        box.setText("BOLB");
        layout.addView(desc);
        layout.addView(box);
        //Set the layout of the popup window
        alert.setTitle("Public Person Description")
                .setCancelable(false)
                .setView(layout)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        final AlertDialog alertDialog = alert.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (desc.length() <= 0) {
                            Toast.makeText(NewEvent.this, "Description Field empty!",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            if(box.isChecked()) {
                                people.put(1, desc.getText());
                            } else {
                                people.put(0, desc.getText());
                            }
                            Toast.makeText(NewEvent.this, "Person Description added!",
                                    Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        }
                    }
                });
    }

    private void propertyPopUp(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(NewEvent.this);
        //Create edit fields for the pop up window
        LinearLayout layout = new LinearLayout(NewEvent.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText desc = new EditText(NewEvent.this);
        desc.setHint("Person Description:");
        final CheckBox box = new CheckBox(NewEvent.this);
        box.setText("BOLB");
        layout.addView(desc);
        layout.addView(box);
        //Set the layout of the popup window
        alert.setTitle("Public Person Description")
                .setCancelable(false)
                .setView(layout)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        final AlertDialog alertDialog = alert.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (desc.length() <= 0) {
                            Toast.makeText(NewEvent.this, "Description Field empty!",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            if(box.isChecked()) {
                                people.put(1, desc.getText());
                            } else {
                                people.put(0, desc.getText());
                            }
                            Toast.makeText(NewEvent.this, "Person Description added!",
                                    Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        }
                    }
                });
    }

    private void submitEvent() {
        events.add(id);
        Intent intent = new Intent(NewEvent.this, Home.class);
        intent.putExtra("IDS", (Serializable) events);
        startActivity(intent);
    }

    private void cancelEvent() {
        Intent intent = new Intent(NewEvent.this, Home.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        // do nothing.
    }
}