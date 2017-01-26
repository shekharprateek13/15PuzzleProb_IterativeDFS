package cs.ai.dfs;

import static cs.ai.dfs.Constant.POSSIBLE_COORDINATES;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

import cs.ai.dfs.Constant.ACTION;

/**
 * This class solves 15-puzzle problem using Iterative Depth First Search. 
 * The class consists of the following fields:
 * FRONTIER: Queue Implementation used in Breadth First Search
 * EXPLORED: List of Visited or Explored Nodes in graph.
 * POSSIBLE_ACTIONS: List of all the possible actions which could be applied to a node.
 * INITIAL_NODE: the initial state of 15-puzzle problem or the Root Node.
 * GOAL_NODE: the final state of the 15-puzzle or the Leaf node at which the algorithm stops.
 * @author Akhauri Prateek Shekhar
 */
public class IterativeDepthFirstSearch {

	static Deque<Node> FRONTIER = new ArrayDeque<Node>();	//Here, Frontier is used as a LIFO data structure
	static List<Node> EXPLORED = new ArrayList<Node>();
	static List<ACTION> POSSIBLE_ACTIONS = new ArrayList<ACTION>(Arrays.asList(ACTION.values())); 
	static Node INITIAL_NODE;
	static Node GOAL_NODE;

	/**
	 * This function takes an Input String Configuration for 15-puzzle problem (Example: '1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 0') and returns a Node.
	 * @param stateConfigStr: the Input String where labels of 15 puzzle problem are comma separated.
	 * @param parent: The parent of the Node to be returned.
	 * @param action: The Action associated with this Node.
	 * @param pathCost: The Path cost associated with this Node.
	 * @return a Node with State corresponding to the input string, parent, action and path-cost.
	 */
	public Node createNode(String stateConfigStr,Node parent,ACTION action,Integer pathCost){
		Node node = new Node();

		String[] tempStateArr = stateConfigStr.split(" ");
		Tile[][] state = new Tile[4][4];
		for(int x = 0;x < 4;x++){
			for(int y = 0;y < 4;y++){
				String label = tempStateArr[(4*x + y)];
				Tile tile = new Tile(label,new Coordinates(x,y));
				state[x][y] = tile;
			}
		}

		node.setSTATE(state);
		node.setACTION(action);
		node.setPARENT(parent);
		node.setPATH_COST(pathCost);
		return node;
	}

	/**
	 * This function returns the position of Blank Tile from the Input State
	 * @param inputState, the state from which Blank Tile's position has to be found.
	 * @return The Blank Tile along with its position in the state
	 */
	public Tile getBlankTilePosition(Tile[][] inputState){
		Tile blankTileInfo = null;
		loop_rows:for(Tile[] rows:inputState){
			for(Tile tile:rows){
				if(tile.getLabel().equals("0")){
					blankTileInfo = tile;
					break loop_rows;
				}
			}
		}
		return blankTileInfo;
	}

	/**
	 * This function takes an existing state and an Action and returns a new state after applying action to the existing state
	 * @param inputState, to which action has to be applied
	 * @param action, which has to be applied
	 * @return New state, after applying action to the Input State or Null if the New State after applying action is not permissible
	 */
	public Tile[][] getNewState(Tile[][] inputState,ACTION action){
		Tile[][] newState = null;

		Tile blankTile = getBlankTilePosition(inputState);
		Coordinates blankTilePosition = blankTile.getCoOrdinates();

		Coordinates newBlankTilePosition = new Coordinates(); 
		switch(action){
		case MOVE_LEFT: //New Position: x_coordinate,y_coordinate - 1; Keep the row same and decrease column value
			newBlankTilePosition.setX_coordinate(blankTilePosition.getX_coordinate());
			newBlankTilePosition.setY_coordinate(blankTilePosition.getY_coordinate() - 1);
			break;
		case MOVE_RIGHT://New Position: x_coordinate,y_coordinate + 1; Keep the row same and increase column value
			newBlankTilePosition.setX_coordinate(blankTilePosition.getX_coordinate());
			newBlankTilePosition.setY_coordinate(blankTilePosition.getY_coordinate() + 1);
			break;
		case MOVE_UP://New Position: x_coordinate - 1,y_coordinate; Keep the column same and decrease row value
			newBlankTilePosition.setX_coordinate(blankTilePosition.getX_coordinate() - 1);
			newBlankTilePosition.setY_coordinate(blankTilePosition.getY_coordinate());
			break;
		case MOVE_DOWN://New Position: x_coordinate + 1,y_coordinate; Keep the column same and increase row value
			newBlankTilePosition.setX_coordinate(blankTilePosition.getX_coordinate() + 1);
			newBlankTilePosition.setY_coordinate(blankTilePosition.getY_coordinate());
			break;
		}

		if(POSSIBLE_COORDINATES.contains(newBlankTilePosition)){
			//Create New State identical to Input State
			newState = new Tile[inputState.length][inputState.length];
			for(int i = 0;i < inputState.length;i++){
				for(int j = 0;j < inputState[i].length;j++){
					Tile tempTile = inputState[i][j];
					newState[i][j] = new Tile(tempTile.getLabel(),tempTile.getCoOrdinates());
				}
			}

			int blankTileOldXPos = blankTilePosition.getX_coordinate();
			int blankTileOldYPos = blankTilePosition.getY_coordinate();

			int blankTileNewXPos = newBlankTilePosition.getX_coordinate();
			int blankTileNewYPos = newBlankTilePosition.getY_coordinate();

			//Set the coordinates of new blank to new position
			Tile newBlankTile = newState[blankTileOldXPos][blankTileOldYPos];
			newBlankTile.setCoOrdinates(newBlankTilePosition);

			//Get the adjacent tile to blank tile which will be swapped with the blank tile and set its new coordinates
			Tile adjacentTileToBlankTile = newState[blankTileNewXPos][blankTileNewYPos];
			adjacentTileToBlankTile.setCoOrdinates(new Coordinates(blankTileOldXPos, blankTileOldYPos));

			//Set the adjacent tile to position of blank tile
			newState[blankTileOldXPos][blankTileOldYPos] = adjacentTileToBlankTile;

			//Finally, set blank tile to its new position
			newState[blankTileNewXPos][blankTileNewYPos] = newBlankTile;
		}

		return newState;
	}

