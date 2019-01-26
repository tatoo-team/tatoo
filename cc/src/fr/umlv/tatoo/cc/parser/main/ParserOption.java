package fr.umlv.tatoo.cc.parser.main;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import fr.umlv.tatoo.cc.common.main.AbstractSimpleCommand;

public class ParserOption {
  private ParserOption() {
    // can't be instantiated
  }
  
  public static final AbstractSimpleCommand<ParserParam> logFile=
    new AbstractSimpleCommand<ParserParam>("logFile") {
      public void execute(String option,ParserParam bean, List<? extends String> args) {
          bean.setLogFile(new File(args.get(0)));
      }
      public String usage(String optionName, int numberOfArgument) {
        return "name of the file to write table (for debugging grammar)";
      }
      public String usageArgumentName(String option, int numberOfArgument, int index) {
        return "file";
      }
    };
    
    /** 
     * Defines the type of the parser SLR(0), LR(1), LALR(1).
     */
    public static final AbstractSimpleCommand<ParserParam> parserType=
      new AbstractSimpleCommand<ParserParam>("type") {
      public void execute(String option,ParserParam bean, List<? extends String> args) {
        bean.setParserType(ParserType.valueOf(args.get(0)));
      }
      public String usage(String optionName, int numberOfArgument) {
        return "choose the parser type "+Arrays.toString(ParserType.values());
      }
      public String usageArgumentName(String option, int numberOfArgument, int index) {
        return "type";
      }
    };
    
    /** 
     * Defines the conflict resolver.
     */
    public static final AbstractSimpleCommand<ParserParam> conflictResolver=
      new AbstractSimpleCommand<ParserParam>("resolver") {
      public void execute(String option,ParserParam bean, List<? extends String> args) {
        bean.setConflictResolverType(ConflictResolverType.valueOf(args.get(0).toUpperCase()));
      }
      public String usage(String optionName, int numberOfArgument) {
        return "choose a conflict resolver "+Arrays.toString(ParserType.values());
      }
      public String usageArgumentName(String option, int numberOfArgument, int index) {
        return "conflict resolver type";
      }
    };
}
