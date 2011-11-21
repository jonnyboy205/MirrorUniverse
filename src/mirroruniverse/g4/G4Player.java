package mirroruniverse.g4;

import java.util.ArrayList;

import mirroruniverse.sim.MUMap;
import mirroruniverse.sim.Player;

public class G4Player implements Player {

	public boolean started = false;
	public int sightRadius;
	
	//local information
	public int midElement;
	public int intDeltaX;
	public int intDeltaY;
	
	//knowledge base stuff
	public static final int MAX_SIZE = 100;
	public int[][] kb_p1;
	public int[][] kb_p2;
	private int[] p1Pos;
	private int[] p2Pos;
	private int[][] aintLocalViewL;
	private int[][] aintLocalViewR;
	private AStar myAStar;
	private int leftExitX;
	private int leftExitY;
	private int rightExitX;
	private int rightExitY;
	private boolean leftExitSet;
	private boolean rightExitSet;

	//used mainly with move function
	private int numPath;
	private int initialDir;
	private int turn;
	private int stepCounter;
	private int currentDir;
	
	private RandomPlayer myRandomPlayer; 
	
	private ArrayList<Integer> path;

	@Override
	public int lookAndMove(int[][] aintViewL, int[][] aintViewR) {
		if (!started) {
			initialize(aintViewL);
		}
		
		/**
		 * If you find the exit, go in immediately
		 * @author From RandomPlayer, Shen
		 */
		//left
		aintLocalViewL = new int[ 3 ][ 3 ];
		int intMid = aintViewL.length / 2;
		for ( int i = -1; i <= 1; i ++ )
		{
			for ( int j = -1; j <= 1; j ++ )
			{
				//setting kb here
				kb_p1[ p1Pos[0] + i ][ p1Pos[1] + j ] = aintViewL[ intMid + j ][ intMid + i ];

				aintLocalViewL[ 1 + j ][ 1 + i ] = aintViewL[ intMid + j ][ intMid + i ];
				if ( aintLocalViewL[ 1 + j ][ 1 + i ] == 2 )
				{
					if ( i == 0 && j == 0 )
						continue;
					//let Nate know about exit here
					//AStar.setExit1()
					leftExitX = p1Pos[0] + i;
					leftExitY = p1Pos[1] + j;
					leftExitSet = true;
					//check if right Exit has been set
					//if (rightExitSet == true)
						//let AStar know it can take over now
					/**
					 * Nate, we currently move to the exit as soon as we see it.
					 * This is so that it would actually compile and so MirrorUniverse would stop.
					 * But should comment out this line once you're ready to pass on the movements
					 * to AStar's algorithm.
					 */
					//return MUMap.aintMToD[ j + 1 ][ i + 1 ];
				}
			}
		}
		//right
		intMid = aintViewR.length / 2;
		aintLocalViewR = new int[ 3 ][ 3 ];
		for ( int i = -1; i <= 1; i ++ )
		{
			for ( int j = -1; j <= 1; j ++ )
			{
				//setting kb here
				kb_p2[ p2Pos[0] + i ][ p2Pos[1] + j ] = aintViewL[ intMid + j ][ intMid + i ];
				
				aintLocalViewR[ 1 + j ][ 1 + i ] = aintViewR[ intMid + j ][ intMid + i ];
				if ( aintViewR[ intMid + j ][ intMid + i ] == 2 )
				{
					if ( i == 0 && j == 0 )
						continue;
					//let Nate know about exit here
					//AStar.setExit2()
					rightExitX = p2Pos[0] + i;
					rightExitY = p2Pos[1] + j;
					rightExitSet = true;
					//if (leftExitSet == true)
						//letAStar know it can now take over
					/**
					 * Nate, we currently move to the exit as soon as we see it.
					 * This is so that it would actually compile and so MirrorUniverse would stop.
					 * But should comment out this line once you're ready to pass on the movements
					 * to AStar's algorithm.
					 */
					//return MUMap.aintMToD[ j + 1 ][ i + 1 ];
				}
			}
		}
		int direction;
		if(rightExitSet && leftExitSet){
			if(path.isEmpty()){
				AStar a = new AStar(p1Pos[0], p1Pos[1], p2Pos[0], p2Pos[1], kb_p1, kb_p2);
				path = a.findPath();
			}
			direction = path.remove(0);
		}else{
			direction = move(aintViewL, aintViewR);
		}
		stepCounter++;
		turn++;
		
		//set new current position here
		setNewCurrentPosition();
		
		return direction;
	}

