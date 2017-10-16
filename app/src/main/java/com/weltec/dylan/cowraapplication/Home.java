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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Dylan on 30/09/2017.
 */

public class Home extends Activity{

    private String policeNum;
    private double miles;
    private double begin;
    private List patrolers;
    private List events;
    private TextView polNum;
    private TextView obList;
    private TextView driverName;
    private RadioGroup eventIds;
    private final Integer LOCATION = 0x1;
    private LocationManager manage;
    private LocationListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.end_patrol);
        miles = 0;
        begin = 0;
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
        try {
            begin = Double.valueOf(getIntent().getStringExtra("KMS"));
        } catch (Exception e) {
            Toast.makeText(Home.this,
                    "Starting KMS unable to be retrieved!",
                    Toast.LENGTH_LONG).show();
        }
        polNum = (TextView) findViewById(R.id.curPoliceNum);
        polNum.setText("Police Job #" + policeNum);
        driverName = (TextView) findViewById(R.id.divName);
        driverName.setText("Driver Name: " + patrolers.get(1).toString());
        obList = (TextView) findViewById(R.id.observerList);
        for(Object row : patrolers) {
            if(row.toString().contains(patrolers.get(1).toString())){
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
                if(checkConnected()) {
                    logOutPopUp(v);
                } else {
                    connectFail();
                }
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
        //Location
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
            askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION);
            askForPermission(Manifest.permission.ACCESS_COARSE_LOCATION, LOCATION);
        }
        manage.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
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
                        int first=0;
                        int second=0;
                        for(Object row : patrolers) {
                            if(row.toString().contains(text)) {
                                first = patrolers.indexOf(row);
                            }
                            if(row.toString().contains(driverName.getText().toString()
                                    .replaceAll("Driver Name: ",""))) {
                                second = patrolers.indexOf(row);
                            }
                        }
                        patrolers.set(first, driverName.getText().toString()
                                .replaceAll("Driver Name: ",""));
                        patrolers.set(second, text);
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
                            if (Double.valueOf(kms.getText().toString()) > begin) {
                                try {
                                    miles = Double.parseDouble(kms.getText().toString());
                                    alertDialog.dismiss();
                                    saveToFile();
                                    Uploader send = new Uploader(Home.this);
                                    AlertDialog alert = loadGif();
                                    alert.show();
                                    if(send.upload()) {
                                        alert.dismiss();
                                        Home.this.finish();
                                    }
                                } catch (Exception e) {
                                    Toast.makeText(Home.this,
                                            "End kilometers must be a number! " + e,
                                            Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(Home.this,
                                        "Finishing KMs must be greater than the Start KMS!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void displayEvents(List events, RadioGroup group) {
        int num = events.size();
        final RadioButton[] rb = new RadioButton[num];
        group.removeAllViews();
        group.clearCheck();
        for(int i=0; i<num; i++) {
            rb[i] = new RadioButton(this);
            rb[i].setText(events.get(i).toString());
            rb[i].setId(i);
            rb[i].setTextAppearance(this, android.R.style.TextAppearance_DeviceDefault_Medium);
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
            intent.putExtra("IDS", data[2]);
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
        File file = new File(path, "/TimeLoc.txt");
        String[] data = load(file);
        List idList = new ArrayList();
        if(data.length > 4) {
            for (int i = 4; i < data.length; i += 4) {
                idList.add("Date/Time: " + data[i+3].toString().replaceAll(",", " "));
            }
            displayEvents(idList, eventIds);
        } else {
            Toast.makeText(Home.this,
                    "No events to display!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void saveToFile() {
        Date currentTime = Calendar.getInstance().getTime();
        String tableID = createID(currentTime);
        saveToEvent(tableID);
        saveToLogEvent(tableID);
        saveToTimeLoc(tableID, currentTime);
    }

    private void saveToEvent(String id) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cowra";
        File dir = new File(path);
        dir.mkdirs();
        File file = new File(path, "/Event.txt");
        String blank = "NULL";
        String[] data = {id, id, blank, id, blank, blank, blank, blank, blank, blank,
                blank, blank, Integer.toString(0)};
        save(file, data);
    }

    private void saveToLogEvent(String id) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cowra";
        File dir = new File(path);
        dir.mkdirs();
        File file = new File(path, "/LogEvent.txt");
        String[] data = {id, Double.toString(miles)};
        save(file, data);
    }

    private void saveToTimeLoc(String id, Date time) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cowra";
        File dir = new File(path);
        dir.mkdirs();
        File file = new File(path, "/TImeLoc.txt");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION);
            askForPermission(Manifest.permission.ACCESS_COARSE_LOCATION, LOCATION);
        }
        Location loc = manage.getLastKnownLocation(LocationManager.NETWORK_PROVIDER.toString());
        ArrayList locList = calLoc(loc);
        String[] data = {id, locList.get(0).toString(), locList.get(1).toString(),
                android.text.format.DateFormat.format("yyy-MM-dd hh:mm:ss", time).toString()};
        save(file, data);
    }

    private ArrayList calLoc(Location location) {
        ArrayList latLon = new ArrayList();
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        latLon.add(latitude);
        latLon.add(longitude);
        return latLon;
    }

    private void askForPermission(String permission, Integer requestCode) {
        if(ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            }
        }
    }

    private String createID(Date time) {
        String id = Integer.toString(time.getMonth()) + Integer.toString(time.getDay())
                + Integer.toString(time.getYear()) + Integer.toString(time.getHours())
                + Integer.toString(time.getMinutes()) + Integer.toString(time.getSeconds());
        return id;
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

    private static String[] load(File file) {
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

    private AlertDialog loadGif() {
        Uploader send = new Uploader(Home.this);
        ImageView gifImageView = new ImageView(this);
        AlertDialog alert = new AlertDialog.Builder(this).create();
        alert.setTitle("Uploading...");
        alert.setView(gifImageView);
        Glide.with(this).load("http://103.73.65.142/spinspinspin.gif").asGif().into(gifImageView);
        return alert;
    }

    @Override
    public void onBackPressed() {
        // do nothing.
    }

    private boolean checkConnected() {
        boolean connected = false;
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(this.CONNECTIVITY_SERVICE);
        if(manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                        .getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        return connected;
    }

    private void connectFail() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(Home.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final TextView name = new TextView(Home.this);
        name.setText("\tPlease check your internet connection, then try again.");
        layout.addView(name);
        alert.setTitle("Connection Error:")
                .setCancelable(false)
                .setView(layout)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        alert.show();
    }
}
