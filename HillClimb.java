package Hill_climbing;
/*
 * Hill climbing search is used for coloring a map of Canada
 * where no two touching provinces can have the same color.
 *
 * This implementation of the hill climbing search is a stochastic 
 * hill climbing search that allows for sideways moves.
 * 
 * An input k is given as an argument on the command line by the user.
 * k is the number of different colours used to colour the map described by the 
 * matrix in the HillNode class;
 * 
 * 
 */

import java.util.Random;

public class HillClimb {
	
	/*note on colours and states:
	 * * A state is structured as an array of integers with a length of 13.
	 * * Each index of the array described one region
	 * {BC, AB, SK, MB, ON, QC, NB, NS, PEI, NL, NU, NT, YT}
	 * 
	 * BC - 0, AB - 1, SK - 2, MB - 3, ON - 4, QC - 5, NB - 6
	 * NS - 7, PEI - 8, NL - 9, NU - 10, NT - 11, YT - 12
	 * 	
	 * Colours are handled as arrays of integers in this implementation;
	 * The encoding of colours is done with the colourNames array, which
	 * associates the numeric indices with the colour names;
	 * 
	 * Regions are also handled throughout with their indices (as described above);
	 * Their encoding is done with the regionNames array; 
	 */
	public static  String[] regionNames = {"BC", "AB", "SK", "MB", "ON", "QC", "NB", "NS", "PEI", "NL", "NU", "NT", "YT"};
	public static String[] colourNames = {"blue", "orange", "red", "jungle", "yellow", "green",
			"purple", "indigo", "turquoise", "cyan", "maroon", "lime", "onyx"};
	public static int initialCost = 0; //static variable used by the equation in selectNeighbor()
	

	public static void main(String[] args) {		
		
		//get argument from user for the number of colors
		int k = Integer.parseInt(args[0]);
		
		if(k > 13 || k <= 1) {
			System.out.println("please use a value of 1 < k <= 13");
		}
		else {

			//define initial state
			int [] initState = createRandomState(k);
			//int[] initState = {0,1,1,1,2,2,3,3,3,3,3,3,3};  //initial state given by assignment 
			
			

			//assign initial state to a node
			HillNode currentNode = new HillNode(initState, k);	
			initialCost = currentNode.cost; //initialize the initial cost for later use in selectNeighbor()
			System.out.println("Current State: ");
			printState(currentNode);
			
					
			//set variables for hill climbing search
			Boolean goalReached = false; //used to control while loop 
			int maxIterations = 100; //to prevent infinite loops with sideways moves
			int steps = 0; //accumulator to be compared with maxIterations within loop
			
			goalReached = goalTest(currentNode);	//do initial test before loop	
			
			
			//perform hill climbing search
			//the probabilistic test is carried out in the selectNeighbor() function
			while (!goalReached && steps < maxIterations) {
				
				//select next better node (or equal in cost since sideways moves are allowed)
				currentNode = selectNeighbor(currentNode);				
				goalReached = goalTest(currentNode); //test if goal state is reached, will exit loop if so
				steps++;
				
				//print for a visualization of the process
				System.out.println("Step " + steps + ":");
				printState(currentNode);
			}		
			
			if(goalReached) {
				System.out.println("Goal State Reached!");
			}
			else {
				System.out.println("Goal not reached, the search reached the maximum number of steps permitted (100)");
			}
		}		
	}
	
	
	
	
	/* Creates a random state to start out from */	
	public static int[] createRandomState(int numColours) {
		int[] newState = new int[13]; //initialize state array to hold colours for 13 regions
		
		//randomly assign a colour to each region from a set of k different colours
		Random rand = new Random();
		for (int i = 0; i < newState.length; i++) {
			newState[i] = rand.nextInt(numColours);
		}
		
		return newState;
	}
	
	
	
	/* 
	 * The transition function for stochastic hill climbing
	 * 
	 * This function performs the generation and probabilistic selection of a new
	 * better or equal in cost neighbor to the current state with the
	 * probability of selection based on the cost of the neighbor.
	 * */
	public static HillNode selectNeighbor (HillNode currNode) {
	 
		Random rand = new Random(); 
		Boolean neighborSelected = false;
		
		//initialize neighbor to current state, will change one colour
		//for each generated neighbor and then select neighbor
		//based on probability
		HillNode neighbor = new HillNode(currNode.state, currNode.k);
				
		while (!neighborSelected)
		{			
			//create a random  neighbor until one is selected based on probability
			//associated with its cost
			//randomly select a region and change its colour 
			neighbor = new HillNode(currNode.state, currNode.k); 
			int randRegion = rand.nextInt(currNode.state.length);			
			int newColour = rand.nextInt(currNode.k);
			
			//ensure a new colour is selected (i.e. it is actually a new state)
			while(newColour == neighbor.state[randRegion])
				newColour = rand.nextInt(currNode.k);
			
			neighbor.setRegion(randRegion, newColour);  //what makes the neighbor different from the current state
			
			
			
			/*
			 * determine if this neighbor will be selected based on probability associated with its cost
			 * compared to the cost of the current node;
			 * This also allows for SIDEWAYS movement (neighbor's cost can be <= rather than < compared to current)
			 * 
			 *  The equation used for probabilistic selection generates a higher probability of a node
			 * 	being selected if there is a greater change between its cost and the current node's cost (to a lower cost).
			 *  Bigger jumps are given a higher probability of occurring.
			 *  
			 *  T starts at 10 and decreases as the cost gets smaller, closer to the goal, meaning that the size 
			 *  of steps prioritized decreases as the search gets closer to the solution; Larger
			 *  steps are expected at the beginning of the search.
			 * 
			 *  A randomly generated number between 0 and 1 will have to be larger than p
			 *  for the neighbor node to be selected/accepted.
			 * 
			 */ 
			if(neighbor.cost <= currNode.cost) {
				double T = currNode.cost*10/initialCost;
				double p = 1 / (1 + Math.exp((currNode.cost - neighbor.cost) / T));
				if (Math.random() > p) {
			 		neighborSelected = true;
			 	}
			}
			
		}			
	
		return neighbor;
	}
	
	
	
	/*
	 * Test if the goal has been reached (heuristic value is 0)
	 * h(s) = 0
	 */
	public static Boolean goalTest (HillNode node) {
		if (node.heuristic == 0 )
			return true;
		else return false;
	}
	
	
	/* 
	 * print colour of each region in state, along with the cost and heuristic
	 */
	public static void printState(HillNode node) {
		
		System.out.println("Cost: " + node.cost);
		System.out.println("Heuristic: " + node.heuristic);
		
		for(int i = 0; i < node.state.length; i++) {
			System.out.print(regionNames[i] + " : " + colourNames[node.state[i]] + "  |  ");
		}
		System.out.println("\n\n");
	}

}
