package github.adjustamat.jigsawpuzzlefloss.ui;

import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.FillType;
import android.graphics.Shader.TileMode;

import github.adjustamat.jigsawpuzzlefloss.game.Direction;
import github.adjustamat.jigsawpuzzlefloss.game.ImagePuzzle;
import github.adjustamat.jigsawpuzzlefloss.pieces.AbstractPiece.VectorEdges;
import github.adjustamat.jigsawpuzzlefloss.pieces.LargerPiece.LargerPieceEdges;

public class PuzzleGraphics
{
private PuzzleGraphics(){ }

private static final MaskFilter embossMaskFilter = new EmbossMaskFilter(
 new float[]{0.5f, 0.5f, 0.5f}, 0.9f, 8, 1);

private static final Paint piecePaint = new Paint();
private static final Paint outerPaint = new Paint();
private static final Paint innerPaint = new Paint();
private static BitmapShader shader;
private static ImagePuzzle theImagePuzzle;

public static void setOutlineStrokeColor(int r, int g, int b)
{
   outerPaint.setColor(Color.argb(255, r, g, b));
}

public static void init(ImagePuzzle imagePuzzle)
{
   theImagePuzzle = imagePuzzle;
   
   piecePaint.setStyle(Style.FILL); // not FILL_AND_STROKE, because using MaskFilter instead of stroke!
   
   // fill puzzle piece shape with the puzzle image:
   shader = new BitmapShader(imagePuzzle.image, TileMode.CLAMP, TileMode.CLAMP);
   //shader.setFilterMode(BitmapShader.FILTER_MODE_NEAREST);
   piecePaint.setShader(shader);
   
   // draw puzzle piece shape outlines as emboss (3d-effect)
   piecePaint.setMaskFilter(embossMaskFilter);
   
   // Paint for drawing outline when rotating:
   outerPaint.setStyle(Style.STROKE);
   outerPaint.setColor(Color.argb(255, 128, 128, 128));
   outerPaint.setStrokeWidth(1); // width - is unit pixel or dp?
   outerPaint.setStrokeJoin(Join.ROUND);
   
   // Paint for drawing inner edges of LargerPieces:
   innerPaint.setStyle(Style.STROKE);
   innerPaint.setColor(Color.argb(100, 128, 128, 128)); // grey with low opacity
   innerPaint.setStrokeWidth(1); // width (see embossMaskFilter specular and/or blurRadius)
   innerPaint.setStrokeCap(Cap.BUTT);
   
}

public static void drawRotatingPiece(Canvas playFieldCanvas, VectorEdges vectorEdges,
 Direction direction, boolean clockwise, float amount)
{

}

/**
 * Draw on a buffer canvas. This canvas can then be drawn with the PlayField-coordinates onto the PlayField canvas,
 * or drawn shrunken as a thumbnail for BoxAdapter. The buffer canvas can be saved until the piece is attached (made
 * larger).
 * @param mrBuffer the buffer canvas
 * @param vectorEdges the shape of an AbstractPiece from the ImagePuzzle supplied to {@link #init(ImagePuzzle)}.
 */
public static void drawPiece(Canvas mrBuffer, VectorEdges vectorEdges)
{
   // set fill type to respect holes
   Path pieceShapePath = new Path();
   pieceShapePath.setFillType(FillType.EVEN_ODD);
   
   // collect all edges of the puzzle piece shape
   int holes = vectorEdges.getOuterEdgesCount();
   for (int i = 0; i < holes; i++) {
      Path path = vectorEdges.drawOuterEdges(i, 0f, 0f);
      pieceShapePath.addPath(path);
   }
   
   // offset (translate) to the correct part of the image
   shader.setLocalMatrix(vectorEdges.getTranslateMatrix(theImagePuzzle));
   //piecePaint.setShader(shader); // TODO: necessary to do this again?
   
   // draw the puzzle piece
   mrBuffer.drawPath(pieceShapePath, piecePaint);
   
   // draw innerEdges of LargerPiece
   if (vectorEdges instanceof LargerPieceEdges) {
      Path path = ((LargerPieceEdges) vectorEdges).drawInnerEdges(0f, 0f);
      mrBuffer.drawPath(path, innerPaint);
   }
}
}
