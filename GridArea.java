import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

// Inner class for the grid area
public class GridArea extends JPanel
{
    private final int IMAGE_WIDTH;
    private final int IMAGE_HEIGHT;

    private Image[] gridImages;
    private Image playerImage;
    private Image leftPlayerImage;
    private Image rightPlayerImage;

    // Variables to keep track of the grid, the player position, game levels and gravity speed.
    private int[] [] grid;
    private int currentRow;
    private int currentColumn;
    // Array of levels
    public static String[] GAMELIST = {"Level-1.txt", "Level-2.txt", "Level-3.txt", "Level-4.txt", "Level-5.txt", "Level-6.txt", "Level-7.txt", "Level-8.txt", "Level-9.txt", "Level-10.txt"};
    public int gameLevel = 0;
    public static int MAX_GAME_LEVEL = 10;
    public Dimension BOARD_SIZE = null;
    private final int SQUARE_SIZE = 32;
    private final int DROPPING_SPEED = 5;
    private final boolean ANIMATION_ON = true;
    private boolean droppingPiece;
    private int xFallingPiece, yFallingPiece;

    /**
      * Constructs a new GridArea object
      */
    public GridArea ()
    {

	initiateGridArea ();

	// Starts a new game and loads up the grid (sets size of grid array)
	newGame (GAMELIST [gameLevel]);

	// Set the image height and width based on the path image size
	// Aslo sizes this panel based on the image and grid size
	IMAGE_WIDTH = gridImages [0].getWidth (this);
	IMAGE_HEIGHT = gridImages [0].getHeight (this);
	BOARD_SIZE = new Dimension (grid [0].length * IMAGE_WIDTH,
		grid.length * IMAGE_HEIGHT);
	this.setPreferredSize (BOARD_SIZE);

	// Sets up for keyboard input (arrow keys) on this panel
	this.setFocusable (true);
	this.addKeyListener (new KeyHandler ());
	this.requestFocusInWindow ();
    }


    public void initiateGridArea ()
    {
	// Create an array for the gridImages and load them up
	// Also load up the player image
	gridImages = new Image [7];

	gridImages [0] = new ImageIcon ("bgBlock.png").getImage (); // The background block, coloured white. Players may freely move through them.
	gridImages [1] = new ImageIcon ("blackBlock.png").getImage (); // The walls of the map, coloured black. Players cannot pass through them.
	gridImages [2] = new ImageIcon ("invisibleBlock.png").getImage (); // The invisible blocks, coloured black changes to white. Players activate them by moving on them.
	gridImages [3] = new ImageIcon ("bgBlock.png").getImage (); // The block where the player starts, coloured white.
	gridImages [4] = new ImageIcon ("jumpBlock.png").getImage (); // The jump block, black block with an up arrow. Players are moved up 4 blocks and across 1.
	gridImages [5] = new ImageIcon ("exit.gif").getImage (); // The block with the letter 'E' on it. The player must reach there to proceed to the next level.
	gridImages [6] = new ImageIcon ("skullBlock.png").getImage (); // The block with a skull on it. Sends the player back to the start block.
	playerImage = new ImageIcon ("frontBall.png").getImage (); // How the character looks at the initial start of the game.
	leftPlayerImage = new ImageIcon ("leftBall.png").getImage (); // How the player looks when they face left.
	rightPlayerImage = new ImageIcon ("rightBall.png").getImage (); // How the player looks when they face right.

    }


    /**Finds the inital position of where the player is suppose to go.
      *
      */
    public void setInitialPosition ()
    {
	// Initial position of the character
	// Loop through the world to find the starting point, and place the
	// character there
	for (int row = 0 ; row < grid.length ; row++)
	    for (int column = 0 ; column < grid [0].length ; column++)
		if (grid [row] [column] == 3)
		{
		    currentRow = row;
		    currentColumn = column;
		}
    }


    /**Repaint the drawing panel
      * @param g
      * The Graphics context
      */
    public void paintComponent (Graphics g)
    {
	super.paintComponent (g);

	// Redraw the grid with current images
	for (int row = 0 ; row < grid.length ; row++)
	    for (int column = 0 ; column < grid [0].length ; column++)
	    {
		// Put a path underneath everywhere
		g.drawImage (gridImages [0], column * IMAGE_WIDTH, row
			* IMAGE_HEIGHT, this);
		int imageNo = grid [row] [column];
		g.drawImage (gridImages [imageNo], column * IMAGE_WIDTH, row
			* IMAGE_HEIGHT, this);
	    }


	if (droppingPiece)
	    g.drawImage (playerImage, xFallingPiece, yFallingPiece, this);
	else
	    g.drawImage (playerImage, currentColumn * IMAGE_WIDTH, currentRow * IMAGE_HEIGHT, this);


    } // paint component method


