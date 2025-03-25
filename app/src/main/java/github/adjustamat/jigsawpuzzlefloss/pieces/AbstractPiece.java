package github.adjustamat.jigsawpuzzlefloss.pieces;

import androidx.annotation.NonNull;
import android.graphics.PointF;
import github.adjustamat.jigsawpuzzlefloss.game.Container;

/**
 * A {@link SinglePiece}, or a {@link LargerPiece} made up of two or more of the former that
 * fit together.
 */
public abstract class AbstractPiece
{

Group groupParent;
Integer indexInGroup;

@NonNull Container containerParent;
public int indexInContainer;

PointF positionInContainer; // null when container is Box.
boolean lockedInPlace; // irrelevant when container is Box.



protected AbstractPiece(@NonNull Container containerParent)
{
   this.containerParent = containerParent;
}

public @NonNull Container getContainer()
{
   return containerParent;
}

public void setContainer(Container newParent)
{


}

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
   if(groupParent != null) {
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
   if(groupParent != null) {
      groupParent.removeMe(indexInGroup);
   }
   groupParent = group;
   indexInGroup = index;
}

public boolean isGrouped()
{
   return groupParent != null && !groupParent.isLonelyPiece();
}

}
