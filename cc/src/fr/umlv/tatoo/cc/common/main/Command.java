package fr.umlv.tatoo.cc.common.main;

import java.util.List;

/** Represents a command.
 *  A command associate in one place :
 *   - a register method to register all options
 *     managed by the current command in the
 *     {@link fr.umlv.tatoo.cc.common.main.OptionRegistry OptionRegistry}.
 *   - a usage of the command
 *   - a method {@link #execute(String, Object, List) execute}.
 * 
 *  First, the method {@link #register(OptionRegistry) register()}
 *  is called by the {@link fr.umlv.tatoo.cc.common.main.CommandLineParser command line parser}
 *  during its construction
 *  ({@link fr.umlv.tatoo.cc.common.main.CommandLineParser#CommandLineParser(String, UsageFormatter, Command[])
 *   CommandLineParser(prefix,usageFormatter,commands)}=
 *  or during an addition of commands
 *  ({@link fr.umlv.tatoo.cc.common.main.CommandLineParser#addCommands(Command[]) addCommand(commands)}.
 *  
 *  This method may register some options to the
 *  {@link fr.umlv.tatoo.cc.common.main.OptionRegistry OptionRegistry}.
 *  
 *  When the method
 *  {@link fr.umlv.tatoo.cc.common.main.CommandLineParser#parse(Object, String[]) parse}
 *  of {@link fr.umlv.tatoo.cc.common.main.CommandLineParser CommandLineParser}
 *  found an option, it calls the method
 *  {@link #execute(String, Object, List) execute(optionName,userData,arguments)} of the
 *  corresponding command.
 * 
 * @author remi
 *
 * @param <D> type of the user data.
 */
public interface Command<D> {
  /** Returns the usage of an option represented by the command.
   * @param optionName name of the option.
   * @param numberOfArgument number of argument of the option.
   * @return the usage of the option.
   */
  public String usage(String optionName, int numberOfArgument);
  
  /** Return the name of each argument of an option represented by the command.
   * @param optionName name of the option.
   * @param numberOfArgument number of argument of the option.
   * @param index of the argument
   *        index if always between [0,numberOfArgument[
   * @return the name of the argument
   */
  public String usageArgumentName(String optionName, int numberOfArgument, int index);
  
  /** Register all options managed by the current command.
   * @param registry the option registry.
   * 
   * @see OptionRegistry
   */
  public void register(OptionRegistry<? extends D> registry);
  
  /** Execute the command corresponding to the option.
   * @param optionName name of the option.
   * @param userData user data.
   * @param optionArguments list of option arguments.
   * @throws IllegalCommandLineArgumentException if by example optionArguments are bad filled.
   */
  public void execute(String optionName,D userData, List<? extends String> optionArguments)
    throws IllegalCommandLineArgumentException;
}
