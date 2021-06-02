/** 
 * InvalidMazeFileException.java
 * @author Group J (Ryan Harding, Michael Pate, Adeline Reichert, Austin
 *                  Stephen, and Ben Wilkin)
 * Date: Apr 30, 2021
 * Purpose: Thrown when the first 4 bytes of a file are incorrect
 */

public class InvalidMazeFileException extends Exception
{
  public static final long serialVersionUID=1;

  public InvalidMazeFileException(String errorMessage)
  {
    super(errorMessage);
  }

};
