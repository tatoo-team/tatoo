package fr.umlv.tatoo.cc.lexer.regex;

import java.util.Arrays;

import fr.umlv.tatoo.cc.common.util.MultiMap;
import fr.umlv.tatoo.cc.lexer.charset.CharacterInterval;
import fr.umlv.tatoo.cc.lexer.charset.encoding.Encoding;
import fr.umlv.tatoo.cc.lexer.charset.encoding.UTF16Encoding;

public class TestRegex {
  public static void main(String[] args) {
    Encoding encoding = UTF16Encoding.getInstance();
    Regex quote = RegexFactory.letter('\'',encoding);
    Regex quote2 = quote.cloneRegex();
    Regex set = RegexFactory.createSet(true, Arrays.asList(new CharacterInterval('\\',encoding),new CharacterInterval('\'',encoding)), encoding);
    Regex antis = RegexFactory.cat(
        RegexFactory.letter('\\', encoding),
        RegexFactory.or(RegexFactory.letter('\\',encoding),RegexFactory.letter('\'',encoding)));
    Regex middle = RegexFactory.star(RegexFactory.or(set,antis));
    Regex regex = RegexFactory.cat(RegexFactory.cat(quote, middle),quote2);
    Leaf end = new Leaf();
    Regex root=RegexFactory.cat(regex, end);
    MultiMap<Leaf,Leaf> transitions =
      new MultiMap<Leaf,Leaf>();
    root.computeFollowPos(transitions);
    
    System.out.println(root.firstPos());
    System.out.println(transitions);
    
    AutomatonDecl automaton = RegexFactory.table(regex, UTF16Encoding.getInstance()).createAutomaton();
    System.out.println(automaton);
  }
}
