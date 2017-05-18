package HAL;

import reversi.Coordinates;
import reversi.GameBoard;
import reversi.OutOfBoundsException;

public class Eval {


	/**
	 * 
	 * @param the Gameboard gb
	 * @param the current move (that we plan on making)
	 * @return how good the move is (+inf == very good, -inf == shit)
	 */
	protected int getWeight(BitBoard board, int lastCoordinate, int depth, int mobility) {

		Coordinates last = board.ArraytoCoordinate(lastCoordinate);
		int totalstones = (board.countStones(HAL.player)+board.countStones(HAL.opponent));
		double diff = 0.5;//totalstones/64;
		int col = last.getCol(), row = last.getRow();
		int weight = mobility/2;
		
		//if early game
		if(totalstones < 40)
			diff = -2*diff;
		
		//if end game
		if(totalstones > 55)
			diff = 3*diff;
		
		//number of tiles
		weight += (board.countStones(HAL.player)-board.countStones(HAL.opponent))*diff;
		
		/*weight of fields:
		 100  -8  8  6  6  8  -8 100
		 -8  -44 -4 -3 -3 -4 -44 -8
		  8   -4  7  4  4  7  -4  8
		  6   -3  4  0  0  4  -3  6
		  6   -3  4  0  0  4  -3  6
		  8   -4  7  4  4  7  -4  8
		 -8  -44 -4 -3 -3 -4 -44 -8
		 100   -8  8  6  6  8  -8 100
		*/
		
		//position
		switch (col){
			case 1: col = 8;
			case 8: 
				if(row == 1 || row == 8){
					//Corner
					weight += 100;
				}else {
					if(row == 2 || row == 7){
						//C Square
						weight += -8;
					}else {
						if(row == 3 || row == 6){
							//A Square
							weight += 8;
						}else{
							//B Square
							weight += 6;
						}
					}
				}
				break;
			case 2: col = 7;
			case 7: 
				if(row == 1 || row == 8){
					//C Square
					weight += -8;
				}else {
					if(row == 2 || row == 7){
						//X Square
						weight += -44;
					}else {
						if(row == 3 || row == 6)
							weight += -4;
						else
							weight += -3;
					}
				}
		
				break;
			case 3: col = 6;
			case 6: 
				if(row == 1 || row == 8){
					//A Square
					weight += 8;
				}else{
					if(row == 2 || row == 7)
						weight += -4;
					else {
						if(row == 3 || row == 6)
							weight += 7;
						else
							weight += 4;
					}
				}
				break;
			case 4: col = 5;
			case 5: 
				if(row == 1 || row == 8){
					//B Square
					weight += 6;
				}else{
					if(row == 2 || row == 7)
						weight += -3;
					else {
						if(row == 3 || row == 6)
							weight += 4;
						else
							weight += 0;
					}
				}
				break;
			default:
				weight += 0;
		}
		
		//System.out.println("weight: " + weight);
		
		return weight;
	}
	
