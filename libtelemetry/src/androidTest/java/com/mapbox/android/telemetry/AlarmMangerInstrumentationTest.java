package com.mapbox.android.telemetry;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.util.Log;

import org.junit.Assert;
import org.junit.Test;

public class AlarmMangerInstrumentationTest {
  private int broadcastTrack;

  @Test
  public void checksAlarmCancelledProperly() {
    Log.e("test", "trigger1");
    broadcastTrack = 0;
    Context context = InstrumentationRegistry.getContext();
    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    AlarmReceiver alarmReceiver = obtainAlarmReceiver();

    AlarmSchedulerFlusher theAlarmSchedulerFlusher = new AlarmSchedulerFlusher(context, alarmManager,
      alarmReceiver);

    long elapsedMockedTime = 2000;
    long elapsedMockedTime2 = 5000;

    theAlarmSchedulerFlusher.register();
    theAlarmSchedulerFlusher.scheduleExact(elapsedMockedTime);

    theAlarmSchedulerFlusher.register();
    theAlarmSchedulerFlusher.scheduleExact(elapsedMockedTime2);

    try {
      Thread.sleep(30000);
      Assert.assertEquals(1, broadcastTrack);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private AlarmReceiver obtainAlarmReceiver() {
    return new AlarmReceiver(new SchedulerCallback() {
      @Override
      public void onPeriodRaised() {

      }

      @Override
      public void onError() {

      }
    }){
      @Override
      public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.e("test", "trigger2");
        broadcastTrack++;
      }
    };
  }
}