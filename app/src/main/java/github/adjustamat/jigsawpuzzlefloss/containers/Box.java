package github.adjustamat.jigsawpuzzlefloss.containers;

import android.content.Context;

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

public void setExpanded(Group group, boolean expand)
{
   if (group.isExpanded() == expand)
      return;
   group.setExpanded(expand);
   int index = group.getIndexInContainer();
   int size = group.getPieceCount();
   if (expand)
      expanded.put(index, group);
   else
      expanded.remove(index);
   for (int i = size - 1; i > 0; i--) {
      if (expand)
         expandedList.add(index, group);
      else
         expandedList.remove(index);
   }
}

public Group createBoxGroup(List<SinglePiece> selectedPieces, int atBoxIndex)
{
   Group group = new Group(this, atBoxIndex, imagePuzzle.getNewGroupNumber());
   
   //list.removeAll(selectedPieces);
   for (SinglePiece piece: selectedPieces) {
      int boxIndex = piece.getIndexInContainer();
      if (boxIndex < atBoxIndex)
         atBoxIndex--;
      list.remove(boxIndex);
   }
   
   int atExpandedIndex = atBoxIndex;
   for (Entry<Integer, Group> expandedGroup: expanded.entrySet()) {
      if (atBoxIndex > expandedGroup.getKey())
         atExpandedIndex += expandedGroup.getValue().getPieceCount();
   }
   list.add(atBoxIndex, group);
   expandedList.add(atExpandedIndex, group);
   
   return group;
}

// TODO: create ungroupGroup method in PlayMat also.
public void ungroupBoxGroup(Group group)
{
   // TODO: if group.expanded, optimize: replace items in expandedList instead of removing and then adding
}


// TODO: should this method take an indexInGroup or a SinglePiece?
public void ungroupPiece(Group group, int indexInGroup)
{
   if (group.getPieceCount() == 1) {
      ungroupBoxGroup(group);
      return;
   }
   
   SinglePiece piece = (SinglePiece) group.getSinglePieces().get(indexInGroup);
   
   // TODO: THIS PROBABLY WON'T WORK: maybe remove group methods from AbstractPiece!
   //  see also Group.add(AbstractPiece) (which uses AbstractPiece.setGroup())
   piece.removeFromGroup(this,list.size());
   
   // movePieceFrom(this,piece); NO! do everything manually in this method!
   
   group.removeFromContainer(piece); // make sure group.remove() works even when container is Box - it's a Container method!
   // TODO!
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
         fromExpandedIndex += expandedGroup.getValue().getPieceCount();
      if (toIndex > expandedGroup.getKey())
         toExpandedIndex += expandedGroup.getValue().getPieceCount();
   }
   // TODO: what is fromexpanded and toexpanded??? what to do?
   
}

public void removeFromContainer(AbstractPiece p)
{
   list.remove(p.getIndexInContainer());
   expandedList.remove(p.getIndexInContainer());
   // TODO: all objects with higher index must index--! - see reorder() and PlayMat.removeGroup(Group group)
}

public void removeGroup(Group group)
{
   // this method is used when moving the Group of pieces to another container.
   list.remove(group.getIndexInContainer());
   if (group.isExpanded())
      setExpanded(group, false);
   expandedList.remove(group.getIndexInContainer());
   // TODO: all objects with higher index must index--! - see reorder() and PlayMat.removeGroup(Group group)
}

public boolean movePieceFrom(Container other, AbstractPiece p)
{
   // TODO: method for adding piece/group atIndex: just use this method and then reorder. - see reorder()
   
   
   // only a SinglePiece can be put back into the box.
   if (p instanceof LargerPiece)
      return false;
   other.removeFromContainer(p);
   p.setContainer(this, list.size());
   list.add((SinglePiece) p);
   
   // TODO: expanded list - does this work?
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
   
   // TODO: expanded list - does this work?
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
}
}
