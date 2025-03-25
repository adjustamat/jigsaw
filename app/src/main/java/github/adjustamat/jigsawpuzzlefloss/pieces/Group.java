package github.adjustamat.jigsawpuzzlefloss.pieces;

import java.util.Collection;
import java.util.LinkedList;
import github.adjustamat.jigsawpuzzlefloss.game.Box;
import github.adjustamat.jigsawpuzzlefloss.game.PlayField;
import github.adjustamat.jigsawpuzzlefloss.game.TemporaryStorage;

/**
 * A (named) group of {@link SinglePiece}s and/or {@link LargerPiece}s.
 * A group can be in the {@link Box}, on the {@link PlayField}, or in {@link TemporaryStorage}.
 * The group can be spread out (with each piece visible), a pile (with all pieces overlapping),
 * or in a custom state (with some pieces overlapping).
 */
public class Group
{
String name;

final LinkedList<AbstractPiece> pieces = new LinkedList<>();
//Set<AbstractPiece> separated;
int largerPieces = 0;

public Group()
{
}

public Group(Collection<AbstractPiece> pieces)
{
   int i = 0;
   for(AbstractPiece p : pieces) {
      if(p instanceof LargerPiece)
         largerPieces++;
      p.setGroup(this, i++);
      this.pieces.add(p);
   }
}

public LinkedList<AbstractPiece> getAll(){
   return pieces;
}
//
//@Override
//public void setContainer(Container newParent)
//{
//
//}

public void add(AbstractPiece piece)
{
   if(piece instanceof LargerPiece)
      largerPieces++;
   piece.setGroup(this, this.pieces.size());
   this.pieces.add(piece);
}

void removeMe(int index)
{
   pieces.remove(index);
}

public boolean isPile()
{
   // TODO!
   return true;
}

public boolean isOverlapping()
{
   // TODO!
   return true;
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

}
