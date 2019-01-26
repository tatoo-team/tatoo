package fr.umlv.tatoo.samples.httpserver.banzai;

public abstract class WorkerTask<E> extends Emitter<E> implements Runnable {
  protected WorkerTask() {
    // do nothing 
  }
  
  @Override
  public Emitter<E> getNextSecondaryEmitter() {
    return this;
  }
}
