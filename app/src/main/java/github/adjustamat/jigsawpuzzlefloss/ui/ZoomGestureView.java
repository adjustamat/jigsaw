package github.adjustamat.jigsawpuzzlefloss.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.NonNull;

public class ZoomGestureView
 extends View
{
public static final float MIN_ZOOM_FACTOR = 0.1f;
public static final float MAX_ZOOM_FACTOR = 5.0f;
private final ScaleGestureDetector zoomDetect;
private float zoomFactor = 1.f;

public ZoomGestureView(Context context)
{
   super(context);
   //...
   //Your view code
   //...
   zoomDetect = new ScaleGestureDetector(context, new ZoomGestureListener());
}

@Override
public boolean onTouchEvent(MotionEvent ev)
{
   // Let the ScaleGestureDetector inspect all events.
   zoomDetect.onTouchEvent(ev);
   return true;
}

@Override
public void onDraw(@NonNull Canvas canvas)
{
   super.onDraw(canvas);
   canvas.save();
   
   canvas.scale(zoomFactor, zoomFactor);
   //...
   //Your onDraw() code
   //...
   
   
   canvas.restore();
}

private class ZoomGestureListener
 extends ScaleGestureDetector.SimpleOnScaleGestureListener
{
   @Override
   public boolean onScale(ScaleGestureDetector detector)
   {
      zoomFactor *= detector.getScaleFactor();
      
      // Limit zoom to between min and max
      zoomFactor = Math.max(MIN_ZOOM_FACTOR, Math.min(zoomFactor, MAX_ZOOM_FACTOR));
      
      invalidate();
      return true;
   }
}
}
