/*
 * Created on 10 janv. 2006
 */
package fr.umlv.tatoo.cc.lexer.regex.pattern;

import java.util.ArrayList;
import java.util.Map;

import fr.umlv.tatoo.cc.lexer.charset.CharacterInterval;
import fr.umlv.tatoo.cc.lexer.charset.encoding.Encoding;
import fr.umlv.tatoo.cc.lexer.regex.Leaf;
import fr.umlv.tatoo.cc.lexer.regex.Regex;
import fr.umlv.tatoo.cc.lexer.regex.RegexFactory;
import fr.umlv.tatoo.cc.lexer.regex.pattern.tools.GrammarEvaluator;

public class RegexGrammarEvaluator implements GrammarEvaluator {
  public RegexGrammarEvaluator(Map<String,Regex> macroes,Encoding charset) {
    this.macroes = macroes;
    this.encoding = charset;
  }

  public void initial(boolean hatOpt, Regex main, Regex follow) {
    this.beginningOfLineRequired = hatOpt;
    this.main = main;
    this.follow = follow;
  }

  public Regex mainRegex(Regex regex) {
    return regex;
  }
  public void macro(Regex regex) {
    this.macro = regex;
  }

  public void acceptMacro() {
    // nothing
  }
  
  public void acceptPattern() {
    // nothing
  }
  
  public Regex followEmpty() {
    return null;
  }
  
  public Regex followDollar() {
    return RegexFactory.createDollarRegex(encoding);
  }

  public Regex followRegex(Regex regex) {
    return regex;
  }

  public boolean hatEmpty() {
    return false;
  }

  public boolean hatPresent() {
    return true;
  }
  
  public Regex regexMacro(String name) {
    Regex macro = macroes.get(name);
    if (macro == null) {
      throw new IllegalArgumentException("Unknown macro ("+name+")");
    }
    return macro;
  }

  public Regex regexAny() {
    return new Leaf(encoding.getAny());
  }

  public Regex regexLetter(char specialOrNormalLetter) {
    return RegexFactory.letter(specialOrNormalLetter,encoding);
  }

  public char normalLetter(char normalLetter) {
    return normalLetter;
  }

  public char normalSpecialLetter(char specialLetter) {
    return specialLetter;
  }

  public Regex regexString(Regex string) {
    return string;
  }

  public Regex string(Regex string, Regex specialOrStringLetter) {
    return RegexFactory.cat(string, specialOrStringLetter);
  }

  public Regex specialOrStringLetter(Regex specialOrStringLetter) {
    return specialOrStringLetter;
  }

  public Regex stringSpecialLetter(char specialLetter) {
    return RegexFactory.letter(specialLetter,encoding);
  }

  public Regex stringLetter(char stringLetter) {
    return RegexFactory.letter(stringLetter,encoding);
  }

  public Regex regexInterval(ArrayList<CharacterInterval> intervals) {
    return RegexFactory.leaf(intervals, false,encoding);
  }

  public Regex regexIntervalNegate(ArrayList<CharacterInterval> intervals) {
    return RegexFactory.leaf(intervals, true,encoding);
  }

  public ArrayList<CharacterInterval> interval(CharacterInterval interval) {
    ArrayList<CharacterInterval> intervals = new ArrayList<CharacterInterval>();
    intervals.add(interval);
    return intervals;
  }

  public ArrayList<CharacterInterval> intervals(
      ArrayList<CharacterInterval> intervals, CharacterInterval interval) {
    intervals.add(interval);
    return intervals;
  }

  public CharacterInterval intervalSet(char specialOrIntervalLetter,
      char specialOrIntervalLetter2) {
    return new CharacterInterval(specialOrIntervalLetter,
        specialOrIntervalLetter2,encoding);
  }

  public CharacterInterval intervalSingleton(char specialOrIntervalLetter) {
    return new CharacterInterval(specialOrIntervalLetter,encoding);
  }

  public char intervalSpecialLetter(char specialLetter) {
    return specialLetter;
  }

  public char intervalLetter(char intervalLetter) {
    return intervalLetter;
  }

  public Regex regexStar(Regex regex) {
    return RegexFactory.star(regex);
  }

  public Regex regexPlus(Regex regex) {
    return RegexFactory.plus(regex);
  }

  public Regex regexOptional(Regex regex) {
    return RegexFactory.optional(regex);
  }

  public Regex regexRange(Regex regex, int from, int to) {
    return RegexFactory.range(from, to, regex);
  }

  public Regex regexAtLeast(Regex regex, int num) {
    return RegexFactory.atLeast(num, regex);
  }

  public Regex regexTimes(Regex regex, int num) {
    return RegexFactory.times(num, regex);
  }

  public Regex regexPar(Regex regex) {
    return regex;
  }

  public Regex regexCat(Regex regex, Regex regex2) {
    return RegexFactory.cat(regex, regex2);
  }

  public Regex regexOr(Regex regex, Regex regex2) {
    return RegexFactory.or(regex, regex2);
  }

  public Regex getFollow() {
    return follow;
  }

  public Regex getMain() {
    return main;
  }

  public boolean isBeginningOfLineRequired() {
    return beginningOfLineRequired;
  }

  public final Regex getMacro() {
    return this.macro;
  }

  private Regex main;
  private Regex follow;
  private boolean beginningOfLineRequired;
  private Regex macro;
  private final Encoding encoding;
  private final Map<String,Regex> macroes;
}
