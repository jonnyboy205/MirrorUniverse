package mirroruniverse.g4;

import java.util.ArrayList;

public class Node implements Comparable<Node>{
	
	// The two players' positions
	private int x1;
	private int y1;
	private int x2;
	private int y2;
	
	public int getX1() {
		return x1;
	}

	public int getY1() {
		return y1;
	}

	public int getX2() {
		return x2;
	}

	public int getY2() {
		return y2;
	}

	// The value of this node
	private int value;
	
	// The depth of this node
	private int depth;
	
	// The path taken to get to this node
	private ArrayList<Integer> actionPath;
	
	// The positions of the exits for each player
	private static int p1ExitX = -1000;
	private static int p1ExitY = -1000;
	private static int p2ExitX = -1000;
	private static int p2ExitY = -1000;
	
	// Constructor, for the first node, have parent = null;
	public Node(int p1X, int p1Y, int p2X, int p2Y, Node parent, int action){
		x1 = p1X;
		y1 = p1Y;
		x2 = p2X;
		y2 = p2Y;
		
		// Set the value of this node equal to our heuristic rating for it
		value = heuristic(x1,y1,x2,y2);
		
		if(parent == null){
			depth = 0;
			actionPath = new ArrayList<Integer>();
		} else {
			depth = parent.getDepth() + 1;
			actionPath = ((ArrayList<Integer>) parent.getActionPath().clone());
			actionPath.add(action);
		}
	}

	//Implement our actual heuristic here, right now just takes the total distance both players are away from the goal
	private static int heuristic(int p1X, int p1Y, int p2X, int p2Y){
		if(p1ExitX == -1000 || p2ExitX == -1000){
			return Integer.MAX_VALUE;
		}
		return Math.max(Math.abs(p1X - p1ExitX), Math.abs(p1Y - p1ExitY)) + Math.max(Math.abs(p2X - p2ExitX), Math.abs(p2Y - p2ExitY));
		
	}


	public int getValue() {
		return value;
	}


	public int getDepth() {
		return depth;
	}


	public ArrayList<Integer> getActionPath() {
		return actionPath;
	}
	
	// Equals function, will return true if the corresponding x and y values are equal
	public boolean equals(Object o){
		if(!(o instanceof Node)){
			return false;
		}
		
		Node n = (Node) o;
		return (n.x1 == x1) && (n.x2 == x2) && (n.y1 == y1) && (n.y2 == y2); 
	}

	@Override
	public int compareTo(Node n) {
		if(value > n.value){
			return -1;
		} else if(value < n.value){
			return 1;
		} else {
		return 0;
		}
	}
}
