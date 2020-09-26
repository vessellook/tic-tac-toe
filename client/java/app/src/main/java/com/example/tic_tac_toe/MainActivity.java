package com.example.tic_tac_toe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class MainActivity extends AppCompatActivity {
//    public static final String EXTRA_PLAYER_ID = "com.example.tic_tac_toe.PLAYER_ID";
//    public static final String EXTRA_PLAYER_PASSWORD = "com.example.tic_tac_toe.PLAYER_PASSWORD";
//    private int player_id;
//    private int player_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

//    public void startGame(View view) {
//        Intent intent = new Intent(this, GameActivity.class);
//        intent.putExtra(EXTRA_PLAYER_ID, player_id);
//        intent.putExtra(EXTRA_PLAYER_PASSWORD, player_password);
//        startActivity(intent);
//    }
//
//    protected void saveNewPlayerId() throws IOException, ExecutionException, InterruptedException, JSONException {
//        URL url = new URL("http", getString(R.string.host), R.integer.port, "/tic-tac-toe/api/v1.1/client-id/");
//        while (true) {
//            FutureTask<String> task = new FutureTask<>(new RequestToServer(url));
//            Thread thread = new Thread(task);
//            thread.start();
//            String message = task.get();
//            JSONObject json = new JSONObject(message);
//            if (json.getString("message-type").equals("player-id")) {
//                player_id = json.getInt("player-id");
//                player_password = json.getInt("password");
//                break;
//            }
//        }
//    }
}
