package fr.umlv.tatoo.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.EnumMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import fr.umlv.tatoo.gui.jit.AttributeHolder;
import fr.umlv.tatoo.gui.jit.ParserSettings;
import fr.umlv.tatoo.gui.jit.RuntimeAnalyzer;
import fr.umlv.tatoo.runtime.buffer.impl.LocationTracker;
import fr.umlv.tatoo.runtime.buffer.impl.ReaderWrapper;

public class GUI {
  RuntimeAnalyzer<ReaderWrapper,CharSequence> analyzer;
  final AttributeHolder holder = new AttributeHolder();
  final TreeStackModel treeStack = new TreeStackModel(holder,this);
  final TreeViewBuilder treeViewBuilder = new TreeViewBuilder(treeStack);
  final ParserSettings settings = new ParserSettings();
  final ComboBoxModel versions = new ComboBoxModel(settings,ParserSettings.Property.version);
  final ComboBoxModel starts = new ComboBoxModel(settings,ParserSettings.Property.start);
  
  private static enum Files {
    Lexer,Parser,Tools,Input
  }
  private static final Files[] fileKeys = Files.values();
  EnumMap<Files, File> files = new EnumMap<Files, File>(Files.class);
  File last = new File(System.getProperty("user.dir"));
  
  final JPanel filesPanel = createFilePanel();
  
  private JPanel createFilePanel() {
    final JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.LINE_END;
    for(Files fileVar : fileKeys) {
      final Files file = fileVar;
      gbc.gridwidth = 1;
      gbc.fill = GridBagConstraints.NONE;
      gbc.weightx = 0;
      panel.add(new JLabel(file+":"),gbc);
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.weightx = 1;
      final JTextField text = new JTextField();
      text.addActionListener(new ActionListener() {
      
        public void actionPerformed(ActionEvent e) {
          files.put(file,new File(text.getText()));
        }
      
      });
      panel.add(text,gbc);
      gbc.weightx=0;
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      JButton button = new JButton("Choose...");
      button.addActionListener(new ActionListener() {
      
        public void actionPerformed(ActionEvent e) {
          File old = files.get(file);
          if (old == null) {
            old = last;
          }
          JFileChooser chooser = new JFileChooser(old);
          chooser.showOpenDialog(panel);
          File choosed = chooser.getSelectedFile();
          if (choosed == null)
            return;
          last = choosed;
          files.put(file,choosed.getAbsoluteFile());
          text.setText(choosed.getAbsolutePath());
        }
      
      });
      panel.add(button,gbc);
    }
    return panel;
  }
  
  void resetAnalyzer() throws ParserConfigurationException, SAXException, IOException {
    treeStack.reset();
    analyzer = new RuntimeAnalyzer<ReaderWrapper,CharSequence>(
        files.get(Files.Lexer),
        files.get(Files.Parser),
        files.get(Files.Tools),
        new ReaderWrapper(new FileReader(files.get(Files.Input)),new LocationTracker()),
        treeStack.getParserListener(),
        treeStack.getErrorRecoveryListener(),
        holder,settings);
    versions.setObjects(analyzer.getVersions());
    starts.setObjects(analyzer.getStarts());
  }
  
  public JButton getReset() {
    JButton button = new JButton("Reset");
    button.addActionListener(new ActionListener() {
    
      public void actionPerformed(ActionEvent e) {
        
          Thread t = new Thread() {
            @Override
            public void run() {
              try {
                resetAnalyzer();
                analyzer.run();
              } catch (final Exception e) {
                EventQueue.invokeLater(new Runnable() {
                
                  public void run() {
                    resetOnError(e);
                  }
                
                });
              }
             }
          };
          t.setDaemon(true);
          t.start();
      }
    
    });
    return button;
  }
  
  public JButton getRestart() {
    JButton button = new JButton("Restart");
    button.addActionListener(new ActionListener(){
    
      public void actionPerformed(ActionEvent e) {
        if (analyzer == null) {
          displayError("Analyzer not initialized");
          return;
        }
        try {
          treeStack.reset();
          analyzer.reset(new ReaderWrapper(new FileReader(files.get(Files.Input)),new LocationTracker()));
        } catch (FileNotFoundException e1) {
          resetOnError(e1);
        }
      }
    
    });
    return button;
  }


  public JButton getStep() {
    JButton button = new JButton("Step");
    button.addActionListener(new ActionListener(){
    
      public void actionPerformed(ActionEvent e) {
        if (analyzer == null) {
          displayError("Analyzer not initialized");
          return;
        }
        treeStack.step();
      }
    
    });
    return button;
  }

  public JPanel getFilesPanel() {
    return filesPanel;
  }
  
  public JComponent getMainPanel() {
    return new JScrollPane(treeViewBuilder.getView());
  }
  
  public JFrame getFrame() {
    JFrame frame = new JFrame("Main");
    frame.add(getFilesPanel(),BorderLayout.NORTH);
    frame.add(getMainPanel());
    JPanel panel = new JPanel(new GridLayout(1,0));
    panel.add(getReset());
    panel.add(getRestart());
    panel.add(getStep());
    panel.add(getVersions());
    panel.add(getStarts());
    frame.add(panel,BorderLayout.SOUTH);
    frame.setSize(800,600);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    return frame;
  }
  
  void resetOnError(Throwable e) {
    analyzer = null;
    StringBuilder builder = new StringBuilder();
    while(e != null) {
      builder.append(e.getClass()).append(':').append(e.getMessage()).append("->");
      e.printStackTrace();
      e=e.getCause();
    }
    builder.setLength(builder.length()-2);
    displayError(builder);
  }
    
  void displayError(Object message) { 
    JOptionPane.showMessageDialog(filesPanel, message,"Error",JOptionPane.ERROR_MESSAGE);
  }
  
  
  public JComboBox getVersions() {
    return new JComboBox(versions);
  }
  
  public JComboBox getStarts() {
    return new JComboBox(starts);
  }
  
}
