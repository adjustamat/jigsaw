package github.adjustamat.jigsawpuzzlefloss.game;

import android.content.Context;

import github.adjustamat.jigsawpuzzlefloss.pieces.AbstractPiece;
import github.adjustamat.jigsawpuzzlefloss.pieces.Group;

/**
 * An abstract container for pieces and groups of pieces.
 */
public abstract class Container
{

protected final ImagePuzzle parent;

protected Container(ImagePuzzle parent)
{
   this.parent = parent;
}

public ImagePuzzle getImagePuzzle()
{
   return parent;
}

//public abstract void add(AbstractPiece p);

public abstract void remove(AbstractPiece p);
public abstract void removeGroup(Group group);

public abstract boolean movePieceFrom(Container other, AbstractPiece p);

public abstract boolean moveGroupFrom(Context ctx, Container other, Group group);

/**
 * Move <code>pieces</code> to another Container, if possible.
 * @param pieces the AbstractPieces to move from this Container, if possible.
 * @return whether or not <code>pieces</code> is now in <code>to</code>.
 */
//public boolean movePieces(Group pieces, Container to)
//{
//   // TODO: this method isn't effective. supply an index to Box.remove() instead.
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
