package github.adjustamat.jigsawpuzzlefloss.ui;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.otaliastudios.zoom.ZoomEngine;

import java.util.Iterator;

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
private PuzzleActivity activity;
private Handler handler;
private PlayMenu menu;

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
   // TODO: does the parent ZoomLayout support View.OnScrollChangeListener? see: View.setOnSCrollChangeListener()
   //  But if it was a zoom gesture, some other algorithm has to be used to calculate what new parts come into view.
   //  View.OnScrollChangeListener could be used to draw part of the playmat (only the parts
   //  that comes into view when scrolling)
   
   // TODO: https://developer.android.com/reference/android/view/OrientationEventListener
}

public void handleOnPause()
{
   fingers = 0; // cancel all touch events
   setTouchState(TouchState.UP_NONE);
   
   activity = null;
   handler = null;
   menu = null;
}

public void unPause(PuzzleActivity activity)
{
   this.activity = activity;
   this.handler = activity.getMainHandler();
   this.menu = activity.getMenu();
   ViewConfiguration viewConf = ViewConfiguration.get(activity);
   this.bigSlopSqd = viewConf.getScaledPagingTouchSlop();
   bigSlopSqd *= bigSlopSqd;
   this.slopSqd = viewConf.getScaledTouchSlop();
   slopSqd *= slopSqd;
   this.doubleTapSlopSqd = viewConf.getScaledDoubleTapSlop();
   doubleTapSlopSqd *= doubleTapSlopSqd;
}

public boolean handleOnBackPressed()
{
   if (fingers > 0) {
      fingers = 0;
      return true;
   }
   
   if (lastState == TouchState.UP_MENU) {
      setTouchState(TouchState.UP_NONE);
      menu.hideMenu();
      return true;
   }
   
   if (selected > 0) {
      playMat.deselectAll();
      selected = 0;
      return true;
   }
   
   return false; // show backbutton choices in PuzzleActivity
}

private static class TouchFinger
{
   long down;
   int id;
   float downX;
   float downY;
   float lastX;
   float lastY;
   float downFat;
   float maxFat;
   float lastFat;
   
   public TouchFinger(int id, float downX, float downY, float downFat)
   {
      down(id, downX, downY, downFat);
   }
   
   public void copy(TouchFinger other)
   {
      down = other.down;
      id = other.id;
      downX = other.downX;
      downY = other.downY;
      lastX = other.lastX;
      lastY = other.lastY;
      downFat = other.downFat;
      maxFat = other.maxFat;
      lastFat = other.lastFat;
   }
   
   public void update(float x, float y, float fat)
   {
      lastX = x;
      lastY = y;
      lastFat = fat;
      maxFat = Math.max(maxFat, fat);
   }
   
   public void down(int id, float x, float y, float fat)
   {
      this.down = SystemClock.elapsedRealtime();
      this.id = id;
      this.downX = lastX = x;
      this.downY = lastY = y;
      this.maxFat = downFat = lastFat = fat;
   }
}

private static class Move
{
   public final long time;
   public final float x;
   public final float y;
   
   public Move(long time, float x, float y)
   {
      this.time = time;
      this.x = x;
      this.y = y;
   }
}

private static class RecentMoves
{
   //   private final long[] time;
//   private final float[] x;
//   private final float[] y;
   private final Move[] moves;
   private final int size;
   private int logged = 0;
   private int currentPosition = 0;
   
   public RecentMoves(int size)
   {
      this.size = size;
      moves = new Move[size];
//      time = new long[size];
//      x = new float[size];
//      y = new float[size];
   }
   
   public void log(long time, float x, float y)
   {
      moves[currentPosition] = new Move(time, x, y);
//      this.time[currentPosition] = time;
//      this.x[currentPosition] = x;
//      this.y[currentPosition] = y;
      logged++;
      if (currentPosition == size - 1) currentPosition = 0;
      else currentPosition++;
   }
   
   public Iterator<Move> toIterator()
   {
      return new Iterator<Move>()
      {
         private int i = 0;
         private final int length = logged < size ?logged :size;
         
         public boolean hasNext()
         {
            return i < length;
         }
         
         public Move next()
         {
            int index = currentPosition - i;
            if (index < 0)
               index += size;
            if (index >= size)
               index -= size;
            Move ret = moves[index];
            i++;
            return ret;
         }
      };
   } // toIterator()
}

