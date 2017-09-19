package com.weltec.dylan.cowraapplication;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dylan on 19/09/2017.
 */

public class BackgroundWorker extends AsyncTask<String[], Void, String> {
    Context context;
    AlertDialog alert;
    public  BackgroundWorker(Context c) {
        this.context = c;
    }

    @Override
    protected String doInBackground(String[]... params) {
        String type = params[0][0];
        String pOFN = params[1][0];
        String pOLN = params[1][1];
        String pTFN = params[2][0];
        String pTLN = params[2][1];
        List<String> dbConn = new ArrayList<>();
        String login_url = null;
        String ip = null;
        String uName = null;
        String pass = null;
        String dbName = null;
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
            e.printStackTrace();
        }
        if(type.equals("login")) {
            try {
                URL url = new URL(login_url);
                HttpURLConnection hUC = (HttpURLConnection) url.openConnection();
                hUC.setRequestMethod("POST");
                hUC.setDoOutput(true);
                hUC.setDoInput(true);
                OutputStream output = hUC.getOutputStream();
                BufferedWriter buff = new BufferedWriter(new OutputStreamWriter(output, "UTF-8"));
                String post_data = URLEncoder.encode("pOFN","UTF-8")+"="+
                        URLEncoder.encode(pOFN,"UTF-8")+"&"+URLEncoder.encode("pOLN","UTF-8")+"="+
                        URLEncoder.encode(pOLN,"UTF-8")+"&"+URLEncoder.encode("pTFN","UTF-8")+"="+
                        URLEncoder.encode(pTFN,"UTF-8")+"&"+URLEncoder.encode("pTLN","UTF-8")+"="+
                        URLEncoder.encode(pTLN,"UTF-8")+"&"+URLEncoder.encode("ip","UTF-8")+"="+
                        URLEncoder.encode(ip,"UTF-8")+"&"+URLEncoder.encode("uName","UTF-8")+"="+
                        URLEncoder.encode(uName,"UTF-8")+"&"+URLEncoder.encode("pass","UTF-8")+"="+
                        URLEncoder.encode(pass,"UTF-8")+"&"+URLEncoder.encode("dbName","UTF-8")+"="+
                        URLEncoder.encode(dbName,"UTF-8");
                buff.write(post_data);
                buff.flush();
                buff.close();
                output.close();
                InputStream input = hUC.getInputStream();
                BufferedReader read = new BufferedReader(new InputStreamReader(input,
                        "iso-8859-1"));
                String result="";
                String line;
                while((line = read.readLine()) != null) {
                    result += line;
                }
                read.close();
                input.close();
                hUC.disconnect();
                return result;
            } catch (Exception e) {
                Toast.makeText(context, "Error connecting to the server!" + e,
                        Toast.LENGTH_LONG).show();
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        alert = new AlertDialog.Builder(context).create();
        alert.setTitle("Login Status");
    }

    @Override
    protected void onPostExecute(String result) {
        if(result.contains("Login success")) {
            alert.setMessage(result);
            alert.show();
        } else {
            Toast.makeText(context, "Error connecting to the server!",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
