package com.weltec.dylan.cowraapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Dylan on 7/10/2017.
 */

public class EditEvent extends Activity{

    private List patrolers;
    private String id;
    private int blob;
    private ArrayList<People> people;
    //private ArrayList<PropDetails> properties;
    private ArrayList<Vehicle> vehicles;
    private TextView lat;
    private TextView lon;
    private TextView time;
    private EditText desc;
    private EditText policeJobNum;
    private EditText councilJobNum;
    private Spinner spotter;
    private Spinner cats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_event);
        patrolers = getIntent().getStringArrayListExtra("LIST");
        id = getIntent().getStringExtra("IDS");
        TextView eventId = (TextView) findViewById(R.id.submitEventLabel);
        eventId.setText("Edit Event - ID: " + id);
        policeJobNum = (EditText) findViewById(R.id.policeJobNumTxtField);
        policeJobNum.setText("P0");
        councilJobNum = (EditText) findViewById(R.id.councilJobNumTxtField);
        councilJobNum.setText("C:");
        people = new ArrayList<>();
        //properties = new ArrayList<>();
        vehicles = new ArrayList<>();
        blob = 0;
        lat = (TextView) findViewById(R.id.latField);
        lon = (TextView) findViewById(R.id.lonField);
        time = (TextView) findViewById(R.id.timefield);
        getTimeLoc(id);
        desc = (EditText) findViewById(R.id.description);
        getNotes();
        spotter = (Spinner) findViewById(R.id.spotterSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditEvent.this,
                android.R.layout.simple_spinner_item,
                patrolers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spotter.setAdapter(adapter);
        cats = (Spinner) findViewById(R.id.eventSpinner);
        ArrayAdapter<CharSequence> adapt = ArrayAdapter.createFromResource(EditEvent.this,
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
        propBtn.setEnabled(false);
//        propBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                propertyPopUp(v);
//            }
//        });
        Button vehBtn = (Button) findViewById(R.id.VehicleBtn);
        vehBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { vehiclePopUp(v); }});
        final Button submitEvent = (Button) findViewById(R.id.eventBtn);
        submitEvent.setText("Submit Event");
        submitEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (desc.getText().toString().isEmpty()) {
                        desc.setText("");
                    }
                    String temp = desc.getText().toString()
                            .replaceAll("\n", "<").replaceAll("\r", ">");
                    Toast.makeText(EditEvent.this, "Starting to Save!",
                            Toast.LENGTH_SHORT).show();
                    saveData();
                    closeEvent();
                    Toast.makeText(EditEvent.this, "Event Saved!",
                            Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(EditEvent.this, "Error saving data: " + e,
                            Toast.LENGTH_SHORT).show();
                }
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

    //Public popup window
    private void publicPopUp(View v) {
        android.support.v7.app.AlertDialog.Builder alert =
                new android.support.v7.app.AlertDialog.Builder(EditEvent.this);
        //Create edit fields for the pop up window
        LinearLayout layout = new LinearLayout(EditEvent.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText desc = new EditText(EditEvent.this);
        desc.setHint("Person Description:");
        desc.setHeight(250);
        TextView bolb = new TextView(EditEvent.this);
        bolb.setText("Actual B.O.L.B.:");
        final EditText num = new EditText(EditEvent.this);
        num.setHint("e.g 12");
        layout.addView(desc);
        layout.addView(bolb);
        layout.addView(num);
        //Set the layout of the popup window
        alert.setTitle("Public Person Description")
                .setCancelable(false)
                .setView(layout)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        final android.support.v7.app.AlertDialog alertDialog = alert.create();
        alertDialog.show();
        alertDialog.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(desc.length() > 0 || num.length() > 0) {
                            if (desc.length() <= 0) {
                                desc.setText("NULL");
                            }
                            String id = createID(Calendar.getInstance().getTime());
                            if (num.length() > 0) {
                                try {
                                    People temp = new People(id, desc.getText().toString(),
                                            Integer.valueOf(num.getText().toString()));
                                    blob = Integer.valueOf(num.getText().toString());
                                    people.add(temp);
                                } catch (Exception e) {
                                    People temp = new People(id, desc.getText().toString(), 0);
                                    people.add(temp);
                                }
                            } else {
                                People temp = new People(id, desc.getText().toString(), 0);
                                people.add(temp);
                            }
                            Toast.makeText(EditEvent.this, "Person Description added!",
                                    Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        } else {
                            Toast.makeText(EditEvent.this,
                                    "All fields can not be empty!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //Property popup window
//    private void propertyPopUp(View v) {
//        final android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(NewEvent.this);
//        //Create edit fields for the pop up window
//        LinearLayout layout = new LinearLayout(EditEvent.this);
//        layout.setOrientation(LinearLayout.VERTICAL);
//        TextView house = new TextView(EditEvent.this);
//        house.setText("House Number:");
//        final EditText num = new EditText(EditEvent.this);
//        num.setHint("e.g 163");
//        TextView street = new TextView(EditEvent.this);
//        street.setText("Street:");
//        final EditText add1 = new EditText(EditEvent.this);
//        add1.setHint("e.g Kent Tce");
//        TextView sub = new TextView(EditEvent.this);
//        sub.setText("Suburb:");
//        final EditText suburb = new EditText(EditEvent.this);
//        suburb.setHint("e.g Te Aro");
//        TextView city = new TextView(EditEvent.this);
//        city.setText("City:");
//        final EditText add2 = new EditText(EditEvent.this);
//        add2.setText("Wellington");
//        final CheckBox burglary = new CheckBox(EditEvent.this);
//        final CheckBox noise = new CheckBox(EditEvent.this);
//        burglary.setText("Burglary");
//        noise.setText("Noise");
//        layout.addView(house);
//        layout.addView(num);
//        layout.addView(street);
//        layout.addView(add1);
//        layout.addView(sub);
//        layout.addView(suburb);
//        layout.addView(city);
//        layout.addView(add2);
//        layout.addView(burglary);
//        layout.addView(noise);
//        //Set the layout of the popup window
//        alert.setTitle("Property Details")
//                .setCancelable(false)
//                .setView(layout)
//                .setNegativeButton("Cancel", null)
//                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                    }
//                });
//        final android.support.v7.app.AlertDialog alertDialog = alert.create();
//        alertDialog.show();
//        alertDialog.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE)
//                .setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        int n, b;
//                        if (noise.isChecked()) {
//                            n = 1;
//                        } else {
//                            n = 0;
//                        }
//                        if (burglary.isChecked()) {
//                            b = 1;
//                        } else {
//                            b = 0;
//                        }
//                        if (num.getText().length() <= 0) {
//                            num.setText("0");
//                        }
//                        if (add1.getText().length() <= 0) {
//                            add1.setText("");
//                        }
//                        if (suburb.getText().length() <= 0) {
//                            suburb.setText("");
//                        }
//                        if (add2.getText().length() <= 0) {
//                            add2.setText("");
//                        }
//                        PropDetails temp = new PropDetails(Integer.parseInt(num.getText().toString()),
//                                add1.getText().toString(),
//                                suburb.getText().toString(),
//                                add2.getText().toString(),
//                                n, b);
//                        properties.add(temp);
//                        Toast.makeText(NewEvent.this, "Property Details saved!",
//                                Toast.LENGTH_SHORT).show();
//                        alertDialog.dismiss();
//                    }
//                });
//    }

    //vehicle button popup
    private void vehiclePopUp(View v) {
        android.support.v7.app.AlertDialog.Builder alert =
                new android.support.v7.app.AlertDialog.Builder(EditEvent.this);
        LinearLayout layout = new LinearLayout(EditEvent.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final TextView lP = new TextView(EditEvent.this);
        lP.setText("License Plate:");
        final EditText plate = new EditText(EditEvent.this);
        plate.setHint("ABC123");
        final TextView mk = new TextView(EditEvent.this);
        mk.setText("Make");
        final EditText make = new EditText(EditEvent.this);
        make.setHint("Toyota");
        final TextView md = new TextView(EditEvent.this);
        md.setText("Model");
        final EditText model = new EditText(EditEvent.this);
        model.setHint("Corolla");
        final TextView c = new TextView(EditEvent.this);
        c.setText("Colour");
        final EditText color = new EditText(EditEvent.this);
        color.setHint("Red");
        final TextView y = new TextView(EditEvent.this);
        y.setText("Year");
        final EditText year = new EditText(EditEvent.this);
        year.setHint("2006");
        final TextView cs = new TextView(EditEvent.this);
        cs.setText("Class");
        final EditText cls = new EditText(EditEvent.this);
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
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        final android.support.v7.app.AlertDialog alertDialog = alert.create();
        alertDialog.show();
        alertDialog.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(plate.length() > 0 || make.length() > 0 || model.length() > 0
                                || color.length() > 0 || year.length() > 0
                                || cls.length() > 0) {
                            if (plate.length() <= 0) {
                                plate.setText("NULL");
                            }
                            if (make.length() <= 0) {
                                make.setText("NULL");
                            }
                            if (model.length() <= 0) {
                                model.setText("NULL");
                            }
                            if (color.length() <= 0) {
                                color.setText("NULL");
                            }
                            if (year.length() <= 0) {
                                year.setText("NULL");
                            }
                            if (cls.length() <= 0) {
                                cls.setText("NULL");
                            }
                            String id = createID(Calendar.getInstance().getTime());
                            Vehicle temp = new Vehicle(plate.getText().toString(),
                                    make.getText().toString(), model.getText().toString(),
                                    color.getText().toString(), year.getText().toString(),
                                    cls.getText().toString(), id);
                            vehicles.add(temp);
                            Toast.makeText(EditEvent.this, "Vehicle Information added!",
                                    Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        } else {
                            Toast.makeText(EditEvent.this,
                                    "All fields can not be empty!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private String createID(Date time) {
        String id = Integer.toString(time.getMonth()) + Integer.toString(time.getDay())
                + Integer.toString(time.getYear()) + Integer.toString(time.getHours())
                + Integer.toString(time.getMinutes()) + Integer.toString(time.getSeconds());
        return id;
    }

    private void getTimeLoc(String eventId) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cowra";
        File dir = new File(path);
        dir.mkdirs();
        File file = new File(path, "/TimeLoc.txt");
        String[] data = load(file);
        try {
            for (int i = 0; i < data.length; i += 4) {
                String check = data[i].toString();
                String value = eventId;
                if (check.contains(value)) {
                    String latitude = data[i + 1].toString().replaceAll(",", " ");
                    String longitude = data[i + 2].toString().replaceAll(",", " ");
                    String ticToc = data[i + 3].toString().replaceAll(",", " ");
                    String[] justTime = ticToc.split("-|\\ |\\:");
                    lat.setText("Lat: " + latitude);
                    lon.setText("Lon: " + longitude);
                    time.setText(justTime[3].toString() + ":" + justTime[4].toString());
                }
            }
        } catch (Exception e) {
            Toast.makeText(EditEvent.this, "Failed getting TimeLoc data!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void getNotes() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cowra";
        File dir = new File(path);
        dir.mkdirs();
        File file = new File(path, "/Notes.txt");
        String[] data = load(file);
        try {
            for (int i = 0; i < data.length; i += 2) {
                String check = data[i].toString();
                String value = id;
                if (check.contains(value)) {
                    String temp = data[i + 1].toString();
                    getDescription(temp);
                }
            }
        } catch (Exception e) {
            Toast.makeText(EditEvent.this, "Failed getting Event description!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void getDescription(String eventId) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cowra";
        File dir = new File(path);
        dir.mkdirs();
        File file = new File(path, "/Description.txt");
        String[] data = load(file);
        try {
            for (int i = 0; i < data.length; i += 2) {
                String check = data[i].replaceAll(",", "");
                if (eventId.contains(check)) {
                    String temp = data[i + 1].replaceAll(",", " ");
                    desc.setText(temp.replaceAll("<br>", "\n").replaceAll(">", "\r")
                            .replaceAll("<",""));
                }
            }
        } catch (Exception e) {
            Toast.makeText(EditEvent.this, "Failed getting Event description!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void updateEvent(String eventId) {
        if(blob > 0) {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cowra";
            File dir = new File(path);
            dir.mkdirs();
            File file = new File(path, "/Event.txt");
            String[] temp = load(file);
            List<String> event = new ArrayList<String>(Arrays.asList(temp));
            for (int i = 0; i < event.size(); i++) {
                event.set(i, event.get(i).replaceAll(",", "").replaceAll(" ", ""));
            }
            for (int i = 0; i < event.size(); i += 13) {
                if (event.get(i).equals(eventId)) {
                    event.set(i + 8, Integer.toString(blob));
                }
            }
            String[] data = event.toArray(new String[0]);
            file.delete();
            save(file, data);
        }
    }

    private void saveData() {
        String oldID = id;
        String newID = createID(Calendar.getInstance().getTime());;
        saveToNotes(oldID, newID);
        saveToPeople(oldID);
//        saveToProperty(oldID);
        saveToPublic();
        saveToDescription(newID);
        saveToVehicle();
        saveToVehicleComp(oldID);
        updateEvent(oldID);
    }

    private void saveToDescription(String id) {
        String catt;
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cowra";
        File dir = new File(path);
        dir.mkdirs();
        if(cats.getSelectedItem().toString().contains("--Event Category--")) {
            catt = " ";
        } else {
            catt = cats.getSelectedItem().toString().replaceAll("-", "");
        }
        File file = new File(path, "/Description.txt");
        String info = spotter.getSelectedItem().toString() + "-<" + catt + ">-" +
                desc.getText().toString()
                        .replaceAll("\n", "<br>").replaceAll("\r", ">").replaceAll("'","") +
                " - " + Calendar.getInstance().getTime().toString();
        String[] data = {id, info + " "};
        save(file, data);
    }

    private void saveToNotes(String old, String id) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cowra";
        File dir = new File(path);
        dir.mkdirs();
        File file = new File(path, "/Notes.txt");
        String[] data = {old, id + " "};
        save(file, data);
    }

    private void saveToPeople(String id) {
        if(people.isEmpty() == false) {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cowra";
            File dir = new File(path);
            dir.mkdirs();
            File file = new File(path, "/People.txt");
            for(People temp : people) {
                String[] data = {id, temp.getId() + " "};
                save(file, data);
            }
        }
    }

//    private void saveToProperty(String id) {
//        if(properties.isEmpty() == false) {
//            PropDetails temp = properties.get(0);
//            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cowra";
//            File dir = new File(path);
//            dir.mkdirs();
//            File file = new File(path, "/Property.txt");
//            String[] data = {id, Integer.toString(temp.getNumber()), temp.getStreet(),
//                    temp.getSuburb(), temp.getCity(), Integer.toString(temp.getNoise()),
//                    Integer.toString(temp.getBulglary()) + " "};
//            save(file, data);
//        }
//    }

    private void saveToPublic() {
        if(people.isEmpty() == false) {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cowra";
            File dir = new File(path);
            dir.mkdirs();
            File file = new File(path, "/Public.txt");
            for(People temp : people) {
                String[] data = {temp.getId(),
                        temp.getDescription()
                                .replaceAll("\n", "<br>").replaceAll("\r", ">").replaceAll("'","")
                                + " "};
                save(file, data);
            }
        }
    }

    private void saveToVehicle() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cowra";
        File dir = new File(path);
        dir.mkdirs();
        File file = new File(path, "/Vehicle.txt");
        if(vehicles.isEmpty() == false) {
            for (Vehicle temp : vehicles) {
                String[] data = {temp.getId(), temp.getlPlate(), temp.getColor(), temp.getMake(),
                        temp.getModel(), temp.getYear(), temp.getCarClass() + " "};
                save(file, data);
            }
        }
    }

    private void saveToVehicleComp(String id) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cowra";
        File dir = new File(path);
        dir.mkdirs();
        File file = new File(path, "/VehicleComp.txt");
        for(Vehicle temp : vehicles) {
            String[] data = {id, temp.getId() + " "};
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

    private void closeEvent() {
        onBackPressed();
    }
}