    /** Loads up the file for the levels
      *
      */

    public void newGame (String mazeFileName)
    {
	// Load up the file for the maze (try catch, is for file io errors)
	try
	{
	    // Find the size of the file first to size the array
	    // Standard Java file input (better than hsa.TextInputFile)
	    BufferedReader mazeFile = new BufferedReader (new FileReader (
			mazeFileName));

	    // Assume file has at least 1 line
	    int noOfRows = 1;
	    String rowStr = mazeFile.readLine ();
	    int noOfColumns = rowStr.length ();

	    // Read and count the rest of rows until the end of the file
	    String line;
	    while ((line = mazeFile.readLine ()) != null)
	    {
		noOfRows++;
	    }
	    mazeFile.close ();

	    // Set up the array
	    grid = new int [noOfRows] [noOfColumns];

	    // Load in the file data into the grid
	    // translate each letter into an integer
	    mazeFile = new BufferedReader (new FileReader (mazeFileName));
	    for (int row = 0 ; row < grid.length ; row++)
	    {
		rowStr = mazeFile.readLine ();
		for (int column = 0 ; column < grid [0].length ; column++)
		{
		    grid [row] [column] = (int) (rowStr.charAt (column) - '0');
		}
	    }
	    setInitialPosition ();
	    mazeFile.close ();
	}
	catch (IOException e)
	{
	    JOptionPane.showMessageDialog (this, mazeFileName
		    + " not a valid maze file", "Message - Invalid Maze File",
		    JOptionPane.WARNING_MESSAGE);
	    System.exit (0);
	}
    }


    // Inner class to handle key events when the user presses an arrow key.
    private class KeyHandler extends KeyAdapter
    {

