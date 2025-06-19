package github.adjustamat.jigsawpuzzlefloss;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity
 extends AppCompatActivity
{

@Override
protected void onCreate(Bundle savedInstanceState)
{
   super.onCreate(savedInstanceState);
   EdgeToEdge.enable(this);
   setContentView(R.layout.activity_main);
   ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
      Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
      return insets;
   });
   // TODO: add play field fragment. (back button saves game and shows question dialog: a button closes fragment, goes back to main menu. another quits the app. another "continue puzzle".)
  // LATER: add main menu fragment instead. use an imagepicker there (or saved games) to start a game. button to show settingsfragment.
  
  
}
}
