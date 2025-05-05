package github.adjustamat.jigsawpuzzlefloss.pieces;

import android.graphics.Path;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.Random;

public abstract class WholeEdge
{
private static final StraightLine STRAIGHT_NORTH = new StraightLine(new float[]{120f, 0f});
private static final StraightLine STRAIGHT_EAST = new StraightLine(new float[]{0f, 120f});
private static final StraightLine STRAIGHT_SOUTH = new StraightLine(new float[]{-120f, 0f});
private static final StraightLine STRAIGHT_WEST = new StraightLine(new float[]{0f, -120f});

public static StraightEdge getNorthOuterEdge (){
   return new StraightEdge(STRAIGHT_NORTH);
}

public static StraightEdge getEastOuterEdge (){
   return new StraightEdge(STRAIGHT_EAST);
}

public static StraightEdge getSouthOuterEdge (){
   return new StraightEdge(STRAIGHT_SOUTH);
}

public static StraightEdge getWestOuterEdge (){
   return new StraightEdge(STRAIGHT_WEST);
}

/**
 * Returns the pool from which to get paths with the selected neckWidth and curvature, like this:
 * pool [NECK*6 + CURV*2 + (secondHalf?1:0)] [(inward?4:0) + direction_ordinal]
 * @return the pool of pre-rotated paths
 */
public static HalfEdge[][] generateAllJigsaws (){
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

static HalfEdge[] generateFirstHalf (float neckWidth, float curvature){
   HalfEdge cfirst_north_out = new HalfEdge(
    new float[]{0f, 0f,/*<-cp1*/ 40f, curvature,/*<-cp2*/ 45f, curvature,//<-endPoint
     15f, 0f,/*<-cp1*/ 45f - neckWidth, -curvature,/*<-cp2*/ 47.5f - neckWidth, -7.5f - curvature, 2.5f, -7.5f,/*<-cp1*/
     -15 + neckWidth, -15f,/*<-cp2*/ -15 + neckWidth, -25f,//<-endPoint
     0f, -10f,/*<-cp1*/ 7.5f, -17.5f,/*<-cp2*/ 22.5f, -17.5f//<-endPoint
    }).setEdgeWidth(25 + 7.5f + 17.5f);
   HalfEdge cfirst_east_out = cfirst_north_out.getTransformed(Direction.EAST)
    .setEdgeWidth(cfirst_north_out.edgeWidth);
   HalfEdge cfirst_south_out = cfirst_north_out.getTransformed(Direction.SOUTH)
    .setEdgeWidth(cfirst_north_out.edgeWidth);
   HalfEdge cfirst_west_out = cfirst_north_out.getTransformed(Direction.WEST)
    .setEdgeWidth(cfirst_north_out.edgeWidth);
   
   HalfEdge cfirst_north_in = cfirst_north_out.getTransformed(Direction.NORTH).setEdgeWidth(curvature);
   HalfEdge cfirst_east_in = cfirst_north_in.getTransformed(Direction.EAST).setEdgeWidth(curvature);
   HalfEdge cfirst_south_in = cfirst_north_in.getTransformed(Direction.SOUTH).setEdgeWidth(curvature);
   HalfEdge cfirst_west_in = cfirst_north_in.getTransformed(Direction.WEST).setEdgeWidth(curvature);
   
   return new HalfEdge[]{cfirst_north_out, cfirst_east_out, cfirst_south_out, cfirst_west_out, cfirst_north_in,
    cfirst_east_in, cfirst_south_in, cfirst_west_in};
}

static HalfEdge[] generateSecondHalf (float neckWidth, float curvature){
   HalfEdge csecond_north_out = new HalfEdge(
    new float[]{15f, 0f,/*<-cp1*/ 22.5f, 7.5f,/*<-cp2*/ 22.5f, 17.5f,//<-endPoint
     0f, -10f,/*<-cp1*/ -17.5f + neckWidth, -17.5f,/*<-cp2*/ -15 + neckWidth, 25,//<-endPoint
     2.5f, 7.5f,/*<-cp1*/ 32.5f - neckWidth, 7.5f + curvature,/*<-cp2*/ 47.5f - neckWidth, 7.5f + curvature, 5f, 0f,
/*<-cp1*/ 45f, -curvature,/*<-cp2*/ 45f, -curvature//<-endPoint
    }).setEdgeWidth(25 + 7.5f + 17.5f);
   HalfEdge csecond_east_out = csecond_north_out.getTransformed(Direction.EAST)
    .setEdgeWidth(csecond_north_out.edgeWidth);
   HalfEdge csecond_south_out = csecond_north_out.getTransformed(Direction.SOUTH)
    .setEdgeWidth(csecond_north_out.edgeWidth);
   HalfEdge csecond_west_out = csecond_north_out.getTransformed(Direction.WEST)
    .setEdgeWidth(csecond_north_out.edgeWidth);
   
   HalfEdge csecond_north_in = csecond_north_out.getTransformed(Direction.NORTH).setEdgeWidth(curvature);
   HalfEdge csecond_east_in = csecond_north_in.getTransformed(Direction.EAST).setEdgeWidth(curvature);
   HalfEdge csecond_south_in = csecond_north_in.getTransformed(Direction.SOUTH).setEdgeWidth(curvature);
   HalfEdge csecond_west_in = csecond_north_in.getTransformed(Direction.WEST).setEdgeWidth(curvature);
   
   return new HalfEdge[]{csecond_north_out, csecond_east_out, csecond_south_out, csecond_west_out, csecond_north_in,
    csecond_east_in, csecond_south_in, csecond_west_in};
}

private Point subPiece;
private Direction subPieceDir;

public Point getSubPiece (){
   return subPiece;
}

public Direction getSubPieceDir (){
   return subPieceDir;
}

public WholeEdge setSubPiece (Point subPiece, Direction dir){
   this.subPiece = subPiece;
   this.subPieceDir = dir;
   return this;
}

private WholeEdge next;
private WholeEdge prev;

public WholeEdge getNext (){
   return next;
}

public WholeEdge getPrev (){
   return prev;
}

public WholeEdge unlinkInnerEdge (){
   next = null;
   prev = null;
   return this;
}

public WholeEdge linkNext (WholeEdge next){
   this.next = next;
   next.prev = this;
   return this;
}

public abstract void appendSegmentsTo (Path path);
public abstract float getEdgeWidth ();

/**
 * Two HalfEdges that makes a WholeEdge.
 * Can be further combined into SinglePieceOutlines, LargerPieceOutlines, or stored in LargerPiece innerEdges.
 */
public static class DoubleEdge
 extends WholeEdge
{
   private final HalfEdge half1, half2;
   
   public DoubleEdge (HalfEdge firstHalf, HalfEdge secondHalf){
      half1 = firstHalf;
      half2 = secondHalf;
   }
   
   public void appendSegmentsTo (Path path){
      half1.appendHalfEdgeTo(path);
      half2.appendHalfEdgeTo(path);
   }
   
   public float getEdgeWidth (){
      return Math.max(half1.edgeWidth, half2.edgeWidth);
   }
}

public static class StraightEdge
 extends WholeEdge
{
   StraightLine impl;
   
   private StraightEdge (StraightLine data){
      this.impl = data;
   }
   
   public void appendSegmentsTo (Path path){
      path.rLineTo(impl.data[0], impl.data[1]);
   }
   
   public float getEdgeWidth (){
      return 0f;
   }
}

private static class StraightLine
{
   private final float[] data;
   
   private StraightLine (float[] data){
      this.data = data;
   }
}

/**
 * HalfEdges are generated for the pool.
 */
public static class HalfEdge
{
   private final ArrayList<Cubic> segments;
   
   float edgeWidth;
   
   /**
    * Create a NORTH OUT HalfEdge.
    * @param allData data for all Cubic segments of this SVG HalfEdge.
    */
   private HalfEdge (float[] allData){
      segments = new ArrayList<>(4); // 4 segments
      for (int i = 0; i < allData.length; i += 6) { // 6 float values per segment (rCubicTo)
         segments.add(new Cubic(allData, i/*, true*/));
      }
   }
   
   private HalfEdge (ArrayList<Cubic> segments){
      this.segments = segments;
   }
   
   HalfEdge setEdgeWidth (float f){
      edgeWidth = f;
      return this;
   }
   
   public void appendHalfEdgeTo (Path path){
      for (Cubic segm: segments) {
         segm.appendSegmentTo(path);
      }
   }
   
   private HalfEdge getTransformed (Direction dir){
      ArrayList<Cubic> ret = new ArrayList<>(4);
      for (Cubic segm: segments) {
         Cubic newSegment;
         switch (dir) {
         case NORTH:
            newSegment = segm.flipToIn();
            break;
         case EAST:
            newSegment = segm.rotateToEast();
            break;
         case WEST:
            newSegment = segm.rotateToWest();
            break;
         default:// case SOUTH:
            newSegment = segm.rotateToSouth();
         }
         ret.add(newSegment);
      }
      return new HalfEdge(ret);
   }
} // class HalfEdge

private static class Cubic
{
   final float[] data;
   
   Cubic (float[] data){
      this.data = data;
   }
   
   Cubic (float[] allData, int dataOffset/*, boolean from102to60*/){
      this.data = new float[6];
      for (int i = 0; i < data.length; i++) {
         data[i] = allData[dataOffset + i];
         //if (from102to60)
         data[i] /= 1.7f;
      }
   }
   
   void appendSegmentTo (Path path){
      path.rCubicTo(data[0], data[1], data[2], data[3], data[4], data[5]);
   }
   
   Cubic rotateToEast (){
      // rotate clockwise - exchange x and (inverted) y values:
      return new Cubic(new float[]{-data[1], data[0], -data[3], data[2], -data[5], data[4]});
   }
   
   Cubic rotateToWest (){
      // rotate counterclockwise - exchange (inverted) x and y values:
      return new Cubic(new float[]{-data[1], data[0], -data[3], data[2], -data[5], data[4]});
   }
   
   Cubic rotateToSouth (){
      // rotate twice (or flip both ways) - invert x-values and y-values:
      return new Cubic(new float[]{-data[0], -data[1], -data[2], -data[3], -data[4], -data[5]});
   }
   
   Cubic flipToIn (){
      // flip (mirror) upside-down - invert y-values:
      return new Cubic(new float[]{data[0], -data[1], data[2], -data[3], data[4], -data[5]});
   }
} // class Cubic

public static class RandomEdge
{
   public final boolean in;
   public final int curv1, curv2;
   public final int neck1, neck2;
   
   public RandomEdge (Random rng){
      in = rng.nextBoolean();
      curv1 = rng.nextInt(3);
      curv2 = rng.nextInt(3);
      neck1 = rng.nextInt(3);
      neck2 = rng.nextInt(3);
   }
   
   public DoubleEdge getWholeEdge (HalfEdge[][] pool, Direction dir){
      int poolIndex;
      switch (dir) {
      case EAST: case SOUTH:
         poolIndex = (in ?4 :0) + dir.ordinal();
         return new DoubleEdge(
          // pool [ NECK*6 + CURV*2 + (secondHalf?1:0) ]  [ (inward ?4 :0) + direction.ordinal ]
          pool[neck1 * 6 + curv1 * 2][poolIndex],
          pool[neck2 * 6 + curv2 * 2 + 1][poolIndex]
         );
      default: // WEST: NORTH:
         // in/out is flipped:
         poolIndex = (in ?0 :4) + dir.ordinal();
         return new DoubleEdge(
          // first/second is flipped:
          pool[neck2 * 6 + curv2 * 2][poolIndex],
          pool[neck1 * 6 + curv1 * 2 + 1][poolIndex]
         );
      }
   }
} // class RandomEdge
}
