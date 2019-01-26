package fr.umlv.tatoo.cc.ebnf.ast.analysis;

import fr.umlv.tatoo.cc.common.log.DiagnosisReporter;
import fr.umlv.tatoo.cc.common.log.DiagnosisReporter.Level;
import fr.umlv.tatoo.runtime.node.Node;

public interface ASTDiagnosisReporter {
  public enum ErrorKey implements DiagnosisReporter.Key {
    import_not_qualified,
    priority_var_unknown,
    version_var_unknown,
    priority_duplicate,
    rule_duplicate,
    macro_duplicate,
    rule_regex_parsing,
    rule_macro_parsing,
    terminal_eof_duplicate,
    terminal_duplicate,
    terminal_alias_duplicate,
    nonterminal_duplicate,
    nonTerminal_var_unknown,
    nonTerminal_var_unknown_terminal_instead,
    nonTerminal_var_terminal_instead,
    terminal_var_unknown,
    terminal_var_nonterminal_instead,
    terminal_var_branch_nonterminal_instead,
    alias_name_already_id_of_terminal_or_nonterminal,
    two_terminals_with_same_alias,
    duplicate_type_declared,
    type_declaration_illegal_append_mode,
    attribute_duplicate,
    unknown_directive;
    
    public Level defaultLevel() {
      return Level.ERROR;
    }
  }
  
  public enum WarningKey implements DiagnosisReporter.Key {
    type_not_exist,
    import_duplicate,
    bad_associativity,
    startnonterminals_empty_recover,
    startnonterminals_duplicates,
    generated_production_id,
    duplicate_type_declared_recover,
    check_unused_terminal;
    
    public Level defaultLevel() {
      return Level.WARNING;
    }
  }
  
  public boolean isOnError();
  
  public void signal(DiagnosisReporter.Key key, Node node, Object... data);
}
