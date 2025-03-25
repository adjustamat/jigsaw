package github.adjustamat.jigsawpuzzlefloss.pieces;

import android.graphics.Path;
import android.graphics.Path.Op;
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

final EdgeType left;
final EdgeType top;
final EdgeType right;
final EdgeType bottom;
final RectF edgeWidths;

final Point correctPlace;

//PointF imageCoordinates; // use imageMask instead

ImagePuzzle.Area area;

PathShape imageMask;
//Shape completeEdgePath;
//Shape outline;

LargerPiece largerPieceParent;
Point positionInLargerPiece;

public SinglePiece(Box box, Point coordinates,
 float stdWidth, float stdHeight, RectF widths,
 EdgeType left, EdgeType top, EdgeType right, EdgeType bottom,
 Path path1, Path path2, Path path3, Path path4
)
{
   super(box);
   this.correctPlace = coordinates;
   this.edgeWidths = widths;
   this.left = left;
   this.top = top;
   this.right = right;
   this.bottom = bottom;
   
   
   Path path = new Path(path1);
   path.op(path2, Op.UNION);
   path.op(path3, Op.UNION);
   path.op(path4, Op.UNION);
   imageMask = new PathShape(path, stdWidth, stdHeight);

}

public boolean isLeftEdge()
{
   return left == EdgeType.OUTER_EDGE;
}

public boolean isTopEdge()
{
   return top == EdgeType.OUTER_EDGE;
}

public boolean isRightEdge()
{
   return right == EdgeType.OUTER_EDGE;
}

public boolean isBottomEdge()
{
   return bottom == EdgeType.OUTER_EDGE;
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
