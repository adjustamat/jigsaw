package github.adjustamat.jigsawpuzzlefloss.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
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
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import github.adjustamat.jigsawpuzzlefloss.game.ImagePuzzle;
import github.adjustamat.jigsawpuzzlefloss.pieces.AbstractPiece;
import github.adjustamat.jigsawpuzzlefloss.pieces.AbstractPiece.VectorJedges;
import github.adjustamat.jigsawpuzzlefloss.pieces.LargerPiece.LargerPieceJedges;

public class PuzzleGraphics
{
private PuzzleGraphics(){ }

public static final String DBG = "PuzzleGraphics";

private static final MaskFilter embossMaskFilter = new EmbossMaskFilter(
 new float[]{0.5f, 0.5f, 0.5f}, 0.9f, 8, 1);
// TODO: experiment with direction values, specular and blurRadius.

private static ImagePuzzle theImagePuzzle;
private static final Paint piecePaint = new Paint();
private static final Paint outerPaint = new Paint();
private static final Paint innerPaint = new Paint();
private static final Paint innerOpacity = new Paint();

private static BitmapShader shader;

//public static void setOutlineStrokeColor(int r, int g, int b)
//{
//   outerPaint.setColor(Color.argb(255, r, g, b));
//}

public static void init(ImagePuzzle imagePuzzle, Context ctx)
{
   theImagePuzzle = imagePuzzle;
   
   // draw puzzle piece shape outlines as emboss (3d-effect):
   piecePaint.setMaskFilter(embossMaskFilter);
   
   piecePaint.setStyle(Style.FILL); // not FILL_AND_STROKE, because we use MaskFilter instead of stroke!
   
   // fill puzzle piece shape with the puzzle image:
   Glide.with(ctx)
    .asBitmap()
    .load(imagePuzzle.bitmapUri)
    .into(new CustomTarget<Bitmap>()
    {
       public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition)
       {
          shader = new BitmapShader(resource,
           Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
          piecePaint.setShader(shader);
          // Use the paint to draw on a Canvas
       }
       
       public void onLoadCleared(@Nullable Drawable placeholder)
       {
       }
    });
//   shader = new BitmapShader(imagePuzzle.image, TileMode.CLAMP, TileMode.CLAMP);
//   //shader.setFilterMode(BitmapShader.FILTER_MODE_NEAREST);
//   piecePaint.setShader(shader);
   
   
   // Paint for drawing outline when rotating:
   outerPaint.setStyle(Style.STROKE);
   outerPaint.setXfermode(new PorterDuffXfermode(Mode.SCREEN)); // TODO: experiment with transfermode!
   outerPaint.setColor(Color.argb(255, 128, 128, 128));
   outerPaint.setStrokeWidth(1); // TODO: width - is unit pixel or dp?
   outerPaint.setStrokeJoin(Join.ROUND);
   
   // Paint for drawing inner edges of LargerPieces:
   innerPaint.setStyle(Style.STROKE);
   innerPaint.setColor(Color.argb(255/*100*/, 128, 128, 128)); // grey with low opacity
   innerOpacity.setAlpha(100);
   innerPaint.setStrokeWidth(1); // TODO: width (see embossMaskFilter specular and/or blurRadius)
   innerPaint.setStrokeCap(Cap.BUTT);
}

public static void drawRotatingPiece(Canvas playMatCanvas, AbstractPiece piece,
 PointF clickPoint, boolean clockwise, @FloatRange(from=0, to=1) float amount)
{
   VectorJedges vectorJedges = piece.getVectorJedges();
   // TODO: is this path scaled properly? how about theImagePuzzle.pieceImageSize?
   Path pieceShapePath = vectorJedges.drawAllOuterJedges();
   
   playMatCanvas.save();
   
   // move to the playMat-coordinates of the piece:
   PointF position = piece.getAbsolutePos();
   playMatCanvas.translate(position.x, position.y);
   
   // rotate:
   float degrees = piece.currentNorthDirection.degrees;
   degrees += (clockwise ?90 :-90) * amount;
   playMatCanvas.rotate(-degrees, clickPoint.x, clickPoint.y);
   
   // draw the outline:
   playMatCanvas.drawPath(pieceShapePath, outerPaint);
   
   playMatCanvas.restore();
}

/**
 * Draw on a buffer canvas. This canvas can then be drawn with the PlayMat-coordinates onto the PlayMat canvas,
 * or drawn shrunken as a thumbnail for BoxAdapter. The buffer canvas can be saved until the piece is attached (made
 * larger).
 * @param pieceBufferCanvas the buffer canvas
 * @param vectorJedges an AbstractPiece from the ImagePuzzle supplied to {@link #init(ImagePuzzle, Context)}.
 * @param width of the bitmap/canvas
 * @param height of the bitmap/canvas
 */
public static void drawPiece(Canvas pieceBufferCanvas, VectorJedges vectorJedges, int width, int height)
{
   // TODO: is this path scaled properly? how about theImagePuzzle.pieceImageSize?
   Path pieceShapePath = vectorJedges.drawAllOuterJedges();
   
   // offset (translate) BitmapShader to the correct part of the puzzle image:
   shader.setLocalMatrix(vectorJedges.getImageTranslation(theImagePuzzle));
   piecePaint.setShader(shader); // TODO: is it necessary to do this again?
   
   // draw the puzzle piece:
   pieceBufferCanvas.drawPath(pieceShapePath, piecePaint);
   
   // draw innerEdges of LargerPiece:
   if (vectorJedges instanceof LargerPieceJedges) {
      Bitmap innerBitmap = Bitmap.createBitmap(width, height, Config.RGB_565);
      Canvas innerCanvas = new Canvas(innerBitmap);
      // TODO: is this path scaled properly? how about theImagePuzzle.pieceImageSize?
      Path path = ((LargerPieceJedges) vectorJedges).drawInnerJedges(0f, 0f);
      innerCanvas.drawPath(path, innerPaint);
      pieceBufferCanvas.drawBitmap(innerBitmap, 0f, 0f, innerOpacity);
      //pieceBufferCanvas.drawPath(path, innerPaint);
   }
}
}
