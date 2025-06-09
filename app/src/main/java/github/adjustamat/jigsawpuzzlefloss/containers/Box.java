package github.adjustamat.jigsawpuzzlefloss.containers;

import android.content.Context;

import java.util.LinkedList;
import java.util.List;

import github.adjustamat.jigsawpuzzlefloss.game.ImagePuzzle;
import github.adjustamat.jigsawpuzzlefloss.pieces.AbstractPiece;
import github.adjustamat.jigsawpuzzlefloss.pieces.LargerPiece;
import github.adjustamat.jigsawpuzzlefloss.pieces.SinglePiece;

/**
 * The {@link Container} from which the {@link SinglePiece}s are moved onto the {@link PlayField}.
 */
public class Box
 extends Container
{
final List<GroupOrSinglePiece> list;
public final List<GroupOrSinglePiece> expandedList;

public Box(List<GroupOrSinglePiece> pieces, ImagePuzzle parent)
{
   // super(parent);
   this.list = pieces;
   this.expandedList = new LinkedList<>();
   expandedList.addAll(list);
}

void setExpanded(Group group, boolean expand)
{
   int index = group.getIndexInContainer();
   int size = group.size();
   if (size > 1) {
      for (int i = size - 1; i > 0; i--) {
         if (expand)
            expandedList.add(index, group);
         else
            expandedList.remove(index);
      }
   }
}

public void remove(AbstractPiece p)
{
   list.remove(p.getIndexInContainer());
   
   // TODO:  check that all uses add the piece to another container and that container's list.

// /*
// * Move a Group of pieces to another Container, if possible.
// * @param pieces the AbstractPieces to move from this Container, if possible.
// * @return whether or not <code>pieces</code> is now in <code>to</code>.
// */
//public boolean movePieces(Group pieces, Container to)
//{
//   if (to == this)
//      return true;
//
//   if (to instanceof Box) {
//         if(pieces instanceof Group) {
//      Group group = (Group) pieces;
//      if (group.hasLargerPieces())
//         return false;
//      LinkedList<AbstractPiece> all = group.getAll();
//      for (AbstractPiece piece: all) {
//      }
//      return true;
//         }
//         else if (pieces instanceof LargerPiece)
//            return false;
//         else { // pieces instanceof SinglePiece
//            SinglePiece singlePiece = (SinglePiece) pieces;
//            if(singlePiece.isGrouped())
//               return false;
//            to.add(singlePiece);
//            return true;
//         }
//   }
//   else if (to instanceof TemporaryStorage) {
//   }
//   else { // to instanceof PlayField
//      //   if(containerParent instanceof Box){
//   }else if(containerParent instanceof TemporaryStorage){
//
//   }else{
//
//   }
//   }
//
//}
}

public void removeGroup(Group group)
{
   list.remove(group.getIndexInContainer());
}

public boolean movePieceFrom(Container other, AbstractPiece p)
{
   // only a SinglePiece can be put back into the box.
   if (p instanceof LargerPiece)
      return false;
   other.remove(p);
   p.setContainer(this, list.size());
   list.add((SinglePiece) p);
//   if (other instanceof Group) {
//      Group temporaryStorage = (Group) other;
//
//   }
//   else {
//      PlayField playField = (PlayField) other;
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
//   if (other instanceof TemporaryStorage) {
//      TemporaryStorage storage = (TemporaryStorage) other;
//
//   }
//   else {
//      PlayField playField = (PlayField) other;
//
//   }
   return true;
}

//public int size(){ return expandedList.size(); }
//public GroupOrSinglePiece get(int index){ return expandedList.get(index); }

public interface GroupOrSinglePiece
{
   boolean isSelected();
   void setSelected(boolean b);
}
}
