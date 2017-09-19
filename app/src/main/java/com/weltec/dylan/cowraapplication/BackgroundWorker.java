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
        String login_url = "http://103.73.65.142/login.php";
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
                        URLEncoder.encode(pTLN,"UTF-8");
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
                Toast.makeText(context, "Error connecting to the server\n" + e,
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
        alert.setMessage(result);
        alert.show();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
