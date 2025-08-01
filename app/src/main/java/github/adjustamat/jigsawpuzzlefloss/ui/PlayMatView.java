package github.adjustamat.jigsawpuzzlefloss.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.otaliastudios.zoom.ZoomEngine;

import github.adjustamat.jigsawpuzzlefloss.PuzzleActivity;
import github.adjustamat.jigsawpuzzlefloss.containers.Group;
import github.adjustamat.jigsawpuzzlefloss.containers.PlayMat;
import github.adjustamat.jigsawpuzzlefloss.pieces.AbstractPiece;
import github.adjustamat.jigsawpuzzlefloss.pieces.LargerPiece;
import github.adjustamat.jigsawpuzzlefloss.pieces.SinglePiece;

public class PlayMatView
 extends View
// implements OnLongClickListener
{
/// **
// * Called when a view has been clicked and held.
// * @param v The view that was clicked and held.
// * @return true if the callback consumed the long click, false otherwise.
// */
//public boolean onLongClick(View v)
//{
//   return false;
//}

ZoomEngine zoomPan;
PlayMat playMat;
PuzzleActivity activity;
private final Handler handler;

public PlayMatView(Context context)
{
   this(context, null, 0, 0);
}

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
//   setOnLongClickListener(this);
   handler = new Handler(Looper.getMainLooper());
   // TODO: does the parent ZoomLayout support View.OnScrollChangeListener? see: View.setOnSCrollChangeListener()
   //  But if it was a zoom gesture, some other algorithm has to be used to calculate what new parts come into view.
   //  View.OnScrollChangeListener could be used to draw part of the playmat (only the parts
   //  that comes into view when scrolling)
   
   // TODO: https://developer.android.com/reference/android/view/OrientationEventListener
}

public void cancelHandlerDelayed()
{
   handler.removeCallbacks(delayedLongPress);
}

private static class TouchFinger
{
   long down;
   int id;
   float downX, downY;
   float lastX, lastY;
   float downFat, lastFat, maxFat;
}

public interface PieceOrGroup { }

private final TouchFinger first = new TouchFinger();
private final TouchFinger second = new TouchFinger();
private int fingers;
private final Runnable delayedLongPress = ()->{
   setTouchState(TouchState.HOLD/*, first*/);
};
private TouchState lastState = TouchState.NONE;
private TouchState predictedUpState = TouchState.NONE;
private PieceOrGroup dragged;
private PieceOrGroup lastTouched;

// draw this with low opacity (when placing something from another Container)
public PieceOrGroup transparentDragged;

private void setTouchState(TouchState newCurrent/*, TouchFinger finger*/)
{
   if (fingers == 0)
      return;
   
   // TODO?
   if (newCurrent == TouchState.HOLD) {
      if (lastState == newCurrent) {
         // trigger menu. or do it in the other method, which only calls setTouchState(NONE)
         
      }
      
      predictedUpState = TouchState.HOLD;
      
      // TODO: show something like a menu with low opacity, which materializes on UP. but a second finger
      //  can cancel HOLD and turn it into FLICK_2_ROTATE // touch first finger and drag/flick with second finger
   }
   
   // TODO: for a DRAG_2_PAN_RV to become PINCH_2_ZOOM_PAN_RV, the distance between fingers
   //  has to significantly change both from lastDistance and from downDistance.
   
   // TODO: after DRAG, no CLICK! after DRAG_2GR or HOLD_2GR, no CLICK_2GR!
   
   // TODO: after HOLD, DRAG, or HOLD_DRAG: TOUCH_2GR no longer possible, instead becomes
   //  TOUCH_2 or immediately DRAG_2_PAN_RV.
   
   // TODO: after starting DRAG_2GR or HOLD_DRAG_2GR, breaking fingers apart or even lifting second finger are all
   //  ignored. only first finger is listened to.
   
   lastState = newCurrent;
}

/*
  
  state==NONE : DOWN => state = TOUCH , up = CLICK , (delay: HOLD)
  state==CLICKED : DOWN => state = DOUBLECLICK_TOUCH , up = DOUBLECLICK
  
  state==TOUCH : UP => (CLICK, cancel HOLD) => state = CLICKED
  state==TOUCH : MOVE (check slop, can stay in the same state <=) => state = DRAG , up = DROP , (cancel HOLD)
  state==TOUCH : DOWN => ...
                  (always cancel HOLD)
                  
  
  state==DRAG : UP => (DROP) => state = NONE
  state==DRAG : MOVE <=
  state==DRAG : DOWN => ...
  
  
  state==HOLD : UP => (HOLD) => state = NONE
  state==HOLD : MOVE
  state==HOLD : DOWN
  
  state== : UP
  state== : MOVE
  state== : DOWN
  
  state== : UP
  state== : MOVE
  state== : DOWN
  
  state== : UP
  state== : MOVE
  state== : DOWN
  
  state== : UP
  state== : MOVE
  state== : DOWN
  
   */
