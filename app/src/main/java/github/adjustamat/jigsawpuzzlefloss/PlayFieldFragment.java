package github.adjustamat.jigsawpuzzlefloss;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnFlingListener;
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener;

import github.adjustamat.jigsawpuzzlefloss.game.ImagePuzzle;
import github.adjustamat.jigsawpuzzlefloss.ui.BoxAdapter;
import github.adjustamat.jigsawpuzzlefloss.ui.BoxAdapter.MiniBoxAdapter;
import github.adjustamat.jigsawpuzzlefloss.ui.PuzzleGraphics;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PlayFieldFragment
 extends Fragment
{
/**
 * Some older devices needs a small delay between UI widget updates
 * and a change of the status and navigation bar.
 */
private static final int UI_ANIMATION_DELAY = 300;

private static final int INITIAL_AUTO_HIDE_DELAY_MILLIS = 100;

private class Views
{
   final Handler mHideHandler;
   
   final View layoutPlayFieldFragment;
   final NestedScrollView zoomAndScrollView;
   final SurfaceView srfPlayingField;
   final ImageView imgBoxCover;
   final RecyclerView lstBox;
   final SurfaceView srfMiniMap;
   
   final RecyclerView.LayoutManager bigBoxLayout;
   final RecyclerView.LayoutManager miniBoxLayout;
   final OnItemTouchListener bigBoxItemTouchListener;
   final OnItemTouchListener miniBoxItemTouchListener;
//   final OnFlingListener boxFlingListener;
   final LayoutParams boxParams;
   final int miniBoxHeight;
   boolean big = true; // initially true, so setMiniBoxLayout works.
   
   
   public Views(NestedScrollView zoomAndScrollView, SurfaceView srfPlayingField,
    ImageView imgBoxCover, RecyclerView lstBox, SurfaceView srfMiniMap, View layoutPlayFieldFragment,
   Context ctx)
   {
      this.mHideHandler = new Handler(Looper.getMainLooper());
      
      this.zoomAndScrollView = zoomAndScrollView;
      this.srfPlayingField = srfPlayingField;
      this.imgBoxCover = imgBoxCover;
      this.lstBox = lstBox;
      boxParams = lstBox.getLayoutParams();
      miniBoxHeight = boxParams.height;
      this.srfMiniMap = srfMiniMap;
      this.layoutPlayFieldFragment = layoutPlayFieldFragment;
      
      bigBoxLayout = new GridLayoutManager(ctx, 3, // TODO: calculate optimal spanCount!
       RecyclerView.VERTICAL, false);
      miniBoxLayout = new LinearLayoutManager(ctx, RecyclerView.HORIZONTAL, false);
      bigBoxItemTouchListener = new OnItemTouchListener()
      {
         public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e)
         {
            return false;
         }
         
         public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e)
         {
            // TODO!
         }
         
         public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept)
         {
            // TODO!
         }
      };
      miniBoxItemTouchListener = new OnItemTouchListener()
      {
         public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e)
         {
            return false;
         }
         
         public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e)
         {
            // TODO!
         }
         
         public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept)
         {
            // TODO!
         }
      };
      lstBox.setOnFlingListener(new OnFlingListener()
      {
         public boolean onFling(int velocityX, int velocityY)
         {
            
            if (Math.abs(velocityY) > Math.abs(velocityX)) { // vertical flings only
               if (velocityY < 0) { // fling up
                  expandBox();
               } // fling up
               else { // fling down
                  retractBox();
               } // fling down
            }
            return false;
         }
      });
//      boxFlingListener = new OnFlingListener()
//      {
//         public boolean onFling(int velocityX, int velocityY)
//         {
//
//            if (Math.abs(velocityY) > Math.abs(velocityX)) { // vertical flings only
//               if (velocityY < 0) { // fling up
//                  expandBox();
//               } // fling up
//               else { // fling down
//                  retractBox();
//               } // fling down
//            }
//            return false;
//         }
//      };
   }
   
   private void setBigBoxLayout(int height)
   {
      if (!big) {
         big = true;
         lstBox.setLayoutManager(bigBoxLayout);
         lstBox.removeOnItemTouchListener(miniBoxItemTouchListener);
         lstBox.addOnItemTouchListener(bigBoxItemTouchListener);
//         lstBox.setOnFlingListener(bigBoxFlingListener);
      }
      boxParams.height = height;
      if (height == LayoutParams.MATCH_PARENT)
         zoomAndScrollView.setVisibility(View.GONE);
      else
         zoomAndScrollView.setVisibility(View.VISIBLE);
      lstBox.setLayoutParams(boxParams);
   }
   
   private void setMiniBoxLayout()
   {
      if (big) {
         big = false;
         lstBox.setLayoutManager(miniBoxLayout);
         lstBox.removeOnItemTouchListener(bigBoxItemTouchListener);
         lstBox.addOnItemTouchListener(miniBoxItemTouchListener);
//         lstBox.setOnFlingListener(boxFlingListener);
         boxParams.height = miniBoxHeight;
         zoomAndScrollView.setVisibility(View.VISIBLE);
         lstBox.setLayoutParams(boxParams);
      }
   }
   //   public Views(View layoutPlayFieldFragment, View llhFullscreenButtons, Button btnDummy)
