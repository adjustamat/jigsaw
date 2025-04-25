package github.adjustamat.jigsawpuzzlefloss.pieces;

import android.graphics.Path;

import java.util.ArrayList;
import java.util.LinkedList;

public abstract class SVGPath
{
public static class LargerPieceOutline
 extends SVGPath
{
   private final LinkedList<WholeEdge> children = new LinkedList<>();
   
   public LargerPieceOutline()
   {
      // TODO: use a LinkedList internally, so that I can inset the path of a new piece into a LargerPiece shape.
      //  keep a record of which piece (x,y) is where in the linked list! this is not needed for innerEdges.
   }
   
   public void toPath(Path path)
   {
      for (WholeEdge child: children) {
         child.toPath(path);
      }
   }
}

public static class SinglePieceOutline
 extends SVGPath
{
   final WholeEdge north;
   final WholeEdge east;
   final WholeEdge south;
   final WholeEdge west;
   
   public SinglePieceOutline(WholeEdge north, WholeEdge east, WholeEdge south, WholeEdge west)
   {
      this.north = north;
      this.east = east;
      this.south = south;
      this.west = west;
   }
   
   public void toPath(Path path)
   {
      north.toPath(path);
      east.toPath(path);
      south.toPath(path);
      west.toPath(path);
   }
}

public static abstract class WholeEdge
{
   public abstract void toPath(Path path);
   public abstract float getEdgeWidth();
}

/**
 * Two HalfEdges that makes a WholeEdge.
 * Can be further combined into SinglePieceOutlines, LargerPieceOutlines, or stored in LargerPiece innerEdges.
 */
public static class DoubleEdge
 extends WholeEdge
{
   //
   final HalfEdge half1;
   final HalfEdge half2;
   
   DoubleEdge(HalfEdge firstHalf, HalfEdge secondHalf)
   {
      half1 = firstHalf;
      half2 = secondHalf;
   }
   
   public void toPath(Path path)
   {
      half1.toPath(path);
      half2.toPath(path);
   }
   
   public float getEdgeWidth()
   {
      return Math.max(half1.edgeWidth, half2.edgeWidth);
   }
}

public static class StraightEdge
 extends WholeEdge
{
   public static final StraightEdge STRAIGHT_NORTH = new StraightEdge(new float[]{120f, 0f});
   public static final StraightEdge STRAIGHT_EAST = new StraightEdge(new float[]{0f, 120f});
   public static final StraightEdge STRAIGHT_SOUTH = new StraightEdge(new float[]{-120f, 0f});
   public static final StraightEdge STRAIGHT_WEST = new StraightEdge(new float[]{0f, -120f});
   
   private final float[] data;
   
   private StraightEdge(float[] data)
   {
      this.data = data;
   }
   
   public void toPath(Path path)
   {
      path.rLineTo(data[0], data[1]);
   }
   
   public float getEdgeWidth()
   {
      return 0f;
   }
}

/**
 * HalfEdges are generated for the pool.
 */
public static class HalfEdge
{
   final ArrayList<Cubic> segments;
   
   float edgeWidth;
   
   /**
    * Create a NORTH OUT HalfEdge.
    * @param allData data for all Cubic segments of this SVGPath.
    */
   private HalfEdge(float[] allData)
   {
      segments = new ArrayList<>(4); // 4 segments
      for (int i = 0; i < allData.length; i += 6) { // 6 float values per segment (rCubicTo)
         segments.add(new Cubic(allData, i, true));
      }
   }
   
   private HalfEdge(ArrayList<Cubic> segments)
   {
      this.segments = segments;
   }
   
   HalfEdge setEdgeWidth(float f)
   {
      edgeWidth = f;
      return this;
   }
   
   public void toPath(Path path)
   {
      for (Cubic segment: segments) {
         segment.toPath(path);
      }
   }
   
