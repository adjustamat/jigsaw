package github.adjustamat.jigsawpuzzlefloss.game;

import android.content.Context;

import java.util.LinkedList;

import github.adjustamat.jigsawpuzzlefloss.pieces.AbstractPiece;
import github.adjustamat.jigsawpuzzlefloss.pieces.Group;
import github.adjustamat.jigsawpuzzlefloss.pieces.LargerPiece;
import github.adjustamat.jigsawpuzzlefloss.pieces.SinglePiece;

/**
 * The {@link Container} from which the {@link SinglePiece}s are moved onto the {@link PlayField}.
 */
public class Box
 extends Container
{
final LinkedList<GroupOrSinglePiece> pieces;

public Box(LinkedList<GroupOrSinglePiece> pieces, ImagePuzzle parent)
{
   super(parent);
   this.pieces = pieces;
}

public void remove(AbstractPiece p)
{
   pieces.remove(p.getIndexInContainer());
   // TODO: see Container.java!
}

public void removeGroup(Group group)
{

}

public boolean movePieceFrom(Container other, AbstractPiece p)
{
   // only a SinglePiece can be put back into the box.
   if (p instanceof LargerPiece)
      return false;
   other.remove(p);
   p.setContainer(this, pieces.size());
   pieces.add((SinglePiece) p);
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

public boolean moveGroupFrom(Context ctx, Container other, Group group)
{
   // only a Group of SinglePieces can be put back into the box. They lose their position information.
   if (group.hasLargerPieces())
      return false;
   other.removeGroup(group);
   group.setContainer(this);
   pieces.add(group);
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

public interface GroupOrSinglePiece { }
}
