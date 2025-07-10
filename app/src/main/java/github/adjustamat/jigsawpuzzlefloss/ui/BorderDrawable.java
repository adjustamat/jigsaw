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

private BorderDrawable(//@DrawableRes
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

private BorderDrawable rotateCW()
{
   // TODO! only rotate around center of drawable, not center of SinglePiece!
   return this;
}

private BorderDrawable rotateCCW()
{
   // TODO! only rotate around center of drawable, not center of SinglePiece!
   return this;
}

private BorderDrawable turn(Direction direction)
{
   // TODO!
   //  rotate/flip around center of SinglePiece, not center of drawable!
   
   // original position and rotation for primitives: (this is "NORTH" == "no rotation")
   // NW: PRIMITIVE_CORNER,
   //     PRIMITIVE_INNERCORNER,
   // W: PRIMITIVE_SIDE_I3 /* length 120 = 50 + 20 + 50 */,
   //    PRIMITIVE_SHORT_I2_CCW /* length 70 = 50 + 20 */,
   //    PRIMITIVE_SHORT_I2_CW /* length 70 = 20 + 50 */,
   //    PRIMITIVE_INNER_I1 /* length 20 */,
   // E: PRIMITIVE_BGSIDE;
   
   if (next != null)
      next.turn(direction);
   return this;
}

private BorderDrawable flip(boolean northSouth, boolean westEast)
{
   // TODO! make sure to have the correct top-left coordinates - depends not only on x and y! (see corner method)
   //  rotate/flip around center of SinglePiece, not center of drawable!
   
   // TODO: if the drawable is nw then:
   //  false,false is no rotation
   //  false,true is flip horizontally (to ne)
   //  true,false is flip vertically (to sw)
   //  true,true is rotate 180 or flip twice (to se).

//   if (next != null)
//      next.flip(northSouth, westEast);
   return this;
}

public static BorderDrawable side(int x, int y, Direction dir)
{
   return new BorderDrawable(PRIMITIVE_SIDE_I3, x, y)
    .concat(new BorderDrawable(PRIMITIVE_BGSIDE, x + dir.x, y + dir.y))
    .turn(dir);
}

public static BorderDrawable sideShortLeft(int x, int y, Direction dir)
{
   return new BorderDrawable(PRIMITIVE_SHORT_I2_CCW, x, y)
    .concat(new BorderDrawable(PRIMITIVE_CORNER, x, y).rotateCCW())
    .concat(new BorderDrawable(PRIMITIVE_BGSIDE, x + dir.x, y + dir.y))
    .turn(dir);
}

public static BorderDrawable sideShortRight(int x, int y, Direction dir)
{
   return new BorderDrawable(PRIMITIVE_SHORT_I2_CW, x, y)
    .concat(corner(x, y, false, true).rotateCW())
    
    // new BorderDrawable(PRIMITIVE_CORNER, x, y).rotateCW().turn(Direction.WEST)
    .concat(new BorderDrawable(PRIMITIVE_BGSIDE, x + dir.x, y + dir.y))
    .turn(dir);
}

private static BorderDrawable corner(int x, int y, boolean north, boolean west)
{
   // TODO: if the drawable is nw then:
   //  true,true is no rotation
   //  true,false is flip horizontally (to ne, rotate CW)
   //  false,true is flip vertically (to sw, rotate CCW)
   //  false,false is rotate 180 or flip twice (to se).
   
   // TODO in flip: make sure to have the correct top-left coordinates - depends not only on x and y!
   return new BorderDrawable(PRIMITIVE_CORNER, x, y)
    .flip(!north, !west);
}

public static BorderDrawable cornerSE(int x, int y)
{
   return corner(x, y, false, false);
}

public static BorderDrawable cornerSW(int x, int y)
{
   return corner(x, y, false, true);
}

public static BorderDrawable cornerNE(int x, int y)
{
   return corner(x, y, true, false);
}

public static BorderDrawable cornerNW(int x, int y)
{
   return corner(x, y, true, true);
}

private static BorderDrawable innerSide(int x, int y, Direction dir)
{
   return new BorderDrawable(PRIMITIVE_INNER_I1, x, y)
    .concat(new BorderDrawable(PRIMITIVE_BGSIDE, x + dir.x, y + dir.y))
    .turn(dir);
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
   return innerSide(x, y, Direction.NORTH) // "NORTH" == no rotation == W
    .concat(innerSide(x, y, Direction.WEST)) // turn CCW ==> S
    .concat(innerSide(x, y, Direction.SOUTH)) // turn twice ==> E
    .concat(innerSide(x, y, Direction.EAST)) // turn CW ==> N
    .concat(new BorderDrawable(PRIMITIVE_INNERCORNER, x, y)) // NW
    .concat(new BorderDrawable(PRIMITIVE_INNERCORNER, x, y).flip(false, true)) // NE
    .concat(new BorderDrawable(PRIMITIVE_INNERCORNER, x, y).flip(true, false)) // SW
    .concat(new BorderDrawable(PRIMITIVE_INNERCORNER, x, y).flip(true, true)); // SE
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
   return new BorderDrawable(PRIMITIVE_SHORT_I2_CW, x, y).turn(Direction.WEST) // CCW == S
    .concat(new BorderDrawable(PRIMITIVE_BGSIDE, x + prev.x, y + prev.y))
    .concat(new BorderDrawable(PRIMITIVE_SHORT_I2_CW, x, y).turn(Direction.EAST)) // CW == N
    .concat(new BorderDrawable(PRIMITIVE_BGSIDE, x + next.x, y + next.y))
    .concat(new BorderDrawable(PRIMITIVE_INNERCORNER, x, y).flip(false, true)) // NE
    .concat(new BorderDrawable(PRIMITIVE_INNERCORNER, x, y).flip(true, true)) // SE
    .turn(dir)
    .concat(innerSide(x, y, dir.opposite()));
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
   Direction prev = dir.prev();
   Direction next = dir.next();
   return new BorderDrawable(PRIMITIVE_CORNER, x, y).rotateCW()
    .concat(corner(x, y, false, true).rotateCCW())
    .concat(new BorderDrawable(PRIMITIVE_INNERCORNER, x, y).flip(false, true)) // NE
    .concat(new BorderDrawable(PRIMITIVE_INNERCORNER, x, y).flip(true, true)) // SE
    .turn(dir)
    .concat(innerSide(x, y, prev))
    .concat(innerSide(x, y, next))
    .concat(innerSide(x, y, dir.opposite()));
}

public static BorderDrawable innerShapeL(int x, int y, Direction dirAndPrev)
{
   Direction prev = dirAndPrev.prev();
   return new BorderDrawable(PRIMITIVE_SHORT_I2_CCW, x, y).turn(Direction.WEST) // CCW == S
    .concat(new BorderDrawable(PRIMITIVE_INNERCORNER, x, y).flip(true, false)) // SW
    .concat(new BorderDrawable(PRIMITIVE_SHORT_I2_CW, x, y))
    .concat(new BorderDrawable(PRIMITIVE_BGSIDE, x + prev.x, y + prev.y))
    .concat(new BorderDrawable(PRIMITIVE_BGSIDE, x + dirAndPrev.x, y + dirAndPrev.y))
    .turn(dirAndPrev);
}

public static BorderDrawable innerShapeLShortLeft(int x, int y, Direction dirAndPrev)
{
   // also contains two quarter backgrounds
   Direction prev = dirAndPrev.prev();
   return new BorderDrawable()
    
    .turn(dirAndPrev)
    .concat(innerSide(x, y, dirAndPrev));
}

public static BorderDrawable innerShapeLShortRight(int x, int y, Direction dirAndPrev)
{
   // also contains two quarter backgrounds
   Direction prev = dirAndPrev.prev();
   return new BorderDrawable()
    
    .turn(dirAndPrev);
    .concat(innerSide(x, y, prev));
}
}
