package com.feroov.frv;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.feroov.frv.R;

import java.util.Random;

public class PlayerShip
{
    Context context;
    Bitmap ourSpaceship;
    int ox, oy;
    Random random;

    public PlayerShip(Context context)
    {
        this.context = context;
        ourSpaceship = BitmapFactory.decodeResource(context.getResources(), R.drawable.rocket1);
        random = new Random();
        ox = random.nextInt(AttackOfFRV.screenWidth);
        oy = AttackOfFRV.screenHeight - ourSpaceship.getHeight();
    }

    public Bitmap getOurSpaceship(){ return ourSpaceship; }

    int getOurSpaceshipWidth(){ return ourSpaceship.getWidth(); }
}
