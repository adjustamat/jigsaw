package github.adjustamat.jigsawpuzzlefloss.ui;

import github.adjustamat.jigsawpuzzlefloss.game.Direction;

public class BorderDrawable
{

final int var = 1;

private BorderDrawable concat(BorderDrawable other)
{
   return new BorderDrawable();
}

private BorderDrawable turn(Direction direction)
{
   return this;
}

private BorderDrawable flip(boolean northSouth, boolean westEast)
{
   return this;
}

public static BorderDrawable single(int x, int y, Direction dir)
{
   // also contains a quarter background
   return new BorderDrawable();
}

public static BorderDrawable singleShortLeft(int x, int y, Direction dir)
{
   // also contains a quarter background
   return new BorderDrawable();
}

public static BorderDrawable singleShortRight(int x, int y, Direction dir)
{
   // also contains a quarter background
   return new BorderDrawable();
}

private static BorderDrawable corner(int x, int y, boolean north, boolean west)
{
   // TODO: if the drawable is nw then:
   //  true,true is no rotation
   //  true,false is flip horizontally
   //  false,true is flip vertically
   //  false,false is rotate 180 or flip twice.
   
   // TODO: make sure to have the correct top-left coordinates - depends not only on x and y!
   return new BorderDrawable()
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

public static BorderDrawable innerShapeO(int x, int y)
{
   // also contains 4 quarter backgrounds:
//        /\
//       /**\
//     /f/''\f\
//    /*|    |*\
//    \*|    |*/
//     \f\__/f/
//       \**/
//        \/
   return new BorderDrawable();
}

public static BorderDrawable innerShapeU(int x, int y, Direction dir)
{
   // also contains 3 quarter backgrounds:
//     /|    |\   // continues up with single(s) or corner(s)
//    /*|    |*\
//    \*|    |*/
//     \f\__/f/
//      \****/
//        \/
   return new BorderDrawable()
    .turn(dir);
}

public static BorderDrawable innerShapeL(int x, int y, Direction d1, Direction d2)
{
   // also contains two quarter backgrounds
   return new BorderDrawable();
}

public static BorderDrawable innerShapeUShort(int x, int y, Direction dir)
{
   // also contains 3 quarter backgrounds:
//     /c    c\   // ends here, StraightEdges left and right!
//    /*|    |*\
//    \*|    |*/
//     \f\__/f/
//      \****/
//       \**/
   return new BorderDrawable()
    .concat(cornerNW(x, y - 1))
    .concat(cornerNE(x, y - 1))
    .turn(dir);
}

public static BorderDrawable innerShapeLShortLeft(int x, int y, Direction d1, Direction d2)
{
   // also contains two quarter backgrounds
   return new BorderDrawable();
}

public static BorderDrawable innerShapeLShortRight(int x, int y, Direction d1, Direction d2)
{
   // also contains two quarter backgrounds
   return new BorderDrawable();
}
}
