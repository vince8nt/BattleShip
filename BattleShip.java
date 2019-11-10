import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class BattleShip
{
	// var declaration
	static JFrame frame;
	static GridBagLayout layout;
	static GridBagConstraints c = new GridBagConstraints();
	static JLabel title;
	static JLabel corner;
	static JLabel[] numbers;
	static JLabel[] letters;
	static ImageIcon hitI = new ImageIcon("hit.png");
	static ImageIcon missI = new ImageIcon("miss.png");
	static ImageIcon waterI = new ImageIcon("water.png");
	static ImageIcon shipI = new ImageIcon("ship.png");
	static JLabel[][] tiles;
	static JComboBox numberSelect;
	static JComboBox letterSelect;
	static String[] numberSelectOptions = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
	static String[] letterSelectOptions = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
	static JButton fire;
	static JLabel messager;
	static Ship[] ships;
	static boolean[] shipsDestroyed;
	static int turnCounter;

	public static void main(String[] args)
	{
		// frame properties
		frame = new JFrame();
		frame.setSize(352, 448);
		frame.getContentPane().setBackground(new Color(150, 200, 255));
		layout = new GridBagLayout();
		frame.setLayout(layout);
		
		// make labels
		title = new JLabel("Battleship!");
		corner = new JLabel(shipI);
		numbers = new JLabel[10];
		for (int i = 0; i < 10; i++)
			numbers[i] = new JLabel(numberSelectOptions[i]);
		letters = new JLabel[10];
		for (int i = 0; i < 10; i++)
			letters[i] = new JLabel(letterSelectOptions[i]);
		tiles = new JLabel[10][10];
		for (int i = 0; i < 10; i++)
		{
			for (int j = 0; j < 10; j++)
				tiles[i][j] = new JLabel(waterI);
		}
		numberSelect = new JComboBox(numberSelectOptions);
		letterSelect = new JComboBox(letterSelectOptions);
		fire = new JButton("Fire!");
		messager = new JLabel("Fire when ready.");
		turnCounter = 0;
		
		// amke title
		c.weightx = 1;
		c.gridwidth = 11;
		c.gridx = 0;
		c.gridy = 0;
		frame.add(title, c);
		
		//corner square
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;
		frame.add(corner, c);
		
		// add row labels
		for (int i = 0; i < 10; i++)
		{
			c.gridx = i + 1;
			c.gridy = 1;
			frame.add(letters[i], c);
		}
		
		// add col labels
		for (int i = 0; i < 10; i++)
		{
			c.gridx = 0;
			c.gridy = i + 2;
			frame.add(numbers[i], c);
		}
		
		// add all the game tiles (10x10)
		for (int i = 0; i < 10; i++)
		{
			for (int j = 0; j < 10; j++)
			{
				c.gridx = i + 1;
				c.gridy = j + 2;
				frame.add(tiles[i][j], c);
			}
		}
		
		// more labels
		c.gridx = 0;
		c.gridy = 12;
		c.gridwidth = 3;
		frame.add(letterSelect, c);
		
		c.gridx = 4;
		frame.add(numberSelect, c);
		
		c.gridx = 8;
		frame.add(fire, c);
		
		c.gridwidth = 11;
		c.gridx = 0;
		c.gridy = 13;
		frame.add(messager, c);
		
		//place ships
		placeShips();
		shipsDestroyed = new boolean[5];
		
		//terminate on exit
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//main loop
		fire.addActionListener(new ActionListener()
		{
            public void actionPerformed(ActionEvent evt)
            {
            	int x = letterSelect.getSelectedIndex();
            	int y = numberSelect.getSelectedIndex();
            	boolean hit = false;
            	int hitIndex = -1;
            	for (int i = 0; i < ships.length; i++)
            	{
            		if (ships[i].hit(x, y))
            		{
            			hit = true;
            			hitIndex = i;
            		}
            	}
            	if (!tiles[x][y].getIcon().equals(waterI))
            		messager.setText("You already shot here.");
            	else if (hit)
            	{
            		turnCounter++;
            		tiles[x][y].setIcon(hitI);
            		if (ships[hitIndex].destroyed() && !shipsDestroyed[hitIndex])
            		{
            			shipsDestroyed[hitIndex] = true;
            			if (shipsUp() == 0) // hit and sink and win
            			{
            				messager.setText("All ships destroyed! Turns: " + turnCounter);
            				fire.setEnabled(false);
            				letterSelect.setEnabled(false);
            				numberSelect.setEnabled(false);
            			}
            			else // hit and sink
            			{
            			messager.setText("You sunk the " + ships[hitIndex].getType() + "! " + shipsUp() + " more to destroy.");
            			}
            		}
            		else // hit
            			messager.setText("It's a hit!");
            	}
            	else // miss
            	{
            		turnCounter++;
            		tiles[x][y].setIcon(missI);
            		messager.setText("You missed.");
            	}
            }
        });
	}
	
	// places ships that don't intersect
	static void placeShips()
	{
		ships = new Ship[5];
		ships[0] = new Ship("Carrier");
		ships[1] = new Ship("Battleship");
		while (ships[1].overlaps(ships[0]))
			ships[1] = new Ship("Battleship");
		ships[2] = new Ship("Cruiser");
		while (ships[2].overlaps(ships[0]) || ships[2].overlaps(ships[1]))
			ships[2] = new Ship("Cruiser");
		ships[3] = new Ship("Submarine");
		while (ships[3].overlaps(ships[0]) || ships[3].overlaps(ships[1]) || ships[3].overlaps(ships[2]))
			ships[3] = new Ship("Submarine");
		ships[4] = new Ship("Destroyer");
		while (ships[4].overlaps(ships[0]) || ships[4].overlaps(ships[1]) || ships[4].overlaps(ships[2]) || ships[4].overlaps(ships[3]))
			ships[4] = new Ship("Destroyer");
	}
	
	// returns how many ships are not destroyed
	static int shipsUp()
	{
		int up = 5;
		for (int i = 0; i < shipsDestroyed.length; i++)
		{
			if (shipsDestroyed[i])
				up--;
		}
		return up;
	}
}