// TODO: always primary finger, with one exception: FLICK_2_ROTATE. recentmoves resets on POINTER_DOWN and DOWN.
// TODO: or use velocitytracker instead of recentmoves
private RecentMoves recentMoves;
private int fingers;
private TouchState lastState = TouchState.UP_NONE;
private boolean movingSelection; // selected pieces/groups is in PlayMat
private boolean movingPiece; // lastTouched[0] (lastTouched should be only one piece)
private boolean movingGroup; // lastTouched[0].group

private final TouchFinger first = new TouchFinger(-1, 0f, 0f, 0f);
private TouchFinger second;
private double downAngle,
 lastAngle;
private float downDistanceSqd,
 lastDistanceSqd;

private int slopSqd;
private int doubleTapSlopSqd;
private int bigSlopSqd;

private int selected;
private AbstractPiece[] lastTouched;

// TODO: draw transparent piece/group with low opacity (when placing something from another Container)
//  draw transparentDraggedGroup as only outlines (lonely piece becomes draggedPiece)
public AbstractPiece transparentDraggedPiece;
public Group transparentDraggedGroup;

public void cancelHandlerDelayed()
{
   handler.removeCallbacks(onSingleTap);
   handler.removeCallbacks(onLongHold);
   handler.removeCallbacks(onLongerHoldMenuFinished);
   handler.removeCallbacks(onPanFlingFinished);
}

Animator panFlingAnimator; // TODO: or use Scroller.fling() in android.widget

private final Runnable onPanFlingFinished = ()->{
   setTouchState(TouchState.UP_NONE);
};

private final Runnable onAutomationFinished = ()->{
   setTouchState(TouchState.AUTOMATING_DONE);
};

private final Runnable onSingleTap = ()->{
   if (lastState == TouchState.UP_TAP)
      selected = playMat.selectOrDeselect(first.downX, first.downY); // perform click!
   setTouchState(TouchState.UP_NONE);
};

private final Runnable onLongerHoldMenuFinished = ()->{
   if (fingers == 0)
      setTouchState(TouchState.UP_MENU);
   else
      setTouchState(TouchState.MENU_HOLD);
};

public void setMenuGroupDrag()
{
   if (fingers == 0)
      setTouchState(TouchState.UP_MENU_GROUP);
   else
      setTouchState(TouchState.MENU_GROUP_DRAG);
}

private final Runnable onLongHold = ()->{
//   synchronized (first) { // no, everything is in UI-thread.
   if (fingers == 0)
      return;
   if (lastState == TouchState.TOUCH || lastState == TouchState.DBLTAP_TOUCH) {
      setTouchState(TouchState.HOLD);
      //PointF clickPoint = new PointF(first.downX, first.downY);
      menu.animateShowMenu(lastTouched, first.downX, first.downY,
       false, onLongerHoldMenuFinished);
   }
   else if (lastState == TouchState.TOUCH_2GR) {
      setTouchState(TouchState.HOLD_2GR);
      menu.animateShowMenu(lastTouched, first.downX, first.downY,
       true, onLongerHoldMenuFinished);
   }
   // else do nothing
//   } // synchronized(first)
};

private void setTouchState(@NonNull TouchState newCurrent)
{ // all calls to this method should be done inside a synchronized (first) {   } // no, everything is in UI-thread.
   if (lastState == TouchState.AUTOMATING) {
      if (newCurrent == TouchState.AUTOMATING_DONE)
         newCurrent = TouchState.UP_NONE;
      else
         return;
   }
   if (newCurrent == TouchState.UP_NONE) {
      fingers = 0;
      cancelHandlerDelayed();
      
      menu.hideMenu(); // cancel menu animation and hide menu
   }
//   if(newCurrent == TouchState.AUTOMATING){
//      fingers = 0;
//   }

//   if(fingers==0)
//      newCurrent = TouchState.NONE;
   lastState = newCurrent;
}

enum TouchState
{
   UP_NONE, // default state
   UP_TAP, // unknown: either single tap or first tap of a DBLTAP
   UP_MENU, // context menu is showing
   UP_MENU_GROUP, // group menu choice selected
   UP_PAN_FLING, // fling animation
   AUTOMATING, // complex animation
   AUTOMATING_DONE, // special case of UP_NONE
   
   TOUCH, // one finger down
   HOLD, // one finger down, after one delay
   MENU_HOLD, // one finger down, after two delays, context menu is showing
   DRAG, // one finger down and moved
   MENU_GROUP_DRAG, // group menu choice selected, one finger down (and moved)
   
