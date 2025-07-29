package github.adjustamat.jigsawpuzzlefloss.ui;

import android.graphics.Point;

import androidx.annotation.DrawableRes;

import github.adjustamat.jigsawpuzzlefloss.game.Direction;

public class BorderDrawable
{

public static int
 PRIMITIVE_CORNER,
 PRIMITIVE_INNERCORNER,
 PRIMITIVE_SIDE_I3 /* length 120 = 50 + 20 + 50 */,
 PRIMITIVE_SHORT_I2_CCW /* length 70 = 50 + 20 */,
 PRIMITIVE_SHORT_I2_CW /* length 70 = 20 + 50 */,
 PRIMITIVE_INNER_I1 /* length 20 */,
 PRIMITIVE_BGSIDE;

private final @DrawableRes int drawable;
private final Point location;
private BorderDrawable next;
public int rotateCorner = 0; // TODO: (rotateCorner % 4)
public int rotateAndMove = 0; // TODO: ((rotateAndMove + rotateAllFromWest) % 4)
public int rotateAndMoveAllFromWest = 0;
//public @NonNull Direction rotationFromNW = Direction.NORTH;
//public @NonNull Direction turnAndMove = Direction.NORTH;
//public @NonNull Direction iHeadingTurnAndMoveAll = Direction.WEST;

// TODO: GradientBorderDrawable and OutlineHiliteBorder(works more like Jedge)

// TODO: create methods drawMe(int rotateAndMoveAll) and drawAll()

private BorderDrawable(
 //TODO: @DrawableRes
 int drawable, int x, int y)
{
   this.drawable = drawable;
   this.location = new Point(x, y);
}

private BorderDrawable concat(BorderDrawable other)
{
   if (next == null) {
      next = other;
      return this;
   }
   if (other.next == null) {
      other.next = this;
      return other;
   }
   next.concat(other);
   return this;
}

private BorderDrawable rotateMeCW()
{
   // TODO! only rotate around center of drawable, not center of SinglePiece!
   rotateCorner++;//rotationFromNW = rotationFromNW.next();
   return this;
}

private BorderDrawable rotateMeCCW()
{
   // TODO! only rotate around center of drawable, not center of SinglePiece!
   rotateCorner--;//rotationFromNW = rotationFromNW.prev();
   return this;
}

private BorderDrawable turnMe(Direction turnMove)
{
   // TODO! make sure to have the correct top-left coordinates - depends not only on x and y!
   //  rotate/flip around center of SinglePiece, not center of drawable!
   rotateAndMove += turnMove.ordinal();//turnAndMove = turnAndMove.rotated(turnMove);
   
   // TODO: if the drawable (CORNER or INNERCORNER) is NW then:
   //  NORTH is no rotation
   //  EAST is rotate CW or flip horizontally (to NE)
   //  WEST is rotate CCW or flip vertically (to SW)
   //  SOUTH is rotate 180 or flip twice (to SE).
   
   // TODO: if the drawable (I1, I2, I3) is W then:
   //  NORTH is no rotation
   //  EAST is rotate CW (to N) (and move)
   //  WEST is rotate CCW (to S) (and move)
   //  SOUTH is rotate 180 (to E) (and move).
   return this;
}

private BorderDrawable allIHeadedTowards_AndPrev(Direction direction)
{
   // TODO!
   //  rotate/flip around center of SinglePiece, not center of drawable!
   
   rotateAndMoveAllFromWest = direction.next().ordinal();//iHeadingTurnAndMoveAll = direction;
   
   // original position and rotation for primitives: // TODO: ?? (this is "NORTH" == "no rotation")
   // NW: PRIMITIVE_CORNER,
   //     PRIMITIVE_INNERCORNER,
   // W: PRIMITIVE_SIDE_I3 /* length 120 = 50 + 20 + 50 */,
   //    PRIMITIVE_SHORT_I2_CCW /* length 70 = 50 + 20 */,
   //    PRIMITIVE_SHORT_I2_CW /* length 70 = 20 + 50 */,
   //    PRIMITIVE_INNER_I1 /* length 20 */,
   // E: PRIMITIVE_BGSIDE;

//   if (next != null)
//      next.turnAll(direction);
   return this;
}

// PRIVATE: crnr (primitive + turnMe)
private static BorderDrawable crnr(int x, int y, Direction turnFromNW/*boolean north, boolean west*/)
{
   return cornerNW(x, y)
    .turnMe(turnFromNW);
}

public static BorderDrawable cornerSE(int x, int y)
{
   return crnr(x, y, Direction.SOUTH);
}

public static BorderDrawable cornerSW(int x, int y)
{
   return crnr(x, y, Direction.WEST);
}

public static BorderDrawable cornerNE(int x, int y)
{
   return crnr(x, y, Direction.EAST);
}

public static BorderDrawable cornerNW(int x, int y)
{
   return new BorderDrawable(PRIMITIVE_CORNER, x, y);
   //crnr(x, y, Direction.NORTH);
}

public static BorderDrawable side(int x, int y, Direction dir)
{
   return new BorderDrawable(PRIMITIVE_SIDE_I3, x, y)
    .concat(new BorderDrawable(PRIMITIVE_BGSIDE, x + dir.x, y + dir.y))
    .allIHeadedTowards_AndPrev(dir);
}

public static BorderDrawable sideShortLeft(int x, int y, Direction dir)
{
   return new BorderDrawable(PRIMITIVE_SHORT_I2_CCW, x, y)
    .concat(new BorderDrawable(PRIMITIVE_CORNER, x, y).rotateMeCCW()) // TODO: corner?
    
    .concat(new BorderDrawable(PRIMITIVE_BGSIDE, x + dir.x, y + dir.y))
    .allIHeadedTowards_AndPrev(dir);
}

public static BorderDrawable sideShortRight(int x, int y, Direction dir)
{
   return new BorderDrawable(PRIMITIVE_SHORT_I2_CW, x, y)
    .concat(crnr(x, y, Direction.WEST).rotateMeCW()) // TODO: ?
    // new BorderDrawable(PRIMITIVE_CORNER, x, y).rotateCW().turn(Direction.WEST) // TODO: ?
    .concat(new BorderDrawable(PRIMITIVE_BGSIDE, x + dir.x, y + dir.y))
    .allIHeadedTowards_AndPrev(dir);
}

// PRIVATE: i1side (i1 + bgside + 2x turnMe)
private static BorderDrawable i1side(int x, int y, Direction dir)
{
   return new BorderDrawable(PRIMITIVE_INNER_I1, x, y).turnMe(dir)
    .concat(new BorderDrawable(PRIMITIVE_BGSIDE, x + dir.x, y + dir.y).turnMe(dir));
}

public static BorderDrawable innerShapeO(int x, int y)
{
   // also contains 4 quarter backgrounds:
//        /\
//       /**\
//     /i/''\i\
//    /*|    |*\
//    \*|    |*/
//     \i\__/i/
//       \**/
//        \/
   // TODO: or have a SHAPE_O primitive
   return i1side(x, y, Direction.NORTH)
    .concat(i1side(x, y, Direction.EAST))
    .concat(i1side(x, y, Direction.WEST))
    .concat(i1side(x, y, Direction.SOUTH))
    .concat(new BorderDrawable(PRIMITIVE_INNERCORNER, x, y)) // NW
    .concat(new BorderDrawable(PRIMITIVE_INNERCORNER, x, y).turnMe(Direction.EAST)) // NE
    .concat(new BorderDrawable(PRIMITIVE_INNERCORNER, x, y).turnMe(Direction.WEST)) // SW
    .concat(new BorderDrawable(PRIMITIVE_INNERCORNER, x, y).turnMe(Direction.SOUTH)); // SE
}

public static BorderDrawable innerShapeU(int x, int y, Direction dir)
{
   // also contains 3 quarter backgrounds:
//     /|    |\   // continues up with single(s) or corner(s)
//    /*|    |*\
//    \*|    |*/
//     \i\__/i/
//      \****/
//        \/
   // TODO: or have a SHAPE_U primitive
   Direction prev = dir.prev();
   Direction next = dir.next();
   return new BorderDrawable(PRIMITIVE_SHORT_I2_CW, x, y).turnMe(Direction.WEST) // CCW == S
    .concat(new BorderDrawable(PRIMITIVE_BGSIDE, x + prev.x, y + prev.y))
    .concat(new BorderDrawable(PRIMITIVE_SHORT_I2_CW, x, y).turnMe(Direction.EAST)) // CW == N
    .concat(new BorderDrawable(PRIMITIVE_BGSIDE, x + next.x, y + next.y))
    .concat(new BorderDrawable(PRIMITIVE_INNERCORNER, x, y).turnMe(Direction.EAST)) // NE
    .concat(new BorderDrawable(PRIMITIVE_INNERCORNER, x, y).turnMe(Direction.SOUTH)) // SE
    .allIHeadedTowards_AndPrev(dir) // TODO: turnAll should be last!
    .concat(i1side(x, y, dir.opposite()));
}

public static BorderDrawable innerShapeUShort(int x, int y, Direction dir)
{
   // also contains 3 quarter backgrounds:
//     /c    c\   // ends here, StraightEdges (EdgeJedge) left and right!
//    /*|    |*\
//    \*|    |*/
//     \i\__/i/
//      \****/
//       \**/
   // TODO: or have a SHAPE_U_SHORT primitive
   Direction prev = dir.prev();
   Direction next = dir.next();
   return new BorderDrawable(PRIMITIVE_CORNER, x, y).rotateMeCW() // TODO: corner?
    .concat(crnr(x, y, Direction.WEST).rotateMeCCW()) // TODO: ?
    .concat(new BorderDrawable(PRIMITIVE_INNERCORNER, x, y).turnMe(Direction.EAST)) // NE
    .concat(new BorderDrawable(PRIMITIVE_INNERCORNER, x, y).turnMe(Direction.SOUTH)) // SE
    .allIHeadedTowards_AndPrev(dir) // TODO: turnAll should be last!
    .concat(i1side(x, y, prev))
    .concat(i1side(x, y, next))
    .concat(i1side(x, y, dir.opposite()));
}

public static BorderDrawable innerShapeL(int x, int y, Direction dirAndPrev)
{
   Direction prev = dirAndPrev.prev();
   return new BorderDrawable(PRIMITIVE_SHORT_I2_CCW, x, y).turnMe(Direction.WEST) // CCW == S
    .concat(new BorderDrawable(PRIMITIVE_INNERCORNER, x, y).turnMe(Direction.WEST)) // SW
    .concat(new BorderDrawable(PRIMITIVE_SHORT_I2_CW, x, y))
    .concat(new BorderDrawable(PRIMITIVE_BGSIDE, x + prev.x, y + prev.y))
    .concat(new BorderDrawable(PRIMITIVE_BGSIDE, x + dirAndPrev.x, y + dirAndPrev.y))
    .allIHeadedTowards_AndPrev(dirAndPrev);
}

public static BorderDrawable innerShapeLShortLeft(int x, int y, Direction dirAndPrev)
{
   // also contains two quarter backgrounds
   Direction prev = dirAndPrev.prev();
   return new BorderDrawable()
    
    .allIHeadedTowards_AndPrev(dirAndPrev) // TODO: turnAll should be last!
    .concat(i1side(x, y, dirAndPrev));
}

public static BorderDrawable innerShapeLShortRight(int x, int y, Direction dirAndPrev)
{
   // also contains two quarter backgrounds
   Direction prev = dirAndPrev.prev();
   return new BorderDrawable()
    
    .allIHeadedTowards_AndPrev(dirAndPrev) // TODO: turnAll should be last!
    .concat(i1side(x, y, prev));
}
}
