package github.adjustamat.jigsawpuzzlefloss.pieces;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.shapes.Shape;

import java.util.ArrayList;

import github.adjustamat.jigsawpuzzlefloss.game.BgDrawable;

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


ArrayList<Object> matrix;
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

private void init(int width, int height)
{
   matrixWidth = width;
   matrixHeight = height;
   
   matrix = new ArrayList();
   int len = width * height;
   for (int i = 0; i < len; i++) {
      matrix.add(null);
   }
}

private LargerPiece(SinglePiece p1, SinglePiece p2, Direction dir)
{
   super(p1.containerParent);
   init(dir.startWidth, dir.startHeight);
   pieceCount = 2;
   set(dir.startX1, dir.startY1, p1);
   set(dir.startX2, dir.startY2, p2);
}

private LargerPiece(LargerPiece p1, LargerPiece p2, Point point1, Point point2, Direction dir)
{
   super(p1.containerParent);

//   this(
//      Math.max()
//    p1.matrixWidth + p2.matrixWidth - point2.x - point1.x/* TDO! */,
//    /*TDO! */,
//    p1.containerParent
//   );
   pieceCount = p1.pieceCount + p2.pieceCount;
   int x1Offset = ; // TODO: make sure to care about Direction dir!
   int x2Offset = ;
   int y1Offset = ;
   int y2Offset = ;
   init(Math.max(), Math.max());
   
   // combine matrices
   for (Object obj: p1.matrix) {
      set(p.positionInLargerPiece.x + x1Offset,
       p.positionInLargerPiece.y + y1Offset,
       p);
   }
   for (Object obj: p2.matrix) {
      set(p.positionInLargerPiece.x + x2Offset,
       p.positionInLargerPiece.y + y2Offset,
       p);
   }
}

/*public static LargerPiece combine(AbstractPiece p1, AbstractPiece p2, Direction dir){
   if(p1 instanceof LargerPiece){
      if(p2 instanceof LargerPiece){

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
}*/

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

private int linear(int x, int y)
{
   return y * matrixWidth + x;
}

public Point getPuzzlePiece(PointF mouseOffset)
{
   Point ret = new Point(correctPuzzlePosition);
   ret.x += getX(mouseOffset.x);
   ret.y += getY(mouseOffset.y);
   return ret;
}

public Point getMatrixPiece(PointF offset)
{
   return new Point(getX(offset.x), getY(offset.y));
}

public int getX(float mouseOffsetX)
{
   return (int) (mouseOffsetX / (SIDE_SIZE * matrixWidth));
}

public int getY(float mouseOffsetY)
{
   return (int) (mouseOffsetY / (SIDE_SIZE * matrixHeight));
}

public Object get(int x, int y)
{
   return matrix.get(linear(x, y));
}

public Object get(Point p)
{
   return matrix.get(linear(p.x, p.y));
}

private void set(int x, int y)
{
   matrix.set(linear(x, y), Boolean.TRUE);
}

private void set(int x, int y, SinglePiece p)
{
   set(x, y);
   // TODO: combine SVGPath outline (UNION) and check edgeWidths (positionInContainer is handled by Container)
   // TODO: SVGPath SinglePiece.left, right, top, bottom
   //p.setLargerPiece(this, new Point(x, y)); // no, remove all reference to a SinglePiece, once it is in a LargerPiece!
   if (p.isLeftEdge())
      leftEdge = EdgeType.EDGE;
   if (p.isRightEdge())
      rightEdge = EdgeType.EDGE;
   if (p.isTopEdge())
      topEdge = EdgeType.EDGE;
   if (p.isBottomEdge())
      bottomEdge = EdgeType.EDGE;
}

