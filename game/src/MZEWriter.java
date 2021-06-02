/**
 * MZEWriter.java
 * @author Group J (Ryan Harding, Michael Pate, Adeline Reichert, Austin
 *                  Stephen, and Ben Wilkin)
 * Date: Apr 30, 2021
 * Purpose: Writes out the contents of original and played maze files
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MZEWriter
{
  private byte mazeData[];
  private Long time;
  // Not static, so counter is reset by a new instance of class

  public MZEWriter()
  {
    // number of coordinates is unknown and must be built dynamically
    mazeData = new byte[0];
    time =0L;
  }

  // Sets the field mazeData
  // Should only be passed a tile object if it needs written to the file
  public void addData(Tile tile, int location)
  {
      // Setting tile location in the current game field
      // converting tile location to bytes
      byte locationBytes[] = new byte[4];
      locationBytes = Converter.convertToByteArray(location);

      // Handling rotation
      // Getting rotation
      int rotation = tile.getOrientation();
      // Converting rotation to byte array
      byte rotationBytes[] = new byte[4];
      rotationBytes = Converter.convertToByteArray(rotation);

      // Handling coordinates and computing number of lines
      // Getting coordinate list
      float coordFloatTmp[] = tile.getLineCoordinateList();
      // To correct for rotations when read in
      int offsettingRotations = (4 - rotation)%4;
      float coordFloat[] = Tile.rotate90Math(coordFloatTmp,offsettingRotations);

      // Getting number of lines
      int numberOfLines = coordFloat.length/4;
      //converting number of lines to byte array
      byte numberOfLinesBytes[] = new byte[4];
      numberOfLinesBytes = Converter.convertToByteArray(numberOfLines);

      // converting coordinate list to byte array
      byte coordByte[] = new byte[coordFloat.length*4];
      for(int i=0; i <coordFloat.length; i++)
      {
        byte tmp[] = new byte[4];
        tmp = Converter.convertToByteArray(coordFloat[i]);
        int position = i*4;
        coordByte[position]= tmp[0];
        coordByte[position+1]= tmp[1];
        coordByte[position+2]= tmp[2];
        coordByte[position+3]= tmp[3];
      }

      //appending to mazeData    

      // new mazeData size computed by:
      // new = length old data(varies) + length new coordinates(varies) + 
      // length rotation(4) + length location(4) + length of number of lines(4)  
      int newLength = coordByte.length + mazeData.length + 4 + 4 + 4;
      //Creating adder storage array of correct length to build new data onto
      byte adder[] = new byte[newLength];
      // Copy existing data into adder
      for(int i=0; i < mazeData.length ; i++ )
      { 
        adder[i] = mazeData[i];
      }
      // Copy new data into adder
      for(int i= mazeData.length; i < newLength; i++)
      {
        // Gives the count for number of times in loop ignoring old data offset
        int position = i - mazeData.length;

        if( position < 4) // first 4 write location
        {
          adder[i] = locationBytes[position];
        }
        else if(position < 8) // write rotation 4 bytes
        {
          // subtracting off how far we are to start at 0 in position 
          adder[i] = rotationBytes[position -4];
        }
        else if(position < 12) // write # of lines 4 bytes
        {
          // subtracting off how far we are to start at 0 in position 
          adder[i] = numberOfLinesBytes[position - 8];  
        }
        else if(position >= 12) // last write in all the points length unknown
        {
          // subtracting off how far we are to start at 0 in position
          adder[i] = coordByte[position -12];
        }
      }
      // changing the memory address of mazeData to adder
      mazeData = adder;
  }   

  public void writeToFile(File file)
  {
    FileOutputStream fos = null;

    try {
      fos = new FileOutputStream(file);

      // Writing hex for played 
      fos.write((byte)0xca); // 0xca
      fos.write((byte)0xfe); // 0xfe
      fos.write((byte)0xde); //0xde
      fos.write((byte)0xed); //0xed

      // Writing # of tiles
      fos.write(Converter.convertToByteArray(16));
      
      // Writing time
      // time has to be set in game window or it will print 0
      fos.write(Converter.convertToByteArray(time));
      
      // All other data should already be added to mazeData gets written here
      for(int i=0; i <mazeData.length/4; i++)
      {
        // Take the data out of mazeData and write 4 bytes at a time
        byte writer[] =  new byte[4];
        int position = i*4;
        writer[0] = mazeData[position];
        writer[1]= mazeData[position + 1];
        writer[2] = mazeData[position + 2];
        writer[3]= mazeData[position + 3];
        //System.out.print("Float value: ");
        //System.out.print(Converter.convertToFloat(writer));
        //System.out.print(" Int value: ");
        //System.out.println(Converter.convertToInt(writer));
        fos.write(writer);
      }

    }
    // Just to compile handled before the file is passed 
    catch (FileNotFoundException e) {
    }
    catch (IOException ioe) {
    }
    finally {
      // close the streams using close method
      try {
        if (fos != null) {
          fos.close();
        }
      }
      catch (IOException ioe) {
      }
    }
  }

  // Test if a tile contains a maze piece 
  // checks if lines are drawn in a tile to see if we need its data
  public boolean testForMazePiece(Tile testTile)
  {
    boolean tileIsEmpty = !testTile.isEmpty();
    return tileIsEmpty;  
  }

  public void getTime(Long gameState)
  {
      time = gameState;
  }
};