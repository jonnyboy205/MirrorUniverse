package mirroruniverse.g2;

import java.util.LinkedList;
import java.util.Random;

import mirroruniverse.sim.MUMap;

public class Explorer {
	Map leftMap;
	Map rightMap;
	LinkedList<Position> leftOpenList = new LinkedList<Position>();
	LinkedList<Position> rightOpenList = new LinkedList<Position>();
	public int r = -1;
	public boolean allExplored = false;
	LinkedList<Integer> backtrack = new LinkedList<Integer>();
	
	//New Strategy
	//Enumerate every possible move
	//See which move would gain most information
	//If no good move, find direction towards next open space
	

	public Explorer(Map leftMap, Map rightMap) {
		this.leftMap = leftMap;
		this.rightMap = rightMap;
	}
	
	// random move
	public int getMove(int[][] aintViewL, int[][] aintViewR) {
		if (r == -1) {
			r = aintViewL.length / 2;
			System.out.println("*********r is " + r);
		}
//		Random rdmTemp = new Random();
//		int nextX = rdmTemp.nextInt(3);
//		int nextY = rdmTemp.nextInt(3);

//		int d = MUMap.aintMToD[nextX][nextY];
		int d = nextBestSearch();
		//if (Config.DEBUG) {
//			System.out.println("Next move is :" + MUMap.aintDToM[d][0] + " "
//					+ MUMap.aintDToM[d][1]);
		//}
		
		return d;
	}
	
	public int nextBestSearch() {
		int d = 0;
		int bestCount = 0;
		for (int i = 0; i <= 8; i++) {
			int[] diff = MUMap.aintDToM[i];
			//left
			int leftCount = countNewSpacesOpened(diff, leftMap, leftMap.playerPos);
			//right
			int rightCount = countNewSpacesOpened(diff, rightMap, rightMap.playerPos);
			//System.out.println("best count " + (leftCount + rightCount));
			if (leftCount + rightCount > bestCount) {
				bestCount = leftCount + rightCount;
				d = i;
			}
		}
		if (bestCount != 0) {
			//System.out.println("the count is " + bestCount);
			//System.out.println("best count: " + d);
			//backtrack.addFirst(d);
			return d;
		} else {
//			if (!backtrack.isEmpty()) {
//				int back = oppositeDirection(backtrack.removeLast());
//				if (back != -1 || back != 0) {
//					return back;
//				}
//			}
			//d = findnextUnopen();
			Random rdmTemp = new Random();
			int nextX;
			int nextY;
			do {
				//System.out.println("Unopen: " + d);
				nextX = rdmTemp.nextInt(3);
				nextY = rdmTemp.nextInt(3);
				d = MUMap.aintMToD[nextX][nextY];
			} while (d == 0 /*&& 
					leftMap.map[leftMap.playerPos.y+nextY][leftMap.playerPos.x+nextX] != 
						Map.Tile.EXIT.getValue() &&
						rightMap.map[rightMap.playerPos.y+nextY][rightMap.playerPos.x+nextX] != 
							Map.Tile.EXIT.getValue()*/
					);
			return d;
		}
	}
	
	public int countNewSpacesOpened(int[] diff, Map myMap, Position pos) {
		//System.out.println("******" + myMap.name + "******");
		int ret = 0;
		Position newPos = pos.newPosFromOffset(diff[1], diff[0]);
		//System.out.println("new position: " + newPos.y + "," + newPos.x + " is " + myMap.map[newPos.y][newPos.x]);
		//Account for bad moves
		if (myMap.map[newPos.y][newPos.x] == Map.Tile.EMPTY.getValue()) {
			for (int i = -((r / 2) + 1); i <= (r / 2) + 1; i++) {
				for (int j = -((r / 2) + 1); j <= (r / 2) + 1; j++) {
					//System.out.println("countNewSpacesOpened: " + myMap.map[newPos.y + i][newPos.x + j]);
					//System.out.println("y: " + (newPos.y + i));
					//System.out.println("x: " + (newPos.x + j));
					if (myMap.map[newPos.y + i][newPos.x + j] == 
							Map.Tile.UNKNOWN.getValue()) {
						ret++;
					}
				}
			}
		}
		return ret;
	}
	
