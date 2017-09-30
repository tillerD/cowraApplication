package com.weltec.dylan.cowraapplication;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.weltec.dylan.cowraapplication.R.id.policeJobNumID;

public class SignIn extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    List patrolers;
    EditText driver;
    EditText observer;
    EditText observer2;
    EditText policeNum;
    EditText kms;
    String result;
    final Integer LOCATION = 0x1;
    LocationManager manage;
    LocationListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        //get permissions
        askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION);
        askForPermission(Manifest.permission.ACCESS_COARSE_LOCATION, LOCATION);
        //get text fields
        patrolers = new ArrayList<String>();
        driver = (EditText) findViewById(R.id.driverNameField);
        observer = (EditText) findViewById(R.id.ob1NameField);
        observer2 = (EditText) findViewById(R.id.ob2NameField);
        policeNum = (EditText) findViewById(policeJobNumID);
        kms = (EditText) findViewById(R.id.vecStartField);
        //AddUser button listener
        Button addUser = (Button) findViewById(R.id.addUser);
        addUser.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(observer2.length() > 0) {
                    if(patrolers.size() < 2) {
                        patrolerPopUp(v);
                    } else {
                        Toast.makeText(SignIn.this,
                                "Too many patrolers added: max is 5 per vehicle.",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignIn.this, "Observer 2 field is empty!",
                            Toast.LENGTH_SHORT).show();
                }

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
    public void patrolerPopUp(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(SignIn.this);
        //Create edit fields for the pop up window
        LinearLayout layout = new LinearLayout(SignIn.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText name = new EditText(SignIn.this);
        name.setHint("Observer Name:");
        layout.addView(name);
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
                        if (name.length() <= 0) {
                            Toast.makeText(SignIn.this, "Observer Name required!",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            patrolers.add(name.getText());
                            Toast.makeText(SignIn.this, "Patroler added!",
                                    Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        }
                    }
                });
    }

    //start button popup
    public void startPopUp(final View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(SignIn.this);
        LinearLayout layout = new LinearLayout(SignIn.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final TextView name = new TextView(SignIn.this);
        name.setText("Login Success!");
        layout.addView(name);
        alert.setTitle("Login Details:")
                .setCancelable(false)
                .setView(layout)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        saveToArray();
                        //saveToFile();
                        Intent intent = new Intent(SignIn.this, Home.class);
                        intent.putExtra("POLICE", policeNum.getText().toString());
                        intent.putExtra("LIST", (Serializable) patrolers);
                        startActivity(intent);
                    }
                });
        final AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    //startPatrol button
    public void startBtn(View v) {
        if ((driver.length() > 0) && (observer.length() > 0)
                && (policeNum.length() > 0) && (kms.length() > 0)) {
            if (checkLogin(driver, observer)) {
                if (onComplete()) {
                    startPopUp(v);
                }
            }
        } else {
            if (driver.length() <= 0) {
                Toast.makeText(SignIn.this, "Driver Name is required!",
                        Toast.LENGTH_LONG).show();
            } else if (observer.length() <= 0) {
                Toast.makeText(SignIn.this, "Observer 1 Name is required!",
                        Toast.LENGTH_LONG).show();
            } else if (policeNum.length() <= 0) {
                Toast.makeText(SignIn.this, "Police Job Number is required!",
                        Toast.LENGTH_LONG).show();
            } else if (kms.length() <= 0) {
                Toast.makeText(SignIn.this, "Start Kms is required!",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    //call database checker class
    public boolean checkLogin(EditText name1, EditText name2) {
        String login = "login login";
        String[] firstName = new String[0];
        String[] secondName = new String[0];
        String type[] = new String[0];
        double value = 0;
        if (policeNum.getText().toString().contains("P0")) {
            try {
                value = Double.parseDouble(kms.getText().toString());
            } catch (Exception e) {
                Toast.makeText(SignIn.this,
                        "Starting kms is not a number! " + e,
                        Toast.LENGTH_LONG).show();
                return false;
            }
            if (name1.getText().toString().contains(" ")
                    && name2.getText().toString().contains(" ")
                    && value > 0) {
                type = login.split(" ");
                firstName = name1.getText().toString().split(" ");
                secondName = name2.getText().toString().split(" ");
                try {
                    BackgroundWorker backgroundWorker = new BackgroundWorker(this);
                    backgroundWorker.execute(type, firstName, secondName);
                    result = backgroundWorker.get();
                    return true;
                } catch (Exception e) {
                    Toast.makeText(SignIn.this,
                            "Error connecting to the database! " + e,
                            Toast.LENGTH_LONG).show();
                    return false;
                }
            } else {
                Toast.makeText(SignIn.this,
                        "Name fields must incldue a first and last name!",
                        Toast.LENGTH_LONG).show();
                return false;
            }
        } else {
            Toast.makeText(SignIn.this,
                    "Police job number is incorrect!",
                    Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public boolean onComplete() {
        if (result.contains("Login failed")) {
            return false;
        } else {
            return true;
        }
    }

    public boolean saveToFile() {
        Date currentTime = Calendar.getInstance().getTime();
        String tableID = createID(currentTime);
        saveToEdit(tableID);
        saveToTimeLoc(tableID, currentTime);
        return false;
    }

    public void saveToEdit(String id) {
        File myFile;
        File directory = this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        String fileName = "Event.csv";
        try {
            myFile = new File(directory, fileName);
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(id + "," + id + "," + id + "," + id + ",,,,");
            if (patrolers.size() > 2) {
                myOutWriter.append(id);
            }
            myOutWriter.append(",,," + policeNum.getText() + ",,1");
            myOutWriter.append("\n");
            myOutWriter.close();
        } catch (Exception e) {
            Toast.makeText(SignIn.this,
                    "Error: Could not save to Event.csv! " + e,
                    Toast.LENGTH_LONG).show();
        }
    }

    public void saveToTimeLoc(String id, Date time) {
        File myFile;
        File directory = this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        String fileName = "TimeLoc.csv";
        try {
            myFile = new File(directory, fileName);
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION);
                askForPermission(Manifest.permission.ACCESS_COARSE_LOCATION, LOCATION);
            }
            Location loc = manage.getLastKnownLocation(LocationManager.NETWORK_PROVIDER.toString());
            ArrayList list = calLoc(loc);
            myOutWriter.append(id+","+list.get(0)+","+list.get(1)+","
                    +android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss", time));
            myOutWriter.append("\n");
            myOutWriter.close();
        } catch (Exception e) {
            Toast.makeText(SignIn.this,
                    "Error: Could not save to TimeLoc.csv! " + e,
                    Toast.LENGTH_LONG).show();
        }
    }

    public void saveToArray() {
        patrolers.add(0, driver.getText());
        patrolers.add(1, observer.getText());
        if (observer2.getText().length() > 0) {
            patrolers.add(2, observer2.getText());
        }
    }

    public String createID(Date time) {
        String id = Integer.toString(time.getMonth()) + Integer.toString(time.getDay())
                + Integer.toString(time.getYear()) + Integer.toString(time.getHours())
                + Integer.toString(time.getMinutes()) + Integer.toString(time.getSeconds());
        return id;
    }

    public void askForPermission(String permission, Integer requestCode) {
        if(ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            }
        } else {
            Toast.makeText(this,
                    "" + permission + " is already granted.",
                    Toast.LENGTH_LONG).show();
        }
    }

    public ArrayList calLoc(Location location) {
        ArrayList latLon = new ArrayList();
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        latLon.add(longitude);
        latLon.add(latitude);
        return latLon;
    }
}