package fr.umlv.tatoo.cc.common.main;

/** Represents a registry of options
 *  for a specific command.
 *  
 * @author Remi
 *
 * @param <D> type of user data.
 */
public interface OptionRegistry<D> {
  /** register an option with a name a command
   *  to execute and a number of option argument.
   *  The prefix of the option is the default prefix of the command line parser
   *  see {@link CommandLineParser#CommandLineParser(String, UsageFormatter, Command[])
   *  CommandLineParser(defaultPrefix,usageFormatter,commands)}.
   *  
   *  @param optionName the name of the option.
   *  @param numberOfArgument the number of argument of the option.
   */
  public void registerOption(String optionName, int numberOfArgument);

  /** register an option with a specific prefix, a name a command
   *  to execute and a number of option argument.
   *  
   *  @param prefix the specific prefix of the option.
   *  @param optionName the name of the option.
   *  @param numberOfArgument the number of argument of the option.
   *  
   *  @see #registerOption(String, int)
   */
  public void registerOption(String prefix, String optionName,
      int numberOfArgument);

}