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
 * The mask of the ImagePuzzle image. Consists of the outline SVGPath turned into a graphics.Path.
 */
protected Path imageMask;
protected Point correctPuzzlePosition;

protected Group groupParent;
protected Integer indexInGroup; // null when not in any Group.

protected @NonNull Container containerParent;
protected int indexInContainer;
protected PointF positionInContainer; // null when container is Box. (use indexInContainer instead)
protected boolean lockedInPlace; // always false when container is Box.

protected @NonNull Direction currentRotationNorthDirection;

protected AbstractPiece (@NonNull Container containerParent, @NonNull Direction rotation){
   this.containerParent = containerParent;
   currentRotationNorthDirection = rotation;
}

public void setContainer (Container newParent){
   this.containerParent = newParent;
}

public @NonNull Container getContainer (){
   return containerParent;
}

public boolean isLockedInPlace (){
   return lockedInPlace;
}

public void setLockedInPlace (boolean locked){
   lockedInPlace = locked;
}

void removeFromGroup (){
   if (groupParent != null) {
      groupParent.removeMe(indexInGroup);
      setNullGroup();
   }
}

private void setNullGroup (){
   groupParent = null;
   indexInGroup = null;
}

void setGroup (@NonNull Group group, int index){
   if (groupParent != null) {
      groupParent.removeMe(indexInGroup);
   }
   groupParent = group;
   indexInGroup = index;
}

public boolean isGrouped (){
   return groupParent != null && !groupParent.isLonelyPiece();
}

public abstract RectF getEdgeWidths ();

public abstract boolean isWestEdge ();
public abstract boolean isNorthEdge ();
public abstract boolean isEastEdge ();
public abstract boolean isSouthEdge ();

public boolean isEdgePiece (){
   return isWestEdge() || isNorthEdge() || isEastEdge() || isSouthEdge();
}

public boolean isCornerPiece (){
   return (isWestEdge() || isEastEdge()) && (isNorthEdge() || isSouthEdge());
}

public abstract static class VectorEdges
{

/**
 * Create a vector graphics closed Path with the supplied top-left corner.
 * @param startX X of top-left corner
 * @param startY Y of top-left corner
 * @return a closed Path
 */
public Path getPath (float startX, float startY){
   Path ret = new Path();
   ret.moveTo(startX, startY);
   this.appendToPath(ret);
   ret.close();
   return ret;
}

public abstract void appendToPath (Path path);

}
}
