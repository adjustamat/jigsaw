package github.adjustamat.jigsawpuzzlefloss;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

class GeneratorFragment
 extends Fragment
 implements Frag
{

public static final String ARG_IMAGE_URI = "mat.IMAGE_URI";
private boolean cropMode;

public static GeneratorFragment newInstance(Uri image)
{
   GeneratorFragment fragment = new GeneratorFragment();
   Bundle args = new Bundle();
   args.putParcelable(ARG_IMAGE_URI, image);
   fragment.setArguments(args);
   return fragment;
}

public void onCreate(@Nullable Bundle savedInstanceState)
{
   super.onCreate(savedInstanceState);
   Bundle arguments = getArguments();
   if (arguments != null) {
      Uri uri = arguments.getParcelable(ARG_IMAGE_URI);
      assert (uri != null);
      Context ctx = requireContext();
      String mimetype = ctx.getContentResolver().getType(uri);
      //ImageDecoder.createSource(ctx.getContentResolver(),uri);
      //ContentResolver.wrap(contentProvider)
      //MediaStore.Images.Media.
   }
}

public GeneratorFragment()
{
   // Required empty public constructor
}

public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
{
   return inflater.inflate(R.layout.fragment_generator, container, false);
}

private Point calculate(Bitmap bitmap, int circa)
{
   double ratio = (float) bitmap.getWidth() / bitmap.getHeight();
   // equation: A * (ratio*A) = circa   =>
   // =>   A = sqrt(circa/ratio)
   double height = Math.sqrt(circa / ratio);
   double width = ratio * height;
   return new Point((int) Math.round(width), (int) Math.round(height));
}

public void handleOnBackPressed(BackCallback callback)
{
   callback.goBackToMenu();
}

}