	public int oppositeDirection(int n) {
		switch(n) {
			case 0: return 0; 
			case 1: return 5; 
			case 2: return 6; 
			case 3: return 7; 
			case 4: return 8; 
			case 5: return 1; 
			case 6: return 2; 
			case 7: return 3; 
			case 8: return 4; 
		}
		return -1;
	}
	
	
	//TODO - How to use Route Finder
	public int findnextUnopen() {
		Position leftPos = countTillUnopen(leftMap, leftMap.playerPos);
		Position rightPos = countTillUnopen(rightMap, rightMap.playerPos);
		if (leftPos == null && rightPos == null) {
			allExplored = true;
		} else if (leftPos == null) {
			return getDirection(rightPos, rightMap.playerPos);
		} else if (rightPos == null) {
			return getDirection(leftPos, leftMap.playerPos);
		} else {
			if ((Math.abs(leftPos.x - leftMap.playerPos.x) + 
					Math.abs(leftPos.y - leftMap.playerPos.y)) > 
					(Math.abs(rightPos.x - rightMap.playerPos.x) + 
							Math.abs(rightPos.y - rightMap.playerPos.y))) {
								return getDirection(rightPos, rightMap.playerPos);
							} else {
								return getDirection(leftPos, leftMap.playerPos);
							}
		}
		return 0;
	}
	
	public int getDirection(Position newPos, Position oldPos) {
		int x1 = oldPos.x;
		int x2 = newPos.x;
		int y1 = oldPos.y;
		int y2 = newPos.y;
		
		if (x1 == x2 && y1 < y2) {
			return 7; // move down
		} else if (x1 == x2 && y1 > y2) {
			return 3; // move up
		} else if (y1 == y2 && x1 < x2) {
			return 1; // move right
		} else if (y1 == y2 && x1 > x2) {
			return 5; // move left
		} else if (x1 < x2 && y1 < y2) {
			return 8; // move down- right (south east)
		} else if (x1 > x2 && y1 > y2) {
			return 4; // up left ( north west)
		} else if (x1 > x2 && y1 < y2) {
			return 6; // down left (south west)
		} else if (x1 < x2 && y1 > y2) {
			return 2; // up right (north east)
		} else {
			return 0; //stay put
		}
	}
	
	public Position countTillUnopen(Map myMap, Position pos) {
		LinkedList<Position> toCheck = new LinkedList<Position>();
		LinkedList<Position> checked = new LinkedList<Position>();
		toCheck.add(pos);
		int count = 0;
		while (!toCheck.isEmpty() && count < Config.MAX_SIZE * 800) {
			count++;
			for (int i = 0; i <= 8; i++) {
				int[] diff = MUMap.aintDToM[i];
				Position newPos = pos.newPosFromOffset(diff[0], diff[1]);
				if (myMap.map[newPos.y][newPos.x] == Map.Tile.EMPTY.getValue()) {
					if (!toCheck.contains(newPos) && !checked.contains(newPos)) {
						toCheck.addLast(newPos);	
					}
				}
			}
			Position thisLoopPos = toCheck.removeFirst();
			if (hasUnexploredNeighbor(thisLoopPos, myMap)) {
				return thisLoopPos;
			}
			checked.add(thisLoopPos);
		}
		System.out.println("Return null");
		return null;
	}
	
	public boolean hasUnexploredNeighbor(Position pos, Map myMap) {
		for (int i = 0; i <= 8; i++) {
			int[] diff = MUMap.aintDToM[i];
			Position newPos = pos.newPosFromOffset(diff[0], diff[1]);
			if (myMap.map[newPos.y][newPos.x] == Map.Tile.UNKNOWN.getValue())
				System.out.println(myMap.map[newPos.y][newPos.x] + " equal unknown?");
				return true;
		}
		System.out.println("false");
		return false;
	}
	
	public void updateSeen(Map myMap, LinkedList<Position> list, int[][] view) {
		LinkedList<int[]> views = new LinkedList<int[]>();
		for (Position p: list) {
			for (int[] v: views) {
				if (p.y == myMap.playerPos.y + v[0] && 
						p.x == myMap.playerPos.x + v[1]) {
					views.remove(v);
				}		
			}
		}
		
	}
		 
}
