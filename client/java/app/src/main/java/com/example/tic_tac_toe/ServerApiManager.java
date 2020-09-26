package com.example.tic_tac_toe;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;


/*
 * current API version: v1.1
 */
class ServerApiManager {
    private static ServerApiManager singleton;

    static ServerApiManager instance() {
        if (singleton == null) {
            singleton = new ServerApiManager();
            try {
                singleton.requestClientId();
            } catch (JSONException | ExecutionException | InterruptedException | MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return singleton;
    }

    private GameModel gameModel;

    public void setGameModel(GameModel gameModel) {
        this.gameModel = gameModel;
    }

    private static final String host = "192.168.1.2";
    private static final int port = 5000;
    private int clientId;
    private int clientPassword;

    private ServerApiManager() {}

    private String request(URL url) throws ExecutionException, InterruptedException {
        FutureTask<String> task = new FutureTask<>(new RequestToServer(url));
        Thread thread = new Thread(task);
        thread.start();
        return task.get();
    }

    private String request(URL url, String body) throws ExecutionException, InterruptedException {
        FutureTask<String> task = new FutureTask<>(new RequestToServer(url, body));
        Thread thread = new Thread(task);
        thread.start();
        return task.get();
    }

    private void requestClientId() throws JSONException, ExecutionException, InterruptedException, MalformedURLException {
        URL url = new URL("http", host, port, "/tic-tac-toe/api/v1.1/client-id/");
        while (true) {
            String message = request(url);
            JSONObject json = new JSONObject(message);
            if (json.has("message-type") && json.getString("message-type").equals("client-id")) {
                clientId = json.getInt("client-id");
                clientPassword = json.getInt("password");
                break;
            }
            Thread.sleep(10*1000);
        }
    }

    public void askToStartGame() throws JSONException, MalformedURLException, ExecutionException, InterruptedException {
        URL url = new URL("http", host, port, "/tic-tac-toe/api/v1.1/client-id/" + clientId + "/start-game/");
        String bodyString = "{\"password\":" + clientPassword + "}";
        while (true) {
            String message = request(url);
            JSONObject json = new JSONObject(message);
            if (json.has("message-type") &&
                    json.getString("message-type").equals("start-game") &&
                    json.getBoolean("start-game")) {
                gameModel.setOrder(json.getInt("player-order"));
                break;
            }
            Thread.sleep(10*1000);
        }
    }

    void sendMove(int col, int row) throws MalformedURLException, JSONException, ExecutionException, InterruptedException {
        sendMove(gameModel.getStep(), col, row);
    }

    void sendMove(int step, int col, int row) throws MalformedURLException, JSONException, ExecutionException, InterruptedException {
        URL url = new URL("http", host, port, "/tic-tac-toe/api/v1.1/" + clientId + "/make-move/");
        @SuppressLint("DefaultLocale") String bodyString = String.format("{\"password\":%d, \"step\": %d, \"col\": %d, \"row\": %d}", clientPassword, step, col, row);;
        String message = request(url, bodyString);
        JSONObject json = new JSONObject(message);
        if (json.has("message-type") &&
                json.getString("message-type").equals("accept-move") &&
                json.getBoolean("accepted")) {
            gameModel.setStep(step + 1);
            gameModel.setValue(gameModel.getOrder(), col, row);
        }
    }

    private int[] convertJSONArray(JSONArray jsonArray) throws JSONException {
        int[] array = new int[jsonArray.length()];
        for (int i = 0; i < array.length; i++) {
            array[i] = jsonArray.getInt(i);
        }
        return array;
    }

    void getChangesFromServer(int step) throws IOException, JSONException, ExecutionException, InterruptedException {
        URL url = new URL("http", host, port,"/tic-tac-toe/api/v1.1/" + clientId + "/get-changes/");
        String bodyString = "{\"password\":" + clientPassword + "}";
        while (true) {
            String message = request(url, bodyString);
            JSONObject json = new JSONObject(message);
            if (json.has("message-type") &&
                    json.getString("message-type").equals("game-changes") &&
                    json.getBoolean("changed")) {
                gameModel.setStep(json.getInt("step"));
                gameModel.setValues(convertJSONArray(json.getJSONArray("values")));
                break;
            }
        }
    }


}
