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
import android.graphics.PointF;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;

import androidx.annotation.FloatRange;

import github.adjustamat.jigsawpuzzlefloss.game.Direction;
import github.adjustamat.jigsawpuzzlefloss.game.ImagePuzzle;
import github.adjustamat.jigsawpuzzlefloss.pieces.AbstractPiece.VectorEdges;
import github.adjustamat.jigsawpuzzlefloss.pieces.LargerPiece.LargerPieceEdges;

public class PuzzleGraphics
{
private PuzzleGraphics(){ }

private static final MaskFilter embossMaskFilter = new EmbossMaskFilter(
 new float[]{0.5f, 0.5f, 0.5f}, 0.9f, 8, 1);
// TODO: experiment with direction values, specular and blurRadius.

private static final Paint piecePaint = new Paint();
private static final Paint outerPaint = new Paint();
private static final Paint innerPaint = new Paint();
private static BitmapShader shader;
private static ImagePuzzle theImagePuzzle;

//public static void setOutlineStrokeColor(int r, int g, int b)
//{
//   outerPaint.setColor(Color.argb(255, r, g, b));
//}

public static void init(ImagePuzzle imagePuzzle)
{
   theImagePuzzle = imagePuzzle;
   
   piecePaint.setStyle(Style.FILL); // not FILL_AND_STROKE, because we use MaskFilter instead of stroke!
   // fill puzzle piece shape with the puzzle image:
   shader = new BitmapShader(imagePuzzle.image, TileMode.CLAMP, TileMode.CLAMP);
   //shader.setFilterMode(BitmapShader.FILTER_MODE_NEAREST);
   piecePaint.setShader(shader);
   // draw puzzle piece shape outlines as emboss (3d-effect):
   piecePaint.setMaskFilter(embossMaskFilter);
   
   // Paint for drawing outline when rotating:
   outerPaint.setStyle(Style.STROKE);
   outerPaint.setXfermode(new PorterDuffXfermode(Mode.SCREEN)); // TODO: experiment with transfermode!
   outerPaint.setColor(Color.argb(255, 128, 128, 128));
   outerPaint.setStrokeWidth(1); // TODO: width - is unit pixel or dp?
   outerPaint.setStrokeJoin(Join.ROUND);
   
   // Paint for drawing inner edges of LargerPieces:
   innerPaint.setStyle(Style.STROKE);
   innerPaint.setColor(Color.argb(100, 128, 128, 128)); // grey with low opacity
   innerPaint.setStrokeWidth(1); // TODO: width (see embossMaskFilter specular and/or blurRadius)
   innerPaint.setStrokeCap(Cap.BUTT);
}

public static void drawRotatingPiece(Canvas playFieldCanvas, PointF mousePoint, VectorEdges vectorEdges,
 Direction originalRotation, boolean clockwise, @FloatRange(from=0, to=1) float amount)
{
   Path pieceShapePath = vectorEdges.drawOuterEdges();
   
   // move to the playField-coordinates of the piece:
   // TODO: playFieldCanvas.translate(x,y); // use the PlayField-coordinates
   
   // rotate:
   float degrees = originalRotation.degrees;
   
   degrees += (clockwise ?90 :-90) * amount;
   playFieldCanvas.rotate(-degrees, mousePoint.x, mousePoint.y);
   
   // draw the outline:
   playFieldCanvas.drawPath(pieceShapePath, outerPaint);
}

/**
 * Draw on a buffer canvas. This canvas can then be drawn with the PlayField-coordinates onto the PlayField canvas,
 * or drawn shrunken as a thumbnail for BoxAdapter. The buffer canvas can be saved until the piece is attached (made
 * larger).
 * @param pieceBufferCanvas the buffer canvas
 * @param vectorEdges the shape of an AbstractPiece from the ImagePuzzle supplied to {@link #init(ImagePuzzle)}.
 */
public static void drawPiece(Canvas pieceBufferCanvas, VectorEdges vectorEdges)
{
   Path pieceShapePath = vectorEdges.drawOuterEdges();
   
   // offset (translate) to the correct part of the image:
   shader.setLocalMatrix(vectorEdges.getImageTranslateMatrix(theImagePuzzle));
   //piecePaint.setShader(shader); // TODO: is it necessary to do this again?
   
   // draw the puzzle piece:
   pieceBufferCanvas.drawPath(pieceShapePath, piecePaint);
   
   // draw innerEdges of LargerPiece:
   if (vectorEdges instanceof LargerPieceEdges) {
      Path path = ((LargerPieceEdges) vectorEdges).drawInnerEdges(0f, 0f);
      pieceBufferCanvas.drawPath(path, innerPaint);
   }
}
}
