package github.adjustamat.jigsawpuzzlefloss.containers;

import android.content.Context;

import github.adjustamat.jigsawpuzzlefloss.game.ImagePuzzle;
import github.adjustamat.jigsawpuzzlefloss.pieces.AbstractPiece;
import github.adjustamat.jigsawpuzzlefloss.pieces.Group;

/**
 * A surface with a customizable background, for laying the puzzle on.
 */
public class PlayField
 extends Container
{
float usableMargin;
//float visibleOuterMargin;

//protected PointF positionInContainer; // null when container is Box. (in Box, use indexInContainer instead)
//protected boolean lockedInPlace; // always false when container is Box.
//public boolean isLockedInPlace()
//{
//   return lockedInPlace;
//}
//
//public void setLockedInPlace(boolean locked)
//{
//   lockedInPlace = locked;
//}

public PlayField(ImagePuzzle imagePuzzle)
{
   super(imagePuzzle);
}

public void remove(AbstractPiece p)
{

}

public void removeGroup(Group group)
{

}

public boolean movePieceFrom(Container other, AbstractPiece p)
{
   other.remove(p);
//   p.setContainer(this, );
//   .add(p);
//   if (other instanceof TemporaryStorage) {
//      TemporaryStorage storage = (TemporaryStorage) other;
//
//   }
//   else {
//      Box box = (Box) other;
//
//   }
   return true;
}

public boolean moveGroupFrom(Context ctx, Container other, Group group)
{
   other.removeGroup(group);
   group.setContainer(this);
//   .add(group);
//   if (other instanceof TemporaryStorage) {
//      TemporaryStorage storage = (TemporaryStorage) other;
//
//   }
//   else {
//      Box box = (Box) other;
//
//   }
   return true;
}

/**
 * Make all pieces in a Group non-overlapping, if possible.
 */
public void spreadOutGroup(Group group)
{
   // TODO!
}

/**
 * Make all pieces in a Group overlap, if possible.
 */
public void pileUpGroup(Group group)
{
   // TODO!
}
}
