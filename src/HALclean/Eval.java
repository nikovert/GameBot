package HALclean;

import reversi.Coordinates;

public class Eval {
	
	StaticAnalysis anal; //need to come up with a better name ;)
	protected Eval(){
		anal = new StaticAnalysis();
	}
	
  
	/**
	 * Evaluates the board state
	 * @param player
	 * @return the value of the current board state
	 */
  protected int evaluate(BitBoard board, int player){
	  return getWeight(board, player);
  }
  

	/**
	 * 
	 * @param the Gameboard gb
	 * @param the current move (that we plan on making)
	 * @return how good the move is (+inf == very good, -inf == shit)
	 */
	private int getWeight(BitBoard board, int player) {

		//number of disc advantage
		int stones = (board.countStones(player)-board.countStones(2-player));
		
		//static Analysis advantage
		int sA =  anal.staticAnalysis(board.lastmove);
		
		//mobility advantage
		board.generate_all(player);
		int mobility = board.getAllMoves().length;
				
		//calculate Coefficients 
		double dA = -0.5;
		
		//if early game
		if((board.disks_played+4) < 40){
			dA = -2*dA;
		}
		
		//if end game
		if((board.disks_played+4) > 55){
			dA = 3*dA;
		}
	
		
		//System.out.println("BitHAL: stones: " + stones + " sa: " + sA + " mobility: " + mobility);
		
		return (int) (sA + stones * dA + mobility);
		
	}
	
	
	/**
	 * Checks if a corner move is possible
	 * @return the corner, or null if no corner move is possible
	 */
	/*
	protected Coordinates checkCorners(BitBoard board, int player) {
		Coordinates result = null;
		int max = 0, newmax = 0;
		
		if(board.checkMove(player, new Coordinates(1, 1)))
			result = new Coordinates(1, 1);
		
		if(board.checkMove(player, new Coordinates(1, 8)))
			if(result != null){
				//GameBoard test = board.clone();
				board.checkMove(player, result);
				board.makeMove(player, result);
				try{
					max = HAL.max(board.clone(), System.currentTimeMillis(), -64, 64, 0, null);
				}catch(RuntimeException e){
					
				}
				board.undo_move();
				board.checkMove(player, new Coordinates(1, 8));
				board.makeMove(player, new Coordinates(1, 8));
				try{
					newmax = HAL.max(board.clone(), System.currentTimeMillis(), -64, 64, 0, null);
				}catch(RuntimeException e){
					
				}
				if(newmax%1000 >= max%1000) result = new Coordinates(1, 8);
			}
			else result = new Coordinates(1, 8);
			
		if(board.checkMove(player, new Coordinates(8, 1)))
			if(result != null){
				board.checkMove(player, result);
				board.makeMove(player, result);
				try{
					max = HAL.max(board.clone(), System.currentTimeMillis(), -64, 64, 0, null);
				}catch(RuntimeException e){
					
				}
				board.undo_move();
				board.checkMove(player, new Coordinates(8, 1));
				board.makeMove(player, new Coordinates(8, 1));
				try{
					newmax = HAL.max(board.clone(), System.currentTimeMillis(), -64, 64, 0, null);
				}catch(RuntimeException e){
					
				}
				if(newmax%1000 >= max%1000) result = new Coordinates(1, 8);
			}
			else result = new Coordinates(8, 1);
		if(board.checkMove(player, new Coordinates(8, 8)))
			if(result != null){
				board.checkMove(player, result);
				board.makeMove(player, result);
				try{
					max = HAL.max(board.clone(), System.currentTimeMillis(), -64, 64, 0, null);
				}catch(RuntimeException e){
					
				}
				board.undo_move();
				board.checkMove(player, new Coordinates(8, 8));
				board.makeMove(player, new Coordinates(8, 8));
				try{
					newmax = HAL.max(board.clone(), System.currentTimeMillis(), -64, 64, 0, null);
				}catch(RuntimeException e){
					
				}
				if(newmax%1000 >= max%1000) result = new Coordinates(8, 8);
			}
			else result = new Coordinates(8, 8);
		return result;	
	}
	*/
	
	
}
