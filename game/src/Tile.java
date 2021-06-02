/**
 * Tile.java
 * @author Group J (Ryan Harding, Michael Pate, Adeline Reichert, Austin
 *                  Stephen, and Ben Wilkin)
 * Date: Apr 30, 2021
 * Purpose: Class of type tile which allows each of the 32 tile slots to know
 *          and communicate if they contain a maze piece or empty image, and if
 *          they contain a maze piece, what the coordinates of all their maze
 *          lines are and their current orientation. Additionally generates the
 *          random placement of each of the 16 tiles.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.Collections;
import java.util.ArrayList;

public class Tile extends JPanel
{
  public static final long serialVersionUID=1;
  private boolean empty;
  private float[] lineCoordinateList;
  private static ArrayList<Integer> randomPlacement =
                                                     new ArrayList<Integer>(16);
  private int orientation;
  private int home;

  public Tile(GridBagLayout tileLayout, PanelClickListener mouseListener)
  {
    super(tileLayout);
    addMouseListener(mouseListener);
    setPreferredSize(new Dimension(100,100));

    Color lightBlue = new Color(176, 237, 255);
    setBackground(lightBlue);

    setEmpty(false);
    setLineCoordinateList(null);
    setOrientation(0);
    setHome(-1);

    //Generate random placement of the 16 tiles
    if(randomPlacement.isEmpty())
    {
      for(int i = 0; i < 16; i++)
      {
        randomPlacement.add(i);
      }
      shuffleRandomPlacement();
    }
  }

  public boolean isEmpty()
  {
    return empty;
  }

  public void setEmpty(boolean newEmpty)
  {
    empty = newEmpty;
  }

  public float[] getLineCoordinateList()
  {
    return lineCoordinateList;
  }

  public void setLineCoordinateList(float[] newLineCoordinateList)
  {
    if(newLineCoordinateList == null)
    {
      lineCoordinateList = null;
    }
    else 
    {
      lineCoordinateList = new float[newLineCoordinateList.length];
      for(int i = 0; i < lineCoordinateList.length; i++)
      {
        lineCoordinateList[i] = newLineCoordinateList[i];
      }
    }
  }

  public static void shuffleRandomPlacement()
  {
    Collections.shuffle(randomPlacement);
  }

  public static int getRandomPlace(int index)
  {
    return randomPlacement.get(index);
  }

  public int getOrientation()
  {
    return orientation;
  }

  public void setOrientation(int newOrientation)
  {
    orientation = newOrientation % 4;
  }

  public int getHome()
  {
    return home;
  }

  public void setHome(int newHome)
  {
    home = newHome;
  }

  public void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    draw(g);  
  }

  private void draw(Graphics g)
  {
    Graphics2D g2d = (Graphics2D)g;
    g2d.setStroke(new BasicStroke(3.0f));

    if(lineCoordinateList == null)
    {
      return;
    }

    for(int i = 0; i < (lineCoordinateList.length / 4); i++)
    {
      int placeInList = i*4;
      float point1 = lineCoordinateList[placeInList];
      float point2 = lineCoordinateList[(placeInList+1)];
      float point3 = lineCoordinateList[(placeInList+2)];
      float point4 = lineCoordinateList[(placeInList+3)];

      g2d.draw(new Line2D.Float(point1,point2,point3,point4));
    }
  }

  public void rotate90(int numberOfRotations)
  {
    for(int j=0; j <numberOfRotations; j++)
    {    
      for(int i=0;i<lineCoordinateList.length/2;i++)
      {
        int placeInList = i*2;
        float x = lineCoordinateList[placeInList];
        float y = lineCoordinateList[(placeInList+1)];
        // subtracting offset to rotate around (50,50)
        x= x-50;
        y= y-50;
        // formula for 90 rotation around origin (-y,x)
        float tmp = x;
        x = -1 *y;
        y = tmp;
        // adding back offset so points are in normal plane
        x = x+50;
        y = y+50; 
        // overwrite position in coordinate list with transformed coordinate
        lineCoordinateList[placeInList] = x;
        lineCoordinateList[(placeInList+1)] = y; 
      }
      orientation = (orientation + 1) % 4;
    }
  }

  public static float[] rotate90Math(float[] toTheoreticallyRotate, 
                                                               int numRotations)
  {
    float[] returnArray = new float[toTheoreticallyRotate.length];
    for(int i = 0; i < returnArray.length; i++)
    {
      returnArray[i] = toTheoreticallyRotate[i];
    }
    for(int j=0; j <numRotations; j++)
    {   
      for(int i=0;i<returnArray.length/2;i++)
      {
        int placeInList = i*2;
        float x = returnArray[placeInList];
        float y = returnArray[(placeInList+1)];
        // subtracting offset to rotate around (50,50)
        x= x-50;
        y= y-50;
        // formula for 90 rotation around origin (-y,x)
        float tmp = x;
        x = -1 *y;
        y = tmp;
        // adding back offset so points are in normal plane
        x = x+50;
        y = y+50; 
        // overwrite position in coordinate list with transformed coordinate
        returnArray[placeInList] = x;
        returnArray[(placeInList+1)] = y; 
      }
    }
    return returnArray;
  }

};
