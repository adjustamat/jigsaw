package github.adjustamat.jigsawpuzzlefloss.pieces;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Parcel;

import androidx.annotation.Nullable;

import github.adjustamat.jigsawpuzzlefloss.containers.Box.GroupOrSinglePiece;
import github.adjustamat.jigsawpuzzlefloss.containers.Container.Loading;
import github.adjustamat.jigsawpuzzlefloss.game.Direction;
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

Color edgesColor; // TODO: extract color from the super.imageMask part of the image.
Color highContrastBgColor;

/**
 * The outline to draw when rotating or when drawing embossed 3D-effect.
 */
final SinglePieceEdges vectorEdges;
final RandomEdge[] neswRandomEdges = new RandomEdge[4];

//    PointF shapeSize;
//   this.shapeSize = new PointF(
//    imageSize + edgeWidths.left + edgeWidths.right,
//    imageSize + edgeWidths.top + edgeWidths.bottom);

class SinglePieceEdges
 extends VectorEdges
{
   final PieceEdge[] nesw = new PieceEdge[4];
   
   public SinglePieceEdges(HalfEdge[][] pool)
   {
      for (Direction d: Direction.values()) {
         int i = d.ordinal();
         nesw[i] = neswRandomEdges[i] != null
          ?neswRandomEdges[i].getPieceEdge(pool, d)
          :PieceEdge.getStraightEdge(d);
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
   
   public int getOuterEdgesCount()
   {
      return 1;
   }
   
   public PieceEdge getFirstEdge(int hole)
   {
      return nesw[0];
   }
   
   protected int width()
   {
      return MAX_BUFFER_SIZE;
   }
   
   protected int height()
   {
      return MAX_BUFFER_SIZE;
   }
} // class SinglePieceEdges

public void writeToParcel(Parcel dest, int flags)
{
   // TODO!
}

public static SinglePiece createFromParcelToBox(Parcel in, Loading loading)
{
   // TODO!
}

public static SinglePiece createFromParcelToGroup(Parcel in, Loading loading)
{
   // TODO!
}

public static SinglePiece createFromParcelToPlayMat(Parcel in, Loading loading)
{
   // TODO!
}

protected SinglePiece(Loading loading, int indexInContainer,
 Direction rotation, Point correct, float x, float y, boolean lockedRotation, boolean lockedPlace,
 @Nullable RandomEdge north, @Nullable RandomEdge east, @Nullable RandomEdge south, @Nullable RandomEdge west,
 HalfEdge[][] pool
)
{
   super(loading, indexInContainer, rotation, correct, x, y, lockedRotation, lockedPlace);
   neswRandomEdges[0] = north;
   neswRandomEdges[1] = east;
   neswRandomEdges[2] = south;
   neswRandomEdges[3] = west;
   vectorEdges = new SinglePieceEdges(pool);
}

public SinglePiece(ImagePuzzle imagePuzzle, int indexInBox, Point coordinates,
 @Nullable RandomEdge north, @Nullable RandomEdge east, @Nullable RandomEdge south, @Nullable RandomEdge west,
 HalfEdge[][] pool, int randomRotation
)
{
   super(imagePuzzle.singlePiecesContainer, indexInBox,
    Direction.values()[randomRotation], coordinates);
   
   neswRandomEdges[0] = north;
   neswRandomEdges[1] = east;
   neswRandomEdges[2] = south;
   neswRandomEdges[3] = west;
   vectorEdges = new SinglePieceEdges(pool);

//   imageOffset = new PointF(coordinates.x * imagePuzzle.pieceImageSize,
//    coordinates.y * imagePuzzle.pieceImageSize);
//   zeroOffsetOutline = vectorEdges.drawOuterEdges(0, 0f, 0f);
   //this.imageMask = new Path();
   //zeroOffsetOutline.offset(imageOffset.x, imageOffset.y, imageMask);
}

protected VectorEdges getVectorEdges()
{
   return vectorEdges;
}

public RectF getEdgeWidths()
{
   // TODO: edgeWidths *= imageSize / SIDE_SIZE;
   return vectorEdges.getEdgeWidths();
}

public boolean isNorthEdge()
{
   return neswRandomEdges[0] == null;
}

public boolean isEastEdge()
{
   return neswRandomEdges[1] == null;
}

public boolean isSouthEdge()
{
   return neswRandomEdges[2] == null;
}

public boolean isWestEdge()
{
   return neswRandomEdges[3] == null;
}

/**
 * @return whether or not this piece is one of the four pieces at the outer corners of an ImagePuzzle.
 * @see #isEdgePiece()
 */
public boolean isCornerPiece()
{
   return (isWestEdge() || isEastEdge()) && (isNorthEdge() || isSouthEdge());
}
}
