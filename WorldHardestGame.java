/**The "World's Hardest Game" class. A game where the objective is to navigate to the exit.
 * @author Jason Wang & Francis Peng
 * @version January 21, 2013
 */

import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class WorldHardestGame extends JFrame implements ActionListener
{

    // Program variables for the Menu items and the game board
    private JMenuItem newOption, exitOption, rulesMenuItem, aboutMenuItem;
    private GridArea gameBoard;

    public WorldHardestGame ()
    {
	// Set up the frame and the grid
	super ("World's Hardest Game");
	setResizable (false);

	// Sets up a grid for the character to move around in
	gameBoard = new GridArea ();
	getContentPane ().add (gameBoard, BorderLayout.CENTER);

	// Centre the frame in the middle (almost) of the screen
	Dimension screen = Toolkit.getDefaultToolkit ().getScreenSize ();
	setLocation ((screen.width - gameBoard.BOARD_SIZE.width) / 2,
		(screen.height - gameBoard.BOARD_SIZE.height) / 2 - 50);


	// Adds the menu and menu items to the frame
	// Set up the Game MenuItems
	newOption = new JMenuItem ("Restart Level");
	newOption.setAccelerator (KeyStroke.getKeyStroke (KeyEvent.VK_N,
		    InputEvent.CTRL_MASK));
	newOption.addActionListener (this);

	exitOption = new JMenuItem ("Exit");
	exitOption.setAccelerator (KeyStroke.getKeyStroke (KeyEvent.VK_X,
		    InputEvent.CTRL_MASK));
	exitOption.addActionListener (this);

	// Set up the Help Menu
	JMenu helpMenu = new JMenu ("Help");
	rulesMenuItem = new JMenuItem ("Rules...");
	rulesMenuItem.addActionListener (this);
	helpMenu.add (rulesMenuItem);
	aboutMenuItem = new JMenuItem ("About...");
	aboutMenuItem.addActionListener (this);
	helpMenu.add (aboutMenuItem);

	// Add each MenuItem to the Game Menu (with a separator)
	JMenu gameMenu = new JMenu ("Game");
	gameMenu.add (newOption);
	gameMenu.addSeparator ();
	gameMenu.add (exitOption);
	JMenuBar mainMenu = new JMenuBar ();
	mainMenu.add (gameMenu);
	mainMenu.add (helpMenu);

	// Set the menu bar for this frame to mainMenu
	setJMenuBar (mainMenu);
    } // Constructor


    /**
      * Responds to a Menu Event.
      * @param event the event that triggered this method
      */
    public void actionPerformed (ActionEvent event)
    {
	if (event.getSource () == newOption) // Selected "New"
	{
	    gameBoard.newGame (GridArea.GAMELIST [gameBoard.gameLevel]);
	    repaint ();
	}
	else if (event.getSource () == exitOption)  // Selected "Exit"
	{
	    hide ();
	    System.exit (0);
	}
	else if (event.getSource () == rulesMenuItem)  // Selected "Rules"
	{
	    JOptionPane
		.showMessageDialog (
		    this,
		    "This game is a tiled based 2D platformer game."
		    + "\nThe objective of our game is to move the character to"
		    + "\nthe exit block, depicted by the block with the letter E."
		    + "\nThe controls are the arrow keys, where the up-direction"
		    + "\n key prompts the character to jump up two blocks and across one."
		    + "\n There are many types of special blocks, each with its own functions"
		    + "\n that change the character's movement in the world."
		    + "\n The block with a up arrow makes the character jump up 4 blocks and"
		    + "\n across one. Some blocks are invisible and others take you back to"
		    + "\n the beginning."
		    + "\n Avoid being returned to the start, and experiment with the game."
		    + "\n\nGood Luck!", "Rules",
		    JOptionPane.INFORMATION_MESSAGE);
	}
	else if (event.getSource () == aboutMenuItem)  // Selected "About"
	{
	    JOptionPane.showMessageDialog (this, "By Jason Wang & Francis Peng"
		    + "\n\u00a9 2013", "Credits",
		    JOptionPane.INFORMATION_MESSAGE);
	}
    }


    // Sets up the main frame for the Game
    public static void main (String[] args)
    {
	WorldHardestGame frame = new WorldHardestGame ();
	frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
	frame.pack ();
	frame.setVisible (true);
    } // main method
} // WorldHardestGame class
