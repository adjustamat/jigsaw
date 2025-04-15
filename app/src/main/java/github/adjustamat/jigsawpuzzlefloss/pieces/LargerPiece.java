package github.adjustamat.jigsawpuzzlefloss.pieces;

import java.util.ArrayList;
import android.graphics.drawable.shapes.Shape;
import github.adjustamat.jigsawpuzzlefloss.game.Container;

/**
 * Two or more {@link SinglePiece}s that fit together.
 */
public class LargerPiece
 extends AbstractPiece
{

public static final int BIT_NW = 0b1;
public static final int BIT_SW = 0b10;
public static final int BIT_SE = 0b100;
public static final int BIT_NE = 0b1000;
public static final int BITMASK_CORNERS = 0b1111;
public static final int BIT_WEST = 0b10000;
public static final int BIT_SOUTH = 0b100000;
public static final int BIT_EAST = 0b1000000;
public static final int BIT_NORTH = 0b10000000;
public static final int BITS_BG = 0b111111111;

public static enum Direction{
   NORTH(1,2, 0,0, 1,0, 0,-1),
   SOUTH(1,2, 0,0, 0,1, 0,1),
   EAST(2,1, 0,1, 0,0, 1,0),
   WEST(2,1, 1,0, 0,0, -1,0);
   public final int startWidth;
   public final int startHeight;
   public final int startX1;
   public final int startX2;
   public final int startY1;
   public final int startY2;
   public final int directionX;
   public final int directionY;
   //public final int ;
   //public final int ;
   //public final int ;
   private Direction(int startWidth, int startHeight, 
    int startX1, int startX2, int startY1, int startY2, 
    int directionX, int directionY)// , int X, int Y
   {
      this.startWidth=startWidth;
      // TODO: etc...
   }
   public Direction opposite(){
      switch(this){
         case NORTH: return SOUTH;
         case SOUTH: return NORTH;
         case EAST: return WEST;
         default: return EAST;
      }
   }
}

ArrayList<SinglePiece> matrix;
int matrixWidth;
int matrixHeight;
int pieceCount;

Shape combinedImageMask;

/*private LargerPiece(int width, int height, Container parent){
   super(parent);
   matrixWidth = width;
   matrixHeight = height;

   matrix = new ArrayList();
   int full = width * height;
   for(int i=0; i < full; i++){
      matrix.add(null);
   }
}*/

private void init(int width, int height){
   matrixWidth = width;
   matrixHeight = height;

   matrix = new ArrayList();
   int len = width * height;
   for(int i=0; i < len; i++){
      matrix.add(null);
   }
}

private LargerPiece(SinglePiece p1, SinglePiece p2, Direction dir)
{
   this(dir.startWidth, dir.startHeight, p1.containerParent);
   pieceCount = 2;
   set(dir.startX1, dir.startY1, p1);
   set(dir.startX2, dir.startY2, p2);
}

private LargerPiece(LargerPiece p1, LargerPiece p2, Point point1, Point point2, Direction dir){
   this(
      Math.max()
    p1.matrixWidth + p2.matrixWidth - point2.x - point1.x/* TODO! */,
    /*TODO! */, 
    p1.containerParent
   );
   pieceCount = p1.pieceCount + p2.pieceCount;
   int x1Offset = ; // TODO: make sure to care about Direction dir!
   int x2Offset = ;
   int y1Offset = ;
   int y2Offset = ;

   // combine matrices
   for(SinglePiece p: p1.matrix){
      set(p.positionInLargerPiece.x + x1Offset, p.positionInLargerPiece.y + y1Offset, p);
   }
   for(SinglePiece p: p2.matrix){
      set(p.positionInLargerPiece.x + x2Offset, p.positionInLargerPiece.y + y2Offset, p);
   }
}

public static LargerPiece combine(AbstractPiece p1, AbstractPiece p2, Direction dir){
   if(p1 instanceof LargerPiece){
      if(p2 instanceof LargerPiece){
         return new LargerPiece(p1.largerPieceParent, p2.largerPieceParent, 
          p1.positionInLargerPiece, p2.positionInLargerPiece, dir);
      }
      else{
         p1.largerPieceParent.add(p2, p1.positionInLargerPiece, dir);
         return p1.largerPieceParent;
      }
   }
   else if(p2 instanceof LargerPiece){
      p2.largerPieceParent.add(p1, p2.positionInLargerPiece, dir);
      return p2.largerPieceParent;
   }
   else{
      return new LargerPiece(p1, p2, dir);
   }
}

/*public static LargerPiece combine(SinglePiece p1, SinglePiece p2, Direction dir){
   if(p1.hasLarger()){
      if(p2.hasLarger()){
         return new LargerPiece(p1.largerPieceParent, p2.largerPieceParent, 
          p1.positionInLargerPiece, p2.positionInLargerPiece, dir);
      }
      else{
         p1.largerPieceParent.add(p2, p1.positionInLargerPiece, dir);
         return p1.largerPieceParent;
      }
   }
   else if(p2.hasLarger()){
      p2.largerPieceParent.add(p1, p2.positionInLargerPiece, dir);
      return p2.largerPieceParent;
   }
   else{
      return new LargerPiece(p1, p2, dir);
   }
}*/

private int linear(int x, int y){
   return y * matrixWidth + x;
}

public Point getPuzzlePiece(PointF offset){
   Point ret = new Point(correctPuzzlePosition);
   ret.x += getX(offset.x);
   ret.y += getY(offset.y);
}

public Point getMatrixPiece(PointF offset){
   return new Point(getX(offset.x), getY(offset.y));
}

public int getX(float offsetX){
   return (int) (offsetX / (SIDE_SIZE * matrixWidth))
}

public int getY(float offsetY){
   return (int) (offsetY / (SIDE_SIZE * matrixHeight))
}

public Object get(int x, int y){
   return matrix.get(linear(x, y));
}

public Object get(Point p){
   return matrix.get(linear(p.x, p.y));
}

private void set(int x, int y, SinglePiece p){
   matrix.set(linear(x,y), /*p*/Boolean.TRUE);
   // TODO: combine SVGPath outline (UNION) and check edgeWidths (positionInContainer is handled by Container)
   //p.setLargerPiece(this, new Point(x, y));
   if(p.isLeftEdge())
      leftEdge = EdgeType.EDGE;
   if(p.isRightEdge())
      rightEdge = EdgeType.EDGE;
   if(p.isTopEdge())
      topEdge = EdgeType.EDGE;
   if(p.isBottomEdge())
      bottomEdge = EdgeType.EDGE;
}

public void add(SinglePiece newPiece, Point attachedToPiece, Direction dir)
{
   // the new piece may be attached to more than just attachedToPiece!

   pieceCount++;

   if(attachedToPiece.x == matrixWidth - 1 && dir == Direction.EAST){
      expandX(matrixWidth);
   }
   else if(attachedToPiece.x == 0 && dir == Direction.WEST){
      for(SinglePiece p: matrix){
         p.insertPieceLeft();
      }
      expandX(0);
   }
   else if(attachedToPiece.y == matrixHeight - 1 && dir == Direction.SOUTH){
      expandY(matrix.size());
   }
   else if(attachedToPiece.y == 0 && dir == Direction.NORTH){
      for(SinglePiece p: matrix){
         p.insertPieceTop();
      }
      expandY(0);
   }

   set(attachedToPiece.x + dir.directionX, attachedToPiece.y + dir.directionY, newPiece);
}

private void expandX(int i){
   matrixWidth++;
   // expand matrix right or left by inserting null items into matrix list.
   for(int j=0; j < matrixHeight; j++){
      matrix.add(i, null);
      i += matrixWidth;
   }
}

private void expandY(int i){
   matrixHeight++;
   // expand matrix up or down by inserting null items into matrix list.
   for(int j=0; j < matrixWidth; j++){
      matrix.add(i, null);
   }
}


public ArrayList<BgDrawable> getOutline(){
   ArrayList<BgDrawable> ret= new ArrayList<>(matrix.size());//+2*matrixWidth+2*matrixHeight+4);
   int yLimit = matrixHeight - 1;
   int xLimit = matrixWidth - 1;

   // check edges (do not outline edges!)
   int width = matrixWidth;
   int height = matrixHeight;
   int extraX = 0;
   int extraY = 0;
   if(!isLeftEdge()){
      width++;
      extraX++;
   }
   if(!isRightEdge()){
      width++;
   }
   if(!isTopEdge()){
      height++;
      extraY++;
   }
   if(!isBottomEdge()){
      height++;
   }
   
   //int[][] bits = new int[width][height];
   
   /*for(int y=0;y < matrixHeight; y++){
      for(int x=0; x < matrixWidth; x++){
         int i = linear(x,y);
         SinglePiece piece = matrix.get(i);
         if(piece != null){
            boolean emptyNorth = false;
            boolean emptySouth = false;
            boolean emptyEast = false;
            boolean emptyWest = false;
            //ret.add(BgDrawable.bg(x,y));
            bits[x+1][y+1] = BITS_BG;
            if(x == 0 || matrix.get(i-1) == null){
               emptyWest = true;
               bits[x][y+1] |= BIT_WEST; // TODO: should be EAST
            }
            if(x == xLimit || matrix.get(i+1) == null){
               emptyEast = true;
               bits[x+2][y+1] |= BIT_EAST;
            }
            if(y == 0 || matrix.get(i-matrixWidth) == null){
               emptyNorth = true;
               bits[x+1][y] |= BIT_NORTH;
            }
            if(y == yLimit || matrix.get(i+matrixWidth) == null){
               emptySouth = true;
               bits[x+1][y+2] |= BIT_SOUTH;
            }
            if(emptyNorth && emptyWest){
               bits[x][y] |= BIT_NW;
            }
            if(emptyNorth && emptyEast){
               bits[x+2][y] |= BIT_NE;
            }
            if(emptySouth && emptyWest){
               bits[x][y+2] |= BIT_SW;
            }
            if(emptySouth && emptyEast){
               bits[x+2][y+2] |= BIT_SE; // TODO: should be NW
            }

         } // piece != null
      } // for(x)
   } // for(y)
   */

   // check edges (do not outline edges!)
   if(!isLeftEdge()){ // leftmost column (WEST) can be outlined

      // leftmost column: top left corner
      if(!isTopEdge()){ // NW can be outlined
         if(matrix.get(linear(0, 0)) != null){
            ret.add(BgDrawable.cornerSE(-1, -1));
            ret.add(BgDrawable.edge(-1, 0, Direction.EAST));
         }
         else if(matrix.get(linear(0, 1)) != null){
            ret.add(BgDrawable.cornerSE(-1, 0));
         }
      }
      else{
         // leftmost column: isTopEdge
         if(matrix.get(linear(0, 0)) != null){
            ret.add(BgDrawable.edgeShapeRight(-1, 0, Direction.EAST));
         }
         else if(matrix.get(linear(0, 1)) != null){
            ret.add(BgDrawable.cornerSE(-1, 0));
         }
      }

      // leftmost column: loop through the middle of the column
      for(int y = 1; y < matrixHeight - 1; y++){
         if(matrix.get(linear(0, y)) != null){
            ret.add(BgDrawable.edge(-1, y, Direction.EAST));
         }
         else{
            if(matrix.get(linear(0, y - 1)) != null){
               ret.add(BgDrawable.cornerNE(-1, y));
            }
            if(matrix.get(linear(0, y + 1)) != null){
               ret.add(BgDrawable.cornerSE(-1, y));
            }
         }
      }

      // leftmost column: bottom left corner
      if(!isBottomEdge()){
         // SW
         if(matrix.get(linear(0, matrixHeight - 1)) != null){
            ret.add(BgDrawable.edge(-1, matrixHeight - 1, Direction.EAST));
            ret.add(BgDrawable.cornerNE(-1, matrixHeight));
         }
         else if(matrix.get(linear(0, matrixHeight - 2)) != null){
            ret.add(BgDrawable.cornerNE(-1, matrixHeight - 1));
         }
      }
      else{
         // sw special
         if(matrix.get(linear(0, matrixHeight - 1)) != null){
            ret.add(BgDrawable.edge(-1, matrixHeight - 1, Direction.EAST));
         }
         else if(matrix.get(linear(0, matrixHeight - 2)) != null){
            ret.add(BgDrawable.cornerNE(-1, matrixHeight - 1));
         }
      }
   } // leftmost column (WEST) can be outlined
   if(!isRightEdge()){ // rightmost column (EAST) can be outlined
   } // rightmost column (EAST) can be outlined
   if(!isTopEdge()){ // top row (NORTH) can be outlined
      for(int x=0; x < matrixWidth; x++){
         if(matrix.get(linear(x, 0)) != null){
            ret.add(BgDrawable.edge(-1, y, Direction.EAST));
         }
         else{
            if(matrix.get(linear(x - 1, 0)) != null){
               ret.add(BgDrawable.cornerSW(x, -1));
            }
            if(matrix.get(linear(x + 1, 0)) != null){
               ret.add(BgDrawable.cornerSE(x, -1));
            }
         }
      }
   } // top row (NORTH) can be outlined
   if(!isBottomEdge()){ // bottom row (SOUTH) can be outlined
   } // bottom row (SOUTH) can be outlined

   // loop through the middle of the matrix
   for(int y=0; y < matrixHeight; y++){
      for(int x=0; x < matrixWidth; x++){
   /*for(int y=1 + extraY;y < height-1; y++){
         for(int x=1 + extraX; x < width-1; x++){*/
         int i = linear(x,y);
         if(matrix.get(i) != null){
            //if(bits[x][y] == BITS_BG){
            ret.add(BgDrawable.bg(x,y));
            continue;
         }
         if(bits[x][y] && BIT_NORTH != 0){
            if(bits[x][y] && BIT_SOUTH != 0){
               if(bits[x][y] && BIT_WEST != 0){
                  if(bits[x][y] && BIT_EAST != 0){
                     // NSWE o
                     ret.add(BgDrawable.innerShapeO(x,y));
                  }
                  else{
                     // NSW u
                     ret.add(BgDrawable.innerShapeU(x,y,Direction.EAST));
                  }
               }
               else if(bits[x][y] && BIT_EAST != 0){
                  // NSE u
                  ret.add(BgDrawable.innerShapeU(x,y,Direction.WEST));
               }
               else{
                  // NS ii
                  ret.add(BgDrawable.edge(x,y,Direction.NORTH));
                  ret.add(BgDrawable.edge(x,y,Direction.SOUTH));
               }
            } // NORTH && SOUTH
            else if(bits[x][y] && BIT_WEST != 0){
               if(bits[x][y] && BIT_EAST != 0){
                  // NWE u
                  ret.add(BgDrawable.innerShapeU(x,y,Direction.SOUTH));
               }
               else{
                  // NW l +?
                  ret.add(BgDrawable.innerShapeL(x,y,Direction.NORTH,Direction.WEST));
                  if(bits[x][y] && BIT_SE != 0){
                     ret.add(BgDrawable.cornerSE(x,y));
                  }
               }
            } // NORTH && !SOUTH && WEST
            else if(bits[x][y] && BIT_EAST != 0){
               // NE l +?
               ret.add(BgDrawable.innerShapeL(x,y,Direction.NORTH,Direction.EAST));
               if(bits[x][y] && BIT_SW != 0){
                  ret.add(BgDrawable.cornerSW(x,y));
               }
            } // NORTH && !SOUTH && !WEST && EAST
            else{
               // N i ++?
               ret.add(BgDrawable.edge(x,y,Direction.NORTH));
               if(bits[x][y] && BIT_SW != 0){
                  ret.add(BgDrawable.cornerSW(x,y));
               }
               if(bits[x][y] && BIT_SE != 0){
                  ret.add(BgDrawable.cornerSE(x,y));
               }
            } // NORTH && !SOUTH && !WEST && !EAST
         } // NORTH
         else if(bits[x][y] && BIT_SOUTH != 0){
            if(bits[x][y] && BIT_WEST != 0){
               if(bits[x][y] && BIT_EAST != 0){
                  // SWE u
                  ret.add(BgDrawable.innerShapeU(x,y,Direction.NORTH));
               }
               else{
                  // SW l +?
                  ret.add(BgDrawable.innerShapeL(x,y,Direction.SOUTH,Direction.WEST));
                  if(bits[x][y] && BIT_NE != 0){
                     ret.add(BgDrawable.cornerNE(x,y));
                  }
               }
            }
            else if(bits[x][y] && BIT_EAST != 0){
               // SE l +?
               ret.add(BgDrawable.innerShapeL(x,y,Direction.SOUTH,Direction.EAST));
               if(bits[x][y] && BIT_NW != 0){
                  ret.add(BgDrawable.cornerNW(x,y));
               }
            }
            else{
               // S i ++?
               ret.add(BgDrawable.edge(x,y,Direction.SOUTH));
               if(bits[x][y] && BIT_NW != 0){
                  ret.add(BgDrawable.cornerNW(x,y));
               }
               if(bits[x][y] && BIT_NE != 0){
                  ret.add(BgDrawable.cornerNE(x,y));
               }
            }
         } // SOUTH
         else if(bits[x][y] && BIT_WEST != 0){
            if(bits[x][y] && BIT_EAST != 0){
               // WE ii
               ret.add(BgDrawable.edge(x,y,Direction.WEST));
               ret.add(BgDrawable.edge(x,y,Direction.EAST));
            }
            else{
               // W i ++?
               ret.add(BgDrawable.edge(x,y,Direction.WEST));
               if(bits[x][y] && BIT_NE != 0){
                  ret.add(BgDrawable.cornerNE(x,y));
               }
               if(bits[x][y] && BIT_SE != 0){
                  ret.add(BgDrawable.cornerSE(x,y));
               }
            }
         }
         else if(bits[x][y] && BIT_EAST != 0){
            // E i ++?
            ret.add(BgDrawable.edge(x,y,Direction.EAST));
            if(bits[x][y] && BIT_NW != 0){
               ret.add(BgDrawable.cornerNW(x,y));
            }
            if(bits[x][y] && BIT_SW != 0){
               ret.add(BgDrawable.cornerSW(x,y));
            }
         }
         else{
            // only corners
            if(bits[x][y] && BIT_NW != 0){
               ret.add(BgDrawable.cornerNW(x,y));
            }
            if(bits[x][y] && BIT_NE != 0){
               ret.add(BgDrawable.cornerNE(x,y));
            }
            if(bits[x][y] && BIT_SW != 0){
               ret.add(BgDrawable.cornerSW(x,y));
            }
            if(bits[x][y] && BIT_SE != 0){
               ret.add(BgDrawable.cornerSE(x,y));
            }
         }
      } // for(x)
   } // for(y)
} // method: getOutline()

}
