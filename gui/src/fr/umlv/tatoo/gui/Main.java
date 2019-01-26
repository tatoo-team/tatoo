package fr.umlv.tatoo.gui;

import javax.swing.JFrame;

public class Main {
  public static void main(String[] args) {
    GUI gui = new GUI();
    JFrame frame = gui.getFrame();
    frame.setVisible(true);
  }
}
