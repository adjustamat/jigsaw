package github.adjustamat.jigsawpuzzlefloss.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

import github.adjustamat.jigsawpuzzlefloss.containers.PlayMat;

/**
 * A View of the whole {@link PlayMat}, to help with scrolling.
 */
public class MiniMap
 extends SurfaceView
{
public MiniMap(Context context)
{
   this(context, null, 0, 0);
}

public MiniMap(Context context, AttributeSet attrs)
{
   this(context, attrs, 0, 0);
}

public MiniMap(Context context, AttributeSet attrs, int defStyleAttr)
{
   this(context, attrs, defStyleAttr, 0);
}

public MiniMap(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
{
   super(context, attrs, defStyleAttr, defStyleRes);
}
}
