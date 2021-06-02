/** 
 * GameWindow.java
 * @author Group J (Ryan Harding, Michael Pate, Adeline Reichert, Austin
 *                  Stephen, and Ben Wilkin)
 * Date: Apr 30, 2021
 * Purpose: Separate elements of the game board are created here.
 *          This is the staging area for all of the elements of the game.
 *          Also, the majority of popup/UI features are dealt with here as
 *          well as button functionality.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;
import java.util.Timer;

public class GameWindow extends JFrame implements ActionListener
{
  public static final long serialVersionUID=1;
  private Tile[] storagePanels;
  private Tile[] fieldPanels;
  private int[] tileStartingRotations;
  private MZEReader reader;
  private PanelClickListener mouseListener;
  private long gameTimeSeconds;
  private Nav_Bar main_nav;
  private Timer gameTimer = new Timer();
  private gameTimerTask gameTimerHandler = new gameTimerTask(this);
  private boolean timerRunning = true;

  // Flags for changed games
  private boolean gameChanged;
  private boolean gameSaved = false;
  private boolean blankFileState = false;

  public GameWindow(String s)
  {
    super(s);
    GameWindowListener handleClosing = new GameWindowListener();
    this.addWindowListener(handleClosing);
    GridBagLayout gbl=new GridBagLayout();
    setLayout(gbl);
  }

  /**
   * For the buttons
   * @param e is the ActionEvent
   * 
   * BTW can ask the event for the name of the object generating event.
   * The odd syntax for non-java people is that "exit" for instance is
   * converted to a String object, then that object's equals() method is
   * called.
   *
   * Your code MUST not print out anything to console when you submit it.
   */

  public void actionPerformed(ActionEvent e) 
  {
    gameChanged = this.checkForChanges();

    if(blankFileState)
    {
      gameChanged = false;
    }

    if("Quit".equals(e.getActionCommand()))
    {
      if (this.quitWithChanges()) System.exit(0);
    }
    if("Reset".equals(e.getActionCommand())) 
    {
      if(blankFileState)
      {
        return;
      }
      gameSaved = false;
      // Reset the game board and start over.
      if(reader.readFilePlacement(0) == -1)
      {
        setOriginalFileTiles();
        gameTimeSeconds = 0;
        this.startGameTimer();
      }
      else
      {
        setPlayedFileTiles();
        gameTimeSeconds = reader.readFileTime();
        this.startGameTimer();
      }
    }
    if("File".equals(e.getActionCommand()))
    {
      this.stopGameTimer();
      String[] options = {"Load Game", "Save Game", "Cancel"};
      int choice = JOptionPane.showOptionDialog(this,
                                                "Load new game or save?",
                                                "Options",
                                               JOptionPane.YES_NO_CANCEL_OPTION,
                                                JOptionPane.QUESTION_MESSAGE,
                                                null,
                                                options,
                                                options[0]);

      if (choice == 0)
      {
        // Load Game
        // If the file has been changed, warn the user.
        boolean loadNewFile = false;
        if (gameChanged && gameSaved == false)
        {
          String[] fileOptions = {"Load Anyway", "Cancel"};
          choice = JOptionPane.showOptionDialog(this,
                                          "The current maze has been changed.\n"
                                                + "Load new anyway or cancel?",
                                                "Maze changed!",
                                                JOptionPane.YES_NO_OPTION,
                                                JOptionPane.QUESTION_MESSAGE,
                                                null,
                                                fileOptions,
                                                fileOptions[0]);
          if (choice == 0) loadNewFile = true;
        }
        else loadNewFile = true;

        if (loadNewFile)
        {
          this.loadFile(false);
        }
      }
      else if (choice == 1)
      {
        // Save Game
        this.saveGame();
      }
      else if (choice == 2)
      {
        // Cancel
        //System.out.println("Cancel");
      }
      if(!blankFileState)
      {
        this.startGameTimer();
      }
    }
  }

  /**
   *  Establishes the initial board
   */
  public void setUp() throws FileNotFoundException, IOException
  {
    BackgroundClickListener backListener = new BackgroundClickListener();
    addMouseListener(backListener);
    tileStartingRotations = generateRotations();

    storagePanels = new Tile[16];
    fieldPanels = new Tile[16];
    for (int index = 0; index < 16; index++) 
    {
      storagePanels[index] = new Tile(new GridBagLayout(), mouseListener);
      fieldPanels[index] = new Tile(new GridBagLayout(), mouseListener);
    }
    
    main_nav = new Nav_Bar(this); // Navigation Bar Constructor

    // Attempt to open default.mze
    reader = null;
    try {
      File file = new File("../game/input/default.mze");
      if (file.exists() == false) 
        throw new FileNotFoundException(file.getAbsolutePath());
      String p1 = file.getAbsolutePath();
      reader = new MZEReader(p1, this);
      reader.close();
    }
    catch (Exception e) {
      String[] options = {"Open New File", "Exit"};
      int choice = JOptionPane.showOptionDialog(this,
                                          "The selected maze file is invalid.\n"
                                                + "Choose a new file or exit.",
                                                "Maze invalid!",
                                                JOptionPane.YES_NO_OPTION,
                                                JOptionPane.ERROR_MESSAGE,
                                                null,
                                                options,
                                                options[0]);
      if (choice == 1) 
      {
        System.exit(0);
      }
      else if (choice == 0)
      {
        this.loadFile(true);
      }
    }
    
    // Start timer
    gameTimeSeconds = reader.readFileTime();
    main_nav.setVisibleTime(gameTimeSeconds);
    gameTimer.schedule(gameTimerHandler, 0, 1000);
    this.startGameTimer();

    // Successfully opened default.mze!
    // Tiles are named either 'Sxx' or 'Fxx', where 'xx' is a number.
    // 'S' indicates it is a storage tile.
    // 'F' indicates it is a field tile.
    mouseListener = new PanelClickListener(this);

    JPanel leftSideBar = new JPanel(new GridBagLayout());
    leftSideBar.setBackground(Color.gray);
    leftSideBar.addMouseListener(backListener);
    JPanel rightSideBar = new JPanel(new GridBagLayout());
    rightSideBar.setBackground(Color.gray);
    rightSideBar.addMouseListener(backListener);

    GridBagConstraints tileStorageConstraint =
                                            generateSideBarInteriorConstraint();

    for (int index = 0; index < 8; index++) 
    {
      storagePanels[index] = new Tile(new GridBagLayout(), mouseListener);
      storagePanels[index].setName("S"+ Integer.toString(index));
      int location = Tile.getRandomPlace(index);
      storagePanels[index].setHome(location);
      storagePanels[index].setLineCoordinateList(
                                       reader.readFileCoordinateList(location));
      leftSideBar.add(storagePanels[index], tileStorageConstraint);
      tileStorageConstraint.gridy += 1;  
      storagePanels[index].rotate90(tileStartingRotations[index]);
    }
    tileStorageConstraint.gridy = 0;
    for (int index = 8; index < 16; index++) 
    {
      storagePanels[index] = new Tile(new GridBagLayout(), mouseListener);
      storagePanels[index].setName("S"+ Integer.toString(index));
      int location = Tile.getRandomPlace(index);
      storagePanels[index].setHome(location);
      storagePanels[index].setLineCoordinateList(
                                       reader.readFileCoordinateList(location));
      rightSideBar.add(storagePanels[index], tileStorageConstraint);
      tileStorageConstraint.gridy += 1;
      storagePanels[index].rotate90(tileStartingRotations[index]);
    }

    ////////////////////////////////////////////////////////////////////////

    // Generate the play area
    GridBagConstraints tileFieldConstraint = generateGameFieldConstraint(); 

    int x = 0;
    int y = 0;
    for (int index = 0; index < 16; index++) 
    {
      if (y > 2) 
      {
        y = 0;
        // Each side bar is 874px tall.
        // The center point then is 437px down from the top.
        // The game field is 400px tall, so
        // 400px / 2 = 200px to offset to
        // the center of the game field. 437px - 200px = 237px, this centers
        // the game field along the height of the side bars.
        tileFieldConstraint.insets = new Insets(237, 0, 0, 0);
        x++;
      } 
      else
      {
        y++;
        // We only want to adjust the external padding for the top
        // row of game field spaces, so everyone else has zero padding.
        tileFieldConstraint.insets = new Insets(0, 0, 0, 0);
      }
      if (x > 3) 
      {
        x = 0;
      }

      tileFieldConstraint.gridx = x + 2;
      tileFieldConstraint.gridy = y + 4;		
      fieldPanels[index] = new Tile(new GridBagLayout(), mouseListener);
      fieldPanels[index].setName("F" + Integer.toString(index));
      fieldPanels[index].setEmpty(true);
      fieldPanels[index].setBorder(BorderFactory.createLineBorder(Color.black));
      this.getContentPane().add(fieldPanels[index], tileFieldConstraint);
    }

    leftSideBar.setPreferredSize(new Dimension(200, 850));
    rightSideBar.setPreferredSize(new Dimension(200, 850));
    this.getContentPane().add(leftSideBar, generateLeftSideBarConstraint());
    this.getContentPane().add(rightSideBar, generateRightSideBarConstraint());

    fixFieldPlacement();

    if(reader.readFilePlacement(0) != -1)
    {
      setPlayedFileTiles();
    }

    return;
  }

  private GridBagConstraints generateGameFieldConstraint()
  {
    GridBagConstraints constraint = new GridBagConstraints();
    Insets centerInset = new Insets(0,0,0,0);
    constraint.insets = centerInset;
    constraint.fill = GridBagConstraints.BOTH;
    constraint.anchor = GridBagConstraints.CENTER;
    constraint.ipadx = 0;
    constraint.ipady = 0;
    return constraint;
  }

  private GridBagConstraints generateLeftSideBarConstraint()
  {
    GridBagConstraints constraint = new GridBagConstraints();
    Insets centerInset = new Insets(10,10,10,10);
    constraint.insets = centerInset;
    constraint.fill = GridBagConstraints.BOTH;
    constraint.anchor = GridBagConstraints.CENTER;
    constraint.ipadx = 0;
    constraint.ipady = 0;
    constraint.gridy = 1;
    constraint.gridx = 0;
    constraint.gridheight = 8;
    constraint.gridwidth = 1;
    return constraint;
  }

  private GridBagConstraints generateRightSideBarConstraint()
  {
    GridBagConstraints constraint = new GridBagConstraints();
    Insets centerInset = new Insets(10,10,10,10);
    constraint.insets = centerInset;
    constraint.fill = GridBagConstraints.BOTH;
    constraint.anchor = GridBagConstraints.CENTER;
    constraint.ipadx = 0;
    constraint.ipady = 0;
    constraint.gridy = 1;
    constraint.gridx = 6;
    constraint.gridheight = 8;
    constraint.gridwidth = 1;
    return constraint;
  }

  private GridBagConstraints generateSideBarInteriorConstraint()
  {
    GridBagConstraints constraint = new GridBagConstraints();
    Insets centerInset = new Insets(6,0,0,0);
    constraint.insets = centerInset;
    constraint.fill = GridBagConstraints.BOTH;
    constraint.anchor = GridBagConstraints.CENTER;
    constraint.ipadx = 0;
    constraint.ipady = 0;
    constraint.gridx = 0;
    constraint.gridy = 0;
    return constraint;
  }

  private int[] generateRotations()
  {
    int[] positionList = new int[16];
    boolean valid = false; // checks if a random assignment meets conditions

    // Can only stop generating lists of rotations when they meet req.
    while(!valid)
    {
      // Creating an array of 16 rotations
      int is_0_limit = 0; // tracks how many 0 rotations
      boolean contains1 = false; // at least 1
      boolean contains2 = false; // at least 1
      boolean contains3 = false; // at least 1

      // Generate list
      for(int i=0; i< 16; i++)
      {
        Random rand = new Random();
        int rand_int;

        // generates rotations from 1-3 if 4 have been given a 0 rotation
        if(is_0_limit == 4) 
        {
          rand_int = rand.nextInt(3);//0-2
          rand_int = rand_int+1; // shifts range from 0-2 to 1-3
        }
        else // generates rotations from 0-3
        {
          rand_int = rand.nextInt(4);//0-3
        }

        // constraint checking
        if(rand_int == 0)
        {
          is_0_limit++;
        }
        if(rand_int == 1)
        {
          contains1 = true;
        }
        if(rand_int == 2)
        {
          contains2 = true;
        }
        if(rand_int == 3)
        {
          contains3 = true;
        }
        positionList[i] = rand_int;
      }

      // testing if list generated met all of the constraints
      if( (is_0_limit > 0) && contains1 && contains2 && contains3)
      {
        valid = true; // met constraints now exiting
      }
    }
    return positionList;       
  }

  // GridBag scrambles the order of our tiles when it makes them visible.
  // This puts them in the right order in fieldPanels[].
  private void fixFieldPlacement()
  {
    Tile[] tmp = {fieldPanels[15], fieldPanels[3],  fieldPanels[7],
                  fieldPanels[11], fieldPanels[0],  fieldPanels[4],
                  fieldPanels[8],  fieldPanels[12], fieldPanels[1],
                  fieldPanels[5],  fieldPanels[9],  fieldPanels[13],
                  fieldPanels[2],  fieldPanels[6],  fieldPanels[10],
                  fieldPanels[14]
                 };

    fieldPanels = tmp;

    for(int i = 0; i < 16; i++)
    {
      fieldPanels[i].setName("F" + Integer.toString(i));
    }
  }

  //////////////////////////////////////////////////////////////////////////////
  // Methods needed to help with various popups/UI features:

  // This has package visibility so that GameWindowListener can close the window
  // Returns true if the game can be closed, false otherwise
  boolean quitWithChanges()
  {
    if(blankFileState)
    {
      return true;
    }
    gameChanged = this.checkForChanges();
    // See if the file has changed.
    // If it has, prompt the user to save or to close anyway.
    if(gameChanged && gameSaved == false)
    {
      String[] options = {"Save and Exit", "Exit Without Saving"};
      int choice = JOptionPane.showOptionDialog(this,
                                          "The current maze has been changed.\n"
                                                + "Still want to exit?",
                                                "Maze changed!",
                                                JOptionPane.YES_NO_OPTION,
                                                JOptionPane.QUESTION_MESSAGE,
                                                null,
                                                options,
                                                options[0]);
      if (choice == 1) return true;
      else if (choice == 0)
      {
        if (this.saveGame()) return true;
      }
    }
    else return true;
    return false;
  }

  private boolean checkForChanges()
  {
    // If the file is an original
    if(reader.readFilePlacement(0) == -1)
    {    
      for(int i = 0; i < 16; i++)
      {
        if(storagePanels[i].isEmpty())
        {
          return true;
        }
        // Check if the current maze piece number in each storage panel matches
        // its corresponding random initial maze piece number
        if(storagePanels[i].getHome() != Tile.getRandomPlace(i))
        {
          return true;
        }
        // Check if the current maze piece orientation matches its corresponding
        // random initial rotation
        if(storagePanels[i].getOrientation() != tileStartingRotations[i])
        {
          return true;
        }
      }
    }
    // If the file has been played
    else
    {
      int location = -1;
      for(int i = 0; i < 16; i++)
      {
        location = reader.readFilePlacement(i);
        if(location < 16)
        {
          if(storagePanels[location].isEmpty())
          {
            return true;
          }
          // Check if the current maze piece number in the storage panel matches
          // its file location
          if(storagePanels[location].getHome() != location)
          {
            return true;
          }
          // Check if the current maze piece orientation matches its
          // corresponding file load orientation
          if(storagePanels[location].getOrientation() != 
                                                     reader.readFileRotation(i))
          {
            return true;
          }
        }
        else
        {
          if(fieldPanels[location - 16].isEmpty())
          {
            return true;
          }
          // Check if the current maze piece number in the field panel matches
          // its file location
          if(fieldPanels[location - 16].getHome() != (location - 16))
          {
            return true;
          }
          // Check if the current maze piece orientation matches its
          // corresponding file load orientation
          if(fieldPanels[location - 16].getOrientation() != 
                                                     reader.readFileRotation(i))
          {
            return true;
          }
        }
      }
    }
    return false;
  }

  public void setGameAsChanged()
  {
    gameSaved = false;
  }

  private boolean checkWin()
  {
    boolean win = true;
    for(int index = 0; index < 16; index++)
    {
      if(fieldPanels[index].isEmpty())
      {
        win = false;
      }
      else
      {
        if(fieldPanels[index].getHome() != index)
        {
          win = false;
        }
      }
      if(fieldPanels[index].getOrientation() != 0)
      {
        win = false;
      }
    }
    return win;
  }

  public void winPopup()
  {
    // If the user has won the game...
    if(checkWin())
    {
      long hours = gameTimeSeconds / 60;
      long minutes = hours % 60;
      long seconds = gameTimeSeconds % 60;
      hours = hours / 60;
      
      String hoursText = 
          (hours<10)?"0"+Long.toString(hours):Long.toString(hours);
      String minutesText = 
          (minutes<10)?"0"+Long.toString(minutes):Long.toString(minutes);
      String secondsText = 
          (seconds<10)?"0"+Long.toString(seconds):Long.toString(seconds);
      
      this.stopGameTimer();
      JOptionPane.showMessageDialog(this,
                                    "You have won!\nTime: " + hoursText + ":" +
                                    minutesText + ":" + secondsText,
                                    "Congratulations!",
                                    JOptionPane.INFORMATION_MESSAGE);
    }
  }

  //////////////////////////////////////////////////////////////////////////////
  // Load Game Capability:

  // The boolean parameter determines how to handle the user
  // closing the file chooser. If true, then it will prompt the user
  // with the option to choose a new file (re-open the file chooser) or exit.
  // If false, then it will let the user close the file chooser and abort
  // the load.
  private void loadFile(boolean mustLoadFile)
  {
    boolean valid = false;
    while (valid == false)
    {
      try
      {
        JFileChooser loadGameChooser = new JFileChooser();
        File defaultFileLocation = new File("../game/input/");
        loadGameChooser.setCurrentDirectory(defaultFileLocation);
        loadGameChooser.setDialogTitle("Select an aMaze File to Load");
        loadGameChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        loadGameChooser.setApproveButtonText("Load");
        int userOption = loadGameChooser.showOpenDialog(this);
        if (userOption == JFileChooser.APPROVE_OPTION)
        {
          File desiredLocation = loadGameChooser.getSelectedFile();
          if (desiredLocation.exists() == false) 
            throw new FileNotFoundException(desiredLocation.getAbsolutePath());
          String p1 = desiredLocation.getAbsolutePath();
          reader = new MZEReader(p1, this);
          reader.close();
          valid = true;
        }
        else if (userOption == JFileChooser.CANCEL_OPTION && mustLoadFile)
        {
          valid = false;
          String[] options = {"Open New File", "Exit"};
          int choice = JOptionPane.showOptionDialog(this,
                                          "The selected maze file is invalid.\n"
                                                 + "Choose a new file or exit.",
                                                    "Maze invalid!",
                                                    JOptionPane.YES_NO_OPTION,
                                                    JOptionPane.ERROR_MESSAGE,
                                                    null,
                                                    options,
                                                    options[0]);
          if (choice == 1) System.exit(0);
        }
        else if (userOption == JFileChooser.CANCEL_OPTION 
            && mustLoadFile == false)
        {
          //System.out.println("Tried to cancel and dont need to load a file.");
          return;
        }
      }
      catch (InvalidMazeFileException e1)
      {
        JOptionPane.showMessageDialog(this, 
                              "File format is not correct!",
                                      "Maze file format invalid.",
                                      JOptionPane.ERROR_MESSAGE);
        if(!mustLoadFile)
        {
          setEmptyTiles();
          blankFileState = true;
          gameTimeSeconds = 0;
          main_nav.setVisibleTime(gameTimeSeconds);
          this.stopGameTimer();
          return;
        }
      }
      catch (FileNotFoundException e2)
      {
        JOptionPane.showMessageDialog(this, 
                                      "Unable to find the selected file"
                        + "!\n"+ "Click \'OK\' to choose a new file or cancel.",
                                      "File not found.",
                                      JOptionPane.ERROR_MESSAGE);
      }
      catch (Exception e3)
      {
        JOptionPane.showMessageDialog(this, 
                                      "The selected maze file is invalid!"
                                  + "!\n"+ "Click \'OK\' to choose a new file.",
                                      "Maze file format invalid.",
                                      JOptionPane.ERROR_MESSAGE);
      }
    }

    // If reading in an original file
    if(reader.readFilePlacement(0) == -1)
    {
      // Generate random placement and rotation
      Tile.shuffleRandomPlacement();
      generateRotations();
      // Load in the corresponding maze with the new random positions 
      // and new random rotations
      setOriginalFileTiles();
    }

    // If reading in a played game
    else
    {
      setPlayedFileTiles();
    }

    blankFileState = false;
    
    gameTimeSeconds = reader.readFileTime();
    main_nav.setVisibleTime(gameTimeSeconds);
    //this.startGameTimer();
  }

  private void setOriginalFileTiles()
  {
    // Set all the tile locations (storage and field) to be empty
    setEmptyTiles();
    
    for (int i = 0; i < 16; i++)
    {
      storagePanels[i].setEmpty(false);
      int location = Tile.getRandomPlace(i);
      storagePanels[i].setHome(location);
      storagePanels[i].setLineCoordinateList(
                                       reader.readFileCoordinateList(location));
      storagePanels[i].setOrientation(0);
      storagePanels[i].rotate90(tileStartingRotations[i]);
      storagePanels[i].setBorder(BorderFactory.createEmptyBorder());
      storagePanels[i].repaint();

      fieldPanels[i].setEmpty(true);
      fieldPanels[i].setLineCoordinateList(null);
      fieldPanels[i].setOrientation(0);
      fieldPanels[i].setBorder(BorderFactory.createLineBorder(Color.black));
      fieldPanels[i].repaint();
    }
  }

  private void setPlayedFileTiles()
  {
    // Set all the tile locations (storage and field) to be empty
    setEmptyTiles();

    // Fill in the locations that have tiles
    for(int i = 0; i < 16; i++)
    {
      int location = reader.readFilePlacement(i);
      if(location < 16)
      {
        storagePanels[location].setEmpty(false);
        storagePanels[location].setHome(location);
        storagePanels[location].setLineCoordinateList(
                                              reader.readFileCoordinateList(i));
        storagePanels[location].rotate90(reader.readFileRotation(i));
        
        storagePanels[location].setBorder(BorderFactory.createEmptyBorder());
        storagePanels[location].repaint();
      }
      else
      {
        fieldPanels[location - 16].setEmpty(false);
        fieldPanels[location - 16].setHome(location - 16);
        fieldPanels[location - 16].setLineCoordinateList(
                                              reader.readFileCoordinateList(i));
        fieldPanels[location - 16].rotate90(reader.readFileRotation(i));
        fieldPanels[location - 16].setBorder(BorderFactory.createEmptyBorder());
        fieldPanels[location - 16].repaint();
      }
    }
  }
  
  private void setEmptyTiles()
  {
    PanelClickListener.resetSelectedFirst();
    // Set all the tile locations (storage and field) to be empty
    for(int i = 0; i < 16; i++)
    {
      storagePanels[i].setEmpty(true);
      storagePanels[i].setLineCoordinateList(null);
      storagePanels[i].setOrientation(0);
      storagePanels[i].setHome(-1);
      storagePanels[i].setBorder(BorderFactory.createLineBorder(Color.black));
      storagePanels[i].repaint();

      fieldPanels[i].setEmpty(true);
      fieldPanels[i].setLineCoordinateList(null);
      fieldPanels[i].setOrientation(0);
      fieldPanels[i].setHome(-1);
      fieldPanels[i].setBorder(BorderFactory.createLineBorder(Color.black));
      fieldPanels[i].repaint();
    }
  }

  //////////////////////////////////////////////////////////////////////////////
  // Save Game Capability:
  // Returns true if the game was saved, otherwise false
  private boolean saveGame()
  {
    if (!blankFileState)
    {
      JFileChooser saveGameChooser = new JFileChooser();
      File defaultFileLocation = new File("../game/input/");
      saveGameChooser.setCurrentDirectory(defaultFileLocation);
      saveGameChooser.setDialogTitle("Select an aMaze File to Save");
      saveGameChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      saveGameChooser.setApproveButtonText("Save");
      int userOption = saveGameChooser.showSaveDialog(this);
      if (userOption == JFileChooser.APPROVE_OPTION)
      {
        // Save the file!
        File desiredFile = saveGameChooser.getSelectedFile();
        if (desiredFile.exists())
        {
          String[] options = {"Overwrite", "Cancel"};
          int choice = JOptionPane.showOptionDialog(this,
                                desiredFile.getName() + " already exists!\n"
                                                    + "Overwrite the file?",
                                                    "File Exists!",
                                                    JOptionPane.YES_NO_OPTION,
                                                    JOptionPane.ERROR_MESSAGE,
                                                    null,
                                                    options,
                                                    options[0]);
          if (choice == 1)
          {
            saveGame();
            return false;
          }
        }

        // Save the file!
        MZEWriter mainData = new MZEWriter();
        // Get the data about the maze
        // Storage panels
        int currentLocation;
        int originalLocation = 0 ;
        // Gathering information about the current game
        while(originalLocation < 16) // terminate once all 16 tiles found
        {    
         // Checking storage panels for current tile
            for(int index = 0; index < 16; index++)
            {
                // If current panel has the home we are looking for add it
                if(storagePanels[index].getHome() == originalLocation )
                {
                   currentLocation = index;
                   mainData.addData(storagePanels[index],currentLocation);
                }
            }
        // Checking field panels for current tile
            for(int index = 0; index < 16; index++)
            {
             // If current panel has the home we are looking for add it
                if(fieldPanels[index].getHome() == originalLocation )
                {
                   currentLocation = index +16; // accounts for interior
                   mainData.addData(fieldPanels[index],currentLocation);
                }
            }
            originalLocation++;// next iteration will look for the next tile
        }
        mainData.getTime(gameTimeSeconds);
        // Write all of the aggregated data
        mainData.writeToFile(desiredFile);

        /**
        JOptionPane.showMessageDialog(this, 
                                      "The game was saved!\n"+
                                      desiredFile.getAbsolutePath(),
                                      "Game Saved!",
                                      JOptionPane.PLAIN_MESSAGE);
        **/
        gameSaved = true;
        return true;
      }
      else if (userOption == JFileChooser.CANCEL_OPTION)
      {
        JOptionPane.showMessageDialog(this, 
                                      "Save Aborted.",
                                      "Game Save Aborted",
                                      JOptionPane.WARNING_MESSAGE);
        return false;
      }
    } 
    else
    {
      // The maze is blank. We can't save.
      JOptionPane.showMessageDialog(this, 
                                    "Nothing to Save!",
                                    "Nothing to Save!",
                                    JOptionPane.WARNING_MESSAGE);   
      return false;
    }

    // Default return value
    return false;
  }
  
  public void incTimeSeconds()
  {
    gameTimeSeconds++;
    main_nav.setVisibleTime(gameTimeSeconds);
  }
  
  public long setTimeSeconds(long newTime)
  {
    long oldTime = gameTimeSeconds;
    gameTimeSeconds = newTime;
    main_nav.setVisibleTime(gameTimeSeconds);
    return oldTime;
  }
  
  public long getTimeSeconds()
  {
    return this.gameTimeSeconds;
  }
  
  // these two methods improve readability.
  private void stopGameTimer()
  {
    this.timerRunning = false; 
  }
  
  private void startGameTimer()
  {
    this.timerRunning = true;
  }
  
  public boolean isGameRunning()
  {
    return this.timerRunning;
  }

};
