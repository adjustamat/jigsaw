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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import github.adjustamat.jigsawpuzzlefloss.containers.Container;
import github.adjustamat.jigsawpuzzlefloss.containers.Group;
import github.adjustamat.jigsawpuzzlefloss.game.Direction;
import github.adjustamat.jigsawpuzzlefloss.game.ImagePuzzle;
import github.adjustamat.jigsawpuzzlefloss.pieces.PieceEdge.DoubleEdge;
import github.adjustamat.jigsawpuzzlefloss.ui.PuzzleGraphics;

/**
 * A {@link SinglePiece}, or a {@link LargerPiece} made up of two or more of the former that
 * fit together.
 */
public abstract class AbstractPiece
{
public static final float SIDE_SIZE = 120f;
public static final float HALF_SIZE = 60f;
public static final float MAX_SIZE = (25 + 7.5f + 17.5f) * 2 / 1.7f + SIDE_SIZE;
public static final int MAX_BUFFER_SIZE = 180;
// 50 / 1.7f == 29.411764705882355f (from 102 to 60)
// 100 / 1.7f ==58.82352941176471f (from 102 to 60)

/*
 * The mask of the ImagePuzzle bitmap, equal to the outline "vectorEdges".
 */
//protected Path imageMask;

/**
 * The visual position of this piece, relative to its Group, or if none, to its Container. Can be null in the Box.
 */
private PointF relativePos;
private boolean selected;
public @NonNull Direction currentRotationNorthDirection = Direction.NORTH;
public boolean lockedRotation = false;
public boolean lockedInPlace = false;

protected Point correctPuzzlePosition;

protected @NonNull Container containerParent;
private int indexInContainer;

protected AbstractPiece(@NonNull Container containerParent, int indexInContainer,
 @NonNull Direction rotation, Point correctPuzzlePosition)
{
   this(containerParent, indexInContainer, rotation);
   this.correctPuzzlePosition = correctPuzzlePosition;
}

protected AbstractPiece(@NonNull Container containerParent, int indexInContainer,
 @NonNull Direction rotation)
{
   setContainer(containerParent, indexInContainer);
   this.currentRotationNorthDirection = rotation;
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
protected Bitmap buffer;

public Bitmap getUnrotatedFullSizeGraphics()
{
   VectorEdges vectorEdges = getVectorEdges();
   
   if (buffer == null) { // TODO: for LargerPieces: make buffer=null when it grows!
      buffer = Bitmap.createBitmap(vectorEdges.width(), vectorEdges.height(), Config.ARGB_8888);
      Canvas canvas = new Canvas(buffer);
      PuzzleGraphics.drawPiece(canvas, vectorEdges);
   }
   return buffer;
}

// groups:

protected Group groupParent;

public void setGroup(@NonNull Group newGroup, int newIndex)
{
   if (groupParent != null) {
      groupParent.remove(this);
   }
   // TODO: do this but not in this method: containerParent.remove(this); // the group needs to be added to containerParent or the group needs to be a temporary storage!
   groupParent = newGroup;
   indexInContainer = newIndex;
}

public void removeFromGroup(Container newContainer)
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

public abstract RectF getEdgeWidths();

public PointF getCurrentEdgeWidths()
{
   RectF edges = getEdgeWidths();
   switch (currentRotationNorthDirection) {
   case NORTH: case SOUTH:
      return new PointF(edges.left + edges.right, edges.top + edges.bottom);
   default: // case WEST: case EAST:
      return new PointF(edges.top + edges.bottom, edges.left + edges.right);
   }
}

protected abstract VectorEdges getVectorEdges();

public abstract boolean isWestEdge();
public abstract boolean isNorthEdge();
public abstract boolean isEastEdge();
public abstract boolean isSouthEdge();

/**
 * @return whether or not this piece is, or contains at least, one "edge piece" - a piece at one of the outermost rows
 * or columns of an ImagePuzzle.
 */
public boolean isEdgePiece()
{
   return isWestEdge() || isNorthEdge() || isEastEdge() || isSouthEdge();
}

public abstract class VectorEdges
{
   /**
    * Create a closed vector graphics Path of the outer edge of this AbstractPiece, with the supplied top-left corner.
    * @param hole an integer between 0 (inclusive) and {@link #getOuterEdgesCount()} (exclusive)
    * @return a closed Path
    */
   private Path drawOuterEdges(int hole/*, float startX, float startY*/)
   {
//         *@param startX X of the top - left corner
//    * @param startY Y of the top -left corner
      return getPath(/*startX, startY,*/ getFirstEdge(hole));
   }
   
   public Path drawOuterEdges()
   {
      if (bufferedPath == null) {
         bufferedPath = new Path();
         // set fill type to respect holes:
         bufferedPath.setFillType(FillType.EVEN_ODD);
         
         // collect all edges of the puzzle piece shape:
         int holes = getOuterEdgesCount();
         for (int i = 0; i < holes; i++) {
            Path path = drawOuterEdges(i/*, 0f, 0f*/);
            bufferedPath.addPath(path);
         }
      }
      return bufferedPath;
   }
   
   protected /*static*/ Path getPath(/*float startX, float startY,*/ PieceEdge firstEdge)
   {
      Path ret = new Path();
      ret.moveTo(0f, 0f);//startX, startY);
      toPath(ret, firstEdge);
      ret.close();
      return ret;
   }
   
   protected /*static*/ void toPath(Path path, PieceEdge firstEdge)
   {
      // in SinglePiece: for(PieceEdge edge:nesw) edge.appendSegmentsTo(path);
      // in LargerPiece: loop with getNext(), not index in the list!
      PieceEdge nextEdge = firstEdge;
      do {
         path.incReserve(
          nextEdge instanceof DoubleEdge
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
      RectF edgeWidths = getEdgeWidths();
      matrix.preTranslate(correctPuzzlePosition.x * imagePuzzle.pieceImageSize - edgeWidths.left,
       correctPuzzlePosition.y * imagePuzzle.pieceImageSize - edgeWidths.top);
      return matrix;
   }
   
   protected abstract PieceEdge getFirstEdge(int hole);
   
   public abstract int getOuterEdgesCount();
   
   protected abstract int width();
   protected abstract int height();
}
}
