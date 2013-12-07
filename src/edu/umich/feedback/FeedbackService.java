package edu.umich.feedback;

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
    if (mAudioManager == null) {
      mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
      mReceiver = new ComponentName(getPackageName(),
                      FeedbackButtonIntentReceiver.class.getName());
      mAudioManager.registerMediaButtonEventReceiver(mReceiver);
    }
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
    if (Constant.toastEnabled) {
      Toast.makeText(this, "FeedbackService: Bind the service succefully!!!", Toast.LENGTH_SHORT).show();
    }
    return mBinder;
  }

  @Override
  public void onDestroy() {
    if (Constant.toastEnabled) {
      Toast.makeText(this, "FeedbackService: Destroy the service succefully!!!", Toast.LENGTH_SHORT).show();
    }
    // Stop listening for button presses
    // mAudioManager.unregisterMediaButtonEventReceiver(mReceiver);
  }
}
