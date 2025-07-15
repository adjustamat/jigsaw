package github.adjustamat.jigsawpuzzlefloss.game;

import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.os.Parcel;
import android.util.Size;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import github.adjustamat.jigsawpuzzlefloss.containers.Box;
import github.adjustamat.jigsawpuzzlefloss.containers.Box.GroupOrSinglePiece;
import github.adjustamat.jigsawpuzzlefloss.containers.Container.Loading;
import github.adjustamat.jigsawpuzzlefloss.containers.Group;
import github.adjustamat.jigsawpuzzlefloss.containers.PlayMat;
import github.adjustamat.jigsawpuzzlefloss.db.DB;
import github.adjustamat.jigsawpuzzlefloss.pieces.LargerPiece;
import github.adjustamat.jigsawpuzzlefloss.pieces.PieceJedge;
import github.adjustamat.jigsawpuzzlefloss.pieces.PieceJedge.HalfJedge;
import github.adjustamat.jigsawpuzzlefloss.pieces.PieceJedge.JedgeParams;
import github.adjustamat.jigsawpuzzlefloss.pieces.SinglePiece;

/**
 * An instance of an image turned into many jigsaw puzzle pieces ({@link SinglePiece}s).
 * This class contains the generation algorithm for {@link ImagePuzzle}s.
 */