//   {
//
//
//      this.layoutPlayFieldFragment = layoutPlayFieldFragment;
//      this.llhFullscreenButtons = llhFullscreenButtons;
//      this.btnDummy = btnDummy;
//   }
}

private Views ui;

BoxAdapter bigBoxAdapter;
MiniBoxAdapter miniBoxAdapter;

private boolean mVisible;

/**
 * Whether or not the system UI should be auto-hidden after
 * {@link #autoHideDelayMillis} milliseconds.
 */
private boolean autoHide = true;

/**
 * If {@link #autoHide} is set, the number of milliseconds to wait after
 * user interaction before hiding the system UI.
 */
private int autoHideDelayMillis = 3000;

/**
 * Delayed removal of status and navigation bar
 */
//@SuppressLint("InlinedApi")
// Note that some of these constants are new as of API 16 (Jelly Bean)
// and API 19 (KitKat). It is safe to use them, as they are inlined
// at compile-time and do nothing on earlier devices.
private final Runnable runHideSystemUIAndActionBar = ()->{
   Activity activity = getActivity();
   if (activity != null && activity.getWindow() != null) {
      int flags = View.SYSTEM_UI_FLAG_LOW_PROFILE
       | View.SYSTEM_UI_FLAG_FULLSCREEN
       | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
       | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
       | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
       | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
      activity.getWindow().getDecorView().setSystemUiVisibility(flags);
   }
   ActionBar actionBar = getSupportActionBar();
   if (actionBar != null)
      actionBar.hide();
};

/**
 * Delayed display of mControlsView and ActionBar
 */
private final Runnable runShowActionBarAndControlsView = ()->{
   ActionBar actionBar = getSupportActionBar();
   if (actionBar != null) {
      actionBar.show();
   }
//   ui.llhFullscreenButtons.setVisibility(View.VISIBLE);
};

private final Runnable runHideMethod = this::hide;

/**
 * Touch listener to use for in-layout UI controls to delay hiding the
 * system UI. This is to prevent the jarring behavior of controls going away
 * while interacting with activity UI.
 */
@SuppressLint("ClickableViewAccessibility")
private final View.OnTouchListener delayHideTouchListener =
 (view, motionEvent)->{
    delayAutoHide(autoHideDelayMillis);
    return false;
 };

public PlayFieldFragment()
{
   // Required empty public constructor
}

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
{
   // Inflate the layout for this fragment
   return inflater.inflate(R.layout.fragment_playing_field, container, false);
}

@Override
@SuppressLint("ClickableViewAccessibility")
public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
{
   super.onViewCreated(view, savedInstanceState);
   mVisible = true;
   
   ui = new Views(
    view.findViewById(R.id.zoomAndScrollView),
    view.findViewById(R.id.srcPlayField),
    view.findViewById(R.id.imgBoxCover),
    view.findViewById(R.id.lstBox),
    view.findViewById(R.id.srcMiniMap),
    view.findViewById(R.id.layoutPlayFieldFragment),
    requireContext());
   
   // Set up the user interaction to manually show or hide the system UI.
   ui.layoutPlayFieldFragment.setOnClickListener(v->toggle());
   
   // Upon interacting with UI controls, delay any scheduled hide()
   // operations to prevent the jarring behavior of controls going away
   // while interacting with the UI.
//   ui.btnDummy.setOnTouchListener(delayHideTouchListener);
}

