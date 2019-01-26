package fr.umlv.tatoo.runtime.log;

public interface Logger {

  void warning(String message);

  void info(String message);

  void severe(String message);

  void fine(String message);

  void finest(String message);

}
