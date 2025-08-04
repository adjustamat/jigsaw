package github.adjustamat.jigsawpuzzlefloss.containers;

import android.content.Context;

import github.adjustamat.jigsawpuzzlefloss.pieces.AbstractPiece;

/**
 * An abstract container for pieces and groups of pieces.
 */
public abstract class Container
{
public abstract boolean movePieceFrom(Container other, AbstractPiece p);

public abstract boolean moveGroupFrom(Container other, Group group, Context ctx);

/**
 * The piece has to still have the index that it had in this container!
 * @param p a piece to remove from this container
 */
public abstract void removeFromContainer(AbstractPiece p);

public abstract void removeGroup(Group group);

public static class Loading
 extends Container
{
   public boolean movePieceFrom(Container other, AbstractPiece p)
   {
      return false;
   }
   
   public boolean moveGroupFrom(Container other, Group group, Context ctx)
   {
      return false;
   }
   
   public void removeFromContainer(AbstractPiece p)
   {
   }
   
   public void removeGroup(Group group)
   {
   }
}
}