   DBLTAP_TOUCH, // second tap of a DBLTAP: one finger down
   DBLTAP_DRAG_ZOOM, // second tap of a DBLTAP: one finger down and moved
   
   TOUCH_2, // two fingers down
   FLICK_2_ROTATE, // two fingers down
   DRAG_2_PAN_RV, // two fingers down
   PINCH_2_ZOOM_PAN_RV, // two fingers down
   
   LIFTED_PAN_RV, // one finger lifted from DRAG_2 or PINCH_2
   
   TOUCH_2GR, // two fingers down together
   HOLD_2GR, // two fingers down together
   DRAG_2GR, // two fingers down together and moved
   
   LIFTED_2GR_TAP, // one finger lifted from TOUCH_2GR
   LIFTED_2GR // one finger lifted from HOLD_2GR or DRAG_2GR
}

private void onActionDown(MotionEvent ev)
{
   first.down(ev.getPointerId(0), ev.getX(), ev.getY(),
    ev.getSize(0));
   fingers = 1;
   // TODO: maybe if it's fat enough, move directly to TOUCH_2GR and fingers = 2;
   
   lastTouched = playMat.getPieceAt(first.lastX, first.lastY);
   
   
   
   // viewConf = ViewConfiguration.get(getContext()); // TODO: do this in unPause()
   //ViewConfiguration.getJumpTapTimeout()
   //ViewConfiguration.getDoubleTapTimeout()
   // viewConf.getScaledDoubleTapSlop()
   
   switch (lastState) {
   case UP_NONE:
      setTouchState(TouchState.TOUCH);
      handler.postDelayed(onLongHold, ViewConfiguration.getLongPressTimeout());
      break;
   
   case UP_TAP:
      setTouchState(TouchState.DBLTAP_TOUCH); // set delay but even longer
      handler.postDelayed(onLongHold, ViewConfiguration.getLongPressTimeout() * 2L);
      handler.removeCallbacks(onSingleTap);
      break;
   
   
   case UP_MENU:
      setTouchState(TouchState.UP_NONE); // menu.hideMenu(); // and cancel all
      break;
   
   case UP_MENU_GROUP:
      setTouchState(TouchState.MENU_GROUP_DRAG);
      break;
   
   case UP_PAN_FLING:
      handler.removeCallbacks(onPanFlingFinished);
      // TODO: cancel fling animation without finishing it. - see android.widget.OverScroller, GestureDetector, VelocityTracker, ScrollFlingDetector
      // overScroller.forceFinished(true);
      // copy of case UP_NONE:
      setTouchState(TouchState.TOUCH);
      handler.postDelayed(onLongHold, ViewConfiguration.getLongPressTimeout());
      break;
   
   default:
      setTouchState(TouchState.UP_NONE); // cancel all
   }
}

private void onActionPointerDown(MotionEvent ev)
{
   if (fingers >= 2) { // cancel on too many fingers (3)
      fingers = 0;
      return;
   }
   // fingers == 1
   second = new TouchFinger(ev.getPointerId(ev.getActionIndex()), ev.getX(), ev.getY(),
    ev.getSize(1));
   fingers++;
   // fingers == 2
   
   // TODO: is two fingers touching at the same time 1 event or 2? - or 1 when together, 2 when apart? probably 2 events.
   
   // TODO: are fingers apart or together? (how sense that two fingers went down at the same time, as one?)
   //   check fatness of touch?
   
   float diffX = second.lastX - first.lastX;
   float diffY = second.lastY - first.lastY;
   downDistanceSqd = lastDistanceSqd = diffX * diffX + diffY * diffY;
   // Math.hypot(diffX, diffY); // Math.sqrt(diffX * diffX + diffY * diffY);
   downAngle = lastAngle = Math.atan2(diffY, diffX);
   
   // TODO: when touching two fingers together:
   //  after starting DRAG_2GR, breaking fingers apart or even lifting second finger (LIFTED_2GR) are all
   //  ignored. only first finger is listened to. (ignore all ACTION_MOVE with non-primary finger)
   //  but if a new finger is DOWN: CANCEL! see paper!
   
   boolean twoFingersTogether = downDistanceSqd < bigSlopSqd; // TODO: test this!
   
   if (twoFingersTogether) {
      long since = second.down - first.down;
      if (lastState == TouchState.TOUCH && since < ViewConfiguration.getZoomControlsTimeout()) {
         setTouchState(TouchState.TOUCH_2GR);
      }
      else {
         setTouchState(TouchState.UP_NONE); // cancel all
      }
   }
   else { // two fingers apart:
      switch (lastState) {
      case TOUCH:
         handler.removeCallbacks(onLongHold);
         setTouchState(TouchState.TOUCH_2);
         break;
      case HOLD:
         menu.hideMenu();
         setTouchState(TouchState.TOUCH_2);
         break;
      case DRAG:
         setTouchState(TouchState.TOUCH_2);
         break;
      case LIFTED_PAN_RV:
         setTouchState(TouchState.DRAG_2_PAN_RV);
         break;
      default:
         setTouchState(TouchState.UP_NONE); // cancel all
      }
   } // two fingers apart
}

