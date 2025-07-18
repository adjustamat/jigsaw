package github.adjustamat.jigsawpuzzlefloss.pieces;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Parcel;

import androidx.annotation.Nullable;

import github.adjustamat.jigsawpuzzlefloss.containers.Box.GroupOrSinglePiece;
import github.adjustamat.jigsawpuzzlefloss.containers.Container;
import github.adjustamat.jigsawpuzzlefloss.containers.Container.Loading;
import github.adjustamat.jigsawpuzzlefloss.game.Direction;
import github.adjustamat.jigsawpuzzlefloss.game.ImagePuzzle;
import github.adjustamat.jigsawpuzzlefloss.pieces.PieceJedge.HalfJedge;
import github.adjustamat.jigsawpuzzlefloss.pieces.PieceJedge.JedgeParams;

/**
 * A piece of an {@link ImagePuzzle}. Has four edges that are either jigsaw-shaped or flat (at the
 * outer edges of the ImagePuzzle).
 */
public class SinglePiece
 extends AbstractPiece
 implements GroupOrSinglePiece
{
/**
 * The outline to draw when rotating or when drawing embossed 3D-effect.
 */
final SinglePieceJedges vectorJedges;
final JedgeParams[] neswParameters = new JedgeParams[4];

//    PointF shapeSize;
//   this.shapeSize = new PointF(
//    imageSize + jigBreadth.left + jigBreadth.right,
//    imageSize + jigBreadth.top + jigBreadth.bottom);

class SinglePieceJedges
 extends VectorJedges
{
   final PieceJedge[] nesw = new PieceJedge[4];
   
   public SinglePieceJedges(HalfJedge[][] pool)
   {
      for (Direction d: Direction.values()) {
         int i = d.ordinal();
         nesw[i] = neswParameters[i] != null
          ?neswParameters[i].getDoubleJedge(pool, d)
          :PieceJedge.getEdgeJedge(d);
      }
      nesw[3].linkNext(nesw[0].linkNext(nesw[1].linkNext(nesw[2].linkNext(nesw[3]))));
   }
   
   public RectF getJigBreadth()
   {
      return new RectF(
       nesw[3].getJigBreadth(),
       nesw[0].getJigBreadth(),
       nesw[1].getJigBreadth(),
       nesw[2].getJigBreadth()
      );
   }
   
   public int getOuterEdgesCount()
   {
      return 1;
   }
   
   public PieceJedge getFirstEdge(int hole)
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
   
   public void writeToParcel(Parcel dest)
   {
      // TODO!
   }
} // class SinglePieceJedges

public void writeToParcelFromMixedGroup(Parcel dest)
{
   dest.writeInt(0);
   super.writeToParcelFromMixedGroup(dest);
   writeOnlySinglePieceToParcel(dest);
}

public void writeToSinglePieceParcel(Parcel dest)
{
   super.writeToParcelFromMixedGroup(dest);
   writeOnlySinglePieceToParcel(dest);
}

private void writeOnlySinglePieceToParcel(Parcel dest)
{
   for (JedgeParams param: neswParameters) {
      param.writeToParcel(dest);
   }
   vectorJedges.writeToParcel(dest);
}

public static SinglePiece createSinglePieceFromParcelToBox(Parcel in, Container loading, int i)
{
   // TODO!
}

// TODO: this method (ToGroup) is used when loading into BoxGroup, PlayMatGroup and TempContainerGroup. good?
//  take into account writeToMixedGroupParcel and writeToSinglePieceParcel!
//  should I create toMixedGroup and toElsewhere instead of current three methods?
public static SinglePiece createSinglePieceFromParcelToGroup(Parcel in, Container loading, int i)
{
   // TODO!
}

public static SinglePiece createSinglePieceFromParcelToPlayMat(Parcel in, Container loading, int i)
{
   // TODO!
}

protected SinglePiece(Loading loading, int indexInContainer,
 Direction rotation, Point correct, PointF relative, boolean lockedRotation, boolean lockedPlace,
 @Nullable JedgeParams north, @Nullable JedgeParams east, @Nullable JedgeParams south, @Nullable JedgeParams west,
 HalfJedge[][] pool
)
{
   super(loading, indexInContainer, rotation, correct, relative, lockedRotation, lockedPlace);
   neswParameters[0] = north;
   neswParameters[1] = east;
   neswParameters[2] = south;
   neswParameters[3] = west;
   vectorJedges = new SinglePieceJedges(pool);
}

public SinglePiece(ImagePuzzle imagePuzzle, int indexInBox, Point coordinates,
 @Nullable JedgeParams north, @Nullable JedgeParams east, @Nullable JedgeParams south, @Nullable JedgeParams west,
 HalfJedge[][] pool, int randomRotation
)
{
   super(imagePuzzle.singlePiecesContainer, indexInBox,
    Direction.values()[randomRotation], coordinates);
   
   neswParameters[0] = north;
   neswParameters[1] = east;
   neswParameters[2] = south;
   neswParameters[3] = west;
   vectorJedges = new SinglePieceJedges(pool);

//   imageOffset = new PointF(coordinates.x * imagePuzzle.pieceImageSize,
//    coordinates.y * imagePuzzle.pieceImageSize);
//   zeroOffsetOutline = vectorEdges.drawOuterEdges(0, 0f, 0f);
   //this.imageMask = new Path();
   //zeroOffsetOutline.offset(imageOffset.x, imageOffset.y, imageMask);
}

//public Color getColor(){
//   // TODO: extract color from the vectorJedges part of the puzzle bitmap.
//}
//
//public Color getHighContrastBgColor()
//{
//   // TODO: make color with high contrast to getColor().
//}

protected VectorJedges getVectorJedges()
{
   return vectorJedges;
}

public RectF getJigBreadth()
{
   // TODO: jigBreadth *= imageSize / SIDE_SIZE;
   return vectorJedges.getJigBreadth();
}

public boolean isNorthPEdge()
{
   return neswParameters[0] == null;
}

public boolean isEastPEdge()
{
   return neswParameters[1] == null;
}

public boolean isSouthPEdge()
{
   return neswParameters[2] == null;
}

public boolean isWestPEdge()
{
   return neswParameters[3] == null;
}

/**
 * @return whether or not this piece is one of the four pieces at the outer corners of an ImagePuzzle.
 * @see #isEdgePiece()
 */
public boolean isCornerPiece()
{
   return (isWestPEdge() || isEastPEdge()) && (isNorthPEdge() || isSouthPEdge());
}
}
