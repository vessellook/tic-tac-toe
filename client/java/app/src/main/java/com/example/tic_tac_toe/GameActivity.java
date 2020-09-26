package com.example.tic_tac_toe;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class GameActivity extends AppCompatActivity {
    private static final String host = "192.168.1.2";
    private static final int port = 5000;
    private int dim;
    private int[] cellValues;
    private int player_order;
    private int step;
    private boolean isGameStarted;
    private boolean isGameLocked;
    private String sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        GameModel gameModel = new GameModel();
        ServerApiManager.instance().setGameModel(gameModel);
        dim = 3;
        step = 0;
        cellValues = new int[dim * dim];
        for (int i = 0; i < dim * dim; i++) {
            cellValues[i] = 0;
        }
        try {
            ServerApiManager.instance().askToStartGame();
        } catch (InterruptedException | JSONException | IOException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void updateStep(int step) {

    }

    public void updateValue(int value, int col, int row) {

    }

    public void updateValues(int[] values) {

    }

    public void updateDim(int dim) {

    }

    public void updateOrder(int order) {

    }

    protected int getIdByNum(int num) {
        switch (num) {
            case 0:
                return R.id.topLeftCell;
            case 1:
                return R.id.topCell;
            case 2:
                return R.id.topRightCell;
            case 3:
                return R.id.leftCell;
            case 4:
                return R.id.centerCell;
            case 5:
                return R.id.rightCell;
            case 6:
                return R.id.bottomRightCell;
            case 7:
                return R.id.bottomCell;
            case 8:
                return R.id.bottomLeftCell;
            default:
                return 0;
        }
    }

    protected Integer getNumById(int id) {
        switch (id) {
            case R.id.topLeftCell:
                return 0;
            case R.id.topCell:
                return 1;
            case R.id.topRightCell:
                return 2;
            case R.id.leftCell:
                return 3;
            case R.id.centerCell:
                return 4;
            case R.id.rightCell:
                return 5;
            case R.id.bottomLeftCell:
                return 6;
            case R.id.bottomCell:
                return 7;
            case R.id.bottomRightCell:
                return 8;
            default:
                return -1;
        }
    }

//    protected void startNewGame() throws IOException, JSONException, InterruptedException, ExecutionException {
//        ServerApiManager.instance().askToStartGame();
//        URL url = new URL("http", host, port,
//                "/tic-tac-toe/api/v1.1/" + player_id + "/start-game/");
//        while (!isGameStarted) {
//            String bodyString = "{\"password\":" + player_password + "}";
//            FutureTask<String> task = new FutureTask<>(new RequestToServer(url, bodyString));
//            Thread thread = new Thread(task);
//            thread.start();
//            String message = task.get();
//            JSONObject json = new JSONObject(message);
//            isGameStarted = json.getBoolean("start-game");
//            if (isGameStarted) {
//                player_order = json.getInt("player-order");
//                isGameLocked = (player_order == 2);
//                if (player_order == 1) {
//                    sign = getString(R.string.x_value);
//                } else {
//                    sign = getString(R.string.o_value);
//                }
//                break;
//            }
//            Thread.sleep(10 * 1000);
//        }
//    }

//    protected void getChangesFromServer(int step) throws IOException, JSONException, ExecutionException, InterruptedException {
//        URL url = new URL("http", host, port,"/tic-tac-toe/api/v1.1/" + player_id + "/get-changes/");
//        while (true) {
//            String bodyString = "{\"password\":" + player_password + "}";
//            FutureTask<String> task = new FutureTask<>(new RequestToServer(url, bodyString));
//            Thread thread = new Thread(task);
//            thread.start();
//            String message = task.get();
//            JSONObject json = new JSONObject(message);
//            if (json.getBoolean("changed")) {
//                JSONArray values = json.getJSONArray("values");
//                Button b;
//                for (int i = 0; i < values.length(); i++) {
//                    cellValues[i] = values.getInt(i);
//                    b = findViewById(getIdByNum(i));
//                    switch(cellValues[i]) {
//                        case 0:
//                            b.setText("");
//                            break;
//                        case 1:
//                            b.setText(getString(R.string.x_value));
//                            break;
//                        case 2:
//                            b.setText(getString(R.string.o_value));
//                            break;
//                    }
//                }
//                this.step = json.getInt("step");
//                isGameLocked = false;
//                break;
//            }
//        }
//    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void cellClicked(View v) {
        int num = getNumById(v.getId());

        if (! isGameLocked && cellValues[num] == 0) {
            //TODO: обработка обычного хода/выигрыша/проигрыша/ничьей
            //TODO: блокировка ввода до хода противника
            //TODO: отправка сообщения на сервер
            //TODO: получение сообщения с сервера из снятие блокировки

            isGameLocked = true;
            cellValues[num] = player_order;
            Button b = (Button) v;
            b.setText(sign);
            step++;
            try {
                ServerApiManager.instance().sendMove(step, num / dim, num % dim);
                ServerApiManager.instance().getChangesFromServer(step);
            } catch (IOException | JSONException | ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

        } else {
            /*TODO: нужна разная реакция при неправильном вводе
             *      и при вводе не в свой ход
             */
//            try {
//                saveNewPlayerId();
//            } catch (JSONException | IOException | ExecutionException | InterruptedException e) {
//                e.printStackTrace();
//            }
            ValueAnimator colorAnim = ObjectAnimator.ofInt(v, "backgroundColor", 0xFFFFFFFF, 0xFFFFA500);
            colorAnim.setDuration(500);
            colorAnim.setEvaluator(new ArgbEvaluator());
            colorAnim.setRepeatCount(1);
            colorAnim.setRepeatMode(ValueAnimator.REVERSE);
            colorAnim.start();
        }
    }
}
