package github.adjustamat.jigsawpuzzlefloss.pieces;

import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;

import androidx.annotation.NonNull;

import github.adjustamat.jigsawpuzzlefloss.containers.Container;
import github.adjustamat.jigsawpuzzlefloss.game.Direction;

/**
 * A {@link SinglePiece}, or a {@link LargerPiece} made up of two or more of the former that
 * fit together.
 */
public abstract class AbstractPiece
{
public static final float SIDE_SIZE = 120f;
public static final float HALF_SIZE = 60f;

/*
 * The mask of the ImagePuzzle bitmap, equal to the outline "vectorEdges".
 */
//protected Path imageMask;

protected Point correctPuzzlePosition;
protected @NonNull Direction currentRotationNorthDirection;

protected @NonNull Container containerParent;
private int indexInContainer;

protected AbstractPiece(@NonNull Container containerParent, int indexInContainer,
 @NonNull Direction rotation, Point correctPuzzlePosition)
{
   setContainer(containerParent, indexInContainer);
   this.currentRotationNorthDirection = rotation;
   this.correctPuzzlePosition = correctPuzzlePosition;
}

protected AbstractPiece(@NonNull Container containerParent, int indexInContainer,
 @NonNull Direction rotation)
{
   setContainer(containerParent, indexInContainer);
   this.currentRotationNorthDirection = rotation;
}
public void setContainer(Container newParent, int indexInContainer)
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

// groups:

protected Group groupParent;
protected Integer indexInGroup; // TODO: null when not in any Group. or -1 int?

void setGroup(@NonNull Group group, int index)
{
   if (groupParent != null) {
      groupParent.removeMe(indexInGroup);
   }
   groupParent = group;
   indexInGroup = index;
}

public void removeFromGroup()
{
   if (groupParent != null) {
      groupParent.removeMe(indexInGroup);
      setNullGroup();
   }
}

private void setNullGroup()
{
   groupParent = null;
   indexInGroup = null;
}

public boolean isGrouped()
{
   return groupParent != null && !groupParent.isLonelyPiece();
}

// edges:

public abstract RectF getEdgeWidths();

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

public abstract static class VectorEdges
{
   /**
    * Create a closed vector graphics Path of the outer edge of this AbstractPiece, with the supplied top-left corner.
    * @param hole an integer between 0 (inclusive) and {@link #getOuterEdgesCount()} (exclusive)
    * @param startX X of the top-left corner
    * @param startY Y of the top-left corner
    * @return a closed Path
    */
   public Path drawOuterEdges(int hole, float startX, float startY)
   {
      return getPath(startX, startY, getFirstEdge(hole));
   }
   
   protected static Path getPath(float startX, float startY, PieceEdge firstEdge)
   {
      Path ret = new Path();
      ret.moveTo(startX, startY);
      toPath(ret, firstEdge);
      ret.close();
      return ret;
   }
   
   protected static void toPath(Path path, PieceEdge firstEdge)
   {
      // in SinglePiece: for(PieceEdge edge:nesw) edge.appendSegmentsTo(path);
      // in LargerPiece: loop with getNext(), not index in the list!
      PieceEdge nextEdge = firstEdge;
      do {
         nextEdge.appendSegmentsTo(path);
         nextEdge = nextEdge.getNext();
      }while (nextEdge != firstEdge);
   }


//   public Path drawOuterEdges(float startX, float startY, int hole)
//   {
//      return getPath(startX, startY, outerEdgeHoles.get(hole).get(0));
//   }
   
   protected abstract PieceEdge getFirstEdge(int hole);
   
   public abstract int getOuterEdgesCount();
}
}
