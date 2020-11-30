import java.util.*;

public class alphabeta_SuperBlade extends AIModule
{
	private int startLevel = 0;
	private int number;
	private int CutLevel = 6; //search the further 6 level to find the value.
	public int[] Weight = new int[] {1, 5, 100, 10000, 2, 6, 200, 15000}; //set the weight when checking the lines, to count the score.

	public void getNextMove(final GameStateModule game)
	{
		number = game.getActivePlayer();
		StateType state = new StateType(game);
		state.Turn = number;
		if (game.getCoins()==0)
			chosenMove = 3;	//when there is no coin on board, choose the column 4.
		else
			chosenMove = AlphaBetaSearch(state);
	}
	
	private int AlphaBetaSearch(StateType state)
	{		
		state.Succesors = Succesors(state);
		startLevel = state.MoveCount;
		
		int j = MaxValue(state, Integer.MIN_VALUE, Integer.MAX_VALUE);
		
		for(Integer a : state.Succesors.keySet())	//search the integer in the hashmap to figure out the object.
		{
			if (state.Succesors.get(a).V == j)
				return a;
		}
		return -1;
	}
	
	private int MaxValue(StateType state, int alpha, int beta)
	{
		if (CutOff(state)) return state.V;	// when cutting off, stop searching and return the value.
		state.V = Integer.MIN_VALUE;
		HashMap<Integer, StateType> succ = (state.Succesors != null) ? state.Succesors : Succesors(state);
		for (Integer a : succ.keySet()) {	//search the hashmap of the current states to get the maximum one.
			state.V = Math.max(state.V, MinValue(succ.get(a), alpha, beta));
			if (state.V >= beta) return state.V;
			alpha = Math.max(alpha, state.V);
		}
		return state.V;
	}
	
	private int MinValue(StateType state, int alpha, int beta)
	{
		if (CutOff(state)) return Eval(state.Values);
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
				StateType st = state.Move(i);
			// check the move is valid
				if (st != null) succ.put( i, st);					
		}
		return succ;
	}
	

	
	private boolean CutOff(StateType state)
	{
		state.V = Eval(state.Values);
		if (terminate) return true; //when time's up, cut off.
		if (Math.abs(state.V) > 5000) return true; // when find the win state, cut off.
		if ((state.MoveCount - startLevel) > 6) return true; // when search to the level 6, cut off.
		return false; 
	} 
	
	private int Eval(int[][] state)
	{
		int score = 0;		
		// Eval Horizontal
		for (int i = 0; i < 6; i++) {
			int [] temp = new int[] {state[0][i], state[1][i], state[2][i], state[3][i], state[4][i], state[5][i], state[6][i]};
			score += CheckFunction(temp);
		}
		
		// Eval Vertical
		for(int i = 0; i < 7; i ++)
		{
			int [] temp = new int[] {state[i][0], state[i][1], state[i][2], state[i][3], state[i][4], state[i][5]};
			score += CheckFunction(temp);
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
		score += CheckFunction(temp1);
		score += CheckFunction(temp2);
		score += CheckFunction(temp3);
		score += CheckFunction(temp4);
		score += CheckFunction(temp5);
		score += CheckFunction(temp6);

		// Eval Digonal 2
		score += CheckFunction(temp7);
		score += CheckFunction(temp8);
		score += CheckFunction(temp9);
		score += CheckFunction(temp10);
		score += CheckFunction(temp11);
		score += CheckFunction(temp12);
		
		return score;
	}
	
	// Iterate through every possibility (4 fields)
	private int CheckFunction(int[] vals)
	{
		int score = 0;
		for (int i = 0; i < (vals.length - 3); i++) {
			//Examine each opportunity
			int computer = 0;
			int player = 0;
			for (int j = 0; j < 4; j++) {
				if (vals[i + j] == number) computer++;
				else if (vals[i + j] != 0) player++;
			}
			if ((computer > 0) && (player == 0)) {
				// AI opportunity
				if (computer == 4) return Weight[3]; // win
				score += ((computer/3)*Weight[2]) + ((computer/2)*Weight[1]) + Weight[0];
			}
			else if ((computer == 0)  && (player > 0))
			{
				// Player opportunity
				if (player == 4) return -1*Weight[7];
				score -= ((player / 3) * Weight[6]) + ((player / 2) * Weight[5]) + Weight[4];
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
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 6; j++) {
				this.Values[i][j] = state.getAt(i,j);
			}
		}
		
		for (int i = 0; i < 7; i++) {
			this.RowCounts[i] = state.getHeightAt(i);
		}
	}
		
	public StateType Move(int M)
	{
		StateType st = this.copystate(); // copy current state
		 int m = M ;
		
		if (st.RowCounts[m] == 6)
		{ 
			return null; //If column is full, return null. Illegal action
		}
		
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
