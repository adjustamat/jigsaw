package github.adjustamat.jigsawpuzzlefloss.pieces;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Path.FillType;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Parcel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import github.adjustamat.jigsawpuzzlefloss.containers.Container;
import github.adjustamat.jigsawpuzzlefloss.containers.Group;
import github.adjustamat.jigsawpuzzlefloss.game.Direction;
import github.adjustamat.jigsawpuzzlefloss.game.ImagePuzzle;
import github.adjustamat.jigsawpuzzlefloss.pieces.PieceJedge.DoubleJedge;
import github.adjustamat.jigsawpuzzlefloss.ui.PlayMatView.PieceOrGroup;
import github.adjustamat.jigsawpuzzlefloss.ui.PuzzleGraphics;

/**
 * A {@link SinglePiece}, or a {@link LargerPiece} made up of two or more of the former that
 * fit together.
 */
public abstract class AbstractPiece
 implements PieceOrGroup
{
public static final float SIDE_SIZE = 120f;
public static final float HALF_SIZE = 60f;
// 50 / 1.7f == 29.411764705882355f (from 102 to 60)
// 100/ 1.7f == 58.82352941176471f (from 102 to 60)
public static final float MAX_SIZE = (25 + 7.5f + 17.5f) * 2 / 1.7f + SIDE_SIZE;
public static final int MAX_BUFFER_SIZE = 180;

private boolean selected; // NO SERIALIZATION
public boolean lockedRotation; // ALWAYS SERIALIZATION
public boolean lockedInPlace; // ALWAYS SERIALIZATION
/**
 * The visual position of this piece, relative to its Group, or if none, to its Container. Can be null in the Box.
 */
private PointF relativePos; // CONDITIONAL SERIALIZATION

public @NonNull Direction currentNorthDirection;  // (aka rotation) ALWAYS SERIALIZATION

protected Point correctPuzzlePosition; // ALWAYS SERIALIZATION

protected @NonNull Container containerParent; // NO SERIALIZATION

/**
 * actually index in Group when in a Group inside box or PlayMat!
 * @see #groupParent
 */
private int indexInContainer; // NO SERIALIZATION

protected final void serializeAbstractPieceFields(Parcel dest)
{
   dest.writeInt(currentNorthDirection.ordinal());
   dest.writeInt(correctPuzzlePosition.x);
   dest.writeInt(correctPuzzlePosition.y);
   dest.writeInt(relativePos == null ?0 :1);
   if (relativePos != null) {
      dest.writeFloat(relativePos.x);
      dest.writeFloat(relativePos.y);
   }
   dest.writeInt(lockedRotation ?0 :1);
   dest.writeInt(lockedInPlace ?0 :1);
}

/**
 * Super-constructor for LargerPiece.
 * Remember to set {@link #correctPuzzlePosition} manually in LargerPiece constructor!
 * @param containerParent container
 * @param indexInContainer index in container
 * @param rotation current rotation
 */
protected AbstractPiece(@NonNull Container containerParent, int indexInContainer,
 @NonNull Direction rotation)
{
   setContainer(containerParent, indexInContainer);
   this.currentNorthDirection = rotation;
}

/**
 * Super-constructor for SinglePiece.
 * @param correctPuzzlePosition position in finished ImagePuzzle
 */
protected AbstractPiece(@NonNull Container containerParent, int indexInContainer,
 @NonNull Direction rotation, Point correctPuzzlePosition)
{
   this(containerParent, indexInContainer, rotation);
   this.correctPuzzlePosition = correctPuzzlePosition;
}

/**
 * Super-constructor for deserializing from savegame.
 * @param relativePos current position relative to parent
 * @param lockedRotation current locked rotation status
 * @param lockedInPlace current locked position status
 */
protected AbstractPiece(@NonNull Container containerParent, int indexInContainer,
 @NonNull Direction rotation, Point correctPuzzlePosition,
 PointF relativePos, boolean lockedRotation, boolean lockedInPlace)
{
   this(containerParent, indexInContainer, rotation, correctPuzzlePosition);
   this.relativePos = relativePos;
   this.lockedInPlace = lockedInPlace;
   this.lockedRotation = lockedRotation;
}

// container:

public void replaceLoading(Container loadedContainer)
{
   containerParent = loadedContainer;
}

public void setContainer(@NonNull Container newParent, int indexInContainer)
{
   this.containerParent = newParent;
   this.indexInContainer = indexInContainer;
}

public @NonNull Container getContainer()
{
   return containerParent;
}

public int getIndexInContainer()
{
   return indexInContainer;
}

public void setIndex(int newIndex)
{
   indexInContainer = newIndex;
}

public void decrementIndex()
{
   indexInContainer--;
}

public void incrementIndex()
{
   indexInContainer++;
}

// groups:

/**
 * Can be null. TODO: Is it always non-null when the Group is a Container?
 * @see #indexInContainer
 */
protected Group groupParent;

public void setGroup(@NonNull Group newGroup, int newIndex)
{
   if (groupParent != null) {
      groupParent.remove(this);
   }
   // TODO: do this but not in this method: containerParent.remove(this);
   // TODO: the group needs to be added to containerParent or the group needs to be a temporary container!
   //  this method should probably not do a lot of things, instead do it in Box and PlayMat.
   groupParent = newGroup;
   indexInContainer = newIndex;
}

// TODO: when should this method be used? see Box and PlayMat - see their ungroupGroup and ungroupPiece methods
public void removeFromGroup(Container newContainer, int newIndexInContainer)
{
   if (groupParent != null) {
      groupParent.remove(this);
      setNullGroup();
      // TODO: this AbstractPiece needs a new indexInContainer!
   }
}

private void setNullGroup()
{
   groupParent = null;
}

public boolean isGrouped()
{
   return groupParent != null && !groupParent.isLonelyPiece();
}

// edges:

public abstract boolean isWestPuzzleEdge();
public abstract boolean isNorthPuzzleEdge();
public abstract boolean isEastPuzzleEdge();
public abstract boolean isSouthPuzzleEdge();

/**
 * @return whether or not this piece is, or contains at least, one "edge piece" - a piece at one of the outermost rows
 * or columns of an ImagePuzzle.
 */
public boolean isEdgePiece()
{
   return isWestPuzzleEdge() || isNorthPuzzleEdge() || isEastPuzzleEdge() || isSouthPuzzleEdge();
}

/**
 * @return whether or not this piece is or contains one of the four pieces at the outer corners of an ImagePuzzle.
 * @see #isEdgePiece()
 */
public boolean isCornerPiece()
{
   return (isWestPuzzleEdge() || isEastPuzzleEdge()) && (isNorthPuzzleEdge() || isSouthPuzzleEdge());
}

// ui:

public boolean isSelected()
{
   return selected;
}

public void setSelected(boolean b)
{
   selected = b;
}

public void setRelativePos(float x, float y)
{
   if (relativePos == null) {
      relativePos = new PointF(x, y);
   }
   else {
      relativePos.x = x;
      relativePos.y = y;
   }
}

public @Nullable PointF getRelativePos()
{
   return relativePos;
}

// graphics:

protected Path bufferedPath = null;
protected Bitmap buffer; // TODO: image loader?

public Bitmap getUnrotatedFullSizeGraphics()
{
   if (buffer == null) { // TODO: for LargerPieces: make buffer=null when it grows!
      VectorJedges vectorJedges = getVectorJedges();
      // TODO: image loader?
      int width = vectorJedges.width();
      int height = vectorJedges.height();
      buffer = Bitmap.createBitmap(width, height, Config.ARGB_8888);
      Canvas canvas = new Canvas(buffer);
      PuzzleGraphics.drawPiece(canvas, vectorJedges, width, height);
   }
   return buffer;
}

public Matrix getPlayMatTranslationAndRotation()
{
   Matrix matrix = new Matrix();
   
   matrix.preRotate(currentNorthDirection.degrees);
   
   float x = relativePos.x;
   float y = relativePos.y;
   if (groupParent != null) {
      x += groupParent.relativePos.x;
      y += groupParent.relativePos.y;
   }
   matrix.postTranslate(x, y);
   
   return matrix;
}

public abstract VectorJedges getVectorJedges();

public abstract RectF getJigBreadth();

public PointF getCurrentJigBreadth()
{
   RectF edges = getJigBreadth();
   switch (currentNorthDirection) {
   case NORTH: case SOUTH:
      return new PointF(edges.left + edges.right, edges.top + edges.bottom);
   default: // case WEST: case EAST:
      return new PointF(edges.top + edges.bottom, edges.left + edges.right);
   }
}

public abstract class VectorJedges
{ // TODO: perhaps we don't need VectorJedges at all, just JedgeParams.init and a buffer in LargerPiece with calculated outerJedges and holes.
//   /**
//    * Create a closed vector graphics Path of the outer edge of this AbstractPiece, with the supplied top-left corner.
//    * @param startX X of the top-left corner
//    * @param startY Y of the top-left corner
//    * @param hole an integer between 0 (inclusive) and {@link #getOuterJedgesCount()} (exclusive)
//    * @return a closed Path
//    */
//   private Path drawOuterJedges(int hole/*, float startX, float startY*/)
//   {
//      return getPath(/*startX, startY,*/ getFirstJedge(hole));
//   }
   
   public Path drawAllOuterJedges()
   {
      if (bufferedPath == null) {
         bufferedPath = new Path();
         // set fill type to respect holes:
         bufferedPath.setFillType(FillType.EVEN_ODD);
         
         // collect all jigsaw edges of the puzzle piece shape:
         int holes = getOuterJedgesCount();
         for (int i = 0; i < holes; i++) {
            Path path = getPath(getFirstJedge(i)); // drawOuterJedges(0f, 0f);
            bufferedPath.addPath(path);
         }
      }
      return bufferedPath;
   }
   
   protected /*static*/ Path getPath(/*float startX, float startY,*/ PieceJedge firstEdge)
   {
      Path ret = new Path();
      ret.moveTo(0f, 0f);//startX, startY);
      toPath(ret, firstEdge);
      ret.close();
      return ret;
   }
   
   protected /*static*/ void toPath(Path path, PieceJedge firstEdge)
   {
      // in SinglePiece: for(PieceJedge edge:nesw) edge.appendSegmentsTo(path);
      // in LargerPiece: loop with getNext(), not index in the list!
      PieceJedge nextEdge = firstEdge;
      do {
         path.incReserve(
          nextEdge instanceof DoubleJedge
           ?8 // increase with 4 points per half edge
           :1 // increase with 1 point per straight edge
         );
         nextEdge.appendSegmentsTo(path);
         nextEdge = nextEdge.getNext();
      }while (nextEdge != firstEdge);
   }
   
   public Matrix getImageTranslateMatrix(ImagePuzzle imagePuzzle)
   {
      Matrix matrix = new Matrix();
      RectF jigBreadth = getJigBreadth();
      matrix.preTranslate(correctPuzzlePosition.x * imagePuzzle.pieceImageSize - jigBreadth.left,
       correctPuzzlePosition.y * imagePuzzle.pieceImageSize - jigBreadth.top);
      return matrix;
   }
   
   protected abstract PieceJedge getFirstJedge(int hole);
   
   public abstract int getOuterJedgesCount();
   
   protected abstract int width();
   protected abstract int height();
}
}
