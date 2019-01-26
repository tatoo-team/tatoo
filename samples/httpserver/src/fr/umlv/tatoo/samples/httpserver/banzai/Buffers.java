package fr.umlv.tatoo.samples.httpserver.banzai;

import java.nio.ByteBuffer;

import fr.umlv.tatoo.samples.httpserver.banzai.util.ConcurrentPool;

public class Buffers  {
  public static final int BUFFER_SIZE=1024*32;
  public static final int SMALL_BUFFER_SIZE=1024;
  
  private Buffers() {
    // static helper
  }
  
  public static final class BufferPool extends ConcurrentPool<ByteBuffer> {
    private final int bufferSize;
    public BufferPool(int bufferSize) {
      this.bufferSize=bufferSize;
    }
    
    @Override
    protected ByteBuffer create() {
      return ByteBuffer.allocateDirect(bufferSize);
    }

    @Override
    protected void reset(ByteBuffer buffer) {
      buffer.clear();
    }
  }
  
  public static final BufferPool POOL_SMALL=new BufferPool(SMALL_BUFFER_SIZE);
  public static final BufferPool POOL=new BufferPool(BUFFER_SIZE);
}