private void onActionMove(MotionEvent ev)
{
   if (fingers == 1) {
      
      first.update(ev.getX(), ev.getY(), ev.getSize(0));
      
      switch (lastState) {
      
      case TOUCH: {
         
         // TODO: only after moving too much (slop) do we cancel delayedLongPress and setTouchState(DRAG).
         break;
      }
      case DBLTAP_TOUCH: {
         
         break;
      }
      case DBLTAP_DRAG_ZOOM: {
         
         break;
      }
      
      case HOLD: {
         
         break;
      }
      case DRAG: {
         
         break;
      }
      
      case MENU_GROUP_DRAG: {
         
         break;
      }
      case LIFTED_2GR: {
         
         break;
      }
      case LIFTED_2GR_TAP: {
         
         break;
      }
      
      // default: ignore!
      } // switch(lastState)
   } // fingers == 1
   else { // fingers == 2
      
      float distanceSqd;
      double angle;
      
      int id = ev.getPointerId(ev.getActionIndex());
      if (id == first.id) {
         float diffX = second.lastX - ev.getX();
         float diffY = second.lastY - ev.getY();
         distanceSqd = diffX * diffX + diffY * diffY;
         angle = Math.atan2(diffY, diffX);
         
         switch (lastState) {
         case TOUCH_2GR: {
            
            break;
         }
         case HOLD_2GR: {
            
            break;
         }
         case DRAG_2GR: {
            
            break;
         }
         
         case TOUCH_2: {
            
            break;
         }
         case FLICK_2_ROTATE: {
            setTouchState(TouchState.UP_NONE); // cancel all (primary finger)
            break;
         }
         case PINCH_2_ZOOM_PAN_RV: {
            
            break;
         }
         case DRAG_2_PAN_RV: {
            
            // TODO: for a DRAG_2_PAN_RV to become PINCH_2_ZOOM_PAN_RV, the distance between fingers
            //  has to significantly change both from lastDistance and from downDistance.
            //  don't use a ScaleGestureDetector, copy its useful code instead.
            break;
         }
         
         // default: ignore!
         } // switch(lastState)
         first.update(ev.getX(), ev.getY(), ev.getSize(ev.getActionIndex()));
      } // primary finger (fingers == 2)
      else { // non-primary finger (fingers == 2)
         float diffX = ev.getX() - first.lastX;
         float diffY = ev.getY() - first.lastY;
         distanceSqd = diffX * diffX + diffY * diffY;
         angle = Math.atan2(diffY, diffX);
         
         switch (lastState) {
         case TOUCH_2: {
            
            // TODO: see paper: slop!
            break;
         }
         case FLICK_2_ROTATE: {
            
            // TODO: only here is recentmoves of the non-primary finger. otherwise, primary finger. it resets on POINTER_DOWN and DOWN.
            // TODO: or use velocitytracker instead of recentmoves?
            break;
         }
         case PINCH_2_ZOOM_PAN_RV: {
            
            break;
         }
         case DRAG_2_PAN_RV: {
            
            // TODO: for a DRAG_2_PAN_RV to become PINCH_2_ZOOM_PAN_RV, the distance between fingers
            //  has to significantly change both from lastDistance and from downDistance.
            break;
         }
         // default: ignore!
         } // switch(lastState)
         second.update(ev.getX(), ev.getY(), ev.getSize(ev.getActionIndex()));
      } // non-primary finger
      
      // TODO: compare to last distance and angle before this
      
      lastDistanceSqd = distanceSqd;
      
      lastAngle = angle;
   } // fingers == 2
   
   // TODO: when DOUBLETAP_DRAG_ZOOM: show a zoom icon below finger. arrows pointing SW(-) and NE(+).
   
   // TODO: to get to FLICK_2_ROTATE, second.down must be after a significant delay after first.down!
   long since = second.down - first.down;
   
   
   
   // THROW (same as DRAG) // THROW_2GR (same as DRAG_2GR) - TODO: check direction which temp container to send to!
   
   
   // TODO: TouchState.UP_PAN_FLING
   
   // TODO: do this in unPause() : viewConf = ViewConfiguration.get(getContext());
   
   //ViewConfigurationCompat.getScaledHorizontalScrollFactor()
   //ViewConfigurationCompat.getScaledVerticalScrollFactor()
   
   //viewConf.getScaledPagingTouchSlop() - ? - save this for ACTION_UP/ACTION_POINTER_UP
   //viewConf.getScaledTouchSlop()
   //viewConf.getScaledHoverSlop() ViewConfigurationCompat.getScaledHoverSlop()
   
   
   
   
   // TODO: call setTouchState()!
}

