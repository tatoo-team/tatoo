package fr.umlv.tatoo.cc.ebnf.ast;

public enum Kind {
  /** @see AliasDefAST
   */
  ALIAS_DEF,
  /** @see ProductionIdAndVersionDefAST
   */
  PRODUCTION_ID_AND_VERSION,
  /** @see TerminalDefAST
   */
  BLANK_DEF,
  /** @see TerminalDefAST
   */
  BRANCH_DEF,
  /** @see TerminalDefAST
   */
  COMMENT_DEF,
  /** @see DirectiveDefAST
   */
  DIRECTIVE_DEF,
  /** @see EnhancedDefAST
   */
  ENHANCED_OPTIONAL,
  /** @see EnhancedDefAST
   */
  ENHANCED_STAR,
  /** @see EnhancedDefAST
   */
  ENHANCED_PLUS,
  /** @see EnhancedDefAST
   */
  ENHANCED_GROUP,
  /** @see TerminalDefAST
   */
  EOF_DEF,
  /** @see TerminalDefAST
   */
  ERROR_DEF,
  /** @see ImportDefAST
   */
  IMPORT_DEF,
  /** @see MacroDefAST
   */
  MACRO_DEF,
  /** @see NonTerminalDefAST
   */
  NONTERMINAL_DEF,
  /** @see SimpleNodeAST
   */
  NO_KIND,
  /** @see PriorityDefAST
   */
  PRIORITY_DEF,
  /** @see PriorityVarAST
   */
  PRIORITY_VAR,
  /** @see ProductionDefAST
   */
  PRODUCTION_DEF,
  /** @see RuleDefAST
   */
  RULE_DEF,
  /** @see StartNonTerminalSetDefAST
   */
  START_NONTERMINAL_SET_DEF,
  /** @see QuotedIdVarAST
   */
  QUOTED_ID_VAR,
  /** @see RootDefAST
   */
  ROOT_DEF,
  /** @see TerminalDefAST
   */
  TOKEN_DEF,
  /** @see TypeVarAST
   */
  TYPE_VAR,
  /** @see UnquotedIdVarAST
   */
  UNQUOTED_ID_VAR,
  /** @see VariableTypeDefAST
   */
  VARIABLE_TYPE_DEF,
  /** @see VersionDefAST
   */
  VERSION_DEF,
  /** @see VersionVarAST
   */
  VERSION_VAR
}
