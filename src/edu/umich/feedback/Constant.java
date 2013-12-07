package edu.umich.feedback;

import android.os.Environment;

public class Constant {
  // minimum time in between two feedback
  public static long MIN_LOG_INTERVAL_MS = 1000;
  public static final String logTagMSG = "UserFeedback";
  public static final boolean toastEnabled = false;
  // output file path
  public static final String outputPath = Environment.getExternalStorageDirectory().getPath() + "/feedback";
}
