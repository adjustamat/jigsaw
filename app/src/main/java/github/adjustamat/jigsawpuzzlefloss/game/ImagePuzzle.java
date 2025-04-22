package github.adjustamat.jigsawpuzzlefloss.game;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.shapes.PathShape;

import java.util.ArrayList;
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
   PathShape includedPieces;
   
} // class Area

private ImagePuzzle() {}

private static class RandomEdge
{
   EdgeType type;
   final int curv1;
   final int curv2;
   final int neck1;
   final int neck2;
   
   public RandomEdge(Random random)
   {
      type = random.nextBoolean() ?EdgeType.OUT :EdgeType.IN;
      curv1 = random.nextInt(3);
      curv2 = random.nextInt(3);
      neck1 = random.nextInt(3);
      neck2 = random.nextInt(3);
   }
}

public ImagePuzzle generate(int pWidth, int pHeight, Bitmap image)
{
   Box box = new Box();
   ArrayList<SinglePiece> singlePieces = new ArrayList<>();
   int totalPieces = pHeight * pWidth;
   int verticalEdges = pHeight * (pWidth - 1);
   int horizontalEdges = (pHeight - 1) * pWidth;
   //int totalEdges = verticalEdges + horizontalEdges;
   
   // boolean[] edgeTypeIn = new boolean[totalEdges];
   
   Random rng = new Random();
   SVGPath[][] pool = SVGPath.generateAllJigsaws();
   SVGPath[] rotatedPaths = new SVGPath[8];
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
         RectF edgeWidths = new RectF(0f, 0f, 0f, 0f);
         
         singlePieces.add(new SinglePiece(box, new Point(x, y),
          north == null ?EdgeType.EDGE :north.type,
          east == null ?EdgeType.EDGE :east.type,
          south == null ?EdgeType.EDGE :south.type,
          wests[y] == null ?EdgeType.EDGE :wests[y].type,
          rotatedPaths, edgeWidths));
         
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
   
   
   
   ImagePuzzle ret = new ImagePuzzle();
   return ret;
}
}
