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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
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
public class PlayMatFragment
 extends Fragment
 implements Frag
{
/**
 * Some older devices needs a small delay between UI widget updates
 * and a change of the status and navigation bar.
 */
private static final int UI_ANIMATION_DELAY = 300;

private static final int INITIAL_AUTO_HIDE_DELAY_MILLIS = 100;

private class Views
{
   final Handler mainHandler;
   
   final FrameLayout frameFullscreenLayout;
   final LinearLayout llvPlayMatFragment;
   
   final NestedScrollView zoomAndScrollView; // TODO: not NestedScrollView, something else!
   final SurfaceView srcPlayMat;
   
   final ImageView imgSeparator;
   final LinearLayout llhBottom;
   
   final ImageView imgBoxCover;
   final RecyclerView lstBox;
   final SurfaceView srcMiniMap;
   
   final RecyclerView.LayoutManager bigBoxLayout;
   final RecyclerView.LayoutManager miniBoxLayout;
   final OnItemTouchListener bigBoxItemTouchListener;
   final OnItemTouchListener miniBoxItemTouchListener;
   //   final OnFlingListener boxFlingListener;
   final LayoutParams boxParams;
   final int miniBoxHeight;
   boolean big = true; // initially true, so setMiniBoxLayout works.
   
   public Views(FrameLayout frameFullscreenLayout, LinearLayout llvPlayMatFragment,
    NestedScrollView zoomAndScrollView, SurfaceView srcPlayMat,
    ImageView imgSeparator, LinearLayout llhBottom,
    ImageView imgBoxCover, RecyclerView lstBox, SurfaceView srcMiniMap,
    Context ctx)
   {
      this.frameFullscreenLayout = frameFullscreenLayout;
      this.imgSeparator = imgSeparator;
      this.llhBottom = llhBottom;
      this.mainHandler = new Handler(Looper.getMainLooper());
      
      this.zoomAndScrollView = zoomAndScrollView;
      this.srcPlayMat = srcPlayMat;
      this.imgBoxCover = imgBoxCover;
      this.lstBox = lstBox;
      this.boxParams = lstBox.getLayoutParams();
      this.miniBoxHeight = boxParams.height;
      this.srcMiniMap = srcMiniMap;
      this.llvPlayMatFragment = llvPlayMatFragment;
      
      this.bigBoxLayout = new GridLayoutManager(ctx, 3, // TODO: calculate optimal spanCount!
       RecyclerView.VERTICAL, false);
      this.miniBoxLayout = new LinearLayoutManager(ctx, RecyclerView.HORIZONTAL, false);
      this.bigBoxItemTouchListener = new OnItemTouchListener()
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
      this.miniBoxItemTouchListener = new OnItemTouchListener()
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
}

private Views ui;

BoxAdapter bigBoxAdapter;
MiniBoxAdapter miniBoxAdapter;

private boolean fullscreenMode;

/**
 * Whether or not the system UI should be auto-hidden after
 * {@link #autoFullscreenDelayMillis} milliseconds.
 */
private boolean autoFullscreen = true;

/**
 * If {@link #autoFullscreen} is set, the number of milliseconds to wait after
 * user interaction before hiding the system UI.
 */
private int autoFullscreenDelayMillis = 3000;

private void endFullscreen()
{
   fullscreenMode = false;
   
   // Show the system bar
   ui.llvPlayMatFragment.setSystemUiVisibility(
    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
     | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
}

private final Runnable runStartFullscreen = this::startFullscreen;

private void startFullscreen()
{
   fullscreenMode = true;
   
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
}

/**
 * Schedules a call to hide() in delay milliseconds, canceling any
 * previously scheduled calls.
 */
private void delayAutoHideUI(long millis)
{
   ui.mainHandler.removeCallbacks(runStartFullscreen);
   if (autoFullscreen)
      ui.mainHandler.postDelayed(runStartFullscreen, millis);
}

/**
 * Touch listener to use for in-layout UI controls to delay hiding the
 * system UI. This is to prevent the jarring behavior of controls going away
 * while interacting with activity UI.
 */
@SuppressLint("ClickableViewAccessibility")
private final View.OnTouchListener autoHideUITouchListener =
 (view, motionEvent)->{
    delayAutoHideUI(autoFullscreenDelayMillis);
    return false;
 };

//public static PlayMatFragment newInstance(/* param1, String param2*/)
//{
//   return new PlayMatFragment();
//}

public void startGame(@NonNull ImagePuzzle imagePuzzle)
{
   // initialize PuzzleGraphics
   PuzzleGraphics.init(startedGame = imagePuzzle);
   
   // initialize Box
   bigBoxAdapter = new BoxAdapter(imagePuzzle.singlePiecesContainer);
   miniBoxAdapter = new MiniBoxAdapter(imagePuzzle.singlePiecesContainer);
   retractBox();
   
   // show everything (layout is INVISIBLE before startGame)
   ui.llvPlayMatFragment.setVisibility(View.VISIBLE);
}

public PlayMatFragment()
{
   // Required empty public constructor
}

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
{
   // Inflate the layout for this fragment
   return inflater.inflate(R.layout.fragment_playmat, container, false);
}

@Override

public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
{
   super.onViewCreated(view, savedInstanceState);
   fullscreenMode = false;
   
   ui = new Views(
    view.findViewById(R.id.frameFullscreenLayout),
    view.findViewById(R.id.llvPlayMatFragment),
    view.findViewById(R.id.zoomAndScrollView),
    view.findViewById(R.id.srcPlayMat),
    view.findViewById(R.id.imgSeparator),
    view.findViewById(R.id.llhBottom),
    view.findViewById(R.id.imgBoxCover),
    view.findViewById(R.id.lstBox),
    view.findViewById(R.id.srcMiniMap),
    requireContext()
   );
   
   // Set up the user interaction to manually show or hide the system UI.
   // TODO: ui.layoutPlayMatFragment.setOnClickListener(v->toggle());
   
   // Upon interacting with UI controls, delay any scheduled hide()
   // operations to prevent the jarring behavior of controls going away
   // while interacting with the UI.
//   ui.btnDummy.setOnTouchListener(autoHideTouchListener);
}

public void onCreate(@Nullable Bundle savedInstanceState)
{
   super.onCreate(savedInstanceState);
   
}

private ImagePuzzle startedGame;

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
   if (autoFullscreen)
      delayAutoHideUI(INITIAL_AUTO_HIDE_DELAY_MILLIS);
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
   endFullscreen();
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
   if (fullscreenMode) {
      endFullscreen();
   }
   else {
      startFullscreen();
   }
}

public boolean isAutoFullscreen()
{
   return autoFullscreen;
}

public void setAutoFullscreen(boolean autoFullscreen)
{
   this.autoFullscreen = autoFullscreen;
}

public int getAutoFullscreenDelayMillis()
{
   return autoFullscreenDelayMillis;
}

public void setAutoFullscreenDelayMillis(int autoHideDelayMillis)
{
   this.autoFullscreenDelayMillis = autoHideDelayMillis;
}

void expandBox()
{
   ui.imgBoxCover.setVisibility(View.GONE);
   ui.srcMiniMap.setVisibility(View.GONE);
   
   ui.lstBox.setAdapter(bigBoxAdapter);
   ui.setBigBoxLayout(LayoutParams.MATCH_PARENT);
}

void retractBox()
{
   // TODO: check prefs before showing minimap and boxcover
   ui.imgBoxCover.setVisibility(View.VISIBLE);
   ui.srcMiniMap.setVisibility(View.VISIBLE);
   
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

private void save()
{

}

public void handleOnBackPressed(BackCallback callback)
{
   save();
   
   new AlertDialog.Builder(requireContext())
    .setNegativeButton(R.string.btnLeaveNot,
     (dialog, which)->dialog.cancel())
    .setNeutralButton(R.string.btnLeaveApp,
     (dialog, which)->callback.goBackQuit())
    .setPositiveButton(R.string.btnLeaveToMenu,
     (dialog, which)->callback.goBackToMenu())
    .show();
}
}