	public void keyPressed (KeyEvent event)
	{

	    int initRow = currentRow;
	    int initColumn = currentColumn;

	    // Change the currentRow and currentColumn of the player based on the key pressed
	    if (event.getKeyCode () == KeyEvent.VK_LEFT)
	    {
		// move left or drop
		if (playerImage == leftPlayerImage)

		    // if the player is currently facing left, then proceed to check for different types of blocks.
		    {

			// Checks for exit left
			if (isExit (currentRow, currentColumn - 1))
			    takeExit (currentRow, currentColumn - 1);

			// Checks for the return block on the left.
			// Also known as the death block to send the player back to the start block
			else if (isDeathBlock (currentRow, currentColumn - 1))
			    newGame (GAMELIST [gameLevel]);

			// Moves normally if there is nothing in its way. Checks if the player falls by incorporating gravity.
			else if (isPath (currentRow, currentColumn - 1))
			{
			    currentColumn--;
			    gravity ();
			}

			// Checks for invisible block and reacts accordingly. Checks for the invisible block to the left
			else if (isInvisible (currentRow, currentColumn - 1))
			{
			    deleteBlock (currentRow, currentColumn - 1);
			    gravity ();

			    // If there are multiple invisible blocks consecutively below the player
			    while (isInvisible (currentRow + 1, currentColumn))
			    {
				deleteBlock (currentRow + 1, currentColumn);
				gravity ();
			    }

			}

			// Checks for jump block below the player's current position
			else if (grid [currentRow + 1] [currentColumn] == 4)
			{
			    jumpBlock (currentRow, currentColumn - 1);
			}
			gravity ();

			// Checks for invisible block below the player's current position
			if (isInvisible (currentRow + 1, currentColumn))
			{
			    deleteBlock (currentRow + 1, currentColumn);
			    gravity ();
			    repaint ();

			    // If there are multiple invisible blocks consecutively below the player
			    while (isInvisible (currentRow + 1, currentColumn))
			    {
				deleteBlock (currentRow + 1, currentColumn);
				gravity ();
			    }

			}
			// Checks for the exit below the player's current position
			if (isExit (currentRow + 1, currentColumn))
			    takeExit (currentRow + 1, currentColumn);
			if (isDeathBlock (currentRow + 1, currentColumn))
			    newGame (GAMELIST [gameLevel]);
		    }
		else
		    // If the player isn't currently facing left, then change to face left
		    playerImage = leftPlayerImage;

	    }

	    // Goes through the possible scenarios for if the character moves right
	    if (event.getKeyCode () == KeyEvent.VK_RIGHT)
	    {

		// If the player is currently facing right, then proceed
		if (playerImage == rightPlayerImage)
		{

		    // Checks for the exit below
		    if (isExit (currentRow, currentColumn + 1))
			takeExit (currentRow, currentColumn + 1);

		    // Checks for a death block below
		    else if (isDeathBlock (currentRow, currentColumn + 1))
			newGame (GAMELIST [gameLevel]);

		    // Checks for a path below that the player can take, gravity is applied
		    else if (isPath (currentRow, currentColumn + 1))
		    {
			currentColumn++;
			gravity ();
		    }

		    // Checks for invisible blocks to the side
		    else if (isInvisible (currentRow, currentColumn + 1))
		    {
			deleteBlock (currentRow, currentColumn + 1);
			gravity ();

			while (isInvisible (currentRow + 1, currentColumn))
			{
			    deleteBlock (currentRow + 1, currentColumn);
			    gravity ();
			}

		    }
		    // Checks for jump blocks
		    else if (grid [currentRow + 1] [currentColumn] == 4)
		    {
			jumpBlock (currentRow, currentColumn + 1);
		    }
		    gravity ();

		    // Checks for invisible blocks below
		    if (isInvisible (currentRow + 1, currentColumn))
		    {
			deleteBlock (currentRow + 1, currentColumn);
			gravity ();

			while (isInvisible (currentRow + 1, currentColumn))
			{
			    deleteBlock (currentRow + 1, currentColumn);
			    gravity ();
			}

		    }
		    // Death Block returns you to the beginning
		    if (isExit (currentRow + 1, currentColumn))
			takeExit (currentRow + 1, currentColumn);
		    if (isDeathBlock (currentRow + 1, currentColumn))
			newGame (GAMELIST [gameLevel]);

		}
		else
		    // If the player isn't currently facing right, then change to face right
		    playerImage = rightPlayerImage;


	    }
	    // Up arrow button
	    if (event.getKeyCode () == KeyEvent.VK_UP)
	    {
		// Dummy temporary variable for the jump physics going either left or right
		int tempRow = currentRow;
		int tempColumn = currentColumn;
		if (playerImage.equals (rightPlayerImage))
		    tempColumn++;
		else
		    tempColumn--;

		// Jumps for exit if possible
		if (isExit (currentRow - 1, currentColumn))
		    takeExit (currentRow - 1, currentColumn);
		// Jumps for death block
		else if (isDeathBlock (currentRow - 1, currentColumn))
		    newGame (GAMELIST [gameLevel]);
		// Jumps for invisible block
		else if (isInvisible (currentRow - 1, currentColumn))
		{
		    deleteBlock (currentRow - 1, currentColumn);
		    gravity ();
		    repaint ();

		    while (isInvisible (currentRow + 1, currentColumn))
		    {
			deleteBlock (currentRow + 1, currentColumn);
			gravity ();
			repaint ();
		    }

		}
		// check nothing is blocked from above before jump
		else if (isPath (tempRow - 1, currentColumn)
			&& isPath (tempRow - 2, tempColumn))
		{
		    currentRow = tempRow - 2;
		    currentColumn = tempColumn;
		}
		gravity ();
		// Jumping on another jump block
		if (grid [currentRow + 1] [currentColumn] == 4)
		{
		    jumpBlock (currentRow, currentColumn + 1);
		}

		// Checks for invisible block underneath
		if (isInvisible (currentRow + 1, currentColumn))
		{
		    deleteBlock (currentRow + 1, currentColumn);
		    gravity ();

		    repaint ();
		    while (isInvisible (currentRow + 1, currentColumn))
		    {
			deleteBlock (currentRow + 1, currentColumn);
			gravity ();
			repaint ();
		    }

		}
		// Take the exit if possible
		if (isExit (currentRow + 1, currentColumn))
		    takeExit (currentRow + 1, currentColumn);
		// Returns to beginning if you land on a death block
		if (isDeathBlock (currentRow + 1, currentColumn))
		    newGame (GAMELIST [gameLevel]);
	    }
	    
	    // Moves down, for glitches
	    if (event.getKeyCode () == KeyEvent.VK_DOWN)
	    {
		if (isPath (currentRow + 1, currentColumn))
		    currentRow++;

	    }

	    // Repaint the screen after the change
	    animatePiece (initRow, currentRow);
	    repaint ();
	}
    }


    /**Determines if the position is a background block
     * @param row
     * @param column
     * @return boolean if position holds a path return true else return false
     */
    private boolean isPath (int row, int column)
    {
	if (isValid (row, column) && grid [row] [column] == 0)
	    return true;
	else
	    return false;
    }


