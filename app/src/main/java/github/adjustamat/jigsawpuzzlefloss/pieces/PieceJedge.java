package github.adjustamat.jigsawpuzzlefloss.pieces;

import android.graphics.Path;
import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Random;

import github.adjustamat.jigsawpuzzlefloss.game.Direction;

/**
 * A jigsaw edge, or straight edge for a puzzle "edge piece" (see {@link AbstractPiece#isEdgePiece()}), being one of four sides of a SinglePiece.
 */
public abstract class PieceJedge
// implements Parcelable
{
private static final Line STRAIGHT_NORTH = new Line(new float[]{120f, 0f});
private static final Line STRAIGHT_EAST = new Line(new float[]{0f, 120f});
private static final Line STRAIGHT_SOUTH = new Line(new float[]{-120f, 0f});
private static final Line STRAIGHT_WEST = new Line(new float[]{0f, -120f});
public static final float STRAIGHT_EDGE_WIDTH = 0f;

//public static final Creator<PieceJedge> CREATOR = new Creator<PieceJedge>()
//{
//   /**
//    * {@link JedgeParams#getDoubleJedge(ImagePuzzle, Direction)}
//    * @param in The Parcel to read the object's data from.
//    * @return an EdgeJedge or DoubleJedge
//    */
//   @Override
//   public PieceJedge createFromParcel(Parcel in)
//   {
//      switch (in.readInt()) {
//      case 0:
//         return new EdgeJedge(STRAIGHT_NORTH);
//      case 1:
//         return new EdgeJedge(STRAIGHT_EAST);
//      case 2:
//         return new EdgeJedge(STRAIGHT_SOUTH);
//      case 3:
//         return new EdgeJedge(STRAIGHT_WEST);
//      default:
//         // TODO: here's all the trouble. cannot create a DoubleJedge from parcel, need to use
//         //  JedgeParams.getDoubleJedge() !!
//         return ;
//      }
//   }
//
//   @Override
//   public PieceJedge[] newArray(int size)
//   {
//      return new PieceJedge[size];
//   }
//};

public static EdgeJedge getEdgeJedge(Direction d)
{
   switch (d) {
   case NORTH:
      return new EdgeJedge(STRAIGHT_NORTH);
   case EAST:
      return new EdgeJedge(STRAIGHT_EAST);
   case SOUTH:
      return new EdgeJedge(STRAIGHT_SOUTH);
   default: // case WEST:
      return new EdgeJedge(STRAIGHT_WEST);
   }
}

private static HalfJedge[][] pool;

/**
 * Returns the pool from which to get jigsaw edges with desired neckWidth (NECK) and curvature (CURV) like this:
 * pool [ NECK*6 + CURV*2 + (secondHalf?1:0) ]  [ (inward?4:0) + direction_ordinal ]
 * @return the pool of pre-rotated jigsaw edges, first and second half, pointing inward and outward.
 */
public static HalfJedge[][] getAllJigsawEdges()
{
   if (pool == null)
      pool = generateAllJigsawEdges();
   return pool;
}

private static HalfJedge[][] generateAllJigsawEdges()
{
   HalfJedge[][] ret = new HalfJedge[3 * 3 * 2][];
   int reti = 0;
   for (float neckWidth = 0f; neckWidth < 13f; neckWidth += 5f) { // from narrow to wide
      for (float curvature = 0f; curvature < 6.25f; curvature += 2.5f) { // from straight to bent
         ret[reti++] = generateFirstHalf(neckWidth, curvature);
         ret[reti++] = generateSecondHalf(neckWidth, curvature);
      }
   }
   return ret;
}

private static HalfJedge[] generateFirstHalf(float neckWidth, float curvature)
{
   HalfJedge cfirst_north_out = new HalfJedge(
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
   HalfJedge cfirst_east_out = cfirst_north_out.getTransformed(Direction.EAST);
   HalfJedge cfirst_south_out = cfirst_north_out.getTransformed(Direction.SOUTH);
   HalfJedge cfirst_west_out = cfirst_north_out.getTransformed(Direction.WEST);
   
   HalfJedge cfirst_north_in = cfirst_north_out.getTransformed(Direction.NORTH);
   cfirst_north_in.jigBreadth = curvature;
   HalfJedge cfirst_east_in = cfirst_north_in.getTransformed(Direction.EAST);
   HalfJedge cfirst_south_in = cfirst_north_in.getTransformed(Direction.SOUTH);
   HalfJedge cfirst_west_in = cfirst_north_in.getTransformed(Direction.WEST);
   
   return new HalfJedge[]{cfirst_north_out, cfirst_east_out, cfirst_south_out, cfirst_west_out,
    cfirst_north_in, cfirst_east_in, cfirst_south_in, cfirst_west_in};
}

private static HalfJedge[] generateSecondHalf(float neckWidth, float curvature)
{
   HalfJedge csecond_north_out = new HalfJedge(
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
   HalfJedge csecond_east_out = csecond_north_out.getTransformed(Direction.EAST);
   HalfJedge csecond_south_out = csecond_north_out.getTransformed(Direction.SOUTH);
   HalfJedge csecond_west_out = csecond_north_out.getTransformed(Direction.WEST);
   
   HalfJedge csecond_north_in = csecond_north_out.getTransformed(Direction.NORTH);
   csecond_north_in.jigBreadth = curvature;
   HalfJedge csecond_east_in = csecond_north_in.getTransformed(Direction.EAST);
   HalfJedge csecond_south_in = csecond_north_in.getTransformed(Direction.SOUTH);
   HalfJedge csecond_west_in = csecond_north_in.getTransformed(Direction.WEST);
   
   return new HalfJedge[]{csecond_north_out, csecond_east_out, csecond_south_out, csecond_west_out,
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
   return subPieceDir.rotated(rotate);
}

public PieceJedge setSubPiece(Point subPiece, Direction dir)
{
   this.subPiece = subPiece;
   this.subPieceDir = dir;
   return this;
}

private PieceJedge nextLink;
private PieceJedge prevLink;

public PieceJedge getNext()
{
   return nextLink;
}

public PieceJedge getPrev()
{
   return prevLink;
}

public PieceJedge linkNext(PieceJedge next)
{
   this.nextLink = next;
   next.prevLink = this;
   return this;
}

public abstract void appendSegmentsTo(Path path);
public abstract float getJigBreadth();

/**
 * Two HalfEdges makes a DoubleJedge.
 */
public static class DoubleJedge
 extends PieceJedge
{
   private final HalfJedge half1, half2;
   
   public DoubleJedge(HalfJedge firstHalf, HalfJedge secondHalf)
   {
      half1 = firstHalf;
      half2 = secondHalf;
   }
   
   public void appendSegmentsTo(Path path)
   {
      half1.appendHalfEdgeTo(path);
      half2.appendHalfEdgeTo(path);
   }
   
   public float getJigBreadth()
   {
      return Math.max(half1.jigBreadth, half2.jigBreadth);
   }
   
   public int describeContents()
   {
      return 0;
   }
   
   public void writeToParcel(@NonNull Parcel dest, int flags)
   {
      // TODO!
   }
}

/**
 * The straight edge of an "edge piece"
 * @see AbstractPiece#isEdgePiece()
 */
public static class EdgeJedge
 extends PieceJedge
{
   Line segm;
   
   private EdgeJedge(Line data)
   {
      this.segm = data;
   }
   
   public void appendSegmentsTo(Path path)
   {
      path.rLineTo(segm.data[0], segm.data[1]);
   }
   
   public float getJigBreadth()
   {
      return STRAIGHT_EDGE_WIDTH;
   }
   
   public int describeContents()
   {
      return 0;
   }
   
   public void writeToParcel(@NonNull Parcel dest, int flags)
   {
      if (segm == STRAIGHT_NORTH) {
         dest.writeInt(0);
      }
      else if (segm == STRAIGHT_EAST) {
         dest.writeInt(1);
      }
      else if (segm == STRAIGHT_SOUTH) {
         dest.writeInt(2);
      }
      else {// STRAIGHT_WEST:
         dest.writeInt(3);
      }
   }
}

/**
 * A Line is a horizontal or vertical SVG line segment.
 */
private static class Line
{
   private final float[] data;
   
   private Line(float[] data)
   {
      this.data = data;
   }
}

/**
 * A HalfJedge is a jigsaw edge, generated for the pool, made up of Cubic SVG segments. It is one half of a DoubleJedge.
 */
public static class HalfJedge
{
   private final ArrayList<Cubic> segments;
   float jigBreadth;
   
   /**
    * Create a NORTH OUT HalfJedge.
    * @param allData data for all Cubic segments of this HalfJedge.
    */
   private HalfJedge(float[] allData, float jigBreadth)
   {
      this.jigBreadth = jigBreadth;
      segments = new ArrayList<>(4); // 4 segments
      for (int i = 0; i < allData.length; i += 6) { // 6 float values per segment (rCubicTo)
         segments.add(new Cubic(allData, i));
      }
   }
   
   private HalfJedge(ArrayList<Cubic> segments, float jigBreadth)
   {
      this.jigBreadth = jigBreadth;
      this.segments = segments;
   }
   
   public void appendHalfEdgeTo(Path path)
   {
      for (Cubic segm: segments) {
         segm.appendSegmentTo(path);
      }
   }
   
   private HalfJedge getTransformed(Direction dir)
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
      return new HalfJedge(ret, jigBreadth);
   }
} // class HalfJedge

/**
 * A Cubic is a cubic SVG curve segment. Four such segments make up a HalfJedge.
 */
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

public static class JedgeParams
 implements Parcelable
{
   public final boolean in;
   public final int curv1, curv2;
   public final int neck1, neck2;
   private DoubleJedge doubleJedge;
   
   @Override
   public void writeToParcel(Parcel dest, int flags)
   {
      dest.writeInt(in ?1 :0);
      dest.writeInt(curv1);
      dest.writeInt(curv2);
      dest.writeInt(neck1);
      dest.writeInt(neck2);
   }
   
   @Override
   public int describeContents()
   {
      return 0;
   }
   
   public static final Creator<JedgeParams> CREATOR = new Creator<JedgeParams>()
   {
      @Override
      public JedgeParams createFromParcel(Parcel in)
      {
         return new JedgeParams(in);
      }
      
      @Override
      public JedgeParams[] newArray(int size)
      {
         return new JedgeParams[size];
      }
   };
   
   public JedgeParams(Parcel in)
   {
      this.in = in.readInt() == 1;
      curv1 = in.readInt();
      curv2 = in.readInt();
      neck1 = in.readInt();
      neck2 = in.readInt();
   }
   
   public JedgeParams(Random rng)
   {
      in = rng.nextBoolean();
      curv1 = rng.nextInt(3);
      curv2 = rng.nextInt(3);
      neck1 = rng.nextInt(3);
      neck2 = rng.nextInt(3);
   }
   
   /*
   TODO: perhaps we don't need VectorJedges at all, just this method, and a buffer in LargerPiece with calculated outerJedges and holes.
    */
   public @NonNull DoubleJedge getDoubleJedge(Direction dir){
      if(doubleJedge==null){
         getAllJigsawEdges(); // generate pool if not already generated.
         int rotationIndex;
         switch (dir) {
         case EAST: case SOUTH:
            rotationIndex = (in ?4 :0) + dir.ordinal();
            doubleJedge = new DoubleJedge(
             pool[neck1 * 6 + curv1 * 2][rotationIndex],
             pool[neck2 * 6 + curv2 * 2 + 1][rotationIndex]
            );
         default: // WEST: NORTH:
            // in/out is flipped:
            rotationIndex = (in ?0 :4) + dir.ordinal();
            doubleJedge = new DoubleJedge(
             // first/second is flipped:
             pool[neck2 * 6 + curv2 * 2][rotationIndex],
             pool[neck1 * 6 + curv1 * 2 + 1][rotationIndex]
            );
         }
      }
      return doubleJedge;
   }
} // class JedgeParams
}
