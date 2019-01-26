package fr.umlv.tatoo.samples.pseudo.comp.builtin;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import fr.umlv.tatoo.samples.pseudo.comp.BuiltInFunction.OperatorKind;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BuiltinOperator {
  OperatorKind value();
}
