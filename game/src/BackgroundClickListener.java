/**
 * BackgroundClickListener.java
 * @author Group J (Ryan Harding, Michael Pate, Adeline Reichert, Austin
 *                  Stephen, and Ben Wilkin)
 * Date: Apr 16, 2021
 * Purpose: Tracks clicks on the background elements, so that when they are
 *          clicked, any selected tile deselects
 */

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;

public class BackgroundClickListener implements MouseListener
{
  // Used when a background element is clicked to deselect selectedFirst, if
  // selected
  @Override
  public void mouseClicked(MouseEvent e) 
  {
    // If nothing is selected, don't do anything
    if(PanelClickListener.getSelectedFirst() == null)
    {
      return;
    }

    Tile selectedFirst = PanelClickListener.getSelectedFirst();

    // If the tile is an empty piece, turn on its border. 
    if(selectedFirst.isEmpty())
    {
      selectedFirst.setBorder(BorderFactory.createLineBorder(Color.black));
    }
    // If the tile is a maze piece, turn off its border.
    else
    {
      selectedFirst.setBorder(BorderFactory.createEmptyBorder());
    }
    PanelClickListener.resetSelectedFirst();
  }

  @Override
  public void mousePressed(MouseEvent e) 
  {
    // TODO Auto-generated method stub
  }

  @Override
  public void mouseReleased(MouseEvent e) 
  {
    // TODO Auto-generated method stub
  }

  @Override
  public void mouseEntered(MouseEvent e) 
  {
    // TODO Auto-generated method stub
  }

  @Override
  public void mouseExited(MouseEvent e) 
  {
    // TODO Auto-generated method stub
  }

};
