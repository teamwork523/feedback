package edu.umich.feedback;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class FeedbackButtonIntentReceiver extends BroadcastReceiver {
  
  @Override
  public void onReceive(Context context, Intent intent) {
    // Toast.makeText(context, "FeedbackButtonIntentReceiver: receive Key press Event!!!", Toast.LENGTH_SHORT).show();
    String intentAction = intent.getAction();
    // int volume = (Integer)intent.getExtras().get("android.media.EXTRA_VOLUME_STREAM_VALUE");
    // Toast.makeText(context, "FeedbackButtonIntentReceiver: current volume value is " + volume + " !!!", Toast.LENGTH_SHORT).show();
    if ("android.media.VOLUME_CHANGED_ACTION".equals(intentAction)) {
      long newTime = System.currentTimeMillis();
      Toast.makeText(context, "FeedbackButtonIntentReceiver: receive volume change action after previous message " + (newTime - Util.privVolChangeTime) + " ms !!!", Toast.LENGTH_SHORT).show();
      Util.privVolChangeTime = newTime;
    }
  }

}
