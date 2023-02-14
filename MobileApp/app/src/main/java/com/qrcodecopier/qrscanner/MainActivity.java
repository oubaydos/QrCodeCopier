package com.qrcodecopier.qrscanner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    Button scanBtn,submitBtn;
    String token;
    EditText urlInput, server;
    ActivityResultLauncher<ScanOptions> barLaucher = registerForActivityResult(new ScanContract(), result->
    {
        if(result.getContents() !=null)
        {
            token = result.getContents();
            Log.i("TOKEN", "token: "+token);
            urlInput.setVisibility(View.VISIBLE);
            submitBtn.setVisibility(View.VISIBLE);
            server.setVisibility(View.VISIBLE);
            scanBtn.setVisibility(View.GONE);

        }else {
            token = null;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scanBtn = findViewById(R.id.scanbtn);
        submitBtn = findViewById(R.id.submit);
        urlInput = findViewById(R.id.urlInput);
        server = findViewById(R.id.server);

        submitBtn.setVisibility(View.GONE);
        urlInput.setVisibility(View.GONE);
        server.setVisibility(View.GONE);

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanQrCode();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (urlInput.getText().toString().length() != 0){ //needs improvement
                    sendUrl(urlInput.getText().toString());
                }
            }
        });
    }

    private void scanQrCode()
    {
        ScanOptions options = new ScanOptions();
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLaucher.launch(options);
    }

    private void sendUrl(String url){
        if (token != null && server.getText().toString().length() != 0){
            MyTask task = new MyTask();
            task.execute(url);
        }else {
            Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG);
        }


    }
    private class MyTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... params) {
            String responseBody;
            try {
                URL endpoint = new URL("http://"+server.getText().toString()+"/api");
                HttpURLConnection connection = (HttpURLConnection) endpoint.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                String requestBody = "{\"token\":\"" + token + "\", \"url\":\"" + params[0] + "\"}";

                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(requestBody.getBytes());
                outputStream.flush();
                outputStream.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    responseBody = readStream(inputStream);
                    inputStream.close();
                } else {
                    responseBody = "HTTP Error: " + responseCode;
                }
                connection.disconnect();
            } catch (Exception e) {
                responseBody = e.toString();

            }
            return responseBody;
        }
        private String readStream(InputStream inputStream) throws IOException {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead = 0;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return outputStream.toString();
        }
        protected void onPostExecute(String result) {
            Log.i("received", "message: "+result);
        }
    }


}