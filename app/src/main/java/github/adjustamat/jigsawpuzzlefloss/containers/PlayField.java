package github.adjustamat.jigsawpuzzlefloss.containers;

import android.content.Context;
import android.graphics.drawable.shapes.PathShape;

import java.util.List;

import github.adjustamat.jigsawpuzzlefloss.pieces.AbstractPiece;
import github.adjustamat.jigsawpuzzlefloss.pieces.Group;
import github.adjustamat.jigsawpuzzlefloss.pieces.LargerPiece;
import github.adjustamat.jigsawpuzzlefloss.pieces.SinglePiece;

/**
 * A surface with a customizable background, for laying the puzzle on.
 */
public class PlayField
 extends Container
{
float usableMargin;
List<Group> groups;
List<LargerPiece> largerPieces;
List<SinglePiece> singlePieces;

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

//public PlayField(ImagePuzzle imagePuzzle)
//{
//   super(imagePuzzle);
//}

public void remove(AbstractPiece p)
{
   (p instanceof SinglePiece ?singlePieces :largerPieces).remove(p.getIndexInContainer());
}

public void removeGroup(Group group)
{
   groups.remove(group.getIndexInContainer());
}

public boolean movePieceFrom(Container other, AbstractPiece p)
{
   other.remove(p);
   List<?> list = (p instanceof SinglePiece ?singlePieces :largerPieces);
   p.setContainer(this, list.size());
   if (p instanceof SinglePiece) {
      singlePieces.add((SinglePiece) p);
   }
   else {
      largerPieces.add((LargerPiece) p);
   }
   return true;
}

public boolean moveGroupFrom(Container other, Group group, Context ctx)
{
   // TODO: when if anytime will this method return false and use ctx?
   other.removeGroup(group);
   group.setContainer(this,groups.size());
   groups.add(group);
//   if (other instanceof Group) {
//      Group temporaryStorage = (Group) other;
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
public void spreadOutGroup(Group group, PathShape area)
{
   // TODO: supply which area it can be spread out over
   // TODO: give the group a coordinate and the pieces coordinates relative to that.
}

/**
 * Make all pieces in a Group overlap, if possible.
 */
public void pileUpGroup(Group group, PathShape area)
{
   // TODO: supply area to limit the group to
   // TODO: give the group a coordinate and the pieces coordinates relative to that.
   //  no two pieces can have the exact same coordinates!
}
}
