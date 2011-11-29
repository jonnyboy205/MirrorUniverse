package mirroruniverse.g4;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import mirroruniverse.sim.MUMap;
import mirroruniverse.sim.Player;

public class G4Player2 implements Player {

	public boolean started = false;

	public int sightRadius1;
	public int sightRadius2;
	public int intDeltaX;
	public int intDeltaY;
	public static final int MAX_SIZE = 100;
	public int[][] kb_p1;
	public int[][] kb_p2;
	private int[] p1Pos;
	private int[] p2Pos;
	private int leftExitX;
	private int leftExitY;
	private int rightExitX;
	private int rightExitY;
	private boolean leftExitSet;
	private boolean rightExitSet;

	// used mainly with move function
	private int numPath;
	private int initialDir;
	private int turn;
	private int stepCounter;
	private int currentDir;

	private ArrayList<Integer> path;

	@Override
	public int lookAndMove(int[][] aintViewL, int[][] aintViewR) {
		if (!started) {
			initialize(aintViewL, aintViewR);
		}

		// left player finding exit and updating kb
		for (int y = 0; y < aintViewL.length; ++y) {
			for (int x = 0; x < aintViewL[0].length; ++x) {
				try {
					kb_p1[p1Pos[1] - sightRadius1 + y][p1Pos[0] - sightRadius1
							+ x] = aintViewL[y][x];
				} catch (Exception e) {
					System.out.println();
				}
				if (aintViewL[y][x] == 2 && !leftExitSet) {
					leftExitX = p1Pos[0] - sightRadius1 + x;
					leftExitY = p1Pos[1] - sightRadius1 + y;
					leftExitSet = true;
				}
			}
		}
		// right player finding exit and updating kb
		for (int y = 0; y < aintViewR.length; ++y) {
			for (int x = 0; x < aintViewR[0].length; ++x) {
				kb_p2[p2Pos[1] - sightRadius2 + y][p2Pos[0] - sightRadius2 + x] = aintViewR[y][x];

				if (aintViewR[y][x] == 2 && !rightExitSet) {
					rightExitX = p2Pos[0] - sightRadius2 + x;
					rightExitY = p2Pos[1] - sightRadius2 + y;
					rightExitSet = true;
				}
			}
		}

		// after you find the exits, call AStar
		// if not, call the normal move function, which is currently a spiral
		int direction;
		if (rightExitSet && leftExitSet) {
			if (path.isEmpty()) {
				System.out.println("p1: " + p1Pos[0] + "," + p1Pos[1]
						+ "   p2:" + p2Pos[0] + "," + p2Pos[1] + "   exits: "
						+ leftExitX + "," + leftExitY + "  " + rightExitX + ","
						+ rightExitY);
				AStar_2 a = new AStar_2(p1Pos[0], p1Pos[1], p2Pos[0], p2Pos[1],
						kb_p1, kb_p2);
				a.setExit1(leftExitX, leftExitY);
				a.setExit2(rightExitX, rightExitY);
				path = a.findPath();
			}
			direction = path.remove(0);
		} else {
			direction = move(aintViewL, aintViewR);
		}
		stepCounter++;
		turn++;
		// set new current position here
		setNewCurrentPosition(direction, aintViewL, aintViewR);
		return direction;
	}

	private void initialize(int[][] aintViewL, int[][] aintViewR) {
		intDeltaX = 0;
		intDeltaY = 0;
		started = true;
		sightRadius1 = (aintViewL[0].length - 1) / 2;
		sightRadius2 = (aintViewR[0].length - 1) / 2;
		kb_p1 = new int[2 * (MAX_SIZE + sightRadius1) -1 ][2 * (MAX_SIZE + sightRadius1) -1 ];
		kb_p2 = new int[2 * (MAX_SIZE + sightRadius2) -1 ][2 * (MAX_SIZE + sightRadius2) -1 ];
		
		p1Pos = new int[2];
		p2Pos = new int[2];
		p1Pos[0] = p2Pos[0] = MAX_SIZE - 1 + sightRadius1;
		p1Pos[1] = p2Pos[1] = MAX_SIZE - 1 + sightRadius2;
		for (int i = 0; i < kb_p1.length; ++i) {
			for (int j = 0; j < kb_p1.length; ++j) {
				kb_p1[i][j] = -5;
				kb_p2[i][j] = -5;
			}
		}
		numPath = 0;
		initialDir = 2;
		currentDir = initialDir;
		stepCounter = 0;
		turn = 0;
		rightExitSet = false;
		leftExitSet = false;
		path = new ArrayList<Integer>();
	}

