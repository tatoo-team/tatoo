package fr.umlv.tatoo.samples.scanner;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.EnumSet;

import fr.umlv.tatoo.runtime.buffer.impl.LocationTracker;
import fr.umlv.tatoo.runtime.buffer.impl.ReaderWrapper;
import fr.umlv.tatoo.runtime.lexer.LexerTable;
import fr.umlv.tatoo.runtime.lexer.Tokenizer;
import fr.umlv.tatoo.samples.scanner.lexer.LexerDataTable;
import fr.umlv.tatoo.samples.scanner.lexer.RuleEnum;

/**
 * @author Remi
 */
public class Main {
  
  public static void main(String[] args) throws IOException {
    //Reader reader = new StringReader("++++aaabb");
    /*if (args.length>0)
      reader=new FileReader(args[0]);
    else*/
    Reader reader=new InputStreamReader(System.in);

    ReaderWrapper buffer = new ReaderWrapper(reader,new LocationTracker());

    LexerTable<RuleEnum> lexerDataTable = LexerDataTable.createTable();

    Tokenizer<RuleEnum,ReaderWrapper> scanner=
      Tokenizer.createTokenizer(lexerDataTable,buffer,RuleEnum.space,RuleEnum.eoln);

    //for(;;)
    //  System.err.println(scanner.next(EnumSet.of(RuleEnum.plus)));
    RuleEnum[] values = RuleEnum.values();
    RuleEnum[][] sets = new RuleEnum[values.length][1];
    for(int i=0;i<values.length;i++)
      sets[i][0] = values[i];
    EnumSet<RuleEnum> next = EnumSet.noneOf(RuleEnum.class);
    RuleEnum[] all = RuleEnum.values();
    for(;;) {
      next.clear();
      for(int i=0;i<values.length;i++)
        if (scanner.hasNext(sets[i]))
          next.add(values[i]);
      if (next.isEmpty())
        break;
      System.out.println("available: "+next);
      System.out.println("winner :"+scanner.next(all));
    }
  }

}
