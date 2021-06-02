/**
 * Converter.java
 * @author Group J (Ryan Harding, Michael Pate, Adeline Reichert, Austin
 *                  Stephen, and Ben Wilkin)
 * Date: Apr 30, 2021
 * Purpose: Converts between byte arrays and ints, floats, and bytes.
 *          The methods came from:
 *            http://www.java2s.com/Book/Java/Examples/
 *            Convert_data_to_byte_array_back_and_forth.htm
 */

import java.nio.ByteBuffer;

public class Converter
{
  public static int convertToInt(byte[] array)
  {
    ByteBuffer buffer = ByteBuffer.wrap(array);
    return buffer.getInt();
  }

  public static float convertToFloat(byte[] array)
  {
    ByteBuffer buffer = ByteBuffer.wrap(array);
    return buffer.getFloat();
  }

  public static byte convertToByte(byte[] array)
  {
    return array[0];
  }
  
  public static long convertToLong(byte[] array)
  {
    ByteBuffer buffer = ByteBuffer.wrap(array);
    return buffer.getLong();
  }

  public static byte[] convertToByteArray(int value)
  {
    byte[] bytes = new byte[4];
    ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
    buffer.putInt(value);
    return buffer.array();
  }

  public static byte[] convertToByteArray(float value)
  {
    byte[] bytes = new byte[4];
    ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
    buffer.putFloat(value);
    return buffer.array();
  }
  
  public static byte[] convertToByteArray(long value) {

      byte[] bytes = new byte[8];
      ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
      buffer.putLong(value);
      return buffer.array();
  }

};
