package mirroruniverse.g4;

import mirroruniverse.sim.Player;

public class G4Player implements Player{
	
	public int sightRadius;
	public boolean started = false;
	public static final int MAX_SIZE = 100;
	public int[][] kb_p1;
	public int[][] kb_p2;
	private int[] p1Pos;
	private int[] p2Pos;
	
	private int numPath;
	private int initialDir;
	//private int turn;
	private int stepCounter;
	private int currentDir;

	@Override
	public int lookAndMove(int[][] aintViewL, int[][] aintViewR) {
		if (!started){
			initialize(aintViewL);
		}
		
		return move();
		
		//return 0;
	}
	
	public void initialize(int[][] aintViewL){
		started = true;
		sightRadius = (aintViewL[0].length - 1)/2;
		kb_p1 = new int[2*MAX_SIZE-1][2*MAX_SIZE-1];
		kb_p2 = new int[2*MAX_SIZE-1][2*MAX_SIZE-1];
		p1Pos = new int[2];
		p2Pos = new int[2];
		p1Pos[0] = p2Pos[0] = p1Pos[1] = p2Pos[1] = 99;
		
		for(int i = 0; i < kb_p1.length; ++i){
			for(int j = 0; j < kb_p1.length; ++j){
				kb_p1[i][j] = -5;
				kb_p2[i][j] = -5;
			}
		}
		
		numPath = 0;
		initialDir = 2;
		currentDir = initialDir;
		stepCounter = 0;
	}
	
	//not currently taking into account obstacles
	public int move(){
		if (stepCounter == calcPathSteps()){
			currentDir -= 2;
			if (currentDir == 0){
				currentDir = 8;
			}
			numPath++;
			stepCounter = 0;
		}
		
		stepCounter++;
		//turn++;
		return currentDir;
	}
	
	private int calcPathSteps(){
		return (numPath/2 + 1)*(2*sightRadius - 1);
	}

}
