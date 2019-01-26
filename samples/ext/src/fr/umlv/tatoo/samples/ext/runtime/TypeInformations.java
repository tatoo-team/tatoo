package fr.umlv.tatoo.samples.ext.runtime;

public interface TypeInformations<T> {
  public Class<?> getTerminalType(T terminal);
}
