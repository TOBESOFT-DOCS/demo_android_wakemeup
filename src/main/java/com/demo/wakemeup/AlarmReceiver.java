package com.demo.wakemeup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // intent로부터 전달받은 알람 상태값
        String strAlarmState = intent.getExtras().getString("state");


        Log.i("AlarmReceiver", "onReceive(): strAlarmState="+ strAlarmState);

        // RingtonePlayingService 서비스 intent 생성
        Intent service_intent = new Intent(context, RingtonePlayingService.class);

        // RingtonePlayinService intent에 알람 상태값 저장
        service_intent.putExtra("state", strAlarmState);

        // 알람음 서비스 시작
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(service_intent);
        }
        else {
            context.startService(service_intent);
        }

    }
}