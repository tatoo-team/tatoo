import java.io.File;
import java.io.FileFilter;

@SuppressWarnings(value={"foo","bar"})
class Test {
  public static void main(String[] args) {
    assert args==null: "hello assert";
    
    final String tatoo="Tatoo";
    new FileFilter() {
      @SuppressWarnings(value={"empty"})
      @Override
      public boolean accept(File pathname) {
        return false;
      }
    }.accept(new File("."));
    
    for(final String s:args) {
      System.out.println(s);
    }
  } 
}

interface A {
  int t;
}
