package github.adjustamat.jigsawpuzzlefloss.pieces;

import android.graphics.Path;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.Random;

import github.adjustamat.jigsawpuzzlefloss.game.Direction;

/**
 * A jigsaw edge, or straight edge for an "edge piece" (see {@link AbstractPiece#isEdgePiece()}), being one of four sides of a SinglePiece.
 */
public abstract class PieceEdge
{
private static final StraightLine STRAIGHT_NORTH = new StraightLine(new float[]{120f, 0f});
private static final StraightLine STRAIGHT_EAST = new StraightLine(new float[]{0f, 120f});
private static final StraightLine STRAIGHT_SOUTH = new StraightLine(new float[]{-120f, 0f});
private static final StraightLine STRAIGHT_WEST = new StraightLine(new float[]{0f, -120f});
public static final float STRAIGHT_EDGE_WIDTH = 0f;

public static StraightEdge getStraightEdge(Direction d)
{
   switch (d) {
   case NORTH:
      return new StraightEdge(STRAIGHT_NORTH);
   case EAST:
      return new StraightEdge(STRAIGHT_EAST);
   case SOUTH:
      return new StraightEdge(STRAIGHT_SOUTH);
   default: // case WEST:
      return new StraightEdge(STRAIGHT_WEST);
   }
}

/**
 * Returns the pool from which to get jigsaw edges with desired neckWidth (NECK) and curvature (CURV) like this:
 * pool [ NECK*6 + CURV*2 + (secondHalf?1:0) ]  [ (inward?4:0) + direction_ordinal ]
 * @return the pool of pre-rotated jigsaw edges, first and second half, pointing inward and outward.
 */
public static HalfEdge[][] generateAllJigsawEdges()
{
   HalfEdge[][] ret = new HalfEdge[3 * 3 * 2][];
   int reti = 0;
   for (float neckWidth = 0f; neckWidth < 13f; neckWidth += 5f) { // from narrow to wide
      for (float curvature = 0f; curvature < 6.25f; curvature += 2.5f) { // from straight to bent
         ret[reti++] = generateFirstHalf(neckWidth, curvature);
         ret[reti++] = generateSecondHalf(neckWidth, curvature);
      }
   }
   return ret;
}

private static HalfEdge[] generateFirstHalf(float neckWidth, float curvature)
{
   HalfEdge cfirst_north_out = new HalfEdge(
    new float[]{0f, 0f, // <- control from
     40f, curvature, // <- control to
     45f, curvature, // <- endPoint 1
     
     15f, 0f, // <- control from
     45 - neckWidth, -curvature, // <- control to
     47.5f - neckWidth, -7.5f - curvature, // <- endPoint 2
     
     2.5f, -7.5f,// <- control from
     -15 + neckWidth, -15f,// <- control to
     -15 + neckWidth, -25f, // <- endPoint 3
     
     0f, -10f, // <- control from
     7.5f, -17.5f, // <- control to
     22.5f, -17.5f // <- endPoint 4
    },
    25 + 7.5f + 17.5f
   );
   HalfEdge cfirst_east_out = cfirst_north_out.getTransformed(Direction.EAST);
   HalfEdge cfirst_south_out = cfirst_north_out.getTransformed(Direction.SOUTH);
   HalfEdge cfirst_west_out = cfirst_north_out.getTransformed(Direction.WEST);
   
   HalfEdge cfirst_north_in = cfirst_north_out.getTransformed(Direction.NORTH);
   cfirst_north_in.edgeWidth = curvature;
   HalfEdge cfirst_east_in = cfirst_north_in.getTransformed(Direction.EAST);
   HalfEdge cfirst_south_in = cfirst_north_in.getTransformed(Direction.SOUTH);
   HalfEdge cfirst_west_in = cfirst_north_in.getTransformed(Direction.WEST);
   
   return new HalfEdge[]{cfirst_north_out, cfirst_east_out, cfirst_south_out, cfirst_west_out,
    cfirst_north_in, cfirst_east_in, cfirst_south_in, cfirst_west_in};
}

private static HalfEdge[] generateSecondHalf(float neckWidth, float curvature)
{
   HalfEdge csecond_north_out = new HalfEdge(
    new float[]{15f, 0f, // <- control from
     22.5f, 7.5f, // <- control to
     22.5f, 17.5f, // <- endPoint 1
     
     0f, -10f, // <- control from
     -17.5f + neckWidth, -17.5f, // <- control to
     -15 + neckWidth, 25f, // <- endPoint 2
     
     2.5f, 7.5f, // <- control from
     32.5f - neckWidth, 7.5f + curvature, // <- control to
     47.5f - neckWidth, 7.5f + curvature, // <- endPoint 3
     
     5f, 0f, // <- control from
     45f, -curvature, // <- control to
     45f, -curvature // <- endPoint 4
    },
    25 + 7.5f + 17.5f
   );
   HalfEdge csecond_east_out = csecond_north_out.getTransformed(Direction.EAST);
   HalfEdge csecond_south_out = csecond_north_out.getTransformed(Direction.SOUTH);
   HalfEdge csecond_west_out = csecond_north_out.getTransformed(Direction.WEST);
   
   HalfEdge csecond_north_in = csecond_north_out.getTransformed(Direction.NORTH);
   csecond_north_in.edgeWidth = curvature;
   HalfEdge csecond_east_in = csecond_north_in.getTransformed(Direction.EAST);
   HalfEdge csecond_south_in = csecond_north_in.getTransformed(Direction.SOUTH);
   HalfEdge csecond_west_in = csecond_north_in.getTransformed(Direction.WEST);
   
   return new HalfEdge[]{csecond_north_out, csecond_east_out, csecond_south_out, csecond_west_out,
    csecond_north_in, csecond_east_in, csecond_south_in, csecond_west_in};
}

private Point subPiece;
private Direction subPieceDir;

public Point getSubPiece()
{
   return subPiece;
}

public Direction getSubPieceDir()
{
   return subPieceDir;
}

public Direction getRealDirection(Direction rotate)
{
   switch (rotate) {
   case EAST:
      return subPieceDir.next();
   case SOUTH:
      return subPieceDir.opposite();
   case WEST:
      return subPieceDir.prev();
   default: //    case NORTH:
      return subPieceDir;
   }
}

public PieceEdge setSubPiece(Point subPiece, Direction dir)
{
   this.subPiece = subPiece;
   this.subPieceDir = dir;
   return this;
}

private PieceEdge nextLink;
private PieceEdge prevLink;

public PieceEdge getNext()
{
   return nextLink;
}

public PieceEdge getPrev()
{
   return prevLink;
}

public PieceEdge linkNext(PieceEdge next)
{
   this.nextLink = next;
   next.prevLink = this;
   return this;
}

public abstract void appendSegmentsTo(Path path);
public abstract float getEdgeWidth();

/**
 * Two HalfEdges makes a DoubleEdge.
 */
public static class DoubleEdge
 extends PieceEdge
{
   private final HalfEdge half1, half2;
   
   public DoubleEdge(HalfEdge firstHalf, HalfEdge secondHalf)
   {
      half1 = firstHalf;
      half2 = secondHalf;
   }
   
   public void appendSegmentsTo(Path path)
   {
      half1.appendHalfEdgeTo(path);
      half2.appendHalfEdgeTo(path);
   }
   
   public float getEdgeWidth()
   {
      return Math.max(half1.edgeWidth, half2.edgeWidth);
   }
}

/**
 * The straight edge of an "edge piece"
 * @see AbstractPiece#isEdgePiece()
 */
public static class StraightEdge
 extends PieceEdge
{
   StraightLine impl;
   
   private StraightEdge(StraightLine data)
   {
      this.impl = data;
   }
   
   public void appendSegmentsTo(Path path)
   {
      path.rLineTo(impl.data[0], impl.data[1]);
   }
   
   public float getEdgeWidth()
   {
      return STRAIGHT_EDGE_WIDTH;
   }
}

private static class StraightLine
{
   private final float[] data;
   
