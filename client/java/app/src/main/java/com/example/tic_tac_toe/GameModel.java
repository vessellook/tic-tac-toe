package com.example.tic_tac_toe;

public class GameModel {
    private int dim;
    private int step;
    private int[] values;
    private int order;
    private GameActivity subscriber;

    GameModel() {
        dim = 3;
        values = new int[9];
//        this.order = order;
    }

    GameModel(int dim) {
        this.dim = dim;
//        this.order = order;
        values = new int[dim*dim];
    }

    void accept(GameActivity gameActivity) {
        subscriber = gameActivity;
    }

    void setValue(int value, int col, int row) {
        values[col * dim + row] = value;
        if(subscriber != null) subscriber.updateValue(value, col, row);
    }

    void setStep(int step) {
        this.step = step;
        if(subscriber != null) subscriber.updateStep(step);
    }

    void setValues(int[] values) {
        this.values = values;
        if(subscriber != null) subscriber.updateValues(values);
    }

    void setDim(int dim) {
        this.dim = dim;
        if(subscriber != null) subscriber.updateDim(dim);
    }

    void setOrder(int order) {
        this.order = order;
        if(subscriber != null) subscriber.updateOrder(order);
    }

    public int[] getValues() {
        return values;
    }

    int getStep() {
        return step;
    }

    int getDim() {
        return dim;
    }

    int getOrder() { return order; }
}
