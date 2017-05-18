package HALtests;

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
	protected static int getWeight(GameBoard gb, Coordinates last, int depth, int mobility) {
		if(last == null) return 0;
		int totalstones = (gb.countStones(HAL.player)+gb.countStones(HAL.opponent));
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
		weight += (gb.countStones(HAL.player)-gb.countStones(HAL.opponent))*diff;
		
		
		
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
	protected static boolean checkPrune(GameBoard gb, Coordinates move, int totalstones, int player){
		int opponent = GameBoard.GREEN;
		if(player == GameBoard.GREEN)
			opponent = GameBoard.RED;
		int row = move.getRow();
		int col = move.getCol();
		
		//Check if X Square
		if((row == 2 || row == 7) && (col == 2 || col == 7)){
			
			//if the total disc count is 35 or more, never prune
				if(totalstones >= 35) 
					return false;
				
				if(row == 2){
					if(col == 2){
						try {
							//Otherwise, if the adjacent corner is occupied, do not prune
							if(gb.getOccupation(new Coordinates(1,1))== 0)
								return true;
							//Otherwise, if both adjacent C-Square are occupied, always prune
							if(gb.getOccupation(new Coordinates(1,2)) != 0 && gb.getOccupation(new Coordinates(2,1)) != 0 )
								return true;
							//Otherwise, prune if and only if the total disc count is less than 25
							if(totalstones < 25)
								return true;
						} catch (OutOfBoundsException e) {
							e.printStackTrace();
						}
					}else{
						try {
							//Otherwise, if the adjacent corner is occupied, do not prune
							if(gb.getOccupation(new Coordinates(1,8)) == 0)
								return true;
							//Otherwise, if both adjacent C-Square are occupied, always prune
							if(gb.getOccupation(new Coordinates(1,7)) != 0 && gb.getOccupation(new Coordinates(2,8)) != 0 )
								return true;
							//Otherwise, prune if and only if the total disc count is less than 25
							if(totalstones < 25)
								return true;
						} catch (OutOfBoundsException e) {
							e.printStackTrace();
						}
					}
				}else{
					if(col == 2){
						try {
							//Otherwise, if the adjacent corner is occupied, do not prune
							if(gb.getOccupation(new Coordinates(8,1)) == 0)
								return true;
							//Otherwise, if both adjacent C-Square are occupied, always prune
							if(gb.getOccupation(new Coordinates(8,2)) != 0 && gb.getOccupation(new Coordinates(7,1)) != 0 )
								return true;
							//Otherwise, prune if and only if the total disc count is less than 25
							if(totalstones < 25)
								return true;
						} catch (OutOfBoundsException e) {
							e.printStackTrace();
						}
					}else{
						try {
							//Otherwise, if the adjacent corner is occupied, do not prune
							if(gb.getOccupation(new Coordinates(8,8)) == 0)
								return true;
							//Otherwise, if both adjacent C-Square are occupied, always prune
							if(gb.getOccupation(new Coordinates(8,7)) != 0 && gb.getOccupation(new Coordinates(7,8)) != 0 )
								return true;
							//Otherwise, prune if and only if the total disc count is less than 25
							if(totalstones < 25)
								return true;
						} catch (OutOfBoundsException e) {
							e.printStackTrace();
						}
					}
				}
		}
		
		//Check if C Square
		if((row == 1 && (col == 2 || col == 7)) || (row == 2 && (col == 1 || col == 8)) || (row == 7 && (col == 1 || col == 8)) || (row == 8 && (col == 2 || col == 7))){
				
				if(row == 1){
					if(col == 2){
						//Coordinates (1,2)
						try {
							//the adjacent corner and A square are empty, prune
							if(gb.getOccupation(new Coordinates(1,1))== 0 && gb.getOccupation(new Coordinates(1,2))== 0)
								return true;
							//There is no opponent on the nearest B-Square
							if(gb.getOccupation(new Coordinates(1,4)) != opponent)
								return true;
							//The total number of discs is less than 28
							if(totalstones < 28)
								return true;
						} catch (OutOfBoundsException e) {
							e.printStackTrace();
						}
					}if(col == 7){
						//Coordinates (1,7)
						try {
							//the adjacent corner and A square are empty, prune
							if(gb.getOccupation(new Coordinates(1,8))== 0 && gb.getOccupation(new Coordinates(1,6))== 0)
								return true;
							//There is no opponent on the nearest B-Square
							if(gb.getOccupation(new Coordinates(1,5)) != opponent)
								return true;
							//The total number of discs is less than 28
							if(totalstones < 28)
								return true;
						} catch (OutOfBoundsException e) {
							e.printStackTrace();
						}
					}
				}if(row == 2){
					if(col == 1){
						//Coordinates (2,1)
						try {
							//the adjacent corner and A square are empty, prune
							if(gb.getOccupation(new Coordinates(1,1))== 0 && gb.getOccupation(new Coordinates(3,1))== 0)
								return true;
							//There is no opponent on the nearest B-Square
							if(gb.getOccupation(new Coordinates(4,1)) != opponent)
								return true;
							//The total number of discs is less than 28
							if(totalstones < 28)
								return true;
						} catch (OutOfBoundsException e) {
							e.printStackTrace();
						}
					}if(col == 8){
						//Coordinates (2,8)
						try {
							//the adjacent corner and A square are empty, prune
							if(gb.getOccupation(new Coordinates(1,8))== 0 && gb.getOccupation(new Coordinates(3,8))== 0)
								return true;
							//There is no opponent on the nearest B-Square
							if(gb.getOccupation(new Coordinates(4,8)) != opponent)
								return true;
							//The total number of discs is less than 28
							if(totalstones < 28)
								return true;
						} catch (OutOfBoundsException e) {
							e.printStackTrace();
						}
					}
				}
				if(row == 7){
					if(col == 1){
						//Coordinates (7,1)
						try {
							//the adjacent corner and A square are empty, prune
							if(gb.getOccupation(new Coordinates(8,1))== 0 && gb.getOccupation(new Coordinates(6,1))== 0)
								return true;
							//There is no opponent on the nearest B-Square
							if(gb.getOccupation(new Coordinates(5,1)) != opponent)
								return true;
							//The total number of discs is less than 28
							if(totalstones < 28)
								return true;
						} catch (OutOfBoundsException e) {
							e.printStackTrace();
						}
					}if(col == 8){
						//Coordinates (7,8)
						try {
							//the adjacent corner and A square are empty, prune
							if(gb.getOccupation(new Coordinates(8,8))== 0 && gb.getOccupation(new Coordinates(6,8))== 0)
								return true;
							//There is no opponent on the nearest B-Square
							if(gb.getOccupation(new Coordinates(5,8)) != opponent)
								return true;
							//The total number of discs is less than 28
							if(totalstones < 28)
								return true;
						} catch (OutOfBoundsException e) {
							e.printStackTrace();
						}
					}
				}if(row == 8){
					if(col == 2){
						//Coordinates (8,2)
						try {
							//the adjacent corner and A square are empty, prune
							if(gb.getOccupation(new Coordinates(8,1))== 0 && gb.getOccupation(new Coordinates(8,3))== 0)
								return true;
							//There is no opponent on the nearest B-Square
							if(gb.getOccupation(new Coordinates(8,4)) != opponent)
								return true;
							//The total number of discs is less than 28
							if(totalstones < 28)
								return true;
						} catch (OutOfBoundsException e) {
							e.printStackTrace();
						}
					}if(col == 7){
						//Coordinates (8,7)
						try {
							//the adjacent corner and A square are empty, prune
							if(gb.getOccupation(new Coordinates(8,8))== 0 && gb.getOccupation(new Coordinates(8,6))== 0)
								return true;
							//There is no opponent on the nearest B-Square
							if(gb.getOccupation(new Coordinates(8,5)) != opponent)
								return true;
							//The total number of discs is less than 28
							if(totalstones < 28)
								return true;
						} catch (OutOfBoundsException e) {
							e.printStackTrace();
						}
					}
				}
		}
		
		return false;
	}
	
	/**
	 * Checks if a corner move is possible
	 * @return the corner, or null if no corner move is possible
	 */
	protected static Coordinates checkCorners(GameBoard gb) {
		Coordinates result = null;
		int max = 0, newmax = 0;
		
		if(gb.checkMove(HAL.player, new Coordinates(1, 1)))
			result = new Coordinates(1, 1);
		
		if(gb.checkMove(HAL.player, new Coordinates(1, 8)))
			if(result != null){
				GameBoard test = gb.clone();
				test.checkMove(HAL.player, result);
				test.makeMove(HAL.player, result);
				try{
					max = HAL.max(gb.clone(), System.currentTimeMillis(), -64, 64, 0, null);
				}catch(RuntimeException e){
					
				}
				test = gb.clone();
				test.checkMove(HAL.player, new Coordinates(1, 8));
				test.makeMove(HAL.player, new Coordinates(1, 8));
				try{
					newmax = HAL.max(gb.clone(), System.currentTimeMillis(), -64, 64, 0, null);
				}catch(RuntimeException e){
					
				}
				if(newmax%1000 >= max%1000) result = new Coordinates(1, 8);
			}
			else result = new Coordinates(1, 8);
			
		if(gb.checkMove(HAL.player, new Coordinates(8, 1)))
			if(result != null){
				GameBoard test = gb.clone();
				test.checkMove(HAL.player, result);
				test.makeMove(HAL.player, result);
				try{
					max = HAL.max(gb.clone(), System.currentTimeMillis(), -64, 64, 0, null);
				}catch(RuntimeException e){
					
				}
				test = gb.clone();
				test.checkMove(HAL.player, new Coordinates(8, 1));
				test.makeMove(HAL.player, new Coordinates(8, 1));
				try{
					newmax = HAL.max(gb.clone(), System.currentTimeMillis(), -64, 64, 0, null);
				}catch(RuntimeException e){
					
				}
				if(newmax%1000 >= max%1000) result = new Coordinates(1, 8);
			}
			else result = new Coordinates(8, 1);
		if(gb.checkMove(HAL.player, new Coordinates(8, 8)))
			if(result != null){
				GameBoard test = gb.clone();
				test.checkMove(HAL.player, result);
				test.makeMove(HAL.player, result);
				try{
					max = HAL.max(gb.clone(), System.currentTimeMillis(), -64, 64, 0, null);
				}catch(RuntimeException e){
					
				}
				test = gb.clone();
				test.checkMove(HAL.player, new Coordinates(8, 8));
				test.makeMove(HAL.player, new Coordinates(8, 8));
				try{
					newmax = HAL.max(gb.clone(), System.currentTimeMillis(), -64, 64, 0, null);
				}catch(RuntimeException e){
					
				}
				if(newmax%1000 >= max%1000) result = new Coordinates(8, 8);
			}
			else result = new Coordinates(8, 8);
		return result;	
	}
	
}
