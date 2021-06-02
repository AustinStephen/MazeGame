/**
 * Nav_Bar.java
 * @author Group J (Ryan Harding, Michael Pate, Adeline Reichert, Austin
 *                  Stephen, and Ben Wilkin)
 * Date: Apr 30, 2021
 * Purpose: Manages the navigation bar.
 *          Constructor draws the navigation bar using the GridBag layout 
 *          manager. All of the methods manage the elements in the Navigation
 *          Bar.
*/

import java.awt.*;
import javax.swing.*;

public class Nav_Bar 
{
  
  private JLabel timeLabel;
  
  public Nav_Bar(GameWindow window)
  {
    JPanel buttonBar = new JPanel(new GridBagLayout());
    buttonBar.addMouseListener(window.getMouseListeners()[0]);

    GridBagConstraints buttonConstraint = new GridBagConstraints();
    buttonConstraint.fill = GridBagConstraints.HORIZONTAL;
    buttonConstraint.weightx = 0.0;
    buttonConstraint.weighty = 1.0;
    buttonConstraint.ipadx = 0;
    buttonConstraint.ipady = 0;
    buttonConstraint.anchor = GridBagConstraints.CENTER;
    
    buttonConstraint.insets = new Insets(10, 20, 10, 10);
    timeLabel = new JLabel("00:00:00");
    buttonConstraint.gridx = 1;
    buttonConstraint.gridy = 0;
    buttonBar.add(timeLabel, buttonConstraint);
    buttonConstraint.insets = new Insets(10, 10, 10, 10);

    // Then we add the buttons using the constraint.
    buttonConstraint.gridx = 2;
    buttonConstraint.gridy = 1;
    JButton quitButton = new JButton("Quit");
    buttonBar.add(quitButton, buttonConstraint);
    quitButton.addActionListener(window);

    buttonConstraint.gridx = 0;
    buttonConstraint.gridy = 1;
    JButton fileButton = new JButton("File");
    buttonBar.add(fileButton, buttonConstraint);
    fileButton.addActionListener(window);
    buttonBar.add(fileButton, buttonConstraint);

    buttonConstraint.gridx = 1;
    buttonConstraint.gridy = 1;
    JButton resetButton = new JButton("Reset");
    buttonBar.add(resetButton, buttonConstraint);
    resetButton.addActionListener(window);

    // This constraint determines where the buttonBar panel will go on the UI
    GridBagConstraints buttonBarConstraint = new GridBagConstraints();
    buttonBarConstraint.insets = new Insets(10, 0, 10, 1);
    buttonBarConstraint.fill = GridBagConstraints.HORIZONTAL;
    buttonBarConstraint.gridwidth = 8;
    buttonBarConstraint.ipadx = 0;
    buttonBarConstraint.ipady = 0;
    buttonBarConstraint.gridx = 0;
    buttonBarConstraint.gridy = 0;
    buttonBarConstraint.anchor = GridBagConstraints.CENTER;

    window.getContentPane().add(buttonBar, buttonBarConstraint);
    window.setVisible(true);

  }
  
  public void setVisibleTime(long inputSeconds)
  {
    long hours = inputSeconds / 60;
    long seconds = inputSeconds % 60;
    long minutes = hours % 60;
    hours = hours / 60;

    String hoursText = 
        (hours<10)?"0"+Long.toString(hours):Long.toString(hours);
    String minutesText = 
        (minutes<10)?"0"+Long.toString(minutes):Long.toString(minutes);
    String secondsText = 
        (seconds<10)?"0"+Long.toString(seconds):Long.toString(seconds);
    this.timeLabel.setText(hoursText + ":" + minutesText + ":" + secondsText);
  }

  //
  // ALL FUTURE NAV BAR METHODS GO HERE
  //

};
