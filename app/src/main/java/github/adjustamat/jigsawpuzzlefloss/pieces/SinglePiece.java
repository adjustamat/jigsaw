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

    SVGPath north, west, south, east;

    public SinglePiece(Box box, Point coordinates,
                       EdgeType top, EdgeType right, EdgeType bottom, EdgeType left,
                       SVGPath[] rotatedPaths, RectF edgeWidths
    )
    {
        super(box);
        this.correctPuzzlePosition = coordinates;
        this.edgeWidths = edgeWidths;
        
        this.topEdge = top;
        this.rightEdge = right;
        this.bottomEdge = bottom;
        this.leftEdge = left;

        // LATER: don't worry about drawing and lighting of animation-rotated pieces yet!
        // just draw top and right as light, and bottom left as dark. less opacity when pieces are put together into LargerPieces.

        // rotatedPaths array come from the pool, but is not the pool.
        this.north = new SVGPath(rotatedPaths[0]).append(rotatedPaths[1]);
        this.east = new SVGPath(rotatedPaths[2]).append(rotatedPaths[3]);
        this.south = new SVGPath(rotatedPaths[4]).append(rotatedPaths[5]);
        this.west = new SVGPath(rotatedPaths[6]).append(rotatedPaths[7]);
        // TODO: translate by (coordinates.x * SIDE_SIZE, coordinates.y * SIDE_SIZE)
        SVGPath path = new SVGPath(this.north).append(this.east).append(this.south).append(this.west);

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
