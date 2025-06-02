package github.adjustamat.jigsawpuzzlefloss.pieces;

import android.content.Context;
import android.graphics.PointF;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.LinkedList;

import github.adjustamat.jigsawpuzzlefloss.R;
import github.adjustamat.jigsawpuzzlefloss.containers.Box;
import github.adjustamat.jigsawpuzzlefloss.containers.Box.GroupOrSinglePiece;
import github.adjustamat.jigsawpuzzlefloss.containers.Container;
import github.adjustamat.jigsawpuzzlefloss.containers.PlayField;

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

/**
 * The visual position of this group, relative to its Container, or if it's its own Container, to the screen.
 */
public PointF relativePos;

private boolean selected;

private @Nullable String name;
private final int myNumber;
Container containerParent;
private int indexInContainer;

final LinkedList<AbstractPiece> pieces = new LinkedList<>();
//Set<AbstractPiece> separatedPieces;
int largerPieces = 0;

public Group(Container container, int indexInContainer)
{
   //super(null); // TODO: Container needs ImagePuzzle?
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
   //super(null); // TODO: Container needs ImagePuzzle?
   myNumber = 0;
   setContainer(this, newIndex);
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

public LinkedList<AbstractPiece> getAllPieces()
{
   return pieces;
}

public void setContainer(Container newParent, int indexInContainer)
{
   if (newParent == null) { // merging this Group into another Group!
      pieces.clear();
   }
   containerParent = newParent;
   this.indexInContainer = indexInContainer;
}

public void add(Collection<AbstractPiece> pieces)
{
   //int i = 0;
   for (AbstractPiece p: pieces) {
      add(p);
//      if(p instanceof LargerPiece)
//         largerPieces++;
//      p.setGroup(this, i++);
//      this.pieces.add(p);
   }
}

public void add(AbstractPiece piece)
{
   if (piece instanceof LargerPiece)
      largerPieces++;
   piece.setGroup(this, pieces.size());
   pieces.add(piece);
}

/**
 * Move a piece here from another Container - only use when this Group is a temporary storage!
 * @param other a Container
 * @param p the piece
 * @return true
 */
public boolean movePieceFrom(Container other, AbstractPiece p)
{
   other.remove(p);
   int indexInContainerAndGroup = pieces.size();
   p.setContainer(this, indexInContainerAndGroup);
   p.setGroup(this, indexInContainerAndGroup);
   pieces.add(p);
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
   // TODO: combine groups, but ask first!
//   if (ANSWER_IS_CANCEL) {
//      return false;
//   }
   other.removeGroup(group);
   
   add(group.getAllPieces());
   // delete the group:
   group.setContainer(null, -1);
   return true;
}

public void remove(AbstractPiece p)
{
   pieces.remove(p.getIndexInGroup()); // uses index in group, not index in container!
}

/**
 * Do nothing.
 * @param group == this (should be)
 */
public void removeGroup(Group group)
{
   if (group != this) {
      throw new UnsupportedOperationException("No Groups inside Group!");
   }
   Log.i(DBG, "removing Group from itself");
}

//void removeAbstractPiece(int index)
//{
//   pieces.remove(index);
//}

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

//public boolean isPile() // TODO: maybe move these to PlayField.java
//{
//   // TOD
//   return false;
//}
//public boolean isOverlapping()
//{
//   // TOD
//   return true;
//}
}
