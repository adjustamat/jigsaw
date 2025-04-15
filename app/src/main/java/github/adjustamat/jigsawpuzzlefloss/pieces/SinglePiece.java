package github.adjustamat.jigsawpuzzlefloss.pieces;

import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.shapes.PathShape;

import github.adjustamat.jigsawpuzzlefloss.game.Box;
import github.adjustamat.jigsawpuzzlefloss.game.ImagePuzzle;

/**
 * A piece of an {@link ImagePuzzle}. Has four edges that are either jigsaw-shaped or flat (at the
 * outer edges of the ImagePuzzle).
 */
public class SinglePiece
        extends AbstractPiece {

// ImagePuzzle.Area areaParent; // can be calculated when needed, instead of stored in memory.
// LargerPiece largerPieceParent;
// Point positionInLargerPiece;

    SVGPath left, right, top, bottom;

    public SinglePiece(Box box, Point coordinates,
                       EdgeType left, EdgeType top, EdgeType right, EdgeType bottom,
                       SVGPath[] rotatedPaths, RectF edgeWidths
    ) // TODO: do not store it as Path objects, but as instructions that can be pre-translated and mirrored, rotated and such - by my own algorithm which has known and unchanging rotating standard. implement as PathIterator perhaps.
    {
        super(box);
        this.correctPuzzlePosition = coordinates;
        this.edgeWidths = edgeWidths;
        this.leftEdge = left;
        this.topEdge = top;
        this.rightEdge = right;
        this.bottomEdge = bottom;

        // LATER: don't worry about drawing and lighting of animation-rotated pieces yet!
        // just draw top and right as light, and bottom left as dark. less opacity when pieces are put together into LargerPieces.

        // rotatedPaths come from pre-rotated (in all directions) instances loaded when starting the app.
        this.left = new SVGPath(rotatedPaths[0]);
        this.left.append(rotatedPaths[1]);
        this.right = new SVGPath(rotatedPaths[2]);
        this.right.append(rotatedPaths[3]);
        this.top = new SVGPath(rotatedPaths[4]);
        this.top.append(rotatedPaths[5]);
        this.bottom = new SVGPath(rotatedPaths[6]);
        this.bottom.append(rotatedPaths[7]);
        // TODO: translate by (coordinates.x * SIDE_SIZE, coordinates.y * SIDE_SIZE)
        SVGPath path = new SVGPath(this.left);
        path.append(this.top);
        path.append(this.right);
        path.append(this.bottom);

        imageMask = new PathShape(path.toPath(coordinates.x * SIDE_SIZE, coordinates.y * SIDE_SIZE),
                SIDE_SIZE + edgeWidths.left + edgeWidths.right,
                SIDE_SIZE + edgeWidths.top + edgeWidths.bottom);

    }

//public boolean hasLarger(){
//   return largerPieceParent != null;
//}

//void setLargerPiece(LargerPiece parent, Point position){
//   largerPieceParent = parent;
//   positionInLargerPiece = position;
//}

//void insertPieceLeft(){
//   positionInLargerPiece.x++;
//}
//
//void insertPieceTop(){
//   positionInLargerPiece.y++;
//}


}
