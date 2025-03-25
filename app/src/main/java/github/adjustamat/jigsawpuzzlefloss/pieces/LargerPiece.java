package github.adjustamat.jigsawpuzzlefloss.pieces;

import java.util.ArrayList;
import android.graphics.drawable.shapes.Shape;
import github.adjustamat.jigsawpuzzlefloss.game.Container;

/**
 * Two or more {@link SinglePiece}s that fit together.
 */
public class LargerPiece
 extends AbstractPiece
{
ArrayList<SinglePiece> matrix;
int pieceCount;

Shape combinedImageMask;

public LargerPiece(AbstractPiece p1, AbstractPiece p2, Container parent)
{
   super(parent);
}


}
