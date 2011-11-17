package mirroruniverse.g4;

import mirroruniverse.sim.Player;

public class G4Player implements Player{
	
	public int sightRadius;
	public boolean started = false;
	public static final int MAX_SIZE = 100;
	public int[][] kb;

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
		kb = new int[MAX_SIZE][MAX_SIZE];
	}

}
