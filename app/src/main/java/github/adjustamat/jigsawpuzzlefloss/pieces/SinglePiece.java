package github.adjustamat.jigsawpuzzlefloss.pieces;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Parcel;

import github.adjustamat.jigsawpuzzlefloss.containers.Box.GroupOrSinglePiece;
import github.adjustamat.jigsawpuzzlefloss.containers.Container;
import github.adjustamat.jigsawpuzzlefloss.game.Direction;
import github.adjustamat.jigsawpuzzlefloss.game.ImagePuzzle;
import github.adjustamat.jigsawpuzzlefloss.game.ImagePuzzle.RandomJedges;
import github.adjustamat.jigsawpuzzlefloss.pieces.PieceJedge.HalfJedge;
import github.adjustamat.jigsawpuzzlefloss.pieces.PieceJedge.JedgeParams;

/**
 * A piece of an {@link ImagePuzzle}. Has four edges that are either jigsaw-shaped or flat (at the
 * outer edges of the ImagePuzzle).
 */
public class SinglePiece
 extends AbstractPiece
 implements GroupOrSinglePiece
{
/**
 * The outline to draw when rotating or when drawing embossed 3D-effect.
 */
final SinglePieceJedges vectorJedges;

//    PointF shapeSize;
//   this.shapeSize = new PointF(
//    imageSize + jigBreadth.left + jigBreadth.right,
//    imageSize + jigBreadth.top + jigBreadth.bottom);

class SinglePieceJedges
 extends VectorJedges
{
   final PieceJedge[] nesw = new PieceJedge[4];
   final JedgeParams[] neswParameters = new JedgeParams[4]; // TODO: remove?
   
   public SinglePieceJedges(HalfJedge[][] pool,
    int x, int y, RandomJedges parameters
    //@Nullable JedgeParams north, @Nullable JedgeParams east, @Nullable JedgeParams south, @Nullable JedgeParams west
   )
   {
      neswParameters[0] = parameters.getNorth(x, y);
      neswParameters[1] = parameters.getEast(x, y);
      neswParameters[2] = parameters.getSouth(x, y);
      neswParameters[3] = parameters.getWest(x, y);
      for (Direction d: Direction.values()) { // for(int i = 0; i < 4; i++)
         int i = d.ordinal(); // Direction d = Direction.values()[i];
         nesw[i] = neswParameters[i] != null
          ?neswParameters[i].init(pool, d)
          :PieceJedge.getEdgeJedge(d);
      }
      nesw[3].linkNext(nesw[0].linkNext(nesw[1].linkNext(nesw[2].linkNext(nesw[3]))));
   }
   
   public RectF getJigBreadth()
   {
      return new RectF(
       nesw[3].getJigBreadth(),
       nesw[0].getJigBreadth(),
       nesw[1].getJigBreadth(),
       nesw[2].getJigBreadth()
      );
   }
   
   public int getOuterJedgesCount()
   {
      return 1;
   }
   
   public PieceJedge getFirstJedge(int hole)
   {
      return nesw[0];
   }
   
   protected int width()
   {
      return MAX_BUFFER_SIZE;
   }
   
   protected int height()
   {
      return MAX_BUFFER_SIZE;
   }
} // class SinglePieceJedges

public void serializeSinglePiece(Parcel dest)
{
   super.serializeAbstractPieceFields(dest);
   for (JedgeParams param: vectorJedges.neswParameters) {
      param.writeToParcel(dest, 0);
   }
}

public static SinglePiece deserializeSinglePiece(Parcel in, Container loading, int i,
 HalfJedge[][] pool, RandomJedges randomJedges)
{
   Direction rotation = Direction.values()[in.readInt()];
   Point correct = new Point(in.readInt(), in.readInt());
   PointF relative;
   if (in.readInt() == 0)
      relative = null;
   else
      relative = new PointF(in.readFloat(), in.readFloat());
   boolean lockedRotation = in.readInt() == 0,
    lockedPlace = in.readInt() == 0;

//   JedgeParams north = new JedgeParams(in),
//    east = new JedgeParams(in),
//    south = new JedgeParams(in),
//    west = new JedgeParams(in);
   
   return new SinglePiece(loading, i,
    rotation, correct, relative, lockedRotation, lockedPlace,
    randomJedges,
    //north, east, south, west,
    pool);
}

/**
 * Contructor for deserializing from savegame.
 */
private SinglePiece(Container loading, int indexInContainer,
 Direction rotation, Point correct, PointF relative, boolean lockedRotation, boolean lockedPlace,
 RandomJedges randomJedges,
 //@Nullable JedgeParams north, @Nullable JedgeParams east, @Nullable JedgeParams south, @Nullable JedgeParams west,
 HalfJedge[][] pool
)
{
   super(loading, indexInContainer,
    rotation, correct, relative, lockedRotation, lockedPlace);
   vectorJedges = new SinglePieceJedges(pool, correct.x, correct.y, randomJedges);
}

/**
 * Constructor for ImagePuzzle generator.
 */
public SinglePiece(ImagePuzzle imagePuzzle, int indexInBox,
// Point coordinates,
// @Nullable JedgeParams north, @Nullable JedgeParams east, @Nullable JedgeParams south, @Nullable JedgeParams west,
 int x, int y,
 HalfJedge[][] pool, int randomRotation
)
{
   super(imagePuzzle.singlePiecesContainer, indexInBox,
    Direction.values()[randomRotation], new Point(x, y));
   vectorJedges = new SinglePieceJedges(pool, x, y, imagePuzzle.randomJedges);
}

//public Color getColor(){
//   // TODO: extract color from the vectorJedges part of the ImagePuzzle bitmap.
//}
//
//public Color getHighContrastBgColor()
//{
//   // TODO: make color with high contrast to getColor().
//}

protected VectorJedges getVectorJedges()
{
   return vectorJedges;
}

public RectF getJigBreadth()
{
   // TODO: jigBreadth *= imageSize / SIDE_SIZE;
   return vectorJedges.getJigBreadth();
}

public boolean isNorthPuzzleEdge()
{
   return vectorJedges.neswParameters[0] == null;
}

public boolean isEastPuzzleEdge()
{
   return vectorJedges.neswParameters[1] == null;
}

public boolean isSouthPuzzleEdge()
{
   return vectorJedges.neswParameters[2] == null;
}

public boolean isWestPuzzleEdge()
{
   return vectorJedges.neswParameters[3] == null;
}
}
