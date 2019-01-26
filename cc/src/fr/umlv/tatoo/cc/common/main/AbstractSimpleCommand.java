/*
 * Created on 21 juil. 2005
 */
package fr.umlv.tatoo.cc.common.main;

public abstract class AbstractSimpleCommand<B>  implements Command<B> {
  protected AbstractSimpleCommand(String optionName) {
    this.optionName=optionName;
  }
  public void register(OptionRegistry<? extends B> registry) {
    registry.registerOption(optionName,1);
    registry.registerOption("-",Character.toString(optionName.charAt(0)),1);
  }
  private final String optionName;  
}
