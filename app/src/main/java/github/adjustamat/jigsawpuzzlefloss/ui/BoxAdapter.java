package github.adjustamat.jigsawpuzzlefloss.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import github.adjustamat.jigsawpuzzlefloss.R;
import github.adjustamat.jigsawpuzzlefloss.containers.Box;
import github.adjustamat.jigsawpuzzlefloss.containers.Box.GroupOrSinglePiece;
import github.adjustamat.jigsawpuzzlefloss.pieces.Group;
import github.adjustamat.jigsawpuzzlefloss.pieces.SinglePiece;

public class BoxAdapter
 extends RecyclerView.Adapter<BoxAdapter.BoxItemView>
{
RecyclerView recyclerView;
Box box;

@NonNull public BoxItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
{
   LayoutInflater inflater = LayoutInflater.from(parent.getContext());
   BoxItemView ret = new BoxItemView(
    inflater.inflate(R.layout.item_box_item_view, parent, false)
   );
   return ret;
}

public void onBindViewHolder(@NonNull BoxItemView holder, int position)
{
   GroupOrSinglePiece or = box.get(position);
   if (or instanceof Group) {
      Group group = (Group) or;
      // TODO: generate or get RecyclerView thumbnail from group,
   }
   else {
      SinglePiece piece = (SinglePiece) or;
      // TODO: get image with standard outlines, respecting the piece's current rotation.
   }
}

public int getItemCount()
{
   return box.size();
}

/**
 * An ImageView for showing a {@link GroupOrSinglePiece} from a {@link Box} in a {@link RecyclerView}.
 */
public static class BoxItemView
 extends RecyclerView.ViewHolder
{
   public ImageView imgBoxItem; // NOT SurfaceView!
   
   public BoxItemView(@NonNull View itemView)
   {
      super(itemView);
      imgBoxItem = itemView.findViewById(R.id.imgBoxItem);
   }
   
}
}
