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
      if (Util.feedbackEnabled && newTime - Util.privVolChangeTime >= Constant.MIN_LOG_INTERVAL_MS) {
        Toast.makeText(context, "Feedback received!!!", Toast.LENGTH_SHORT).show();
        String content = Util.convertTSinMStoTSinS(newTime);
        Util.writeResultToFile(Util.getFilename(), content);
        Util.privVolChangeTime = newTime;
      }
    }
  }

}
