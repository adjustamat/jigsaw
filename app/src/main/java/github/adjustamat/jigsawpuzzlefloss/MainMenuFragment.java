package github.adjustamat.jigsawpuzzlefloss;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainMenuFragment
 extends Frag
{

public static MainMenuFragment newInstance()
{
   return new MainMenuFragment();
}

public MainMenuFragment()
{
   // Required empty public constructor
}

@Override
public void onCreate(Bundle savedInstanceState)
{
   super.onCreate(savedInstanceState);
   Bundle arguments = getArguments();
   if (arguments != null) {
//      mParam1 = arguments.getString(ARG_PARAM1);
//      mParam2 = arguments.getString(ARG_PARAM2);
   }
}

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
{
   // Inflate the layout for this fragment
   return inflater.inflate(R.layout.fragment_main_menu, container, false);
}
/*
 // TODO: use an imagepicker in main menu (or saved games) to start a game. button to show settingsfragment.
   // TODO: choose bitmap, choose crop and number of pieces!
   ImagePuzzle puzzle = ImagePuzzle.generateNewPuzzle(7,5,BITMAP,new Random());
   showNewPlayMat();
 */

void handleOnBackPressed(BackCallback callback)
{
   callback.goBackQuit();
}
}
