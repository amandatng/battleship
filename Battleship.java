import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class Battleship {
	
	private static JFrame frame = new JFrame("Battleship GUI");
	private static GridBagConstraints c = new GridBagConstraints();
	private static JButton[][] playerButtons;
	private static JButton[][] cpuButtons;
	private static char[][] playerBoard = new char[10][10];
	private static char[][] cpuBoard = new char[10][10];
	private static JLabel playerMessages;
	private static JLabel cpuMessages;
	private static int[] hits = {0, 0};
	private static boolean startGame = false;
	
	// Reads a file called fileName and loads a two-dimensional array called board with the file's contents
	public static void loadFile(char[][] board, String fileName) throws IOException {
		BufferedReader inputStream = null;

		try {
			int row = 0;
			inputStream = new BufferedReader(new FileReader(fileName));
			// Reads the first line of data in the file
			String lineRead = inputStream.readLine();
			while (lineRead != null) {
				for (int col = 0; col < lineRead.length(); col += 2) {
					board[row][col / 2] = lineRead.charAt(col);
				}
				row++;
				// Reads the next line of data in the file
				lineRead = inputStream.readLine();
			}
		}
		catch (FileNotFoundException exception) {
			System.out.println("Error opening file");
		}
		finally {		
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}
	
	public static String whichShip(char letter) {
		switch (letter) {
			case 'C':
				return "Aircraft Carrier!";
			case 'B':
				return "Battleship!";
			case 'S':
				return "Submarine!";
			case 'D':
				return "Destroyer!";
			case 'P':
				return "Patrol Boat!";
		}
		return "";
	}
	
	public static String shipSank(char[][] board, char whichShipSank, String playerOrCpu) { // Displays ship sank message
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[row].length; col++) {
				if (board[row][col] == whichShipSank) { // Checks to see if the ship that was hit still exists on the board
					 whichShipSank = ' ';
				}
			}
		}
		
		if (whichShipSank != ' ') { // If the ship no longer exists on the board, display a message
			return playerOrCpu + whichShip(whichShipSank);
		}
		return ""; 
	}
	
	public static void setConstraints(int top, int left, int bottom, int right, int width, int height, int fill, int x, int y) { // Sets constraints for GridBagLayout
		c.insets = new Insets(top, left, bottom, right);
		c.gridwidth = width;
		c.gridheight = height;
		c.fill = fill;
		c.gridx = x;
		c.gridy = y;
	}
	
	public static void main(String[] args) throws IOException {
		JPanel panel = new JPanel(); // Multiple panels are needed for borders
		JPanel topPanel = new JPanel(new GridBagLayout());
		JPanel grid = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		JPanel playerGrid = new JPanel(new GridBagLayout());
		JPanel cpuGrid = new JPanel(new GridBagLayout());
		JPanel messages = new JPanel(new GridBagLayout());
		JLabel playerCols = new JLabel(new ImageIcon("cols.png"));
		JLabel cpuCols = new JLabel(new ImageIcon("cols.png"));
		JLabel playerRows = new JLabel(new ImageIcon("rows.png"));
		JLabel cpuRows = new JLabel(new ImageIcon("rows.png"));
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem open = new JMenuItem("Open");
		JMenuItem restartGame = new JMenuItem("Restart Game");
		JMenuItem exit = new JMenuItem("Exit");
		playerButtons = new JButton[10][10];
		cpuButtons = new JButton[10][10];
		playerMessages = new JLabel("Please open the PLAYER.txt file!");
		cpuMessages = new JLabel("Please open the CPU.txt file!");
		Buttons buttonClicked = new Buttons();
		
		// FILE OPENER MENU
		menuBar.add(menu); 
		open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		open.addActionListener(new OpenFileChooser());
		menu.add(open);
		restartGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
		restartGame.addActionListener(new RestartGame());
		menu.add(restartGame);
		menu.addSeparator();
		exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		exit.addActionListener(new Exit());
		menu.add(exit);
		
		// TOP PANEL
		setConstraints(0, 0, 0, 0, 2, 1, GridBagConstraints.HORIZONTAL, 0, 0);
		topPanel.add(menuBar, c);
		setConstraints(0, 76, 5, 0, 1, 1, GridBagConstraints.NONE, 0, 1);
		topPanel.add(playerCols, c);
		setConstraints(0, 82, 5, 16, 1, 1, GridBagConstraints.NONE, 1, 1);
		topPanel.add(cpuCols, c);
		
		// PLAYER GRID PANEL
		playerGrid.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		setConstraints(0, 0, 0, 10, 1, 10, GridBagConstraints.NONE, 0, 0);
		playerGrid.add(playerRows, c);
		setConstraints(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE, 0, 0);
		
		for (int row = 0; row < 10; row++) {
			c.gridy = row;
			for (int col = 0; col < 10; col++) {
				c.gridx = col + 1;
				playerButtons[row][col] = new JButton(); // Creates a button
				playerButtons[row][col].setPreferredSize(new Dimension(50, 50)); // Sets button size
				playerButtons[row][col].setForeground(Color.BLUE); // Sets text colour
				playerGrid.add(playerButtons[row][col], c); // Adds button to grid
			}
		}
		
		// CPU GRID PANEL
		for (int row = 0; row < 10; row++) {
			c.gridy = row;
			for (int col = 0; col < 10; col++) {
				c.gridx = col + 1;
				cpuButtons[row][col] = new JButton();
				cpuButtons[row][col].setPreferredSize(new Dimension(50, 50));
				cpuButtons[row][col].setForeground(Color.RED);
				cpuButtons[row][col].addActionListener(buttonClicked); // Using a class object instead of creating 100 ActionListener objects
				cpuGrid.add(cpuButtons[row][col], c);
			}
		}
		
		cpuGrid.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		setConstraints(0, 0, 0, 10, 1, 10, GridBagConstraints.NONE, 0, 0);
		cpuGrid.add(cpuRows, c);
		
		// GRID PANEL
		grid.add(playerGrid);
		grid.add(cpuGrid);
		
		// MESSAGES PANEL
		messages.setMaximumSize(new Dimension(1164, messages.getMinimumSize().height));
		messages.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK), BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		playerMessages.setPreferredSize(new Dimension(1160, 50));
		playerMessages.setFont(new Font("Arial", Font.PLAIN, 12));
		playerMessages.setForeground(Color.BLUE);
		cpuMessages.setPreferredSize(new Dimension(1160, 50));
		cpuMessages.setFont(new Font("Arial", Font.PLAIN, 12));
		cpuMessages.setForeground(Color.RED);		
		
		playerMessages.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Player Messages", TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION, new Font("Arial", Font.PLAIN, 10), Color.BLUE));
		cpuMessages.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "CPU Messages", TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION, new Font("Arial", Font.PLAIN, 10), Color.RED));
		
		setConstraints(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE, 0, 0);
		messages.add(playerMessages, c);
		setConstraints(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE, 0, 1);
		messages.add(cpuMessages, c);
		
		// MAIN PANEL
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
		panel.add(topPanel);
		panel.add(grid);
		panel.add(messages);
		
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(panel);
		frame.pack();
	}
	
	private static class RestartGame implements ActionListener {
		
		public void actionPerformed(ActionEvent event) { // Reinitializes all variabels and JComponents
			startGame = false; // Prevents buttons from being clicked by stopping the ActionListener
			hits[0] = 0;
			hits[1] = 0;
			playerMessages.setText("Please open the PLAYER.txt file!");
			cpuMessages.setText("Please open the CPU.txt file!");
			
			for (int row = 0; row < 10; row++) {
				for (int col = 0; col < 10; col++) {
					playerButtons[row][col].setEnabled(true);
					cpuButtons[row][col].setEnabled(true);
					playerButtons[row][col].setText("");
					cpuButtons[row][col].setText("");
					playerButtons[row][col].setIcon(null);
					cpuButtons[row][col].setIcon(null);
				}
			}
		}
	}
	
	private static class Exit implements ActionListener {
		
		public void actionPerformed(ActionEvent event) {
			System.exit(0); // Exits program
		}
	}
	
	private static class Buttons implements ActionListener {
		
		public void actionPerformed(ActionEvent event) {
			if (startGame) { // If both files have been loaded
				Random random = new Random();
				int cpuRow, cpuCol;
				char whichShipPlayerHit = ' ', whichShipCpuHit = ' ';
				
				search: // Searches the grid for the button clicked
				for (int row = 0; row < 10; row++) {
					for (int col = 0; col < 10; col++) {
						if (cpuButtons[row][col] == event.getSource()) {
							if (cpuButtons[row][col].getIcon() == null) {
								if (cpuBoard[row][col] != '*') { // If the player hits a ship
									cpuButtons[row][col].setText(""); // Removes text in order to align image
									cpuButtons[row][col].setIcon(new ImageIcon("H.png"));
									whichShipPlayerHit = cpuBoard[row][col]; // Stores last ship hit in a variable
									cpuBoard[row][col] = 'H'; // Indicates that a ship has been hit for checking if the ship has sunk
									playerMessages.setText("<html>Direct hit, nice shot sir!<br>" + shipSank(cpuBoard, whichShipPlayerHit, "You have sunk the computer's ") + "</html>");
									hits[0]++; // Increases the player's ship hit count
								} else {
									cpuButtons[row][col].setText("");
									cpuButtons[row][col].setIcon(new ImageIcon("M.png"));
									cpuBoard[row][col] = 'M';
									playerMessages.setText("You have missed sir!");
								}
								
								if (hits[0] != 17) { // If the player hasn't won, the CPU makes a move
									do {
										cpuRow = random.nextInt(10);
										cpuCol = random.nextInt(10);
									} while (playerButtons[cpuRow][cpuCol].getIcon() != null); // Generates a move that has not been previously made
									
									if (playerBoard[cpuRow][cpuCol] != '*') { // If the CPU hits a ship
										playerButtons[cpuRow][cpuCol].setText("");
										playerButtons[cpuRow][cpuCol].setIcon(new ImageIcon("H.png"));
										whichShipCpuHit = playerBoard[cpuRow][cpuCol];
										playerBoard[cpuRow][cpuCol] = 'H';
										cpuMessages.setText("<html>The computer has attacked " + (char) (cpuRow + 65) + cpuCol + " and hit your " + whichShip(whichShipCpuHit) + "<br>" + shipSank(playerBoard, whichShipCpuHit, "The computer has sunk your ") + "</html>");
										hits[1]++;
									} else { // If the CPU misses
										playerButtons[cpuRow][cpuCol].setText("");
										playerButtons[cpuRow][cpuCol].setIcon(new ImageIcon("M.png"));
										playerBoard[cpuRow][cpuCol] = 'M';
										cpuMessages.setText("The computer has attacked " + (char) (cpuRow + 65) + cpuCol + " and missed!");
									}
								}
							}
							break search; // Exits the search for the button clicked if the button has been found
						}
					}
				}
				
				if (hits[0] == 17 || hits[1] == 17) { // If someone has won
					for (int row = 0; row < 10; row++) { // Disables all buttons
						for (int col = 0; col < 10; col++) {
							playerButtons[row][col].setEnabled(false);
							cpuButtons[row][col].setEnabled(false);
						}
					}
					if (hits[0] == 17) { // Displays message
						JOptionPane.showMessageDialog(frame, "The player has won the game!");
					} else {
						JOptionPane.showMessageDialog(frame, "The computer has won the game!");
					}
				}
			}
		}
	}
	
	private static class OpenFileChooser implements ActionListener {
		
		public void actionPerformed(ActionEvent event) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.showOpenDialog(null);
			
			// If game has not been restarted or is currently running, do not open files; last condition prevents NullPointerException
			if (cpuButtons[0][0].isEnabled() && !startGame && fileChooser.getSelectedFile() != null) {
				if (fileChooser.getSelectedFile().getName().equals("PLAYER.txt")) {
					try {
						loadFile(playerBoard, "PLAYER.txt");
					} catch (Exception e) {
						System.out.println("Error opening PLAYER.txt");
					}
					
					for (int row = 0; row < 10; row++) {
						for (int col = 0; col < 10; col++) {
							playerButtons[row][col].setText(String.valueOf(playerBoard[row][col]));
						}
					}
					
					playerMessages.setText("File Loaded!");
				} else if (fileChooser.getSelectedFile().getName().equals("CPU.txt")) {
					try {
						loadFile(cpuBoard, "CPU.txt");
					} catch (Exception e) {
						System.out.println("Error opening CPU.txt");
					}
					
					for (int row = 0; row < 10; row++) {
						for (int col = 0; col < 10; col++) {
							cpuButtons[row][col].setText("*");
						}
					}
					
					cpuMessages.setText("File Loaded!");
				}
				
				if (playerMessages.getText().equals("File Loaded!") && cpuMessages.getText().equals("File Loaded!")) {
					startGame = true; // Ready to start game once both files are loaded
				}
			}
		}
	}

}
