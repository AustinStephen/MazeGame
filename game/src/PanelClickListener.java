/**
 * PanelClickListener.java
 * @author Group J (Ryan Harding, Michael Pate, Adeline Reichert, Austin
 *                  Stephen, and Ben Wilkin)
 * Date: Apr 30, 2021
 * Purpose: Stores and implements all the logic of tile selection and
 *          tile movement.
 */

//package game;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.InputEvent;

import javax.swing.BorderFactory;
import java.awt.Color;
import java.util.Timer;

public class PanelClickListener implements MouseListener
{
  private static Tile selectedFirst = null;
  private boolean rightClicked = false;
  private GameWindow sourceWindow;

  public PanelClickListener(GameWindow g)
  {
    this.sourceWindow = g;
  }

  // When a tile is clicked, it is selected (highlighted in red)
  // If another tile is already selected, when a tile is clicked, the
  // two tiles will swap images if one of the tiles is empty.
  @Override
  public void mouseClicked(MouseEvent e) 
  {
    Tile source = (Tile)e.getSource();

    if(rightClicked)
    {
      if(!source.isEmpty())
      {
        source.rotate90(1);
        source.repaint();
        sourceWindow.setGameAsChanged();

        if(selectedFirst != null)
        {
          selectedFirst.setBorder(BorderFactory.createLineBorder(Color.red));
        }
      }
      rightClicked = false;

      // Check if the rotation resulted in the user having won the game
      sourceWindow.winPopup();
      return;
    }

    // If no tiles are already selected...
    if(selectedFirst == null)
    {
      // If the clicked tile is a maze piece
      if(!source.isEmpty())
      {
        // Store it and highlight it in red
        selectedFirst = source;
        source.setBorder(BorderFactory.createLineBorder(Color.red));
      }
    }

    // If one tile is already selected...
    else
    {
      // If both selected tiles are maze pieces...
      if(!source.isEmpty() && !selectedFirst.isEmpty())
      {
        // If the clicked tile is different from the one selected, both tiles
        // will flash red, to indicate an illegal move has been attempted
        if(source != selectedFirst)
        {
          Timer timer = new Timer();
          TileFlasher tileFlasher = new TileFlasher();
          tileFlasher.helper(selectedFirst, source, source.getBackground());

          source.setBackground(Color.red);
          selectedFirst.setBackground(Color.red);

          timer.schedule(tileFlasher, 500);
        }
        else
        {
          setBorderVisibility(selectedFirst);
          selectedFirst = null;
        }
        return;
      }

      // Swap the images on the two tiles
      swapImages(selectedFirst, source);
      sourceWindow.setGameAsChanged();

      // Swap the tiles' empty variable, so each Tile knows if it has a
      // maze piece or is empty
      boolean tempEmpty = selectedFirst.isEmpty();
      selectedFirst.setEmpty(source.isEmpty());
      source.setEmpty(tempEmpty);

      // Determine new border visibility
      setBorderVisibility(selectedFirst);
      setBorderVisibility(source);

      // Reset so that the PanelClickListener knows no tiles are selected
      selectedFirst = null;

      // Check if the move resulted in the user having won the game
      sourceWindow.winPopup();
    } 
  }

  @Override
  public void mousePressed(MouseEvent e) 
  {
    // Used to determine if the mouse was right or left clicked
    if((e.getModifiersEx() & InputEvent.BUTTON3_DOWN_MASK) == 
                                                   InputEvent.BUTTON3_DOWN_MASK)
    {
      rightClicked = true;
    }
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

  // Helper function to swap the images on two selected tiles
  private void swapImages(Tile t1, Tile t2)
  {
    float[] tempFloatArr = t1.getLineCoordinateList();
    t1.setLineCoordinateList(t2.getLineCoordinateList());
    t2.setLineCoordinateList(tempFloatArr);
    
    int tempOrientation = t1.getOrientation();
    t1.setOrientation(t2.getOrientation());
    t2.setOrientation(tempOrientation);
    
    int tempHome = t1.getHome();
    t1.setHome(t2.getHome());
    t2.setHome(tempHome);

    t1.repaint();
    t2.repaint();
  }

  // Helper function to reset highlighting and ensure that maze pieces never 
  // have a border
  private void setBorderVisibility(Tile t1)
  {
    // If the tile is an empty piece, turn on its border. 
    if(t1.isEmpty())
    {
      t1.setBorder(BorderFactory.createLineBorder(Color.black));
    }
    // If the tile is a maze piece, turn off its border.
    else
    {
      t1.setBorder(BorderFactory.createEmptyBorder());
    }
  }

  // Function to be used when the reset button is pressed or a click is made
  // on the background (and selectedFirst needs to deselect)
  public static void resetSelectedFirst()
  {
    selectedFirst = null;
  }

  // To be used to help BackgroundClickListener unselect selectedFirst
  public static Tile getSelectedFirst()
  {
    return selectedFirst;
  }

};