   HalfEdge getTransformed(Direction dir)
   {
      ArrayList<Cubic> ret = new ArrayList<>(4);
      for (Cubic segm: segments) {
         Cubic newSegment;
         switch (dir) {
         case NORTH:
            newSegment = new Cubic(segm.flipIn());
            break;
         case EAST:
            newSegment = new Cubic(segm.rotateEAST());
            break;
         case WEST:
            newSegment = new Cubic(segm.rotateWEST());
            break;
         default:// case SOUTH:
            newSegment = new Cubic(segm.rotateSOUTH());
         }
         ret.add(newSegment);
      }
      return new HalfEdge(ret);
   }
}

/**
 * Returns the pool from which to get paths with the selected neckWidth and curvature, like this:
 * pool [NECK*6 + CURV*2 + (secondHalf?1:0)] [(inward?4:0) + direction_ordinal]
 * @return the pool of pre-rotated paths
 */
public static HalfEdge[][] generateAllJigsaws()
{
   HalfEdge[][] ret = new HalfEdge[3 * 3 * 2][];
   int reti = 0;
   for (float neckWidth = 0f; neckWidth < 13f; neckWidth += 5f) { // from narrow to wide
      for (float curvature = 0f; curvature < 6.25f; curvature += 2.5f) { // from straight to curved
         ret[reti++] = generateFirstHalf(neckWidth, curvature);
         ret[reti++] = generateSecondHalf(neckWidth, curvature);
      }
   }
   // returns 18 arrays:
   //  generated[0] == cfirst straight and narrow
   //  generated[1] == csecond straight and narrow
   //  generated[2] == cfirst bent and narrow
   //  generated[3] == csecond bent and narrow
   //  generated[4] == cfirst curved and narrow
   //  generated[5] == csecond curved and narrow
   //  generated[6] == cfirst straight and mid
   //  generated[7] == csecond straight and mid
   //  generated[8] == cfirst bent and mid
   //  generated[9] == csecond bent and mid
   //  generated[10] == cfirst curved and mid
   //  generated[11] == csecond curved and mid
   //  generated[12] == cfirst straight and wide
   //  generated[13] == csecond straight and wide
   //  generated[14] == cfirst bent and wide
   //  generated[15] == csecond bent and wide
   //  generated[16] == cfirst curved and wide
   //  generated[17] == csecond curved and wide
   // each array has length 8:
   //  array[0] == out NORTH
   //  array[1] == out EAST
   //  array[2] == out SOUTH
   //  array[3] == out WEST
   //  array[4] == in NORTH
   //  array[5] == in EAST
   //  array[6] == in SOUTH
   //  array[7] == in WEST
   return ret;
}

static HalfEdge[] generateSecondHalf(float neckWidth, float curvature)
{
   HalfEdge csecond_north_out = new HalfEdge(new float[]{
    15f, 0f,/*<-cp1*/ 22.5f, 7.5f,/*<-cp2*/ 22.5f, 17.5f,//<-endPoint
    0f, -10f,/*<-cp1*/ -17.5f + neckWidth, -17.5f,/*<-cp2*/ -15 + neckWidth, 25,//<-endPoint
    2.5f, 7.5f,/*<-cp1*/ 32.5f - neckWidth, 7.5f + curvature,/*<-cp2*/ 47.5f - neckWidth, 7.5f + curvature,
    5f, 0f,/*<-cp1*/ 45f, -curvature,/*<-cp2*/ 45f, -curvature//<-endPoint
   }).setEdgeWidth(25 + 7.5f + 17.5f);
   HalfEdge csecond_east_out = csecond_north_out.getTransformed(Direction.EAST)
    .setEdgeWidth(csecond_north_out.edgeWidth);
   HalfEdge csecond_south_out = csecond_north_out.getTransformed(Direction.SOUTH)
    .setEdgeWidth(csecond_north_out.edgeWidth);
   HalfEdge csecond_west_out = csecond_north_out.getTransformed(Direction.WEST)
    .setEdgeWidth(csecond_north_out.edgeWidth);
   
   HalfEdge csecond_north_in = csecond_north_out.getTransformed(Direction.NORTH)
    .setEdgeWidth(curvature);
   HalfEdge csecond_east_in = csecond_north_in.getTransformed(Direction.EAST)
    .setEdgeWidth(curvature);
   HalfEdge csecond_south_in = csecond_north_in.getTransformed(Direction.SOUTH)
    .setEdgeWidth(curvature);
   HalfEdge csecond_west_in = csecond_north_in.getTransformed(Direction.WEST)
    .setEdgeWidth(curvature);
   
   return new HalfEdge[]{
    csecond_north_out, csecond_east_out, csecond_south_out, csecond_west_out,
    csecond_north_in, csecond_east_in, csecond_south_in, csecond_west_in
   };
}

static HalfEdge[] generateFirstHalf(float neckWidth, float curvature)
{
   HalfEdge cfirst_north_out = new HalfEdge(new float[]{
    0f, 0f,/*<-cp1*/ 40f, curvature,/*<-cp2*/ 45f, curvature,//<-endPoint
    15f, 0f,/*<-cp1*/ 45f - neckWidth, -curvature,/*<-cp2*/ 47.5f - neckWidth, -7.5f - curvature,
    2.5f, -7.5f,/*<-cp1*/ -15 + neckWidth, -15f,/*<-cp2*/ -15 + neckWidth, -25f,//<-endPoint
    0f, -10f,/*<-cp1*/ 7.5f, -17.5f,/*<-cp2*/ 22.5f, -17.5f//<-endPoint
   }).setEdgeWidth(25 + 7.5f + 17.5f);
   HalfEdge cfirst_east_out = cfirst_north_out.getTransformed(Direction.EAST)
    .setEdgeWidth(cfirst_north_out.edgeWidth);
   HalfEdge cfirst_south_out = cfirst_north_out.getTransformed(Direction.SOUTH)
    .setEdgeWidth(cfirst_north_out.edgeWidth);
   HalfEdge cfirst_west_out = cfirst_north_out.getTransformed(Direction.WEST)
    .setEdgeWidth(cfirst_north_out.edgeWidth);
   
   HalfEdge cfirst_north_in = cfirst_north_out.getTransformed(Direction.NORTH)
    .setEdgeWidth(curvature);
   HalfEdge cfirst_east_in = cfirst_north_in.getTransformed(Direction.EAST)
    .setEdgeWidth(curvature);
   HalfEdge cfirst_south_in = cfirst_north_in.getTransformed(Direction.SOUTH)
    .setEdgeWidth(curvature);
   HalfEdge cfirst_west_in = cfirst_north_in.getTransformed(Direction.WEST)
    .setEdgeWidth(curvature);
   
   return new HalfEdge[]{
    cfirst_north_out, cfirst_east_out, cfirst_south_out, cfirst_west_out,
    cfirst_north_in, cfirst_east_in, cfirst_south_in, cfirst_west_in
   };
}

public static class Cubic
{
   public final float[] data;
   
