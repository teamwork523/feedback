package edu.umich.feedback;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class FeedbackIntentReceiver extends BroadcastReceiver {
  
  @Override
  public void onReceive(Context context, Intent intent) {
    //Toast.makeText(context, "FeedbackButtonIntentReceiver: receive notificaiton!!!", Toast.LENGTH_SHORT).show();
    String intentAction = intent.getAction();
    if (intentAction.equals(Constant.feedbackNotificaitonAction) &&
        Util.feedbackEnabled == true) {
      long newTime = System.currentTimeMillis();
      Toast.makeText(context, "Feedback received!!!", Toast.LENGTH_SHORT).show();
      String content = Util.convertTSinMStoTSinS(newTime);
      Util.writeResultToFile(Util.getFilename("user_feedback"), content);
    }
  }
}
