package github.adjustamat.jigsawpuzzlefloss.pieces;

import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;

import github.adjustamat.jigsawpuzzlefloss.game.BorderDrawable;
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

/**
 * The outline to draw when rotating or when drawing embossed 3D-effect.
 */
private final LargerPieceEdges vectorEdges;

private ArrayList<OuterEdgeIndices> matrix; // remove all reference to a SinglePiece, once it is in a LargerPiece!

int matrixWidth;
int matrixHeight;
int pieceCount;

boolean westEdge;
boolean northEdge;
boolean eastEdge;
boolean southEdge;

private final RectF edgeWidths = new RectF();

static class OuterEdgeIndex
{
   int hole;
   int index;
   
   public OuterEdgeIndex(int hole, int index)
   {
      this.hole = hole;
      this.index = index;
   }
} // class OuterEdgeIndex

static class OuterEdgeIndices
{
   private final ArrayList<OuterEdgeIndex> nesw = new ArrayList<>(4);
   
   OuterEdgeIndices()
   {
      nesw.add(null);
      nesw.add(null);
      nesw.add(null);
      nesw.add(null);
   }
   
   OuterEdgeIndex getIndex(int direction)
   {
      return nesw.get(direction);
   }
   
   void set(int direction, int hole, @NonNull Integer index)
   {
      nesw.set(direction, new OuterEdgeIndex(hole, index));
   }

//   void remove(int direction)
//   {
//      nesw.set(direction, null);
//   }
   
   OuterEdgeIndex getIndex(Direction direction)
   {
      return getIndex(direction.ordinal());
   }
   
   void set(Direction direction, int hole, @NonNull Integer index)
   {
      set(direction.ordinal(), hole, index);
   }

//   void remove(Direction direction)
//   {
//      remove(direction.ordinal());
//   }
} // class OuterEdgeIndices

