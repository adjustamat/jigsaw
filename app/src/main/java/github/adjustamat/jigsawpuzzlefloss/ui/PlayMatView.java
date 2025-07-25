package github.adjustamat.jigsawpuzzlefloss.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.otaliastudios.zoom.ZoomEngine;

import github.adjustamat.jigsawpuzzlefloss.PuzzleActivity;
import github.adjustamat.jigsawpuzzlefloss.containers.PlayMat;

public class PlayMatView
 extends View
{

ZoomEngine zoomPan;
PlayMat playMat;
PuzzleActivity activity;

public PlayMatView(Context context, @Nullable AttributeSet attrs)
{
   this(context, attrs, 0, 0);
}

public PlayMatView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
{
   this(context, attrs, defStyleAttr, 0);
}

public PlayMatView(Context context, @Nullable AttributeSet attrs, int defStyleAttr,
 int defStyleRes)
{
   super(context, attrs, defStyleAttr, defStyleRes);
   // TODO: does the parent ZoomLayout support View.OnScrollChangeListener? see: View.setOnSCrollChangeListener()
   //  But if it was a zoom gesture, some other algorithm has to be used to calculate what new parts come into view.
   //  View.OnScrollChangeListener could be used to draw part of the playmat (only the parts
   //  that comes into view when scrolling)
   
   // TODO: https://developer.android.com/reference/android/view/OrientationEventListener
}

public PlayMatView(Context context)
{
   this(context, null, 0, 0);
}

protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
{
   if (playMat == null) {
      // TODO: can this be possible if we only construct PlayMatView in code? But it's in the xml!
      // TODO: use ViewStub in xml and construct this in PuzzleActivity onCreate()
      setMeasuredDimension(
       MeasureSpec.getSize(widthMeasureSpec),
       MeasureSpec.getSize(heightMeasureSpec)
      );
      return;
   }
   
   PointF topLeft = playMat.getTopLeft();
   PointF bottomRight = playMat.getBottomRight();
   
   // TODO: calculate PlayMat width and height, and add space for panning outside and for zooming out.
   // TODO: combine with ZoomLayout.
   
   // TODO: set size in pixels(?) of this custom View!
   setMeasuredDimension(
    MeasureSpec.getSize(widthMeasureSpec),
    MeasureSpec.getSize(heightMeasureSpec)
   );
   
}

@SuppressLint("ClickableViewAccessibility")
public boolean onTouchEvent(MotionEvent ev)
{
   // TODO: ViewConfiguration.get(Context)
   //  ViewConfiguration:
   //  what is a "jump tap"?
   //  multi-press?
   
   // TODO: flick - MinimumFlingVelocity:
   //  https://developer.android.com/reference/android/view/ViewConfiguration#getScaledMinimumFlingVelocity(int,%20int,%20int)
   
   // android.view.VelocityTracker: look at the code! write code that can handle diagnoal movement.
   /*
      ViewConfiguration.getScaledVerticalScrollFactor() Returns:
       Amount to scroll in response to a vertical MotionEvent.ACTION_SCROLL event.
       Multiply this by the event's axis value to obtain the number of pixels to be scrolled.
    */
   
   // TODO: EVENTS (gestures) and ACTIONS that need pairing:
   //  EVENTS: click, double-click, long-click, flick, flick after long-click, drag, drag after long-click (
   //   moving diagonally, horizontally, vertically, changing direction, changing velocity )
   //   click with two fingers apart, click with two fingers together, flick with two fingers,
   //   long-click with two fingers, hold first finger and drag/flick with second finger
   //   drag with two fingers together, drag with two fingers apart, pinch, rotate with two fingers apart.
   //  EVENTS DURING DRAG: stationary hover over drag target, hover near to edge (edge scrolling), release.
   //  ACTIONS: (scroll), (zoom), move or rotate piece, zoom in on piece, show PlayMenu, select?, select group?.
   
   // TODO: ACTIONS in context menus: - ui.PlayMenu (do not use android builtin ContextMenu!)
   //  BG:
   //   rotate/modify bg
   //   group pieces together (rectangular selection, custom path selection)
   //    (can move already grouped pieces to new group)
   //  PIECE:
   //   lockedRotation
   //   lockedInPlace
   //  PIECE IN A GROUP:
   //   move whole group (click with second finger while still holding a finger down - disable
   //    this menu item if the first finger lifts while menu is showing)
   
   // TODO: send touch events that zoom and scroll to parent ZoomLayout by returning false,
   //  or modify https://github.com/natario1/ZoomLayout (ZoomImageView) to handle all touch events in one class
   return true;
}

public void onDraw(@NonNull Canvas canvas)
{
   // TODO: draw play mat
   // TODO: ViewConfiguration.getScaledMaximumDrawingCacheSize() // The maximum drawing cache size expressed in bytes.
}

}
