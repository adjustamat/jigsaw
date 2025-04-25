package github.adjustamat.jigsawpuzzlefloss.pieces;

import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;

import github.adjustamat.jigsawpuzzlefloss.game.Box;
import github.adjustamat.jigsawpuzzlefloss.game.ImagePuzzle;

/**
 * A piece of an {@link ImagePuzzle}. Has four edges that are either jigsaw-shaped or flat (at the
 * outer edges of the ImagePuzzle).
 */
public class SinglePiece
 extends AbstractPiece
{

// ImagePuzzle.Area areaParent; // can be calculated when needed, instead of stored in memory.
// LargerPiece largerPieceParent;
// Point positionInLargerPiece;

SVGPath.WholeEdge north, west, south, east;

// translate PathShape imageMask by coordinates to get the correct part of the image:

/**
 * The outline to draw when rotating or when drawing embossed 3D-effect.
 */
SVGPath.SinglePieceOutline outline;

Path zeroOffsetOutline;
PointF imageOffset;
//    PointF shapeSize;
//   this.shapeSize = new PointF(
//    imageSize + edgeWidths.left + edgeWidths.right,
//    imageSize + edgeWidths.top + edgeWidths.bottom);

Color edgesColor; // TODO: extract color from the imageMask part of the image.
Color highContrastBgColor;

public SinglePiece(Box box, Point coordinates,
 EdgeType top, EdgeType right, EdgeType bottom, EdgeType left,
 SVGPath.WholeEdge[] northEastSouthWest, float imageSize
)
{
   super(box);
   this.correctPuzzlePosition = coordinates;
   
   // TODO: edgeWidths *= imageSize / SIDE_SIZE;
   
   this.imageOffset = new PointF(coordinates.x * imageSize, coordinates.y * imageSize);
   
   
   this.topEdge = top;
   this.rightEdge = right;
   this.bottomEdge = bottom;
   this.leftEdge = left;
   
   // LATER: don't worry about drawing and lighting of animation-rotated pieces yet!
   // just draw top and right as light, and bottom left as dark. less opacity when pieces are put together into LargerPieces.
   
   // rotatedPaths array come from the pool, but is not the pool.
   this.north = northEastSouthWest[0];
   this.east = northEastSouthWest[1];
   this.south = northEastSouthWest[2];
   this.west = northEastSouthWest[3];
   
   this.outline = new SVGPath.SinglePieceOutline(this.north, this.east, this.south, this.west);
   
   zeroOffsetOutline = outline.toSVG(0f, 0f);
   this.imageMask = new Path();
   zeroOffsetOutline.offset(imageSize * coordinates.x, imageSize * coordinates.y, imageMask);
}

public RectF getEdgeWidths()
{
   return new RectF(west.getEdgeWidth(),north.getEdgeWidth(),east.getEdgeWidth(),south.getEdgeWidth());
}
}
