package com.qrcodecopier.qrscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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
        if (url.matches(regex))
            return true;
        return false;
    }
    private void processToken(String token){
        String positiveMsg="We have the token, you can continue !";
        String negativeMsg="The token maybe not be correct, it is recommanded to go back and scan the right Qr Code";
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
        if (!token.contains(" ;)({}:")){
            return false;
        }
        return true;
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
    private class MyTask extends AsyncTask<String, Void, Pair<String,Boolean>> {
        protected Pair<String,Boolean> doInBackground(String... params) {
            String responseBody;
            Boolean succes = true;
            try {
                URL endpoint = new URL(Config.SERVER_URL);
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
                    responseBody="Done. ";
                    responseBody += readStream(inputStream);
                    inputStream.close();
                } else {
                    responseBody = "HTTP Error: " + responseCode;
                    succes = false;
                }
                connection.disconnect();
            } catch (Exception e) {
                responseBody = e.toString();
                succes = false;

            }
            return new Pair<>(responseBody,succes);
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