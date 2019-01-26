package fr.umlv.tatoo.cc.common.main;

import java.util.Map;
import java.util.Set;

import fr.umlv.tatoo.cc.common.main.CommandLineParser.Option;

/** Format the usage of a {@link fr.umlv.tatoo.cc.common.main.CommandLineParser CommandLineParser}.
 * 
 * @author remi
 * 
 * @see fr.umlv.tatoo.cc.common.main.CommandLineParser
 * @see fr.umlv.tatoo.cc.common.main.CommandLineParser.Option
 */
public class UsageFormatter {
  
  private static String NEWLINE = String.format("%n");
  /** returns the usage of a set of command.
   * @param comment general comment of the program.
   * @param commands map that associate options informations
   *  {@link fr.umlv.tatoo.cc.common.main.CommandLineParser.Option Info}
   *  to a command. 
   * @param depth shift in character for each line of the usage 
   * @param optionLength size in caracter reserved for printing option information
   * @return the formatted usage
   */
  public String usage(String comment,Map<? extends Command<?>,? extends Set<? extends Option>> commands,int depth,int optionLength) {
    StringBuilder builder=new StringBuilder();
    if (comment!=null)
       builder.append(comment).append(NEWLINE);
    
    for(Map.Entry<? extends Command<?>,? extends Set<? extends Option>> mapEntry:commands.entrySet()) {
      Command<?> command=mapEntry.getKey();
      Set<? extends Option> value=mapEntry.getValue();
      for(Option item:value) {
        space(builder,depth);
        StringBuilder optBuilder=new StringBuilder();
        
        
        String optionName=item.optionName();
        int numberOfArgument=item.numberOfArgument();
        optionText(optBuilder,command,optionName,numberOfArgument);
        if (optBuilder.length()>optionLength) {
          builder.append(optBuilder.append(NEWLINE));
          optBuilder.setLength(0);
          space(optBuilder,optionLength+depth);
        }
        else
          space(optBuilder,optionLength-optBuilder.length());
        optBuilder.append(':').append(command.usage(optionName,numberOfArgument)).append(NEWLINE);
        builder.append(optBuilder);
      }
    }
    if (commands.isEmpty())
      builder.setLength(builder.length()-1);
    return builder.toString();
  }
  
  protected StringBuilder optionText(StringBuilder optBuilder,Command<?> command,String option,int numberOfArgument) {
    optBuilder.append(option).append(' ');
    for(int i=0;i<numberOfArgument;i++)
      optBuilder.append(command.usageArgumentName(option,numberOfArgument,i)).append(' ');
    if (numberOfArgument!=0)
      optBuilder.setLength(optBuilder.length()-1);
    return optBuilder;
  }
  
  private static StringBuilder space(StringBuilder builder,int length) {
    for(int i=0;i<length;i++)
      builder.append(' ');
    return builder;
  }
  
  public static final int DEFAULT_OPTION_LENGTH=15;
  public static final int DEFAULT_DEPTH=2;
}
