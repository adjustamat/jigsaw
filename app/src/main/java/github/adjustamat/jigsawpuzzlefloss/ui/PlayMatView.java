package github.adjustamat.jigsawpuzzlefloss.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.otaliastudios.zoom.ZoomEngine;

import github.adjustamat.jigsawpuzzlefloss.PuzzleActivity;
import github.adjustamat.jigsawpuzzlefloss.containers.PlayMat;

public class PlayMatView
 extends View
 implements OnLongClickListener
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
   //setLongClickable(true);
   setOnLongClickListener(this);
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

private final TouchFinger first = new TouchFinger();
private final TouchFinger second = new TouchFinger();
private int fingers;
private TouchEvent event = null; // current event/state, or event if EVENT_UP is triggered. (?)

/**
 * Called when a view has been clicked and held.
 * // TODO: how sense long-click without any new motionevents? longclicklistener?
 * @param v The view that was clicked and held.
 * @return true if the callback consumed the long click, false otherwise.
 */
public boolean onLongClick(View v)
{
   return false;
}

private static class TouchFinger
{
   long down;
   int id;
   float downX, downY;
   float prevX, prevY;
}

enum TouchEvent
{
   CLICK, DOUBLECLICK, HOLD,
   PINCH_ZOOM, DOUBLECLICK_ZOOM
}

@SuppressLint("ClickableViewAccessibility")
public boolean onTouchEvent(MotionEvent ev)
{
   /*
   state = null
   
   DOWN => state = one-finger-down
   
   one-finger-down: MOVE (check slop, can stay in the same state) => one-finger-drag
   
    */
   
   // TODO: how sense that two fingers went down at the same time, as one? check fatness of touch?
   
   // TODO: EVENTS (gestures) and ACTIONS that need pairing:
   //  1-FINGER EVENTS: click, double-click, drag, flick, long-click, drag after long-click, flick after long-click
   //  2-FINGER EVENTS:
   //   click with two fingers apart, click with two fingers together, flick with two fingers (THROW GROUP),
   //   long-click with two fingers, touch first finger and drag/flick with second finger (ROTATE ABS_PIECE)
   //   drag with two fingers together, drag with two fingers apart, pinch, rotate with two fingers apart.
   //  DRAG: ( moving diagonally, horizontally, vertically, changing direction, changing velocity )
   //  EVENTS DURING DRAG: stationary hover over drag target, hover near to edge (edge scrolling), release.
   //  ACTIONS: (scroll), (zoom), move or rotate piece, zoom in on piece, show PlayMenu, select?, select group?.
   //   rotate whole group, rotate all in group but around individual centers.
   
   // TODO: when highlighting a group, do all these things:
   //  draw a line around the group (with quite a bit of margin).
   //  show the name of the group
   //  draw around the pieces with a color, on a layer below all pieces (like BorderDrawable but also around
   //   puzzle-outer edges), or on a layer above all pieces (line border, not gradient. I have to make special path segments different from PieceJedge for this expanded outline-border)
   //  fade other pieces (give them 80% opacity), but note especially the other pieces that overlap pieces in the group
   //   but are not in the group.
   
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
   
   switch (ev.getActionMasked()) {
   case MotionEvent.ACTION_DOWN:
      first.id = ev.getPointerId(0);
      first.down = SystemClock.elapsedRealtime();
      first.downX = first.prevX = ev.getX();
      first.downY = first.prevY = ev.getY();
      fingers = 1;
      break;
   case MotionEvent.ACTION_POINTER_DOWN:
      if (fingers == 0)
         break;
      if (fingers >= 2) { // cancel on too many fingers
         fingers = 0;
         break;
      }
      second.id = ev.getPointerId(ev.getActionIndex());
      second.down = SystemClock.elapsedRealtime();
      second.downX = second.prevX = ev.getX();
      second.downY = second.prevY = ev.getY();
      fingers++;
      
      break;
   case MotionEvent.ACTION_POINTER_UP: {
      if (fingers == 0)
         break;
      int id = ev.getPointerId(ev.getActionIndex());
      if (id == first.id) { // cancel on lifting the first finger
         fingers = 0;
         break;
      }
      
      break;
   }
   case MotionEvent.ACTION_UP:
      if (fingers == 0)
         break;
      fingers = 0;
      // TODO: if(sensed a click or something) doSomething();
      break;
   case MotionEvent.ACTION_CANCEL:
      fingers = 0;
      break;
   case MotionEvent.ACTION_MOVE:
      if (fingers == 0)
         break;
      
      
   }
   // TODO: flick - MinimumFlingVelocity:
   //  https://developer.android.com/reference/android/view/ViewConfiguration#getScaledMinimumFlingVelocity(int,%20int,%20int)
   // android.view.VelocityTracker: look at the code! write code that can handle diagonal movement.
   
   
   return true;
}

public void onDraw(@NonNull Canvas canvas)
{
   // TODO: draw play mat
   // TODO: ViewConfiguration.getScaledMaximumDrawingCacheSize() // The maximum drawing cache size expressed in bytes.
}

}