    /**Determines if the position is a black block
     * @param row
     * @param column
     * @return boolean if position is a black block return true else return false
     */
    private boolean isBrick (int row, int column)
    {
	if (isValid (row, column) && grid [row] [column] == 1)
	    return true;
	else
	    return false;
    }


    /**Determines if the position is an invisible block
    * @param row
    * @param column
    * @return boolean if the position is an invisible block return true else return false
    */
    private boolean isInvisible (int row, int column)
    {

	if (isValid (row, column) && (grid [row] [column] == 2 || grid [row] [column] == 3))
	    return true;
	else
	    return false;
    }


    /**Makes the block invisible
     * @param row
     * @param column
     */
    private void deleteBlock (int row, int column)
    {
	currentRow = row;
	currentColumn = column;
	grid [currentRow] [currentColumn] = 0;
    }


    /**Makes the player drop, also known as gravity
     *
     */
    private void gravity ()
    {
	while (grid [currentRow + 1] [currentColumn] == 0)
	{
	    currentRow++;
	}
    }


    /**Block that makes the player jump 2 extra blocks vertically
     * @param row
     * @param column
     */
    private void jumpBlock (int row, int column)
    {
	if (playerImage.equals (rightPlayerImage))
	{
	    currentColumn++;
	    currentRow -= 3;
	}
	else if (playerImage.equals (leftPlayerImage))
	{
	    currentColumn--;
	    currentRow -= 3;
	}
    }


    /**Checks if the current position is an exit
     * @param row
     * @param column
     * @return boolean return true if there is an exit else return false
     */
    private boolean isExit (int row, int column)
    {

	if (isValid (row, column) && grid [row] [column] == 5)
	    return true;
	else
	    return false;
    }


    /**Checks to see if the position of the character is within the grid
     * @param row
     * @param column
     * @return boolean true if the player is within the boundaries else return fales
     */
    private boolean isValid (int row, int column)
    {
	if (0 <= row && row < grid.length && 0 <= column && column < grid [0].length)
	    return true;
	else
	    return false;
    }


    /**Checks if the current position is an death block
     * @param row
     * @param column
     * @return boolean if position holds a death block return true else return false
     */
    private boolean isDeathBlock (int row, int column)
    {
	if (isValid (row, column) && grid [row] [column] == 6)
	    return true;
	else
	    return false;
    }


    /**Moves the player to the next level
     * @param row
     * @param column
     */
    private void takeExit (int row, int column)
    {
	currentRow = row;
	currentColumn = column;
	repaint ();
	gameLevel++;
	if (gameLevel == MAX_GAME_LEVEL)
	{

	    // Finish all levels. Game over. Displays the ending messages depending on what the user chooses.
	    int reply = JOptionPane.showConfirmDialog (null, "You have completed all levels in this map pack! Would you like more challenges?", "Congratulations!",
		    JOptionPane.YES_NO_OPTION);
	    if (reply == JOptionPane.YES_OPTION)
	    {
		JOptionPane.showMessageDialog (null, "Thank you for playing the World's Hardest Game. Stay tuned for World's Hardest Game 2.0!", "Game Over",
			JOptionPane.INFORMATION_MESSAGE);
		System.exit (0);
	    }
	    else
	    {
		JOptionPane.showMessageDialog (null, "Hope you had fun!");
		System.exit (0);
	    }

	}
	else
	{
	    JOptionPane.showMessageDialog (this, "Well Done!", "Get ready for the next level.",
		    JOptionPane.INFORMATION_MESSAGE);
	    newGame (GAMELIST [gameLevel]);
	}
    }


    /**Animates the player dropping
     * @param initRow the initial position of the player
     * @param finalRow where the player falling will end up
     */
    private void animatePiece (int initRow, int finalRow)
    {
	droppingPiece = true;
	for (double row = initRow ; row < finalRow ; row += 0.20)
	{
	    // Find the x and y positions for the falling piece
	    xFallingPiece = currentColumn * SQUARE_SIZE;
	    yFallingPiece = (int) (row * SQUARE_SIZE);

	    // Update the drawing area
	    paintImmediately (0, 0, getWidth (), getHeight ());
	    delay (DROPPING_SPEED);
	}
	droppingPiece = false;
    }


    /**Delays the given a number of milliseconds
     * @param milliSec the number of milliseconds to delay
     */
    private void delay (int milliSec)
    {
	try
	{
	    Thread.sleep (milliSec);
	}
	catch (InterruptedException e)
	{
	}
    }
}
