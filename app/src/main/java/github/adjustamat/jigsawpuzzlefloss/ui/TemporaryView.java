package github.adjustamat.jigsawpuzzlefloss.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TemporaryView
 extends FrameLayout
{

public TemporaryView(@NonNull Context context)
{
   this(context, null, 0, 0);
}

public TemporaryView(@NonNull Context context, @Nullable AttributeSet attrs)
{
   this(context, attrs, 0, 0);
}

public TemporaryView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr)
{
   this(context, attrs, defStyleAttr, 0);
}

public TemporaryView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes)
{
   super(context, attrs, defStyleAttr, defStyleRes);
   // TODO?
}
}
