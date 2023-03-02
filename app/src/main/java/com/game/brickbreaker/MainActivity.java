package com.game.brickbreaker;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    MediaPlayer BGmusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BGmusic = MediaPlayer.create(MainActivity.this, R.raw.bgmusic);
        BGmusic.start();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void startGame(View view){
        GameView gameView = new GameView(this);
        setContentView(gameView);
        BGmusic.release();
    }
}