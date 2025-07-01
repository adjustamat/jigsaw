package github.adjustamat.jigsawpuzzlefloss.containers;

import android.content.Context;
import android.os.Parcel;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;

import github.adjustamat.jigsawpuzzlefloss.game.ImagePuzzle;
import github.adjustamat.jigsawpuzzlefloss.pieces.AbstractPiece;
import github.adjustamat.jigsawpuzzlefloss.pieces.LargerPiece;
import github.adjustamat.jigsawpuzzlefloss.pieces.SinglePiece;

/**
 * The {@link Container} from which the {@link SinglePiece}s are moved onto the {@link PlayMat}.
 */
public class Box
 extends Container
{
public final List<GroupOrSinglePiece> list;
public final List<GroupOrSinglePiece> expandedList;
private final HashMap<Integer, Group> expanded = new HashMap<>();
public final ImagePuzzle imagePuzzle;

public Box(List<GroupOrSinglePiece> pieces, ImagePuzzle parent)
{
   // super(parent);
   this.imagePuzzle = parent;
   this.list = pieces;
   this.expandedList = new LinkedList<>();
   expandedList.addAll(list);
}

void setExpanded(Group group, boolean expand)
{
   int index = group.getIndexInContainer();
   int size = group.size();
   if (expand)
      expanded.put(index, group);
   else
      expanded.remove(index);
   if (size > 1) {
      for (int i = size - 1; i > 0; i--) {
         if (expand)
            expandedList.add(index, group);
         else
            expandedList.remove(index);
      }
   }
}

public Group createGroup(List<SinglePiece> selectedPieces, int atIndex)
{
   Group group = new Group(this, atIndex);
   
   
   int atExpandedIndex = atIndex;
   for (Entry<Integer, Group> expandedGroup: expanded.entrySet()) {
      if (atIndex > expandedGroup.getKey())
         atExpandedIndex += expandedGroup.getValue().size();
   }
   list.add(atIndex, group);
   expandedList.add(atExpandedIndex, group);
   
   return group;
}

public void ungroupGroup(Group group)
{

}

public void ungroupPiece(Group group, int indexInGroup)
{

}

public void reorder(int fromIndex, int toIndex)
{
   GroupOrSinglePiece or = list.remove(fromIndex);
   list.add(toIndex, or);
   or.setIndex(toIndex);
   
   if (fromIndex < toIndex) {
      for (ListIterator<GroupOrSinglePiece> i = list.listIterator(fromIndex);
           i.nextIndex() < toIndex; ) {
         i.next().decrementIndex();
      }
   }
   else {
      for (ListIterator<GroupOrSinglePiece> i = list.listIterator(toIndex + 1);
           i.nextIndex() <= fromIndex; ) {
         i.next().incrementIndex();
      }
   }
   
   int fromExpandedIndex = fromIndex;
   int toExpandedIndex = toIndex;
   for (Entry<Integer, Group> expandedGroup: expanded.entrySet()) {
      if (fromIndex > expandedGroup.getKey())
         fromExpandedIndex += expandedGroup.getValue().size();
      if (toIndex > expandedGroup.getKey())
         toExpandedIndex += expandedGroup.getValue().size();
   }
   
}

public void remove(AbstractPiece p)
{
   list.remove(p.getIndexInContainer());
   expandedList.remove(p.getIndexInContainer());
   // TODO: all objects with higher index must index--!
}

public void removeGroup(Group group)
{
   // this method is used when moving to another container.
   list.remove(group.getIndexInContainer());
   if (group.isExpanded())
      setExpanded(group, false);
   expandedList.remove(group.getIndexInContainer());
   // TODO: all objects with higher index must index--!
}

public boolean movePieceFrom(Container other, AbstractPiece p)
{
   // TODO: method for adding piece/group atIndex: just use this method and then reorder.
   // only a SinglePiece can be put back into the box.
   if (p instanceof LargerPiece)
      return false;
   other.remove(p);
   p.setContainer(this, list.size());
   list.add((SinglePiece) p);
   expandedList.add((SinglePiece) p);
//   if (other instanceof Group) {
//      Group temporaryContainer = (Group) other;
//
//   }
//   else {
//      PlayMat playMat = (PlayMat) other;
//
//   }
   return true;
}

public boolean moveGroupFrom(Container other, Group group, Context ctx)
{
   // only a Group of SinglePieces can be put into the box. They may lose their position information.
   if (group.hasLargerPieces())
      return false;
   other.removeGroup(group);
   group.setContainer(this, list.size());
   list.add(group);
   expandedList.add(group);
//   if (other instanceof Group) {
//      Group temporaryContainer = (Group) other;
//
//   }
//   else {
//      PlayMat playMat = (PlayMat) other;
//
//   }
   return true;
}

public interface GroupOrSinglePiece
{
   boolean isSelected();
   void setSelected(boolean b);
   void setIndex(int newIndex);
   void decrementIndex();
   void incrementIndex();
   void replaceLoading(Container loadedContainer);
   void writeToParcel(Parcel dest, int flags);

}
}
