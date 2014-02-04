package edu.umich.feedback;

import android.os.Environment;

public class Constant {
  // minimum time in between two feedback
  public static long MIN_LOG_INTERVAL_MS = 1000;
  public static final String logTagMSG = "UserFeedback";
  public static final String feedbackNotificaitonAction = "edu.umich.feedback.userFeedback";
  public static final boolean toastEnabled = true;
  // output file path
  public static final String userFeedbackPath = Environment.getExternalStorageDirectory().getPath() + "/feedback";
}
