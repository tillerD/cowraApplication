package com.weltec.dylan.cowraapplication;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Dylan on 19/09/2017.
 */

public class BackgroundWorker extends AsyncTask<String[], Void, String> {
    Context context;
    AlertDialog alert;
    String type;
    String pOFN;
    String pOLN;
    String pTFN;
    String pTLN;
    List<String> dbConn;
    String login_url = null;
    String ip = null;
    String uName = null;
    String pass = null;
    String dbName = null;

    public  BackgroundWorker(Context c) {
        this.context = c;
    }

    @Override
    protected String doInBackground(String[]... params) {
        type = params[0][0];
        pOFN = params[1][0];
        pOLN = params[1][1];
        pTFN = params[2][0];
        pTLN = params[2][1];
        dbConn = new ArrayList<>();
        try {
            InputStream is = context.getAssets().open("fidget.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                dbConn.add(line);
            }
            login_url = dbConn.get(0);
            ip = dbConn.get(1);
            uName = dbConn.get(2);
            pass = dbConn.get(3);
            dbName = dbConn.get(4);
            reader.close();
            is.close();
        } catch (Exception e) {
            return "Error getting server details! " + e;
        }
        try {
            if(type.equals("login")) {
                String data = URLEncoder.encode("ip", "UTF-8")+"="+URLEncoder.encode(ip, "UTF-8");
                data+="&"+URLEncoder.encode("uName", "UTF-8")+"="+URLEncoder.encode(uName, "UTF-8");
                data+="&"+URLEncoder.encode("pass", "UTF-8")+"="+URLEncoder.encode(pass, "UTF-8");
                data+="&"+URLEncoder.encode("dbName", "UTF-8")+"="+URLEncoder.encode(dbName, "UTF-8");
                data+="&"+URLEncoder.encode("pOFN", "UTF-8")+"="+URLEncoder.encode(pOFN, "UTF-8");
                data+="&"+URLEncoder.encode("pOLN", "UTF-8")+"="+URLEncoder.encode(pOLN, "UTF-8");
                data+="&"+URLEncoder.encode("pTFN", "UTF-8")+"="+URLEncoder.encode(pTFN, "UTF-8");
                data+="&"+URLEncoder.encode("pTLN", "UTF-8")+"="+URLEncoder.encode(pTLN, "UTF-8");

                URL url = new URL(login_url);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write(data);
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = reader.readLine()) != null) {
                    sb.append(line);
                    break;
                }

                return  sb.toString();
            }
        } catch (Exception e) {
            return "Error connecting to the server! " + e;
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        alert = new AlertDialog.Builder(context).create();
        alert.setTitle("Login Status");
        alert.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
    }

    @Override
    protected void onPostExecute(String result) {
        if(result.contains("Login failed")){
            alert.setMessage(result);
            alert.show();
        } else if (isExternalStorageWritable()) {
            try {
                alert.setMessage(result);
                alert.show();
            } catch (Exception e) {
                alert.setMessage("Error creating file! " + e);
                alert.show();
            }
        } else {
            alert.setMessage("Unable to locate external storage!");
            alert.show();
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
