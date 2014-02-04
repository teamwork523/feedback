package edu.umich.feedback;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class FeedbackService extends Service {
  // This is the object that receives interactions from clients.  See
  // RemoteService for a more complete example.
  private final IBinder mBinder = new FeedbackBinder();
  private AudioManager mAudioManager = null;
  private ComponentName mReceiver = null;
  private NotificationManager notificationManager;
  
  // This arbitrary id is private to Feedback
  private static final int NOTIFICATION_ID = 53947;
  
  // create a process to capture all the logcat files
  private Process logcatProc = null;
  
  public class FeedbackBinder extends Binder {
    FeedbackService getService() {
      return FeedbackService.this;
    }
  }
  
  @Override
  public void onCreate() {
    super.onCreate();
    if (Constant.toastEnabled) {
      Toast.makeText(this, "FeedbackService: onCreate() succefully!!!", Toast.LENGTH_SHORT).show();
    }
    
    this.notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    addIconToStatusBar();
  }
  
  private void addIconToStatusBar() {
    notificationManager.notify(NOTIFICATION_ID,
        createServiceRunningNotification());
  }

  private Notification createServiceRunningNotification() {
    // Prepare intent which is triggered if the
    // notification is selected
    // Intent intent = new Intent(this, FeedbackActivity.class);
    Intent intent = new Intent(this, FeedbackIntentReceiver.class);
    intent.setAction(Constant.feedbackNotificaitonAction);
    // keep only one single instance
    // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, intent, 
                  PendingIntent.FLAG_CANCEL_CURRENT);
    
    // Build notification
    // Actions are just fake
    Notification notice = new Notification(R.drawable.ic_launcher,
        getString(R.string.notificationSchedulerStarted),
        System.currentTimeMillis());
    notice.flags |= Notification.FLAG_NO_CLEAR
        | Notification.FLAG_ONGOING_EVENT;
    // This is deprecated in 3.x. But most phones still run 2.x systems
    notice.setLatestEventInfo(this, getString(R.string.app_name),
        getString(R.string.notificationServiceRunning), pIntent);
    return notice;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId)  {
    if (Constant.toastEnabled) {
      Toast.makeText(this, "FeedbackService: onStartCommand succefully!!!", Toast.LENGTH_SHORT).show();
    }
    return START_STICKY;
  }
  
  @Override
  public IBinder onBind(Intent intent) {
    Log.i(Constant.logTagMSG, "FeedbackService: successful bind");
    if (Constant.toastEnabled) {
      Toast.makeText(this, "FeedbackService: Bind the service succefully!!!", Toast.LENGTH_SHORT).show();
    }
    if (mAudioManager != null) {
      Util.privVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
      if (Constant.toastEnabled) {
        Toast.makeText(this, "FeedbackService: Max volume is " + Util.MAX_VOLUME + "; current volume is " +
                       Util.privVolume, Toast.LENGTH_SHORT).show();
      }
    }
    
    return mBinder;
  }

  @Override
  public void onDestroy() {
    if (Constant.toastEnabled) {
      Toast.makeText(this, "FeedbackService: Destroy the service succefully!!!", Toast.LENGTH_SHORT).show();
    }
    if (logcatProc != null) {
      logcatProc.destroy();
    }
    // Stop listening for button presses
    // mAudioManager.unregisterMediaButtonEventReceiver(mReceiver);
  }
}