	/**
	 * Tests whether the input Node is Goal Node or not.
	 * @param node: the input node
	 * @return: true if the input node is goal node; false otherwise
	 */
	public boolean isGoalNode(Node node){
		return GOAL_NODE.equals(node);
	}

	/**
	 * This function returns Child Node, given a Node(Parent) and Action
	 * @param node: Parent Node
	 * @param action: action to be performed on the state of parent node
	 * @return: Child Node which is generated after action is applied on Parent Node.
	 */
	public Node getChildNode(Node node,ACTION action){
		Node childNode = null;

		Tile[][] newState = getNewState(node.getSTATE(), action);
		if(newState != null){
			childNode = new Node();
			childNode.setSTATE(newState);
			childNode.setACTION(action);
			childNode.setPARENT(node);
			childNode.setNODE_LEVEL(node.getNODE_LEVEL()+1);
			childNode.setPATH_COST(node.getPATH_COST()+1);
		}
		return childNode;
	}

	/**
	 * @param node: Print the state of Node
	 */
	public static void displayNodeState(Node node){
		if(node!=null){
			Tile[][] state = node.getSTATE();
			System.out.println();
			for(Tile[] rows:state){
				for(Tile tile:rows){
					System.out.print(tile+" ");
				}

			}
		}
	}

	/**
	 * Function implementation of Depth Limited Search  
	 * @param maxDepthLimit: the maximum depth limit of tree or graph to which depth will traverse 
	 * @return: Goal Node which encapsulates State, Parent, Action and Path cost if it is found!
	 * @throws Exception: In case the explored runs out of memory or there is any other type of exception
	 */
	public Node depthLimitedSearch(int maxDepthLimit) throws Exception{
		System.out.println("\n\nDisplaying Nodes of Iterative Depth First Search for Max Depth Limit: "+maxDepthLimit);
		FRONTIER.clear();
		EXPLORED.clear();
		FRONTIER.add(INITIAL_NODE);
		Node solutionNode = null;

		outer_while:while(!FRONTIER.isEmpty()){
			Node node = FRONTIER.pop();
			EXPLORED.add(node);
			displayNodeState(node);
			if(node.getNODE_LEVEL() < maxDepthLimit){
				for(ACTION action:POSSIBLE_ACTIONS){
					Node childNode = getChildNode(node, action);
					if(isGoalNode(childNode)){
						solutionNode = childNode;
						break outer_while;
					}
					if(childNode!=null && !EXPLORED.contains(childNode)  && !FRONTIER.contains(childNode)){
						FRONTIER.push(childNode);
					}
				}
			}
		}
		return solutionNode;
	}
	
	public Node iterativeDepthFirstSearch() throws Exception{
		Node solutionNode = null;
		for(int i = 0; i < Integer.MAX_VALUE; i++){
			solutionNode = this.depthLimitedSearch(i);
			if(solutionNode!=null){
				break;
			}
		}
		return solutionNode;
	}
	
	public static void main(String...args){
		if(args!=null && args.length == 1){
			long t1 = System.currentTimeMillis();

			String inputStr = args[0];
			System.out.println("Input String: "+inputStr+"\n");
			IterativeDepthFirstSearch ids = new IterativeDepthFirstSearch();

			INITIAL_NODE = ids.createNode(inputStr,null, null, 0);
			INITIAL_NODE.setNODE_LEVEL(0);;
			FRONTIER.add(INITIAL_NODE);

			//Not setting the parent of Goal node yet as we don't know what it is. We will use IDS to find it.
			Node goalNode = ids.createNode("1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 0",null, null, null);
			GOAL_NODE = goalNode;
			try {
				Node solutionNode = ids.iterativeDepthFirstSearch();
				displayNodeState(solutionNode);
			}catch (OutOfMemoryError  e) {
				System.out.println("\nCan't find solution-Iterative Deepening DFS ran out of memory");
			}catch (Exception e) {
				System.out.println("\nCan't find solution-Iterative Deepening DFS ran out of memory");
			}
			long t2 = System.currentTimeMillis();
			System.out.println("\nTotal Running Time: "+(t2-t1)+"ms");
		}
	}
}