class LargerPieceEdges
 extends VectorEdges
{
   private final ArrayList<PieceEdge> innerEdges = new ArrayList<>();
   //   private final ArrayList<WholeEdge> outerEdges = new ArrayList<>();
   private final ArrayList<ArrayList<PieceEdge>> outerEdgeHoles = new ArrayList<>(2);
   private int[] removed;
   
   LargerPieceEdges(LargerPiece p1, LargerPiece p2,
    Point point1, Point point2, Direction dir, int offsetX1, int offsetX2, int offsetY1, int offsetY2)
   {
      // TODO: combine outlines and innerEdges (my own UNION) - indexes in matrix need to change too
      // TODO: outerEdgeHoles is empty, only its CAPACITY is two! Add at least one list to it!
      // TODO: make sure when combining two LargerPieces that I know there can be nulls in both outerEdgeHoles lists!
      
      LargerPieceEdges edges1 = p1.vectorEdges;
      LargerPieceEdges edges2 = p2.vectorEdges;
      int matrixX, matrixY;
      
      innerEdges.addAll(edges1.getInnerEdges());
      innerEdges.addAll(edges2.getInnerEdges());
      
      { // combine matrices:
         matrixX = 0;
         matrixY = 0;
         for (OuterEdgeIndices indices1: p1.matrix) {
            if (indices1 != null)
               setSubPieceIndices(
                matrixX + offsetX1, matrixY + offsetY1,
                indices1 // TODO: combine outlines (indices1 value(s) should change)
               );
            matrixX++;
            if (matrixX == p1.matrixWidth) {
               matrixY++;
               matrixX = 0;
            }
         }
         matrixX = 0;
         matrixY = 0;
         for (OuterEdgeIndices indices2: p2.matrix) {
            if (indices2 != null)
               setSubPieceIndices(
                matrixX + offsetX2, matrixY + offsetY2,
                indices2 // TODO: combine outline (indices should change)
               );
            matrixX++;
            if (matrixX == p2.matrixWidth) {
               matrixY++;
               matrixX = 0;
            }
         }
      } // combine matrices
      
      matrixX = 0;
      matrixY = 0;
      // TODO: where the hell do I add to holes? and how do I link together the subchain from p1 and p2?
      //  outerEdgeHoles is empty, only its CAPACITY is two! Add at least one list to it!
      
      for (OuterEdgeIndices indices: matrix) {
         if (indices != null) {
            // TODO: check around the point which edges are inner and outer!
            //  some inner edges are already in innerEdges, some need to be added to innerEdges!
            //  no edge can go from inner to outer!
            OuterEdgeIndices eastIndicesOrNull = getSubPieceIndicesOrNull(matrixX + 1, matrixY);
            OuterEdgeIndices westIndicesOrNull = getSubPieceIndicesOrNull(matrixX - 1, matrixY);
            OuterEdgeIndices southIndicesOrNull = getSubPieceIndicesOrNull(matrixX, matrixY + 1);
            OuterEdgeIndices northIndicesOrNull = getSubPieceIndicesOrNull(matrixX, matrixY - 1);
            if (eastIndicesOrNull != null) {
               OuterEdgeIndex index = indices.nesw.get(1);
               if (index != null) {
                  PieceEdge outerEdge = getOuterEdge(index);
                  if (outerEdge != null && outerEdge.getSubPieceDir().ordinal() == 1) {
                     // move to innerEdges! do not add to outerEdges.
                     
                  }
                  
               }
            }
         }
         matrixX++;
         if (matrixX == matrixWidth) {
            matrixY++;
            matrixX = 0;
         }
      }
      
      
      removed = new int[outerEdgeHoles.size()];
   } // LargerPieceEdges(LargerPiece, LargerPiece, Point, Point, Direction, int, int, int, int)
   
   LargerPieceEdges(SinglePieceEdges p1, SinglePieceEdges p2, Direction dir)
   {
      ArrayList<PieceEdge> outerEdges = new ArrayList<>();
      outerEdgeHoles.add(outerEdges);
      // only need one innerEdge! skip the same one from p2.
      innerEdges.add(p1.nesw[dir.ordinal()]);
      
      Direction dirNext = dir.next();
      Direction dirPrev = dir.prev();
      
      // UNION: add the six outer edges from p1 and p2 to outerEdges, and save the list indices in the matrix.
      
      // the point in the LargerPiece matrix that corresponds to the next 3 edges in the list
      Point point1 = new Point(dir.initX1, dir.initY1);
      // the index references to store at this point
      OuterEdgeIndices indices1 = new OuterEdgeIndices();
      
      PieceEdge firstEdge = p1.nesw[dirNext.ordinal()];
      PieceEdge nextEdge;
      addOuterEdge(0, outerEdges, nextEdge = firstEdge, point1, dirNext, indices1
      );
//      indices1.set(currentDir = dirNext, 0, 0);
//      outerEdges.add(nextEdge.setSubPiece(point1, currentDir));
      addOuterEdge(0, outerEdges, nextEdge = nextEdge.getNext(), point1, dir.opposite(), indices1
      );
//      indices1.set(currentDir = dirNext.next(), 0, 1);
//      outerEdges.add(nextEdge = nextEdge.getNext().setSubPiece(point1, currentDir));
      addOuterEdge(0, outerEdges, nextEdge = nextEdge.getNext(), point1, dirPrev, indices1
      );
//      indices1.set(currentDir = dirNext.opposite(), 0, 2);
//      outerEdges.add(nextEdge = nextEdge.getNext().setSubPiece(point1, currentDir));
      
      // link together the edges from p1 and p2
      nextEdge.linkNext(
       nextEdge = p2.nesw[dirPrev.ordinal()]);
      
      // the point in the LargerPiece matrix that corresponds to the next 3 edges in the list
      Point point2 = new Point(dir.initX2, dir.initY2);
      // the index references to store at this point
      OuterEdgeIndices indices2 = new OuterEdgeIndices();
      
      addOuterEdge(0, outerEdges, nextEdge, point2, dirPrev, indices2
      );
      addOuterEdge(0, outerEdges, nextEdge = nextEdge.getNext(), point2, dir, indices2
      );
      addOuterEdge(0, outerEdges, nextEdge = nextEdge.getNext(), point2, dirNext, indices2
      );
//      indices2.set(currentDir, 0, 3);
//      outerEdges.add(nextEdge.setSubPiece(point2, currentDir = dirPrev));
//      indices2.set(currentDir, 0, 4); = dirPrev.next()
//      outerEdges.add(nextEdge = nextEdge.getNext().setSubPiece(point2, currentDir));
//      indices2.set(currentDir, 0, 5); currentDir = dirPrev.opposite()
//      outerEdges.add(nextEdge = nextEdge.getNext().setSubPiece(point2,));
      
      // link together the edges from p1 and p2 a second time
      nextEdge.linkNext(firstEdge);
      
      // store the list index references in the LargerPiece matrix
      setSubPieceIndices(dir.initX1, dir.initY1, indices1);
      setSubPieceIndices(dir.initX2, dir.initY2, indices2);
      
      removed = new int[1];
   } // LargerPieceEdges(SinglePieceEdges, SinglePieceEdges, Direction)
   
   /**
    * Add the edges of a SinglePiece attaching to at least one subPiece.
    * @param singlePieceEdges the edges of the new piece
    * @param subPiece the subPiece attaching to the new piece
    * @param dir the direction of attachment
    */
   void addPieceEdges(SinglePieceEdges singlePieceEdges, Point subPiece, Direction dir)
   {
      // check all around the new piece if we already have pieces there:
      
      // subPiece has the direction of dir.opposite() from the perspective of the new piece being added
      OuterEdgeIndices opposite = getSubPieceIndices(subPiece);
      
      OuterEdgeIndices behind = getSubPieceIndicesOrNull(
       subPiece.x + 2 * dir.x,
       subPiece.y + 2 * dir.y
      );
      boolean behindNull = behind == null;
      
      Direction dirNext = dir.next();
      OuterEdgeIndices next = getSubPieceIndicesOrNull(
       subPiece.x + dir.x + dirNext.x,
       subPiece.y + dir.y + dirNext.y
      );
      boolean nextNull = next == null;
      
      Direction dirPrev = dir.prev(); // perpendicular.opposite()
      OuterEdgeIndices prev = getSubPieceIndicesOrNull(
       subPiece.x + dir.x + dirPrev.x,
       subPiece.y + dir.y + dirPrev.y
      );
      boolean prevNull = prev == null;
      
      // we know the new piece is attaching to "subPiece", but check if it is attaching to more pieces:
      int sum = 1;
      // the direction of the attaching edge is the opposite of the direction of attachment
      OuterEdgeIndex attachingIndex = opposite.getIndex(dir);
      // the attaching edge is now an inner edge. detach:
      PieceEdge attachingEdge = removeOuterEdge(attachingIndex);
      innerEdges.add(attachingEdge);
      
      // check behind the new piece
      if (!behindNull) {
         sum++;
         // the direction of the attaching edge is the opposite of the direction of attachment
         attachingIndex = behind.getIndex(dir.opposite());
         // the attaching edge is now an inner edge. detach:
         attachingEdge = removeOuterEdge(attachingIndex);
         innerEdges.add(attachingEdge);
      }
      
      // check the clockwise side of the new piece (newEdges == singlePieceEdges)
      if (!nextNull) {
         sum++;
         // the direction of the attaching edge is the opposite of the direction of attachment
         attachingIndex = next.getIndex(dirPrev);
         // the attaching edge is now an inner edge. detach:
         attachingEdge = removeOuterEdge(attachingIndex);
         innerEdges.add(attachingEdge);
      }
      
      // check the last side
      if (!prevNull) {
         sum++;
         // the direction of the attaching edge is the opposite of the direction of attachment
         attachingIndex = prev.getIndex(dirNext);
         // the attaching edge is now an inner edge. detach:
         attachingEdge = removeOuterEdge(attachingIndex);
         innerEdges.add(attachingEdge);
      }
      
      // store a new instance of OuterEdgesIndices in the matrix, at the new Point being added
      Point newSubPiece = new Point(subPiece.x + dir.x, subPiece.y + dir.y);
      OuterEdgeIndices newIndices = new OuterEdgeIndices();
      setSubPieceIndices(newSubPiece.x, newSubPiece.y, newIndices);
      
      // do the attaching of the new piece:
      attachingIndex = opposite.getIndex(dir);
      switch (sum) {
      case 1: { // the attached direction is the opposite of dir
         
         attachingEdge = innerEdges.get(innerEdges.size() - 1);
         ArrayList<PieceEdge> outerEdges = getOuterEdges(attachingIndex);
         PieceEdge firstNewEdge = singlePieceEdges.nesw[dir.prev().ordinal()];
         PieceEdge middleNewEdge = firstNewEdge.getNext();
         PieceEdge lastNewEdge = middleNewEdge.getNext(); // singlePieceEdges.nesw[dir.next().ordinal()]
         
         OuterEdgeIndices behindNext = getSubPieceIndicesOrNull(
          subPiece.x + 2 * dir.x + dirNext.x, //dir.perpendicularX,
          subPiece.y + 2 * dir.y + dirNext.y //dir.perpendicularY
         ); // subPiece.x + 1, subPiece.y - 1
         
         OuterEdgeIndices behindPrev = getSubPieceIndicesOrNull(
          subPiece.x + 2 * dir.x + dirPrev.x,//- dir.perpendicularX,
          subPiece.y + 2 * dir.y + dirPrev.y//- dir.perpendicularY
         ); // subPiece.x - 1, subPiece.y - 1
         
         if (behindNext != null) {
            if (behindPrev != null) { // both behindNext and behindPrev (2 new holes)
               
               // get the edges of behindPrev and behindNext, for linking.
               PieceEdge behindPrevNext = outerEdges.get(behindPrev.getIndex(dirNext).index);
               PieceEdge behindPrevOpposite = outerEdges.get(behindPrev.getIndex(dir.opposite()).index);
               PieceEdge behindNextPrev = outerEdges.get(behindNext.getIndex(dirPrev).index);
               PieceEdge behindNextOpposite = outerEdges.get(behindNext.getIndex(dir.opposite()).index);
               
               // link (attach) the first new edge
               firstNewEdge.linkNext(behindPrevOpposite);
               attachingEdge.getPrev().linkNext(firstNewEdge);
               
               // link (attach) the second new edge
               middleNewEdge.linkNext(behindNextPrev);
               behindPrevNext.linkNext(middleNewEdge);
               
               // link (attach) the third new edge
               lastNewEdge.linkNext(attachingEdge.getNext());
               behindNextOpposite.linkNext(lastNewEdge);
               
               // add the first new edge so it gets an index
               addOuterEdge(attachingIndex.hole, outerEdges, firstNewEdge, newSubPiece, dir.prev(), newIndices);
               
               // create the first new hole
               ArrayList<PieceEdge> newHole = new ArrayList<>();
               int hole2 = outerEdgeHoles.size();
               addHole(newHole);
               
               // add middleNewEdge to the first new hole
               addOuterEdge(hole2, newHole, middleNewEdge, newSubPiece, dirPrev, newIndices);
               // add the rest of the linked edges to the new hole and update the indices
               PieceEdge nextEdge;
               nextEdge = attachingEdge.getNext();
               while (nextEdge != middleNewEdge) {
                  OuterEdgeIndices indices = getSubPieceIndices(nextEdge.getSubPiece());
                  indices.set(nextEdge.getSubPieceDir(), hole2, newHole.size());
                  newHole.add(nextEdge);
                  nextEdge = nextEdge.getNext();
               }
               
               // create the second new hole
               newHole = new ArrayList<>();
               hole2++;
               addHole(newHole);
               
               // add lastNewEdge to the first new hole
               addOuterEdge(hole2, newHole, lastNewEdge, newSubPiece, dirPrev, newIndices);
               // add the rest of the linked edges to the new hole and update the indices
               nextEdge = behindNextPrev;
               while (nextEdge != lastNewEdge) {
                  OuterEdgeIndices indices = getSubPieceIndices(nextEdge.getSubPiece());
                  indices.set(nextEdge.getSubPieceDir(), hole2, newHole.size());
                  newHole.add(nextEdge);
                  nextEdge = nextEdge.getNext();
               }
               
            } // both behindNext and behindPrev (2 new holes)
            else { // only behindNext (new hole)
               
               // get the edges of behindNext, for linking.
               PieceEdge behindNextPrev = outerEdges.get(behindNext.getIndex(dirPrev).index);
               PieceEdge behindNextOpposite = outerEdges.get(behindNext.getIndex(dir.opposite()).index);
               
               // link (attach) the first and second new edges
               attachingEdge.getPrev().linkNext(firstNewEdge);
               middleNewEdge.linkNext(behindNextPrev);
               
               // add the new edges so they get indices
               addOuterEdge(attachingIndex.hole, outerEdges, firstNewEdge, newSubPiece, dir.prev(), newIndices);
               addOuterEdge(attachingIndex.hole, outerEdges, middleNewEdge, newSubPiece, dir, newIndices);
               
               // create the new hole
               ArrayList<PieceEdge> newHole = new ArrayList<>();
               int hole2 = outerEdgeHoles.size();
               addHole(newHole);
               
               // link (attach) the third new edge
               lastNewEdge.linkNext(attachingEdge.getNext());
               behindNextOpposite.linkNext(lastNewEdge);
               
               // add lastNewEdge to the new hole
               addOuterEdge(hole2, newHole, lastNewEdge, newSubPiece, dirPrev, newIndices);
               // add the rest of the linked edges to the new hole and update the indices
               PieceEdge nextEdge = attachingEdge.getNext();
               while (nextEdge != lastNewEdge) {
                  OuterEdgeIndices indices = getSubPieceIndices(nextEdge.getSubPiece());
                  indices.set(nextEdge.getSubPieceDir(), hole2, newHole.size());
                  newHole.add(nextEdge);
                  nextEdge = nextEdge.getNext();
               }
               
            } // only behindNext (new hole)
         }
         else if (behindPrev != null) { // only behindPrev (new hole)
            
            // get the edges of behindPrev, for linking.
            PieceEdge behindPrevNext = outerEdges.get(behindPrev.getIndex(dirNext).index);
            PieceEdge behindPrevOpposite = outerEdges.get(behindPrev.getIndex(dir.opposite()).index);
            
            // link (attach) the second and third new edges
            behindPrevNext.linkNext(middleNewEdge);
            lastNewEdge.linkNext(attachingEdge.getNext());
            
            // add the new edges so they get indices
            addOuterEdge(attachingIndex.hole, outerEdges, middleNewEdge, newSubPiece, dir, newIndices);
            addOuterEdge(attachingIndex.hole, outerEdges, lastNewEdge, newSubPiece, dir.next(), newIndices);
            
            // create the new hole
            ArrayList<PieceEdge> newHole = new ArrayList<>();
            int hole2 = outerEdgeHoles.size();
            addHole(newHole);
            
            // link (attach) the first new edge
            firstNewEdge.linkNext(behindPrevOpposite);
            attachingEdge.getPrev().linkNext(firstNewEdge);
            
            // add firstNewEdge to the first new hole
            addOuterEdge(hole2, newHole, firstNewEdge, newSubPiece, dirPrev, newIndices);
            // add the rest of the linked edges to the new hole and update the indices
            PieceEdge nextEdge = behindPrevOpposite;
            while (nextEdge != firstNewEdge) {
               OuterEdgeIndices indices = getSubPieceIndices(nextEdge.getSubPiece());
               indices.set(nextEdge.getSubPieceDir(), hole2, newHole.size());
               newHole.add(nextEdge);
               nextEdge = nextEdge.getNext();
            }
            
         } // only behindPrev (new hole)
         else { // no behindPrev or behindNext (no new hole)
            
            // link (attach) the first new edge
            attachingEdge.getPrev().linkNext(firstNewEdge);
            
            // link (attach) the third new edge
            lastNewEdge.linkNext(attachingEdge.getNext());
            
            // add the new edges so they get indices
            addOuterEdge(attachingIndex.hole, outerEdges, firstNewEdge, newSubPiece, dir.prev(), newIndices);
            addOuterEdge(attachingIndex.hole, outerEdges, middleNewEdge, newSubPiece, dir, newIndices);
            addOuterEdge(attachingIndex.hole, outerEdges, lastNewEdge, newSubPiece, dir.next(), newIndices);
            
         } // no behindPrev or behindNext (no new hole)
         
         break; // case sum == 1
      }
      case 2: { // the attached directions are the opposites of emptyDir1 and emptyDir2
         
         ArrayList<PieceEdge> outerEdges = getOuterEdges(attachingIndex);
         
         if (behindNull) { // corner:
            boolean switched = false;
            
            if (nextNull) { // nextNull (corner: dir.opposite() and dirPrev)
               // switch directions so that ... TODO: DEBUG: does it work? test!
               dirPrev = dirNext;
               dirNext = dir.prev();
               // the value is never used: next = prev;
               switched = true;
            }
            
            // the attaching edge is already moved to innerEdges (see detach above).
            // attachingEdge is the "prev" edge, for linking.
            attachingEdge = outerEdges.get(attachingIndex.index).getPrev();
            // get the "next" edge, for linking.
            PieceEdge attachingCornerNext = attachingEdge.getNext();//=outerEdges.get(next.getIndex(dirPrev).index);
            if (switched) {
               attachingEdge = attachingEdge.getPrev();
            }
            else {
               attachingCornerNext = attachingCornerNext.getNext();
            }//=outerEdges.get(next.getIndex(dirPrev).index);
            
            OuterEdgeIndices behindPrev = getSubPieceIndicesOrNull(
             subPiece.x + 2 * dir.x + dirPrev.x,//- dir.perpendicularX,
             subPiece.y + 2 * dir.y + dirPrev.y//- dir.perpendicularY
            ); // subPiece.x - 1, subPiece.y - 1
            
            if (behindPrev != null) { // corner: creates new hole:
               
               // get the edges of behindPrev, for linking.
               OuterEdgeIndex behindPrevNextIndex = behindPrev.getIndex(dirNext);
               OuterEdgeIndex behindPrevOppositeIndex = behindPrev.getIndex(dir.opposite());
               PieceEdge attachBehindPrevNext = outerEdges.get(behindPrevNextIndex.index);
               PieceEdge nextEdge = outerEdges.get(behindPrevOppositeIndex.index);
               
               // link one of the new edges
               PieceEdge newEdge = singlePieceEdges.nesw[dir.ordinal()];
               attachBehindPrevNext.linkNext(newEdge);
               newEdge.linkNext(attachingCornerNext);// /*.getNext()*/
               // add the new edge to outerEdges
               addOuterEdge(attachingIndex.hole, outerEdges, newEdge, newSubPiece, dir, newIndices);
               
               // link the other new edge:
               newEdge = singlePieceEdges.nesw[dirPrev.ordinal()];
               newEdge.linkNext(nextEdge);
               attachingEdge.linkNext(newEdge);
               // create the new hole
               ArrayList<PieceEdge> newHole = new ArrayList<>();
               int hole2 = outerEdgeHoles.size();
               addHole(newHole);
               // add the other new edge to the new hole
               addOuterEdge(hole2, newHole, newEdge, newSubPiece, dirPrev, newIndices);
               // add the rest of the linked edges to the new hole and update their reference indices
               while (nextEdge != newEdge) {
                  OuterEdgeIndices indices = getSubPieceIndices(nextEdge.getSubPiece());
                  indices.set(nextEdge.getSubPieceDir(), hole2, newHole.size());
                  newHole.add(nextEdge);
                  nextEdge = nextEdge.getNext();
               }
            } // corner: behindPrev!=null (creates new hole)
            else { // corner: not new hole (no behindPrev)
               
               PieceEdge newEdge = switched
                ?singlePieceEdges.nesw[dir.ordinal()]
                :singlePieceEdges.nesw[dirPrev.ordinal()];
               Direction direction = switched ?dir :dirPrev;
               // link (attach) the first new edge
               attachingEdge.linkNext(newEdge);
               // add the edge
               addOuterEdge(attachingIndex.hole, outerEdges, newEdge, newSubPiece, direction, newIndices);
               
               newEdge = switched
                ?singlePieceEdges.nesw[dirPrev.ordinal()]
                :singlePieceEdges.nesw[dir.ordinal()];
               direction = switched ?dirPrev :dir; //  (if switched, dirPrev is actually dirNext)
               // link (attach) the second new edge
               newEdge.linkNext(attachingCornerNext);
               // add the edge
               addOuterEdge(attachingIndex.hole, outerEdges, newEdge, newSubPiece, direction, newIndices);
               
            } // corner: not new hole (no behindPrev)
         } // corner
         else { // dir and dir.opposite() - this always creates a new hole! (or divides a hole in two)
            
            attachingEdge = outerEdges.get(attachingIndex.index);
            PieceEdge attachingEdge2 = outerEdges.get(behind.getIndex(dir.opposite()).index);
            
            // link one of the new edges
            PieceEdge newEdge = singlePieceEdges.nesw[dirNext.ordinal()];
            attachingEdge2.getPrev().linkNext(newEdge);
            newEdge.linkNext(attachingEdge.getNext());
            
            // add the new edge to outerEdges
            addOuterEdge(attachingIndex.hole, outerEdges, newEdge, newSubPiece, dirNext, newIndices);
            
            // link the other edge:
            newEdge = singlePieceEdges.nesw[dirPrev.ordinal()];
            PieceEdge nextEdge = attachingEdge2.getNext();
            newEdge.linkNext(nextEdge);
            attachingEdge = attachingEdge.getPrev();//WholeEdge lastEdge = attachingEdge.getPrev();
            attachingEdge.linkNext(newEdge);
            
            // create the new hole
            ArrayList<PieceEdge> newHole = new ArrayList<>();
            int hole2 = outerEdgeHoles.size();
            addHole(newHole);
            
            // add newEdge to the new hole
            addOuterEdge(hole2, newHole, newEdge, newSubPiece, dirPrev, newIndices);
            // add the rest of the linked edges to the new hole and update the indices
            while (nextEdge != newEdge) {
               OuterEdgeIndices indices = getSubPieceIndices(nextEdge.getSubPiece());
               indices.set(nextEdge.getSubPieceDir(), hole2, newHole.size());
               newHole.add(nextEdge);
               nextEdge = nextEdge.getNext();
            }
         } // dir and dir.opposite()
         
         break; // case sum == 2
      }
      case 3: { // the attached directions are all but emptyDir1
         
         ArrayList<PieceEdge> outerEdges = getOuterEdges(attachingIndex);
         
         PieceEdge attachingEdge3;
         if (behindNull) {
            // -1 is prev => 1, -2 is next => 3, -3 is opposite => 2
            attachingEdge = innerEdges.get(innerEdges.size() - 1);
            attachingEdge3 = innerEdges.get(innerEdges.size() - 2);
         }
         else if (nextNull) {
            // -1 is prev => 2, -2 is dir => 1, -3 is opposite => 3
            attachingEdge = innerEdges.get(innerEdges.size() - 2);
            attachingEdge3 = innerEdges.get(innerEdges.size() - 3);
            dir = dirNext;
         }
         else { // prevNull
            // -1 is next => 2, -2 is dir => 3, -3 is opposite => 1
            attachingEdge = innerEdges.get(innerEdges.size() - 3);
            attachingEdge3 = innerEdges.get(innerEdges.size() - 2);
            dir = dirPrev;
         }
         
         // link (attach) the new edge
         PieceEdge newEdge = singlePieceEdges.nesw[dir.ordinal()];
         attachingEdge.getPrev().linkNext(newEdge);
         newEdge.linkNext(attachingEdge3.getNext());
         
         // add the new edge so it gets an index
         addOuterEdge(attachingIndex.hole, outerEdges, newEdge, newSubPiece, dir, newIndices);
//         newIndices.set(dir, attachingIndex.hole, outerEdges.size());
//         outerEdges.add(newEdge.setSubPiece(newSubPiece, dir));
         
         break; // case sum == 3
      }
      case 4: // all four directions are attached:
         
         // the new piece filled a hole which was 4 edges that all became innerEdges.
         // delete the hole, but keep the null in the list of holes so that hole indexes don't need to change.
         outerEdgeHoles.set(attachingIndex.hole, null);
         
      } // switch(sum)
   } // void addPieceEdges(SinglePieceEdges, Point, Direction)
   
   /**
    * Add an outer edge to a list, set its corresponding subPiece edge (Point and Direction), and
    * store a reference to its list index in newIndices. The instance should
    * have the coordinates of the Point newSubPiece in the LargerPiece matrix.
    * @param hole the index of outerEdges in outerEdgeHoles
    * @param outerEdges the list (hole) to add newEdge to, equal to outerEdgeHoles.get(hole)
    * @param newEdge the outer edge being added to "outerEdges"
    * @param newSubPiece the subPiece that newEdge belongs to
    * @param dir the direction of newSubPiece at which newEdge is the edge
    * @param newIndices the OuterEdgeIndices at the point "newSubPiece"
    */
   private /*static*/ void addOuterEdge(int hole, ArrayList<PieceEdge> outerEdges, PieceEdge newEdge, Point newSubPiece,
    Direction dir, OuterEdgeIndices newIndices)
   {
      newIndices.set(dir, hole, outerEdges.size());
      outerEdges.add(newEdge.setSubPiece(newSubPiece, dir));
   }
   
   public int toOuterEdgePath(Path path)
   {
      // loop through getNext(), not index in the list!
      PieceEdge firstEdge = outerEdgeHoles.get(0).get(0);
      PieceEdge nextEdge = firstEdge;
      do {
         nextEdge.appendSegmentsTo(path);
         nextEdge = nextEdge.getNext();
      }while (nextEdge != firstEdge);
      return outerEdgeHoles.size();
   }
   
   public Path getOuterEdgesPath(float startX, float startY, int hole)
   {
      return getPath(startX, startY, outerEdgeHoles.get(hole).get(0));
   }
   
   public ArrayList<PieceEdge> getInnerEdges()
   {
      return innerEdges;
   }
   //   public Path getInnerEdgesPath(float startX, float startY)
//   {
//      // TODO: all inner edges need a reference to its starting point (subPiece)
//      //return getPath(startX, startY, innerEdges.get(0));
//      // TODO: first draw all inner edges with 100% opacity on a buffered image / canvas, and then draw the canvas with lower opacity on top of the largerpiece.
//
//   }
   
   private ArrayList<PieceEdge> getOuterEdges(@NonNull OuterEdgeIndex index)
   {
      return outerEdgeHoles.get(index.hole);//index.hole == -1 ?outerEdges :holes.get(index.hole);
   }
   
   private PieceEdge getOuterEdge(OuterEdgeIndex index)
   {
      if (index == null)
         return null;
      return getOuterEdges(index).get(index.index);
//      if (index.hole == -1)
//         return outerEdges.get(index.index);
//      return holes.get(index.hole).get(index.index);
   }
   
   private void addHole(ArrayList<PieceEdge> newHole)
   {
      outerEdgeHoles.add(newHole);
      removed = Arrays.copyOf(removed, outerEdgeHoles.size());
   }
   
   private PieceEdge removeOuterEdge(OuterEdgeIndex removeIndex)
   {
      if (removeIndex == null)
         return null;
      ArrayList<PieceEdge> outerEdges = getOuterEdges(removeIndex);
      
      // TODO: make sure when combining two LargerPieces that I know there can be nulls in outerEdgeHoles lists!
      
      PieceEdge ret = outerEdges.set(removeIndex.index, null); // hole.remove(removeIndex.index)
      removed[removeIndex.hole]++;
      
      if (removed[removeIndex.hole] > 15) { // wait until 16 have been removed until actually shortening the list
         removed[removeIndex.hole] = 0;
         int remove = 0;
         for (int i = 0; i < outerEdges.size(); i++) {
            PieceEdge edge = outerEdges.get(i);
            if (edge == null) {
               remove++;
               continue;
            }
            OuterEdgeIndices indices = getSubPieceIndices(edge.getSubPiece());
            OuterEdgeIndex index = indices.getIndex(edge.getSubPieceDir());
            index.index -= remove;
            outerEdges.set(i - remove, edge);
         }
         
         for (; remove > 0; remove--) {
            outerEdges.remove(outerEdges.size() - 1);
         }
      }
//      for (int i = removeIndex.index; i < hole.size(); i++) {
//         WholeEdge edge = hole.get(i);
//         OuterEdgesIndices indices = getSubPieceIndices(edge.getSubPiece());
//         OuterEdgeIndex index = indices.getOuterEdgeIndex(edge.getSubPieceDir());
//         index.index = i; // same as: index.index -= 1;
//      }
      return ret;
   }
} // class LargerPieceEdges

