package github.adjustamat.jigsawpuzzlefloss.containers;

import android.content.Context;

import github.adjustamat.jigsawpuzzlefloss.pieces.AbstractPiece;

/**
 * An abstract container for pieces and groups of pieces.
 */
public abstract class Container
{
//protected final ImagePuzzle parent;  // Container needs ImagePuzzle?

//protected Container(ImagePuzzle parent)
//{
//   this.parent = parent;
//}

//public ImagePuzzle getImagePuzzle()
//{
//   return parent;
//}

public abstract boolean movePieceFrom(Container other, AbstractPiece p);

public abstract boolean moveGroupFrom(Container other, Group group, Context ctx);

public abstract void remove(AbstractPiece p);

public abstract void removeGroup(Group group);

}
