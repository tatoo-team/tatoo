package fr.umlv.tatoo.runtime.log;

public class SyserrHandler implements Handler {

  @Override
  public void print(Object string) {
    System.err.println(string);
  }

}
