package github.adjustamat.jigsawpuzzlefloss.game;

import android.graphics.drawable.shapes.Shape;
import github.adjustamat.jigsawpuzzlefloss.pieces.SinglePiece;

/**
 * An instance of an image turned into many jigsaw puzzle pieces ({@link SinglePiece}s).
 * This class contains the generation algorithm for {@link ImagePuzzle}s.
 */
public class ImagePuzzle
{

/**
 * A (named) area of an {@link ImagePuzzle}.
 * Every {@link SinglePiece} belongs to exactly one {@link Area}.
 */
public static class Area
{
String name;
Shape includedPieces;

}
}
