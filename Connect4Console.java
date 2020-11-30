
import java.util.*;
import java.lang.Object;

/**
 * This AI performs 
 */
public class Connect4Console extends AIModule
{
	private int firstLevel = 0;
	public int[] Weights = new int[] {1, 5, 100, 10000, 2, 6, 200, 15000};
	private int number;
	private int CutLevel = 6;
	
	public void getNextMove(final GameStateModule game)
	{
		number = game.getActivePlayer();
		StateType state = new StateType(game);
		state.Turn = number;
		if (game.getCoins()==0)
			chosenMove = 3;
		else
			chosenMove = AlphaBetaSearch(state);
		System.out.println(state.MoveCount+"MC");
		System.out.println("chosenMove move is " + chosenMove + " and the flag is " + terminate);
	}
	
	private int AlphaBetaSearch(StateType state)
	{		
		state.Succesors = Succesors(state);
		firstLevel = state.MoveCount;
		
		int j = MaxValue(state, Integer.MIN_VALUE, Integer.MAX_VALUE);
		
		for(Integer a : state.Succesors.keySet())
		{
		
			if (state.Succesors.get(a).V == j)
				return a;
		}
		return -1;
	}
		private int MaxValue(StateType state, int alpha, int beta)
	{
		if (CutOffTest(state)) return state.V;
		state.V = Integer.MIN_VALUE;
		HashMap<Integer, StateType> succ = (state.Succesors != null) ? state.Succesors : Succesors(state);
		for (Integer a : succ.keySet()) {
			state.V = Math.max(state.V, MinValue(succ.get(a), alpha, beta));
			if (state.V >= beta) return state.V;
			alpha = Math.max(alpha, state.V);
		}
		return state.V;
	}
	
	private int MinValue(StateType state, int alpha, int beta)
	{
		if (CutOffTest(state)) return Eval(state.Values);
		state.V = Integer.MAX_VALUE;
		HashMap<Integer, StateType> succ = (state.Succesors != null) ? state.Succesors : Succesors(state);
		for (Integer a : succ.keySet())
		{
			state.V = Math.min(state.V, MaxValue(succ.get(a), alpha, beta));
			if (state.V <= alpha) return state.V;
			beta = Math.min(beta, state.V);
		}
		return state.V;
	}

	
	private HashMap<Integer, StateType> Succesors(StateType state)
	{
		HashMap<Integer, StateType> succ = new HashMap<Integer, StateType>(7);
		
		for (int i = 0; i <= 6; i++)
		{
			// if(state.state.canMakeMove(i))
			// {
				//StateType temp = new StateType(state);
				
				StateType st = state.Move(i);
				
				
			// check the move is valid
				if (st != null) succ.put( i, st);					
			// }
			
		}
		return succ;
	}
	
	/*public void debug2d(int state[][])
	{
		for (int j = 0; j < 6; j++){
		for (int i = 0; i < 7; i++) {
			
				System.out.print(state[i][j] + " ");
			}
			System.out.println("rows: " + j);
		}
	} */
	
	private boolean CutOffTest(StateType state)
	{
		//System.out.println(state.MoveCount+"MC"+firstLevel+"first"+(state.MoveCount - firstLevel) + "movecount");
		state.V = Eval(state.Values);
		
		if (Math.abs(state.V) > 5000) return true;
		// if (state.V > 5000) return true;
		if ((state.MoveCount - firstLevel) > 6) return true;
		return false; 
	} 
	
