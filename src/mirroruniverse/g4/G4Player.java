package mirroruniverse.g4;

import mirroruniverse.sim.Player;

public class G4Player implements Player{
	
	public int sightRadius;
	public boolean started = false;
	public static final int MAX_SIZE = 100;
	public int[][] kb_p1;
	public int[][] kb_p2;

	@Override
	public int lookAndMove(int[][] aintViewL, int[][] aintViewR) {
		if (!started){
			initialize(aintViewL);
		}
		return 0;
	}
	
	public void initialize(int[][] aintViewL){
		started = true;
		sightRadius = aintViewL[0].length;
		kb_p1 = new int[2*MAX_SIZE][2*MAX_SIZE];
		kb_p2 = new int[2*MAX_SIZE][2*MAX_SIZE];
		for(int i = 0; i < kb_p1.length; ++i){
			for(int j = 0; j < kb_p1.length; ++j){
				kb_p1[i][j] = -5;
				kb_p2[i][j] = -5;
			}
		}
	}

}
