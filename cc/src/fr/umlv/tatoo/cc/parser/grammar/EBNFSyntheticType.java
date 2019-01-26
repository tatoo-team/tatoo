package fr.umlv.tatoo.cc.parser.grammar;

public enum EBNFSyntheticType {
  /** An empty star production
   */
  STAR_EMPTY,
  
  /** A singleton star production
   */
  STAR_SINGLETON,
  
  /** A left recursive star production
   */
  STAR_RECURSIVE_LEFT,
  
  /** A right recursive star production
   */
  STAR_RECURSIVE_RIGHT,
  STAR_PASS_THROUGH,
  
  /** An optional empty production
   */
  OPTIONAL_EMPTY,
  
  /** An optional production
   */
  OPTIONAL_SINGLETON,
  
  /** A fake production use to group variables
   */
  ANONYMOUS     
}