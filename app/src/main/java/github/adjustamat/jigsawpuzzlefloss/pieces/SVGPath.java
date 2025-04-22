package github.adjustamat.jigsawpuzzlefloss.pieces;

import android.graphics.Path;

import java.util.ArrayList;

public class SVGPath
{
public static final int NECK_NARROW = 0;
public static final int NECK_MID = 1;
public static final int NECK_WIDE = 2;
public static final int CURV_NONE = 0;
public static final int CURV_SMALL = 1;
public static final int CURV_BIG = 2;

/**
 * Returns the pool from which to get paths with the selected neckWidth and curvature, like this:
 * pool [NECK*6 + CURV*2 + (secondHalf?1:0)] [(inward?4:0) + direction_ordinal]
 * @return the pool of pre-rotated paths
 */
public static SVGPath[][] generateAllJigsaws()
{
   SVGPath[][] ret = new SVGPath[3 * 3 * 2][];
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

static SVGPath[] generateSecondHalf(float neckWidth, float curvature)
{
   SVGPath csecond_north_out = new SVGPath(new float[]{
    15f, 0f,/*<-cp1*/ 22.5f, 7.5f,/*<-cp2*/ 22.5f, 17.5f,//<-endPoint
    0f, -10f,/*<-cp1*/ -17.5f + neckWidth, -17.5f,/*<-cp2*/ -15 + neckWidth, 25,//<-endPoint
    2.5f, 7.5f,/*<-cp1*/ 32.5f - neckWidth, 7.5f + curvature,/*<-cp2*/ 47.5f - neckWidth, 7.5f + curvature,
    5f, 0f,/*<-cp1*/ 45f, -curvature,/*<-cp2*/ 45f, -curvature//<-endPoint
   }).setEdgeWidth(0f);
   SVGPath csecond_east_out = new SVGPath(csecond_north_out).transform(Rotate.CLOCKWISE)
    .setEdgeWidth(0f);
   SVGPath csecond_south_out = new SVGPath(csecond_north_out).transform(Rotate.TWICE)
    .setEdgeWidth(0f);
   SVGPath csecond_west_out = new SVGPath(csecond_north_out).transform(Rotate.COUNTERCLOCKWISE)
    .setEdgeWidth(0f);
   // TODO: edgeWidths depend on curvature and IN/OUT
   SVGPath csecond_north_in = new SVGPath(csecond_north_out).transform(Rotate.FLIP)
    .setEdgeWidth(0f);
   SVGPath csecond_east_in = new SVGPath(csecond_north_in).transform(Rotate.CLOCKWISE)
    .setEdgeWidth(0f);
   SVGPath csecond_south_in = new SVGPath(csecond_north_in).transform(Rotate.TWICE)
    .setEdgeWidth(0f);
   SVGPath csecond_west_in = new SVGPath(csecond_north_in).transform(Rotate.COUNTERCLOCKWISE)
    .setEdgeWidth(0f);
   
   return new SVGPath[]{
    csecond_north_out, csecond_east_out, csecond_south_out, csecond_west_out,
    csecond_north_in, csecond_east_in, csecond_south_in, csecond_west_in
   };
}

static SVGPath[] generateFirstHalf(float neckWidth, float curvature)
{
   SVGPath cfirst_north_out = new SVGPath(new float[]{
    0f, 0f,/*<-cp1*/ 40f, curvature,/*<-cp2*/ 45f, curvature,//<-endPoint
    15f, 0f,/*<-cp1*/ 45f - neckWidth, -curvature,/*<-cp2*/ 47.5f - neckWidth, -7.5f - curvature,
    2.5f, -7.5f,/*<-cp1*/ -15 + neckWidth, -15f,/*<-cp2*/ -15 + neckWidth, -25f,//<-endPoint
    0f, -10f,/*<-cp1*/ 7.5f, -17.5f,/*<-cp2*/ 22.5f, -17.5f//<-endPoint
   }).setEdgeWidth(0f);
   SVGPath cfirst_east_out = new SVGPath(cfirst_north_out).transform(Rotate.CLOCKWISE)
    .setEdgeWidth(0f);
   SVGPath cfirst_south_out = new SVGPath(cfirst_north_out).transform(Rotate.TWICE)
    .setEdgeWidth(0f);
   SVGPath cfirst_west_out = new SVGPath(cfirst_north_out).transform(Rotate.COUNTERCLOCKWISE)
    .setEdgeWidth(0f);
   // TODO: edgeWidths depend on curvature and IN/OUT
   SVGPath cfirst_north_in = new SVGPath(cfirst_north_out).transform(Rotate.FLIP)
    .setEdgeWidth(0f);
   SVGPath cfirst_east_in = new SVGPath(cfirst_north_in).transform(Rotate.CLOCKWISE)
    .setEdgeWidth(0f);
   SVGPath cfirst_south_in = new SVGPath(cfirst_north_in).transform(Rotate.TWICE)
    .setEdgeWidth(0f);
   SVGPath cfirst_west_in = new SVGPath(cfirst_north_in).transform(Rotate.COUNTERCLOCKWISE)
    .setEdgeWidth(0f);
   
   return new SVGPath[]{
    cfirst_north_out, cfirst_east_out, cfirst_south_out, cfirst_west_out,
    cfirst_north_in, cfirst_east_in, cfirst_south_in, cfirst_west_in
   };
}

private enum Rotate
{
   CLOCKWISE,
   COUNTERCLOCKWISE,
   TWICE,
   FLIP
   // , CW_FLIP, CCW_FLIP, TWICE_FLIP;
}

public static class Segment
{
   public final float[] data;
   
   protected Segment(float[] data)
   {
      this.data = data;
   }
   
   Segment(float[] allData, int dataOffset, boolean from102to60)
   {
      this.data = new float[6];
      for (int i = 0; i < data.length; i++) {
         data[i] = allData[dataOffset + i];
         if (from102to60)
            data[i] /= 1.7f;
      }
   }
   
   Segment(Segment original)
   {
      this(original.data, 0, false);
   }
   
   public void toPath(Path path)
   {
//            switch(verb){
//            case LINE:
//                path.rLineTo(data[0],data[1]);
//                break;
//            case CUBIC:
      path.rCubicTo(data[0], data[1], data[2], data[3], data[4], data[5]);
//                break;
//            }
   }
   
   private void turn(int i1, int i2)
   {
      float tmp = data[i1];
      data[i1] = data[i2];
      data[i2] = tmp;
   }
   
   /**
    * Turn this Segment from NORTH to EAST
    */
   void rotateCW()
   {
      // exchange x and y values:
      turn(0, 1);
      turn(2, 3);
      turn(4, 5);
      // invert x-values (previously y-values):
      data[0] = -data[0];
      data[2] = -data[2];
      data[4] = -data[4];
   }
   
   
   /**
    * Turn this Segment from NORTH to WEST
    */
   void rotateCCW()
   {
      // exchange x and y values:
      turn(0, 1);
      turn(2, 3);
      turn(4, 5);
      // invert y-values (previously x-values):
      data[1] = -data[1];
      data[3] = -data[3];
      data[5] = -data[5];
   }
   
   /**
    * Turn this Segment from NORTH to SOUTH
    */
   void rotateTwice()
   {
      // invert x-values:
      data[0] = -data[0];
      data[2] = -data[2];
      data[4] = -data[4];
      // invert y-values:
      data[1] = -data[1];
      data[3] = -data[3];
      data[5] = -data[5];
   }
   
   /**
    * Mirror this Segment, turning it from EdgeType OUT to IN.
    */
   void flip()
   {
      // invert y-values:
      data[1] = -data[1];
      data[3] = -data[3];
      data[5] = -data[5];
   }
} // class Segment

private static class WholeEdge
 extends Segment
{
   WholeEdge()
   {
      super(new float[]{120f, 0f});
   }
   
   void rotateTwice()
   {
      data[0] = -data[0];
   }
   
   void rotateCCW()
   {
      data[1] = -data[0];
      data[0] = 0f;
   }
   
   void rotateCW()
   {
      data[1] = data[0];
      data[0] = 0f;
   }
   
   void flip()
   {
      // do nothing.
   }
   
   public void toPath(Path path)
   {
      path.rLineTo(data[0], data[1]);
   }
}

private final ArrayList<Segment> list;
public float width = 0f;

private SVGPath()
{
   list = new ArrayList<>(1);
   list.add(new WholeEdge());
}

public static SVGPath getWholeEdge(AbstractPiece.Direction dir)
{
   SVGPath ret = new SVGPath();
   switch (dir) {
   case EAST:
      ret.transform(Rotate.CLOCKWISE);
      break;
   case SOUTH:
      ret.transform(Rotate.TWICE);
      break;
   case WEST:
      ret.transform(Rotate.COUNTERCLOCKWISE);
   }
   return ret;
}

public SVGPath(SVGPath original)
{
   list = new ArrayList<>();
   for (Segment segment: original.list) {
      list.add(new Segment(segment));
   }
}

private SVGPath(float[] allData)
{
   list = new ArrayList<>(4); // 4 segments
   for (int i = 0; i < allData.length; i += 6) { // 6 float values per segment (rCubicTo)
      list.add(new Segment(allData, i, true));
   }
}

private SVGPath setEdgeWidth(float f)
{
   width = f;
   return this;
}

public SVGPath transform(Rotate r)
{
   for (Segment segm: list) {
      switch (r) {
      case FLIP:
         segm.flip();
         break;
      case CLOCKWISE:
         segm.rotateCW();
         break;
      case COUNTERCLOCKWISE:
         segm.rotateCCW();
         break;
      case TWICE:
         segm.rotateTwice();
      }
   }
   return this;
}

public SVGPath append(SVGPath nextPath)
{
   list.addAll(nextPath.list);
   return this;
}

public Path toPath(float startX, float startY)
{
   Path ret = new Path();
   ret.moveTo(startX, startY);
   //i = 0;
   for (Segment segment: list) {
      segment.toPath(ret);
   }
   ret.close();
   return ret;
}
}
