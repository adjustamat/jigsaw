package github.adjustamat.jigsawpuzzlefloss.ui;

import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;

import java.util.ArrayList;

import github.adjustamat.jigsawpuzzlefloss.PuzzleActivity;
import github.adjustamat.jigsawpuzzlefloss.R;
import github.adjustamat.jigsawpuzzlefloss.pieces.AbstractPiece;

public class PlayMenu
{

private final PuzzleActivity activity;
private final LinearLayout llhParent;
private final LinearLayout llvMenu1;
private final LinearLayout llvMenu2;
private final LinearLayout llvMenu3;

private final ArrayList<Item> recyclableItems = new ArrayList<>();
private int currentItemsInMenu1,
 currentItemsInMenu2,
 currentItemsInMenu3;

public PlayMenu(PuzzleActivity parent)
{
   activity = parent;
   llhParent = activity.findViewById(R.id.llhMenus);
   llvMenu1 = activity.findViewById(R.id.llvMenu1);
   llvMenu2 = activity.findViewById(R.id.llvMenu2);
   llvMenu3 = activity.findViewById(R.id.llvMenu3);
}

public void showMenu(@Nullable AbstractPiece piece, MotionEvent clickPosition)
{
   
   
   
   // TODO: set position of llhParent after knowing how many menu items will show.
   //  center menu around clickPosition, but avoid a circle around it. Use margins to jump over clickPosition
   
   // TODO: Consider screen orientation.
   llhParent.setVisibility(View.VISIBLE);
}



// TODO: inflate R.layout.itemview_playmenu_item
public static class Item
{
   final LinearLayout llh;
   final AppCompatCheckBox checkBox;
   final TextView textAndIcon;
   
   Item(View view)
   {
      llh = (LinearLayout) view;//.findViewById(R.id.llhMenuItem);
      checkBox = view.findViewById(R.id.chkMenuItem);
      textAndIcon = view.findViewById(R.id.lblMenuItem);
   }
   
}
}
