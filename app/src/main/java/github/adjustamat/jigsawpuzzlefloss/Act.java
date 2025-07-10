package github.adjustamat.jigsawpuzzlefloss;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.HashMap;
import java.util.Random;

import github.adjustamat.jigsawpuzzlefloss.Frag.BackCallback;
import github.adjustamat.jigsawpuzzlefloss.db.DB;
import github.adjustamat.jigsawpuzzlefloss.db.Global;
import github.adjustamat.jigsawpuzzlefloss.game.ImagePuzzle;

public class Act
 extends AppCompatActivity
 implements BackCallback
{
private boolean everShowedMenu = false;
private Frag currentFrag = null;
private PlayMatFragment startedGame = null;
private ImagePuzzle startedPuzzle = null;

// TODO: empty this cache when starting a game. only needed by MainMenu and Generator fragments
private final HashMap<Integer, Bitmap> bitmapCache = new HashMap<>();

private final OnBackPressedCallback onBackPressed = new OnBackPressedCallback(true)
{
   public void handleOnBackPressed()
   {
      if (currentFrag != null) {
         currentFrag.handleOnBackPressed(Act.this);
      }
   }
};

@Override
protected void onCreate(Bundle savedInstanceState)
{
   super.onCreate(savedInstanceState);
   EdgeToEdge.enable(this);
   setContentView(R.layout.act);
   ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets)->{
      Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
      return insets;
   });
   getOnBackPressedDispatcher().addCallback(onBackPressed);
   
   Global.initEverything(this);
   
   // TODO: check state in prefs, or check intent.
   //  not always showNewMenu(), sometimes instead go directly to showPuzzle(imagePuzzle).
   showNewMenu();
   
}

void showNewMenu()
{
   everShowedMenu = true;
   showFrag(new MainMenuFragment());
}

void showNewGenerator(Uri image)
{
   // TODO: clicking on image in MainMenuFragment Bitmap list calls this method.
   showFrag(GeneratorFragment.newInstance(image));
}

void showNewPrefs()
{
   showFrag(new PrefsFragment());
}

public void generateAndShowNewPuzzle(Point puzzleSize, Bitmap croppedBitmap, boolean cropped, Uri bitmapUri)
{
   int bitmapID;
   if (cropped)
      // save cropped image in ctx.getFilesDir()
      bitmapID = db().saveCroppedBitmap(bitmapUri, croppedBitmap, this);
   else
      bitmapID = db().getBitmapID(bitmapUri);
   
   FragmentManager manager = getSupportFragmentManager();
   manager.popBackStack(); // pop GeneratorFragment!
   
   ImagePuzzle generated = ImagePuzzle.generateNewPuzzle(puzzleSize.x, puzzleSize.y,
    croppedBitmap, bitmapID, new Random());
   
   showPuzzle(generated); // show new PlayMatFragment
}

void showPuzzle(ImagePuzzle imagePuzzle)
{
   if (startedGame == null)
      startedGame = new PlayMatFragment();
   
   if (startedPuzzle != imagePuzzle)
      startedGame.startGame(startedPuzzle = imagePuzzle);
   
   showFrag(startedGame);
   bitmapCache.clear();
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
   manager.popBackStack(); // TODO: does it?:  this shows the menu if everShowedMenu. otherwise it just hides playmat.
   
   currentFrag = (Frag) manager.findFragmentById(R.id.frag);
   if (!everShowedMenu) {
      showNewMenu();
   }
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

/**
 * If the bitmap ID is cached, returns the cached Bitmap.
 * Otherwise gets the bitmapUri from the database, loads the Bitmap from the Uri, and caches it before returning.
 * @param bitmapID a bitmapID
 * @return a cached Bitmap
 */
public Bitmap getBitmap(int bitmapID)
{
   Bitmap bitmap = bitmapCache.get(bitmapID);
   if (bitmap != null)
      return bitmap;
   bitmap = db().getBitmap(bitmapID, this);
   if (bitmap != null)
      bitmapCache.put(bitmapID, bitmap);
   return bitmap;
}
}