private LargerPiece(LargerPiece p1, LargerPiece p2, Point subPiece1, Point subPiece2, Direction dir)
{
   super(p1.containerParent, p1.currentRotationNorthDirection);
   
   int diffX = subPiece1.x + dir.x - subPiece2.x;
   int offsetX1 = diffX < 0 ?-diffX :0;
   int offsetX2 = diffX > 0 ?diffX :0;
   int diffY = subPiece1.y + dir.y - subPiece2.y;
   int offsetY1 = diffY < 0 ?-diffY :0;
   int offsetY2 = diffY > 0 ?diffY :0;
   
   init(Math.max(offsetX1 + p1.matrixWidth, offsetX2 + p2.matrixWidth),
    Math.max(offsetY1 + p1.matrixHeight, offsetY2 + p2.matrixHeight));
   
   pieceCount = p1.pieceCount + p2.pieceCount;
   westEdge = p1.isWestEdge() || p2.isWestEdge();
   eastEdge = p1.isEastEdge() || p2.isEastEdge();
   northEdge = p1.isNorthEdge() || p2.isNorthEdge();
   southEdge = p1.isSouthEdge() || p2.isSouthEdge();
   
   // LargerPieceEdges constructor combines the matrices!
   if (p1.pieceCount > p2.pieceCount)
      vectorEdges = new LargerPieceEdges(p1, p2, subPiece1, subPiece2,
       dir, offsetX1, offsetX2, offsetY1, offsetY2);
   else
      vectorEdges = new LargerPieceEdges(p2, p1, subPiece2, subPiece1,
       dir.opposite(), offsetX2, offsetX1, offsetY2, offsetY1);
   
   // TODO: check edgeWidths
   
}

