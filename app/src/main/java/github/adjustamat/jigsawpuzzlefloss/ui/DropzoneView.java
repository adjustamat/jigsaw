package github.adjustamat.jigsawpuzzlefloss.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class DropzoneView extends View
{

public DropzoneView(Context context)
{
   this(context, null, 0, 0);
}

public DropzoneView(Context context, @Nullable AttributeSet attrs)
{
   this(context, attrs, 0, 0);
}

public DropzoneView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
{
   this(context, attrs, defStyleAttr, 0);
}

public DropzoneView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes)
{
   super(context, attrs, defStyleAttr, defStyleRes);
   // TODO?
}

// TODO: show indicator

/*
view_dropzone_indicator.xml is not needed
<github.adjustamat.jigsawpuzzlefloss.ui.DropzoneView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="0dp"
    android:layout_height="0dp"/>
 */
}