   private StraightLine(float[] data)
   {
      this.data = data;
   }
}

/**
 * A HalfEdge is a jigsaw edge, generated for the pool, made up of Cubic SVG segments. It is one half of a PieceEdge.
 */
public static class HalfEdge
{
   private final ArrayList<Cubic> segments;
   
   float edgeWidth;
   
   /**
    * Create a NORTH OUT HalfEdge.
    * @param allData data for all Cubic segments of this HalfEdge.
    */
   private HalfEdge(float[] allData, float edgeWidth)
   {
      this.edgeWidth = edgeWidth;
      segments = new ArrayList<>(4); // 4 segments
      for (int i = 0; i < allData.length; i += 6) { // 6 float values per segment (rCubicTo)
         segments.add(new Cubic(allData, i));
      }
   }
   
   private HalfEdge(ArrayList<Cubic> segments, float edgeWidth)
   {
      this.edgeWidth = edgeWidth;
      this.segments = segments;
   }
   
   public void appendHalfEdgeTo(Path path)
   {
      for (Cubic segm: segments) {
         segm.appendSegmentTo(path);
      }
   }
   
   private HalfEdge getTransformed(Direction dir)
   {
      ArrayList<Cubic> ret = new ArrayList<>(4);
      for (Cubic segm: segments) {
         Cubic newSegment;
         switch (dir) {
         case EAST:
            newSegment = segm.rotateToEast();
            break;
         case WEST:
            newSegment = segm.rotateToWest();
            break;
         case SOUTH:
            newSegment = segm.rotateToSouth();
            break;
         default: // case NORTH:
            newSegment = segm.flipToIn();
         }
         ret.add(newSegment);
      }
      return new HalfEdge(ret, edgeWidth);
   }
} // class HalfEdge

private static class Cubic
{
   final float[] data;
   
