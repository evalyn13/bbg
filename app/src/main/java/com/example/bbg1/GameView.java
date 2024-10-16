package com.example.bbg1;

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
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.os.Handler;
import java.util.Random;

public class GameView extends View {

    Context context;
    float ballX, ballY;
    Velocity1 velocity = new Velocity1(25, 32);
    Handler handler;
    final long UPDATE_MILLIS = 30;
    Runnable runnable;
    Paint textPaint = new Paint();
    Paint healthPaint = new Paint();
    Paint brickPaint = new Paint();
    Paint levelPaint = new Paint(); // Added for level display
    float TEXT_SIZE = 120;
    float paddleX, paddleY;
    float oldX, oldPaddleX;
    int points = 0;
    int life = 3;
    Bitmap ball, paddle;
    int dWidth, dHeight;
    int ballWidth, ballHeight;
    MediaPlayer mpHit, mpMiss, mpBreak;
    Random random;
    Brick[] bricks = new Brick[30];
    int numBricks = 0;
    int brokenBricks = 0;
    boolean gameOver = false;
    int level = 1; // Added level variable

    public GameView(MainActivity context) {
        super(context);
        this.context = context;
        ball = BitmapFactory.decodeResource(getResources(), R.drawable.vol);
        paddle = BitmapFactory.decodeResource(getResources(), R.drawable.paddle);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };
        mpHit = MediaPlayer.create(context, R.raw.hit);
        mpMiss = MediaPlayer.create(context, R.raw.miss);
        mpBreak = MediaPlayer.create(context, R.raw.brickbreak);
        textPaint.setColor(Color.RED);
        textPaint.setTextSize(TEXT_SIZE);
        textPaint.setTextAlign(Paint.Align.LEFT);
        healthPaint.setColor(Color.GREEN);
        brickPaint.setColor(Color.argb(255, 249, 129, 0));
        levelPaint.setColor(Color.WHITE); // Set color for level display
        levelPaint.setTextSize(80); // Set text size for level display
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        dWidth = size.x;
        dHeight = size.y;
        random = new Random();
        ballX = random.nextInt(dWidth - 50);
        ballY = dHeight / 3;
        paddleY = (dHeight * 4) / 5;
        paddleX = dWidth / 2 - paddle.getWidth() / 2;
        ballWidth = ball.getWidth();
        ballHeight = ball.getHeight();
        createBricks();
    }

    private void createBricks() {
        int brickWidth = dWidth / 8;
        int brickHeight = dHeight / 16;
        numBricks = 0; // Reset the number of bricks
        for (int column = 0; column < 8; column++) {
            for (int row = 0; row < 3; row++) {
                bricks[numBricks] = new Brick(row, column, brickWidth, brickHeight);
                numBricks++;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Check for game over state and handle it immediately
        if (gameOver) {
            launchGameOver(); // Launch the game over activity if not done yet
            return; // Prevent further updates and rendering
        }

        // Your existing game logic below this line
        canvas.drawColor(Color.BLACK);
        ballX += velocity.getX();
        ballY += velocity.getY();

        if ((ballX >= dWidth - ball.getWidth()) || ballX <= 0) {
            velocity.setX(velocity.getX() * -1);
        }
        if (ballY <= 0) {
            velocity.setY(Math.abs(velocity.getY())); // Ensure the ball moves downwards
        }
        if (ballY > paddleY + paddle.getHeight()) {
            ballX = 1 + random.nextInt(dWidth - ball.getWidth() - 1);
            ballY = dHeight / 3;
            if (mpMiss != null) {
                mpMiss.start();
            }
            velocity.setX(xVelocity1());
            velocity.setY(32);
            life--;
            if (life == 0) {
                gameOver = true; // Set game over flag
                // No need to call launchGameOver here, it will be handled in the check above
            }
        }

        if (((ballX + ball.getWidth()) >= paddleX)
                && (ballX <= paddleX + paddle.getWidth())
                && (ballY + ball.getHeight() >= paddleY)
                && (ballY + ball.getHeight() <= paddleY + paddle.getHeight())) {
            if (mpHit != null) {
                mpHit.start();
            }
            velocity.setX(velocity.getX() + 1);
            velocity.setY((velocity.getY() + 1) * -1);
        }

        canvas.drawBitmap(ball, ballX, ballY, null);
        canvas.drawBitmap(paddle, paddleX, paddleY, null);

        for (int i = 0; i < numBricks; i++) {
            if (bricks[i].getVisibility()) {
                canvas.drawRect(bricks[i].column * bricks[i].width + 1, bricks[i].row * bricks[i].height + 1,
                        bricks[i].column * bricks[i].width + bricks[i].width - 1,
                        bricks[i].row * bricks[i].height + bricks[i].height - 1, brickPaint);
            }
        }

        canvas.drawText("Points: " + points, 20, TEXT_SIZE, textPaint);
        if (life == 2) {
            healthPaint.setColor(Color.YELLOW);
        } else if (life == 1) {
            healthPaint.setColor(Color.RED);
        }
        canvas.drawRect(dWidth - 200, 30, dWidth - 200 + 60 * life, 80, healthPaint);
        canvas.drawText("Level: " + level, 20, dHeight - 100, levelPaint); // Draw current level

        for (int i = 0; i < numBricks; i++) {
            if (bricks[i].getVisibility()) {
                if (ballX + ballWidth >= bricks[i].column * bricks[i].width
                        && ballX <= bricks[i].column * bricks[i].width + bricks[i].width
                        && ballY + ballHeight >= bricks[i].row * bricks[i].height
                        && ballY <= bricks[i].row * bricks[i].height + bricks[i].height) {
                    if (mpBreak != null) {
                        mpBreak.start();
                    }
                    velocity.setY((velocity.getY() + 1) * -1);
                    bricks[i].setInvisible();
                    points += 10;
                    brokenBricks++;

                    // Check for level up
                    if (brokenBricks == numBricks) {
                        levelUp(); // Call level up method
                    }
                }
            }
        }

        handler.postDelayed(runnable, UPDATE_MILLIS);
    }

    private void levelUp() {
        level++;
        brokenBricks = 0; // Reset broken bricks
        velocity.setX((int)(velocity.getX() * 1.1)); // Increase ball speed
        velocity.setY((int)(velocity.getY() * 1.1)); // Increase vertical speed
        createBricks(); // Create new set of bricks
    }

    private void launchGameOver() {
        handler.removeCallbacksAndMessages(null);
        Intent intent = new Intent(context, GameOver.class);
        intent.putExtra("points", points);
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    private int xVelocity1() {
        int[] values = {-35, -30, -25, 25, 30, 35};
        int index = random.nextInt(6);
        return values[index];
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        if (touchY >= paddleY) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                oldX = event.getX();
                oldPaddleX = paddleX;
            }
            if (action == MotionEvent.ACTION_MOVE) {
                float shift = oldX - touchX;
                float newPaddleX = oldPaddleX - shift;
                if (newPaddleX >= dWidth - paddle.getWidth())
                    paddleX = dWidth - paddle.getWidth();
                else if (newPaddleX < 0)
                    paddleX = 0; // Prevent paddle from going off-screen
                else
                    paddleX = newPaddleX;
            }
        }
        return true;
    }
}