	/**
	 * 
	 * @param BitBoard gb
	 * @param Coordinates move (the move to be checked)
	 * @param int totalstones (the total number of stones on the gameboard)
	 * @param int player (see GameBoard.player)
	 * @return true if the move should be skipped
	 */
	protected boolean checkPrune(BitBoard board, int moveCoordinates, int totalstones, int player){
		int opponent = GameBoard.GREEN;
		if(player == GameBoard.GREEN)
			opponent = GameBoard.RED;
		Coordinates move = board.ArraytoCoordinate(moveCoordinates);
		int row = move.getRow();
		int col = move.getCol();
		
		//Check if X Square
		if((row == 2 || row == 7) && (col == 2 || col == 7)){
			
			//if the total disc count is 35 or more, never prune
			if(totalstones >= 35) 
				return false;
			
			if(row == 2){
				if(col == 2){
					//Otherwise, if the adjacent corner is occupied, do not prune
					if(board.getOccupation(11)== 0)
						return true;
					//Otherwise, if both adjacent C-Square are occupied, always prune
					if(board.getOccupation(12) != 0 && board.getOccupation(21) != 0 )
						return true;
					//Otherwise, prune if and only if the total disc count is less than 25
					if(totalstones < 25)
						return true;
				}else{
					//Otherwise, if the adjacent corner is occupied, do not prune
					if(board.getOccupation(18) == 0)
						return true;
					//Otherwise, if both adjacent C-Square are occupied, always prune
					if(board.getOccupation(17) != 0 && board.getOccupation(28) != 0 )
						return true;
					//Otherwise, prune if and only if the total disc count is less than 25
					if(totalstones < 25)
						return true;
				}
			}else{
				if(col == 2){
					//Otherwise, if the adjacent corner is occupied, do not prune
					if(board.getOccupation(81) == 0)
						return true;
					//Otherwise, if both adjacent C-Square are occupied, always prune
					if(board.getOccupation(82) != 0 && board.getOccupation(71) != 0 )
						return true;
					//Otherwise, prune if and only if the total disc count is less than 25
					if(totalstones < 25)
						return true;
				}else{
					//Otherwise, if the adjacent corner is occupied, do not prune
					if(board.getOccupation(88) == 0)
						return true;
					//Otherwise, if both adjacent C-Square are occupied, always prune
					if(board.getOccupation(87) != 0 && board.getOccupation(78) != 0 )
						return true;
					//Otherwise, prune if and only if the total disc count is less than 25
					if(totalstones < 25)
						return true;
				}
			}
		}
		
		//Check if C Square
		if((row == 1 && (col == 2 || col == 7)) || (row == 2 && (col == 1 || col == 8)) || (row == 7 && (col == 1 || col == 8)) || (row == 8 && (col == 2 || col == 7))){
				
			if(row == 1){
				if(col == 2){
					//the adjacent corner and A square are empty, prune
					if(board.getOccupation(11)== 0 && board.getOccupation(12)== 0)
						return true;
					//There is no opponent on the nearest B-Square
					if(board.getOccupation(14) != opponent)
						return true;
					//The total number of discs is less than 28
					if(totalstones < 28)
						return true;
				}if(col == 7){
					//the adjacent corner and A square are empty, prune
					if(board.getOccupation(18)== 0 && board.getOccupation(16)== 0)
						return true;
					//There is no opponent on the nearest B-Square
					if(board.getOccupation(15) != opponent)
						return true;
					//The total number of discs is less than 28
					if(totalstones < 28)
						return true;
				}
			}if(row == 2){
				if(col == 1){
					//the adjacent corner and A square are empty, prune
					if(board.getOccupation(11)== 0 && board.getOccupation(31)== 0)
						return true;
					//There is no opponent on the nearest B-Square
					if(board.getOccupation(41) != opponent)
						return true;
					//The total number of discs is less than 28
					if(totalstones < 28)
						return true;
				}if(col == 8){
					//the adjacent corner and A square are empty, prune
					if(board.getOccupation(18)== 0 && board.getOccupation(38)== 0)
						return true;
					//There is no opponent on the nearest B-Square
					if(board.getOccupation(48) != opponent)
						return true;
					//The total number of discs is less than 28
					if(totalstones < 28)
						return true;
				}
			}
			if(row == 7){
				if(col == 1){
					//the adjacent corner and A square are empty, prune
					if(board.getOccupation(81)== 0 && board.getOccupation(61)== 0)
						return true;
					//There is no opponent on the nearest B-Square
					if(board.getOccupation(51) != opponent)
						return true;
					//The total number of discs is less than 28
					if(totalstones < 28)
						return true;
				}if(col == 8){
					//the adjacent corner and A square are empty, prune
					if(board.getOccupation(88)== 0 && board.getOccupation(68)== 0)
						return true;
					//There is no opponent on the nearest B-Square
					if(board.getOccupation(58) != opponent)
						return true;
					//The total number of discs is less than 28
					if(totalstones < 28)
						return true;
				}
			}if(row == 8){
				if(col == 2){
					//the adjacent corner and A square are empty, prune
					if(board.getOccupation(81)== 0 && board.getOccupation(83)== 0)
						return true;
					//There is no opponent on the nearest B-Square
					if(board.getOccupation(84) != opponent)
						return true;
					//The total number of discs is less than 28
					if(totalstones < 28)
						return true;
				}if(col == 7){
					//the adjacent corner and A square are empty, prune
					if(board.getOccupation(88)== 0 && board.getOccupation(86)== 0)
						return true;
					//There is no opponent on the nearest B-Square
					if(board.getOccupation(85) != opponent)
						return true;
					//The total number of discs is less than 28
					if(totalstones < 28)
						return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if a corner move is possible
	 * @return the corner, or null if no corner move is possible
	 */
	/*
	protected Coordinates checkCorners(BitBoard board) {
		Coordinates result = null;
		int max = 0, newmax = 0;
		
		if(board.checkMove(HAL.player, new Coordinates(1, 1)))
			result = new Coordinates(1, 1);
		
		if(board.checkMove(HAL.player, new Coordinates(1, 8)))
			if(result != null){
				//GameBoard test = board.clone();
				board.checkMove(HAL.player, result);
				board.makeMove(HAL.player, result);
				try{
					max = HAL.max(board.clone(), System.currentTimeMillis(), -64, 64, 0, null);
				}catch(RuntimeException e){
					
				}
				board.undo_move();
				board.checkMove(HAL.player, new Coordinates(1, 8));
				board.makeMove(HAL.player, new Coordinates(1, 8));
				try{
					newmax = HAL.max(board.clone(), System.currentTimeMillis(), -64, 64, 0, null);
				}catch(RuntimeException e){
					
				}
				if(newmax%1000 >= max%1000) result = new Coordinates(1, 8);
			}
			else result = new Coordinates(1, 8);
			
		if(board.checkMove(HAL.player, new Coordinates(8, 1)))
			if(result != null){
				board.checkMove(HAL.player, result);
				board.makeMove(HAL.player, result);
				try{
					max = HAL.max(board.clone(), System.currentTimeMillis(), -64, 64, 0, null);
				}catch(RuntimeException e){
					
				}
				board.undo_move();
				board.checkMove(HAL.player, new Coordinates(8, 1));
				board.makeMove(HAL.player, new Coordinates(8, 1));
				try{
					newmax = HAL.max(board.clone(), System.currentTimeMillis(), -64, 64, 0, null);
				}catch(RuntimeException e){
					
				}
				if(newmax%1000 >= max%1000) result = new Coordinates(1, 8);
			}
			else result = new Coordinates(8, 1);
		if(board.checkMove(HAL.player, new Coordinates(8, 8)))
			if(result != null){
				board.checkMove(HAL.player, result);
				board.makeMove(HAL.player, result);
				try{
					max = HAL.max(board.clone(), System.currentTimeMillis(), -64, 64, 0, null);
				}catch(RuntimeException e){
					
				}
				board.undo_move();
				board.checkMove(HAL.player, new Coordinates(8, 8));
				board.makeMove(HAL.player, new Coordinates(8, 8));
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
