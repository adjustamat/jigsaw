package github.adjustamat.jigsawpuzzlefloss.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import com.otaliastudios.zoom.ZoomEngine;

import github.adjustamat.jigsawpuzzlefloss.containers.PlayMat;

/**
 * A View of the whole {@link PlayMat}, to help with scrolling.
 */
public class MiniMapView
 extends View
{
public MiniMapView(Context context)
{
   this(context, null, 0, 0);
}

public MiniMapView(Context context, AttributeSet attrs)
{
   this(context, attrs, 0, 0);
}

public MiniMapView(Context context, AttributeSet attrs, int defStyleAttr)
{
   this(context, attrs, defStyleAttr, 0);
}

public MiniMapView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
{
   super(context, attrs, defStyleAttr, defStyleRes);
   // TODO?
}

private PlayMatView playMatView;

public PlayMatView getPlayMatView()
{
   return playMatView;
}

public void setPlayMatView(PlayMatView playMatView)
{
   this.playMatView = playMatView;
}

private int w;
private int h;

protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
{
   int fixedWidth = this.w = MeasureSpec.getSize(widthMeasureSpec);
   int height;
   if (playMatView == null) {
      height = MeasureSpec.getSize(heightMeasureSpec);
      this.h = 0; // not yet measured.
   }
   else {
      // TODO: ratio cannot be so small that height becomes smaller than minHeight here. playmat has to get
      //  dimensions of the fixed width and minHeight from resources, and increase its smaller dimension if the ratio
      //  becomes too big or small.
      float ratio = playMatView.playMat.getHeightRatio();
      height = (int) (ratio * fixedWidth);
      
      this.h = height;
   }
   setMeasuredDimension(fixedWidth, height);
}

@SuppressLint("ClickableViewAccessibility")
public boolean onTouchEvent(MotionEvent ev)
{
   if (playMatView == null)
      return false;
   
   // TODO: when scrolling or zooming so we see outside the play mat, show that also in this MiniMapView by making
   //  the view of the play mat even smaller and adding more empty white space inside and around the rectangle.
   if (playMatView.isAllVisible())
      return false;
   
   //ZoomEngine zoomPan = playMatView.zoomPan;
   //zoomPan.get
   
   // TODO: when clicking/touching/dragging MiniMap, pan (scroll) the PlayMatView - see ZoomEngine.
   //  - only available when the PlayMatView is zoomed in such that not all of it can be seen.
   return true;
}

public void onDraw(@NonNull Canvas canvas)
{
   if (playMatView == null)
      return;
   
   if (this.h == 0) {
      getParent().requestLayout();
      return;
   }
   
   
   
   // TODO: draw mini map - singlepieces are just black squares (or shapes filled with black if there are less than ~10-20.
   //  largerpieces are also made of black squares, based on its matrix.
   //  then scale it down to the canvas width (height will adjust, see onMeasure)
   
   // TODO: draw rectangle where Zoom window is.
}

}
