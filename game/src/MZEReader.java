/**
 * MZEReader.java
 * @author Group J (Ryan Harding, Michael Pate, Adeline Reichert, Austin
 *                  Stephen, and Ben Wilkin)
 * Date: Apr 30, 2021
 * Purpose: Reads in and stores the contents of original and played maze files
 */

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MZEReader extends FileInputStream
{
  private int[] numOfLinesStorage;
  private float[][] coordinateStorage;
  private int[] placementStorage = null;
  private int[] rotationStorage = null;
  private long loadedTime = 0;

  public MZEReader (String name, GameWindow window) throws 
                    FileNotFoundException, IOException, InvalidMazeFileException
  {
    super(name);

    // Read in the first 4 bytes of the file
    byte first4 = readByte();
    byte second4 = readByte();
    byte third4 = readByte();
    byte fourth4 = readByte();

    // The first 4 bytes match that of an already played file
    if(first4 == -54 && second4 == -2 && third4 == -34 && fourth4 == -19)
    {
      playedReader();
    }

    // The first 4 bytes match that of an original file
    else if(first4 == -54 && second4 == -2 && third4 == -66 && fourth4 == -17)
    {
      originalReader();
    }

    else
    {
      throw(new InvalidMazeFileException(name));
    }

    // Should return -1
    // A check to make sure you are at the EOF
    //System.out.println(readInt());
  }

  private void playedReader() throws IOException
  {
    int intHolder = -100;
    float floatHolder = 1000.0f;
    
    // Read in an integer number of tiles, N
    int numTiles = readInt();
    placementStorage = new int[numTiles];
    rotationStorage = new int[numTiles];
    numOfLinesStorage = new int[numTiles];
    coordinateStorage = new float[numTiles][];
    
    // Read in a long integer, number of seconds played so far
    loadedTime = readLong();

    for(int i = 0; i < numTiles; i++)
    {
      // Read in the tile location; should be a number 0 to N-1
      intHolder = readInt();
      placementStorage[i] = intHolder;

      // Read in the rotation of the tile
      intHolder = readInt();
      rotationStorage[i] = intHolder;

      // Read in the number of lines on the tile
      intHolder = readInt();
      numOfLinesStorage[i] = intHolder;

      coordinateStorage[i] = new float[numOfLinesStorage[i] * 4];
      // Read in the coordinates
      for(int j = 0; j < (4 * numOfLinesStorage[i]); j++)
      {
        floatHolder = readFloat();
        coordinateStorage[i][j] = floatHolder;
      }
    }
  }

  private void originalReader() throws IOException
  {
    int intHolder = -100;
    float floatHolder = 1000.0f;
    long longHolder = -1000;

    // Read in an integer number of tiles, N
    int numTiles = readInt();

    numOfLinesStorage = new int[numTiles];
    coordinateStorage = new float[numTiles][];

    // Read in number of seconds played so far; should be 0 (and ignore)
    longHolder = readLong();

    for(int i = 0; i < numTiles; i++)
    {
      // Read in the tile number; should be i (and ignore)
      intHolder = readInt();

      // Read in the rotation of the tile (and ignore)
      intHolder = readInt();

      // Read in the number of lines on the tile
      intHolder = readInt();
      numOfLinesStorage[i] = intHolder;
      
      coordinateStorage[i] = new float[numOfLinesStorage[i] * 4];
      // Read in the coordinates
      for(int j = 0; j < (4 * numOfLinesStorage[i]); j++)
      {
        floatHolder = readFloat();
        coordinateStorage[i][j] = floatHolder;
      }
    }
  }

  private int readInt() throws IOException
  {
    byte [] toRead = new byte[4];
    if(read(toRead) == -1)
    {
      // At EOF. Don't attempt a conversion
      return -1;
    }
    return Converter.convertToInt(toRead);
  }

  private float readFloat() throws IOException
  {
    byte [] toRead = new byte[4];
    if(read(toRead) == -1)
    {
      // At EOF. Don't attempt a conversion
      return -1;
    }
    return Converter.convertToFloat(toRead);
  }

  private byte readByte() throws IOException
  {
    byte [] toRead = new byte[1];
    if(read(toRead) == -1)
    {
      //At EOF. Don't attempt a conversion
      return -1;
    }
    return Converter.convertToByte(toRead);
  }
  
  private long readLong() throws IOException
  {
    byte [] toRead = new byte[8];
    if(read(toRead) == -1)
    {
      // At EOF. Don't attempt a conversion
      return -1;
    }
    return Converter.convertToLong(toRead);
  }

  public int readFileNumLines(int tileNum)
  {
    return numOfLinesStorage[tileNum];
  }

  public float[] readFileCoordinateList(int tileNum)
  {
    return coordinateStorage[tileNum];
  }

  public int readFilePlacement(int tileNum)
  {
    if(placementStorage == null)
    {
      return -1;
    }
    return placementStorage[tileNum];
  }

  public int readFileRotation(int tileNum)
  {
    if(rotationStorage == null)
    {
      return -1;
    }
    return rotationStorage[tileNum];
  }

  public long readFileTime()
  {
    //return 500;
    return loadedTime;
  }

};
