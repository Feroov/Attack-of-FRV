package com.feroov.frv;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.feroov.frv.R;

public class MainActivity extends AppCompatActivity
{
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(new AttackOfFRV(this));

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.song);
        mediaPlayer.start();
    }

    @Override
    protected void onDestroy() { super.onDestroy(); }

    @Override
    protected void onPause()
    {
        super.onPause();
        mediaPlayer.stop();
        mediaPlayer.release();
    }
}