package fr.umlv.tatoo.cc.lexer.main;

import java.util.Arrays;
import java.util.List;

import fr.umlv.tatoo.cc.common.main.AbstractSimpleCommand;
import fr.umlv.tatoo.cc.common.main.Command;
import fr.umlv.tatoo.cc.common.main.GeneratorBean;
import fr.umlv.tatoo.cc.common.main.OptionRegistry;

public class LexerOption  {
  
  private LexerOption() {
    // can't be instantiated
  }
  
  public interface LexerParam {
    public void setLexerType(LexerType lexerType);
    public void setGenerateLexerSwitch(boolean generateLexerSwitch);
  }
  
  public static final AbstractSimpleCommand<LexerParam> charset=
    new AbstractSimpleCommand<LexerParam>("charset") {
      public void execute(String option,LexerParam bean, List<? extends String> args) {
        String charset = args.get(0);
        bean.setLexerType(LexerType.valueOf(charset));
      }
      public String usage(String optionName, int numberOfArgument) {
        return "choose charset in "+Arrays.toString(LexerType.values());
      }
      public String usageArgumentName(String option, int numberOfArgument, int index) {
        return "charset name";
      }
    };

  public static final Command<LexerParam> generateSwitch=
    new Command<LexerParam>() {
      public void execute(String option,LexerParam bean, List<? extends String> args) {
        bean.setGenerateLexerSwitch(true);
      }
      public String usage(String optionName, int numberOfArgument) {
        return "ask to generate lexer using switch";
      }
      public String usageArgumentName(String option, int numberOfArgument, int index) {
        throw new AssertionError();
      }
      public void register(OptionRegistry<? extends LexerParam> registry) {
        registry.registerOption("switch",0);
        registry.registerOption("-","switch",0);
      }
    };  
    
    public static final AbstractSimpleCommand<GeneratorBean> name=
      new AbstractSimpleCommand<GeneratorBean>("name") {
      public void execute(String option,GeneratorBean bean, List<? extends String> args) {
        bean.setTypeName(LexerAliasPrototype.rule,args.get(0));
      }
      public String usage(String optionName, int numberOfArguments) {
        return "generated rule enum class name";
      }
      public String usageArgumentName(String option, int numberOfArgument, int index) {
        return "name";
      }
    };
}