public class ImagePuzzle
{
private int groupCounter = 0;

public final int width;
public final int height;
public final int totalPieces;

public final int gameID;

public final Uri bitmapUri;
public final Size bitmapSize;

public final float pieceImageSize;

public final Box singlePiecesContainer;
public final PlayMat playMatContainer;

public final List<Group> temporaryContainers = new ArrayList<>();

public void writeToParcel(Parcel dest)
{
   dest.writeInt(width);
   dest.writeInt(height);
   
   dest.writeInt(singlePiecesContainer.list.size());
   for (GroupOrSinglePiece groupOrSinglePiece: singlePiecesContainer.list) {
      dest.writeInt(groupOrSinglePiece instanceof Group ?1 :0);
      groupOrSinglePiece.writeToParcel(dest);
//      if () {
//
//         ((Group) groupOrSinglePiece).writeToParcel(dest);
//      }
//      else {
//         dest.writeInt(0);
//         ((SinglePiece) groupOrSinglePiece).writeToParcel(dest);
//      }
   }
   
   dest.writeInt(temporaryContainers.size());
   for (Group group: temporaryContainers) {
      group.writeToParcel(dest);
   }
   
   dest.writeInt(playMatContainer.groups.size());
   for (Group group: playMatContainer.groups) {
      group.writeToParcel(dest);
   }
   
   dest.writeInt(playMatContainer.singlePieces.size());
   for (SinglePiece piece: playMatContainer.singlePieces) {
      piece.writeToParcel(dest);
   }
   
   dest.writeInt(playMatContainer.largerPieces.size());
   for (LargerPiece piece: playMatContainer.largerPieces) {
      piece.writeToParcel(dest);
   }
}

public static ImagePuzzle loadFromDatabase(int gameID, Context ctx, DB db)
{
   Loading loading = new Loading();
   
   Parcel in = db.getGameData(gameID);
   
   
   
   
   int width = in.readInt();
   int height = in.readInt();
   
   Size bitmapSize = new Size(in.readInt(), in.readInt()); // TODO: writeInt!
   Uri bitmapUri = db.getGameBitmapUri(gameID);
   
   int progressPercent = db.getGameProgress(gameID);
   
   int bitmapID = in.readInt();
   
   //Bitmap bitmap = db.getBitmap(bitmapID, ctx); TODO: image loader
   
   int size = in.readInt();
   List<GroupOrSinglePiece> box = new ArrayList<>(size);
   for (int i = 0; i < size; i++) {
      int isGroup = in.readInt();
      if (isGroup != 0)
         box.add(Group.createFromParcelToBox(in, loading));
      else
         box.add(SinglePiece.createFromParcelToBox(in, loading));
   }
   
   size = in.readInt();
   List<Group> temporaryContainers = new ArrayList<>(size);
   for (int i = 0; i < size; i++) {
      temporaryContainers.add(Group.createFromParcelToTemp(in, loading));
   }
   
   size = in.readInt();
   List<Group> playMatGroups = new ArrayList<>(size);
   for (int i = 0; i < size; i++) {
      playMatGroups.add(Group.createFromParcelToPlayMat(in, loading));
   }
   
   size = in.readInt();
   List<SinglePiece> playMatSinglePieces = new ArrayList<>(size);
   for (int i = 0; i < size; i++) {
      playMatSinglePieces.add(SinglePiece.createFromParcelToPlayMat(in, loading));
   }
   
   size = in.readInt();
   List<LargerPiece> playMatLargerPieces = new ArrayList<>(size);
   for (int i = 0; i < size; i++) {
      playMatLargerPieces.add(LargerPiece.createFromParcelToPlayMat(in, loading));
   }
   
   
   
   in.recycle();
   
   ImagePuzzle ret = new ImagePuzzle(width, height, gameID,
    bitmapSize, bitmapUri, box);
   ret.playMatContainer.setFromDatabase(playMatGroups, playMatSinglePieces, playMatLargerPieces);
   ret.temporaryContainers.addAll(temporaryContainers);
   ret.replaceLoadingWithRealContainers();
   return ret;
}

private ImagePuzzle(int width, int height, int gameID,
 Size bitmapSize, Uri bitmapUri, List<GroupOrSinglePiece> pieceList)
{
   this.width = width;
   this.height = height;
   this.totalPieces = width * height;
   this.gameID = gameID;
   
   //this.image = croppedImage; TODO: use image loader!
   this.bitmapSize = bitmapSize;
   this.bitmapUri = bitmapUri;
   
   this.pieceImageSize = (float) bitmapSize.getHeight() / height;
   
   this.singlePiecesContainer = new Box(pieceList, this);
   this.playMatContainer = new PlayMat();
}

private void replaceLoadingWithRealContainers()
{
   for (GroupOrSinglePiece groupOrSinglePiece: singlePiecesContainer.list) {
      groupOrSinglePiece.replaceLoading(singlePiecesContainer);
   }
   for (Group group: temporaryContainers) {
      group.replaceLoading(group);
   }
   for (Group group: playMatContainer.groups) {
      group.replaceLoading(playMatContainer);
   }
   for (SinglePiece piece: playMatContainer.singlePieces) {
      piece.replaceLoading(playMatContainer);
   }
   for (LargerPiece piece: playMatContainer.largerPieces) {
      piece.replaceLoading(playMatContainer);
   }
}

/**
 * Generate a random jigsaw pattern for the given image and size of the ImagePuzzle.
 * @param pWidth the width, in number of pieces
 * @param pHeight the height, in number of pieces
 * @param bitmapUri the image
 * @param bitmapSize the image size in pixels
 * @param rng a random number generator
 * @return a new ImagePuzzle with the given size and image
 */
public static ImagePuzzle generateNewPuzzle(int pWidth, int pHeight,
 Uri bitmapUri, Size bitmapSize,
 Random rng, int newGameID)
{
   LinkedList<GroupOrSinglePiece> singlePieces = new LinkedList<>();
   ImagePuzzle ret = new ImagePuzzle(pWidth, pHeight, newGameID,
    bitmapSize, bitmapUri, singlePieces);
   HalfJedge[][] pool = PieceJedge.generateAllJigsawEdges();
   
   JedgeParams[] wests = new JedgeParams[pHeight];
   for (int x = 0; x < pWidth; x++) {
      JedgeParams north = null;
      for (int y = 0; y < pHeight; y++) {
         JedgeParams east, south;
         
         if (x == pWidth - 1)
            east = null;
         else
            east = new JedgeParams(rng);
         
         if (y == pHeight - 1)
            south = null;
         else
            south = new JedgeParams(rng);
         
         singlePieces.add(new SinglePiece(
          ret,
          singlePieces.size(),
          new Point(x, y),
          north, east, south, wests[y],
          pool,
          rng.nextInt(4)
         ));
         
         //if (south != null)
         north = south;
         //if (east != null)
         wests[y] = east;
      } // for(y)
   } // for(x)
   return ret;
}

public int getNewGroupNumber()
{
   return ++groupCounter;
}

public int getProgressPercent()
{
   int total = playMatContainer.largerPieces.size();
   int singles = playMatContainer.singlePieces.size();
   total += singles;
   for (Group group: playMatContainer.groups) {
      int size = group.getPieceCount();
      total += size;
      singles += size - group.getLargerPieceCount();
   }
   for (Group group: temporaryContainers) {
      int size = group.getPieceCount();
      total += size;
      singles += size - group.getLargerPieceCount();
   }
   
   int inBox = 0;
   int ungroupedInBox = 0;
   for (GroupOrSinglePiece groupOrSinglePiece: singlePiecesContainer.list) {
      if (groupOrSinglePiece instanceof SinglePiece) {
         inBox++;
         ungroupedInBox++;
      }
      else {
         Group boxGroup = (Group) groupOrSinglePiece;
         inBox += boxGroup.getPieceCount();
      }
   }
   
   total += inBox;
   if (total == 1)
      return 100;
   singles += inBox;
   
   float tot = total * 33f / totalPieces;
   int totInt = Math.round(tot);
//   if (totInt == 33 && total < totalPieces)
//      totInt = 32;
   
   float single = singles * 33f / totalPieces;
   int singleInt = Math.round(single);
   if (singleInt == 33 && singles < totalPieces)
      singleInt = 32;
   else if (singleInt == 0 && singles > 0)
      singleInt = 1;
   
   float boxed = inBox * 23f / totalPieces;
   int boxedInt = Math.round(boxed);
   if (boxedInt == 23 && inBox < totalPieces)
      boxedInt = 22;
   else if (boxedInt == 0 && inBox > 0)
      boxedInt = 1;
   
   float ungrouped = ungroupedInBox * 10f / totalPieces;
   int ungroupedInt = Math.round(ungrouped);
   if (ungroupedInt == 10 && ungroupedInBox < totalPieces)
      ungroupedInt = 9;
   
   /*
   calculate progress like this:
   10%: how many pieces are ungrouped in the box
   23%: how many pieces are in the box
   33%: how many pieces are single (in or not in groups)
   33%: how many pieces there are total, down to 2
   1%: there is only 1 piece.
   // 99 - (int) (tot + single + boxed + ungrouped);
    */
   return 99 - ungroupedInt - boxedInt - singleInt - totInt;
}

// /**
// * A (named) area of an {@link ImagePuzzle}.
// * Every {@link SinglePiece} belongs to exactly one {@link Area}.
// */
//public static class Area
//{
//   public String areaName;
//   //   Shape includedPieces; // import android.graphics.drawable.shapes.Shape;
// //   boolean[][] included;
//   final DividingLine[] neswDividers;// = new DividingLine[4];
//
//   public Area(DividingLine n, DividingLine e, DividingLine s, DividingLine w)
//   {
//      neswDividers = new DividingLine[]{n, e, s, w};
//   }
//
//   public void setDivider(int direction, DividingLine divider)
//   {
//      neswDividers[direction] = divider;
//   }
//} // class Area
//
//public static class DividingLine
//{
//   Point point1, point2;
//}
}
