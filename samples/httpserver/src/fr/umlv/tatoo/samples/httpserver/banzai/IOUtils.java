package fr.umlv.tatoo.samples.httpserver.banzai;

import java.io.IOException;
import java.nio.channels.Channel;

public class IOUtils {
  private IOUtils() {
    // static factory
  }
  public static void closeWitoutException(Channel channel) {
    try {
      channel.close();
    } catch(IOException e) {
      // do nothing
    }
  }
}
