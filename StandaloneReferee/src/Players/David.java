package Players;

import java.util.ArrayList;

import Utilities.Move;
import Utilities.StateTree;
import Utilities.BoardCJR;


/**
 * This is an example of how to make a player.
 * This player is extremely simple and does no tree building
 * but its good to test against at first.
 * 
 * @author Ethan Prihar
 *
 */
public class David extends Player
{
	
	public David(String n, int t, int l)
	{
		super(n, t, l);
	}

	public Move getMove(StateTree state)
	{
		
		state.children = new ArrayList<StateTree>();
		int maxDepth = 7;
		
		state.children = populateChildren(state);
		int[] moveValues = new int[state.children.size()];
		ArrayList<Move> possibleMoves = getAvailableMoves(state);
		for (int i = 0; i < moveValues.length; i++) {
			moveValues[i] = (state.turn == 1 ? Integer.MIN_VALUE : Integer.MAX_VALUE);
		}
		
		for (int i = 0; i < state.children.size(); i++) {
			
			moveValues[i] = (state.turn == 1 ? getMax(state.children.get(i), maxDepth, 1, Integer.MIN_VALUE, Integer.MAX_VALUE) : 
				getMin(state.children.get(i), maxDepth, 1, Integer.MIN_VALUE, Integer.MAX_VALUE));
		}
		Move bestMove = possibleMoves.get(0);
		if (state.turn == 1) {
			int maxValue = moveValues[0];
			for (int i = 0; i < possibleMoves.size(); i++) {
				if (moveValues[i] > maxValue) {
					bestMove = possibleMoves.get(i);
					maxValue = moveValues[i];
				}
			}
		}
		else {
			int minValue = moveValues[0];
			for (int i = 0; i < possibleMoves.size(); i++) {
				if (moveValues[i] < minValue) {
					bestMove = possibleMoves.get(i);
					minValue = moveValues[i];
				}
			}
		}
		return bestMove;
	}
	
	public int getMax(StateTree state, int m, int d, int a, int b) {
		if (m <= d) {
			return evalBoard(state);
		}
		else {
			int max = Integer.MIN_VALUE;
			state.children = populateChildren(state);
			for (int i = 0; i < state.children.size(); i++) {
				int value = getMin(state.children.get(i), m, d + 1, a, b);
				if (value > max) { max = value; }
			}
			//if (max >= b) { return max; }
			//a = (a > max ? a : max);
			return max;
		}
	}
	
	public int getMin(StateTree state, int m, int d, int a, int b) {
		if (m <= d) {
			return evalBoard(state);
		}
		else {
			int min = Integer.MAX_VALUE;
			state.children = populateChildren(state);
			for (int i = 0; i < state.children.size(); i++) {
				int value = getMax(state.children.get(i), m, d + 1, a, b);
				if (value < min) { min = value; }
			}
			//if (min <= a) { return min; }
			//b = (b < min ? b : min);
			return min;
		}
	}
	
	public ArrayList<StateTree> populateChildren(StateTree state) {
		ArrayList<Move> possibleMoves = getAvailableMoves(state);
		ArrayList<StateTree> children = new ArrayList<StateTree>();
		for (Move m: possibleMoves) {
			BoardCJR child = new BoardCJR(state);
			child.makeMove(m);
			children.add(child);
		}
		return children;
	}
	
	
	public ArrayList<Move> getAvailableMoves(StateTree state) {
		ArrayList<Move> moves = new ArrayList<Move>();
		BoardCJR board = new BoardCJR(state);
		for (int i = 0; i < board.columns; i++) {
			Move move = new Move(false, i);
			if (board.validMove(move)) {
				moves.add(move);
			}
			if (board.turn == 1 && board.pop1) {
				move = new Move(true, i);
				if (board.validMove(move)) {
					moves.add(move);
				}
			}
			else if (board.turn == 2 && board.pop2) {
				move = new Move(true, i);
				if (board.validMove(move)) {
					moves.add(move);
				}
			}
		}
		return moves;
	}
	
