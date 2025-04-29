package github.adjustamat.jigsawpuzzlefloss.pieces;

public enum Direction
{
   NORTH(1, 2, 0, 0, 1, 0) {
      public Direction next (){
         return EAST;
      }
   },
   EAST(2, 1, 0, 1, 0, 0) {
      public Direction next (){
         return SOUTH;
      }
   },
   SOUTH(1, 2, 0, 0, 0, 1) {
      public Direction next (){
         return WEST;
      }
   },
   WEST(2, 1, 1, 0, 0, 0) {
      public Direction next (){
         return NORTH;
      }
   };
public final int initWidth;
public final int initHeight;
public final int initX1;
public final int initX2;
public final int initY1;
public final int initY2;
public final int directionX;
public final int directionY;
public final int perpendicularX;
public final int perpendicularY;

Direction (int initWidth, int initHeight,
 int initX1, int initX2, int initY1, int initY2
){
   this.initWidth = initWidth;
   this.initHeight = initHeight;
   this.initX1 = initX1;
   this.initX2 = initX2;
   this.initY1 = initY1;
   this.initY2 = initY2;
   this.directionX = initX2 - initX1;
   this.directionY = initY2 - initY1;
   this.perpendicularX = initY1 + initY2;
   this.perpendicularY = initX1 + initX2;
}

public abstract Direction next ();

public Direction prev (){
   if (this == NORTH)
      return WEST;
   return values()[ordinal() - 1];
}

public Direction opposite (){
   switch (this) {
   case NORTH:
      return SOUTH;
   case SOUTH:
      return NORTH;
   case EAST:
      return WEST;
   default: // WEST:
      return EAST;
   }
}

public Direction positive (){
   switch (this) {
   case NORTH: case SOUTH:
      return SOUTH;
   default: // case EAST: WEST:
      return EAST;
   }
}

public static int cycle(int i){
   return i % 4;
}
}
