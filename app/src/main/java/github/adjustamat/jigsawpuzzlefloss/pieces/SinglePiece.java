package github.adjustamat.jigsawpuzzlefloss.pieces;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.shapes.Shape;
import github.adjustamat.jigsawpuzzlefloss.game.Container;
import github.adjustamat.jigsawpuzzlefloss.game.ImagePuzzle;

/**
 * A piece of an {@link ImagePuzzle}. Has four jigsaw-shaped edges.
 */
public class SinglePiece
{

EdgeType left;
EdgeType top;
EdgeType right;
EdgeType bottom;
RectF edgeWidth;

Point correctPlace;

//PointF imageCoordinates; // use imageMask instead
Shape imageMask;//completeEdgePath;

Shape outline;

Group groupParent;
Integer indexInGroup;

LargerPiece largerPieceParent;
Point positionInLargerPiece;

Container containerParent;
PointF positionInContainer;

public boolean isLeftEdge()
{
   return left == EdgeType.OUTER_EDGE;
}

public boolean isEdgePiece()
{
   return isLeftEdge() || top == EdgeType.OUTER_EDGE
           || right == EdgeType.OUTER_EDGE || bottom == EdgeType.OUTER_EDGE;
}

}
