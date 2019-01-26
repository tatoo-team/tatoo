package fr.umlv.tatoo.cc.common.main;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.umlv.tatoo.cc.common.util.MultiMap;

/** The command line parser :
 *    - permits to add commands by using {@link #CommandLineParser(Command[]) CommandLineParser(commands)} or
 *      {@link #addCommands(Command[]) addCommand(commands)}.
 *      Each command contains a description and 
 *      {@link fr.umlv.tatoo.cc.common.main.Command#register(OptionRegistry) register}
 *      itself as command for a set of options.
 *      When the command line parser find an option, it calls the associated
 *      command method
 *      {@link fr.umlv.tatoo.cc.common.main.Command#execute(String, Object, List) execute(option,userData,argument)}
 *      with the arguments of the option extracted from the command line.
 *      
 *    - parses arguments using the command descriptions
 *      {@link #parse(Object, List) parse(userData,argumentList)} or
 *      {@link #parse(Object, String[]) parse(userData,arguments}.
 *      
 *    - returns {@link #usage(String, int, int) usage}
 *      by delegating to {@link fr.umlv.tatoo.cc.common.main.UsageFormatter}
 *      
 *    
 * @author Remi
 *
 * @param <D> type of the user data.
 */
public class CommandLineParser<D> {
  
  /** Create a command line parser with some commands.
   * @param commands the commands
   */
  public CommandLineParser(Command<? super D>... commands) {
    this("",commands);
  }
  
  /** Create a command line parser with a default option prefix and some commands.
   * @param prefix default prefix for option that doesn't specify a prefix,
   *  see {@link OptionRegistry#registerOption(String, int)
   *             registerOption(optionName,numberOfArgument)}.
   * @param commands the commands
   */
  public CommandLineParser(String prefix,Command<? super D>... commands) {
    this(prefix,new UsageFormatter(),commands);
  }
  
  /** Create a command line parser with a usage formatter and some commands.
   *  This constructor is equivalent to
   *  {@link #CommandLineParser(String, UsageFormatter, Command[]) CommandLineParser("",formatter,commands}.
   * 
   * @param formatter
   * @param commands the commands
   */
  public CommandLineParser(UsageFormatter formatter,Command<? super D>... commands) {
    this("",formatter,commands);
  }
  
  /** Creates a command line parser with a default option prefix, a formatter and some commands.
   * @param prefix the default prefix for option
   * @param formatter the usage formatter
   * @param commands the commands 
   * 
   * @see #addCommands(Command[])
   */
  public CommandLineParser(String prefix,UsageFormatter formatter,Command<? super D>... commands) {
    this.prefix=prefix;
    this.formatter=formatter;
    registeredPrefix.add(prefix);
    addCommands(commands);
  }
  
  /** Add commands to the current command line parser.
   *  This method delegates to each command the fact to
   *  {@link Command#register(OptionRegistry) register itself}
   *  their options.
   * @param commands
   */
  public void addCommands(Command<? super D>... commands) {
    for(final Command<? super D> command:commands) {
      command.register(new OptionRegistry<D>() {
        @Override
        public void registerOption(String optionName,int numberOfArgument) {
          addOptions(prefix,optionName,command,numberOfArgument);
        }
        @Override
        public void registerOption(String prefix,String optionName,int numberOfArgument) {
          addOptions(prefix,optionName,command,numberOfArgument);
          registeredPrefix.add(prefix);
        }
      });
    }
  }
  
  void addOptions(String prefix,String optionName,Command<? super D> command,int numberOfArgument) {
    String opt=prefix+optionName;
    OptionImpl<D> item=new OptionImpl<D>(opt,command,numberOfArgument);
    if (map.put(opt,item)!=null)
      throw new IllegalStateException("option "+opt+" is already declared");
      
    commands.add(command,item);
  }
  
  /** Parse a command line arguments and call the command corresponding to
   *  the registered option.
   *  When an option is found the method
   *  {@link Command#execute(String, Object, List) doIt(optionName,userData,optionArguments)}
   *  of the associated command is called with the user data.
   *  
   * @param userData the user data.
   * @param args arguments received by the method main().
   * @return a list of unrecognized arguments.
   * 
   * @throws IllegalCommandLineArgumentException if an argument starts with
   *  a {@link OptionRegistry#registerOption(String, String, int) registered prefix}
   *  or the {@link #CommandLineParser(String, UsageFormatter, Command[]) default prefix}
   *  and is not a registered option.
   */
  public List<String> parse(D userData,String... args)
    throws IllegalCommandLineArgumentException {
    
    return parse(userData,Arrays.asList(args));
  }
  
