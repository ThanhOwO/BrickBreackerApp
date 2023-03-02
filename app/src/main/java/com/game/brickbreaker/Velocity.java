package com.game.brickbreaker;

public class Velocity {
    private  int x, y;
    public Velocity(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX(){
        return x;
    }

    public int setX(int x){
        this.x = x;
        return x;
    }

    public int getY(){
        return y;
    }

    public int setY(int y){
        this.y = y;
        return y;
    }
}
