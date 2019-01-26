package fr.umlv.tatoo.cc.common.generator;

import java.io.File;
import java.util.Map;


public interface TemplateEngine {
  void generate(Map<String,?> map,String templateName,File output, Class<?> clazz,Iterable<? extends File> classPath, File debugFile) throws GeneratorException;
}