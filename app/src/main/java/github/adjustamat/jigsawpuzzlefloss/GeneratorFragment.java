package github.adjustamat.jigsawpuzzlefloss;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.fragment.app.Fragment;

import java.util.LinkedList;

import github.adjustamat.jigsawpuzzlefloss.db.Prefs;
import github.adjustamat.jigsawpuzzlefloss.db.Prefs.GeneratorStr;
import github.adjustamat.jigsawpuzzlefloss.ui.PuzzleGraphics;

class GeneratorFragment
 extends Fragment
 implements Frag
{
public static final String DBG = "GeneratorFragment";
public static final String ARG_BITMAP_URI = "mat.IMAGE_URI";
public static final String ARG_BITMAP_ID = "mat.IMAGE_ID";
private Bitmap croppedBitmap;
private int defaultPiecesCirca = 200; // TODO: default size (pieces) if no option was chosen before custom!
private boolean cropped;

private class EditTextListener
 implements TextWatcher
{
   private final boolean width;
   
   private EditTextListener(boolean width){ this.width = width; }
   
   public void beforeTextChanged(CharSequence s, int start, int count, int after)
   {
   }
   
   public void onTextChanged(CharSequence s, int start, int before, int count)
   {
      if (width) {
         if (!ui.txtCustomWidth.isEnabled())
            return;
      }
      else {
         if (!ui.txtCustomHeight.isEnabled())
            return;
      }
      Integer i;
      try {
         i = Integer.parseInt(s.toString());
      }
      catch (NumberFormatException exception) {
         i = null;
      }
      if (i != null) {
         int prev;
         if (width) {
            prev = ui.selectedSize.point.x;
            ui.selectedSize.point.x = i;
         }
         else {
            prev = ui.selectedSize.point.y;
            ui.selectedSize.point.y = i;
         }
         if (i != prev) {
            ui.updateCustomSize();
         }
      }
   }
   
   public void afterTextChanged(Editable s)
   {
   }
}

private class SizeOption
{
   final AppCompatRadioButton radioButton;
   final int circa;
   final Point point = new Point();
   
   private SizeOption(AppCompatRadioButton radioButton, OnCheckedChangeListener listener)
   {
      this.circa = -1;
      this.radioButton = radioButton;
      radioButton.setOnCheckedChangeListener(listener);
   }
   
   private SizeOption(Context ctx, int circa, OnCheckedChangeListener listener, Bitmap bitmap)
   {
      this.circa = circa;
      this.radioButton = new AppCompatRadioButton(ctx);
      radioButton.setOnCheckedChangeListener(listener);
      calculateWidthAndHeight(ctx, bitmap, null);
      //radioButton.setText(ctx.getString(R.string.radioChooseSizeUnselected, circa));
   }
   
   private void setPoint(Bitmap bitmap, int ca)
   {
      double ratio = (double) bitmap.getWidth() / bitmap.getHeight();
      // equation: A * (ratio*A) = circa   =>
      // =>   A = sqrt(circa/ratio)
      double height = Math.sqrt(ca / ratio);
      double width = ratio * height;
      point.x = (int) Math.round(width);
      point.y = (int) Math.round(height);
   }
   
   void calculateWidthAndHeight(Context ctx, Bitmap bitmap, SizeOption prev)
   {
      if (circa == -1) {
         if (prev != null) {
            point.x = prev.point.x;
            point.y = prev.point.y;
         }
         else {
            setPoint(bitmap, defaultPiecesCirca);
         }
      }
      else {
         setPoint(bitmap, this.circa);
         radioButton.setText(
          ctx.getString(R.string.radioChooseSize, circa, point.x, point.y)
         );
         
      }
   }
   
}

private class Views
{
   final LinearLayout llhCrop;
   final Button btnCrop;
   final ImageView imgCroppedBitmap;
   final SurfaceView srcCrop;
   
   final TextView lblNoSizeSelected;
   final Button btnStart;
   
   final LinearLayout llvGeneratorSizes;
   final LinkedList<SizeOption> sizeOptions = new LinkedList<>();
   final OnCheckedChangeListener radioChangeListener;
   
   final AppCompatRadioButton radioCustomSize;
   final EditText txtCustomWidth;
   final EditText txtCustomHeight;
   final TextView lblCustomSize;
   
   SizeOption selectedSize;
   
   private boolean cropMode = false;
   
   private Views(LinearLayout llhCrop, Button btnCrop, ImageView imgCroppedBitmap, SurfaceView srcCrop,
    TextView lblNoSizeSelected, Button btnStart, LinearLayout llvGeneratorSizes,
    AppCompatRadioButton radioCustomSize, EditText txtCustomWidth, EditText txtCustomHeight, TextView lblCustomSize,
    Context ctx)
   {
      this.llhCrop = llhCrop;
      this.btnCrop = btnCrop;
      this.imgCroppedBitmap = imgCroppedBitmap;
      this.srcCrop = srcCrop;
      this.lblNoSizeSelected = lblNoSizeSelected;
      this.btnStart = btnStart;
      this.llvGeneratorSizes = llvGeneratorSizes;
      this.radioCustomSize = radioCustomSize;
      this.txtCustomWidth = txtCustomWidth;
      this.txtCustomHeight = txtCustomHeight;
      this.lblCustomSize = lblCustomSize;
      
      
      txtCustomWidth.addTextChangedListener(new EditTextListener(true));
      txtCustomHeight.addTextChangedListener(new EditTextListener(false));
      
      radioChangeListener = (buttonView, isChecked)->{
         if (isChecked) {
            btnStart.setEnabled(true);
            lblNoSizeSelected.setVisibility(View.GONE);
            SizeOption previous = null;
            for (SizeOption sizeOption: sizeOptions) {
               if (sizeOption.radioButton != buttonView) {
                  sizeOption.radioButton.setChecked(false);
               }
               else {
                  previous = selectedSize;
                  selectedSize = sizeOption;
               }
            }
            selectedSize.calculateWidthAndHeight(ctx, croppedBitmap, previous);
            if (buttonView == radioCustomSize)
               updateCustomSize();
            else
               clearCustomSize();
         } // isChecked
      };
      
      sizeOptions.add(new SizeOption(radioCustomSize, radioChangeListener));
      addChoices(ctx);
      
      btnCrop.setOnClickListener(v->{
         setCropMode(true);
      });
      
      btnStart.setOnClickListener(v->{
         Act activity = (Act) requireActivity();
         activity.showPuzzleFromGenerator(selectedSize.point, croppedBitmap, cropped);
         //selectedSize
      });
   }
   
   void setCropMode(boolean crop)
   {
      cropMode = crop;
      if (cropMode) {
         // TODO: hide scrvGeneratorSizes, llhStart, llhCrop.
         //  show srcCrop (with correct bitmap).
      }
      else {
         //  srcCrop itself can call setCropMode(false)!
         
         // TODO: hide srcCrop.
         //  show scrvGeneratorSizes, llhStart, llhCrop.
         //  set cropped=true if changes were made. delete the cropped parts and set croppedBitmap as the rest!
         
         // TODO: load more options on top of llv after crop, using addChoices(ctx)
      }
   }
   
   void clearCustomSize()
   {
      txtCustomWidth.setEnabled(false);
      txtCustomHeight.setEnabled(false);
      txtCustomWidth.setText("");
      txtCustomHeight.setText("");
      
      lblCustomSize.setVisibility(View.INVISIBLE);
   }
   
   void updateCustomSize()
   {
      txtCustomWidth.setText(String.valueOf(selectedSize.point.x));
      txtCustomHeight.setText(String.valueOf(selectedSize.point.y));
      txtCustomWidth.setEnabled(true);
      txtCustomHeight.setEnabled(true);
      
      lblCustomSize.setText(
       getString(R.string.lblCustomSize, selectedSize.point.x * selectedSize.point.y)
      );
      lblCustomSize.setVisibility(View.VISIBLE);
   }
   
   void addChoices(Context ctx)
   {
      String[] sizes = Prefs.get(ctx, GeneratorStr.sizeChoices).split(", ");
      int i = 0; // add views to the top of the layout
      for (String sizeChoice: sizes) {
         int circaSize = Integer.parseInt(sizeChoice);
         SizeOption option = new SizeOption(ctx, circaSize, radioChangeListener, croppedBitmap);
         sizeOptions.add(i, option);
         llvGeneratorSizes.addView(option.radioButton, i++);
      }
   }
}

private Views ui;

public static GeneratorFragment newInstance(int imageID)
{
   GeneratorFragment fragment = new GeneratorFragment();
   Bundle args = new Bundle();
   args.putInt(ARG_BITMAP_ID, imageID);
   fragment.setArguments(args);
   return fragment;
}

public static GeneratorFragment newInstance(Uri image)
{
   GeneratorFragment fragment = new GeneratorFragment();
   Bundle args = new Bundle();
   args.putParcelable(ARG_BITMAP_URI, image);
   fragment.setArguments(args);
   return fragment;
}

public GeneratorFragment()
{
   // Required empty public constructor
}

public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
{
   return inflater.inflate(R.layout.fragment_generator, container, false);
}

public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
{
   super.onViewCreated(view, savedInstanceState);
   ui = new Views(
    view.findViewById(R.id.llhCrop),
    view.findViewById(R.id.btnCrop),
    view.findViewById(R.id.imgCroppedBitmap),
    view.findViewById(R.id.srcCrop),
    view.findViewById(R.id.lblNoSizeSelected),
    view.findViewById(R.id.btnStart),
    view.findViewById(R.id.llvGeneratorSizes),
    view.findViewById(R.id.radioCustomSize),
    view.findViewById(R.id.txtCustomWidth),
    view.findViewById(R.id.txtCustomHeight),
    view.findViewById(R.id.lblCustomSize),
    requireContext()
   );
   Bundle arguments = getArguments();
   if (arguments != null) {
      int id = arguments.getInt(ARG_BITMAP_ID);
      if (id != 0) {
      Act act= (Act) requireActivity();
      croppedBitmap = act.getBitmap(id);
      }
      else {
         Uri uri = arguments.getParcelable(ARG_BITMAP_URI);
         if (uri != null) {
            croppedBitmap = PuzzleGraphics.loadBitmapFromUri(uri, requireContext());
         }
         else {
            Log.d(DBG, "onViewCreated() - all arguments (ARG_IMAGE_*) are null!");
         }
      }
      if (croppedBitmap == null)
         Log.d(DBG, "croppedBitmap is null");
      else
         ui.imgCroppedBitmap.setImageBitmap(croppedBitmap);
   }
   else Log.d(DBG, "onViewCreated() - Bundle getArguments() returns null!");
}

public void handleOnBackPressed(BackCallback callback)
{
   if (ui.cropMode) {
      ui.setCropMode(false);
   }
   else
      callback.goBackToMenu();
}

}
