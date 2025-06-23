package github.adjustamat.jigsawpuzzlefloss;

import androidx.fragment.app.Fragment;

public abstract class Frag extends Fragment
{
interface BackCallback{
   void goBackToMenu();
   void goBackQuit();
   
   
   
}
abstract void handleOnBackPressed(BackCallback callback);
}
