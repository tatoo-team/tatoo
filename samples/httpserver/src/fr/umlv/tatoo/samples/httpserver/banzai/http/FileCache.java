package fr.umlv.tatoo.samples.httpserver.banzai.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class FileCache {
  private final ConcurrentHashMap<String,FileCacheEntry> cache;
  
  private static final int MAX_ENTRY=16;
  
  public FileCache(int concurrencyLevel) {
    cache=new ConcurrentHashMap<String,FileCacheEntry>(16,0.5f,concurrencyLevel);
  }
  
  public FileCacheEntry get(String filename, Encoder encoder) {
    FileCacheEntry entry=cache.get(filename);
    if (entry!=null) {
      return entry;
    }
    return createEntry(filename,encoder);
  }
  
  private FileCacheEntry createEntry(String filename, Encoder encoder) {
    FileChannel input;
    try {
      input=new FileInputStream(new File(".",filename)).getChannel();
    } catch(FileNotFoundException e) {
      return null;
    }
    MappedByteBuffer content;
    try {
      content=input.map(FileChannel.MapMode.READ_ONLY,0,input.size());
    } catch(IOException e) {
      return null;
    }
    
    // eject one entry randomly
    if (cache.size()>=MAX_ENTRY) {
      Iterator<String> it=cache.keySet().iterator();
      it.next();
      it.remove();
    }
    
    FileCacheEntry entry=FileCacheEntry.create(content,encoder);
    cache.put(filename,entry);
    return entry;
  }
  
  public static final FileCache CACHE=new FileCache(256);
}
