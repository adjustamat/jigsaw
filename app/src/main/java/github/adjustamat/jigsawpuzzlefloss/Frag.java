package github.adjustamat.jigsawpuzzlefloss;

import androidx.fragment.app.Fragment;

public interface Frag
{
void handleOnBackPressed(BackCallback callback);
default Fragment thisFragment(){ return (Fragment) this; }

interface BackCallback
{
   void goBackToMenu();
   void goBackQuit();
}
}
