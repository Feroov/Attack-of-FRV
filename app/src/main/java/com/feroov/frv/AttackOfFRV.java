package com.feroov.frv;

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

import com.feroov.frv.R;

import java.util.ArrayList;
import java.util.Random;

public class AttackOfFRV extends View
{
    MediaPlayer mediaPlayer;
    Context context;
    Bitmap background, lifeImage;
    Handler handler;
    long UPDATE_MILLIS = 30;
    static int screenWidth, screenHeight;
    int points = 0;
    int life = 6;
    int TEXT_SIZE = 80;
    Paint scorePaint;

    boolean paused = false;
    PlayerShip playerShip;
    EnemyOne enemyOne;

    Random random;
    ArrayList<Shot> enemyShots, playerShots;
    Explosion explosion;
    ArrayList<Explosion> explosions;
    boolean enemyShotAction = false;
    final Runnable runnable = new Runnable() { @Override public void run() { invalidate(); } };


    public AttackOfFRV(Context context)
    {
        super(context);
        this.context = context;

        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        screenWidth = size.x;
        screenHeight = size.y;

        random = new Random();
        enemyShots = new ArrayList<>();
        playerShots = new ArrayList<>();

        explosions = new ArrayList<>();
        playerShip = new PlayerShip(context);

        enemyOne = new EnemyOne(context);

        handler = new Handler();
        background = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
        lifeImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.life);

        scorePaint = new Paint();
        scorePaint.setColor(Color.WHITE);
        scorePaint.setTextSize(TEXT_SIZE);
        scorePaint.setTextAlign(Paint.Align.LEFT);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.drawBitmap(background, 0, 0, null);
        canvas.drawText("Points: " + points, 20, TEXT_SIZE, scorePaint);
        for(int i=life; i>=1; i--)
        {
            canvas.drawBitmap(lifeImage, screenWidth - lifeImage.getWidth() * i, 0, null);
        }

        if(life == 0){
            paused = true;
            handler = null;
            Intent intent = new Intent(context, GameOver.class);
            intent.putExtra("points", points);
            context.startActivity(intent);
            ((Activity) context).finish();
        }

        enemyOne.ex += enemyOne.enemyVelocity;

        if(enemyOne.ex + enemyOne.getEnemySpaceshipWidth() >= screenWidth)
        {
            enemyOne.enemyVelocity *= -1;
        }

        if(enemyOne.ex <=0) { enemyOne.enemyVelocity *= -1; }

        if(enemyShotAction == false)
        {
            if(enemyOne.ex >= 200 + random.nextInt(400))
            {
                Shot enemyShotOne = new Shot(context, enemyOne.ex + enemyOne.getEnemySpaceshipWidth() / 26, enemyOne.ey );
                enemyShots.add(enemyShotOne);

                mediaPlayer = MediaPlayer.create(context.getApplicationContext(), R.raw.shot);
                mediaPlayer.start();

                enemyShotAction = true;
            }
            if(enemyOne.ex >= 400 + random.nextInt(800))
            {
                Shot enemyShotOne = new Shot(context, enemyOne.ex + enemyOne.getEnemySpaceshipWidth() / 2, enemyOne.ey );

                mediaPlayer = MediaPlayer.create(context.getApplicationContext(), R.raw.shot);
                mediaPlayer.start();

                enemyShotAction = true;
            }
            else
            {
                Shot enemyShotOne = new Shot(context, enemyOne.ex + enemyOne.getEnemySpaceshipWidth() / 2, enemyOne.ey );
                enemyShots.add(enemyShotOne);

                mediaPlayer = MediaPlayer.create(context.getApplicationContext(), R.raw.shot);
                mediaPlayer.start();

                enemyShotAction = true;
            }
        }

        canvas.drawBitmap(enemyOne.getEnemySpaceship(), enemyOne.ex, enemyOne.ey, null);

        if(playerShip.ox > screenWidth - playerShip.getOurSpaceshipWidth())
        {
            playerShip.ox = screenWidth - playerShip.getOurSpaceshipWidth();
        }else if(playerShip.ox < 0){ playerShip.ox = 0; }

        canvas.drawBitmap(playerShip.getOurSpaceship(), playerShip.ox, playerShip.oy, null);

        for(int i=0; i < enemyShots.size(); i++)
        {
            enemyShots.get(i).shy += 15;
            canvas.drawBitmap(enemyShots.get(i).getShot(), enemyShots.get(i).shx, enemyShots.get(i).shy, null);
            if((enemyShots.get(i).shx >= playerShip.ox)
                && enemyShots.get(i).shx <= playerShip.ox
                + playerShip.getOurSpaceshipWidth()
                && enemyShots.get(i).shy >= playerShip.oy
                && enemyShots.get(i).shy <= screenHeight)
            {
                life--;
                enemyShots.remove(i);
                explosion = new Explosion(context, playerShip.ox, playerShip.oy);
                mediaPlayer = MediaPlayer.create(context.getApplicationContext(), R.raw.explosion);
                mediaPlayer.start();
                explosions.add(explosion);
            }else if(enemyShots.get(i).shy >= screenHeight){ enemyShots.remove(i); }

            if(enemyShots.size() < 1){ enemyShotAction = false; }
        }

        for(int i = 0; i < playerShots.size(); i++)
        {
            playerShots.get(i).shy -= 15;
            canvas.drawBitmap(playerShots.get(i).getShot(), playerShots.get(i).shx, playerShots.get(i).shy, null);

            if((playerShots.get(i).shx >= enemyOne.ex)
               && playerShots.get(i).shx <= enemyOne.ex + enemyOne.getEnemySpaceshipWidth()
               && playerShots.get(i).shy <= enemyOne.getEnemySpaceshipWidth()
               && playerShots.get(i).shy >= enemyOne.ey)
            {
                points++;
                playerShots.remove(i);
                explosion = new Explosion(context, enemyOne.ex, enemyOne.ey);

                mediaPlayer = MediaPlayer.create(context.getApplicationContext(), R.raw.explosion);
                mediaPlayer.start();
                explosions.add(explosion);
            } else if(playerShots.get(i).shy <=0){ playerShots.remove(i); }
        }

        for(int i=0; i < explosions.size(); i++)
        {
            canvas.drawBitmap(explosions.get(i).getExplosion(explosions.get(i).explosionFrame),
                    explosions.get(i).eX, explosions.get(i).eY, null);
            explosions.get(i).explosionFrame++;
            if(explosions.get(i).explosionFrame > 8){ explosions.remove(i); }
        }
        if(!paused){ handler.postDelayed(runnable, UPDATE_MILLIS); }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        int touchX = (int)event.getX();
        if(event.getAction() == MotionEvent.ACTION_UP)
        {
            if(playerShots.size() < 5)
            {
                Shot ourShot = new Shot(context, playerShip.ox + playerShip.getOurSpaceshipWidth() / 2, playerShip.oy);
                playerShots.add(ourShot);
                mediaPlayer = MediaPlayer.create(context.getApplicationContext(), R.raw.shot);
                mediaPlayer.start();
            }
        }

        if(event.getAction() == MotionEvent.ACTION_DOWN){ playerShip.ox = touchX; }
        if(event.getAction() == MotionEvent.ACTION_MOVE){ playerShip.ox = touchX; }
        return true;
    }
}
