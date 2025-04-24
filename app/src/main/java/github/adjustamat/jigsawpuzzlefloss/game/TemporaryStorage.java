package github.adjustamat.jigsawpuzzlefloss.game;

import androidx.annotation.NonNull;
import github.adjustamat.jigsawpuzzlefloss.pieces.AbstractPieces;
import github.adjustamat.jigsawpuzzlefloss.pieces.Group;
import github.adjustamat.jigsawpuzzlefloss.pieces.LargerPiece;

/**
 * A place to hide a {@link LargerPiece} or a {@link Group} of pieces out of the way.
 */
public class TemporaryStorage
 extends Container
{
@NonNull Group group;

public TemporaryStorage createEmptyGroup(){
   return new TemporaryStorage();
}

public TemporaryStorage createLargerPieceStorage(LargerPiece piece){
TemporaryStorage ret=new TemporaryStorage();
ret.moveHere(piece);
}

private TemporaryStorage(){
   group=new Group(this);
}

//public TemporaryStorage(AbstractPieces piece, Container from){
//   group = piece instanceof Group;
//   add(piece,from);
//}

//private void add(AbstractPieces piece, Container from){
//
//}
//
//@Override
//public boolean moveHere(AbstractPieces piece, Container from)
//{
//   if(from == this)
//      return false;
//   //if()
//     add(piece,from);
//     return true;
//}

@Override
public boolean spreadOutGroup(Group group)
{
   // TODO!
   return true;
}

@Override
public boolean pileUpGroup(Group group)
{
   // TODO!
   return true;
}
}
