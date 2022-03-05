package edu.ucr.cs.css;

import com.google.errorprone.ErrorProneFlags;

public class Config {

  public final String outputDirectory;
  public final boolean methodTrackerIsActive;
  public final boolean fieldTrackerIsActive;
  public final boolean callTrackerIsActive;
  public final Serializer serializer;

  static final String EP_FL_NAMESPACE = "NullAway";
  static final String FL_FIELD = EP_FL_NAMESPACE + ":ActivateFieldTracker";
  static final String FL_METHOD = EP_FL_NAMESPACE + ":ActivateMethodTracker";
  static final String FL_CALL = EP_FL_NAMESPACE + ":ActivateCallTracker";
  static final String FL_OUTPUT_DIR = EP_FL_NAMESPACE + ":OutputDirectory";

  static final String DEFAULT_PATH = "/tmp/NullAwayFix";

  public Config() {
    methodTrackerIsActive = true;
    fieldTrackerIsActive = true;
    callTrackerIsActive = true;
    outputDirectory = DEFAULT_PATH;
    serializer = new Serializer(this);
  }

  public Config(ErrorProneFlags flags) {
    fieldTrackerIsActive = flags.getBoolean(FL_FIELD).orElse(true);
    methodTrackerIsActive = flags.getBoolean(FL_METHOD).orElse(true);
    callTrackerIsActive = flags.getBoolean(FL_CALL).orElse(true);
    outputDirectory = flags.get(FL_OUTPUT_DIR).orElse(DEFAULT_PATH);
    serializer = new Serializer(this);
  }
}
