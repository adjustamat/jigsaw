package github.adjustamat.jigsawpuzzlefloss;

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

import github.adjustamat.jigsawpuzzlefloss.Frag.BackCallback;
import github.adjustamat.jigsawpuzzlefloss.game.ImagePuzzle;
import github.adjustamat.jigsawpuzzlefloss.prefs.Prefs;

public class Act
 extends AppCompatActivity
 implements BackCallback
{
private boolean everShowedMenu = false;
private Frag currentFrag = null;
private PlayMatFragment startedGame = null;
private ImagePuzzle startedPuzzle = null;

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
   
   Prefs.init(this);
   
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
   showFrag(GeneratorFragment.newInstance(image));
}

void showNewPrefs()
{
   showFrag(new PrefsFragment());
}

void showPuzzle(ImagePuzzle imagePuzzle)
{
   if (startedGame == null)
      startedGame = new PlayMatFragment();
   showFrag(startedGame);
   
   if (startedPuzzle != imagePuzzle)
      startedGame.startGame(startedPuzzle = imagePuzzle);
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
}
