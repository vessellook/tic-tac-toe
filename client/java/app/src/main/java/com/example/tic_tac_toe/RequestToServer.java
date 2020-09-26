package com.example.tic_tac_toe;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.Callable;

public class RequestToServer implements Callable<String> {
    private URL url;
    private String bodyString;
    RequestToServer(URL url, String bodyString) {
        this.url = url;
        this.bodyString = bodyString;
    }
    RequestToServer(URL url) {
        this.url = url;
        this.bodyString = null;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String call() throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        conn.setDefaultUseCaches(false);
        if (bodyString != null) {
            try(OutputStream os = conn.getOutputStream()) {
                byte[] input = bodyString.getBytes(StandardCharsets.UTF_8);
                os.write(input);
            }
        }
        Scanner s = new Scanner(conn.getInputStream()).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
