package github.adjustamat.jigsawpuzzlefloss.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import github.adjustamat.jigsawpuzzlefloss.R;
import github.adjustamat.jigsawpuzzlefloss.containers.Box;

public class BigBox extends RecyclerView.Adapter<BoxItemView>
{
RecyclerView recyclerView;
Box box;

@NonNull public BoxItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
{
   LayoutInflater inflater= LayoutInflater.from(parent.getContext());
   BoxItemView ret = new BoxItemView(inflater.inflate(R.layout.view_box_item_view,parent,false));
   return ret;
}

public void onBindViewHolder(@NonNull BoxItemView holder, int position)
{

}

public int getItemCount()
{
   return 0;
}


}
