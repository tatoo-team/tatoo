package fr.umlv.tatoo.samples.ll;

import static fr.umlv.tatoo.samples.ll.lexer.RuleEnum.minus;
import static fr.umlv.tatoo.samples.ll.lexer.RuleEnum.plus;
import static fr.umlv.tatoo.samples.ll.lexer.RuleEnum.semicolon;
import static fr.umlv.tatoo.samples.ll.lexer.RuleEnum.value;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.NoSuchElementException;

import fr.umlv.tatoo.runtime.buffer.impl.LocationTracker;
import fr.umlv.tatoo.runtime.buffer.impl.ReaderWrapper;
import fr.umlv.tatoo.runtime.lexer.Tokenizer;
import fr.umlv.tatoo.samples.ll.lexer.LexerDataTable;
import fr.umlv.tatoo.samples.ll.lexer.RuleEnum;

public class LL {

  private final Tokenizer<RuleEnum, ReaderWrapper> tokenizer;
  private final ReaderWrapper buffer;
  
  private RuleEnum lookahead;
  
  private void discardAndFind(RuleEnum... rules) throws IOException {
    while(!tokenizer.hasNext(rules)) {
      if (buffer.hasRemaining()) {
        buffer.unwind(1);
        int c = buffer.lastChar();
        System.err.printf("discarding %c (%d)%n",(char)c,c);
      }
      else
        throw new NoSuchElementException(Arrays.toString(rules));
    }
    lookahead = tokenizer.getNext();
  }
  
  public LL() {
    buffer = new ReaderWrapper(new InputStreamReader(System.in),new LocationTracker());
    tokenizer = Tokenizer.createTokenizer(LexerDataTable.createTable(), buffer, RuleEnum.space);
  }
  
  // S -> E $
  public void readStart() throws IOException {
    do {
    discardAndFind(value,minus);
    System.out.println(readExpr());
    if(tokenizer.hasNext(semicolon))
      tokenizer.getNext();
    }
    while(! tokenizer.eof());
  }
  
  
  // E -> T E2
  public int readExpr() throws IOException {
    int t = readT();
    return readE2(t);
  }

  private int readE2(int t) throws IOException {
    if (!tokenizer.hasNext(plus,minus))
      return t;
    lookahead = tokenizer.getNext();
    switch(lookahead) {
    case plus: discardAndFind(value,minus); int t2 = readT(); return readE2(t+t2);
    case minus: discardAndFind(value,minus);int t22 = readT(); return readE2(t-t22);
    default:
    }
    throw new AssertionError("Unreachable code");
  }

  private int readT() throws IOException {
    switch(lookahead) {
    case value: return parseInt(buffer.view());
    case minus: discardAndFind(value); return -parseInt(buffer.view());
    default:
    }
    throw new AssertionError("Unreachable code");
  }

  private static int parseInt(CharSequence sequence) {
    int val = 0;
    for(int i=0;i<sequence.length();i++)
      val = 10*val + (sequence.charAt(i)-'0');
    return val;
  }
  
  public static void main(String[] args) throws IOException {
    new LL().readStart();
  }
  
}
