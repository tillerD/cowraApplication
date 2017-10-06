package com.weltec.dylan.cowraapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dylan on 30/09/2017.
 */

public class NewEvent extends Activity {

    private Date currentTime;
    private List patrolers;
    private ArrayList<PropDetails> properties;
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
    private EditText text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_event);
        currentTime = Calendar.getInstance().getTime();
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
        properties = new ArrayList();
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
            public void onClick(View v) { vehiclePopUp(v); }});
        final Button submitEvent = (Button) findViewById(R.id.eventBtn);
        submitEvent.setText("Submit Event");
        submitEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text = (EditText) findViewById(R.id.description);
                saveData();
                closeEvent();
            }
        });
        Button cancel = (Button) findViewById(R.id.cancelBtn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeEvent();
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
        desc.setHeight(250);
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
        TextView house = new TextView(NewEvent.this);
        house.setText("House Number:");
        final EditText num = new EditText(NewEvent.this);
        num.setHint("e.g 163");
        TextView street = new TextView(NewEvent.this);
        street.setText("Street:");
        final EditText add1 = new EditText(NewEvent.this);
        add1.setHint("e.g Kent Tce");
        TextView sub = new TextView(NewEvent.this);
        sub.setText("Suburb:");
        final EditText suburb = new EditText(NewEvent.this);
        suburb.setHint("e.g Te Aro");
        TextView city = new TextView(NewEvent.this);
        city.setText("City:");
        final EditText add2 = new EditText(NewEvent.this);
        add2.setHint("e.g Wellington");
        final CheckBox burglary = new CheckBox(NewEvent.this);
        final CheckBox noise = new CheckBox(NewEvent.this);
        burglary.setText("Burglary");
        noise.setText("Noise");
        layout.addView(house);
        layout.addView(num);
        layout.addView(street);
        layout.addView(add1);
        layout.addView(sub);
        layout.addView(suburb);
        layout.addView(city);
        layout.addView(add2);
        layout.addView(burglary);
        layout.addView(noise);
        //Set the layout of the popup window
        alert.setTitle("Property Details")
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
                        int n, b;
                        if(noise.isChecked()) {
                            n = 1;
                        } else {
                            n = 0;
                        }
                        if(burglary.isChecked()) {
                            b = 1;
                        } else {
                            b = 0;
                        }
                        if(num.getText().length() > 0
                                && add1.getText().length() > 0
                                && suburb.getText().length() > 0
                                && add2.getText().length() > 0) {
                            PropDetails temp = new PropDetails(Integer.parseInt(num.getText().toString()),
                                    add1.getText().toString(),
                                    suburb.getText().toString(),
                                    add2.getText().toString(),
                                    n, b);
                            properties.add(temp);
                        } else {
                            Toast.makeText(NewEvent.this, "All fields are Required!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //vehicle button popup
    private void vehiclePopUp(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(NewEvent.this);
        LinearLayout layout = new LinearLayout(NewEvent.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final TextView lP = new TextView(NewEvent.this);
        lP.setText("License Plate:");
        final EditText plate = new EditText(NewEvent.this);
        plate.setHint("ABC123");
        final TextView mk = new TextView(NewEvent.this);
        mk.setText("Make");
        final EditText make = new EditText(NewEvent.this);
        make.setHint("Toyota");
        final TextView md = new TextView(NewEvent.this);
        md.setText("Model");
        final EditText model = new EditText(NewEvent.this);
        model.setHint("Corolla");
        final TextView c = new TextView(NewEvent.this);
        c.setText("Colour");
        final EditText color = new EditText(NewEvent.this);
        color.setHint("Red");
        final TextView y = new TextView(NewEvent.this);
        y.setText("Year");
        final EditText year = new EditText(NewEvent.this);
        year.setHint("2006");
        final TextView cs = new TextView(NewEvent.this);
        cs.setText("Class");
        final EditText cls = new EditText(NewEvent.this);
        cls.setHint("Car");
        layout.addView(lP);
        layout.addView(plate);
        layout.addView(mk);
        layout.addView(make);
        layout.addView(md);
        layout.addView(model);
        layout.addView(c);
        layout.addView(color);
        layout.addView(y);
        layout.addView(year);
        layout.addView(cs);
        layout.addView(cls);
        alert.setTitle("Vehicle Details:")
                .setCancelable(false)
                .setView(layout)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        final AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    private void saveData() {
        String tableID = eventId.getText().toString();
        saveToDescription(tableID);
        saveToNotes(tableID);
    }

    private void saveToDescription(String id) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cowra";
        File dir = new File(path);
        dir.mkdirs();
        File file = new File(path, "/Description.txt");
        String desc = spotter.getSelectedItem().toString() + " - " +
                cats.getSelectedItem().toString() + " - " + text.getText().toString();
        String[] data = {id, desc};
        save(file, data);
    }

    private void saveToNotes(String id) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cowra";
        File dir = new File(path);
        dir.mkdirs();
        File file = new File(path, "/Notes.txt");
        String[] data = {id, id};
        save(file, data);
    }

    private void saveToPeople(String id) {
        if(people.isEmpty() == false) {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cowra";
            File dir = new File(path);
            dir.mkdirs();
            File file = new File(path, "/People.txt");
            String[] data = {id, id};
            save(file, data);
        }
    }

    private void saveToProperty(String id) {
        if(properties.isEmpty() == false) {
            PropDetails temp = properties.get(0);
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cowra";
            File dir = new File(path);
            dir.mkdirs();
            File file = new File(path, "/Property.txt");
            String[] data = {id, Integer.toString(temp.getNumber()), temp.getStreet(),
                    temp.getSuburb(), temp.getCity(), Integer.toString(temp.getNoise()),
                    Integer.toString(temp.getBulglary())};
            save(file, data);
        }
    }

    private void save(File file, String[] data) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, true);
            for(int i = 0; i < data.length; i++) {
                fos.write(data[i].getBytes());
                if(i+1 < data.length) {
                    fos.write(", ".getBytes());
                }
                fos.write("\n".getBytes());
            }
            fos.close();
        } catch (Exception e) {
            Toast.makeText(this,
                    "Error: Could not save to file! " + e,
                    Toast.LENGTH_LONG).show();
            Toast.makeText(this,
                    "File info is: " + file.toString(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void closeEvent() {
        onBackPressed();
    }
}