#![tatoo logo](tatoo.gif) Tatoo compiler compiler ![animated tatoo logo](tatoo-anim.gif)

## Tatoo is a compiler compiler which features:

- separate specifications for lexer (regular expression based rules), parser (grammar) and semantics (java interface implementation);
- push analyzer: the characters are fed to the analyzer so it allows asynchronous usage (for instance with thread pool and selectors);
- automated lexer rule selection according to tokens expected by the parser (this allows to let user name their variable as some keywords);
- production of shared parser tables for different versions of the language to simplify backward compatibility or allow version change during parsing;

Tatoo is licensed under the GNU Lesser General Public Licence version 3.0 or greater.

Tatoo is developped by Julien Cervelle, Rémi Forax and Gilles Roussel.
