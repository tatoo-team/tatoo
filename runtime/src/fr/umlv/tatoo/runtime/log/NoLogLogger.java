package fr.umlv.tatoo.runtime.log;

public class NoLogLogger implements Logger {

  @Override
  public void fine(String message) {
    //no log
  }

  @Override
  public void finest(String message) {
    //no log
  }

  @Override
  public void info(String message) {
    //no log
  }

  @Override
  public void severe(String message) {
    //no log
  }

  @Override
  public void warning(String message) {
    //no log
  }

}
