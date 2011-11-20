package mirroruniverse.g4;

import mirroruniverse.sim.MUMap;
import mirroruniverse.sim.Player;

public class G4Player implements Player {

	public int midElement;
	public int intDeltaX;
	public int intDeltaY;
	public int sightRadius;
	public boolean started = false;
	public static final int MAX_SIZE = 100;
	public int[][] kb_p1;
	public int[][] kb_p2;
	private int[] p1Pos;
	private int[] p2Pos;

	private int numPath;
	private int initialDir;
	private int turn;
	private int stepCounter;
	private int currentDir;
	
	private RandomPlayer myRandomPlayer; 

	@Override
	public int lookAndMove(int[][] aintViewL, int[][] aintViewR) {
		int[][] aintLocalViewL = new int[ 3 ][ 3 ];
		int intMid = aintViewL.length / 2;
		for ( int i = -1; i <= 1; i ++ )
		{
			for ( int j = -1; j <= 1; j ++ )
			{
				aintLocalViewL[ 1 + j ][ 1 + i ] = aintViewL[ intMid + j ][ intMid + i ];
				if ( aintLocalViewL[ 1 + j ][ 1 + i ] == 2 )
				{
					if ( i == 0 && j == 0 )
						continue;
					return MUMap.aintMToD[ j + 1 ][ i + 1 ];
				}
			}
		}
		
		intMid = aintViewR.length / 2;
		int[][] aintLocalViewR = new int[ 3 ][ 3 ];
		for ( int i = -1; i <= 1; i ++ )
		{
			for ( int j = -1; j <= 1; j ++ )
			{
				aintLocalViewR[ 1 + j ][ 1 + i ] = aintViewR[ intMid + j ][ intMid + i ];
				if ( aintViewR[ intMid + j ][ intMid + i ] == 2 )
				{
					if ( i == 0 && j == 0 )
						continue;
					return MUMap.aintMToD[ j + 1 ][ i + 1 ];
				}
			}
		}
		
		if (!started) {
			initialize(aintViewL);
		}
		int direction = move(aintViewL, aintViewR);

		return direction;

		// return 0;
	}

	public void initialize(int[][] aintViewL) {

		intDeltaX = 0;
		intDeltaY = 0;
		started = true;
		sightRadius = (aintViewL[0].length - 1) / 2;
		midElement = sightRadius;
		kb_p1 = new int[2 * MAX_SIZE - 1][2 * MAX_SIZE - 1];
		kb_p2 = new int[2 * MAX_SIZE - 1][2 * MAX_SIZE - 1];
		p1Pos = new int[2];
		p2Pos = new int[2];
		p1Pos[0] = p2Pos[0] = p1Pos[1] = p2Pos[1] = 99;

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
		myRandomPlayer = new RandomPlayer();
	}

	// not currently taking into account obstacles
	public int move(int[][] aintViewL, int[][] aintViewR) {
		currentDir = getNormalizedDir();
		if (stepCounter == calcPathSteps()) {
			/*if(currentDir%2!=0)
				currentDir+=1;*/
			
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

		stepCounter++;
		if (turn > 100)
			return myRandomPlayer.lookAndMove(aintViewL, aintViewR);
		turn++;
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
		if (aintViewL[midElement + intDeltaX][midElement + intDeltaY] == 1) {
			return false;
		}
		return true;
	}
}
