package fr.umlv.tatoo.cc.common.generator.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Map;

import fr.umlv.tatoo.cc.Tatoo;
import fr.umlv.tatoo.cc.common.generator.GeneratorException;
import fr.umlv.tatoo.cc.common.generator.TemplateEngine;
import fr.umlv.tatoo.cc.common.log.Info;
import fr.umlv.tatoo.motocity.MotoBatch;
import fr.umlv.tatoo.motocity.compiler.MemoryCompiler;

public class Motocity implements TemplateEngine {

  public void generate(Map<String, ?> parameterMap, String templateName, File output,
      Class<?> clazz, Iterable<? extends File> classPath, File debugFile) throws GeneratorException {
    
    HashSet<File> classPathSet=new HashSet<File>();
    if (classPath!=null) {
      for(File path:classPath) {
        classPathSet.add(path);
      }
    }
    File tatooJarFile = MemoryCompiler.getPath(Tatoo.class);
    classPathSet.add(tatooJarFile);
    
    try {
      InputStream inputStream=clazz.getResourceAsStream(templateName+".mc");
      if (inputStream==null)
        throw new GeneratorException(templateName+".mc not found");
      
      Info.info("Generating from "+templateName+".mc");
      MotoBatch.applyTemplate(null,templateName,new InputStreamReader(inputStream,"UTF-8"),
            new PrintWriter(new OutputStreamWriter(new FileOutputStream(output),"UTF-8")),
            parameterMap, classPathSet,
            Motocity.class.getClassLoader(), debugFile);
    } catch (UnsupportedEncodingException e) {
      throw new GeneratorException(e);
    } catch (IOException e) {
      throw new GeneratorException(e);
    } 
  }
  
  public static Motocity INSTANCE = new Motocity();
  
  private Motocity() {
    // enforce singleton pattern
  }
}
