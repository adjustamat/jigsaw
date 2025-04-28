package github.adjustamat.jigsawpuzzlefloss.game;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.shapes.Shape;

import java.util.LinkedList;
import java.util.Random;

import github.adjustamat.jigsawpuzzlefloss.pieces.Direction;
import github.adjustamat.jigsawpuzzlefloss.pieces.SVGEdges;
import github.adjustamat.jigsawpuzzlefloss.pieces.SVGEdges.DoubleEdge;
import github.adjustamat.jigsawpuzzlefloss.pieces.SVGEdges.HalfEdge;
import github.adjustamat.jigsawpuzzlefloss.pieces.SVGEdges.WholeEdge;
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
   String areaName;
   Shape includedPieces;
   
} // class Area

public static class RandomEdge
{
   public final boolean in;
   public final int curv1;
   public final int curv2;
   public final int neck1;
   public final int neck2;
   
   RandomEdge(Random rng)
   {
      in = rng.nextBoolean();// ?EdgeType.OUT :EdgeType.IN;
      curv1 = rng.nextInt(3);
      curv2 = rng.nextInt(3);
      neck1 = rng.nextInt(3);
      neck2 = rng.nextInt(3);
   }
   
   public DoubleEdge getWholeEdge(HalfEdge[][] pool, Direction dir)
   {
      return dir.getDoubleEdge(pool, in, curv1, curv2, neck1, neck2);
   }
} // class RandomEdge

public final int width;
public final int height;
public final int totalPieces;
public final Bitmap image;
public final float pieceImageSize;
public final Box box;

public final PlayField playingField;

private ImagePuzzle(int width, int height, int totalPieces, Bitmap image, float pieceImageSize,
 LinkedList<SinglePiece> pieceLinkedList)
{
   this.width = width;
   this.height = height;
   this.totalPieces = totalPieces;
   this.image = image;
   this.pieceImageSize = pieceImageSize;
   this.box = new Box(pieceLinkedList, this);
   this.playingField = new PlayField();
}

public static ImagePuzzle generate(int pWidth, int pHeight, Bitmap image)
{
   LinkedList<SinglePiece> singlePieces = new LinkedList<>();
   
   int verticalEdges = pHeight * (pWidth - 1);
   int horizontalEdges = (pHeight - 1) * pWidth;
   // int totalEdges = verticalEdges + horizontalEdges;
   // boolean[] edgeTypeIn = new boolean[totalEdges];
   
   ImagePuzzle ret = new ImagePuzzle(pWidth, pHeight, pHeight * pWidth,
    image, (float) image.getHeight() / pHeight,
    singlePieces);
   
   Random rng = new Random();
   HalfEdge[][] pool = SVGEdges.generateAllJigsaws();
   WholeEdge[] northEastSouthWest = new WholeEdge[4];
   
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
          ret, new Point(x, y), north, east, south, wests[y]
         ));
         
         //if (south != null)
         north = south;
         //if (east != null)
         wests[y] = east;
      } // for(y)
   } // for(x)
   return ret;
}
}
