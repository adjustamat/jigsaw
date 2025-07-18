package github.adjustamat.jigsawpuzzlefloss;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;

import github.adjustamat.jigsawpuzzlefloss.Frag.BackCallback;
import github.adjustamat.jigsawpuzzlefloss.db.DB;
import github.adjustamat.jigsawpuzzlefloss.db.Global;

public class Act
 extends AppCompatActivity
 implements BackCallback
{
//private boolean everShowedMenu = false;
private Frag currentFrag = null;

private final OnBackPressedCallback onBackPressed = new OnBackPressedCallback(true)
{
   public void handleOnBackPressed()
   {
      if (currentFrag != null)
         currentFrag.handleOnBackPressed(Act.this);
      else
         goBackQuit();
   }
};

@Override
protected void onCreate(Bundle savedInstanceState)
{
   super.onCreate(savedInstanceState);
   EdgeToEdge.enable(this);
   setContentView(R.layout.act);
   Global.initEverything(this);
   getOnBackPressedDispatcher().addCallback(onBackPressed);
   Glide.get(this).setMemoryCategory(MemoryCategory.NORMAL);

//   FragmentManager manager = getSupportFragmentManager();
//   if(manager.getBackStackEntryCount() > 0){
//      showFrag(manager.get); // check state in prefs, or check intent.
//   }else {
   showNewMenu();
//   }
}

void showNewMenu()
{
   //everShowedMenu = true;
   showFrag(new MainMenuFragment());
}

void showNewGenerator(Uri image)
{
   showFrag(GeneratorFragment.newInstance(image));
}

void showNewPrefs()
{
   showFrag(new PrefsFragment());
}

//public void generateAndShowNewPuzzle(Point puzzleSize, Bitmap croppedBitmap, boolean cropped, Uri bitmapUri)
//{
//   int bitmapID;
//   if (cropped)
//      // save cropped image in ctx.getFilesDir()
//      bitmapID = db().saveCroppedBitmap(bitmapUri, croppedBitmap, this);
//   else
//      bitmapID = db().getBitmapID(bitmapUri);
//
//   FragmentManager manager = getSupportFragmentManager();
//   manager.popBackStack(); // pop GeneratorFragment!
//
//   ImagePuzzle generated = ImagePuzzle.generateNewPuzzle(puzzleSize.x, puzzleSize.y,
//    croppedBitmap, bitmapID, new Random());
//
//   showPuzzle(generated); // show new PuzzleActivity
//}

//void showPuzzle(ImagePuzzle imagePuzzle)
//{
//   if (startedGame == null)
//      startedGame = new PuzzleActivity();
//   if (startedPuzzle != imagePuzzle)
//      startedGame.startGame(startedPuzzle = imagePuzzle);
//   showFrag(startedGame);
//   bitmapCache.clear();
//}

public void gotoPuzzleFromGenerator(Uri gameBitmapUri, int width, int height, int bitmapWidth, int bitmapHeight)
{
   FragmentManager manager = getSupportFragmentManager();
   manager.popBackStack(); // pop the GeneratorFragment so we return to MainMenuFragment from PuzzleActivity!
   Intent intent = new Intent(getApplicationContext(), PuzzleActivity.class);
   intent.setData(gameBitmapUri);
   intent.putExtra(PuzzleActivity.EXTRA_GENERATE_WIDTH, width);
   intent.putExtra(PuzzleActivity.EXTRA_GENERATE_HEIGHT, height);
   intent.putExtra(PuzzleActivity.EXTRA_GENERATE_BITMAP_WIDTH, bitmapWidth);
   intent.putExtra(PuzzleActivity.EXTRA_GENERATE_BITMAP_HEIGHT, bitmapHeight);
   startActivity(intent);
}

public void gotoPuzzleFromMenu(int gameID)
{
   Intent intent = new Intent(getApplicationContext(), PuzzleActivity.class);
   intent.putExtra(PuzzleActivity.EXTRA_GAME_ID, gameID);
   startActivity(intent);
}

void showFrag(Frag frag)
{
   FragmentManager manager = getSupportFragmentManager();
   FragmentTransaction transaction = manager.beginTransaction();
   currentFrag = frag;
   transaction.replace(R.id.frag, currentFrag.thisFragment());
   transaction.addToBackStack(null);
   transaction.commit();
}

public void goBackToMenu()
{
   FragmentManager manager = getSupportFragmentManager();
   manager.popBackStack();
   
   currentFrag = (Frag) manager.findFragmentById(R.id.frag);
//   if (!everShowedMenu) {
//      showNewMenu();
//   }
}

public void goBackQuit()
{
   finish();
}

private DB dbInstance;

public DB db()
{
   if (dbInstance == null) {
      dbInstance = new DB(this);
   }
   return dbInstance;
}

/*
 * If the bitmap ID is cached, returns the cached Bitmap.
 * Otherwise gets the bitmapUri from the database, loads the Bitmap from the Uri, and caches it before returning.
 * @param bitmapID a bitmapID
 * @return a cached Bitmap
 */

//public Bitmap getBitmap(int bitmapID)
//{
//   Bitmap bitmap = bitmapCache.get(bitmapID);
//   if (bitmap != null)
//      return bitmap;
//   bitmap = db().getBitmap(bitmapID, this);
//   if (bitmap != null)
//      bitmapCache.put(bitmapID, bitmap);
//   return bitmap;
//}
}
