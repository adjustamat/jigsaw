package github.adjustamat.jigsawpuzzlefloss.ui;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import github.adjustamat.jigsawpuzzlefloss.containers.Box;

public class BigBox
 extends RecyclerView
{
BoxAdapter adapter;
Box box;

public BigBox(@NonNull Context context)
{
   this(context,null);
}

public BigBox(@NonNull Context context, @Nullable AttributeSet attrs)
{
   this(context,null,0);
}

public BigBox(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr)
{
   super(context, attrs, defStyleAttr);
}
}
