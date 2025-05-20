package github.adjustamat.jigsawpuzzlefloss.pieces;

import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.Pair;

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

/**
 * The mask of the ImagePuzzle image. Consists of the outline SVGPath turned into a graphics.Path.
 */
protected Path imageMask;
protected Point correctPuzzlePosition;

protected Group groupParent;
protected Integer indexInGroup; // TODO: null when not in any Group. or -1 int?

protected @NonNull Container containerParent;
int indexInContainer;

public int getIndexInContainer()
{
   return indexInContainer;
}

protected @NonNull Direction currentRotationNorthDirection;

protected AbstractPiece(@NonNull Container containerParent, @NonNull Direction rotation)
{
   this.containerParent = containerParent;
   currentRotationNorthDirection = rotation;
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

public abstract RectF getEdgeWidths();

public abstract boolean isWestEdge();
public abstract boolean isNorthEdge();
public abstract boolean isEastEdge();
public abstract boolean isSouthEdge();

/**
 * @return whether or not this piece is an "edge piece" - being at one of the outermost rows or columns of an
 * ImagePuzzle.
 */
public boolean isEdgePiece()
{
   return isWestEdge() || isNorthEdge() || isEastEdge() || isSouthEdge();
}

/**
 * @return whether or not this piece is one of the four "edge pieces" at the corners of an ImagePuzzle.
 * @see #isEdgePiece()
 */
public boolean isCornerPiece()
{
   return (isWestEdge() || isEastEdge()) && (isNorthEdge() || isSouthEdge());
}

public abstract static class VectorEdges
{
   
   /**
    * Create a closed vector graphics Path of the outer edge of this AbstractPiece, with the supplied top-left corner.
    * @param startX X of the top-left corner
    * @param startY Y of the top-left corner
    * @return a closed Path
    */
   public Pair<Path, Integer> getOuterEdgePath(float startX, float startY)
   {
      Path ret = new Path();
      ret.moveTo(startX, startY);
      int num = this.toOuterEdgePath(ret);
      ret.close();
      return new Pair<>(ret, num);
   }
   
   public Path getPath(float startX, float startY, PieceEdge firstEdge)
   {
      Path ret = new Path();
      ret.moveTo(startX, startY);
      PieceEdge nextEdge = firstEdge;
      do {
         nextEdge.appendSegmentsTo(ret);
         nextEdge = nextEdge.getNext();
      }while (nextEdge != firstEdge);
      ret.close();
      return ret;
   }
   
   /**
    * Append all Path segments of the outer edge to {@code path}. If there is more than one outer edge (holes),
    * append all segments of the "first hole" and return the number of "holes".
    * @param path the Path
    * @return the number of outer edges of this AbstractPiece
    */
   public abstract int toOuterEdgePath(Path path);
}
}
