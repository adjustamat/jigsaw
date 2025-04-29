package github.adjustamat.jigsawpuzzlefloss.pieces;

import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;

import java.util.ArrayList;

import github.adjustamat.jigsawpuzzlefloss.game.BgDrawable;
import github.adjustamat.jigsawpuzzlefloss.pieces.SinglePiece.SinglePieceEdges;

/**
 * Two or more {@link SinglePiece}s that fit together.
 */
public class LargerPiece
 extends AbstractPiece
{
//public static final int BIT_NW = 0b1;
//public static final int BIT_SW = 0b10;
//public static final int BIT_SE = 0b100;
//public static final int BIT_NE = 0b1000;
//public static final int BITMASK_CORNERS = 0b1111;
//public static final int BIT_WEST = 0b10000;
//public static final int BIT_SOUTH = 0b100000;
//public static final int BIT_EAST = 0b1000000;
//public static final int BIT_NORTH = 0b10000000;
//public static final int BITS_BG = 0b111111111;

private final LargerPieceEdges svgEdges;
private ArrayList<OuterEdgesIndices> matrix; // remove all reference to a SinglePiece, once it is in a LargerPiece!
int matrixWidth;
int matrixHeight;
int pieceCount;

boolean westEdge;
boolean northEdge;
boolean eastEdge;
boolean southEdge;

static class OuterEdgesIndices
{
   Integer n, e, s, w; // TODO: delete these fields!
   final Integer[] nesw = new Integer[4];
   //Point pointInMatrix;
   
   void set (int direction, Integer value){
      nesw[direction] = value;
   }
   
   Integer get (int direction){
      return nesw[direction];
   }
   
}

class LargerPieceEdges
 extends SVGEdges
{
   private final ArrayList<WholeEdge> outerEdges = new ArrayList<>();
   private final ArrayList<Point> outerPieces = new ArrayList<>();
   private final ArrayList<Direction> pieceEdges = new ArrayList<>();
   
   private final ArrayList<WholeEdge> innerEdges = new ArrayList<>();
   
   LargerPieceEdges (LargerPiece p1, LargerPiece p2,
    Point point1, Point point2, Direction dir, int offsetX1, int offsetX2, int offsetY1, int offsetY2
   ){
      // TODO: combine SVGPath outline (my own UNION) - indexes in matrix need to change too
      // TODO: the new piece may be attached to more than just "attachedTo"!

//      LargerPieceEdges edges1 = p1.svgEdges;
//      LargerPieceEdges edges2 = p2.svgEdges;
      
      //     TODO:      outerPieces.add(); // Point
      //     TODO:      pieceEdges.add(); // Direction
      
      { // combine matrices
         int matrixX = 0;
         int matrixY = 0;
         for (OuterEdgesIndices value: p1.matrix) {
            if (value != null)
               set(matrixX + offsetX1, matrixY + offsetY1, value); // TODO: combine outlines (value should change)
            matrixX++;
            if (matrixX == p1.matrixWidth) {
               matrixY++;
               matrixX = 0;
            }
         }
         matrixX = 0;
         matrixY = 0;
         for (OuterEdgesIndices value: p2.matrix) {
            if (value != null)
               set(matrixX + offsetX2, matrixY + offsetY2, value); // TODO: combine outline (value should change)
            matrixX++;
            if (matrixX == p2.matrixWidth) {
               matrixY++;
               matrixX = 0;
            }
         }
      } // combine matrices
      
      
   }
   
   LargerPieceEdges (SinglePieceEdges p1, SinglePieceEdges p2, Direction dir){
      // TODO: use a List internally, so that I can insert the path of a new piece into a LargerPiece shape.
      //  keep a record of which piece (x,y) is where in the linked list! (not needed for innerEdges.)
      
      Point point1 = new Point(dir.initX1, dir.initY1);
      Point point2 = new Point(dir.initX2, dir.initY2);
      OuterEdgesIndices indices1 = new OuterEdgesIndices();
      OuterEdgesIndices indices2 = new OuterEdgesIndices();
      
      WholeEdge first1, first2;
      Direction dirFirst1, dirFirst2;
      switch (dir) {
      case NORTH:
         innerEdges.add(p1.n.resetInnerEdge()); // only need one! innerEdges.add(p2.s.resetInnerEdge());
         first1 = p1.e;
         first2 = p2.w;
         dirFirst1 = Direction.EAST;
         dirFirst2 = Direction.WEST;
         break;
      case EAST:
         innerEdges.add(p1.e.resetInnerEdge()); // only need one! innerEdges.add(p2.w.resetInnerEdge());
         first1 = p1.s;
         first2 = p2.n;
         dirFirst1 =;
         dirFirst2 =;
         break;
      case SOUTH:
         innerEdges.add(p1.s.resetInnerEdge()); // only need one! innerEdges.add(p2.n.resetInnerEdge());
         first1 = p1.w;
         first2 = p2.e;
         dirFirst1 =;
         dirFirst2 =;
         break;
      default://case WEST:
         innerEdges.add(p1.w.resetInnerEdge()); // only need one! innerEdges.add(p2.e.resetInnerEdge());
         first1 = p1.n;
         first2 = p2.s;
         dirFirst1 =;
         dirFirst2 =;
      }
      
      WholeEdge next;
      
      outerEdges.add(next = first1);
      outerEdges.add(next = next.getNext());
      outerEdges.add(next = next.getNext());
      next.setNext(first2);
      outerPieces.add(point1);
      outerPieces.add(point1);
      outerPieces.add(point1);
      int i = dirFirst1.ordinal();
      pieceEdges.add(dirFirst1);
      pieceEdges.add(dirFirst1.next());
      pieceEdges.add(dirFirst1.opposite());
      indices1.set(i, 0);
      indices1.set(Direction.cycle(++i), 1);
      indices1.set(Direction.cycle(++i), 2);
      set(dir.initX1, dir.initY1, indices1);
      
      
      outerEdges.add(next = first2);
      outerEdges.add(next = next.getNext());
      outerEdges.add(next = next.getNext());
      next.setNext(first1);
      outerPieces.add(point2);
      outerPieces.add(point2);
      outerPieces.add(point2);
      i = dirFirst2.ordinal();
      pieceEdges.add(dirFirst2);
      pieceEdges.add(dirFirst2.next());
      pieceEdges.add(dirFirst2.opposite());
      indices2.set();
      indices2.set();
      indices2.set();
//      indices1.set(i, 0);
//      indices1.set(Direction.cycle(++i), 1);
//      indices1.set(Direction.cycle(++i), 2);
      set(dir.initX2, dir.initY2, indices2);
      
   }
   
   void addEdges (SinglePieceEdges newEdges, Point attachedTo, Direction dir){
      // TODO: increase index with 1 for all that are >= the index of newPiece's outline.
      
      OuterEdgesIndices newMatrixValue;// = null;//new OuterEdges();
      
      WholeEdge first1, first2;
      OuterEdgesIndices[] neswValues = new OuterEdgesIndices[4];
      switch (dir) {
      case NORTH:
         // TODO:  attachedTo.x + dir.directionX,   attachedTo.y + dir.directionY
         //neswValues[dir.ordinal()]
         OuterEdgesIndices sMe = get(attachedTo);
         
         //neswValues[dir.opposite().ordinal()]
         // attachedTo.x + 2 * directionX, attachedTo.y + 2 * directionY
         OuterEdgesIndices n = getOrNull(attachedTo.x, attachedTo.y - 2);
         //neswValues[dir.next().positive().ordinal()] // or dir.next()
         // attachedTo.x + directionX + perpendicularX, attachedTo.y + directionY + perpendicularY
         OuterEdgesIndices e = getOrNull(attachedTo.x + 1, attachedTo.y - 1);
         //neswValues[dir.next().positive().opposite().ordinal()] // or dir.prev()
         // attachedTo.x + directionX - perpendicularX, attachedTo.y + directionY - perpendicularY
         OuterEdgesIndices w = getOrNull(attachedTo.x - 1, attachedTo.y - 1);
         
         
         // TODO: the new piece may be attached to more than just "attachedTo"!
         WholeEdge wholeEdge = outerEdges.get(get(attachedTo).n);
         
         //     TODO:      outerPieces.add(); // Point
         //     TODO:      pieceEdges.add(); // Direction

//    TODO:     innerEdges.add(p1.n.resetInnerEdge()); // only need one! innerEdges.add(p2.s.resetInnerEdge());
//         first1 = p1.e;
//         first2 = p2.w;
         break;
      case EAST:
         innerEdges.add(p1.e.resetInnerEdge()); // only need one! innerEdges.add(p2.w.resetInnerEdge());
         first1 = p1.s;
         first2 = p2.n;
         break;
      case SOUTH:
         innerEdges.add(p1.s.resetInnerEdge()); // only need one! innerEdges.add(p2.n.resetInnerEdge());
         first1 = p1.w;
         first2 = p2.e;
         break;
      default://case WEST:
         innerEdges.add(p1.w.resetInnerEdge()); // only need one! innerEdges.add(p2.e.resetInnerEdge());
         first1 = p1.n;
         first2 = p2.s;
      }
      WholeEdge next;
      
      // TODO: newMatrixValue
      set(attachedTo.x + dir.directionX, attachedTo.y + dir.directionY, newMatrixValue);
   }
   
   public void appendToOutline (Path path){
      for (WholeEdge wholeEdge: outerEdges) {
         wholeEdge.appendSegmentsTo(path);
      }
   }
   
   public ArrayList<WholeEdge> getInnerEdges (){
      return innerEdges;
   }
   
}

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

private LargerPiece (LargerPiece p1, LargerPiece p2, Point point1, Point point2, Direction dir){
   super(p1.containerParent, p1.currentRotationNorthDirection);
   
   int diffX = point1.x + dir.directionX - point2.x;
   int offsetX1 = diffX < 0 ?-diffX :0;
   int offsetX2 = diffX > 0 ?diffX :0;
   int diffY = point1.y + dir.directionY - point2.y;
   int offsetY1 = diffY < 0 ?-diffY :0;
   int offsetY2 = diffY > 0 ?diffY :0;
   
   init(Math.max(offsetX1 + p1.matrixWidth, offsetX2 + p2.matrixWidth),
    Math.max(offsetY1 + p1.matrixHeight, offsetY2 + p2.matrixHeight));
   
   pieceCount = p1.pieceCount + p2.pieceCount;
   westEdge = p1.isWestEdge() || p2.isWestEdge();
   eastEdge = p1.isEastEdge() || p2.isEastEdge();
   northEdge = p1.isNorthEdge() || p2.isNorthEdge();
   southEdge = p1.isSouthEdge() || p2.isSouthEdge();
   
   // svgEdges also combines the matrices!
   svgEdges = new LargerPieceEdges(p1, p2, point1, point2, dir, offsetX1, offsetX2, offsetY1, offsetY2);
   
   // TODO: check edgeWidths (positionInContainer is handled by Container)
   
}

private LargerPiece (SinglePiece p1, SinglePiece p2, Direction dir){
   super(p1.containerParent, p1.currentRotationNorthDirection);
   init(dir.initWidth, dir.initHeight);
   
   pieceCount = 2;
   setIsEdge(p1);
   setIsEdge(p2);
   
   // the matrix is filled in LargerPieceEdges constructor!
   svgEdges = new LargerPieceEdges(p1.svgEdges, p2.svgEdges, dir);
   
   // TODO: check edgeWidths (positionInContainer is handled by Container)
}

public void add (SinglePiece newPiece, Point attachedTo, Direction dir){
   // TODO: check edgeWidths (positionInContainer is handled by Container)
   pieceCount++;
   if (attachedTo.x == 0 && dir == Direction.WEST) {
      correctPuzzlePosition.x -= 1;
      expandX(0);
      attachedTo.x = 1; // update to the coordinates in the expanded matrix
   }
   else if (attachedTo.y == 0 && dir == Direction.NORTH) {
      correctPuzzlePosition.y -= 1;
      expandY(0);
      attachedTo.y = 1; // update to the coordinates in the expanded matrix
   }
   else if (attachedTo.x == matrixWidth - 1 && dir == Direction.EAST) {
      expandX(matrixWidth);
   }
   else if (attachedTo.y == matrixHeight - 1 && dir == Direction.SOUTH) {
      expandY(matrix.size());
   }
   // TODO: svgEdges also sets matrix value.
   svgEdges.addEdges(newPiece.svgEdges, attachedTo, dir);
   setIsEdge(newPiece);
}

private void init (int width, int height){
   matrixWidth = width;
   matrixHeight = height;
   
   matrix = new ArrayList<>();
   int len = width * height;
   for (int i = 0; i < len; i++) {
      matrix.add(null/*new OuterEdges()*/);
   }
}

private int linear (int x, int y){
   return y * matrixWidth + x;
}

private Integer checkBounds (int x, int y){
   int ret = y * matrixWidth + x;
   if (ret < 0 || ret >= matrix.size())
      return null;
   return ret;
}

//private int xFromLinear (int linear){
//   return linear % matrixWidth;
//}
//
//private int yFromLinear (int linear){
//   return linear / matrixWidth;
//}

public Point getPuzzlePiece (PointF mouseOffset){
   Point ret = new Point(correctPuzzlePosition);
   ret.x += getX(mouseOffset.x);
   ret.y += getY(mouseOffset.y);
   return ret;
}

public Point getMatrixPiece (PointF mouseOffset){
   return new Point(getX(mouseOffset.x), getY(mouseOffset.y));
}

public int getX (float mouseOffsetX){
   return (int) (mouseOffsetX / (SIDE_SIZE * matrixWidth));
}

public int getY (float mouseOffsetY){
   return (int) (mouseOffsetY / (SIDE_SIZE * matrixHeight));
}

private OuterEdgesIndices getOrNull (int x, int y){
   Integer linear = checkBounds(x, y);
   if (linear == null)
      return null;
   return matrix.get(linear);
}

private OuterEdgesIndices get (Point p){
   return matrix.get(linear(p.x, p.y));
}

private void set (int x, int y, OuterEdgesIndices value){
   matrix.set(linear(x, y), value);
}

private void setIsEdge (SinglePiece p){
   if (p.isWestEdge())
      westEdge = true;//leftEdge = EdgeType.EDGE;
   if (p.isEastEdge())
      eastEdge = true;//rightEdge = EdgeType.EDGE;
   if (p.isNorthEdge())
      northEdge = true;//topEdge = EdgeType.EDGE;
   if (p.isSouthEdge())
      southEdge = true;//bottomEdge = EdgeType.EDGE;
}

private void set (int x, int y, OuterEdgesIndices value, SinglePiece p){
   set(x, y, value);
   //p.setLargerPiece(this, new Point(x, y));
   
}

private void expandX (int i){
   matrixWidth++;
   // expand matrix right or left by inserting null items into matrix list.
   for (int j = 0; j < matrixHeight; j++) {
      matrix.add(i, null);
      i += matrixWidth;
   }
}

private void expandY (int i){
   matrixHeight++;
   // expand matrix up or down by inserting null items into matrix list.
   for (int j = 0; j < matrixWidth; j++) {
      matrix.add(i, null);
   }
}

public ArrayList<BgDrawable> getBgOutline (){
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
   if (!isWestEdge()) {
      width++;
      extraX++;
   }
   if (!isEastEdge()) {
      width++;
   }
   if (!isNorthEdge()) {
      height++;
      extraY++;
   }
   if (!isSouthEdge()) {
      height++;
   }
   //int[][] bits = new int[width][height];
   
   int x;
   int y;
   // check edges (do not outline EdgeType.EDGE!)
   if (!isWestEdge()) { // leftmost column (WEST) can be outlined
      x = -1; // outside left edge
      
      // leftmost column: top left corner (NW)
      if (!isNorthEdge()) { // leftmost column: NW can be outlined
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
      if (!isSouthEdge()) { // leftmost column: SW can be outlined
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
   if (!isEastEdge()) { // rightmost column (EAST) can be outlined
      // TODO: copy leftEdge
   } // rightmost column (EAST) can be outlined
   if (!isNorthEdge()) { // top row (NORTH) can be outlined
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
   if (!isSouthEdge()) { // bottom row (SOUTH) can be outlined
   
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

public RectF getEdgeWidths (){
   return new RectF(); // TODO! see SinglePiece.getEdgeWidths()!
}

public boolean isWestEdge (){
   return westEdge;
}

public boolean isNorthEdge (){
   return northEdge;
}

public boolean isEastEdge (){
   return eastEdge;
}

public boolean isSouthEdge (){
   return southEdge;
}
}
