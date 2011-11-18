package mirroruniverse.g4;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class AStar {

	private int[][] map1;
	private int[][] map2;
	Node root;
	PriorityQueue<Node> queue;
	
	public AStar(int initialX1, int initialY1, int initialX2, int initialY2, int[][] kb_p1, int[][] kb_p2){
		root = new Node(initialX1, initialY1, initialX2, initialY2, null, 0);
		map1 = kb_p1;
		map2 = kb_p2;
		queue = new PriorityQueue<Node>();
		queue.add(root);
	}
	
	
	
	// Will generate the possible next moves 
	private ArrayList<Node> successors(Node n){
		int x1;
		int x2;
		int y1;
		int y2;
		// Later, use the index variable to say which action has been taken.  Look at MUMap.java, aintDToM or something to figure out the directions
		int index = 0;
		
		ArrayList<Node> nexts = new ArrayList<Node>();
		
		for(int xChange = -1; xChange < 2; ++xChange){
			for(int yChange = -1; yChange < 2; ++yChange){
				
				// Dont want to add the current node to the path
				if(xChange == 0 && yChange == 0){
					continue;
				}
				
				x1 = n.getX1() + xChange;
				y1 = n.getY1() + yChange;
				x2 = n.getX2() + xChange;
				y2 = n.getY2() + yChange;
				
				if(map1[x1][y1] == 1){
					x1 -= xChange;
					y1 -= yChange;
				}
				if(map2[x2][y2] == 1){
					x2 -= xChange;
					y2 -= yChange;
				}
				
				Node toAdd = new Node(x1, y1, x2, y2, n, index);
				
				if(!n.equals(toAdd)){
					nexts.add(toAdd);
				}
			}
		}
		
		return null;
	}
}
