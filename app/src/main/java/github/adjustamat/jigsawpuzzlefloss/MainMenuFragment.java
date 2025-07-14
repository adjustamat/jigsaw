package github.adjustamat.jigsawpuzzlefloss;

import android.Manifest.permission;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts.PickMultipleVisualMedia;
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.DefaultTab.AlbumsTab;
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly;
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Map.Entry;

import github.adjustamat.jigsawpuzzlefloss.db.DB;
import github.adjustamat.jigsawpuzzlefloss.db.Prefs;
import github.adjustamat.jigsawpuzzlefloss.db.Prefs.OtherStr;

public class MainMenuFragment
 extends Fragment
 implements Frag
{

private class Views
 implements ActivityResultCallback<List<Uri>>
{
   final TextView lblNoStartedPuzzles;
   final RecyclerView lstStartedPuzzles;
   final BitmapsAdapter startedPuzzles;
   
   final TextView lblNoBitmaps;
   final RecyclerView lstBitmaps;
   final BitmapsAdapter bitmaps;
   
   final Button btnDownloadBitmap;
   final Button btnOpenBitmap;
   final Button btnSettings;
   
   private Views(View view, Context ctx)
   {
      this.lblNoStartedPuzzles = view.findViewById(R.id.lblNoStartedPuzzles);
      this.lstStartedPuzzles = view.findViewById(R.id.lstStartedPuzzles);
      this.lblNoBitmaps = view.findViewById(R.id.lblNoBitmaps);
      this.lstBitmaps = view.findViewById(R.id.lstBitmaps);
      this.btnDownloadBitmap = view.findViewById(R.id.btnDownloadBitmap);
      this.btnOpenBitmap = view.findViewById(R.id.btnOpenBitmap);
      this.btnSettings = view.findViewById(R.id.btnSettings);
      
      Act act = (Act) ctx;
      DB db = act.db();
      
      startedPuzzles = new BitmapsAdapter(lblNoStartedPuzzles,
       db::getStartedGameCount, db::getGameBitmapUri, db::getGameProgress);
      lstStartedPuzzles.setAdapter(startedPuzzles);
      
      bitmaps = new BitmapsAdapter(lblNoBitmaps,
       db::getBitmapCount, db::getBitmapUri, nullProgress);
      lstBitmaps.setAdapter(bitmaps);
      
      btnDownloadBitmap.setOnClickListener(v->{
         // open URL in browser.
         Intent intent = new Intent(Intent.ACTION_VIEW,
          Uri.parse(Prefs.get(ctx, OtherStr.downloadFromURL)));
         ctx.startActivity(intent);
      });
      
      btnOpenBitmap.setOnClickListener(v->{
         // show image picker if we have permissions, otherwise ask for permissions.
         // the chosen images are received in onActivityResult(List<Uri> images)
         
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // UPSIDE_DOWN_CAKE == 34
            checkPermissions(new String[]{permission.READ_MEDIA_IMAGES,
             permission.READ_MEDIA_VISUAL_USER_SELECTED}, ctx);
         }
         else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) { // TIRAMISU == 33
            checkPermissions(new String[]{permission.READ_MEDIA_IMAGES}, ctx);
         }
         else { // VERSION.SDK_INT <= 32
            checkPermissions(new String[]{permission.READ_EXTERNAL_STORAGE}, ctx);
         }
      });
      
      btnSettings.setOnClickListener(v->act.showNewPrefs());
      
   }
   
   private void checkPermissions(String[] permissions, Context ctx)
   {
      // first, check if we already have permissions.
      boolean denied = false;
      for (String s: permissions) {
         if (PackageManager.PERMISSION_DENIED == ContextCompat.checkSelfPermission(
          ctx, permission.READ_MEDIA_IMAGES))
            denied = true;
      }
      // show image picker if we have permissions.
      if (!denied) {
         showImagePickerForActivityResult();
         return;
      }
      // ask user for permissions.
      ActivityResultLauncher<String[]> requestPermissions =
       registerForActivityResult(new RequestMultiplePermissions(),
        stringBooleanMap->{
           // after user interaction, check again.
           boolean deniedAgain = false;
           for (Entry<String, Boolean> entry: stringBooleanMap.entrySet()) {
              if (entry.getValue() == Boolean.FALSE)
                 deniedAgain = true;
           }
           // show image picker if we have permissions.
           if (!deniedAgain) {
              showImagePickerForActivityResult();
           }
           // denied again! do nothing.
        });
      requestPermissions.launch(permissions);
   }
   
   private void showImagePickerForActivityResult()
   {
      ActivityResultLauncher<PickVisualMediaRequest> pickImages =
       registerForActivityResult(new PickMultipleVisualMedia(), this);
      pickImages.launch(new PickVisualMediaRequest.Builder()
       .setMediaType(ImageOnly.INSTANCE)
       .setDefaultTab(AlbumsTab.INSTANCE)
       .setOrderedSelection(true)
       .build());
      // the chosen images are received in onActivityResult(List<Uri> images)
   }
   
   public void onActivityResult(List<Uri> images)
   {
      // check if the user chose one or more images in the image picker. do nothing if none chosen.
      if (images.size() == 1) {
         Act act = (Act) requireActivity();
         
         // save persistable permission, and save bitmapUri in database.
         Uri uri = images.get(0);
         act.getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
         act.db().saveBitmapUri(uri);
         
         // start a new game with the chosen image.
         act.showNewGenerator(uri);
      }
      else if (!images.isEmpty()) {
         Act act = (Act) requireActivity();
         ContentResolver contentResolver = act.getContentResolver();
         DB db = act.db();
         
         // save persistable permissions, and save bitmapUris in database.
         for (Uri uri: images) {
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            db.saveBitmapUri(uri);
         }
         
         // show the chosen images in the bitmaps list.
         lstBitmaps.postInvalidate();
      }
   }
}