/**
 * @param p1 a SinglePiece
 * @param p2 another SinglePiece
 * @param dir the direction of combination
 * Constructs a LargerPiece by combining two SinglePieces.
 */
private LargerPiece(SinglePiece p1, SinglePiece p2, Direction dir)
{
   super(p1.containerParent, p1.currentRotationNorthDirection);
   init(dir.initWidth, dir.initHeight);
   
   pieceCount = 2;
   copyIsEdge(p1);
   copyIsEdge(p2);
   
   // the matrix is filled in LargerPieceEdges constructor!
   vectorEdges = new LargerPieceEdges(p1.vectorEdges, p2.vectorEdges, dir);
   
   // edge widths:
   RectF edgeWidths1 = p1.getEdgeWidths();
   RectF edgeWidths2 = p2.getEdgeWidths();
   switch (dir) {
   case NORTH:
      edgeWidths.top = edgeWidths2.top;
      edgeWidths.bottom = edgeWidths1.bottom;
      edgeWidths.left = Math.max(edgeWidths1.left, edgeWidths2.left);
      edgeWidths.right = Math.max(edgeWidths1.right, edgeWidths2.right);
      break;
   case EAST:
      edgeWidths.left = edgeWidths1.left;
      edgeWidths.right = edgeWidths2.right;
      edgeWidths.top = Math.max(edgeWidths1.top, edgeWidths2.top);
      edgeWidths.bottom = Math.max(edgeWidths1.bottom, edgeWidths2.bottom);
      break;
   case SOUTH:
      edgeWidths.top = edgeWidths1.top;
      edgeWidths.bottom = edgeWidths2.bottom;
      edgeWidths.left = Math.max(edgeWidths1.left, edgeWidths2.left);
      edgeWidths.right = Math.max(edgeWidths1.right, edgeWidths2.right);
      break;
   default: // WEST:
      edgeWidths.left = edgeWidths2.left;
      edgeWidths.right = edgeWidths1.right;
      edgeWidths.top = Math.max(edgeWidths1.top, edgeWidths2.top);
      edgeWidths.bottom = Math.max(edgeWidths1.bottom, edgeWidths2.bottom);
      break;
   }
}

