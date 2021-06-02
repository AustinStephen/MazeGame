/** 
 * GameWindowListener.java
 * @author Group J (Ryan Harding, Michael Pate, Adeline Reichert, Austin
 *                  Stephen, and Ben Wilkin)
 * Date: Apr 30, 2021
 * Purpose: Gives the ability to close the game at all points
 */

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameWindowListener extends WindowAdapter 
{
  public GameWindowListener()
  {
    super();
  }

  @Override
  public void windowClosing(WindowEvent e)
  {
    GameWindow source = (GameWindow)e.getSource();
    if (source.quitWithChanges() == true)
    {
      System.exit(0);
    }
  }

  @Override
  public void windowClosed(WindowEvent e)
  {
    //System.out.println("Window Closed");
  }

};