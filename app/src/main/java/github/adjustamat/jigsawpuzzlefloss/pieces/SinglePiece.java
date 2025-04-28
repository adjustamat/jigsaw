package github.adjustamat.jigsawpuzzlefloss.pieces;

import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;

import github.adjustamat.jigsawpuzzlefloss.game.ImagePuzzle;
import github.adjustamat.jigsawpuzzlefloss.game.ImagePuzzle.RandomEdge;

/**
 * A piece of an {@link ImagePuzzle}. Has four edges that are either jigsaw-shaped or flat (at the
 * outer edges of the ImagePuzzle).
 */
public class SinglePiece
 extends AbstractPiece
{

final RandomEdge/*EdgeType*/ westEdge;
final RandomEdge northEdge;
final RandomEdge eastEdge;
final RandomEdge southEdge;


/**
 * The outline to draw when rotating or when drawing embossed 3D-effect.
 */
final SinglePieceEdges svgEdges;

// translate PathShape imageMask by coordinates to get the correct part of the image
Path zeroOffsetOutline;
PointF imageOffset;
//    PointF shapeSize;
//   this.shapeSize = new PointF(
//    imageSize + edgeWidths.left + edgeWidths.right,
//    imageSize + edgeWidths.top + edgeWidths.bottom);

// ImagePuzzle.Area areaParent; // can be calculated when needed, instead of stored in memory.


// LargerPiece largerPieceParent;
// Point positionInLargerPiece;

Color edgesColor; // TODO: extract color from the imageMask part of the image.
Color highContrastBgColor;

public static class SinglePieceEdges
 extends SVGEdges
{
   final WholeEdge north, east, south, west;
   
   public SinglePieceEdges(WholeEdge[] northEastSouthWest)
   {
      this.north = northEastSouthWest[0];
      this.east = northEastSouthWest[1];
      this.south = northEastSouthWest[2];
      this.west = northEastSouthWest[3];
   }
   
   public RectF getEdgeWidths()
   {
      return new RectF(west.getEdgeWidth(), north.getEdgeWidth(), east.getEdgeWidth(), south.getEdgeWidth());
   }
   
   public void toPath(Path path)
   {
      north.toSVG(path);
      east.toSVG(path);
      south.toSVG(path);
      west.toSVG(path);
   }
} // class SinglePieceEdges

public SinglePiece(ImagePuzzle imagePuzzle, Point coordinates,
 RandomEdge north, RandomEdge east, RandomEdge south, RandomEdge west
 /* EdgeType top, EdgeType right, EdgeType bottom, EdgeType left,
 WholeEdge[] northEastSouthWest, float imageSize*/
)
{
   super(imagePuzzle.box);
   this.correctPuzzlePosition = coordinates;
   
   northEdge = north;
   eastEdge = east;
   southEdge = south;
   westEdge = west;
   
   this.svgEdges =new SinglePieceEdges(northEastSouthWest); // TODO: fix!
   
   /*
   TODO: EdgeType class is removed, so replace!
north == null ?EdgeType.EDGE :north.type.opposite(),
          east == null ?EdgeType.EDGE :east.type,
          south == null ?EdgeType.EDGE :south.type,
          wests[y] == null ?EdgeType.EDGE :wests[y].type.opposite(),
   
// NORTH edge is previous piece's south edge, but :
         if (north == null) {
            northEastSouthWest[0] = SVGEdges.STRAIGHT_NORTH;
         }
         else {
            northEastSouthWest[0] = north.getWholeEdge(pool, Direction.NORTH);
         }
         
         // EAST edge:
         if (x == pWidth - 1) {
            east = null;
            northEastSouthWest[1] = SVGEdges.STRAIGHT_EAST;
         }
         else {
            east = new RandomEdge(rng);
            northEastSouthWest[1] = east.getWholeEdge(pool, Direction.EAST);
         }
         // SOUTH edge:
         if (y == pHeight - 1) {
            south = null;
            northEastSouthWest[2] = SVGEdges.STRAIGHT_SOUTH;
         }
         else {
            south = new RandomEdge(rng);
            northEastSouthWest[2] = south.getWholeEdge(pool, Direction.SOUTH);
         }
         // WEST edge:
         if (wests[y] == null) {
            northEastSouthWest[3] = SVGEdges.STRAIGHT_WEST;
         }
         else {
            northEastSouthWest[3] = wests[y].getWholeEdge(pool, Direction.WEST);
         }
    */

   
   
   
   final float imageSize = imagePuzzle.pieceImageSize;
   this.imageOffset = new PointF(coordinates.x * imageSize, coordinates.y * imageSize);
   
   // LATER: don't worry about drawing and lighting of animation-rotated pieces yet!
   // just draw top and right as light, and bottom left as dark. less opacity when pieces are put together into LargerPieces.
   
   // rotatedPaths array come from the pool, but is not the pool.
   
   
   zeroOffsetOutline = svgEdges.toSVG(0f, 0f);
   this.imageMask = new Path();
   zeroOffsetOutline.offset(imageSize * coordinates.x, imageSize * coordinates.y, imageMask);
}

public RectF getEdgeWidths()
{
   // TODO: edgeWidths *= imageSize / SIDE_SIZE;
   return svgEdges.getEdgeWidths();
}

public boolean isWestEdge()
{
   return westEdge == null;//EdgeType.EDGE;
}

public boolean isNorthEdge()
{
   return northEdge == null;//EdgeType.EDGE;
}

public boolean isEastEdge()
{
   return eastEdge == null;//EdgeType.EDGE;
}

public boolean isSouthEdge()
{
   return southEdge == null;//EdgeType.EDGE;
}
}