private MainMenuFragment.Views ui;

public MainMenuFragment()
{
   // Required empty public constructor
}

@Override
public void onCreate(Bundle savedInstanceState)
{
   super.onCreate(savedInstanceState);
}

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
{
   // Inflate the layout for this fragment
   return inflater.inflate(R.layout.fragment_mainmenu, container, false);
}

public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
{
   super.onViewCreated(view, savedInstanceState);
   ui = new Views(view, requireActivity());
}

public void handleOnBackPressed(BackCallback callback)
{
   callback.goBackQuit();
}

public @FunctionalInterface interface GetSize
{
   int getSize();
}

public @FunctionalInterface interface GetUri
{
   Uri getUri(int index);
}

public @FunctionalInterface interface GetProgress
{
   Integer getProgress(int index);
}

private final GetProgress nullProgress = index->null;

public class BitmapsAdapter
 extends RecyclerView.Adapter<MenuListItemView>
{
   private final View lblEmpty;
   //private final DB db;
   private final GetSize getSizeFun;
   private final GetUri getUriFun;
   private final GetProgress getProgressFun;
   
   public BitmapsAdapter(View lblEmpty, GetSize sizeFun, GetUri uriFun, GetProgress progressFun)
   {
      this.lblEmpty = lblEmpty;
      this.getSizeFun = sizeFun;
      this.getUriFun = uriFun;
      this.getProgressFun = progressFun;
   }
   
   @NonNull public MenuListItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
   {
      LayoutInflater inflater = LayoutInflater.from(parent.getContext());
      return new MenuListItemView(
       inflater.inflate(R.layout.itemview_mainmenu_bitmap, parent, false)
      );
   }
   
   public void onBindViewHolder(@NonNull MenuListItemView holder, int position)
   {
      Uri uri = getUriFun.getUri(position);
      Glide.with(thisFragment())
       .load(uri)
       .fitCenter()
       .into(holder.imgMenuItem);
      Integer percent = getProgressFun.getProgress(position);
      if (percent == null) {
         holder.puzzleProgressBar.setVisibility(View.GONE);
      }
      else {
         holder.puzzleProgressBar.setProgress(percent);
         holder.puzzleProgressBar.setVisibility(View.VISIBLE);
      }
   }
   
   public int getItemCount()
   {
      int size = getSizeFun.getSize();
      if (size > 0) // hide lblEmpty when size becomes >0
         lblEmpty.setVisibility(View.GONE);
      return size;
   }
}

public static class MenuListItemView
 extends RecyclerView.ViewHolder
{
   public final ImageView imgMenuItem;
   public final ProgressBar puzzleProgressBar;
   
   public MenuListItemView(@NonNull View itemView)
   {
      super(itemView);
      imgMenuItem = itemView.findViewById(R.id.imgMenuItem);
      puzzleProgressBar = itemView.findViewById(R.id.puzzleProgressBar);
   }
}
}
