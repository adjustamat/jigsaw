package github.adjustamat.jigsawpuzzlefloss.game;

import android.content.Context;

import androidx.annotation.NonNull;

import github.adjustamat.jigsawpuzzlefloss.pieces.AbstractPiece;
import github.adjustamat.jigsawpuzzlefloss.pieces.Group;
import github.adjustamat.jigsawpuzzlefloss.pieces.LargerPiece;

/**
 * A place to hide a {@link LargerPiece} or a {@link Group} of pieces out of the way.
 */
public class TemporaryStorage
 extends Container
{
@NonNull Group group;

TemporaryStorage(ImagePuzzle imagePuzzle)
{
   super(imagePuzzle);
   group = new Group(this);
}

TemporaryStorage(ImagePuzzle imagePuzzle, @NonNull Group group)
{
   super(imagePuzzle);
   this.group = group;
   group.setContainer(this);
}

public void remove(AbstractPiece p)
{

}

public void removeGroup(Group group)
{

}

public boolean movePieceFrom(Container other, AbstractPiece p)
{
   other.remove(p);
   
   p.setContainer(this, group.size());
   group.add(p);
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

public boolean moveGroupFrom(Context ctx, Container other, Group group)
{
   // TODO: combine groups, but ask first!
//   if (ANSWER_IS_CANCEL) {
//      return false;
//   }
   other.removeGroup(group);
   //group.setContainer(this);
   this.group.add(group.getAll());
   

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

}
