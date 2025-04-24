package github.adjustamat.jigsawpuzzlefloss.game;

import java.util.LinkedList;

import github.adjustamat.jigsawpuzzlefloss.pieces.Group;
import github.adjustamat.jigsawpuzzlefloss.pieces.AbstractPiece;
import github.adjustamat.jigsawpuzzlefloss.pieces.SinglePiece;

/**
 * The {@link Container} from which the {@link SinglePiece}s are moved onto the {@link PlayField}.
 */
public class Box
 extends Container
{
final LinkedList<SinglePiece> pieces;

final ImagePuzzle parent;

public Box(LinkedList<SinglePiece> pieces, ImagePuzzle parent)
{
   this.pieces = pieces;
   this.parent = parent;
}

public void add(AbstractPiece p)
{
// only a SinglePiece can be put back into the box.
}

public void remove(AbstractPiece p)
{
   // TODO: this method isn't effective. supply an index instead. see Container.movePieces()!
}

public boolean spreadOutGroup(Group group)
{
   return false;
}

public boolean pileUpGroup(Group group)
{
   return false;
}

}
