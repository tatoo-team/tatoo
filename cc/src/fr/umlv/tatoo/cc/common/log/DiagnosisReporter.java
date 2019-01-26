package fr.umlv.tatoo.cc.common.log;

import java.util.ResourceBundle;

public abstract class DiagnosisReporter {
  private boolean onError;
  private final UserDefinedLevelMap userDefinedLevelMap;
  
  public enum Level {
    ERROR, WARNING, INFO, DEBUG, NONE
  }
  
  public interface Key {
    public String name();
    public Level defaultLevel();
  }
  
  protected DiagnosisReporter(UserDefinedLevelMap userDefinedLevelMap) {
    this.userDefinedLevelMap=userDefinedLevelMap;
  }
  
  public boolean isOnError() {
    return onError;
  }
  
  protected final void setErrorIfNedded(Key key) {
    if (getLevel(key)==Level.ERROR)
      onError=true;
  }
  
  protected abstract ResourceBundle getBundle();
  
  protected final String formatMessage(Key key,Object... data) {
    String localizedMessage=getBundle().getString(key.name());
    if (localizedMessage==null)
      return "no localized message for key "+key;
    try {
      return String.format(localizedMessage,data);
    } catch(RuntimeException e) {
      e.printStackTrace();
      return "error while trying to localize "+key+" ("+e.getMessage()+')';
    }
  }
  
  protected final Level getLevel(Key key) {
    if (userDefinedLevelMap==null)
      return key.defaultLevel();
    return userDefinedLevelMap.getLevel(key);
  }
}
