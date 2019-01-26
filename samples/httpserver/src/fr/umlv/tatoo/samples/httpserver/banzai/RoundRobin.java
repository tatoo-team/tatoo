package fr.umlv.tatoo.samples.httpserver.banzai;

import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobin<E> extends Emitter<E> {
  private final WorkerTask<E>[] tasks;
  private final AtomicInteger counter=new AtomicInteger();
  
  public RoundRobin(WorkerTask<E>[] tasks) {
    this.tasks=tasks;
  }
  
  @Override
  public void send(E element) {
    getNextSecondaryEmitter().send(element);
  }
  
  @Override
  public Emitter<E> getNextSecondaryEmitter() {
    return tasks[counter.getAndIncrement()%tasks.length];
  }
}
