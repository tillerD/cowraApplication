package com.weltec.dylan.cowraapplication;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.weltec.dylan.cowraapplication.R.id.policeJobNumID;

public class SignIn extends AppCompatActivity {
    List patrolers;
    EditText driver;
    EditText observer;
    EditText observer2;
    EditText policeNum;
    EditText kms;
    String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        //get text fields
        patrolers = new ArrayList();
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
                if(name.length() <= 0) {
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
    //startPatrol button
    public void startBtn(View v){
        if((driver.length() > 0) && (observer.length() > 0)
                && (policeNum.length() > 0) && (kms.length() > 0)) {
            checkLogin(driver, observer);
            if (onComplete()) {
                saveToArray();
                saveToFile();
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
    public void checkLogin(EditText name1, EditText name2)
    {
        String login = "login login";
        String[] firstName = new String[0];
        String[] secondName = new String[0];
        String type[] = new String[0];
        double value = 0;
        if(policeNum.getText().toString().contains("P0")) {
            try{
                value = Double.parseDouble(kms.getText().toString());
            } catch (Exception e) {
                Toast.makeText(SignIn.this,
                        "Starting kms is not a number! " + e,
                        Toast.LENGTH_LONG).show();
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
                } catch (Exception e) {
                    Toast.makeText(SignIn.this,
                            "Error connecting to the database! " + e,
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(SignIn.this,
                        "Name fields must incldue a first and last name!",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(SignIn.this,
                    "Police job number is incorrect!",
                    Toast.LENGTH_LONG).show();
        }
    }

    public boolean onComplete() {
        if(result.contains("Login failed")) {
            return false;
        } else {
            return true;
        }
    }

    public boolean saveToFile() {
        File myFile;
        Date currentTime = Calendar.getInstance().getTime();
        File directory = this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        String fileName = currentTime.toString() + ".csv";
        try {
            myFile = new File(directory, fileName);
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append("This is a test!");
            myOutWriter.append("\n");
            for (Object temp : patrolers) {

            }
            myOutWriter.append("This is a test!");
            myOutWriter.append("\n");
            myOutWriter.close();
            return true;
        } catch (Exception e) {
            Toast.makeText(SignIn.this,
                    "Error: Could not save to file! " + e,
                    Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public void saveToArray() {
        patrolers.add(0, driver.getText());
        patrolers.add(1, observer.getText());
        if(observer2.getText().length() > 0) {
            patrolers.add(2, observer2.getText());
        }
    }
}