	private void setNewCurrentPosition(int direction, int[][] aintLocalViewL,
			int[][] aintLocalViewR) {
		intDeltaX = MUMap.aintDToM[direction][0];
		intDeltaY = MUMap.aintDToM[direction][1];
		// if the right player's next move is an empty space
		// update new position
		// if (aintLocalViewL[sightRadius1 + intDeltaX][sightRadius1 +
		// intDeltaY] == 0){
		if (aintLocalViewL[sightRadius1 + intDeltaY][sightRadius1 + intDeltaX] == 0) {
			p1Pos[0] += intDeltaX;
			p1Pos[1] += intDeltaY;
		}
		// else if(aintLocalViewL[sightRadius1 + intDeltaX][sightRadius1 +
		// intDeltaY] == 1){
		else if (aintLocalViewL[sightRadius1 + intDeltaY][sightRadius1
				+ intDeltaX] == 1) {
			// nothing changes, you couldn't move, and so you are in the same
			// place
		} else { // you hit the exit which means you go normally
			// maybe i'll lock up the p2Pos so it can't be edited again, but for
			// now
			// intDeltaX and intDeltaY were set in isDirectionCorrect in move
			// p1Pos[0] += intDeltaX;
			// p1Pos[1] += intDeltaY;
		}

		// if the right player's next move is an empty space
		// update new position
		// if (aintLocalViewR[sightRadius2 + intDeltaX][sightRadius2 +
		// intDeltaY] == 0){
		if (aintLocalViewR[sightRadius2 + intDeltaY][sightRadius2 + intDeltaX] == 0) {
			p2Pos[0] += intDeltaX;
			p2Pos[1] += intDeltaY;
		}
		// else if(aintLocalViewR[sightRadius2 + intDeltaX][sightRadius2 +
		// intDeltaY] == 1){
		else if (aintLocalViewR[sightRadius2 + intDeltaY][sightRadius2
				+ intDeltaX] == 1) {
			// nothing changes, you couldn't move, and so you are in the same
			// place
		} else { // you hit the exit which means you go normally
			// maybe i'll lock up the p2Pos so it can't be edited again, but for
			// now
			// intDeltaX and intDeltaY were set in isDirectionCorrect in move
			// p2Pos[0] += intDeltaX;
			// p2Pos[1] += intDeltaY;
		}

		// now I have to update the kb
	}

	// not really taking into account obstacles well
	//spiral while avoid boundaries
	//we first focus on left player
	private int move(int[][] aintViewL, int[][] aintViewR) {
		currentDir = getNormalizedDir();
		if (stepCounter == calcPathSteps()) {

			currentDir -= 2;
			if (currentDir <= 0) {
				currentDir = 8;
			}
			numPath++;
			stepCounter = 0;
		}

		if (turn > 1000) {
			Random rdmTemp = new Random();
			currentDir = rdmTemp.nextInt(8) + 1;
			while (!isDirectionCorrect(currentDir, aintViewL, aintViewR)) {
				currentDir = rdmTemp.nextInt(8) + 1;
			}
		} else {
			while (!isDirectionCorrect(currentDir, aintViewL, aintViewR)) {
				currentDir -= 1;
				if (currentDir <= 0) {
					currentDir = 8;
				}
			}
		}

		return currentDir;
	}

	private int getNormalizedDir() {
		int temp = 0;
		int mod = numPath % 4;
		if (mod == 0) {
			temp = 2;
		} else if (mod == 1) {
			temp = 8;
		} else if (mod == 2) {
			temp = 6;
		} else if (mod == 3) {
			temp = 4;
		}

		return temp;
	}

	private int calcPathSteps() {
		return (numPath / 2 + 1) * (2 * sightRadius1 - 1);
	}

	private boolean isDirectionCorrect(int currentDirection, int[][] aintViewL,
			int[][] aintViewR) {
		intDeltaX = MUMap.aintDToM[currentDirection][0];
		intDeltaY = MUMap.aintDToM[currentDirection][1];
		if (!leftExitSet) {
			if (aintViewL[sightRadius1 + intDeltaY][sightRadius1 + intDeltaX] == 1
	//				|| aintViewR[sightRadius2 + intDeltaY][sightRadius2 + intDeltaX] == 1 //checks for misalignment on other player
					|| isDirectionExit(currentDirection, aintViewL, aintViewR)) {
				return false;
			}
			return true;
		} else {
			if (aintViewR[sightRadius2 + intDeltaY][sightRadius2 + intDeltaX] == 1
	//				|| aintViewL[sightRadius1 + intDeltaY][sightRadius1 + intDeltaX] == 1 //checks for misalignment on other player
					|| isDirectionExit(currentDirection, aintViewL, aintViewR)) {
				return false;
			}
			return true;
		}
	}

	//isDirectionExit for either player
	private boolean isDirectionExit(int currentDirection, int[][] aintViewL,
			int[][] aintViewR) {
		intDeltaX = MUMap.aintDToM[currentDirection][0];
		intDeltaY = MUMap.aintDToM[currentDirection][1];
		if (aintViewL[sightRadius1 + intDeltaY][sightRadius1 + intDeltaX] == 2
				|| aintViewR[sightRadius2 + intDeltaY][sightRadius2 + intDeltaX] == 2) {
			return true;
		}
		return false;

	}
	