private void onActionPointerUp(MotionEvent ev)
{
   fingers--;
   
   int id = ev.getPointerId(ev.getActionIndex());
   if (id == first.id) {
      // cancel on lifting the primary finger while another is still pressed TODO: only sometimes!
      fingers = 0; // TODO: unless both are lifted "at the same time"! (close enough)
      // TODO: is ViewConfiguration.getTapTimeout() useful?
      return;
   }
   
   /** TODO: copy code from here:
    * @see VelocityTracker
    * @see GestureDetector
    * @see ScaleGestureDetector
    */
   
   // TODO: after DRAG_2GR or HOLD_2GR, no CLICK_2GR!
   // TODO: when touching two fingers together:
   //  after starting DRAG_2GR, breaking fingers apart or even lifting second finger (LIFTED_2GR) are all
   //  ignored. only first finger is listened to. (ignore all ACTION_MOVE with non-primary finger)
   //  but if a new finger is DOWN: CANCEL!
   
   // THROW (same as DRAG) // THROW_2GR (same as DRAG_2GR)
   
   //ViewConfigurationCompat.getScaledMaximumFlingVelocity()
   //ViewConfigurationCompat.getScaledMinimumFlingVelocity()
   // TODO: flick - android.view.VelocityTracker: look at the code! write code that can handle diagonal movement.
   //  https://developer.android.com/reference/android/view/ViewConfiguration#getScaledMinimumFlingVelocity(int,%20int,%20int)
   
   
   // TODO: finalize rotate if second finger rotated a piece and left it rotated when lifting finger. or if flicked!
   // TODO: can lifting a second finger do something else?
   switch (lastState) {
   
   
   case DRAG: // do nothing, same as MENU_HOLD
   case MENU_HOLD: // do nothing, same as DRAG
      break;
   default:
      // TODO: drop if holding something.
      setTouchState(TouchState.UP_NONE);
   }
   
   // do not set second = null yet, do that in ACTION_UP instead.
}

private void onActionUp(MotionEvent ev)
{
   fingers = 0; // last finger up.
   // THROW (same as DRAG) // THROW_2GR (same as DRAG_2GR)
   //ViewConfigurationCompat.getScaledMaximumFlingVelocity()
   //ViewConfigurationCompat.getScaledMinimumFlingVelocity()
   // TODO: flick - android.view.VelocityTracker: look at the code! write code that can handle diagonal movement.
   //  https://developer.android.com/reference/android/view/ViewConfiguration#getScaledMinimumFlingVelocity(int,%20int,%20int)
   
   // TODO: after DRAG, no CLICK! after DRAG_2GR or HOLD_2GR, no CLICK_2GR!
   
   switch (lastState) {
   case TOUCH:
      setTouchState(TouchState.UP_TAP);
      handler.postDelayed(onSingleTap, ViewConfiguration.getDoubleTapTimeout());
      break;
   case DBLTAP_TOUCH:
      
      break;
   
   case LIFTED_2GR_TAP:
      
      break;
   case HOLD:
      menu.showMenuNow();
      // HOLD case continues in MENU_HOLD
   case MENU_HOLD:
      setTouchState(TouchState.UP_MENU);
      break;
//      case DBLTAP_DRAG_ZOOM: // same as LIFTED_PAN_RV, no, just default.
//      case LIFTED_PAN_RV: // same as DBLTAP_DRAG_ZOOM, no, just default.
   
   default:
      // TODO: drop if holding something.
      setTouchState(TouchState.UP_NONE);
   }
   
   second = null;
}