// Ship class
class Ship
{
	String type;
	int length;
	int[] posX;
	int[] posY;
	boolean[] partsHit;
	
	// type of ship
	public Ship(String type)
	{
		this.type = type;
		if (type == "Carrier")
			length = 5;
		else if (type == "Battleship")
			length = 4;
		else if (type == "Destroyer")
			length = 2;
		else
			length = 3;
		partsHit = new boolean[length];
		setXY(length);
	}
	
	// position
	void setXY(int length)
	{
		posX = new int[length];
		posY = new int[length];
		if (0.5 < Math.random())//horizontal
		{
			int x = (int)(Math.random() * (10 - length));
			int y = (int)(Math.random() * 10);
			for (int i = 0; i < length; i++)
			{
				posX[i] = x + i;
				posY[i] = y;
			}
		}
		else //vertical
		{
			int x = (int)(Math.random() * 10);
			int y = (int)(Math.random() * (10 - length));
			for (int i = 0; i < length; i++)
			{
				posX[i] = x;
				posY[i] = y + i;
			}
		}
	}
	
	// getters
	public String getType()
	{
		return type;
	}
	public int[] getPosX()
	{
		return posX;
	}
	public int[] getPosY()
	{
		return posY;
	}
	
	// returns if the ship is hit
	public boolean hit(int x, int y)
	{
		boolean hit = false;
		for (int i = 0; i < length; i++)
		{
			if (posX[i] == x && posY[i] == y)
			{
				hit = true;
				partsHit[i] = true;
			}
		}
		return hit;
	}
	
	// returns if the two ships overlap on the board
	public boolean overlaps(Ship test)
	{
		int[] x = test.getPosX();
		int[] y = test.getPosY();
		boolean overlap = false;
		for (int i = 0; i < length; i++)
		{
			for (int j = 0; j < x.length; j++)
			{
				if (posX[i] == x[j] && posY[i] == y[j])
					overlap = true;
			}
		}
		return overlap;
	}
	
	// returns true if the ship is destroyed
	public boolean destroyed()
	{
		boolean destroyed = true;
		for (int i = 0; i < length; i++)
		{
			if (partsHit[i] == false)
				destroyed = false;
		}
		return destroyed;	
	}
}