enum TouchState
{
   NONE, CLICKED,
   
   TOUCH, DOUBLECLICK_TOUCH, HOLD,
   DRAG, DOUBLECLICK_DRAG_ZOOM, HOLD_DRAG,
   
   // no TOUCH_2,
   DRAG_2_PAN_RV, FLICK_2_ROTATE, PINCH_2_ZOOM_PAN_RV,
   
   TOUCH_2GR, HOLD_2GR,
   DRAG_2GR, HOLD_DRAG_2GR,
}

enum UpAction
{ // TODO: this enum class should be removed. just check TouchState when up! see large comment above.
   CLICK, DOUBLECLICK, DROP, CLICK_2GR,
}

@SuppressLint("ClickableViewAccessibility")
public boolean onTouchEvent(MotionEvent ev)
{
   // TODO: EVENTS (gestures) and ACTIONS that need pairing:
   //  1-FINGER EVENTS: click, doubleclick, drag/flick, hold, hold_drag after long-click
   //  2-FINGER EVENTS:
   //   click with two fingers together CLICK_2GR, DRAG_2GR, DRAG_2GR-fling (THROW GROUP), HOLD_DRAG_2GR
   //   long-click HOLD_2GR, FLICK_2_ROTATE (ROTATE PIECE)
   //   drag with two fingers apart, pinch, rotate with two fingers apart.
   //  DRAG: ( moving diagonally, horizontally, vertically, changing direction, changing velocity ) ?
   //  EVENTS DURING DRAG_DROP: stationary hover over drag target, hover near to edge (edge scrolling), release.
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
   int prevFingers = fingers;
   ViewConfiguration viewConf;
   switch (ev.getActionMasked()) {
   case MotionEvent.ACTION_DOWN:
      first.id = ev.getPointerId(0);
      first.down = SystemClock.elapsedRealtime();
      first.downX = first.lastX = ev.getX();
      first.downY = first.lastY = ev.getY();
      first.maxFat = first.downFat = first.lastFat = ev.getSize(0);
      fingers = 1;
      
      
      viewConf = ViewConfiguration.get(getContext());
      
      
      
      //ViewConfiguration.getJumpTapTimeout()
      //ViewConfiguration.getDoubleTapTimeout()
      // viewConf.getScaledDoubleTapSlop()
      
      setTouchState(TouchState.TOUCH);
      handler.postDelayed(delayedLongPress, ViewConfiguration.getLongPressTimeout());
      break;
   
   case MotionEvent.ACTION_POINTER_DOWN:
      if (fingers == 0)
         break;
      if (fingers >= 2) { // cancel on too many fingers (3)
         fingers = 0;
         break;
      }
      second.id = ev.getPointerId(ev.getActionIndex());
      second.down = SystemClock.elapsedRealtime();
      second.downX = second.lastX = ev.getX();
      second.downY = second.lastY = ev.getY();
      second.maxFat = second.downFat = second.lastFat = ev.getSize(1);
      fingers++;
      
      long since = second.down - first.down;
      
      // TODO: to get to FLICK_2_ROTATE, second finger must be after a delay after first.down, otherwise it's
      //  DRAG_2_PAN. also, must not drag first finger. but HOLD is fine.
      
      float diffX = second.downX - first.lastX;
      float diffY = second.downY - first.lastY;
      double distance = Math.sqrt(diffX * diffX + diffY * diffY);
      
      viewConf = ViewConfiguration.get(getContext());
      
      
      // TODO: are fingers apart or together? (how sense that two fingers went down at the same time, as one?)
      // TODO: check fatness of touch?
      // TODO: is two fingers touching at the same time 1 event or 2? - or 1 when together, 2 when apart?
      // TODO: for two-finger hold:
      //  handler.postDelayed(delayedLongPress, ViewConfiguration.getLongPressTimeout());
      
      break;
   
   case MotionEvent.ACTION_MOVE: {
      if (fingers == 0)
         break;
      
      // TODO: when touching two fingers 2gr, ignore all ACTION_MOVE with non-primary finger.
      
      int id = ev.getPointerId(ev.getActionIndex());
      
      if (id == first.id) {
      
      }
      else {
      
      }
      
      // THROW (same as DRAG) // THROW_2GR (same as DRAG_2GR)
      
      viewConf = ViewConfiguration.get(getContext());
      
      //ViewConfigurationCompat.getScaledHorizontalScrollFactor()
      //ViewConfigurationCompat.getScaledVerticalScrollFactor()
      
      //viewConf.getScaledPagingTouchSlop() - ? - save this for ACTION_UP/ACTION_POINTER_UP
      //viewConf.getScaledTouchSlop()
      //viewConf.getScaledHoverSlop() ViewConfigurationCompat.getScaledHoverSlop()
      
      switch (lastState) {
      
      case TOUCH:
         // TODO: only after moving too much (slop) do we cancel delayedLongPress.
         break;
      }
      
      
      
      // TODO: call setTouchState(event,finger)!
      
      
      
      break;
   }
   case MotionEvent.ACTION_POINTER_UP:
      if (fingers == 0)
         break;
      int id = ev.getPointerId(ev.getActionIndex());
      if (id == first.id) {
         // cancel on lifting the primary finger while another is still pressed
         //fingers = 0; TODO: unless both are lifted "at the same time"! (close enough)
         // TODO: is ViewConfiguration.getTapTimeout() useful?
         break;
      }
      
      
      viewConf = ViewConfiguration.get(getContext());
      
      // THROW (same as DRAG) // THROW_2GR (same as DRAG_2GR)
      //ViewConfigurationCompat.getScaledMaximumFlingVelocity()
      //ViewConfigurationCompat.getScaledMinimumFlingVelocity()
      // TODO: flick - android.view.VelocityTracker: look at the code! write code that can handle diagonal movement.
      //  https://developer.android.com/reference/android/view/ViewConfiguration#getScaledMinimumFlingVelocity(int,%20int,%20int)
      
      
      // TODO: finalize rotate if second finger rotated a piece and left it rotated when lifting finger. or if flicked!
      // TODO: can lifting a second finger do something else?
      switch (predictedUpState) {
      
      }
      
      break;
   
   case MotionEvent.ACTION_UP:
      if (fingers == 0)
         break;
      fingers = 0;
      
      viewConf = ViewConfiguration.get(getContext());
      
      // THROW (same as DRAG) // THROW_2GR (same as DRAG_2GR)
      //ViewConfigurationCompat.getScaledMaximumFlingVelocity()
      //ViewConfigurationCompat.getScaledMinimumFlingVelocity()
      // TODO: flick - android.view.VelocityTracker: look at the code! write code that can handle diagonal movement.
      //  https://developer.android.com/reference/android/view/ViewConfiguration#getScaledMinimumFlingVelocity(int,%20int,%20int)
      
      
      // TODO: if(sensed a click or something) doSomething();
      switch (predictedUpState) {
      // TODO: when single CLICK, delay! because double click must be able to happen.
      //ViewConfiguration.getJumpTapTimeout()
      //ViewConfiguration.getDoubleTapTimeout()
      }
      
      break;
   
   case MotionEvent.ACTION_CANCEL:
      fingers = 0;
      // default: error
   }
   
   if (prevFingers != fingers && fingers == 0) {
      cancelHandlerDelayed();
   }
   
   return true;
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
   // TODO: combine with ZoomLayout. see onDraw() below.
   
   // TODO: set size in pixels(?) of this custom View!
   setMeasuredDimension(
    MeasureSpec.getSize(widthMeasureSpec),
    MeasureSpec.getSize(heightMeasureSpec)
   );
   
}

