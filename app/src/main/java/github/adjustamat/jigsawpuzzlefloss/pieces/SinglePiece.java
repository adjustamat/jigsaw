package github.adjustamat.jigsawpuzzlefloss.pieces;

import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;

import androidx.annotation.Nullable;

import github.adjustamat.jigsawpuzzlefloss.game.Box.GroupOrSinglePiece;
import github.adjustamat.jigsawpuzzlefloss.game.ImagePuzzle;
import github.adjustamat.jigsawpuzzlefloss.pieces.PieceEdge.HalfEdge;
import github.adjustamat.jigsawpuzzlefloss.pieces.PieceEdge.RandomEdge;

/**
 * A piece of an {@link ImagePuzzle}. Has four edges that are either jigsaw-shaped or flat (at the
 * outer edges of the ImagePuzzle).
 */
public class SinglePiece
 extends AbstractPiece
 implements GroupOrSinglePiece
{
final RandomEdge westEdge;
final RandomEdge northEdge;
final RandomEdge eastEdge;
final RandomEdge southEdge;
final RandomEdge[] randomEdgesNesw = new RandomEdge[4];

/**
 * The outline to draw when rotating or when drawing embossed 3D-effect.
 */
final SinglePieceEdges vectorEdges;

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

class SinglePieceEdges
 extends VectorEdges
{
   final PieceEdge[] nesw = new PieceEdge[4];
   
   public SinglePieceEdges(HalfEdge[][] pool)
   {
      for (Direction d: Direction.values()) {
         int i = d.ordinal();
         nesw[i] = randomEdgesNesw[i] == null
          ?PieceEdge.getStraightEdge(d)
          :randomEdgesNesw[i].getPieceEdge(pool, d);
      }
      nesw[3].linkNext(nesw[0].linkNext(nesw[1].linkNext(nesw[2].linkNext(nesw[3]))));
   }
   
   public RectF getEdgeWidths()
   {
      return new RectF(
       nesw[3].getEdgeWidth(),
       nesw[0].getEdgeWidth(),
       nesw[1].getEdgeWidth(),
       nesw[2].getEdgeWidth()
      );
   }
   
   public int toOuterEdgePath(Path path)
   {
      for (PieceEdge edge: nesw) {
         edge.appendSegmentsTo(path);
      }
      return 1;
   }
} // class SinglePieceEdges

public SinglePiece(ImagePuzzle imagePuzzle, Point coordinates,
 @Nullable RandomEdge north, @Nullable RandomEdge east, @Nullable RandomEdge south, @Nullable RandomEdge west,
 HalfEdge[][] pool, int randomRotation
)
{
   super(imagePuzzle.singlePiecesContainer, Direction.values()[randomRotation]);
   this.correctPuzzlePosition = coordinates;
   
   final float imageSize = imagePuzzle.pieceImageSize;
   this.imageOffset = new PointF(coordinates.x * imageSize, coordinates.y * imageSize);
   
   northEdge = north;
   eastEdge = east;
   southEdge = south;
   westEdge = west;
   vectorEdges = new SinglePieceEdges(pool);
   
   zeroOffsetOutline = vectorEdges.getOuterEdgePath(0f, 0f).first;
   
   this.imageMask = new Path();
   zeroOffsetOutline.offset(imageSize * coordinates.x, imageSize * coordinates.y, imageMask);
}

public RectF getEdgeWidths()
{
   // TODO: edgeWidths *= imageSize / SIDE_SIZE;
   return vectorEdges.getEdgeWidths();
}

public boolean isNorthEdge()
{
   return randomEdgesNesw[0] == null;
}

public boolean isEastEdge()
{
   return randomEdgesNesw[1] == null;
}

public boolean isSouthEdge()
{
   return randomEdgesNesw[2] == null;
}

public boolean isWestEdge()
{
   return randomEdgesNesw[3] == null;
}

}
