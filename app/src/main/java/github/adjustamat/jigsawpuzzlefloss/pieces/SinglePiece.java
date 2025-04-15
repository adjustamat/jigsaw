package github.adjustamat.jigsawpuzzlefloss.pieces;

import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.shapes.PathShape;
import github.adjustamat.jigsawpuzzlefloss.game.Box;
import github.adjustamat.jigsawpuzzlefloss.game.ImagePuzzle;

/**
 * A piece of an {@link ImagePuzzle}. Has four jigsaw-shaped edges.
 */
public class SinglePiece
 extends AbstractPiece
{
// ImagePuzzle.Area areaParent; // can be calculated when needed, instead of stored in memory.
// LargerPiece largerPieceParent;
// Point positionInLargerPiece;

public SinglePiece(Box box, Point coordinates,
 EdgeType left, EdgeType top, EdgeType right, EdgeType bottom,
 SVGPath[] rotatedPaths, RectF edgeWidths
) // TODO: do not store it as Path objects, but as instructions that can be pre-translated and mirrored, rotated and such - by my own algorithm which has known and unchanging rotating standard. implement as PathIterator perhaps.
{
   super(box);
   this.correctPlace = coordinates;
   this.edgeWidths = edgeWidths;
   this.leftEdge = left;
   this.topEdge = top;
   this.rightEdge = right;
   this.bottomEdge = bottom;

   // LATER: don't worry about drawing and lighting of animation-rotated pieces yet!
   // TODO: just draw top and right as light, and bottom left as dark. less opacity when pieces are put together into LargerPieces.
   
   // rotatedPaths come from pre-rotated (in all directions) instances loaded when starting the app.
   Path path = new Path(rotatedPaths[0]); //  // 0: left 1/2
    // TODO: translate by (coordinates.x * SIDE_SIZE, coordinates.y * SIDE_SIZE)

   // 1: left 2/2. 2: top 1/2. 3: top 2/2. 4: right 1/2. 5: right 2/2. 6: bottom 1/2. 7: bottom 2/2.
   for(Path rp:rotatedPaths){
      // TODO: use PathIterator to add all but the first moveTo (all rLineTo and rCubicTo).
      PathIterator i = rp.getIterator();
      i.next(); // skip first!
      while(i.hasNext()){
         path.add(i.next()); // TODO: translate by SIDE_SIZE (y for bottom, x for right). tranlate all "2/2" by HALF_SIZE!
      }
   }
    // TODO: translate by (coordinates.x * SIDE_SIZE, coordinates.y * SIDE_SIZE)

   //path.op(path4, Op.UNION); this does not work.

   imageMask = new PathShape(path, 
   SIDE_SIZE+edgeWidths.left+edgeWidths.right, 
   SIDE_SIZE+edgeWidths.top+edgeWidths.bottom);

}

public boolean hasLarger(){
   return largerPieceParent != null;
}

void setLargerPiece(LargerPiece parent, Point position){
   largerPieceParent = parent;
   positionInLargerPiece = position;
}

void insertPieceLeft(){
   positionInLargerPiece.x++;
}

void insertPieceTop(){
   positionInLargerPiece.y++;
}

public boolean isEdgePiece()
{
   return isLeftEdge() || isTopEdge() || isRightEdge() || isBottomEdge();
}

public boolean isCornerPiece()
{
   return (isLeftEdge() || isRightEdge()) && (isTopEdge() || isBottomEdge());
}

}