public void add(SinglePiece newPiece, Point attachedToPiece, Direction dir)
{
   // the new piece may be attached to more than just attachedToPiece!
   
   pieceCount++;
   
   if (attachedToPiece.x == matrixWidth - 1 && dir == Direction.EAST) {
      expandX(matrixWidth);
   }
   else if (attachedToPiece.x == 0 && dir == Direction.WEST) {
      correctPuzzlePosition.x -= 1;
//      for(Object obj: matrix){
//         p.insertPieceLeft();
//      }
      expandX(0);
   }
   else if (attachedToPiece.y == matrixHeight - 1 && dir == Direction.SOUTH) {
      expandY(matrix.size());
   }
   else if (attachedToPiece.y == 0 && dir == Direction.NORTH) {
      correctPuzzlePosition.y -= 1;
//      for(Object obj: matrix){
//         p.insertPieceTop();
//      }
      expandY(0);
   }
   
   set(attachedToPiece.x + dir.directionX, attachedToPiece.y + dir.directionY, newPiece);
}

private void expandX(int i)
{
   matrixWidth++;
   // expand matrix right or left by inserting null items into matrix list.
   for (int j = 0; j < matrixHeight; j++) {
      matrix.add(i, null);
      i += matrixWidth;
   }
}

private void expandY(int i)
{
   matrixHeight++;
   // expand matrix up or down by inserting null items into matrix list.
   for (int j = 0; j < matrixWidth; j++) {
      matrix.add(i, null);
   }
}


