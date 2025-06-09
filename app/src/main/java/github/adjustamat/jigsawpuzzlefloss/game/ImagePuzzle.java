package github.adjustamat.jigsawpuzzlefloss.game;

import android.graphics.Bitmap;
import android.graphics.Point;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import github.adjustamat.jigsawpuzzlefloss.containers.Box;
import github.adjustamat.jigsawpuzzlefloss.containers.Box.GroupOrSinglePiece;
import github.adjustamat.jigsawpuzzlefloss.containers.PlayField;
import github.adjustamat.jigsawpuzzlefloss.pieces.PieceEdge;
import github.adjustamat.jigsawpuzzlefloss.pieces.PieceEdge.HalfEdge;
import github.adjustamat.jigsawpuzzlefloss.pieces.PieceEdge.RandomEdge;
import github.adjustamat.jigsawpuzzlefloss.pieces.SinglePiece;

/**
 * An instance of an image turned into many jigsaw puzzle pieces ({@link SinglePiece}s).
 * This class contains the generation algorithm for {@link ImagePuzzle}s.
 */
public class ImagePuzzle
{

public final int width;
public final int height;
public final int totalPieces;

public final Bitmap image;
public final float pieceImageSize;

public final Box singlePiecesContainer;
public final PlayField playingFieldContainer;

private ImagePuzzle(int width, int height, Bitmap image, List<GroupOrSinglePiece> pieceList)
{
   this.width = width;
   this.height = height;
   this.totalPieces = width * height;
   
   this.image = image;
   this.pieceImageSize = (float) image.getHeight() / height;
   
   this.singlePiecesContainer = new Box(pieceList, this);
   this.playingFieldContainer = new PlayField();
}

/**
 * Generate a random jigsaw pattern for the given image and size of the ImagePuzzle.
 * @param pWidth the width, in number of pieces
 * @param pHeight the height, in number of pieces
 * @param image the image
 * @param rng a random number generator
 * @return a new ImagePuzzle with the given size and image
 */
public static ImagePuzzle generateNewPuzzle(int pWidth, int pHeight, Bitmap image, Random rng)
{
   LinkedList<GroupOrSinglePiece> singlePieces = new LinkedList<>();
   ImagePuzzle ret = new ImagePuzzle(pWidth, pHeight, image, singlePieces);
   HalfEdge[][] pool = PieceEdge.generateAllJigsawEdges();
   
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
         
         singlePieces.add(new SinglePiece(
          ret,
          singlePieces.size(),
          new Point(x, y),
          north, east, south, wests[y],
          pool,
          rng.nextInt(4)
         ));
         
         //if (south != null)
         north = south;
         //if (east != null)
         wests[y] = east;
      } // for(y)
   } // for(x)
   return ret;
}

/**
 * A (named) area of an {@link ImagePuzzle}.
 * Every {@link SinglePiece} belongs to exactly one {@link Area}.
 */
public static class Area
{
   public String areaName;
   //   Shape includedPieces; // import android.graphics.drawable.shapes.Shape;
//   boolean[][] included;
   final DividingLine[] neswDividers;// = new DividingLine[4];
   
   public Area(DividingLine n, DividingLine e, DividingLine s, DividingLine w)
   {
      neswDividers = new DividingLine[]{n, e, s, w};
   }
   
   public void setDivider(int direction, DividingLine divider)
   {
      neswDividers[direction] = divider;
   }
} // class Area

public static class DividingLine
{
   Point point1, point2;
}
}
