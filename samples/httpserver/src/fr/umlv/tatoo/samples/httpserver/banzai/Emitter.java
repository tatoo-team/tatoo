package fr.umlv.tatoo.samples.httpserver.banzai;

public abstract class Emitter<E> {
  public abstract void send(E element);
  
  public abstract Emitter<E> getNextSecondaryEmitter();
}
