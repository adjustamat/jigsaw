package github.adjustamat.jigsawpuzzlefloss;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts.PickMultipleVisualMedia;
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.DefaultTab.AlbumsTab;
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainMenuFragment
 extends Fragment
 implements Frag, ActivityResultCallback<List<Uri>>
{

private class Views
{
   final TextView lblNoStartedPuzzles;
   final RecyclerView lstStartedPuzzles;
   
   final TextView lblNoBitmaps;
   final RecyclerView lstBitmaps;
   
   final Button btnDownloadBitmap;
   final Button btnOpenBitmap;
   final Button btnSettings;
   
   private Views(TextView lblNoStartedPuzzles, RecyclerView lstStartedPuzzles, TextView lblNoBitmaps,
    RecyclerView lstBitmaps, Button btnDownloadBitmap, Button btnOpenBitmap, Button btnSettings)
   {
      this.lblNoStartedPuzzles = lblNoStartedPuzzles;
      this.lstStartedPuzzles = lstStartedPuzzles;
      this.lblNoBitmaps = lblNoBitmaps;
      this.lstBitmaps = lstBitmaps;
      this.btnDownloadBitmap = btnDownloadBitmap;
      this.btnOpenBitmap = btnOpenBitmap;
      this.btnSettings = btnSettings;
   }
}

private Views ui;

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
   ui = new Views(
    view.findViewById(R.id.lblNoStartedPuzzles),
    view.findViewById(R.id.lstStartedPuzzles),
    view.findViewById(R.id.lblNoBitmaps),
    view.findViewById(R.id.lstBitmaps),
    view.findViewById(R.id.btnDownloadBitmap),
    view.findViewById(R.id.btnOpenBitmap),
    view.findViewById(R.id.btnSettings)
   );
}

public void handleOnBackPressed(BackCallback callback)
{
   callback.goBackQuit();
}

private void startPickImagesForResult()
{
   ActivityResultLauncher<PickVisualMediaRequest> launcher =
    registerForActivityResult(new PickMultipleVisualMedia(), this);
   launcher.launch(new PickVisualMediaRequest.Builder()
    .setMediaType(ImageOnly.INSTANCE)
    .setDefaultTab(AlbumsTab.INSTANCE)
    .setOrderedSelection(true)
    .build());
}

public void onActivityResult(List<Uri> images)
{

/*
  
   ImagePuzzle puzzle = ImagePuzzle.generateNewPuzzle(7,5,BITMAP,new Random());
   showNewPlayMat();
 */
}

public static class MenuListItemView
 extends RecyclerView.ViewHolder
{
   public final ImageView imgBoxItem;
   
   public MenuListItemView(@NonNull View itemView)
   {
      super(itemView);
      imgBoxItem = itemView.findViewById(R.id.imgBoxItem);
   }
}
}
