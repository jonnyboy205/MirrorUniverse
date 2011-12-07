package mirroruniverse.g4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

public class AStar_Single {

	private int[][] map;
	Node_Single root;
	PriorityQueue<Node_Single> queue;
	ArrayList<Node_Single> closed;
	public boolean debugging = false;
	private int maxNodes = Integer.MAX_VALUE;
	private int nodesExpanded;
	private static HashMap<ArrayList<Integer>,ArrayList<Integer>> continueClosed;
	private boolean useClosed;
	
	public AStar_Single(int initialX, int initialY, int exitX, int exitY, int[][] kb, boolean useSeed){
		root = new Node_Single(initialX, initialY, exitX, exitY);
		map = kb;
		
		queue = new PriorityQueue<Node_Single>();
		queue.add(root);
		closed = new ArrayList<Node_Single>();
		nodesExpanded = 0;
		useClosed = useSeed;
		
		if(useClosed){
			if(continueClosed == null){
				continueClosed = new HashMap<ArrayList<Integer>,ArrayList<Integer>>();
			} else {
				//Node_Single.reRunHeuristic(continueClosed, exitX, exitY);
				//System.out.println(continueClosed.size());
				//queue.addAll(continueClosed.keySet());
			}
		}
	}
	
	public static void resetContinueClosed(){
		continueClosed.clear();
	}
	
	public void setMaxNodes(int n){
		maxNodes = n;
	}
	/*public void exitsFound(){
		if (increase) {
			Node_Single.incDegree();
			Node_Single.reRunHeuristic(nodesToPutOff);
			PriorityQueue<Node_Single> tempQ = new PriorityQueue<Node_Single>(nodesToPutOff);
			queue = tempQ;
			if (queue.isEmpty()) {
				System.out.println("Increasing limit");
				maxNodes *= 2;
				Node_Single.resetDegree();
				Node_Single.reRunHeuristic(closed);
				queue.addAll(closed);
				queue.add(root);
				closed.clear();
			}
			nodesToPutOff.clear();
		} else {
			Node_Single.addDiff(closed);
			PriorityQueue<Node_Single> tempQ = new PriorityQueue<Node_Single>(closed);
			queue = tempQ;
			closed.clear();
		}
		increase = !increase;
		//closed.clear();
	}*/
	
	public Node_Single findPath(){
		while(!queue.isEmpty() && queue.peek().getValue() != queue.peek().getDepth()){
			if(nodesExpanded > maxNodes){
				queue.clear();
				break;
			}
			Node_Single lookingAt = queue.peek();
			ArrayList<Integer> temp = new ArrayList<Integer>();
			temp.add(lookingAt.getX1());
			temp.add(lookingAt.getY1());
			temp.add(lookingAt.getExitX());
			temp.add(lookingAt.getExitY());
			if(useClosed){
				ArrayList<Integer> prevPath = continueClosed.get(temp);
				if(prevPath != null){
					lookingAt.setNewPath(prevPath);
					break;
				}
			}
			ArrayList<Node_Single> nexts = successors(queue.poll());
			queue.addAll(nexts);
		}
		if(useClosed){
			for(Node_Single n : closed){
			ArrayList<Integer> temp = new ArrayList<Integer>();
			temp.add(n.getRootX());
			temp.add(n.getRootY());
			temp.add(n.getExitX());
			temp.add(n.getExitY());
				if(!continueClosed.keySet().contains(temp)){
					continueClosed.put(temp,n.getActionPath());
				}
			}
		}
		//System.out.println("Done");
		if(queue.isEmpty()){
			//System.out.println("Empty :(");
			//exitsFound();
			return null;
		} else {
			//System.out.println("Found :)");
			//System.out.println(queue.peek());
			return queue.peek();
		}
	}
	
	// Will generate the possible next moves 
	private ArrayList<Node_Single> successors(Node_Single n){
		++nodesExpanded;
		closed.add(n);
		int x1;
		int y1;
		int action = 0;
		//int[] indexOfAction = {6,5,4,7,0,3,8,1,2};
		int[] indexOfAction = {4,5,6,3,0,7,2,1,8};
		ArrayList<Node_Single> nexts = new ArrayList<Node_Single>();
		
		for(int xChange = -1; xChange < 2; ++xChange){
			for(int yChange = -1; yChange < 2; ++yChange){
				
				// Dont want to add the current Node_Single to the path
				if(xChange == 0 && yChange == 0){
					++action;
					continue;
				}
				
				x1 = n.getX1() + xChange;
				y1 = n.getY1() + yChange;
				
				try {
					if (map[y1][x1] == 1 || map[y1][x1] == -5) {
						x1 -= xChange;
						y1 -= yChange;
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					x1 -= xChange;
					y1 -= yChange;
				}
				Node_Single toAdd = new Node_Single(x1, y1, n, indexOfAction[action]);
				
				if(!n.equals(toAdd) && shouldIAdd(toAdd)){
					nexts.add(toAdd);
				}
				++action;
			}
		}
		
		return nexts;
	}
	
	private boolean shouldIAdd(Node_Single n){
		for(Node_Single q: queue){
			if(n.equals(q)){
				if(n.getDepth() < q.getDepth()){
					queue.remove(q);
					return true;
				} else {
					return false;
				}
			}
		}
		
		for(Node_Single c: closed){
			if(n.equals(c)){
				//if(n.getValue() + n.getDepth() < c.getValue() + c.getDepth()){
					//closed.remove(c);
					//return true;
				//} else {
					return false;
				//}
			}
		}
		return true;
	}
}
