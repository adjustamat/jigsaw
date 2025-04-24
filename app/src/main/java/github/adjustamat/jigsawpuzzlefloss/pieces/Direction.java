package github.adjustamat.jigsawpuzzlefloss.pieces;

public enum Direction
{
   NORTH(1, 2, 0, 0, 1, 0, 0, -1),
   EAST(2, 1, 0, 1, 0, 0, 1, 0),
   SOUTH(1, 2, 0, 0, 0, 1, 0, 1),
   WEST(2, 1, 1, 0, 0, 0, -1, 0);
public final int initWidth;
public final int initHeight;
public final int initX1;
public final int initX2;
public final int initY1;
public final int initY2;
public final int directionX;
public final int directionY;
//public final int ;
//public final int ;
//public final int ;

Direction(int initWidth, int initHeight,
 int initX1, int initX2, int initY1, int initY2,
 int directionX, int directionY)
{
   this.initWidth = initWidth;
   this.initHeight = initHeight;
   this.initX1 = initX1;
   this.initX2 = initX2;
   this.initY1 = initY1;
   this.initY2 = initY2;
   this.directionX = directionX;
   this.directionY = directionY;
}

public Direction opposite()
{
   switch (this) {
   case NORTH:
      return SOUTH;
   case SOUTH:
      return NORTH;
   case EAST:
      return WEST;
   default:
      return EAST;
   }
}
} // enum Direction