	public int evalBoard(StateTree state) {
		int utility = 0;
		for (int x = 0; x < state.columns; x++) {
			for (int y = state.rows - 1; y >= 0; y--) {
				if (anyAdjacent(state, y, x) && state.boardMatrix[y][x] != 0) {
					utility += evalCell(state, state.boardMatrix[y][x],  y, x);
				}
			}
		}
		//System.out.println("Caleb's utility = " + utility);
		return utility;
	}
	
	public boolean anyAdjacent(StateTree state, int row, int col) {
		boolean bl = false, b = false, br = false, r = false, tr = false, t = false, tl = false, l = false;
		if (row == 0) { br = true; b = true; bl = true; }
		if (row == state.rows - 1) { t = true; tr = true; tl = true; }
		if (col == 0) { l = true; bl = true; tl = true; }
		if (col == state.columns - 1) { r = true; tr = true; br = true; }
		if (!bl) { if (state.boardMatrix[row - 1][col - 1] == 0) { return true; }}
		if (!b) { if (state.boardMatrix[row - 1][col] == 0) { return true; }}
		if (!br) { if (state.boardMatrix[row - 1][col + 1] == 0) { return true; }}
		if (!tr) { if (state.boardMatrix[row + 1][col + 1] == 0) { return true; }}
		if (!t) { if (state.boardMatrix[row + 1][col] == 0) { return true; }}
		if (!tl) { if (state.boardMatrix[row + 1][col - 1] == 0) { return true; }}
		if (!l) { if (state.boardMatrix[row][col - 1] == 0) { return true; }}
		if (!r) { if (state.boardMatrix[row][col + 1] == 0) { return true; }}
		return false;
		
	}
	
	public int evalCell(StateTree state, int turn, int r, int c) {
		int score = 0;
		score += evaluatePartition(state, turn, getVert(state, turn, r, c));
		score += evaluatePartition(state, turn, getHori(state, turn, r, c));
		score += evaluatePartition(state, turn, getDiagUp(state, turn, r, c));
		score += evaluatePartition(state, turn, getDiagDown(state, turn, r, c));
		//System.out.println("(" + r + ", " + c + ") = " + score);
		return score;
	}
	
	public int evaluatePartition(StateTree state, int turn, int[] partition) {
		int sum = 0;
		if (partition.length < state.winNumber) {
			return sum;
		}
		int count;
		for (int i = 0; i <= partition.length - state.winNumber; i++) {
			count = 0;
			for (int j = 0; j < state.winNumber; j++) {
				count += partition[i + j];
			}
			if (turn == 2) { count /= 2; }
			else { sum += Math.pow(count, 3); }
		}
		if (turn == 2) { return -sum; }
		else { return sum; }
	}
	
	public int[] getVert(StateTree state, int turn, int r, int c) {
		int[] vert;
		int count = 0;
		int[] top = new int[state.winNumber - 1];
		int[] bottom = new int[state.winNumber - 1];
		for (int i = 0; i < top.length; i ++) {
			top[i] = -1;
			bottom[i] = -1;
		}
		for (int i = r - 1; i >= 0 && r - i + 1 < state.winNumber - 1; i--) {
			if (state.boardMatrix[i][c] == turn || state.boardMatrix[i][c] == 0) {
				bottom[r - i - 1] = state.boardMatrix[i][c];
				count++;
			}
			else { break; }
		}
		for (int i = r + 1; i < state.rows && i - r - 1< state.winNumber - 1; i++) {
			if (state.boardMatrix[i][c] == turn || state.boardMatrix[i][c] == 0) {
				top[i - r - 1] = state.boardMatrix[i][c];
				count++;
			}
		}
		
		vert = new int[count + 1];
		count = 0;
		for (int i = bottom.length - 1; i >= 0; i--) {
			if (bottom[i] == -1) { continue; }
			else {
				vert[count] = bottom[i];
				count ++;
			}
		}
		vert[count] = turn;
		count++;
		for (int i = 0; i < top.length; i++) {
			if (top[i] != -1) {
				vert[count] = top[i];
				count++;
			}
			else { break; }
		}
		
		return vert;
	}
	
