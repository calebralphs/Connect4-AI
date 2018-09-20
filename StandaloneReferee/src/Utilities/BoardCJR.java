package Utilities;

public class BoardCJR extends StateTree {
	
	public int alpha;
	public int beta;
	public BoardCJR(int r, int c, int w, int t, boolean p1, boolean p2, StateTree p) {
		super(r, c, w, t, p1, p2, p);
	}
	
	//  constructor to convert state to our child class using deep memory clone
	public BoardCJR(StateTree state) {
		super(state.rows, state.columns, state.winNumber, state.turn, state.pop1, state.pop2, state.parent);
		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < this.columns; j++) {
				this.boardMatrix[i][j] = state.boardMatrix[i][j];
			}
		}
		this.parent = state.parent;
	}
	
	// override parent function to remove output
	public boolean validMove(Move move)
	{
		if(move.column >= columns || move.column < 0)
		{
			return false;
		}
		if(!move.pop && boardMatrix[rows-1][move.column] != 0)
		{
			return false;
		}
		if(move.pop)
		{
			if(boardMatrix[0][move.column] != turn)
			{
				return false;
			}
			if((turn == 1 && pop1) || (turn == 2 && pop2))
			{
				return false;
			}
		}
		return true;
	}

}