	private int Eval(int[][] state)
	{
		int score = 0;		
		// Eval Horizontal
		for (int i = 0; i < 6; i++) {
			int [] temp = new int[] {state[0][i], state[1][i], state[2][i], state[3][i], state[4][i], state[5][i], state[6][i]};
			// System.out.println("Horizontal");
			// debug(temp);
			score += CheckLine(temp);
		}
		
		// Eval Vertical
		for(int i = 0; i < 7; i ++)
		{
			int [] temp = new int[] {state[i][0], state[i][1], state[i][2], state[i][3], state[i][4], state[i][5]};
			score += CheckLine(temp);
		}
		
		int[] temp1 = new int[] {state[0][2], state[1][3], state[2][4], state[3][5]};
		int[] temp2 = new int[] {state[0][1], state[1][2], state[2][3], state[3][4], state[4][5]};
		int[] temp3 = new int[] {state[0][0], state[1][1], state[2][2], state[3][3], state[4][4], state[5][5]};
		int[] temp4 = new int[] {state[1][0], state[2][1], state[3][2], state[4][3], state[5][4], state[6][5]};
		int[] temp5 = new int[] {state[2][0], state[3][1], state[4][2], state[5][3], state[6][4]};
		int[] temp6 = new int[] {state[3][0], state[4][1], state[5][2], state[6][3]};
		
		int[] temp7  = new int[] {state[3][0], state[2][1], state[1][2], state[0][3]};
		int[] temp8  = new int[] {state[4][0], state[3][1], state[2][2], state[1][3], state[0][4]};
		int[] temp9  = new int[] {state[5][0], state[4][1], state[3][2], state[2][3], state[1][4], state[0][5]};
		int[] temp10 = new int[] {state[6][0], state[5][1], state[4][2], state[3][3], state[2][4], state[1][5]};
		int[] temp11 = new int[] {state[6][1], state[5][2], state[4][3], state[3][4], state[2][5]};
		int[] temp12 = new int[] {state[6][2], state[5][3], state[4][4], state[3][5]};
		
		// Eval Diagonal
		score += CheckLine(temp1);
		score += CheckLine(temp2);
		score += CheckLine(temp3);
		score += CheckLine(temp4);
		score += CheckLine(temp5);
		score += CheckLine(temp6);

		// Eval Digonal 2
		score += CheckLine(temp7);
		score += CheckLine(temp8);
		score += CheckLine(temp9);
		score += CheckLine(temp10);
		score += CheckLine(temp11);
		score += CheckLine(temp12);

		//System.out.println("here is the Eval score: " + score);
		
		return score;
	}
	

	
	// Iterate through every possibility (4 fields)
	private int CheckLine(int[] vals)
	{
		// debug(vals);
		int score = 0;
		for (int i = 0; i < (vals.length - 3); i++) {
			//Examine each opportunity
			int c = 0;
			int p = 0;
			for (int j = 0; j < 4; j++) {
				if (vals[i + j] == number) c++;
				else if (vals[i + j] != 0) p++;
			}
			if ((c > 0) && (p == 0)) {
				// Computer opportunity
				
				if (c == 4) return Weights[3]; // win
				score += ((c/3)*Weights[2]) + ((c/2)*Weights[1]) + Weights[0];
				// System.out.println("here is the Computer opportunity " + score);
			}
			else if ((c == 0)  && (p > 0))
			{
				// Player opportunity
				if (p == 4) return -1*Weights[7];
				score -= ((p / 3) * Weights[6]) + ((p / 2) * Weights[5]) + Weights[4];
				// System.out.println("here is the Player opportunity " + score);
			}
		}
		return score;
	}
}	


class StateType 
{
	HashMap<Integer, StateType> Succesors;
	public int[][] Values;
	public int Turn;
	public int[] RowCounts;
	GameStateModule state;
	int V;
	int MoveCount;
	
	public StateType(GameStateModule state)
	{
		this.state = state;
		Values = new int[7][6];
		RowCounts = new int[7];
		updateBoard();
	}
	
	private void updateBoard()
	{
		this.MoveCount = state.getCoins();
		// this.Turn = state.getActivePlayer();
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 6; j++) {
				this.Values[i][j] = state.getAt(i,j);
				//System.out.print(this.Values[i][j] + " ");
			}
			//System.out.println( "at " + i + " column");
		}
		
		for (int i = 0; i < 7; i++) {
			this.RowCounts[i] = state.getHeightAt(i);
			//System.out.print(this.RowCounts[i] + " ");
		}
		//System.out.println();
	}
		
	public StateType Move(int M)
	{
		StateType st = this.copystate(); // clone current state
		 int m = M ;
		
		if (st.RowCounts[m] == 6)
		{ 
			//System.out.println("herre is move RowCounts is " + st.RowCounts[m] + " and count is " + st.MoveCount);
			return null; //If column is full, return null. Illegal action
		}
		
		//System.out.println("height here" + st.RowCounts[m]);
		
		st.Values[m][st.RowCounts[m]] = st.Turn; //Update board
		st.Turn = (st.Turn == 1) ? 2:1; // change turn
		st.MoveCount++;
		st.RowCounts[m]++;
		return st;
	}
	
	public StateType copystate()
	{
		StateType game = new StateType();
		game.state = this.state.copy();
		for (int i = 0; i < 7; i++) {                         
			System.arraycopy(this.Values[i], 0, game.Values[i], 0, this.Values[i].length); 
		}
		game.RowCounts = (int[]) this.RowCounts.clone();
		game.Turn = this.Turn;
		game.MoveCount = this.MoveCount;
		
		return game;
	}
	
	public StateType()
	{
		Values = new int[7][6];
		RowCounts = new int[7];
		MoveCount = 0;
	}
}
