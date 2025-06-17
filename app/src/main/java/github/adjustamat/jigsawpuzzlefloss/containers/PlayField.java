package github.adjustamat.jigsawpuzzlefloss.containers;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.ListIterator;

import github.adjustamat.jigsawpuzzlefloss.pieces.AbstractPiece;
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

/*
TODO: in PlayField, use these AbstractPiece fields:
 // PointF positionInContainer; // this already exists: relativePosition
 // boolean lockedInPlace; // see also AbstractPiece.lockedRotation
*/

public void remove(AbstractPiece p)
{
   List<? extends AbstractPiece> list = (p instanceof SinglePiece) ?singlePieces :largerPieces;
   
   int index = p.getIndexInContainer();
   list.remove(index);
   
   // all objects with higher index must decrement index:
   for (ListIterator<? extends AbstractPiece> iterator = list.listIterator(index); iterator.hasNext(); ) {
      AbstractPiece next = iterator.next();
      next.decrementIndex();
   }
}

public boolean movePieceFrom(Container other, AbstractPiece p)
{
   other.remove(p);
   p.setContainer(this, (p instanceof SinglePiece ?singlePieces :largerPieces).size());
   if (p instanceof SinglePiece) {
      singlePieces.add((SinglePiece) p);
   }
   else {
      largerPieces.add((LargerPiece) p);
   }
   return true;
}

public void removeGroup(Group group)
{
   int index = group.getIndexInContainer();
   groups.remove(index);
   
   // all objects with higher index must decrement index:
   for (ListIterator<Group> iterator = groups.listIterator(index); iterator.hasNext(); ) {
      Group next = iterator.next();
      next.decrementIndex();
   }
}

public boolean moveGroupFrom(Container other, Group group, Context ctx)
{
   other.removeGroup(group);
   group.setContainer(this, groups.size());
   groups.add(group);
//   if (other instanceof Group) {
//      Group temporaryStorage = (Group) other; // other == group !!
//
//   }
//   else {
//      Box box = (Box) other;
//
//   }
   return true;
}

/**
 * Make all pieces in a Group non-overlapping, if possible. They will never overlap with other pieces in the same Group.
 */
public void spreadOutGroup(Group group, @NonNull RectF[] within)
{
   PointF groupPos = group.relativePos;
   groupPos.set(within[0].left, within[0].top);
   for (RectF rect: within) {
      rect.offset(-groupPos.x, -groupPos.y);
   }
   group.layoutPiecesNoOverlap(0f, within);
}

/**
 * Make all pieces in a Group overlap, if possible.
 */
public void pileUpGroup(Group group, RectF within)
{
   // TODO: the area to limit the group to has to always be a bit bigger than the largest possible piece,
   //  regardless of the width and height of "within".
   
   // TODO: no two pieces can have the exact same coordinates! (absolute position on the playing field)
}
}
