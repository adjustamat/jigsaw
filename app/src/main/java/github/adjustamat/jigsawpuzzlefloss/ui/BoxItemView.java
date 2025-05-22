package github.adjustamat.jigsawpuzzlefloss.ui;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import github.adjustamat.jigsawpuzzlefloss.containers.Box;
import github.adjustamat.jigsawpuzzlefloss.containers.Box.GroupOrSinglePiece;

/**
 * A SurfaceView for showing a {@link GroupOrSinglePiece} in a {@link RecyclerView} ({@link Box}).
 */
public class BoxItemView extends RecyclerView.ViewHolder
{
public BoxItemView(@NonNull View itemView)
{
   super(itemView);
}

}
