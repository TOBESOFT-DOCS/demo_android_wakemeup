package com.demo.wakemeup;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.nexacro.NexacroActivity;
import com.nexacro.event.NexacroEventHandler;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

//import android.content.pm.PackageManager;


public class UserNotify implements NexacroEventHandler {

    AlarmManager alarmManager;
    Context context;
    PendingIntent pendingIntent;

    //알람 리시버용 인텐트
    Intent alarmIntent;

    NexacroActivity nActivity = com.nexacro.NexacroActivity.getInstance();


    public UserNotify(Context mContext) {
        Log.d("UserNotify", "<init>");

        this.context = mContext;

        //this.package_manager = this.context.getPackageManager();

        this.alarmIntent = new Intent(this.context, AlarmReceiver.class);

        if(NexacroActivity.getInstance() != null)
            NexacroActivity.getInstance().setNexacroEventListener(this);

    }

    @Override
    public void onUserNotify(int nNotifyID, String strMessage) {

        String strMsg = "";

        // 알람매니저 설정
        alarmManager = (AlarmManager) this.context.getSystemService(ALARM_SERVICE);

        Log.d("UserNotify", "[onUserNotify] nNotifyID : "+ nNotifyID +", strMessage : "+ strMessage);

        // TODO :

        switch(nNotifyID)
        {
            case 101:   //Alarm Setting
                Log.i("UserNotify", "case 101 Alarm Setting: "+ strMessage);

                //strMessage="2019,5,29,18,17"
                final int intYear, intMonth, intDay, intHour, intMin;
                String[] strRecvTime = strMessage.split(",", 5);

                intYear = Integer.parseInt(strRecvTime[0]);
                intMonth = Integer.parseInt(strRecvTime[1]);
                intDay = Integer.parseInt(strRecvTime[2]);
                intHour = Integer.parseInt(strRecvTime[3]);
                intMin = Integer.parseInt(strRecvTime[4]);


                // Calendar 객체 생성
                final Calendar calendar = Calendar.getInstance();

                // calendar에 시간 셋팅
                calendar.set(Calendar.YEAR, intYear);
                calendar.set(Calendar.MONTH, intMonth-1);
                calendar.set(Calendar.DATE, intDay);
                calendar.set(Calendar.HOUR_OF_DAY, intHour);
                calendar.set(Calendar.MINUTE, intMin);
                calendar.set(Calendar.SECOND, 0);

                // reveiver에 string 값 넘겨주기
                alarmIntent.putExtra("state","ALARMON");


                // BroadcastReceiver를 시작하는 인텐트를 생성
                pendingIntent = PendingIntent.getBroadcast(this.context,
                        0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                Log.i("UserNotify", "pendingIntent: "+ pendingIntent);
                Log.i("UserNotify", "alarmIntent: "+ alarmIntent);
                Log.i("UserNotify", "Alarm_Receiver.class: "+ AlarmReceiver.class);
                Log.i("UserNotify", "context: "+ context +
                        ", this: "+ this +
                        ", this.context: "+ this.context +
                        ", this.context.getApplicationContext(): "+ this.context.getApplicationContext());


                // 알람 반복 셋팅
                long aTime = System.currentTimeMillis();
                long bTime = calendar.getTimeInMillis();

                long oneday = 1000 * 60 * 60  * 24;

                //만일 내가 설정한 시간과 시스템 시간을 비교하여 현재 시간보다 작다면 알람을 다음날로 설정
                while(aTime>bTime){
                    bTime += oneday;
                }

                //반복 알람 설정
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, bTime, oneday, pendingIntent);
                //alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);



                Toast.makeText(this.context,"알람 예정 시간은 " +
                        calendar.get(Calendar.YEAR) + "년 " +
                        (calendar.get(Calendar.MONTH)+1) + "월 " +
                        calendar.get(Calendar.DATE) + "일 " +
                        calendar.get(Calendar.HOUR_OF_DAY) + "시 " +
                        calendar.get(Calendar.MINUTE) + "분", Toast.LENGTH_SHORT).show();

                Log.i("UserNotify", "calendar.get: "+
                        calendar.get(Calendar.YEAR) + "년 " +
                        (calendar.get(Calendar.MONTH)+1) + "월 " +
                        calendar.get(Calendar.DATE) + "일 " +
                        calendar.get(Calendar.HOUR_OF_DAY) + "시 " +
                        calendar.get(Calendar.MINUTE) + "분");


                if(nActivity != null) {
                    nActivity.callScript("fn_callScript('ALARMON')");

                    Log.i("Alarm_Receiver", "fn_callScript('ALARMON') called.");
                }

                break;

            case 102:   //Alarm OFF
                Log.i("UserNotify", "case 102 Alarm OFF: "+ strMessage);

                Toast.makeText(this.context,"알람 종료", Toast.LENGTH_SHORT).show();

                //알람매니저 취소
                alarmManager.cancel(pendingIntent);

                alarmIntent.putExtra("state","ALARMOFF");

                //서비스로 ALARMOFF 전달하여 알람음 재생 stop
                this.context.sendBroadcast(alarmIntent);

                if(nActivity != null) {
                    nActivity.callScript("fn_callScript('ALARMOFF')");

                    Log.i("Alarm_Receiver", "fn_callScript('ALARMOFF') called.");
                }

                break;

            case 103:   //Alarm CANCEL
                Log.i("UserNotify", "case 103 Alarm CANCEL: "+ strMessage);

                Intent cancelIntent = new Intent(this.context, AlarmReceiver.class);
                PendingIntent cancelSender = PendingIntent.getBroadcast(this.context, 0, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                Log.i("UserNotify", "cancelIntent: "+ cancelIntent);
                Log.i("UserNotify", "sender: "+ cancelSender);

                if(cancelSender != null) {
                    alarmManager.cancel(cancelSender);
                    cancelSender.cancel();

                    strMsg = "설정된 알람을 취소했습니다.";
                }
                else {
                    strMsg = "취소할 알람이 없습니다.";
                }

                if(nActivity != null) {
                    nActivity.callScript("fn_callScript('"+ strMsg +"')");

                    Log.i("Alarm_Receiver", "fn_callScript('\"+ strMsg +\"') called.");
                }

                break;

            case 104:   //Alarm CHECK
                Log.i("UserNotify", "case 104 Alarm CHECK: "+ strMessage);

                Intent checkIntent = new Intent(this.context, AlarmReceiver.class);
                PendingIntent checkSender = PendingIntent.getBroadcast(this.context, 0, checkIntent, PendingIntent.FLAG_NO_CREATE);

                if(checkSender == null) {
                    strMsg = "설정된 알람이 없습니다.";
                }
                else {
                    strMsg = "설정된 알람이 있습니다.";
                }

                if(nActivity != null) {
                    nActivity.callScript("fn_callScript('"+ strMsg +"')");

                    Log.i("Alarm_Receiver", "fn_callScript('\"+ strMsg +\"') called.");
                }

                break;

            case 200:   //Weather
                break;

            case 300:   //Schedule
                break;

            case 901:   //ACTIVITY hidden 처리
                Log.i("UserNotify", "case 901: "+ strMessage);
                //this.package_manager.setComponentEnabledSetting(new ComponentName(this.context.getApplicationContext(), NexacroActivity.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                this.context.startActivity(intent);

                break;

            case 902:   //ACTIVITY show 처리
                Log.i("UserNotify", "case 902: "+ strMessage);
                //this.package_manager.setComponentEnabledSetting(new ComponentName(this.context.getApplicationContext(), NexacroActivity.class), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                /*
                Intent nIntent = new Intent(this, NexacroActivity.class);
                nIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                this.context.startActivity(nIntent);
                */
                break;

            default:
                Log.i("UserNotify", "Unknown nNotifyID: "+ nNotifyID);

        }//end of switch(nNotifyID)


    }
}
