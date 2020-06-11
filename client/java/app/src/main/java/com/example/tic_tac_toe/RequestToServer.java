package com.example.tic_tac_toe;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.Callable;

public class RequestToServer implements Callable<String> {
    private URL url;
    RequestToServer(URL url) {
        this.url = url;
    }
    public String call() throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDefaultUseCaches(false);
        Scanner s = new Scanner(conn.getInputStream()).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
