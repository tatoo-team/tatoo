package fr.umlv.tatoo.cc.tools.main;

import java.util.List;

import fr.umlv.tatoo.cc.common.main.Command;
import fr.umlv.tatoo.cc.common.main.OptionRegistry;

public class ToolsOption {
  private ToolsOption() {
    // can't be instantiated
  }
  
  public static final Command<ToolsParam> generateAST=
    new Command<ToolsParam>() {
      public void execute(String option,ToolsParam bean, List<? extends String> args) {
          bean.setGenerateAST(true);
      }
      public String usage(String optionName, int numberOfArgument) {
        return "ask to generate the ast";
      }
      public String usageArgumentName(String option, int numberOfArgument, int index) {
        throw new AssertionError();
      }
      public void register(OptionRegistry<? extends ToolsParam> registry) {
        registry.registerOption("ast",0);
        registry.registerOption("-","ast",0);
      }
    };
}