public ArrayList<BgDrawable> getBgOutline()
{
   ArrayList<BgDrawable> ret = new ArrayList<>(matrix.size());//+2*matrixWidth+2*matrixHeight+4);

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
   
   int yLimit = matrixHeight - 1;
   int xLimit = matrixWidth - 1;
   int width = matrixWidth;
   int height = matrixHeight;
   int extraX = 0;
   int extraY = 0;
   if (!isLeftEdge()) {
      width++;
      extraX++;
   }
   if (!isRightEdge()) {
      width++;
   }
   if (!isTopEdge()) {
      height++;
      extraY++;
   }
   if (!isBottomEdge()) {
      height++;
   }
   //int[][] bits = new int[width][height];
   
   int x;
   int y;
   // check edges (do not outline EdgeType.EDGE!)
   if (!isLeftEdge()) { // leftmost column (WEST) can be outlined
      x = -1; // outside left edge
      
      // leftmost column: top left corner (NW)
      if (!isTopEdge()) { // leftmost column: NW can be outlined
         y = -1; // outside top edge
         if (matrix.get(linear(x + 1, y + 1)) != null) { // x+1 == 0, y+1 == 0
            // outside left edge, outside top edge (-1, -1)
            ret.add(BgDrawable.cornerSE(x, y));
            // outside left edge, top row (-1, 0)
            ret.add(BgDrawable.edge(x, y + 1, Direction.EAST));
         }
         else if (matrix.get(linear(x + 1, (++y) + 1)) != null) { // x+1 == 0, y+1 == 1
            // outside left edge, top row (-1, 0)
            ret.add(BgDrawable.cornerSE(x, y));
         }
      }
      else { // leftmost column: isTopEdge (NW)
         y = 0; // top row
         if (matrix.get(linear(x + 1, y)) != null) { // x+1 == 0
            // outside left edge, top row (-1, 0)
            ret.add(BgDrawable.edgeShapeRight(x, y, Direction.EAST));
         }
         else if (matrix.get(linear(x + 1, y + 1)) != null) { // x+1 == 0, y+1 == 1
            // outside left edge, top row (-1, 0)
            ret.add(BgDrawable.cornerSE(x, y));
         }
      }
      
      // leftmost column: loop through the middle of the column
      for (y = 1; y < matrixHeight - 1; y++) {
         if (matrix.get(linear(x + 1, y)) != null) { // x+1 == 0
            // outside left edge, middle rows
            ret.add(BgDrawable.edge(x, y, Direction.EAST));
         }
         else {
            if (matrix.get(linear(x + 1, y - 1)) != null) { // x+1 == 0
               // outside left edge, middle rows
               ret.add(BgDrawable.cornerNE(x, y));
            }
            if (matrix.get(linear(x + 1, y + 1)) != null) { // x+1 == 0
               // outside left edge, middle rows
               ret.add(BgDrawable.cornerSE(x, y));
            }
         }
      }
      
      // leftmost column: bottom left corner (SW)
      if (!isBottomEdge()) { // leftmost column: SW can be outlined
         // assert(y == matrixHeight - 1) // bottom row // matrixHeight: outside bottom edge
         if (matrix.get(linear(x + 1, y)) != null) { // x+1 == 0
            // outside left edge, bottom row (-1, matrixHeight - 1)
            ret.add(BgDrawable.edge(x, y, Direction.EAST));
            // outside left edge, outside bottom edge (-1, matrixHeight)
            ret.add(BgDrawable.cornerNE(x, y + 1));
         }
         else if (matrix.get(linear(x + 1, y - 1)) != null) { // x+1 == 0
            // outside left edge, bottom row (-1, matrixHeight - 1)
            ret.add(BgDrawable.cornerNE(x, y));
         }
      }
      else { // leftmost column: isBottomEdge (SW)
         // assert(y == matrixHeight - 1);
         if (matrix.get(linear(0, matrixHeight - 1)) != null) {
            ret.add(BgDrawable.edgeShapeLeft(-1, matrixHeight - 1, Direction.EAST));
         }
         else if (matrix.get(linear(0, matrixHeight - 2)) != null) {
            ret.add(BgDrawable.cornerNE(-1, matrixHeight - 1));
         }
      }
   } // leftmost column (WEST) can be outlined
   if (!isRightEdge()) { // rightmost column (EAST) can be outlined
      // TODO: copy leftEdge
   } // rightmost column (EAST) can be outlined
   if (!isTopEdge()) { // top row (NORTH) can be outlined
      y = -1; // outside top edge
      
      x = 0;
      
      for (x = 1; x < matrixWidth - 1; x++) {
         if (matrix.get(linear(x, 0)) != null) {
            ret.add(BgDrawable.edge(-1, y, Direction.EAST));
         }
         else {
            if (matrix.get(linear(x - 1, 0)) != null) {
               ret.add(BgDrawable.cornerSW(x, -1));
            }
            if (matrix.get(linear(x + 1, 0)) != null) {
               ret.add(BgDrawable.cornerSE(x, -1));
            }
         }
      }
      
      // assert(x == matrixWidth - 1) // rightmost column
      
   } // top row (NORTH) can be outlined
   if (!isBottomEdge()) { // bottom row (SOUTH) can be outlined
   
   } // bottom row (SOUTH) can be outlined
   
   // loop through the middle of the matrix
   for (y = 2; y < matrixHeight - 2; y++) {
      for (x = 2; x < matrixWidth - 2; x++) {
   /*for(int y=1 + extraY;y < height-1; y++){
         for(int x=1 + extraX; x < width-1; x++){*/
         int i = linear(x, y);
         if (matrix.get(i) != null) {
            //if(bits[x][y] == BITS_BG){
            // TODO: just a quarter of the bg! ret.add(BgDrawable.bg(x,y));
            continue;
         }
         if (bits[x][y] && BIT_NORTH != 0) {
            if (bits[x][y] && BIT_SOUTH != 0) {
               if (bits[x][y] && BIT_WEST != 0) {
                  if (bits[x][y] && BIT_EAST != 0) {
                     // NSWE o
                     ret.add(BgDrawable.innerShapeO(x, y));
                  }
                  else {
                     // NSW u
                     ret.add(BgDrawable.innerShapeU(x, y, Direction.EAST));
                  }
               }
               else if (bits[x][y] && BIT_EAST != 0) {
                  // NSE u
                  ret.add(BgDrawable.innerShapeU(x, y, Direction.WEST));
               }
               else {
                  // NS ii
                  ret.add(BgDrawable.edge(x, y, Direction.NORTH));
                  ret.add(BgDrawable.edge(x, y, Direction.SOUTH));
               }
            } // NORTH && SOUTH
            else if (bits[x][y] && BIT_WEST != 0) {
               if (bits[x][y] && BIT_EAST != 0) {
                  // NWE u
                  ret.add(BgDrawable.innerShapeU(x, y, Direction.SOUTH));
               }
               else {
                  // NW l +?
                  ret.add(BgDrawable.innerShapeL(x, y, Direction.NORTH, Direction.WEST));
                  if (bits[x][y] && BIT_SE != 0) {
                     ret.add(BgDrawable.cornerSE(x, y));
                  }
               }
            } // NORTH && !SOUTH && WEST
            else if (bits[x][y] && BIT_EAST != 0) {
               // NE l +?
               ret.add(BgDrawable.innerShapeL(x, y, Direction.NORTH, Direction.EAST));
               if (bits[x][y] && BIT_SW != 0) {
                  ret.add(BgDrawable.cornerSW(x, y));
               }
            } // NORTH && !SOUTH && !WEST && EAST
            else {
               // N i ++?
               ret.add(BgDrawable.edge(x, y, Direction.NORTH));
               if (bits[x][y] && BIT_SW != 0) {
                  ret.add(BgDrawable.cornerSW(x, y));
               }
               if (bits[x][y] && BIT_SE != 0) {
                  ret.add(BgDrawable.cornerSE(x, y));
               }
            } // NORTH && !SOUTH && !WEST && !EAST
         } // NORTH
         else if (bits[x][y] && BIT_SOUTH != 0) {
            if (bits[x][y] && BIT_WEST != 0) {
               if (bits[x][y] && BIT_EAST != 0) {
                  // SWE u
                  ret.add(BgDrawable.innerShapeU(x, y, Direction.NORTH));
               }
               else {
                  // SW l +?
                  ret.add(BgDrawable.innerShapeL(x, y, Direction.SOUTH, Direction.WEST));
                  if (bits[x][y] && BIT_NE != 0) {
                     ret.add(BgDrawable.cornerNE(x, y));
                  }
               }
            }
            else if (bits[x][y] && BIT_EAST != 0) {
               // SE l +?
               ret.add(BgDrawable.innerShapeL(x, y, Direction.SOUTH, Direction.EAST));
               if (bits[x][y] && BIT_NW != 0) {
                  ret.add(BgDrawable.cornerNW(x, y));
               }
            }
            else {
               // S i ++?
               ret.add(BgDrawable.edge(x, y, Direction.SOUTH));
               if (bits[x][y] && BIT_NW != 0) {
                  ret.add(BgDrawable.cornerNW(x, y));
               }
               if (bits[x][y] && BIT_NE != 0) {
                  ret.add(BgDrawable.cornerNE(x, y));
               }
            }
         } // SOUTH
         else if (bits[x][y] && BIT_WEST != 0) {
            if (bits[x][y] && BIT_EAST != 0) {
               // WE ii
               ret.add(BgDrawable.edge(x, y, Direction.WEST));
               ret.add(BgDrawable.edge(x, y, Direction.EAST));
            }
            else {
               // W i ++?
               ret.add(BgDrawable.edge(x, y, Direction.WEST));
               if (bits[x][y] && BIT_NE != 0) {
                  ret.add(BgDrawable.cornerNE(x, y));
               }
               if (bits[x][y] && BIT_SE != 0) {
                  ret.add(BgDrawable.cornerSE(x, y));
               }
            }
         }
         else if (bits[x][y] && BIT_EAST != 0) {
            // E i ++?
            ret.add(BgDrawable.edge(x, y, Direction.EAST));
            if (bits[x][y] && BIT_NW != 0) {
               ret.add(BgDrawable.cornerNW(x, y));
            }
            if (bits[x][y] && BIT_SW != 0) {
               ret.add(BgDrawable.cornerSW(x, y));
            }
         }
         else {
            // only corners
            if (bits[x][y] && BIT_NW != 0) {
               ret.add(BgDrawable.cornerNW(x, y));
            }
            if (bits[x][y] && BIT_NE != 0) {
               ret.add(BgDrawable.cornerNE(x, y));
            }
            if (bits[x][y] && BIT_SW != 0) {
               ret.add(BgDrawable.cornerSW(x, y));
            }
            if (bits[x][y] && BIT_SE != 0) {
               ret.add(BgDrawable.cornerSE(x, y));
            }
         }
      } // for(x)
   } // for(y)
   return ret;
} // method: getOutline()

}
