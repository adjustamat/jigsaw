package github.adjustamat.jigsawpuzzlefloss.ui;

import android.os.Handler;
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
private Handler handler;

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
   handler = activity.getMainHandler();
}

public void hideMenu()
{
   llhParent.setVisibility(View.GONE);
   // TODO: cancel animation!
}

public void showMenuNow()
{
   // TODO: all children of llv's go from INVISIBLE to VISIBLE. Cancel animation! opacity to full.
}

public void animateShowMenu(@Nullable AbstractPiece[] piece, float clickX, float clickY, boolean twoFingersTogether,
 Runnable onMenuFinished)
{
   // TODO: start animation, first slow, then accelerating, which after another long-press-delay solidifies menu.
   
   // TODO: show signifyer/indicator, something like a menu with low opacity, which solidifies on UP.
   //  but moving the finger turns it into HOLD_DRAG and hides the menu immediately.
   //  but a second finger can cancel HOLD and turn it into FLICK_2_ROTATE (touch first finger and drag/flick with second finger)
   
   // TODO: ACTIONS in context menus: - ui.PlayMenu (do not use android builtin ContextMenu!)
   //  BG:
   //   rotate/modify bg
   //   group pieces together (rectangular selection, custom path selection)
   //    (can move already grouped pieces to new group)
   //   ((also find the closest pieces and highlight and show menu for them too.))
   //  PIECE: (( find all pieces under clickPoint and closeby/near. show all these pieces under the finger, locked too. ))
   //   lockedRotation
   //   lockedInPlace
   //  PIECE IN A GROUP: -or- MORE THAN ONE PIECE SELECTED:
   //   move whole group (click with second finger while still holding a finger down - DO NOT disable
   //    this menu item if the first finger lifts while menu is showing)
   //   rotate whole group,
   //   rotate all in group but around individual centers. (not just for group, also just several selected pieces)
   
   // during an automation-animation, the view does not respond to any touch events.
   
   // TODO: set position of llhParent after knowing how many menu items will show.
   //  center menu around clickPoint, but avoid a circle around it. Use margins to jump over clickPoint
   
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
