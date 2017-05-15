package felix;

public class GameConstants {
  
  // Game Settings
  static final int ILLEGAL = -1;
  static final int GREENSQ = 0;
  static final int EMPTY = 1;
  static final int REDSQ = 2;
  static final int OUTSIDE = 3;
  static final int GREEN = 0;
  static final int RED = 2;
  
  // Constants
  static final int MOVE_ORDER_SIZE = 60;
  static final int MAX_SEARCH_DEPTH = 60;
  
  // Patterns
  static final int position_list[] = {
      /*A1*/        11 , 18 , 81 , 88 , 
      /*C1*/        13 , 16 , 31 , 38 , 61 , 68 , 83 , 86 ,
      /*C3*/        33 , 36 , 63 , 66 ,
      /*D1*/        14 , 15 , 41 , 48 , 51 , 58 , 84 , 85 ,
      /*D3*/        34 , 35 , 43 , 46 , 53 , 56 , 64 , 65 ,
      /*D2*/        24 , 25 , 42 , 47 , 52 , 57 , 74 , 75 ,
      /*C2*/        23 , 26 , 32 , 37 , 62 , 67 , 73 , 76 ,
      /*B1*/        12 , 17 , 21 , 28 , 71 , 78 , 82 , 87 ,
      /*B2*/        22 , 27 , 72 , 77 ,
      /*D4*/        44 , 45 , 54 , 45 ,
      /*North*/      0 ,  1 ,  2 ,  3 ,  4 ,  5 ,  6 ,  7 , 8,
      /*East*/       9 , 19 , 29 , 39 , 49 , 59 , 69 , 79 , 89,
      /*West*/      10 , 20 , 30 , 40 , 50 , 60 , 70 , 80 , 90,
      /*South*/     91 , 92 , 93 , 94 , 95 , 96 , 97 , 98 , 99 };
  
  static final int dir_mask[] = {
      0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
      0,  81,  81,  87,  87,  87,  87,  22,  22,   0,
      0,  81,  81,  87,  87,  87,  87,  22,  22,   0,
      0, 121, 121, 255, 255, 255, 255, 182, 182,   0,
      0, 121, 121, 255, 255, 255, 255, 182, 182,   0,
      0, 121, 121, 255, 255, 255, 255, 182, 182,   0,
      0, 121, 121, 255, 255, 255, 255, 182, 182,   0,
      0,  41,  41, 171, 171, 171, 171, 162, 162,   0,
      0,  41,  41, 171, 171, 171, 171, 162, 162,   0,
      0,   0,   0,   0,   0,   0,   0,   0,   0,   0
    };
  
  static final int move_offset[] = { 1, -1, 9, -9, 10, -10, 11, -11 };

}