public void startGame(@NonNull ImagePuzzle imagePuzzle)
{
   // initialize PuzzleGraphics
   PuzzleGraphics.init(imagePuzzle);
   
   // initialize Box
   bigBoxAdapter = new BoxAdapter(imagePuzzle.singlePiecesContainer);
   miniBoxAdapter = new MiniBoxAdapter(imagePuzzle.singlePiecesContainer);
   retractBox();
   
   // show everything (layout is INVISIBLE before startGame)
   ui.layoutPlayFieldFragment.setVisibility(View.VISIBLE);
}

@Override
public void onResume()
{
   super.onResume();
   if (getActivity() != null && getActivity().getWindow() != null) {
      getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
   }
   
   // Trigger the initial hide() shortly after the activity has been
   // created, to briefly hint to the user that UI controls
   // are available.
   if (autoHide)
      delayAutoHide(INITIAL_AUTO_HIDE_DELAY_MILLIS);
}

@Override
public void onPause()
{
   super.onPause();
   if (getActivity() != null && getActivity().getWindow() != null) {
      getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
      
      // Clear the systemUiVisibility flag
      getActivity().getWindow().getDecorView().setSystemUiVisibility(0);
   }
   show();
}

//@Override
//public void onDestroy()
//{
//   super.onDestroy();
//   mContentView = null;
//   mControlsView = null;
//}

private void toggle()
{
   if (mVisible) {
      hide();
   }
   else {
      show();
   }
}

private void hide()
{
   // Hide UI first
   ActionBar actionBar = getSupportActionBar();
   if (actionBar != null) {
      actionBar.hide();
   }
//   ui.llhFullscreenButtons.setVisibility(View.GONE);
   mVisible = false;
   
   // Schedule a runnable to remove the status and navigation bar after a delay
   ui.mHideHandler.removeCallbacks(runShowActionBarAndControlsView);
   ui.mHideHandler.postDelayed(runHideSystemUIAndActionBar, UI_ANIMATION_DELAY);
}

private void show()
{
   // Show the system bar
   ui.layoutPlayFieldFragment.setSystemUiVisibility(
    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
     | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
   mVisible = true;
   
   // Schedule a runnable to display UI elements after a delay
   ui.mHideHandler.removeCallbacks(runHideSystemUIAndActionBar);
   ui.mHideHandler.postDelayed(runShowActionBarAndControlsView, UI_ANIMATION_DELAY);
   // TODO: this method - show() - both shows actionbar and delays showing actionbar.
   ActionBar actionBar = getSupportActionBar();
   if (actionBar != null) {
      actionBar.show();
   }
}

/**
 * Schedules a call to hide() in delay milliseconds, canceling any
 * previously scheduled calls.
 */
private void delayAutoHide(long millis)
{
   if (!autoHide)
      return;
   //if(ui.mHideHandler.hasCallbacks(runHideMethod))
   schedule(runHideMethod, millis);
}

private void schedule(Runnable runnable, long delay)
{
   ui.mHideHandler.removeCallbacks(runHideMethod);
   ui.mHideHandler.removeCallbacks(runHideSystemUIAndActionBar);
   ui.mHideHandler.removeCallbacks(runShowActionBarAndControlsView);
   ui.mHideHandler.postDelayed(runnable, delay);
}

public boolean isAutoHide()
{
   return autoHide;
}

public void setAutoHide(boolean autoHide)
{
   this.autoHide = autoHide;
}

public int getAutoHideDelayMillis()
{
   return autoHideDelayMillis;
}

public void setAutoHideDelayMillis(int autoHideDelayMillis)
{
   this.autoHideDelayMillis = autoHideDelayMillis;
}

void expandBox()
{
   ui.imgBoxCover.setVisibility(View.GONE);
   ui.srfMiniMap.setVisibility(View.GONE);
   
   ui.lstBox.setAdapter(bigBoxAdapter);
   ui.setBigBoxLayout(LayoutParams.MATCH_PARENT);
}

void retractBox()
{
   // TODO: check prefs before showing minimap and boxcover
   ui.imgBoxCover.setVisibility(View.VISIBLE);
   ui.srfMiniMap.setVisibility(View.VISIBLE);
   
   ui.lstBox.setAdapter(miniBoxAdapter);
   ui.setMiniBoxLayout();
}

@Nullable
private ActionBar getSupportActionBar()
{
   ActionBar actionBar = null;
   if (getActivity() instanceof AppCompatActivity) {
      AppCompatActivity activity = (AppCompatActivity) getActivity();
      actionBar = activity.getSupportActionBar();
   }
   return actionBar;
}
}
