package com.weltec.dylan.cowraapplication;

import android.Manifest;
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
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import static com.weltec.dylan.cowraapplication.R.id.policeJobNumID;

public class SignIn extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    private List patrolers;
    private EditText driver;
    private EditText observer;
    private EditText policeNum;
    private EditText kms;
    private String result;
    private final Integer LOCATION = 0x1;
    private LocationManager manage;
    private LocationListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        //get permissions
        askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION);
        askForPermission(Manifest.permission.ACCESS_COARSE_LOCATION, LOCATION);
        askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE, 1);
        askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 1);
        askForPermission(Manifest.permission.ACCESS_NETWORK_STATE, 1);
        //get text fields
        patrolers = new ArrayList<String>();
        driver = (EditText) findViewById(R.id.driverNameField);
        observer = (EditText) findViewById(R.id.ob1NameField);
        policeNum = (EditText) findViewById(policeJobNumID);
        policeNum.setText("P0");
        kms = (EditText) findViewById(R.id.vecStartField);
        checkCrashed();
        //AddUser button listener
        Button addUser = (Button) findViewById(R.id.addUser);
        addUser.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(patrolers.size() < 3) {
                    patrolerPopUp(v);
                } else {
                    Toast.makeText(SignIn.this,
                            "Too many patrolers added: max is 5 per vehicle.",
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
    private void patrolerPopUp(View v) {
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
    private void startPopUp(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(SignIn.this);
        LinearLayout layout = new LinearLayout(SignIn.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final TextView name = new TextView(SignIn.this);
        name.setText("\tLogin Success!");
        layout.addView(name);
        alert.setTitle("Login Details:")
                .setCancelable(false)
                .setView(layout)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        saveToArray();
                        saveToFile();
                        Intent intent = new Intent(SignIn.this, Home.class);
                        intent.putExtra("POLICE", policeNum.getText().toString());
                        intent.putExtra("LIST", (Serializable) patrolers);
                        intent.putExtra("KMS", kms.getText().toString());
                        //clearFields();
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
            if(checkConnected()) {
                if (checkLogin(driver, observer)) {
                    if (onComplete()) {
                        startPopUp(v);
                    }
                }
            } else {
                final AlertDialog.Builder alert = new AlertDialog.Builder(this);
                LinearLayout layout = new LinearLayout(SignIn.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                final TextView name = new TextView(SignIn.this);
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
    private boolean checkLogin(EditText name1, EditText name2) {
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

    private boolean onComplete() {
        if (result.contains("Login failed")) {
            return false;
        } else {
            return true;
        }
    }

    private void saveToFile() {
        Date currentTime = Calendar.getInstance().getTime();
        String tableID = createID(currentTime);
        saveToDescription(tableID);
        saveToEvent(tableID);
        saveToLogEvent(tableID);
        saveToNotes(tableID);
        saveToPatrollers(tableID);
        saveToPeople(tableID);
        saveToProperty(tableID);
        saveToPublic(tableID);
        saveToTimeLoc(tableID, currentTime);
        saveToVehicle(tableID);
        saveToVehicleComp(tableID);
        saveToCrashSave();
    }

    private void saveToDescription(String id) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cowra";
        File dir = new File(path);
        dir.mkdirs();
        File file = new File(path, "/Description.txt");
        String desc = " ";
        for( Object temp : patrolers) {
            if(!temp.toString().equals(patrolers.get(0)) ||
                    !temp.toString().equals(patrolers.get(1))) {
                desc += temp.toString() + " - ";
            }
        }
        String[] data = {id, desc};
        save(file, data);
    }

    private void saveToEvent(String id) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cowra";
        File dir = new File(path);
        dir.mkdirs();
        File file = new File(path, "/Event.txt");
        String blank = "NULL";
        String[] data = {id, id, id, id, blank, blank, blank, blank, blank, id,
                policeNum.getText().toString(), blank, Integer.toString(1)};
        save(file, data);
    }

    private void saveToLogEvent(String id) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cowra";
        File dir = new File(path);
        dir.mkdirs();
        File file = new File(path, "/LogEvent.txt");
        String[] data = {id, kms.getText().toString()};
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

    private void saveToPatrollers(String id) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cowra";
        File dir = new File(path);
        dir.mkdirs();
        File file = new File(path, "/Patrollers.txt");
        String[] results = result.split(",");
        String[] data = {id, results[0], Integer.toString(1)};
        String[] data2 = {id, results[1], Integer.toString(0)};
        save(file, data);
        saveAppend(file, data2);
    }

    private void saveToPeople(String id) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cowra";
        File dir = new File(path);
        dir.mkdirs();
        File file = new File(path, "/People.txt");
        String[] data = {};
        save(file, data);
    }

    private void saveToProperty(String id) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cowra";
        File dir = new File(path);
        dir.mkdirs();
        File file = new File(path, "/Property.txt");
        String[] data = {};
        save(file, data);
    }

    private void saveToPublic(String id) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cowra";
        File dir = new File(path);
        dir.mkdirs();
        File file = new File(path, "/Public.txt");
        String[] data = {};
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
                android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss", time).toString() + " "};
        save(file, data);
    }

    private void saveToVehicle(String id) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cowra";
        File dir = new File(path);
        dir.mkdirs();
        File file = new File(path, "/Vehicle.txt");
        String[] data = {};
        save(file, data);
    }

    private void saveToVehicleComp(String id) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cowra";
        File dir = new File(path);
        dir.mkdirs();
        File file = new File(path, "/VehicleComp.txt");
        String[] data = {};
        save(file, data);
    }

    private void saveToCrashSave() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cowra";
        File dir = new File(path);
        dir.mkdirs();
        File file = new File(path, "/TempData.txt");
        String pat = "";
        for( Object temp : patrolers) {
            pat += temp.toString() + "-";
        }
        String[] data = {pat, policeNum.getText().toString(), kms.getText().toString()};
        save(file, data);
    }

    private void save(File file, String[] data) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
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

    private void checkCrashed() {
        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cowra";
            File dir = new File(path);
            dir.mkdirs();
            File file = new File(path, "/Event.txt");
            String[] data = load(file);
            int length = data.length-1;
            if((data[length-9].contains("NULL") && data[length].contains("0"))
                    ||(data[length].contains("1"))){
                File crashedFile = new File(path, "/TempData.txt");
                String[] crashedData = load(crashedFile);
                String[] temp = crashedData[0].split("-");
                List patts = new ArrayList<String>();
                for (String item : temp) {
                    patts.add(item.replaceAll(",",""));
                }
                Toast.makeText(this,
                        "Recovering...",
                        Toast.LENGTH_LONG).show();
                Intent intent = new Intent(SignIn.this, Home.class);
                intent.putExtra("POLICE", crashedData[1].replaceAll(",",""));
                intent.putExtra("LIST", (Serializable) patts);
                intent.putExtra("KMS", crashedData[2].replaceAll(",",""));
                //clearFields();
                startActivity(intent);
            }
        } catch (Exception e) {
            Toast.makeText(this,
                    "No files to check: " + e,
                    Toast.LENGTH_LONG).show();
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

    private void saveAppend(File file, String[] data) {
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

    private void saveToArray() {
        patrolers.add(0, observer.getText());
        patrolers.add(1, driver.getText());
    }

    private String createID(Date time) {
        String id = Integer.toString(time.getMonth()) + Integer.toString(time.getDay())
                + Integer.toString(time.getYear()) + Integer.toString(time.getHours())
                + Integer.toString(time.getMinutes()) + Integer.toString(time.getSeconds());
        return id;
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

    private ArrayList calLoc(Location location) {
        ArrayList latLon = new ArrayList();
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        latLon.add(latitude);
        latLon.add(longitude);
        return latLon;
    }

    private void clearFields() {
        try {
            patrolers.clear();
            policeNum.setText("P0");
            driver.setText("");
            observer.setText("");
            kms.setText("");
        } catch (Exception e) {

        }
    }

    private boolean checkConnected() {
        boolean connected = false;
        try {
            ConnectivityManager manager = (ConnectivityManager)
                    getSystemService(this.CONNECTIVITY_SERVICE);
            if (manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                            .getState() == NetworkInfo.State.CONNECTED) {
                //we are connected to a network
                connected = true;
            }
        } catch (Exception e) {
            Toast.makeText(this,
                    "Error: Checking internet connection! " + e,
                    Toast.LENGTH_LONG).show();
        }
        return connected;
    }
}