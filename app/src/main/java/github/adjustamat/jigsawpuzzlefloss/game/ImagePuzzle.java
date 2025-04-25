package github.adjustamat.jigsawpuzzlefloss.game;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.shapes.Shape;

import java.util.LinkedList;
import java.util.Random;

import github.adjustamat.jigsawpuzzlefloss.pieces.AbstractPiece.EdgeType;
import github.adjustamat.jigsawpuzzlefloss.pieces.SVGPath;
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

private static class RandomEdge
{
   EdgeType type;
   final int curv1;
   final int curv2;
   final int neck1;
   final int neck2;
   
   public RandomEdge(Random rng)
   {
      type = rng.nextBoolean() ?EdgeType.OUT :EdgeType.IN;
      curv1 = rng.nextInt(3);
      curv2 = rng.nextInt(3);
      neck1 = rng.nextInt(3);
      neck2 = rng.nextInt(3);
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
   SVGPath.HalfEdge[][] pool = SVGPath.generateAllJigsaws();
   SVGPath.WholeEdge[] northEastSouthWest = new SVGPath.WholeEdge[4];
   // TODO: pool [NECK*6 + CURV*2 + (secondHalf?1:0)] [(inward?4:0) + direction_ordinal]
   
   RandomEdge[] wests = new RandomEdge[pHeight];
   for (int x = 0; x < pWidth; x++) {
      RandomEdge north = null;
      for (int y = 0; y < pHeight; y++) {
         RandomEdge east, south;
         
         if (y == pHeight - 1) {
            south = null;
         }
         else {
            south = new RandomEdge(rng);
         }
         
         if (x == pWidth - 1) {
            east = null;
         }
         else {
            east = new RandomEdge(rng);
         }
         
         // TODO: edgeWidth depends on curv1, curv2 and IN/OUT. see SVGPath.java
         //RectF edgeWidths = new RectF(0f, 0f, 0f, 0f);
         
         singlePieces.add(new SinglePiece(ret.box, new Point(x, y),
          north == null ?EdgeType.EDGE :north.type,
          east == null ?EdgeType.EDGE :east.type,
          south == null ?EdgeType.EDGE :south.type,
          wests[y] == null ?EdgeType.EDGE :wests[y].type,
          northEastSouthWest, ret.pieceImageSize));
         
         if (south != null) {
            north = south;
            north.type = north.type == EdgeType.IN ?EdgeType.OUT :EdgeType.IN;
         }
         if (east != null) {
            wests[y] = east;
            wests[y].type = wests[y].type == EdgeType.IN ?EdgeType.OUT :EdgeType.IN;
         }
      }
   }
   
   for (int i = 0; i < verticalEdges; i++) {
      boolean edgeTypeIn = rng.nextBoolean();
      int curv1 = rng.nextInt(3);
      int curv2 = rng.nextInt(3);
      int neck1 = rng.nextInt(3);
      int neck2 = rng.nextInt(3);
      
   }
   
   for (int i = 0; i < horizontalEdges; i++) {
      boolean edgeTypeIn = rng.nextBoolean();
      int curv1 = rng.nextInt(3);
      int curv2 = rng.nextInt(3);
      int neck1 = rng.nextInt(3);
      int neck2 = rng.nextInt(3);
      
      
   }
   
   
   
   
   
   return ret;
}
}
