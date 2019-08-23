package com.demo.wakemeup;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.nexacro.NexacroActivity;


public class RingtonePlayingService extends Service {

    MediaPlayer mediaPlayer;
    int startId;
    boolean isRunning;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        Log.d("RingtonePlayingService", "onCreate() 실행");

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("RingtonePlayingService", "onStartCommand() 실행");

        String getState = intent.getExtras().getString("state");

        assert getState != null;

        switch (getState) {
            case "ALARMON":
                startId = 1;
                break;
            case "ALARMOFF":
                startId = 0;
                break;
            default:
                startId = 0;
                break;
        }

        // 알람음 재생중이 아니면 알람음 재생 시작
        if(!this.isRunning && startId == 1) {
            mediaPlayer = MediaPlayer.create(this, R.raw.anything);
            mediaPlayer.start();

            this.isRunning = true;
            this.startId = 0;
        }

        // 알람음 재생중이면 알람음 재생 종료
        else if(this.isRunning && startId == 0) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();

            this.isRunning = false;
            this.startId = 0;
        }

        // 알람음 재생중이 아니면 알람음 재생종료
        else if(!this.isRunning && startId == 0) {
            this.isRunning = false;
            this.startId = 0;

        }

        // 알람음 재생중이면 알람음 재생 시작
        else if(this.isRunning && startId == 1){
            this.isRunning = true;
            this.startId = 1;
        }

        else {
        }

        //FLAG_ACTIVITY_NEW_TASK
        Intent nIntent = new Intent(this, NexacroActivity.class);
        nIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(nIntent);

        return START_NOT_STICKY;
        //return START_STICKY;
    }

    @Override
    public void onDestroy() {

        Log.d("RingtonePlayingService", "onDestroy() 실행");

        super.onDestroy();
    }
}