  /** Parse a command line arguments and call the command corresponding to
   *  the registered option.
   *  When an option is found the method
   *  {@link Command#execute(String, Object, List) doIt(optionName,userData,optionArguments)}
   *  of the associated command is called with the user data.
   *  
   * @param userData the user data.
   * @param args arguments received by the method main().
   * @return a list of unrecognized arguments.
   * 
   * @throws IllegalCommandLineArgumentException if an argument starts with
   *  a {@link OptionRegistry#registerOption(String, String, int) registered prefix}
   *  or the {@link #CommandLineParser(String, UsageFormatter, Command[]) default prefix}
   *  and is not a registered option.
   */
  public List<String> parse(D userData,List<? extends String> args)
    throws IllegalCommandLineArgumentException {
    
    int size=args.size();
    ArrayList<String> unrecognized=new ArrayList<String>(size);
   
    for(int i=0;i<size;) {
      String arg=args.get(i);
      
      if (registeredPrefix.contains(arg))
        return collectArgs(unrecognized,args,i+1);
      
      OptionImpl<D> option=map.get(arg);
      if (option!=null) {
        //FIXME Remi ensure that the number of argument is correct
        try {
          option.exec(userData,args,i+1);
        } catch(RuntimeException e) {
          throw new IllegalCommandLineArgumentException(e);
        }
        i=option.nextIndex(i+1);
        continue;
      }
      
      for(String pfx:registeredPrefix)
        if (arg.startsWith(pfx))
          throw new IllegalCommandLineArgumentException("unknown option "+arg);
        
      unrecognized.add(arg);
      i++;
    }
    return unrecognized;
  }
  
  private static ArrayList<String> collectArgs(ArrayList<String> unknown,List<? extends String> args, int from) {
    for(int i=from;i<args.size();i++)
      unknown.add(args.get(i));
    return unknown;
  }
  
  /** Returns the usage of the command line.
   *  The implementation of this method is equivalent
   *  to a call to
   *  {@link #usage(String, int, int) usage(comment,DEFAULT_DEPTH,DEFAULT_LENGTH)}.
   * 
   * @param comment general comment of the program.
   * @return the formatted usage
   * 
   * @see UsageFormatter#usage(String, Map, int, int)
   * @see UsageFormatter#DEFAULT_DEPTH
   * @see UsageFormatter#DEFAULT_OPTION_LENGTH
   */
  public String usage(String comment) {
    return usage(comment,
      UsageFormatter.DEFAULT_DEPTH,UsageFormatter.DEFAULT_OPTION_LENGTH);
  }
  
  /** Returns the usage of the command line.
   * @param comment general comment of the program.
   * @param depth shift in character for each line of the usage 
   * @param optionLength size in caracter reserved for printing option information
   * @return the formatted usage
   * 
   * @see UsageFormatter#usage(String, Map, int, int)
   */
  public String usage(String comment,int depth,int optionLength) {
    return formatter.usage(comment,commands,depth,optionLength);
  }
  
  /** Option information.
   */
  public interface Option {
    /** Returns the number of arguments of the option.
     * @return the option's number of arguments
     */
    public int numberOfArgument();
    
    /** Returns the command to execute if the option is recognized.
     * @return the associated command
     */
    public Command<?> command();
    
    /** Returns the name of the option.
     * @return the name of the option
     */
    public String optionName();
  }
  
  private static class OptionImpl<U> implements Option {
    public OptionImpl(String optName,Command<? super U> command,int numberOfArgument) {
      this.optName=optName;
      this.command=command;
      this.numberOfArgument=numberOfArgument;
    }
    void exec(U userData,final List<? extends String> args,final int index)
      throws IllegalCommandLineArgumentException {
      
      List<String> list;
      if (numberOfArgument==0)
        list=Collections.<String>emptyList();
      else
        list=new AbstractList<String>() {
          @Override public int size() {
            return numberOfArgument;
          }
          @Override public String get(int i) {
            return args.get(index+i);
          }
        };
      command.execute(optName,userData, list); 
    }
    
    int nextIndex(int i) {
      return i+numberOfArgument;
    }
    
    public int numberOfArgument() {
      return numberOfArgument;
    }
    public Command<?> command() {
      return command;
    }
    public String optionName() {
      return optName;
    }
    
    private final String optName;
    private final Command<? super U> command;
    final int numberOfArgument;
  }
  
  final String prefix;
  final HashSet<String> registeredPrefix=
    new HashSet<String>();
  private final UsageFormatter formatter;
  private final MultiMap<Command<? super D>,OptionImpl<D>> commands=
    new MultiMap<Command<? super D>,OptionImpl<D>>() {
      @Override protected Map<Command<? super D>,Set<OptionImpl<D>>> createMap() {
        return new LinkedHashMap<Command<? super D>,Set<OptionImpl<D>>>();
      }
      @Override protected Set<OptionImpl<D>> createSet() {
        return new LinkedHashSet<OptionImpl<D>>();
      }
    };
  private final HashMap<String,OptionImpl<D>> map=new HashMap<String,OptionImpl<D>>();
}
