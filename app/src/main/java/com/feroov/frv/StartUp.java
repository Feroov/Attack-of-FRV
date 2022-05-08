package com.feroov.frv;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.feroov.frv.R;

public class StartUp extends AppCompatActivity
{
    MediaPlayer mediaPlayer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startup);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.start);
        mediaPlayer.start();
    }

    public void startGame(View view)
    {
        startActivity(new Intent(this, MainActivity.class));
        mediaPlayer.stop();
        finish();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mediaPlayer.stop();
    }
}
