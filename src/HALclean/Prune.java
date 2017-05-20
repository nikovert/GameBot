package HALclean;

import reversi.Coordinates;
import reversi.GameBoard;

public class Prune {


	/**
	 * 
	 * @param BitBoard gb
	 * @param Coordinates move (the move to be checked)
	 * @param int totalstones (the total number of stones on the gameboard)
	 * @param int player (see GameBoard.player)
	 * @return true if the move should be skipped
	 */
	protected boolean checkPrune(BitBoard board, int moveCoordinates, int player){
		int opponent = GameBoard.GREEN;
		if(player == GameBoard.GREEN)
			opponent = GameBoard.RED;
		Coordinates move = board.ArraytoCoordinate(moveCoordinates);
		int row = move.getRow();
		int col = move.getCol();
		
		//Check if X Square
		if((row == 2 || row == 7) && (col == 2 || col == 7)){
			
			//if the total disc count is 35 or more, never prune
			if((board.disks_played+4) >= 35) 
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
					if((board.disks_played+4) < 25)
						return true;
				}else{
					//Otherwise, if the adjacent corner is occupied, do not prune
					if(board.getOccupation(18) == 0)
						return true;
					//Otherwise, if both adjacent C-Square are occupied, always prune
					if(board.getOccupation(17) != 0 && board.getOccupation(28) != 0 )
						return true;
					//Otherwise, prune if and only if the total disc count is less than 25
					if((board.disks_played+4) < 25)
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
					if((board.disks_played+4) < 25)
						return true;
				}else{
					//Otherwise, if the adjacent corner is occupied, do not prune
					if(board.getOccupation(88) == 0)
						return true;
					//Otherwise, if both adjacent C-Square are occupied, always prune
					if(board.getOccupation(87) != 0 && board.getOccupation(78) != 0 )
						return true;
					//Otherwise, prune if and only if the total disc count is less than 25
					if((board.disks_played+4) < 25)
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
					if((board.disks_played+4) < 28)
						return true;
				}if(col == 7){
					//the adjacent corner and A square are empty, prune
					if(board.getOccupation(18)== 0 && board.getOccupation(16)== 0)
						return true;
					//There is no opponent on the nearest B-Square
					if(board.getOccupation(15) != opponent)
						return true;
					//The total number of discs is less than 28
					if((board.disks_played+4) < 28)
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
					if((board.disks_played+4) < 28)
						return true;
				}if(col == 8){
					//the adjacent corner and A square are empty, prune
					if(board.getOccupation(18)== 0 && board.getOccupation(38)== 0)
						return true;
					//There is no opponent on the nearest B-Square
					if(board.getOccupation(48) != opponent)
						return true;
					//The total number of discs is less than 28
					if((board.disks_played+4) < 28)
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
					if((board.disks_played+4) < 28)
						return true;
				}if(col == 8){
					//the adjacent corner and A square are empty, prune
					if(board.getOccupation(88)== 0 && board.getOccupation(68)== 0)
						return true;
					//There is no opponent on the nearest B-Square
					if(board.getOccupation(58) != opponent)
						return true;
					//The total number of discs is less than 28
					if((board.disks_played+4) < 28)
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
					if((board.disks_played+4) < 28)
						return true;
				}if(col == 7){
					//the adjacent corner and A square are empty, prune
					if(board.getOccupation(88)== 0 && board.getOccupation(86)== 0)
						return true;
					//There is no opponent on the nearest B-Square
					if(board.getOccupation(85) != opponent)
						return true;
					//The total number of discs is less than 28
					if((board.disks_played+4) < 28)
						return true;
				}
			}
		}
		
		return false;
	}
}
