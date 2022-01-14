package Hill_climbing;
/*
 * The HillNode class defines the node
 * objects used to represent the state in a local hill climbing search.
 * 
 * It has parameters of its state, calculated heuristic value, cost and an array that defines
 * the costs associated with each color depending on the state;
 * 
 * the value of k is the number of colours used to colour the map
 * 
 * It uses an adjacency matrix and its state to calculate a heuristic value and cost based
 * on adjacency and colour sharing; 
 * 
 *  */

public class HillNode {
	
	//define the adjacency matrix for the map, this will
	//be used by the calculateHeuristic and calculateCost functions
	public static int[][] adj_matrix = { 
					{1,1,0,0,0,0,0,0,0,0,0,1,1},		
					{1,1,1,0,0,0,0,0,0,0,0,1,0},
					{0,1,1,1,0,0,0,0,0,0,0,1,0},
					{0,0,1,1,1,0,0,0,0,0,1,0,0},
					{0,0,0,1,1,1,0,0,0,0,0,0,0},
					{0,0,0,0,1,1,1,0,0,1,0,0,0},
					{0,0,0,0,0,1,1,1,1,0,0,0,0},
					{0,0,0,0,0,0,1,1,1,0,0,0,0},
					{0,0,0,0,0,0,1,1,1,0,0,0,0},
					{0,0,0,0,0,1,0,0,0,1,0,0,0},
					{0,0,0,1,0,0,0,0,0,0,1,1,0},
					{1,1,1,0,0,0,0,0,0,0,1,1,1},
					{1,0,0,0,0,0,0,0,0,0,0,1,1}
			};

	public int[] state;
	public int heuristic;
	public int cost;
	public int k; //number of colours in the state
	int[] colour_costs; // array to hold cost for each colour
	

	/*
	 * Constructor for HillNode
	 */
	public HillNode(int [] st, int colours) {
		this.state = st;
		this.k = colours;
		colour_costs = new int[k];
		this.calculateHeuristic();
		this.calculateCost();		
	}
	
	/* set the colour of a region for a node - used when generating a neighbor 
	 in selectNeighbor of the HillClimb class*/
	public void setRegion(int region, int colour) {
		this.state[region] = colour;
		//update heuristic and cost
		this.calculateHeuristic();
		this.calculateCost();
	}
	
	
	/*
	 * Heuristic function
	 * 
	 * calculate the number of adjacent regions sharing the same colour
	 * for this state;
	 */
	private void calculateHeuristic() {
		//loop through top half (above diagonal) of adjacency matrix
		//of the map and sum where there are adjacencies with the same color;
		
		int adjSum = 0; //accumulator for adjacencies
		
		for (int r = 0; r < adj_matrix[0].length; r++) {
			for (int c = r+1; c < adj_matrix[0].length; c++) {
				if (adj_matrix[r][c] == 1 && this.state[r] == this.state[c]) { //adjacent and colour match					
					adjSum++;
				}					
			}
		}
		
		this.heuristic = adjSum;
	}
	
	/*
	 * Calculates the cost of a state
	 * 
	 * -->step 1: the function calculates the cost for each individual colour, which
	 * depends on whether the colour is used in the map (value of 1), and how many adjacencies
	 * there are for that colour (increment for each adjacency)
	 * --> step 2: the function sums the costs of the state by adding the cost
	 * of each colour used for each region
	 */
	private void calculateCost() {
		
		//first initialize colour_cost array to give a value of 1
		//to each colour currently present on the map; this is part of step 1
		Boolean onMap = false;
		
		for (int colour = 0; colour < k; colour++) {
			onMap = false; //initialize to false for each colour
			
			for (int j = 0; j < 13; j++) { //13 for size of map = 13 regions
				
				if (this.state[j] == colour) {
					onMap = true;
				}
			if (onMap) colour_costs[colour] = 1;
			}
		}
			
			
		
		//go through top half of matrix, and sum the adjacent regions 
		//that share each colour; this is part of step 1; 
		//calculate the cost of each colour
		int colourSum = 0; //accumulator variable for each colour
		
		for(int colour = 0; colour < k; colour++ ) {
			
			colourSum = 0; //re-set to 0 for each colour
			
			for (int r = 0; r < adj_matrix[0].length; r++) {
				for (int c = r+1; c < adj_matrix[0].length; c++) {
					
					if (adj_matrix[r][c] == 1 && this.state[r] == colour && this.state[r] == this.state[c] ) { //adjacent and colour match					
						colourSum++;
					}					
				}
			}
			//add to cost for each colour to the colour_costs array
			colour_costs[colour] += colourSum;
		}
		
		
		//sum the cost for the state (step 2) applying the costs of each colour
		int totalCost = 0; //accumulator for total cost calculation
		int regionColour; //will hold the colour for each region
		for (int r = 0; r < adj_matrix[0].length; r++) {
			//get the colour for the region
			regionColour = this.state[r];
			totalCost += colour_costs[regionColour];
		}
		
		//update the cost parameter
		this.cost = totalCost;
	}
	
	
}
