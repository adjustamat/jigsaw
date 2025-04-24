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

SVGPath north, west, south, east;

// translate PathShape imageMask by coordinates to get the correct part of the image:
PointF imageOffset;
//    PointF shapeSize;
//   this.shapeSize = new PointF(
//    imageSize + edgeWidths.left + edgeWidths.right,
//    imageSize + edgeWidths.top + edgeWidths.bottom);


Path zeroOffsetOutline;

Color edgesColor; // TODO: extract color from the imageMask part of the image.
Color highContrastBgColor;

public SinglePiece(Box box, Point coordinates,
 EdgeType top, EdgeType right, EdgeType bottom, EdgeType left,
 SVGPath[] rotatedPaths, RectF edgeWidths, float imageSize
)
{
   super(box);
   this.correctPuzzlePosition = coordinates;
   edgeWidths.top *= imageSize / SIDE_SIZE;
   this.edgeWidths = edgeWidths;
   
   this.imageOffset = new PointF(coordinates.x * imageSize, coordinates.y * imageSize);
   
   
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
   
   this.outline = // append in order NORTH, EAST, SOUTH, WEST:
    // TODO: use CombinedPath class instead, to avoid copying values all the time. It could use a LinkedList internally, so that I can inset the path of a new piece into a LargerPiece shape.
    new SVGPath(this.north).append(this.east).append(this.south).append(this.west);
   
   zeroOffsetOutline = outline.toPath(0f, 0f);
   this.imageMask = new Path();
   zeroOffsetOutline.offset(imageSize * coordinates.x, imageSize * coordinates.y, imageMask);
   
   //this.imageMask = new PathShape(imageMaskPath,
   
   
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
