package github.adjustamat.jigsawpuzzlefloss.pieces;

import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;

import androidx.annotation.Nullable;

import github.adjustamat.jigsawpuzzlefloss.game.ImagePuzzle;
import github.adjustamat.jigsawpuzzlefloss.pieces.SVGEdges.RandomEdge;
import github.adjustamat.jigsawpuzzlefloss.pieces.SVGEdges.HalfEdge;

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

// ImagePuzzle.Area areaParent // can be calculated when needed, instead of stored in memory.


// LargerPiece largerPieceParent;
// Point positionInLargerPiece;

Color edgesColor; // TODO: extract color from the super.imageMask part of the image.
Color highContrastBgColor;

static class SinglePieceEdges
 extends SVGEdges
{
   final WholeEdge[] nesw = new WholeEdge[4];
   
   public SinglePieceEdges (HalfEdge[][] pool,
    @Nullable RandomEdge north, @Nullable RandomEdge east, @Nullable RandomEdge south, @Nullable RandomEdge west
   ){
//      for(Direction d:Direction.values()){
//         int i= d.ordinal();
//         nesw[i] = randomEdges[i]==null?SVGEdges.getStraightEdge(i):randomEdges[i].getWholeEdge(pool,d);
//      }
      this.nesw[0] = north == null ?SVGEdges.getNorthOuterEdge() :north.getWholeEdge(pool, Direction.NORTH);
      this.nesw[1] = east == null ?SVGEdges.getEastOuterEdge() :east.getWholeEdge(pool, Direction.EAST);
      this.nesw[2] = south == null ?SVGEdges.getSouthOuterEdge() :south.getWholeEdge(pool, Direction.SOUTH);
      this.nesw[3] = west == null ?SVGEdges.getWestOuterEdge() :west.getWholeEdge(pool, Direction.WEST);
      nesw[0].setNext(nesw[1].setNext(nesw[2].setNext(nesw[3].setNext(nesw[0]))));
   }
   
   public RectF getEdgeWidths (){
      return new RectF(
       nesw[3].getEdgeWidth(),
       nesw[0].getEdgeWidth(),
       nesw[1].getEdgeWidth(),
       nesw[2].getEdgeWidth()
      );
   }
   
   public void appendToOutline (Path path){
      for (WholeEdge edge: nesw) {
         edge.appendSegmentsTo(path);
      }
   }
} // class SinglePieceEdges

public SinglePiece (ImagePuzzle imagePuzzle, Point coordinates,
 @Nullable RandomEdge north, @Nullable RandomEdge east, @Nullable RandomEdge south, @Nullable RandomEdge west,
 HalfEdge[][] pool, int randomRotation
){
   super(imagePuzzle.singlePiecesContainer, Direction.values()[randomRotation]);
   this.correctPuzzlePosition = coordinates;
   
   final float imageSize = imagePuzzle.pieceImageSize;
   this.imageOffset = new PointF(coordinates.x * imageSize, coordinates.y * imageSize);
   
   northEdge = north;
   eastEdge = east;
   southEdge = south;
   westEdge = west;
   svgEdges = new SinglePieceEdges(pool, north, east, south, west);
   
   zeroOffsetOutline = svgEdges.getOutline(0f, 0f);
   this.imageMask = new Path();
   zeroOffsetOutline.offset(imageSize * coordinates.x, imageSize * coordinates.y, imageMask);
}

public RectF getEdgeWidths (){
   // TODO: edgeWidths *= imageSize / SIDE_SIZE;
   return svgEdges.getEdgeWidths();
}

public boolean isWestEdge (){
   return westEdge == null;//EdgeType.EDGE;
}

public boolean isNorthEdge (){
   return northEdge == null;//EdgeType.EDGE;
}

public boolean isEastEdge (){
   return eastEdge == null;//EdgeType.EDGE;
}

public boolean isSouthEdge (){
   return southEdge == null;//EdgeType.EDGE;
}
}
