package fr.umlv.tatoo.samples.httpserver.banzai.http;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class HttpHeaders {
  private HttpHeaders() {
    // do nothing
  }
 
  public static void putResponseOK(ByteBuffer buffer, boolean keepalive, long length,Encoder encoder) {
    encoder.encode(length,buffer.put(RESPONSE_OK_FIRST_PART)).put(
      (keepalive)?RESPONSE_OK_KEEPALIVE_LAST_PART:RESPONSE_OK_CLOSED_LAST_PART);
  }
  
  public static void putResponseNotFound(ByteBuffer buffer,boolean keepalive) {
    buffer.put((keepalive)?RESPONSE_NOT_FOUND_KEEPALIVE:RESPONSE_NOT_FOUND_CLOSED);
  }
  
  public static void putBadRequest(ByteBuffer buffer) {
    buffer.put(RESPONSE_BAD_REQUEST);
  }
  
  private static byte[] createArray(String text,Charset charset) {
    return text.getBytes(charset);
  }
  
  private static final byte[] RESPONSE_OK_FIRST_PART;
  private static final byte[] RESPONSE_OK_KEEPALIVE_LAST_PART;
  private static final byte[] RESPONSE_OK_CLOSED_LAST_PART;
  private static final byte[] RESPONSE_NOT_FOUND_CLOSED;
  private static final byte[] RESPONSE_NOT_FOUND_KEEPALIVE;
  private static final byte[] RESPONSE_BAD_REQUEST;
  
  
  static {
    String EOLN="\r\n";
    String HTTP_VERSION="HTTP/1.1";
    String OK="200 OK";
    String NOT_FOUND="404 Not Found";
    String BAD_REQUEST="400 Bad Request";
    
    String CONTENT_LENGTH="Content-Length: ";
    String KEEP_ALIVE="Connection: Keep-Alive";
    String CLOSED="Connection: close";
    
    Charset charset=Charset.forName("ISO-8859-1");
    RESPONSE_OK_FIRST_PART=createArray(
      HTTP_VERSION+" "+OK+EOLN+
      CONTENT_LENGTH,charset);
    RESPONSE_OK_KEEPALIVE_LAST_PART=createArray(
        EOLN+KEEP_ALIVE+EOLN+EOLN,charset);
    RESPONSE_OK_CLOSED_LAST_PART=createArray(
        EOLN+CLOSED+EOLN+EOLN,charset);
    RESPONSE_NOT_FOUND_KEEPALIVE=createArray(
        HTTP_VERSION+" "+NOT_FOUND+EOLN+
        CONTENT_LENGTH+"0"+EOLN+
        KEEP_ALIVE+EOLN+EOLN,charset);
    RESPONSE_NOT_FOUND_CLOSED=createArray(
        HTTP_VERSION+" "+NOT_FOUND+EOLN+
        CONTENT_LENGTH+"0"+EOLN+
        CLOSED+EOLN+EOLN,charset);
    RESPONSE_BAD_REQUEST=createArray(
      HTTP_VERSION+" "+BAD_REQUEST+EOLN+EOLN,charset);
  }
}
