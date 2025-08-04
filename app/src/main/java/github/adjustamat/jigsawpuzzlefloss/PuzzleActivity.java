package github.adjustamat.jigsawpuzzlefloss;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Size;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnFlingListener;
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener;

import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;

import java.util.Random;

import github.adjustamat.jigsawpuzzlefloss.db.DB;
import github.adjustamat.jigsawpuzzlefloss.db.Global;
import github.adjustamat.jigsawpuzzlefloss.game.ImagePuzzle;
import github.adjustamat.jigsawpuzzlefloss.ui.BoxAdapter;
import github.adjustamat.jigsawpuzzlefloss.ui.BoxAdapter.MiniBoxAdapter;
import github.adjustamat.jigsawpuzzlefloss.ui.MiniMapView;
import github.adjustamat.jigsawpuzzlefloss.ui.PlayMatView;
import github.adjustamat.jigsawpuzzlefloss.ui.PlayMenu;
import github.adjustamat.jigsawpuzzlefloss.ui.PuzzleGraphics;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PuzzleActivity
 extends AppCompatActivity
{
private static final int INITIAL_AUTO_HIDE_DELAY_MILLIS = 100;

private Handler mainHandler;

private class Views
{
   final FrameLayout frameFullscreenLayout;
   final LinearLayout llvPuzzleActivity;
   
   //final ZoomLayout viewPlayMatParent;
   final PlayMatView viewPlayMat;
   
   final ImageView imgSeparator;
   final LinearLayout llhBottom;
   
   final ImageView imgBoxCover;
   final RecyclerView lstBox;
   final MiniMapView viewMiniMap;
   
   final RecyclerView.LayoutManager bigBoxLayout;
   final RecyclerView.LayoutManager miniBoxLayout;
   final OnItemTouchListener bigBoxItemTouchListener;
   final OnItemTouchListener miniBoxItemTouchListener;
   final LayoutParams boxParams;
   final int miniBoxHeight;
   boolean big = true; // initially true, so setMiniBoxLayout works.
   
   public Views(Context ctx)
   {
      this.frameFullscreenLayout = findViewById(R.id.frameFullscreenLayout);
      this.llvPuzzleActivity = findViewById(R.id.llvPuzzleActivity);
      
      // TODO: construct PlayMatView here! Combine ZoomLayout into PlayMatView.
//      this.viewPlayMatParent = findViewById(R.id.viewPlayMatParent);
      this.viewPlayMat = findViewById(R.id.viewPlayMat);
      
      this.imgSeparator = findViewById(R.id.imgSeparator);
      this.llhBottom = findViewById(R.id.llhBottom);
      this.imgBoxCover = findViewById(R.id.imgBoxCover);
      this.lstBox = findViewById(R.id.lstBox);
      this.viewMiniMap = findViewById(R.id.viewMiniMap);
      
      this.boxParams = lstBox.getLayoutParams();
      this.miniBoxHeight = boxParams.height;
      
      this.bigBoxLayout = new GridLayoutManager(ctx,
       3, // TODO: calculate optimal spanCount!
       RecyclerView.VERTICAL, false);
      this.miniBoxLayout = new LinearLayoutManager(ctx, RecyclerView.HORIZONTAL, false);
      
      // TODO: make it so that the box can switch from minibox to bigbox and back with vertical scrolling.
      //  also enable zooming in bigbox by changing spanCount on pinch events!
      this.bigBoxItemTouchListener = new OnItemTouchListener()
      {
         public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e)
         {
            return false;// TODO!
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
            return false;// TODO!
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
                  expandBoxFully();
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
         lstBox.setAdapter(bigBoxAdapter);
         lstBox.setLayoutManager(bigBoxLayout);
         lstBox.removeOnItemTouchListener(miniBoxItemTouchListener);
         lstBox.addOnItemTouchListener(bigBoxItemTouchListener);
      }
      if (height == LayoutParams.MATCH_PARENT)
         viewPlayMat.setVisibility(View.GONE);
      else
         viewPlayMat.setVisibility(View.VISIBLE); // TODO: change params of viewPlayMatParent!
      
      imgBoxCover.setVisibility(View.GONE);
      viewMiniMap.setVisibility(View.GONE);
      
      boxParams.height = height;
      lstBox.setLayoutParams(boxParams);
   }
   
   private void setMiniBoxLayout()
   {
      // TODO: check prefs before showing minimap and boxcover. also check prefs in onResume or onStart!
      imgBoxCover.setVisibility(View.VISIBLE);
      viewMiniMap.setVisibility(View.VISIBLE);
      
      viewPlayMat.setVisibility(View.VISIBLE); // TODO: change params of viewPlayMatParent?
      
      if (big) {
         big = false;
         // TODO: which order? before changing adapter and layoutmanager, turn it off so that the old object doesn't get notified about the first change.
         lstBox.setAdapter(miniBoxAdapter);
         lstBox.setLayoutManager(miniBoxLayout);
         lstBox.removeOnItemTouchListener(bigBoxItemTouchListener);
         lstBox.addOnItemTouchListener(miniBoxItemTouchListener);
         
         boxParams.height = miniBoxHeight;
         lstBox.setLayoutParams(boxParams);
      }
   }
}

private Views ui;
private PlayMenu menu;

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
   ui.llvPuzzleActivity.setSystemUiVisibility(
    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
     | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
}

private final Runnable runStartFullscreen = this::startFullscreen;

private void startFullscreen()
{
   fullscreenMode = true;
   
   Window window = getWindow();
   if (window != null) {
      int flags = View.SYSTEM_UI_FLAG_LOW_PROFILE
       | View.SYSTEM_UI_FLAG_FULLSCREEN
       | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
       | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
       | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
       | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
      window.getDecorView().setSystemUiVisibility(flags);
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
   mainHandler.removeCallbacks(runStartFullscreen);
   if (autoFullscreen)
      mainHandler.postDelayed(runStartFullscreen, millis);
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

public static final String EXTRA_GAME_ID = "mat.puzzle.id";
public static final String EXTRA_GENERATE_WIDTH = "mat.puzzle.width";
public static final String EXTRA_GENERATE_HEIGHT = "mat.puzzle.height";
public static final String EXTRA_GENERATE_BITMAP_WIDTH = "mat.puzzle.bitmap.width";
public static final String EXTRA_GENERATE_BITMAP_HEIGHT = "mat.puzzle.bitmap.height";

private void readIntent()
{
   Intent intent = getIntent();
   
   int gameID = intent.getIntExtra(EXTRA_GAME_ID, -1);
   if (gameID > -1) {
      startSavedGame(gameID);
   }
   else {
      startNewGame(
       intent.getIntExtra(EXTRA_GENERATE_WIDTH, -1),
       intent.getIntExtra(EXTRA_GENERATE_HEIGHT, -1),
       intent.getData(),
       new Size(intent.getIntExtra(EXTRA_GENERATE_BITMAP_WIDTH, -1),
        intent.getIntExtra(EXTRA_GENERATE_BITMAP_HEIGHT, -1))
      );
   }
}

private DB dbInstance;

public DB db()
{
   if (dbInstance == null) {
      dbInstance = new DB(this);
   }
   return dbInstance;
}

private void startSavedGame(int gameID)
{
   currentGame = ImagePuzzle.loadFromDatabase(gameID, db());
   startGame();
}

private void startNewGame(int width, int height, Uri bitmapUri, Size bitmapSize)
{
   currentGame = ImagePuzzle.generateNewPuzzle(width, height,
    bitmapUri, bitmapSize, new Random(), db().createNewSaveGame());
   saveGame();
   startGame();
}

private void startGame()
{
   // initialize PuzzleGraphics
   PuzzleGraphics.init(currentGame, this);
   
   // initialize Box
   bigBoxAdapter = new BoxAdapter(currentGame.singlePiecesContainer);
   miniBoxAdapter = new MiniBoxAdapter(currentGame.singlePiecesContainer);
   retractBox();
}

public void onConfigurationChanged(@NonNull Configuration c)
{
   //c.orientation is checked - see AndroidManifest.xml
   super.onConfigurationChanged(c);
   // TODO: also use getResources().getConfiguration() in onResume()
   
   //c.densityDpi
   // see DisplayMetrics.DENSITY_*
   //getResources().getDisplayMetrics().densityDpi
   //getResources().getDisplayMetrics().density
   //getResources().getDisplayMetrics().xdpi
   //getResources().getDisplayMetrics().ydpi
   
   //c.screenHeightDp
   //c.screenWidthDp
   //getResources().getDisplayMetrics().heightPixels
   //getResources().getDisplayMetrics().widthPixels
   
}

public void onCreate(@Nullable Bundle savedInstanceState)
{
   EdgeToEdge.enable(this);
   super.onCreate(savedInstanceState);
   setContentView(R.layout.act_puzzle);
   
   Glide.get(this).setMemoryCategory(MemoryCategory.HIGH);
   
   fullscreenMode = false;
   
   mainHandler = new Handler(Looper.getMainLooper());
   
   ui = new Views(this);
   menu = new PlayMenu(this);
   
   // Set up the user interaction to manually show or hide the system UI.
   // ui.layoutPlayMatFragment.setOnClickListener(v->toggle());
   
   // Upon interacting with UI controls, delay any scheduled hide()
   // operations to prevent the jarring behavior of controls going away
   // while interacting with the UI.
   // ui.btnDummy.setOnTouchListener(autoHideTouchListener);
   
   ViewCompat.setOnApplyWindowInsetsListener(ui.frameFullscreenLayout,
    (v, insets)->{
       Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
       v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
       return insets;
    });
   
   Global.initEverything(this);
   
   getOnBackPressedDispatcher().addCallback(onBackPressed);
   
   readIntent();
}

private ImagePuzzle currentGame;

@Override
public void onResume()
{
   super.onResume();
   Window window = getWindow();
   if (window != null) {
      window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
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
   Window window = getWindow();
   if (window != null) {
      window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
      
      // Clear the systemUiVisibility flag
      window.getDecorView().setSystemUiVisibility(0);
   }
   endFullscreen();
}

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

void expandBoxFully()
{
   
   
   
   ui.setBigBoxLayout(LayoutParams.MATCH_PARENT);
}

void retractBox()
{
   
   
   ui.setMiniBoxLayout();
}

private void saveGame()
{
   db().saveGame(currentGame);
}

private final OnBackPressedCallback onBackPressed = new OnBackPressedCallback(true)
{
   private AlertDialog alertDialog;
   
   public void handleOnBackPressed()
   {
      if (alertDialog != null) {
         alertDialog.cancel();
         alertDialog = null;
         return;
      }
      
      if (ui.viewPlayMat.handleOnBackPressed())
         return;
      
      saveGame();
      
      alertDialog =
       new AlertDialog.Builder(PuzzleActivity.this)
        .setNegativeButton(R.string.btnLeaveNot,
         (dialog, which)->{
            alertDialog = null;
            //dialog.cancel();
         })
        .setNeutralButton(R.string.btnLeaveApp,
         (dialog, which)->finishAndRemoveTask())
        .setPositiveButton(R.string.btnLeaveToMenu,
         (dialog, which)->{
            if (isTaskRoot()) {
               alertDialog = null;
               startActivity(new Intent(getApplicationContext(), Act.class));
            }
            else
               finish();
         })
        .show();
   }
};
}
