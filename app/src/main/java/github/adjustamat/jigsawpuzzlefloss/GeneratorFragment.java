package github.adjustamat.jigsawpuzzlefloss;

import android.content.Context;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;
import com.canhub.cropper.CropImageView.RequestSizeOptions;

import java.util.LinkedList;
import java.util.Objects;

import github.adjustamat.jigsawpuzzlefloss.db.Prefs;
import github.adjustamat.jigsawpuzzlefloss.db.Prefs.GeneratorStr;

class GeneratorFragment
 extends Fragment
 implements Frag
{
public static final String DBG = "GeneratorFragment";
public static final String ARG_BITMAP_URI = "mat.IMAGE_URI";
public static final String ARG_BITMAP_ID = "mat.IMAGE_ID";
//private Bitmap croppedBitmap;
private Uri bitmapUri;
private static final int defaultPiecesCirca = 200; // TODO: default size (pieces) if no option was chosen before custom!

private boolean cropped; // TODO

private class CustomSizeListener
 implements TextWatcher
{
   private final boolean isWidth;
   
   private CustomSizeListener(boolean isWidth){ this.isWidth = isWidth; }
   
   public void beforeTextChanged(CharSequence s, int start, int count, int after)
   {
   }
   
   public void onTextChanged(CharSequence s, int start, int before, int count)
   {
      if (isWidth) {
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
         (isWidth ?ui.txtCustomWidth :ui.txtCustomHeight).setTextColor(ui.normalTextColor);
      }
      catch (NumberFormatException exception) {
         i = null;
         (isWidth ?ui.txtCustomWidth :ui.txtCustomHeight).setTextColor(ui.errorTextColor);
      }
      if (i != null) {
         int prev;
         if (isWidth) {
            prev = ui.selectedSize.wxh.x;
            ui.selectedSize.wxh.x = i;
         }
         else {
            prev = ui.selectedSize.wxh.y;
            ui.selectedSize.wxh.y = i;
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
   final Point wxh = new Point();
   
   /**
    * Constructor for custom size
    * @param radioButton radioCustomSize
    * @param listener radioChangeListener
    */
   private SizeOption(AppCompatRadioButton radioButton, OnCheckedChangeListener listener)
   {
      this.circa = -1;
      this.radioButton = radioButton;
      radioButton.setOnCheckedChangeListener(listener);
   }
   
   /**
    * Constructor for size option from user prefs
    * @param ctx a Context
    * @param circa a size from user prefs
    * @param listener radioChangeListener
    * @param rect the current (cropped) image size
    */
   private SizeOption(Context ctx, int circa, OnCheckedChangeListener listener, /*Bitmap bitmap*/ Rect rect)
   {
      this.circa = circa;
      this.radioButton = new AppCompatRadioButton(ctx);
      radioButton.setOnCheckedChangeListener(listener);
      setWidthXHeight(ctx, rect, null);
      //radioButton.setText(ctx.getString(R.string.radioChooseSizeUnselected, circa));
   }
   
   private void calculateWidthXHeight(/*Bitmap bitmap*/Rect rect, int ca)
   {
//      double ratio = (double) bitmap.getWidth() / bitmap.getHeight();
      double ratio = (double) rect.width() / rect.height();
      // equation: A * (ratio*A) = circa   =>
      // =>   A = sqrt(circa/ratio)
      double height = Math.sqrt(ca / ratio);
      double width = ratio * height;
      wxh.x = (int) Math.round(width);
      wxh.y = (int) Math.round(height);
      // TODO: !! cropping is mandatory! show on cropview what happens when choosing options or changing custom size.
      //  how do we calculate where to remove margins from? can pieceSquareSize only be an integer? can the ratio?
      
   }
   
   void setWidthXHeight(Context ctx, Rect rect/*Bitmap bitmap*/, SizeOption prev)
   {
      // TODO: when clicking on size option, change crop rect and freeze aspect ratio!
      
      if (circa == -1) { // custom size (radioCustomSize)
         if (prev != null) { // copy last selected option
            wxh.x = prev.wxh.x;
            wxh.y = prev.wxh.y;
         }
         else { // if custom size is the first selected option
            calculateWidthXHeight(rect, defaultPiecesCirca);
         }
      }
      else { // not custom size
         calculateWidthXHeight(rect, this.circa);
         radioButton.setText(
          ctx.getString(R.string.radioChooseSize, circa, wxh.x, wxh.y)
         );
         
      }
   }
}

private class Views
 implements SurfaceHolder.Callback
{
   final CropImageView viewCrop;
   final Button btnCropOnly;
   private boolean cropMode = false;
   
   final LinearLayout llhStart;
   final Button btnStart;
   final TextView lblNoSizeSelected;
   
   final NestedScrollView scrvGeneratorSizes;
   final LinearLayout llvGeneratorSizes;
   final LinkedList<SizeOption> sizeOptions = new LinkedList<>();
   private SizeOption selectedSize;
   final OnCheckedChangeListener radioChangeListener;
   
   final AppCompatRadioButton radioCustomSize;
   final EditText txtCustomWidth;
   final EditText txtCustomHeight;
   final TextView lblCustomSize;
   final int normalTextColor;
   final int errorTextColor;
   
   private Views(View view, Context ctx)
   {
      this.viewCrop = view.findViewById(R.id.viewCrop);
      this.btnCropOnly = view.findViewById(R.id.btnCropOnly);
      this.llhStart = view.findViewById(R.id.llhStart);
      this.btnStart = view.findViewById(R.id.btnStart);
      this.lblNoSizeSelected = view.findViewById(R.id.lblNoSizeSelected);
      this.scrvGeneratorSizes = view.findViewById(R.id.scrvGeneratorSizes);
      this.llvGeneratorSizes = view.findViewById(R.id.llvGeneratorSizes);
      this.radioCustomSize = view.findViewById(R.id.radioCustomSize);
      this.txtCustomWidth = view.findViewById(R.id.txtCustomWidth);
      this.txtCustomHeight = view.findViewById(R.id.txtCustomHeight);
      this.lblCustomSize = view.findViewById(R.id.lblCustomSize);
      
      normalTextColor = txtCustomHeight.getCurrentTextColor();
      errorTextColor = ctx.getResources().getColor(R.color.color_placeholder_text);
      
      txtCustomWidth.addTextChangedListener(new CustomSizeListener(true));
      txtCustomHeight.addTextChangedListener(new CustomSizeListener(false));
      
      radioChangeListener = (buttonView, isChecked)->{
         if (isChecked) {
            btnStart.setEnabled(true);
            lblNoSizeSelected.setVisibility(View.GONE);
            SizeOption previous = selectedSize;
            for (SizeOption sizeOption: sizeOptions) {
               if (sizeOption.radioButton != buttonView) {
                  sizeOption.radioButton.setChecked(false);
               }
               else {
                  selectedSize = sizeOption;
               }
            }
            selectedSize.setWidthXHeight(ctx, viewCrop.getCropRect(), previous);
            if (buttonView == radioCustomSize)
               updateCustomSize();
            else
               clearCustomSize();
            // TODO: when clicking on size option, change crop rect and freeze aspect ratio!
            //viewCrop.setAspectRatio();
            //viewCrop.setFixedAspectRatio(true);
            
         } // isChecked == true
      };
      
      sizeOptions.add(new SizeOption(radioCustomSize, radioChangeListener));
      addChoices(ctx);
      
      viewCrop.setOnCropImageCompleteListener((cropImageView, cropResult)->{
         Act act = (Act) requireActivity();
         Rect rect = Objects.requireNonNull(cropResult.getCropRect());
         
         // show new PuzzleActivity
         act.gotoPuzzleFromGenerator(cropResult.getUriContent(),
          selectedSize.wxh.x, selectedSize.wxh.y,
          rect.width(), rect.height()
         );
      });
      
      btnCropOnly.setOnClickListener(v->{
         setCropMode(true);
      });
      
      btnStart.setOnClickListener(v->{
         btnStart.setEnabled(false);
         Act act = (Act) requireActivity();
         Rect rect = viewCrop.getCropRect();
         if (rect == null) {
            rect = Objects.requireNonNull(viewCrop.getWholeImageRect());
            act.gotoPuzzleFromGenerator(bitmapUri,
             selectedSize.wxh.x, selectedSize.wxh.y,
             rect.width(), rect.height()
            );
         }
         else {
            // TODO: // Subscribe to async event using cropImageView.setOnCropImageCompleteListener(listener)
            viewCrop.croppedImageAsync(
             CompressFormat.PNG, 100,
             rect.width(), rect.height(), RequestSizeOptions.NONE,
             act.db().makeCroppedPNGFilename(ctx)
            );
         }

//         DB db = act.db();
//         int bitmapID;
//         if (cropped)
//            // save cropped image in ctx.getFilesDir()
//            bitmapID = db.saveCroppedBitmap(bitmapUri, croppedBitmap, act);
//         else
//            bitmapID = db.getBitmapID(bitmapUri);

//         ImagePuzzle generated = ImagePuzzle.generateNewPuzzle(
//          selectedSize.wxh.x, selectedSize.wxh.y,
//          croppedBitmap, bitmapID, new Random());
      
      
      
      
      });
   }
   
   /**
    * TODO: maybe change some settings of the view, see {@link CropImageOptions}
    * @param crop
    */
   void setCropMode(boolean crop)
   {
      cropMode = crop;
      if (cropMode) {
         // remove forced ratio in cropMode.
         viewCrop.clearAspectRatio();
         
         scrvGeneratorSizes.setVisibility(View.GONE);
         llhStart.setVisibility(View.GONE);
         btnCropOnly.setVisibility(View.GONE);
      }
      else {
         // TODO: make viewCrop force the ratio of the selected puzzle size when not in cropMode.
         
         // TODO: unselect size when returning from cropMode!
         
         if (selectedSize != null) {
            //viewCrop.setAspectRatio();
            //viewCrop.setFixedAspectRatio(true);
         }
         
         
         scrvGeneratorSizes.setVisibility(View.VISIBLE);
         llhStart.setVisibility(View.VISIBLE);
         btnCropOnly.setVisibility(View.VISIBLE);
         
         // TODO: only addChoices if changes were made to ratio!
         addChoices(requireContext()); // load choices again (on top) after crop
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
      txtCustomWidth.setText(String.valueOf(selectedSize.wxh.x));
      txtCustomHeight.setText(String.valueOf(selectedSize.wxh.y));
      txtCustomWidth.setTextColor(normalTextColor);
      txtCustomHeight.setTextColor(normalTextColor);
      txtCustomWidth.setEnabled(true);
      txtCustomHeight.setEnabled(true);
      
      lblCustomSize.setText(
       getString(R.string.lblCustomSize, selectedSize.wxh.x * selectedSize.wxh.y)
      );
      lblCustomSize.setVisibility(View.VISIBLE);
   }
   
   void addChoices(Context ctx)
   {
      String[] sizes = Prefs.get(ctx, GeneratorStr.sizeChoices).split(Prefs.sizeChoicesSplit);
      int i = 0; // add views to the top of the layout
      for (String sizeChoice: sizes) {
         int circaSize = Integer.parseInt(sizeChoice);
         SizeOption option = new SizeOption(ctx, circaSize, radioChangeListener, viewCrop.getCropRect());
         sizeOptions.add(i, option);
         llvGeneratorSizes.addView(option.radioButton, i++);
      }
   }
   
   public void surfaceCreated(@NonNull SurfaceHolder holder)
   {
   
   }
   
   public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height)
   {
   
   }
   
   public void surfaceDestroyed(@NonNull SurfaceHolder holder)
   {
   
   }

//   public void surfaceRedrawNeeded(@NonNull SurfaceHolder holder)
//   {
//
//   }
//
//   public void surfaceRedrawNeededAsync(@NonNull SurfaceHolder holder, @NonNull Runnable drawingFinished)
//   {
//      Callback2.super.surfaceRedrawNeededAsync(holder, drawingFinished);
//   }
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
   ui = new Views(view, requireContext());
   
   Bundle arguments = getArguments();
   if (arguments != null) {
      int id = arguments.getInt(ARG_BITMAP_ID);
      if (id != 0) {
         Act act = (Act) requireActivity();
//         croppedBitmap = act.getBitmap(id);
         bitmapUri = act.db().getBitmapUri(id);
      }
      else {
         Uri uri = arguments.getParcelable(ARG_BITMAP_URI);
         if (uri != null) {
            // croppedBitmap = DB.loadBitmapFromUri(uri, requireContext());
            bitmapUri = uri;
         }
         else {
            Log.d(DBG, "onViewCreated() - all arguments (ARG_BITMAP_*) are null!");
         }
      }
      
      
      if (bitmapUri == null)
         Log.d(DBG, "bitmapUri is null");
      else
         ui.viewCrop.setImageUriAsync(bitmapUri);
      
      ui.setCropMode(false);
   }
   else Log.d(DBG, "onViewCreated() - Bundle getArguments() returns null!");
}

public void handleOnBackPressed(BackCallback callback)
{
   if (ui.cropMode) { // TODO: delete ui.cropMode field
      ui.setCropMode(false);
   }
   else
      callback.goBackToMenu();
}
}
