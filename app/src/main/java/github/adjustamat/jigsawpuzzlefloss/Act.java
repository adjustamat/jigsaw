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

import java.util.Random;

import github.adjustamat.jigsawpuzzlefloss.Frag.BackCallback;
import github.adjustamat.jigsawpuzzlefloss.game.ImagePuzzle;

public class Act
 extends AppCompatActivity
 implements BackCallback
{
private boolean everShowedMenu = false;
private Frag currentFrag = null;
private PlayMatFragment startedGame = null;

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
   // TODO: check state. maybe not show menu fragment, go directly to playmat.
   showNewMenu();
   
   
   
   
   //
   //
   getOnBackPressedDispatcher().addCallback(onBackPressed);
}

void showNewMenu()
{
   everShowedMenu = true;
   showFrag(MainMenuFragment.newInstance());
}

void showNewGenerator(Uri image)
{
   showFrag(GeneratorFragment.newInstance(image));
}

void showNewPrefs()
{
   showFrag(new PrefsFragment());
}

void showNewPlayMat(/*TODO!*/)
{
   startedGame = PlayMatFragment.newInstance();
   showFrag(startedGame);
   // TODO!
   startedGame.startGame(/*TODO*/);
}

void showFrag(Frag frag)
{
   FragmentManager manager = getSupportFragmentManager();
   FragmentTransaction transaction = manager.beginTransaction();
   transaction.replace(R.id.frag, currentFrag = frag);
   transaction.addToBackStack(null);
   transaction.commit();
}

public void goBackToMenu()
{
   FragmentManager manager = getSupportFragmentManager();
   manager.popBackStack(); // this shows the menu if everShowedMenu. otherwise it just hides playmat.
   if (!everShowedMenu) {
      showNewMenu();
   }
}

public void goBackQuit()
{
   finish();
}
}