   Cubic(float[] data)
   {
      this.data = data;
   }
   
   Cubic(float[] allData, int dataOffset/*, boolean from102to60*/)
   {
      this.data = new float[6];
      for (int i = 0; i < data.length; i++) {
         data[i] = allData[dataOffset + i];
         //if (from102to60)
         data[i] /= 1.7f;
      }
   }
   
   void appendSegmentTo(Path path)
   {
      path.rCubicTo(data[0], data[1], data[2], data[3], data[4], data[5]);
   }
   
   Cubic rotateToEast()
   {
      // rotate clockwise - exchange x and (inverted) y values:
      return new Cubic(new float[]{-data[1], data[0], -data[3], data[2], -data[5], data[4]});
   }
   
   Cubic rotateToWest()
   {
      // rotate counterclockwise - exchange (inverted) x and y values:
      return new Cubic(new float[]{-data[1], data[0], -data[3], data[2], -data[5], data[4]});
   }
   
   Cubic rotateToSouth()
   {
      // rotate twice (or flip both ways) - invert x-values and y-values:
      return new Cubic(new float[]{-data[0], -data[1], -data[2], -data[3], -data[4], -data[5]});
   }
   
   Cubic flipToIn()
   {
      // flip (mirror) upside-down - invert y-values:
      return new Cubic(new float[]{data[0], -data[1], data[2], -data[3], data[4], -data[5]});
   }
} // class Cubic

public static class RandomEdge
{
   public final boolean in;
   public final int curv1, curv2;
   public final int neck1, neck2;
   
   public RandomEdge(Random rng)
   {
      in = rng.nextBoolean();
      curv1 = rng.nextInt(3);
      curv2 = rng.nextInt(3);
      neck1 = rng.nextInt(3);
      neck2 = rng.nextInt(3);
   }
   
   public DoubleEdge getPieceEdge(HalfEdge[][] pool, Direction dir)
   {
      int rotationIndex;
      switch (dir) {
      case EAST: case SOUTH:
         rotationIndex = (in ?4 :0) + dir.ordinal();
         return new DoubleEdge(
          // pool [ NECK*6 + CURV*2 + (secondHalf?1:0) ]  [ (inward ?4 :0) + direction.ordinal ]
          pool[neck1 * 6 + curv1 * 2][rotationIndex],
          pool[neck2 * 6 + curv2 * 2 + 1][rotationIndex]
         );
      default: // WEST: NORTH:
         // in/out is flipped:
         rotationIndex = (in ?0 :4) + dir.ordinal();
         return new DoubleEdge(
          // first/second is flipped:
          pool[neck2 * 6 + curv2 * 2][rotationIndex],
          pool[neck1 * 6 + curv1 * 2 + 1][rotationIndex]
         );
      }
   }
} // class RandomEdge
}
