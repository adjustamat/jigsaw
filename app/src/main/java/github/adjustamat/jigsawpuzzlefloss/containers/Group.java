package github.adjustamat.jigsawpuzzlefloss.containers;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import github.adjustamat.jigsawpuzzlefloss.R;
import github.adjustamat.jigsawpuzzlefloss.containers.Box.GroupOrSinglePiece;
import github.adjustamat.jigsawpuzzlefloss.pieces.AbstractPiece;
import github.adjustamat.jigsawpuzzlefloss.pieces.LargerPiece;
import github.adjustamat.jigsawpuzzlefloss.pieces.SinglePiece;

/**
 * A (named) group of {@link AbstractPiece}s ({@link SinglePiece}s and/or {@link LargerPiece}s).
 * A group of only SinglePieces can be in the {@link Box}, and any group can be on the {@link PlayField}.
 * A group can also constitute a temporary Container by itself.
 * On the PlayField, a group can be a pile (with all pieces overlapping), spread out (with each piece visible),
 * or in a custom state (with some pieces overlapping).
 */
public class Group
 extends Container
 implements GroupOrSinglePiece
{
private static final String DBG = "Group";
private static int counter = 0;

public class Dirty
{
   public boolean overlapping = true;
   public boolean pile = true;
   public boolean layout = true;
}

public final Dirty dirty = new Dirty();

/**
 * The visual position of this group, relative to its Container, or if it's its own Container, to the screen.
 */
public final PointF relativePos = new PointF();

private boolean selected;
private boolean expanded;

private @Nullable String name;
private final int myNumber;
Container containerParent;
private int indexInContainer;

final List<AbstractPiece> pieces = new LinkedList<>();
int largerPieces = 0;

public Group(Container container, int indexInContainer)
{
   myNumber = ++counter;
   setContainer(container, indexInContainer);
}

/**
 * Create a Container to hide some pieces out of the way.
 */
public Group getNewTemporaryContainer(int newIndex)
{
   return new Group(newIndex);
}

private Group(int newIndex)
{
   myNumber = 0;
   setContainer(this, newIndex);
}

public void setContainer(Container newParent, int indexInContainer)
{
   if (newParent == null) { // merging this Group into another Group!
      pieces.clear();
   }
   containerParent = newParent;
   this.indexInContainer = indexInContainer;
}

public @NonNull String getName(Context ctx)
{
   if (name == null)
      name = ctx.getString(R.string.group_default_name, myNumber);
   return name;
}

/**
 * Trims spaces from name before setting it as this Group's name.
 * @param name a String or null
 */
public void setName(@Nullable String name)
{
   if (name != null) {
      name = name.trim();
      if (name.isEmpty())
         name = null;
   }
   this.name = name;
}

public static void layoutPiecesNoOverlap(Collection<AbstractPiece> pieces, float minMargin,
 @Nullable RectF[] within)
{
   Iterator<AbstractPiece> pieceIterator = pieces.iterator();
   AbstractPiece piece;
   PointF edgeWidths;
   int c = 0;
   float widthSum = 0f, heightSum = 0f, edgeHeightsMax = 0f;
   if (within != null) {
      int rectangles = within.length;
      outer:
      for (int i = 0; i < rectangles; i++) {
         // layout until reaching right edge of rect, new row until bottom edge of rect, next rect from (0,0)
         float rectheight = within[i].height();
         float rectwidth = within[i].width();
         while (heightSum < rectheight) {
            while (widthSum < rectwidth) {
               if (!pieceIterator.hasNext())
                  break outer;
               piece = pieceIterator.next();
               edgeWidths = piece.getCurrentEdgeWidths();
               widthSum += edgeWidths.x + (AbstractPiece.SIDE_SIZE + minMargin);
               edgeHeightsMax = Math.max(edgeHeightsMax, edgeWidths.y);
               piece.setRelativePos(widthSum + within[i].left, heightSum + within[i].top);
               c++;
            }
            c = 0;
            heightSum += edgeHeightsMax + (AbstractPiece.SIDE_SIZE + minMargin);
            edgeHeightsMax = widthSum = 0f;
         }
         // if there are more pieces in pieceIterator, continue with algorithm below.
         if (i < rectangles - 1) {
            c = 0;
            edgeHeightsMax = widthSum = heightSum = 0f;
         }
      } // for(RectF within)
   }
   int size = pieces.size();
   int cols = (size <= 12) ?3 :(size <= 30) ?5 :(size <= 56) ?7 :9;
   while (pieceIterator.hasNext()) {
      piece = pieceIterator.next();
      edgeWidths = piece.getCurrentEdgeWidths();
      widthSum += edgeWidths.x + (AbstractPiece.SIDE_SIZE + minMargin);
      edgeHeightsMax = Math.max(edgeHeightsMax, edgeWidths.y);
      piece.setRelativePos(widthSum, heightSum);
      c++;
      if (c == cols) {
         c = 0;
         heightSum += edgeHeightsMax + (AbstractPiece.SIDE_SIZE + minMargin);
         edgeHeightsMax = widthSum = 0f;
      }
   } // while (pieceIterator.hasNext())
}

public void layoutPiecesNoOverlap(float margin, @Nullable RectF[] within)
{
   // TODO: first check if(dirty.layout) if this method is used to make an image for BoxAdapter.
   layoutPiecesNoOverlap(pieces, margin, within);
   dirty.layout = false;
   // TODO: overlapping = false; dirty.overlapping = false;
}

public List<AbstractPiece> getAllPieces()
{
   return pieces;
}

public void add(Collection<AbstractPiece> pieces)
{
   add(pieces, false);
}

private void add(Collection<AbstractPiece> pieces, boolean isContainer)
{
   for (AbstractPiece piece: pieces) {
      if (isContainer) {
         piece.setContainer(this, pieces.size());
      }
      add(piece);
   }
}

public void add(AbstractPiece piece)
{
   int size = size();
   PointF piecePos = piece.getRelativePos();
   if (piecePos == null)
      dirty.layout = true;
   else {
      if (size == 0) {
         this.relativePos.set(piecePos.x, piecePos.y);
         piecePos.set(0f, 0f);
         dirty.layout = false;
      }
      else {
         piecePos.offset(-relativePos.x, -relativePos.y);
      }
   }
   piece.setGroup(this, size);
   pieces.add(piece);
   if (piece instanceof LargerPiece)
      largerPieces++;
}

/**
 * Move a piece here from another Container - only use when this Group is a temporary storage!
 * @param other a Container
 * @param piece the piece
 * @return true
 */
public boolean movePieceFrom(Container other, AbstractPiece piece)
{
   other.remove(piece);
   piece.setContainer(this, pieces.size());
   add(piece);
//   if (other instanceof Box) {
//      Box box = (Box) other;
//
//   }
//   else {
//      PlayField playField = (PlayField) other;
//
//   }
   return true;
}

/**
 * Move a Group of pieces here from another Container - only use when this Group is a temporary storage!
 * @param other a Container
 * @param group the pieces
 * @param ctx a Context
 * @return whether or not the Group was merged into this Group
 */
public boolean moveGroupFrom(Container other, Group group, Context ctx)
{
   // TODO: merge group into this group, but ask first! (ctx)
//   if (ANSWER_IS_CANCEL) {
//      return false;
//   }
   other.removeGroup(group);
   add(group.getAllPieces(), true);
   // delete the group:
   group.setContainer(null, -1);
   return true;
}

public void remove(AbstractPiece p)
{
   pieces.remove(p.getIndexInContainer());
}

/**
 * Do nothing.
 * @param group == this
 */
public void removeGroup(Group group)
{
   if (group != this) {
      throw new UnsupportedOperationException("No Groups inside a Group!");
   }
   Log.i(DBG, "removing Group from itself");
}

public boolean hasLargerPieces()
{
   return largerPieces > 0;
}

public boolean isLonelyPiece()
{
   return pieces.size() == 1;
}

public boolean isEmpty()
{
   return pieces.isEmpty();
}

public int size()
{
   return pieces.size();
}

public int getIndexInContainer()
{
   return indexInContainer;
}

public boolean isSelected()
{
   return selected;
}

public void setSelected(boolean b)
{
   selected = b;
}

public boolean isExpanded()
{
   return expanded;
}

public void setExpanded(boolean expanded)
{
   this.expanded = expanded;
   Box box = (Box) containerParent;
   box.setExpanded(this, expanded);
}

//public boolean isPile() // TODO: maybe move these to PlayField.java
//{
//   // TOD
//   return false;
//}
//public boolean isOverlapping()
//{
//   if(dirty.overlapping){ // TOD
//   }
//   return overlapping;
//}
}
