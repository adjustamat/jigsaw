package github.adjustamat.jigsawpuzzlefloss.game;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.shapes.Shape;

import java.util.LinkedList;
import java.util.Random;

import github.adjustamat.jigsawpuzzlefloss.game.Box.GroupOrSinglePiece;
import github.adjustamat.jigsawpuzzlefloss.pieces.SinglePiece;
import github.adjustamat.jigsawpuzzlefloss.pieces.WholeEdge;
import github.adjustamat.jigsawpuzzlefloss.pieces.WholeEdge.HalfEdge;
import github.adjustamat.jigsawpuzzlefloss.pieces.WholeEdge.RandomEdge;

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
   String areaName;
   Shape includedPieces;
   
} // class Area

public final int width;
public final int height;
public final int totalPieces;

public final Bitmap image;
public final float pieceImageSize;

public final Box singlePiecesContainer;
public final PlayField playingFieldContainer;

private ImagePuzzle(int width, int height, Bitmap image,
 LinkedList<GroupOrSinglePiece> pieceLinkedList
)
{
   this.width = width;
   this.height = height;
   this.totalPieces = width * height;
   
   this.image = image;
   this.pieceImageSize = (float) image.getHeight() / height;
   
   this.singlePiecesContainer = new Box(pieceLinkedList, this);
   this.playingFieldContainer = new PlayField(this);
}

public static ImagePuzzle generate(int pWidth, int pHeight, Bitmap image)
{
   LinkedList<GroupOrSinglePiece> singlePieces = new LinkedList<>();
   ImagePuzzle ret = new ImagePuzzle(pWidth, pHeight, image, singlePieces);
   HalfEdge[][] pool = WholeEdge.generateAllJigsaws();
   Random rng = new Random();
   
   RandomEdge[] wests = new RandomEdge[pHeight];
   for (int x = 0; x < pWidth; x++) {
      RandomEdge north = null;
      for (int y = 0; y < pHeight; y++) {
         RandomEdge east, south;
         
         if (x == pWidth - 1)
            east = null;
         else
            east = new RandomEdge(rng);
         
         if (y == pHeight - 1)
            south = null;
         else
            south = new RandomEdge(rng);
         
         singlePieces.add(new SinglePiece(ret, new Point(x, y),
          north, east, south, wests[y], pool, rng.nextInt(4)));
         
         //if (south != null)
         north = south;
         //if (east != null)
         wests[y] = east;
      } // for(y)
   } // for(x)
   return ret;
}
}
