package github.adjustamat.jigsawpuzzlefloss.pieces;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.LinkedList;

import github.adjustamat.jigsawpuzzlefloss.R;
import github.adjustamat.jigsawpuzzlefloss.game.Box;
import github.adjustamat.jigsawpuzzlefloss.game.Box.GroupOrSinglePiece;
import github.adjustamat.jigsawpuzzlefloss.game.Container;
import github.adjustamat.jigsawpuzzlefloss.game.PlayField;
import github.adjustamat.jigsawpuzzlefloss.game.TemporaryStorage;

/**
 * A (named) group of {@link AbstractPiece}s ({@link SinglePiece}s and/or {@link LargerPiece}s).
 * A group can be in the {@link Box}, on the {@link PlayField}, or in {@link TemporaryStorage}.
 * The group can be a pile (with all pieces overlapping), spread out (with each piece visible),
 * or in a custom state (with some pieces overlapping).
 */
public class Group
 implements GroupOrSinglePiece
{
@Nullable String name;
private int counterNumber;
Container containerParent;

private static int counter = 0;

final LinkedList<AbstractPiece> pieces = new LinkedList<>();
//Set<AbstractPiece> separatedPieces;
int largerPieces = 0;

public Group(Container container)
{
   counterNumber = ++counter;
   containerParent = container;
}

public @NonNull String getName(Context ctx)
{
   if (name == null)
      name = ctx.getString(R.string.group_default_name, counterNumber);
   return name;
}

public void setName(@Nullable String name)
{
   this.name = name;
}

public LinkedList<AbstractPiece> getAll()
{
   return pieces;
}

public void setContainer(Container newParent)
{
   containerParent = newParent;
}

public void add(Collection<AbstractPiece> pieces)
{
   //int i = 0;
   for (AbstractPiece p: pieces) {
      add(p);
//      if(p instanceof LargerPiece)
//         largerPieces++;
//      p.setGroup(this, i++);
//      this.pieces.add(p);
   }
}

public void add(AbstractPiece piece)
{
   if (piece instanceof LargerPiece)
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

public int size()
{
   return pieces.size();
}
}
