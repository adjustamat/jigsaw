package github.adjustamat.jigsawpuzzlefloss.containers;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Parcel;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import github.adjustamat.jigsawpuzzlefloss.R;
import github.adjustamat.jigsawpuzzlefloss.containers.Box.GroupOrSinglePiece;
import github.adjustamat.jigsawpuzzlefloss.game.ImagePuzzle;
import github.adjustamat.jigsawpuzzlefloss.pieces.AbstractPiece;
import github.adjustamat.jigsawpuzzlefloss.pieces.LargerPiece;
import github.adjustamat.jigsawpuzzlefloss.pieces.PieceJedge.HalfJedge;
import github.adjustamat.jigsawpuzzlefloss.pieces.SinglePiece;

/**
 * A (named) group of {@link AbstractPiece}s ({@link SinglePiece}s and/or {@link LargerPiece}s).
 * A group of only SinglePieces can be in the {@link Box}, and any group can be on the {@link PlayMat}.
 * A group can also constitute a temporary Container by itself.
 * On the PlayMat, a group can be a pile (with all pieces overlapping), spread out (with each piece visible),
 * or in a custom state (with some pieces overlapping).
 */
public class Group
 extends Container
 implements GroupOrSinglePiece
{
private static final String DBG = "Group";

public static class Dirty
{
   public boolean overlapping = true;
   public boolean pile = true;
   public boolean layout = true; // TODO: NO SERIALIZATION??
}

public final Dirty dirty = new Dirty(); // TODO: NO SERIALIZATION??

private boolean selected; // NO SERIALIZATION
private boolean expanded; // NO SERIALIZATION

Container containerParent; // NO SERIALIZATION
private int indexInContainer; // NO SERIALIZATION

private final int groupNumber; // CONDITIONAL SERIALIZATION
private @Nullable String name; // CONDITIONAL SERIALIZATION

/**
 * The visual position of this group, relative to its Container, or if it's its own Container, to the screen.
 */
public final PointF relativePos = new PointF(); // ALWAYS SERIALIZATION

int largerPieces = 0; // ALWAYS SERIALIZATION
final List<AbstractPiece> pieces = new LinkedList<>(); // ALWAYS SERIALIZATION

public void serializeGroup(Parcel dest, boolean singlesOnly)
{
   if (groupNumber > -1)
      dest.writeInt(groupNumber); // do not serialize groupNumber for temporary containers
   dest.writeInt(name == null ?0 :1);
   if (name != null)
      dest.writeString(name);
   dest.writeFloat(relativePos.x);
   dest.writeFloat(relativePos.y);
   dest.writeInt(largerPieces);
   dest.writeInt(pieces.size());
   for (AbstractPiece piece: pieces) {
      if (singlesOnly)
         ((SinglePiece) piece).serializeSinglePiece(dest);
      else if (piece instanceof SinglePiece) {
         dest.writeInt(0);
         ((SinglePiece) piece).serializeSinglePiece(dest);
      }
      else {
         dest.writeInt(1);
         ((LargerPiece) piece).serializeLargerPiece(dest);
      }
   }
}

public static Group deserializeBoxGroup(Parcel in, Loading loading, int i, HalfJedge[][] pool)
{
   Group ret = new Group(loading, i, in.readInt());
   deserializeGroup(ret, in, true, loading, pool);
   return ret;
}

public static Group deserializeMixedGroup(Parcel in, Loading loading, int i, HalfJedge[][] pool)
{
   Group ret = new Group(loading, i, in.readInt());
   deserializeGroup(ret, in, false, loading, pool);
   return ret;
}

public static Group deserializeTemporaryContainerGroup(Parcel in, int i, HalfJedge[][] pool)
{
   Group ret = getNewTemporaryContainer(i);
   deserializeGroup(ret, in, false, ret, pool);
   return ret;
}

private static void deserializeGroup(Group ret, Parcel in, boolean singlesOnly,
 Container container, HalfJedge[][] pool)
{
   if (in.readInt() == 1) {
      ret.name = in.readString();
   }
   ret.relativePos.x = in.readFloat();
   ret.relativePos.y = in.readFloat();
   ret.largerPieces = in.readInt();
   
   int size = in.readInt();
   for (int indexInGroup = 0; indexInGroup < size; indexInGroup++) {
      if (singlesOnly)
         ret.pieces.add(SinglePiece.deserializeSinglePiece(in, container, indexInGroup, pool));
      else {
         if (in.readInt() == 1)
            ret.pieces.add(LargerPiece.deserializeLargerPiece(in, container, indexInGroup));
         else
            ret.pieces.add(SinglePiece.deserializeSinglePiece(in, container, indexInGroup, pool));
         
         //ret.pieces.add(AbstractPiece.createFromParcelToMixedGroup(in, container, indexInGroup));
      }
   }
}

/**
 * Create a Container for removing pieces from the PlayMat temporarily.
 */
public static Group getNewTemporaryContainer(int tempContainerIndex)
{
   return new Group(tempContainerIndex);
}

/**
 * Construct a temporary container Group.
 * @param tempContainerIndex index in ImagePuzzle.temporaryContainers
 * @see ImagePuzzle#temporaryContainers
 */
private Group(int tempContainerIndex)
{
   groupNumber = -1;
   setContainer(this, tempContainerIndex);
}

/**
 * Construct a Group inside a Container
 * @param container the Container
 * @param indexInContainer index in container
 * @param number the group number
 */
public Group(Container container, int indexInContainer, int number)
{
   groupNumber = number;
   setContainer(container, indexInContainer);
}

public void setContainer(Container newParent, int indexInContainer)
{
   containerParent = newParent;
   this.indexInContainer = indexInContainer;
}

public void replaceLoading(Container loadedContainer)
{
   setContainer(loadedContainer, this.indexInContainer);
   for (AbstractPiece piece: pieces) {
      piece.replaceLoading(loadedContainer);
   }
}

/**
 * Temporary Containers don't have names or numbers.
 * @param ctx a Context
 * @return the optional name
 */
public @NonNull String getName(Context ctx)
{
   if (name == null)
      return /*name =*/ctx.getString(R.string.group_default_name, groupNumber);
   return name;
}

public boolean isNameSet()
{
   return name != null;
}

/**
 * Trims spaces from name before setting it as this Group's name.
 * @param name a name or null
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
   PointF jigBreadth;
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
               jigBreadth = piece.getCurrentJigBreadth();
               widthSum += jigBreadth.x + (AbstractPiece.SIDE_SIZE + minMargin);
               edgeHeightsMax = Math.max(edgeHeightsMax, jigBreadth.y);
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
      jigBreadth = piece.getCurrentJigBreadth();
      widthSum += jigBreadth.x + (AbstractPiece.SIDE_SIZE + minMargin);
      edgeHeightsMax = Math.max(edgeHeightsMax, jigBreadth.y);
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

public void add(AbstractPiece piece)
{
   int size = getPieceCount();
   PointF piecePos = piece.getRelativePos();
   if (piecePos == null)
      dirty.layout = true; // added a piece without position - layout needed.
   else {
      if (size == 0) {
         // copy position from first piece
         this.relativePos.set(piecePos.x, piecePos.y);
         piecePos.set(0f, 0f);
         dirty.layout = false;
      }
      else {
         // make piece position relative to this group
         piecePos.offset(-relativePos.x, -relativePos.y);
      }
   }
   piece.setGroup(this, size);
   pieces.add(piece);
   if (piece instanceof LargerPiece)
      largerPieces++;
}

/**
 * Move a Group of pieces into this Group - only use when this Group is in the same Container!
 * @param other the Group to merge into this Group
 * @return whether or not the other Group was merged into this Group
 */
public boolean add(Group other, Context ctx)
{
   // TODO: merge group into this group, but ask first! (ctx)
//   if (ANSWER_IS_CANCEL) {
//      return false;
//   }
   add(other.pieces, false);
   other.deleteMerged();
   return true;
}

private void add(Collection<AbstractPiece> pieces, boolean isContainer)
{
   // TODO: why is this method needed for isContainer=false? when is it used like that?
   
   // TODO: create methods in PlayMat for creating Group from one or more pieces, and
   //  adding one or more pieces to a Group (also moving its relativePos in the process).
   
   for (AbstractPiece piece: pieces) {
      if (isContainer) {
         // TODO: if this method was used by a new method called movePiecesFrom(), then don't forget to other.remove(piece)!
         piece.setContainer(this, pieces.size());
      }
      add(piece);
   }
}

/**
 * Move a piece here from another Container - only use when this Group is a temporary Container!
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
//      PlayMat playMat = (PlayMat) other;
//
//   }
   return true;
}

/**
 * Move a Group of pieces here from another Container - only use when this Group is a temporary Container!
 * @param other a Container
 * @param group the Group to merge into this Group
 * @param ctx a Context
 * @return whether or not the other Group was merged into this Group
 */
public boolean moveGroupFrom(Container other, Group group, Context ctx)
{
   // TODO: merge group into this group, but ask first! (ctx)
//   if (ANSWER_IS_CANCEL) {
//      return false;
//   }
   other.removeGroup(group);
   add(group.pieces, true);
   // delete the other group:
   group.deleteMerged();
   return true;
}

private void deleteMerged()
{
   pieces.clear();  // merging this Group into another Group!
   containerParent = null;
}

public void remove(AbstractPiece p)
{
   pieces.remove(p.getIndexInContainer());
   if (isExpanded()) {
      // TODO: remove 1 reference from Box.expandedList - maybe have to use Box method ungroupPiece - or
      //  is this method only used when moving a piece from a temporaryContainerGroup to playmat or box?
   }
   
   // all objects with higher index must decrement index:
   for (ListIterator<AbstractPiece> iterator = pieces.listIterator(p.getIndexInContainer()); iterator.hasNext(); ) {
      AbstractPiece next = iterator.next();
      next.decrementIndex();
   }
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

public int getLargerPieceCount()
{
   return largerPieces;
}

public boolean isLonelyPiece()
{
   return pieces.size() == 1;
}

public boolean isEmpty()
{
   return pieces.isEmpty();
}

public int getPieceCount()
{
   return pieces.size();
}

public int getIndexInContainer()
{
   return indexInContainer;
}

public void setIndex(int newIndex)
{
   indexInContainer = newIndex;
}

public void decrementIndex()
{
   indexInContainer--;
}

public void incrementIndex()
{
   indexInContainer++;
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

void setExpanded(boolean expanded)
{
   this.expanded = expanded;
}

//public boolean isPile() // maybe move these to PlayMat.java
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
