/**
 * Main.java
 * 
 * @author Group J (Ryan Harding, Michael Pate, Adeline Reichert, Austin
 *                  Stephen, and Ben Wilkin)
 * Date: Apr 30, 2021
 * 
 * @author Kim Buckner
 * Date: Jan 14, 2021
 *
 * Purpose: A working fifth implementation (Program 05) for COSC 3011
 *          Draws a game board, tile slots, and buttons
 *          Tiles have the image of a maze on them
 *          Tiles are moveable by left click and rotatable by right click
 *          User can load and save their mazes
 */

//package game;
import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main 
{
  public static void main(String[] args) throws FileNotFoundException, 
                                                                     IOException
  {
    GameWindow game = new GameWindow("Group J aMaze");

    // 50 extra px for the timer at the top of the game area.
    game.setSize(new Dimension(1000, 1050));

    game.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    game.getContentPane().setBackground(Color.gray);
    game.setUp();

    game.setVisible(true);

    // You will HAVE to read some documentation and catch exceptions so get used
    // to it.   
    try {
      // The 4 that are installed on Linux here
      // May have to test on Windows boxes to see what is there.
      UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
      // This is the "Java" or CrossPlatform version and the default
      //UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
      // Linux only
      //UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
      // really old style Motif
      //UIManager.setLookAndFeel
      //                     ("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
    }
    catch (UnsupportedLookAndFeelException e) {
      // handle possible exception
    }
    catch (ClassNotFoundException e) {
      // handle possible exception
    }
    catch (InstantiationException e) {
      // handle possible exception
    }
    catch (IllegalAccessException e) {
      // handle possible exception
    }

  }

};