package github.adjustamat.jigsawpuzzlefloss;

public interface Frag
{
interface BackCallback{
   void goBackToMenu();
   void goBackQuit();
}
void handleOnBackPressed(BackCallback callback);
}
