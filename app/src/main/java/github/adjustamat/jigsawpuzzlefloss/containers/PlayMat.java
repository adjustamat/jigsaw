package github.adjustamat.jigsawpuzzlefloss.containers;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import github.adjustamat.jigsawpuzzlefloss.pieces.AbstractPiece;
import github.adjustamat.jigsawpuzzlefloss.pieces.LargerPiece;
import github.adjustamat.jigsawpuzzlefloss.pieces.SinglePiece;

/**
 * A surface with a customizable background, for laying the puzzle on.
 */
public class PlayMat
 extends Container
{
public final List<Group> groups = new ArrayList<>();
public final List<LargerPiece> largerPieces = new ArrayList<>();
public final List<SinglePiece> singlePieces = new ArrayList<>();

public PointF topLeft = new PointF();
public PointF bottomRight = new PointF();

/*
TODO: in PlayMat, use these AbstractPiece fields:
 // PointF relativePosition
 // boolean lockedInPlace
 // boolean lockedRotation
*/

public PointF getTopLeft()
{
   return topLeft;
}

public PointF getBottomRight()
{
   return bottomRight;
}

public void removeFromContainer(AbstractPiece p)
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
   other.removeFromContainer(p);
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
   
   // TODO: if moving from a temporary container-group, should the group still exist or should the pieces be ungrouped?
   //  What happens if user creates a temporary container from an already existing group or adds a group to
   //  the container group?
//   if (other instanceof Group) {
//      Group temporaryContainer = (Group) other; // other == group !!
//
//   }
//   else {
//      Box box = (Box) other;
//
//   }
   return true;
}

// TODO: new methods: create group, ungroup, add one or several pieces to group, remove one or several pieces from group.

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
   
   // TODO: no two pieces can have the exact same coordinates! (absolute position on the play mat)
}

/**
 * Load all pieces and groups from savegame and replace Loading container with PlayMat.
 */
public void loadData(List<Group> playMatGroups,
 List<SinglePiece> playMatSinglePieces, List<LargerPiece> playMatLargerPieces)
{
   groups.addAll(playMatGroups);
   singlePieces.addAll(playMatSinglePieces);
   largerPieces.addAll(playMatLargerPieces);
   
   for (Group group: groups) {
      group.replaceLoading(this);
   }
   for (SinglePiece piece: singlePieces) {
      piece.replaceLoading(this);
   }
   for (LargerPiece piece: largerPieces) {
      piece.replaceLoading(this);
   }
}

/**
 * Handle clicking on the play mat.
 * @param clickPoint the point clicked
 * @return the number of selected pieces on the play mat
 */
public int selectDeselect(PointF clickPoint){

}

/**
 * Reset selection status of all pieces on the play mat
 */
public void deselectAll()
{

}
}
