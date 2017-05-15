package evaluation;

import evaluation.Position;

public class Tree_Search {

  /**
   * 
   * Bmkg: Für weitere Informationen siehe: "Spielbaum-Suchverfahren.pdf", "AlphaBetaNegaScout.txt".
   * 
   * @param pos 
   *          The game position to evaluate.
   * @param alpha
   *          The best result of Max so far.
   * @param beta
   *          The best result of Min so far.
   * @return Returns the NegaMax-value of this position.
   */
  public int NegaScout(Position P, int alpha, int beta, int depth) {
    Position[] successors = P.successors();

    // ACHTUNG!!  Hier muss man sehr aufpassen, wie die eval-Funktion funktioniert!
    //            Je nachdem, kann es ja auch sein, dass kein Zug möglich ist, d.h.
    //            ein Spieler passen muss.
    //            Bzw. dass die eval-Funktion in diesem Fall richtig funktioniert!
    if (successors == null)                                           
    // if (depth == Max_Depth)
      return P.eval();                                              //(*Blattbewertung*)

    int lo_value = Integer.MIN_VALUE;
    int hi_value = beta;

    // Man könnte theoretisch etwas sparen wenn man die Suche mit offenem Fenster rausnimmt, dann
    // muss man nicht immer j > 0 überprüfen...Aber eigentlich nicht von Relevanz!
    for(int j = 0; j < successors.length; ++j){
      int t = -NegaScout(successors[j],-hi_value,-Math.max(lo_value, alpha), depth + 1);
      
      if(t > Math.max(lo_value, alpha) && t < beta && j > 0 && depth < Max_Depth - 2)
        t = -NegaScout(successors[j],-hi_value, -t, depth + 1);     //(*Wiederholungssuche*)
      
      lo_value = Math.max(lo_value, t);
      if(lo_value >= beta)
        return lo_value;                                            //(*Schnitt*)
      
      hi_value = Math.max(lo_value, alpha) + 1;                     //(*Neues Nullfenster*)
    }

    return lo_value;
  }

  // NICHT VERWENDEN
  public int MTDF(Position root, int f, int d) {

    int g = f;
    int upperbound = Integer.MAX_VALUE;
    int lowerbound = Integer.MIN_VALUE;

    int beta;

    do{
      if (g == lowerbound)
        beta = g + 1;
      else
        beta = g;
      //Es wäre besser nur ein Argument zu verwenden um Ressourcen zu sparen.
      g = AlphaBetaWithMemory(root, beta - 1, beta, d);
      if (g < beta)
        upperbound = g;
      else
        lowerbound = g;
    }
    while(lowerbound < upperbound);
    return g;
  }

  // NICHT VERWENDEN
  public int iterative_deepening(Position root, int maxDepth){
    
    //The start value for the MTDF-algorithm.
    //Anmerkung: Eventuell zwei verschiedene Startwerte verwenden,
    //jeweils einen für Min-Levels bzw. Max-Levels.
    int startvalue = 0;
    
    //Iterative search. For each new search we use the best
    //value found in the previous depth used.
    for(int d = 1; d <= maxDepth; ++d){
      startvalue = MTDF(root, startvalue, d);
    }
    
    //At this point startvalue equals the minimax value.
    return startvalue;
  }
}
