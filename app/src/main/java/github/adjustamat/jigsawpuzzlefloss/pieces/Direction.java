package github.adjustamat.jigsawpuzzlefloss.pieces;

import github.adjustamat.jigsawpuzzlefloss.pieces.SVGEdges.DoubleEdge;
import github.adjustamat.jigsawpuzzlefloss.pieces.SVGEdges.HalfEdge;

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

public DoubleEdge getDoubleEdge(HalfEdge[][] pool,
 boolean in, int curv1, int curv2, int neck1, int neck2)
{
   int poolIndex;
   switch (this) {
   case EAST: case SOUTH:
      poolIndex = (in ?4 :0) + ordinal();
      return new DoubleEdge(
       // pool [ NECK*6 + CURV*2 + (secondHalf?1:0) ]  [ (inward ?4 :0) + direction_ordinal ]
       pool[neck1 * 6 + curv1 * 2][poolIndex],
       pool[neck2 * 6 + curv2 * 2 + 1][poolIndex]
      );
   default: // WEST: NORTH:
      // in/out is flipped:
      poolIndex = (in ?0 :4) + ordinal();
      return new DoubleEdge(
       // first/second is flipped:
       pool[neck2 * 6 + curv2 * 2][poolIndex],
       pool[neck1 * 6 + curv1 * 2 + 1][poolIndex]
      );
   }
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
   default: // WEST:
      return EAST;
   }
}
}