	public void initialize(int[][] aintViewL) {

		intDeltaX = 0;
		intDeltaY = 0;
		started = true;
		sightRadius = (aintViewL[0].length - 1) / 2;
		midElement = sightRadius;
		kb_p1 = new int[2 * MAX_SIZE/* - 1*/][2 * MAX_SIZE/* - 1*/];
		kb_p2 = new int[2 * MAX_SIZE/* - 1*/][2 * MAX_SIZE/* - 1*/];
		p1Pos = new int[2];
		p2Pos = new int[2];
		p1Pos[0] = p2Pos[0] = p1Pos[1] = p2Pos[1] = 99;

		for (int i = 0; i < kb_p1.length; ++i) {
			for (int j = 0; j < kb_p1.length; ++j) {
				kb_p1[i][j] = -5;
				kb_p2[i][j] = -5;
			}
		}
		//myAStar = new AStar();

		numPath = 0;
		initialDir = 2;
		currentDir = initialDir;
		stepCounter = 0;
		turn = 0;
		myRandomPlayer = new RandomPlayer();
		
		rightExitSet = false;
		leftExitSet = false;
		
		path = new ArrayList<Integer>();
	}

	private void setNewCurrentPosition() {
		p1Pos[0] += intDeltaX;
		p1Pos[1] += intDeltaY;
		
		//if the right player's next move is an empty space
		//update new position
		if (aintLocalViewR[1 + intDeltaX][1 + intDeltaY] == 0){
			p2Pos[0] += intDeltaX;
			p2Pos[1] += intDeltaY;
		}
		else if(aintLocalViewR[1 + intDeltaX][1 + intDeltaY] == 1){
			//nothing changes, you couldn't move, and so you are in the same place
		}
		else{ //you hit the exit which means you go normally
		//maybe i'll lock up the p2Pos so it can't be edited again, but for now
		//intDeltaX and intDeltaY were set in isDirectionCorrect in move
			p2Pos[0] += intDeltaX;
			p2Pos[1] += intDeltaY;
		}
		
		//now I have to update the kb
	}

	// not currently taking into account obstacles
	public int move(int[][] aintViewL, int[][] aintViewR) {
		currentDir = getNormalizedDir();
		if (stepCounter == calcPathSteps()) {
			
//			if(currentDir%2!=0)
//				currentDir+=1;
			
			currentDir -= 2;
			if (currentDir <= 0) {
				currentDir = 8;
			}
			numPath++;
			stepCounter = 0;
		}

		while(!isDirectionCorrect(currentDir, aintViewL)){
			currentDir-=1;
			if (currentDir <= 0) {
				currentDir = 8;
			}
		}

		if (turn > 500)
			return myRandomPlayer.lookAndMove(aintViewL, aintViewR);
		
		return currentDir;
	}

	private int getNormalizedDir() {
		int temp = 0;
		int mod = numPath%4;
		if (mod == 0){
			temp = 2;
		}
		else if (mod == 1){
			temp = 8;
		}
		else if (mod == 2){
			temp = 6;
		}
		else if (mod == 3){
			temp = 4;
		}
		
		return temp;
	}

	private int calcPathSteps() {
		return (numPath / 2 + 1) * (2 * sightRadius - 1);
	}

	private boolean isDirectionCorrect(int currentDirection, int[][] aintViewL) {
		intDeltaX = MUMap.aintDToM[currentDirection][0];
		intDeltaY = MUMap.aintDToM[currentDirection][1];
		if (aintViewL[midElement + intDeltaY][midElement + intDeltaX] == 1) {
			return false;
		}
		return true;
	}
}