   Cubic(float[] data)
   {
      this.data = data;
   }
   
   Cubic(float[] allData, int dataOffset, boolean from102to60)
   {
      this.data = new float[6];
      for (int i = 0; i < data.length; i++) {
         data[i] = allData[dataOffset + i];
         if (from102to60)
            data[i] /= 1.7f;
      }
   }
   
   public void toPath(Path path)
   {
      path.rCubicTo(data[0], data[1], data[2], data[3], data[4], data[5]);
   }
   
   float[] rotateEAST()
   {
      // rotate clockwise - exchange x and (inverted) y values:
      return new float[]{-data[1], data[0], -data[3], data[2], -data[5], data[4]};
   }
   
   float[] rotateWEST()
   {
      // rotate counterclockwise - exchange (inverted) x and y values:
      return new float[]{-data[1], data[0], -data[3], data[2], -data[5], data[4]};
   }
   
   float[] rotateSOUTH()
   {
      // rotate twice - invert x-values and y-values:
      return new float[]{-data[0], -data[1], -data[2], -data[3], -data[4], -data[5]};
   }
   
   float[] flipIn()
   {
      // Mirror this Cubic segment, turning it from EdgeType OUT to IN - invert y-values:
      return new float[]{data[0], -data[1], data[2], -data[3], data[4], -data[5]};
   }
} // class Cubic

public abstract void toPath(Path path);

/**
 * Create a closed SVG Path with the supplied top-left corner.
 * @param startX X of top-left corner
 * @param startY Y of top-left corner
 * @return a closed Path
 */
public Path toSVG(float startX, float startY)
{
   Path ret = new Path();
   ret.moveTo(startX, startY);
   this.toPath(ret);
   ret.close();
   return ret;
}
}
