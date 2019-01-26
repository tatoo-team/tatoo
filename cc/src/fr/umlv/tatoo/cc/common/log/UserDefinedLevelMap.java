package fr.umlv.tatoo.cc.common.log;

import java.util.HashMap;

public class UserDefinedLevelMap {
  private final HashMap<DiagnosisReporter.Key,DiagnosisReporter.Level> levelMap=
    new HashMap<DiagnosisReporter.Key,DiagnosisReporter.Level>();
  
  public DiagnosisReporter.Level getLevel(DiagnosisReporter.Key key) {
    DiagnosisReporter.Level level=levelMap.get(key);
    if (level==null)
      return key.defaultLevel();
    return level;
  }
  
  public void changeKeyLevel(DiagnosisReporter.Key key,DiagnosisReporter.Level level) {
    if (key.defaultLevel()!=level)
      levelMap.put(key,level);
    else
      levelMap.remove(key);
  }
}
