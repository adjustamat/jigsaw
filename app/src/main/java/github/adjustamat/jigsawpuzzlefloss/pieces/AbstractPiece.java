package github.adjustamat.jigsawpuzzlefloss.pieces;

import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;

import androidx.annotation.NonNull;

import github.adjustamat.jigsawpuzzlefloss.game.Container;

/**
 * A {@link SinglePiece}, or a {@link LargerPiece} made up of two or more of the former that
 * fit together.
 */
public abstract class AbstractPiece
{
public static final float SIDE_SIZE = 120f;
public static final float HALF_SIZE = 60f;

/**
 * The type of jigsaw edge of a puzzle piece.
 */
public enum EdgeType
{
   EDGE,
   IN,
   OUT
}

Direction currentRotation;

EdgeType leftEdge;
EdgeType topEdge;
EdgeType rightEdge;
EdgeType bottomEdge;

/**
 * The mask of the ImagePuzzle image. Consists of the outline SVGPath turned into a graphics.Path.
 */
Path imageMask;
Point correctPuzzlePosition;

Group groupParent;
Integer indexInGroup; // null when not in any Group.

@NonNull Container containerParent;
public int indexInContainer;
PointF positionInContainer; // null when container is Box. (use indexInContainer instead)
boolean lockedInPlace; // always false when container is Box.

protected AbstractPiece(@NonNull Container containerParent)
{
   this.containerParent = containerParent;
}

public void setContainer(Container newParent)
{
   this.containerParent = newParent;
}

public @NonNull Container getContainer()
{
   return containerParent;
}

public abstract RectF getEdgeWidths();

public boolean isLockedInPlace()
{
   return lockedInPlace;
}

public void setLockedInPlace(boolean locked)
{
   lockedInPlace = locked;
}

void removeFromGroup()
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

void setGroup(@NonNull Group group, int index)
{
   if (groupParent != null) {
      groupParent.removeMe(indexInGroup);
   }
   groupParent = group;
   indexInGroup = index;
}

public boolean isGrouped()
{
   return groupParent != null && !groupParent.isLonelyPiece();
}

public boolean isLeftEdge()
{
   return leftEdge == EdgeType.EDGE;
}

public boolean isTopEdge()
{
   return topEdge == EdgeType.EDGE;
}

public boolean isRightEdge()
{
   return rightEdge == EdgeType.EDGE;
}

public boolean isBottomEdge()
{
   return bottomEdge == EdgeType.EDGE;
}

public boolean isEdgePiece()
{
   return isLeftEdge() || isTopEdge() || isRightEdge() || isBottomEdge();
}

public boolean isCornerPiece()
{
   return (isLeftEdge() || isRightEdge()) && (isTopEdge() || isBottomEdge());
}
}
