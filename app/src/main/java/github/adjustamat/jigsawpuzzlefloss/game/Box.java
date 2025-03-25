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
final LinkedList<SinglePiece> pieces = new LinkedList<>();

@Override
public void add(AbstractPiece p)
{

}

@Override
public void remove(AbstractPiece p)
{

}

@Override
public boolean spreadOutGroup(Group group)
{
   return false;
}

@Override
public boolean pileUpGroup(Group group)
{
   return false;
}

}
