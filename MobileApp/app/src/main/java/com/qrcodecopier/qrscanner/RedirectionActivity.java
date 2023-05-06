package com.qrcodecopier.qrscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qrcodecopier.qrscanner.utils.MyProperties;
import com.qrcodecopier.qrscanner.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RedirectionActivity extends AppCompatActivity {

    Button submitBtn;
    EditText urlInput;

    TextView alertText, resultText;

    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redirection);

        submitBtn = findViewById(R.id.submit);
        urlInput = findViewById(R.id.urlInput);
        alertText = findViewById(R.id.alertmsg);
        resultText = findViewById(R.id.resultmsg);


        if (getIntent().hasExtra("token")){
            processToken(getIntent().getStringExtra("token"));
        }else {
            processToken(null);
        }

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = urlInput.getText().toString();
                if (input.length() != 0){ //needs improvement
                    if (verifyUrl(input))
                        sendUrl(input);
                }
            }
        });
    }

    private void showResult(String msg,boolean positive){
        resultText.setText(msg);
        if (positive){
            resultText.setTextColor(Color.BLACK);
            resultText.setBackgroundColor(Color.parseColor("#89FC00"));
        }else {
            resultText.setTextColor(Color.WHITE);
            resultText.setBackgroundColor(Color.parseColor("#DC0073"));
        }
    }
    private boolean verifyUrl(String url){
        String regex = "^((http|https)://)?(www\\.)?[a-zA-Z0-9]+(\\.[a-zA-Z]{2,})+(/.*)?$";
        return url.matches(regex);
    }
    private void processToken(String token){
        String positiveMsg=getResources().getString(R.string.positive_msg);
        String negativeMsg=getResources().getString(R.string.negative_msg);
        if (token == null || notAsExpected(token)){
            this.token = null;
            alertText.setText(negativeMsg);
            alertText.setTextColor(Color.WHITE);
            alertText.setBackgroundColor(Color.parseColor("#DC0073"));
        }else{
            this.token = token;
            alertText.setText(positiveMsg);
            alertText.setTextColor(Color.BLACK);
            alertText.setBackgroundColor(Color.parseColor("#89FC00"));
        }
    }

    private boolean notAsExpected(String token){
        return token.contains(" ;)({}:");
    }
    private void sendUrl(String url){
        if (token != null){
            MyTask task = new MyTask();
            task.execute(url);
        }else {
            String error= "Error: No token included";
            showResult(error,false);
        }


    }

    @SuppressLint("StaticFieldLeak")
    private class MyTask extends AsyncTask<String, Void, Pair<String,Boolean>> {

        MyProperties properties = MyProperties.getInstance();
        protected Pair<String,Boolean> doInBackground(String... params) {
            String responseBody;
            boolean success = true;
            try {
                URL endpoint = new URL(properties.getServerURl());
                HttpURLConnection connection = (HttpURLConnection) endpoint.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                String requestBody = Utils.buildRequestBody(token,params[0]);

                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(requestBody.getBytes());
                outputStream.flush();
                outputStream.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    responseBody="Done. ";
                    responseBody += readStream(inputStream);
                    inputStream.close();
                } else {
                    responseBody = "HTTP Error: " + responseCode;
                    success = false;
                }
                connection.disconnect();
            } catch (Exception e) {
                responseBody = e.toString();
                success = false;

            }
            return new Pair<>(responseBody,success);
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
        protected void onPostExecute(Pair<String, Boolean> result) {
            showResult(result.first,result.second);
        }
    }
}