	/**
	 * Checks to see if there is nothing left to explore for a player's map
	 * @param player
	 * @return
	 */
	private boolean isMapComplete(int player){
		
		if (player == 1){
			//loop through kb, looking for walls of 1 surrounded by -5
			for (int i=0; i<kb_p1[0].length; i++){
				for (int j=0; j<kb_p1[0].length; j++){
					
					if (kb_p1[i][j] == 0){ 
						//start looking for 5's around that cell
						if (checkSurroundingCellsForFives(1, i, j) == true){
							//you can still move somewhere on the board
							return false;
						}
					}
				}
			}
			
			return true;
		}
		
		else if (player == 2){
			//loop through kb, looking for walls of 1 surrounded by -5
			for (int i=0; i<kb_p2[0].length; i++){
				for (int j=0; j<kb_p2[0].length; j++){
					
					if (kb_p2[i][j] == 0){ 
						//start looking for 5's around that cell
						if (checkSurroundingCellsForFives(2, i, j) == true){
							//you can still move somewhere on the board
							return false;
						}
					}
				}
				
			}
			
			return true;
		}
		
		else{ //check to see if both players' maps are complete
			//check left player
			//loop through kb, looking for walls of 1 surrounded by -5
			for (int i=0; i<kb_p1[0].length; i++){
				for (int j=0; j<kb_p1[0].length; j++){
					
					if (kb_p1[i][j] == 0){ 
						//start looking for 5's around that cell
						if (checkSurroundingCellsForFives(1, i, j) == true){
							//you can still move somewhere on the board
							return false;
						}
					}
				}
			}
			
			//check right player
			//loop through kb, looking for walls of 1 surrounded by -5
			for (int i=0; i<kb_p2[0].length; i++){
				for (int j=0; j<kb_p2[0].length; j++){
					
					if (kb_p2[i][j] == 0){ 
						//start looking for 5's around that cell
						if (checkSurroundingCellsForFives(2, i, j) == true){
							//you can still move somewhere on the board
							return false;
						}
					}
				}
			}
			
			return true;
		}
			
	}
	/**
	 * Checks whether a particular cell had a -5 surrounding it
	 * @param i, x coordinate in kb
	 * @param j, y coordinate in kb
	 * @return boolean whether any 0's had surrounding -5's
	 */
	private boolean checkSurroundingCellsForFives(int player, int i, int j){
		if (player == 1){ //check player 1's surroundingCells
			for (int a=-1; a<=1; a++){ //3 to capture entire immediate surroundings
				for (int b=-1; b<=1; b++){
					//skips checking current cell when past bounds in kb array
					if ((i+a<0) || (i+a>=kb_p1.length) || (i+b<0) || (i+b>=kb_p1.length))
						continue;
					if (kb_p1[i + a][i + b] == -5)
						return true;
				}	
			}	
		}
		else if(player==2){ //check player 2's surroundingCells
			for (int a=-1; a<=1; a++){ //3 to capture entire immediate surroundings
				for (int b=-1; b<=1; b++){
					//skips checking current cell when past bounds in kb array
					if ((i+a<0) || (i+a>=kb_p2.length) || (i+b<0) || (i+b>=kb_p2.length))
						continue;
					if (kb_p1[i + a][i + b] == -5)
						return true;
				}	
			}	
		}
		else{
			return true;
		}
		
		return false;
	}

	//So if there is more to explore in the map, then explore more.
	//Which node should you explore?
	//Let's try the one closest to you that's available
	private Point getNewSpace(int player){
		Point p = new Point(99, 99);
		
		if (player==1){
			//loop through kb, starting near your current pos
			for (int i=p1Pos[1]; i<kb_p1.length; i++){
				for (int j=p1Pos[0]; j<kb_p1.length; j++){
					if (checkSurroundingCellsForFives(1, i, j) == true){
						return new Point(j, i);
					}
				}
			}
			//loop through kb, starting near your current pos
			for (int i=p1Pos[1]; i>=0; i--){
				for (int j=p1Pos[0]; j>=0; j--){
					if (checkSurroundingCellsForFives(1, i, j) == true){
						return new Point(j, i);
					}
				}
			}
		}
		else if(player==2){
			//loop through kb, starting near your current pos
			for (int i=p2Pos[1]; i<kb_p2.length; i++){
				for (int j=p2Pos[0]; j<kb_p2.length; j++){
					if (checkSurroundingCellsForFives(2, i, j) == true){
						return new Point(j, i);
					}
				}
			}
			//loop through kb, starting near your current pos
			for (int i=p2Pos[1]; i>=0; i--){
				for (int j=p2Pos[0]; j>=0; j--){
					if (checkSurroundingCellsForFives(2, i, j) == true){
						return new Point(j, i);
					}
				}
			}
		}
		else{
			return p;
		}
		
		return p;
	}
}