public void onDraw(@NonNull Canvas canvas)
{
   // TODO: ViewConfiguration.getScaledMaximumDrawingCacheSize() // The maximum drawing cache size expressed in bytes.
   
   // TODO: scale and translate due to zoom/pan.
   //canvas.save();
   //canvas.scale();
   //canvas.translate();
   
   // TODO: all pieces have a cached UnrotatedFullSizeGraphics for the box and this draw method.
   //  Does it all fit in memory? Perhaps nothing can be cached.
   
   // TODO: only draw outlines when zooming!
   
   // TODO: only draw what's currently visible!
   // TODO: can we know what is on top of something, so we do not draw what's below? probably not.
   // TODO: when all is visible (zoomed out, or not much on playmat) - use sub-pixel-something (glide).
   
   // TODO: the later something is in these lists, the more foreground.
   //  how to implement z-position?
   // TODO: draw outlines of covered pieces. what color? how draw invert color? no xfermode for that!
   
   for (LargerPiece piece: playMat.largerPieces) {
      canvas.drawBitmap(piece.getUnrotatedFullSizeGraphics(),
       piece.getPlayMatTranslationAndRotation(), null);
   }
   for (Group group: playMat.groups) {
      for (AbstractPiece piece: group.getAllPieces()) {
         // TODO: larger pieces must be in the beginning of the group list.
         canvas.drawBitmap(piece.getUnrotatedFullSizeGraphics(),
          piece.getPlayMatTranslationAndRotation(), null);
      }
   }
   for (SinglePiece piece: playMat.singlePieces) {
      canvas.drawBitmap(piece.getUnrotatedFullSizeGraphics(),
       piece.getPlayMatTranslationAndRotation(), null);
   }
   
   // TODO: reset scale and translation due to zoom/pan.
   //canvas.restore();
}
}