	public int[] getHori(StateTree state, int turn, int r, int c) {
		int[] hori;
		int count = 0;
		int[] left = new int[state.winNumber - 1];
		int[] right = new int[state.winNumber - 1];
		for (int i = 0; i < left.length; i ++) {
			left[i] = -1;
			right[i] = -1;
		}
		
		for (int i = c - 1; i >= 0 && c - i + 1 < state.winNumber - 1; i--) {
			if (state.boardMatrix[r][i] == turn || state.boardMatrix[r][i] == 0) {
				left[c - i - 1] = state.boardMatrix[r][i];
				count++;
			}
			else { break; }
		}
		for (int i = c + 1; i < state.columns && i - c - 1< state.winNumber - 1; i++) {
			if (state.boardMatrix[r][i] == turn || state.boardMatrix[r][i] == 0) {
				right[i - c - 1] = state.boardMatrix[r][i];
				count++;
			}
		}
		hori = new int[count + 1];
		count = 0;
		for (int i = left.length - 1; i >= 0; i--) {
			if (left[i] == -1) { continue; }
			else {
				hori[count] = left[i];
				count ++;
			}
		}
		hori[count] = turn;
		count++;
		for (int i = 0; i < right.length; i++) {
			if (right[i] != -1) {
				hori[count] = right[i];
				count++;
			}
			else { break; }
		}
		
		return hori;
	}
	
	public int[] getDiagUp(StateTree state, int turn, int r, int c) {
		int[] diagUp;
		int count = 0;
		int[] up = new int[state.winNumber - 1];
		int[] down = new int[state.winNumber - 1];
		for (int i = 0; i < up.length; i ++) {
			up[i] = -1;
			down[i] = -1;
		}
		
		for (int y = r - 1, x = c - 1, i = 0; y >= 0 && x >= 0 && i < state.winNumber - 1; y--, x--, i++) {
			if (state.boardMatrix[y][x] == turn || state.boardMatrix[y][x] == 0) {
				down[i] = state.boardMatrix[y][x];
				count++;
			}
			else { break; }
		}
		
		for (int y = r + 1, x = c + 1, i = 0; y < state.rows && x < state.columns && i < state.winNumber - 1; y++, x++, i++) {
			if (state.boardMatrix[y][x] == turn || state.boardMatrix[y][x] == 0) {
				up[i] = state.boardMatrix[y][x];
				count++;
			}
			else { break; }
		}
		
		diagUp = new int[count + 1];
		count = 0;
		for (int i = down.length - 1; i >= 0; i--) {
			if (down[i] == -1) { continue; }
			else {
				diagUp[count] = down[i];
				count ++;
			}
		}
		diagUp[count] = turn;
		count++;
		for (int i = 0; i < up.length; i++) {
			if (up[i] != -1) {
				diagUp[count] = up[i];
				count++;
			}
			else { break; }
		}
		
		return diagUp;
	}
	
	public int[] getDiagDown(StateTree state, int turn, int r, int c) {
		int[] diagDown;
		int count = 0;
		int[] up = new int[state.winNumber - 1];
		int[] down = new int[state.winNumber - 1];
		for (int i = 0; i < up.length; i ++) {
			up[i] = -1;
			down[i] = -1;
		}
		
		for (int y = r - 1, x = c + 1, i = 0; y >= 0 && x < state.columns && i < state.winNumber - 1; y--, x++, i++) {
			if (state.boardMatrix[y][x] == turn || state.boardMatrix[y][x] == 0) {
				down[i] = state.boardMatrix[y][x];
				count++;
			}
			else { break; }
		}
		
		for (int y = r + 1, x = c - 1, i = 0; y < state.rows && x >= 0 && i < state.winNumber - 1; y++, x--, i++) {
			if (state.boardMatrix[y][x] == turn || state.boardMatrix[y][x] == 0) {
				up[i] = state.boardMatrix[y][x];
				count++;
			}
			else { break; }
		}
		
		diagDown = new int[count + 1];
		count = 0;
		for (int i = up.length - 1; i >= 0; i--) {
			if (up[i] == -1) { continue; }
			else {
				diagDown[count] = up[i];
				count ++;
			}
		}
		diagDown[count] = turn;
		count++;
		for (int i = 0; i < down.length; i++) {
			if (down[i] != -1) {
				diagDown[count] = down[i];
				count++;
			}
			else { break; }
		}
		
		return diagDown;
	}
	
}