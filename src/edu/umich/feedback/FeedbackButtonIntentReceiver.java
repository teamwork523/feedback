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
    // Toast.makeText(context, "FeedbackButtonIntentReceiver: current volume value is " + volume + " !!!", Toast.LENGTH_SHORT).show();
    if ("android.media.VOLUME_CHANGED_ACTION".equals(intentAction)) {
      long newTime = System.currentTimeMillis();
      int volume = (Integer)intent.getExtras().get("android.media.EXTRA_VOLUME_STREAM_VALUE");
      // Make sure the volume actually changes or stay in min/max volume
      if (Util.feedbackEnabled && newTime - Util.privVolChangeTime >= Constant.MIN_LOG_INTERVAL_MS &&
          (volume != Util.privVolume || volume == 0 || volume == Util.MAX_VOLUME)) {
        Toast.makeText(context, "Feedback received!!!", Toast.LENGTH_SHORT).show();
        String content = Util.convertTSinMStoTSinS(newTime);
        Util.writeResultToFile(Util.getFilename(), content);
        Util.privVolChangeTime = newTime;
        Util.privVolume = volume;
      }
    }
  }

}
