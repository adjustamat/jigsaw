package github.adjustamat.jigsawpuzzlefloss.ui;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import github.adjustamat.jigsawpuzzlefloss.R;
import github.adjustamat.jigsawpuzzlefloss.containers.Box;
import github.adjustamat.jigsawpuzzlefloss.containers.Box.GroupOrSinglePiece;
import github.adjustamat.jigsawpuzzlefloss.containers.Group;
import github.adjustamat.jigsawpuzzlefloss.pieces.SinglePiece;
import github.adjustamat.jigsawpuzzlefloss.ui.BoxAdapter.BoxItemView;

public class BoxAdapter
 extends RecyclerView.Adapter<BoxItemView>
{
protected final Box box;

public BoxAdapter(Box box)
{
   this.box = box;
}

// TODO: store thumbnail sizes from Resources with static method!
public static int bigBoxItemWidth, bigBoxItemHeight, miniBoxItemWidth, miniBoxItemHeight;

public @NonNull BoxItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
{
   LayoutInflater inflater = LayoutInflater.from(parent.getContext());
   return new BoxItemView(
    inflater.inflate(R.layout.itemview_box_item, parent, false)
   );
}

public int getItemCount()
{
   return box.expandedList.size();
}

protected GroupOrSinglePiece get(int position)
{
   return box.expandedList.get(position);
}

protected Bitmap createThumbnailBitmap()
{
   return Bitmap.createBitmap(bigBoxItemWidth, bigBoxItemHeight, Config.ARGB_8888);
}

public static class MiniBoxAdapter
 extends BoxAdapter
{
   public MiniBoxAdapter(Box box)
   {
      super(box);
   }
   
   public int getItemCount()
   {
      return box.list.size();
   }
   
   protected GroupOrSinglePiece get(int position)
   {
      return box.list.get(position);
   }
   
   protected Bitmap createThumbnailBitmap()
   {
      return Bitmap.createBitmap(miniBoxItemWidth, miniBoxItemHeight, Config.ARGB_8888);
   }
}

public void onBindViewHolder(@NonNull BoxItemView holder, int position)
{
   GroupOrSinglePiece or = get(position);
   if (or instanceof Group) {
      Group group = (Group) or; // Groups in Box have only SinglePieces, no LargerPiece!
      if (group.isExpanded()) {
         int index = position - group.getIndexInContainer();
         onBind(holder, (SinglePiece) group.getSinglePieces().get(index));
      }
      else {
         
         // TODO: generate or get RecyclerView thumbnail from group, see Group.layoutPiecesNoOverlap and Group.dirty
      }
   }
   else {
      onBind(holder, (SinglePiece) or);
   }
}

private void onBind(@NonNull BoxItemView holder, SinglePiece piece)
{
   Bitmap unrotatedFullSize = piece.getUnrotatedFullSizeGraphics();
   // save thumbnail as a BitmapDrawable? no! ViewHolders are recycled!!
   //  Thus, we have to rotate and shrink the buffered unrotatedFullSize every time.

//   if (holder.bufferedThumbnail == null)
//      holder.bufferedThumbnail = createThumbnailBitmap();
   Bitmap thumbnail = createThumbnailBitmap();
   Canvas thumbnailCanvas = new Canvas(thumbnail);
   
   // TODO: shrink unrotatedFullSize to some size, smaller than thumbnail.getWidth(), thumbnail.getHeight()
   //thumbnailCanvas.scale();
   
   thumbnailCanvas.rotate(-piece.currentNorthDirection.degrees);
   
   // TODO: left and top might not be 0 if the rotated unrotatedFullSize is smaller in some dimension than thumbnail
   thumbnailCanvas.drawBitmap(unrotatedFullSize,0f,0f,null);
   
   

   holder.imgBoxItem.setImageBitmap(thumbnail);
}

/**
 * An ImageView for showing a {@link GroupOrSinglePiece} from a {@link Box} in a {@link RecyclerView}.
 * TODO: use a rounded rectangle background with SweepGradient (NOT RadialGradient!) with colors that are anti-colors of the piece.
 *  The rounded rectangle is not full opacity.
 *  For Groups, do not use SweepGradient, just a single color in the rounded rectangle.
 *  Use four samples: the 9ths at middle-of-edge (skip the five 9ths that are corners and middle of piece) and use colors
 *  that are high contrast to the samples, e.g. white if the sample is dark. BigBox and MiniBox background is dark-grey paper
 *  (user-customizable).
 */
public static class BoxItemView
 extends RecyclerView.ViewHolder
{
   public final ImageView imgBoxItem;
   
   public BoxItemView(@NonNull View itemView)
   {
      super(itemView);
      imgBoxItem = itemView.findViewById(R.id.imgBoxItem);
   }
}
}