public void addPiece(SinglePiece newPiece, Point attachedTo, Direction dir)
{
   // edge widths:
   RectF newEdgeWidths = newPiece.getEdgeWidths();
   if (attachedTo.x == 0)
      edgeWidths.left = Math.max(edgeWidths.left, newEdgeWidths.left);
   if (attachedTo.x == matrixWidth - 1)
      edgeWidths.right = Math.max(edgeWidths.right, newEdgeWidths.right);
   if (attachedTo.y == 0)
      edgeWidths.top = Math.max(edgeWidths.top, newEdgeWidths.top);
   if (attachedTo.y == matrixHeight - 1)
      edgeWidths.bottom = Math.max(edgeWidths.bottom, newEdgeWidths.bottom);
   
   pieceCount++;
   if (attachedTo.x == 0 && dir == Direction.WEST) {
      correctPuzzlePosition.x -= 1;
      expandX(0);
      attachedTo.x = 1; // update to the coordinate in the expanded matrix
      edgeWidths.left = newEdgeWidths.left;
   }
   else if (attachedTo.y == 0 && dir == Direction.NORTH) {
      correctPuzzlePosition.y -= 1;
      expandY(0);
      attachedTo.y = 1; // update to the coordinate in the expanded matrix
      edgeWidths.top = newEdgeWidths.top;
   }
   else if (attachedTo.x == matrixWidth - 1 && dir == Direction.EAST) {
      expandX(matrixWidth);
      edgeWidths.right = newEdgeWidths.right;
   }
   else if (attachedTo.y == matrixHeight - 1 && dir == Direction.SOUTH) {
      expandY(matrix.size());
      edgeWidths.bottom = newEdgeWidths.bottom;
   }
   // vectorEdges also sets the matrix value at the point (attachedTo.x + dir.x, attachedTo.y + dir.y)
   vectorEdges.addPieceEdges(newPiece.vectorEdges, attachedTo, dir);
   copyIsEdge(newPiece);
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

private void init(int width, int height)
{
   matrixWidth = width;
   matrixHeight = height;
   
   matrix = new ArrayList<>();
   int len = width * height;
   for (int i = 0; i < len; i++) {
      matrix.add(null/*new OuterEdges()*/);
   }
}

//private int xFromLinear (int linear){
//   return linear % matrixWidth;
//}
//
//private int yFromLinear (int linear){
//   return linear / matrixWidth;
//}

private int linear(int x, int y)
{
   return y * matrixWidth + x;
}

private Integer checkBounds(int x, int y)
{
   int ret = linear(x, y);
   if (ret < 0 || ret >= matrix.size())
      return null;
   return ret;
}

private OuterEdgeIndices getSubPieceIndicesOrNull(int x, int y)
{
   Integer linear = checkBounds(x, y);
   if (linear == null)
      return null;
   return matrix.get(linear);
}

private OuterEdgeIndices getSubPieceIndices(Point p)
{
   return matrix.get(linear(p.x, p.y));
}

private void setSubPieceIndices(int x, int y, OuterEdgeIndices indices)
{
   matrix.set(linear(x, y), indices);
}

private void copyIsEdge(SinglePiece p)
{
   if (p.isWestEdge())
      westEdge = true;
   if (p.isEastEdge())
      eastEdge = true;
   if (p.isNorthEdge())
      northEdge = true;
   if (p.isSouthEdge())
      southEdge = true;
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

public Point getPuzzlePiece(PointF mouseOffset)
{
   Point ret = new Point(correctPuzzlePosition);
   ret.x += getSubPieceX(mouseOffset.x);
   ret.y += getSubPieceY(mouseOffset.y);
   return ret;
}

public Point getSubPiece(PointF mouseOffset)
{
   return new Point(getSubPieceX(mouseOffset.x), getSubPieceY(mouseOffset.y));
}

public int getSubPieceX(float mouseOffsetX)
{
   return (int) (mouseOffsetX / (SIDE_SIZE * matrixWidth));
}

public int getSubPieceY(float mouseOffsetY)
{
   return (int) (mouseOffsetY / (SIDE_SIZE * matrixHeight));
}

public ArrayList<BorderDrawable> getBgOutline()
{
   /*
   ArrayList<BgDrawable> ret = new ArrayList<>(matrix.size());//+2*matrixWidth+2*matrixHeight+4);
   for(int y=0;y < matrixHeight; y++){
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
               bits[x][y+1] |= BIT_WEST; // should be EAST ...
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
               bits[x+2][y+2] |= BIT_SE; // ... should be NW
            }

         } // piece != null
      } // for(x)
   } // for(y)
   */
   
   ArrayList<BorderDrawable> ret = new ArrayList<>(matrix.size());//+2*matrixWidth+2*matrixHeight+4);
   
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
            ret.add(BorderDrawable.cornerSE(x, y));
            // outside left edge, top row (-1, 0)
            ret.add(BorderDrawable.single(x, y + 1, Direction.EAST));
         }
         else if (matrix.get(linear(x + 1, (++y) + 1)) != null) { // x+1 == 0, y+1 == 1
            // outside left edge, top row (-1, 0)
            ret.add(BorderDrawable.cornerSE(x, y));
         }
      }
      else { // leftmost column: isTopEdge (NW)
         y = 0; // top row
         if (matrix.get(linear(x + 1, y)) != null) { // x+1 == 0
            // outside left edge, top row (-1, 0)
            ret.add(BorderDrawable.singleShortRight(x, y, Direction.EAST));
         }
         else if (matrix.get(linear(x + 1, y + 1)) != null) { // x+1 == 0, y+1 == 1
            // outside left edge, top row (-1, 0)
            ret.add(BorderDrawable.cornerSE(x, y));
         }
      }
      
      // leftmost column: loop through the middle of the column
      for (y = 1; y < matrixHeight - 1; y++) {
         if (matrix.get(linear(x + 1, y)) != null) { // x+1 == 0
            // outside left edge, middle rows
            ret.add(BorderDrawable.single(x, y, Direction.EAST));
         }
         else {
            if (matrix.get(linear(x + 1, y - 1)) != null) { // x+1 == 0
               // outside left edge, middle rows
               ret.add(BorderDrawable.cornerNE(x, y));
            }
            if (matrix.get(linear(x + 1, y + 1)) != null) { // x+1 == 0
               // outside left edge, middle rows
               ret.add(BorderDrawable.cornerSE(x, y));
            }
         }
      }
      
      // leftmost column: bottom left corner (SW)
      if (!isSouthEdge()) { // leftmost column: SW can be outlined
         // TODO: matrixHeight: outside bottom edge
         if (matrix.get(linear(x + 1, y)) != null) { // x+1 == 0, y == matrixHeight - 1
            // outside left edge, bottom row (-1, matrixHeight - 1)
            ret.add(BorderDrawable.single(x, y, Direction.EAST));
            // outside left edge, outside bottom edge (-1, matrixHeight)
            ret.add(BorderDrawable.cornerNE(x, y + 1));
         }
         else if (matrix.get(linear(x + 1, y - 1)) != null) { // x+1 == 0
            // outside left edge, bottom row (-1, matrixHeight - 1)
            ret.add(BorderDrawable.cornerNE(x, y));
         }
      }
      else { // leftmost column: isSouthEdge (SW)
         // TODO: bottom row (NOT OUTSIDE)
         if (matrix.get(linear(x + 1, y)) != null) { // x+1 == 0, y == matrixHeight - 1
            ret.add(BorderDrawable.singleShortLeft(x, y, Direction.EAST));
         }
         else if (matrix.get(linear(x + 1, y - 1)) != null) { // x+1 == 0, y-1 == matrixHeight - 2
            
            ret.add(BorderDrawable.cornerNE(x, y));
         }
      }
   } // west column can be outlined
   if (!isEastEdge()) { // east column can be outlined
      // TODO: copy westEdge
   } // east column can be outlined
   if (!isNorthEdge()) { // north row can be outlined
      y = -1; // outside north edge
      
      x = 0;
      
      for (x = 1; x < matrixWidth - 1; x++) {
         if (matrix.get(linear(x, 0)) != null) {
            ret.add(BorderDrawable.single(-1, y, Direction.EAST));
         }
         else {
            if (matrix.get(linear(x - 1, 0)) != null) {
               ret.add(BorderDrawable.cornerSW(x, -1));
            }
            if (matrix.get(linear(x + 1, 0)) != null) {
               ret.add(BorderDrawable.cornerSE(x, -1));
            }
         }
      }
      
      // assert(x == matrixWidth - 1) // rightmost column
      
   } // north row can be outlined
   if (!isSouthEdge()) { // south row can be outlined
   
   } // south row can be outlined
   
   // loop through the middle of the matrix - TODO: only one loop with if(x==0) and such!
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
         // TODO!
         /*if (bits[x][y] && BIT_NORTH != 0) {
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
         }*/
      } // for(x)
   } // for(y)
   return ret;
} // method: getOutline()

public RectF getEdgeWidths()
{
   return new RectF(westEdge ?PieceEdge.STRAIGHT_EDGE_WIDTH :edgeWidths.left,
    northEdge ?PieceEdge.STRAIGHT_EDGE_WIDTH :edgeWidths.top,
    eastEdge ?PieceEdge.STRAIGHT_EDGE_WIDTH :edgeWidths.right,
    southEdge ?PieceEdge.STRAIGHT_EDGE_WIDTH :edgeWidths.bottom);
}

public boolean isWestEdge()
{
   return westEdge;
}

public boolean isNorthEdge()
{
   return northEdge;
}

public boolean isEastEdge()
{
   return eastEdge;
}

public boolean isSouthEdge()
{
   return southEdge;
}
}
