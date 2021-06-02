/**
 * TileFlasher.java
 * @author Group J (Ryan Harding, Michael Pate, Adeline Reichert, Austin
 *                  Stephen, and Ben Wilkin)
 * Date: Apr 16, 2021
 * Purpose: Used to make a tile flash for 500 milliseconds to let the user know
 *          that they've attempted an illegal move
 */

import java.awt.Color;
import java.util.TimerTask;

public class TileFlasher extends TimerTask
{
  Tile selectedFirst;
  Tile source;
  Color backgroundCol;

  public void run()
  {
    selectedFirst.setBackground(backgroundCol);
    source.setBackground(backgroundCol);
  }

  public void helper(Tile selectedFirst, Tile source, Color backgroundCol)
  {
    this.selectedFirst = selectedFirst;
    this.source = source;
    this.backgroundCol = backgroundCol;
  }

};