@SuppressLint("ClickableViewAccessibility")
public boolean onTouchEvent(MotionEvent ev)
{
   // TODO: EVENTS (gestures) and ACTIONS that need pairing:
   //  1-FINGER EVENTS:
   //   tap - (Runnable!) select/deselect piece, bg: deselect all but only when only 1 piece is selected, otherwise have to use back button
   //   doubletap - show one piece zoomed-in
   //   doubletouch-drag - zoom only, send touch events that zoom and scroll to ZoomEngine
   //   drag - move or rotate piece, bg: do nothing, or show Box on vertical movement
   //   drag-flick - throw piece to new(ask!) temporary container, detect flick on UP, save the last few drag events (when a temporary container already exists, require much less speed to flick, but always show an undo button attached to the temporary container!)
   //   hold - show PlayMenu
   //  2-FINGER EVENTS:
   //   tap_2GR (click with two fingers together) - select group
   //   DRAG_2GR - move selected group, bg: move closest group
   //   DRAG_2GR-flick - throw group to new(ask!) temporary container, detect flick on UP, save the last few drag events
   //   HOLD_2GR - show PlayMenu
   //   FLICK_2_ROTATE - rotate piece
   //   DRAG_2_PAN_RV (drag/rotate with two fingers apart) - scroll and rotate view, send touch events that zoom and scroll to ZoomEngine
   //   PINCH_2_ZOOM_PAN_RV - zoom and scroll and rotate view in 90 degree increments (like FLICK_2_ROTATE but without UP event needed, show indicator icon instead of rotating with outlines)
   
   //  EVENTS DURING DRAG_DROP:
   //   stationary hover over drag target - (Runnable, only a short delay - show PlayMenu)
   //   hover near to edge - (Runnable which copies itself (loops) - edge scrolling while dragging)
   //   release (drop) - (save the last few drag events, see if pieces were moved together and negative acceleration or stopped a few milliseconds, make a sound and visible indicator that the pieces did or didn't fit together.)
   
   
   //ViewConfiguration.getJumpTapTimeout()
   
   // TODO: this is probably not needed.
   int prevFingers = fingers;
   
   switch (ev.getActionMasked()) {
   case MotionEvent.ACTION_DOWN:
      if (lastState == TouchState.AUTOMATING)
         break;
      getParent().requestDisallowInterceptTouchEvent(true);
      onActionDown(ev);
      break;
   case MotionEvent.ACTION_POINTER_DOWN:
      if (fingers == 0)
         break;
      onActionPointerDown(ev);
      break;
   case MotionEvent.ACTION_MOVE:
      if (fingers == 0)
         break;
      onActionMove(ev);
      break;
   case MotionEvent.ACTION_POINTER_UP:
      if (fingers == 0)
         break;
      onActionPointerUp(ev);
      break;
   case MotionEvent.ACTION_UP:
      if (fingers == 0)
         break;
      getParent().requestDisallowInterceptTouchEvent(false);
      onActionUp(ev);
      break;
   case MotionEvent.ACTION_CANCEL:
      fingers = 0;
   } // switch(ev.getActionMasked())
   
   // TODO: this is probably not needed.
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
   
   float w = bottomRight.x - topLeft.x;
   float h = bottomRight.y - topLeft.y;
   topLeft.length();
   
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
   
   
   // TODO: when highlighting a group, do all these things:
   //  draw a line around the group (with quite a bit of margin).
   //  show the name of the group
   //  draw around the pieces with a color, on a layer below all pieces (like BorderDrawable but also around
   //   puzzle-outer edges), or on a layer above all pieces (line border, not gradient. I have to make special path segments different from PieceJedge for this expanded outline-border)
   //  fade other pieces (give them 80% opacity), but note especially the other pieces that overlap pieces in the group
   //   but are not in the group.
   
   
   // TODO: all pieces have a cached UnrotatedFullSizeGraphics for the box and this draw method.
   //  Does it all fit in memory? Perhaps nothing can be cached.
   
   // TODO: ALL pieces are drawn as outlines when zooming (state==PINCH_2_ZOOM_PAN_RV)! (same outlines as when rotating a piece)
   
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
      group.drawOnPlayMat(canvas);
   }
   for (SinglePiece piece: playMat.singlePieces) {
      canvas.drawBitmap(piece.getUnrotatedFullSizeGraphics(),
       piece.getPlayMatTranslationAndRotation(), null);
   }
   
   // TODO: reset scale and translation due to zoom/pan.
   //canvas.restore();
}
}
