package com.game.brickbreaker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import java.util.Random;

public class GameView extends View {

    Context context;
    float ballx, bally;
    Velocity velocity = new Velocity(25, 32);
    Handler handler;
    final  long UPDATE_MILLIS = 30;
    Runnable runnable;
    Paint textPaint = new Paint();
    Paint healthPaint = new Paint();
    Paint brickPaint = new Paint();
    float TEXT_SIZE = 120;
    float paddleX, paddleY;
    float oldX, oldPaddleX;
    int points = 0;
    int life = 3;
    Bitmap ball, paddle;
    int dWidth, dHeight;
    int ballWidth, ballHeight;
    MediaPlayer mpHit, mpBreak, mpBGM;
    Random random;
    Brick[] bricks = new Brick[30];
    int numBrick = 0;
    int brokenBricks =0;
    boolean gameOver = false;

    //main game UI
    public GameView(Context context){
        super(context);
        this.context = context;
        ball = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
        paddle = BitmapFactory.decodeResource(getResources(), R.drawable.paddle);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };
        mpHit = MediaPlayer.create(context, R.raw.hit);
        mpBreak = MediaPlayer.create(context, R.raw.breaking);
        mpBGM = MediaPlayer.create(context, R.raw.bgmusic);
        textPaint.setColor(Color.RED);
        textPaint.setTextSize(TEXT_SIZE);
        textPaint.setTextAlign(Paint.Align.LEFT);
        healthPaint.setColor(Color.GREEN);
        brickPaint.setColor(Color.argb(255, 249, 129, 0));
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        dWidth = size.x;
        dHeight = size.y;
        random = new Random();
        ballx = random.nextInt(dWidth - 50);
        bally = dHeight/3;
        paddleY = (dHeight * 4)/5;
        paddleX = dWidth/2 - paddle.getWidth()/2;
        ballWidth = ball.getWidth();
        ballHeight = ball.getHeight();
        createBricks();
    }

    //create bricks
    private void createBricks(){
        int brickWidth = dWidth / 8;
        int brickHeight = dHeight / 16;
        for (int column = 0; column < 8; column++){
            for(int row = 0; row < 3; row++){
                bricks[numBrick] = new Brick(row, column, brickWidth, brickHeight);
                numBrick++;
            }
        }
    }

    //draw
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        ballx += velocity.getX();
        bally += velocity.getY();
        if((ballx >= dWidth - ball.getWidth()) || ballx <= 0){
            velocity.setX(velocity.getX() * -1);
        }
        if (bally <= 0){
            velocity.setY(velocity.getY() * -1);
        }
        if(bally > paddleY + paddle.getHeight()) {
            ballx = 1 + random.nextInt(dWidth - ball.getWidth() - 1);
            bally = dHeight / 3;
            velocity.setX(xVelocity());
            velocity.setY(32);
            life--;
            if (life == 0) {
                gameOver = true;
                launchGameOver();
            }
        }
            if(((ballx + ball.getWidth()) >= paddleX)
            && (ballx <= paddleX + paddle.getWidth())
            && (bally + ball.getHeight() >= paddleY)
            && (bally + ball.getHeight() <= paddleY + paddle.getHeight())){
                if(mpHit != null){
                    mpHit.start();
                }
                velocity.setX(velocity.getX() + 1);
                velocity.setY((velocity.getY() + 1) * -1);
            }
            canvas.drawBitmap(ball, ballx, bally, null);
            canvas.drawBitmap(paddle, paddleX, paddleY, null);
            for (int i = 0; i<numBrick; i++){
                if(bricks[i].getVisibility()){
                    canvas.drawRect(bricks[i].column * bricks[i].width + 1, bricks[i].row * bricks[i].height + 1, bricks[i].column * bricks[i].width + bricks[i].width - 1,
                            bricks[i].row * bricks[i].height + bricks[i].height - 1, brickPaint);
                }
            }
            canvas.drawText("" + points, 20, TEXT_SIZE, textPaint);
            if(life == 2){
                healthPaint.setColor(Color.YELLOW);
            } else if (life == 1) {
                healthPaint.setColor(Color.RED);
            }
            canvas.drawRect(dWidth-200, 30, dWidth-200 + 60 * life, 80, healthPaint);
            for (int i=0; i<numBrick; i++){
                if(bricks[i].getVisibility()){
                    if(ballx + ballWidth >= bricks[i].column * bricks[i].width
                    && ballx <= bricks[i].column * bricks[i].width + bricks[i].width
                    && bally <= bricks[i].row * bricks[i].height + bricks[i].height
                    && bally >= bricks[i].row * bricks[i].height){
                        if(mpBreak != null){
                            mpBreak.start();
                        }
                        velocity.setY((velocity.getY() + 1) * -1);
                        bricks[i].setInvisible();
                        points += 10;
                        brokenBricks++;
                        if(brokenBricks == 24){
                            launchGameOver();
                        }
                    }
                }
            }
            if(brokenBricks == numBrick){
                gameOver = true;
            }
            if(!gameOver){
                handler.postDelayed(runnable, UPDATE_MILLIS);
            }
        }

    //create touch event
    @Override
    public boolean onTouchEvent(MotionEvent event){
        float touchX = event.getX();
        float touchY = event.getY();
        if(touchY >= paddleY){
            int action = event.getAction();
            if(action == MotionEvent.ACTION_DOWN){
                oldX = event.getX();
                oldPaddleX = paddleX;
            }
            if(action == MotionEvent.ACTION_MOVE){
                float shift = oldX - touchX;
                float newPaddleX = oldPaddleX - shift;
                if(newPaddleX <= 0)
                    paddleX = 0;
                else if (newPaddleX >= dWidth - paddle.getWidth()) {
                    paddleX = dWidth - paddle.getWidth();
                }else
                    paddleX = newPaddleX;
            }
        }
        return true;
    }

    //activity when game over
    private void launchGameOver() {
        handler.removeCallbacksAndMessages(null);
        Intent intent = new Intent(context, GameOver.class);
        intent.putExtra("points", points);
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    //velocity values
    private int xVelocity() {
        int[] values = {-35, -30, -25, 25, 30, 35};
        int index = random.nextInt(6);
        return values[index];
    }
}
