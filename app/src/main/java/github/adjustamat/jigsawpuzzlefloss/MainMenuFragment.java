package github.adjustamat.jigsawpuzzlefloss;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainMenuFragment
 extends Fragment
{

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private static final String ARG_PARAM1 = "param1";
private static final String ARG_PARAM2 = "param2";

/**
 * Use this factory method to create a new instance of
 * this fragment using the provided parameters.
 * @param param1 Parameter 1.
 * @param param2 Parameter 2.
 * @return A new instance of fragment MainMenuFragment.
 */
// TODO: Rename and change types and number of parameters
public static MainMenuFragment newInstance(String param1, String param2)
{
   MainMenuFragment fragment = new MainMenuFragment();
   Bundle args = new Bundle();
   args.putString(ARG_PARAM1, param1);
   args.putString(ARG_PARAM2, param2);
   fragment.setArguments(args);
   return fragment;
}
// TODO: Rename and change types of parameters
//private String mParam1;
//private String mParam2;

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
}
