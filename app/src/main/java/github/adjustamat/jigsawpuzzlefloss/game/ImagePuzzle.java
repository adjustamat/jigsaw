package github.adjustamat.jigsawpuzzlefloss.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Parcel;

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
import github.adjustamat.jigsawpuzzlefloss.pieces.PieceEdge;
import github.adjustamat.jigsawpuzzlefloss.pieces.PieceEdge.HalfEdge;
import github.adjustamat.jigsawpuzzlefloss.pieces.PieceEdge.RandomEdge;
import github.adjustamat.jigsawpuzzlefloss.pieces.SinglePiece;

/**
 * An instance of an image turned into many jigsaw puzzle pieces ({@link SinglePiece}s).
 * This class contains the generation algorithm for {@link ImagePuzzle}s.
 */
public class ImagePuzzle
{

public final int width;
public final int height;
public final int totalPieces;

public final Bitmap image;

public final int bitmapID;

public final float pieceImageSize;

public final Box singlePiecesContainer;
public final PlayMat playMatContainer;

public final List<Group> temporaryContainers = new ArrayList<>();

public void writeToParcel(Parcel dest, int flags)
{
   dest.writeInt(width);
   dest.writeInt(height);
   dest.writeInt(bitmapID);
   
   dest.writeInt(singlePiecesContainer.list.size());
   for (GroupOrSinglePiece groupOrSinglePiece: singlePiecesContainer.list) {
      dest.writeInt(groupOrSinglePiece instanceof Group ?1 :0);
      groupOrSinglePiece.writeToParcel(dest, flags);
//      if () {
//
//         ((Group) groupOrSinglePiece).writeToParcel(dest, flags);
//      }
//      else {
//         dest.writeInt(0);
//         ((SinglePiece) groupOrSinglePiece).writeToParcel(dest, flags);
//      }
   }
   
   dest.writeInt(temporaryContainers.size());
   for (Group group: temporaryContainers) {
      group.writeToParcel(dest, flags);
   }
   
   dest.writeInt(playMatContainer.groups.size());
   for (Group group: playMatContainer.groups) {
      group.writeToParcel(dest, flags);
   }
   
   dest.writeInt(playMatContainer.singlePieces.size());
   for (SinglePiece piece: playMatContainer.singlePieces) {
      piece.writeToParcel(dest, flags);
   }
   
   dest.writeInt(playMatContainer.largerPieces.size());
   for (LargerPiece piece: playMatContainer.largerPieces) {
      piece.writeToParcel(dest, flags);
   }
}

public static ImagePuzzle createFromParcel(Parcel in, Context ctx, DB db)
{
   Loading loading = new Loading();
   
   int width = in.readInt();
   int height = in.readInt();
   int bitmapID = in.readInt();
   
   Bitmap bitmap = db.getBitmap(bitmapID, ctx);
   
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
   
   return ImagePuzzle.getPuzzleFromDatabase(width, height, bitmap, bitmapID,
    box, temporaryContainers,
    playMatGroups, playMatSinglePieces, playMatLargerPieces);
}

public static ImagePuzzle getPuzzleFromDatabase(int pWidth, int pHeight, Bitmap bitmap, int bitmapID,
 List<GroupOrSinglePiece> box, List<Group> temporaryContainers,
 List<Group> playMatGroups, List<SinglePiece> playMatSinglePieces, List<LargerPiece> playMatLargerPieces)
{
   ImagePuzzle ret = new ImagePuzzle(pWidth, pHeight, bitmap, bitmapID, box);
   ret.playMatContainer.setFromDatabase(playMatGroups, playMatSinglePieces, playMatLargerPieces);
   ret.temporaryContainers.addAll(temporaryContainers);
   ret.replaceLoadingWithRealContainers();
   return ret;
}

private ImagePuzzle(int width, int height, Bitmap croppedImage, int bitmapID, List<GroupOrSinglePiece> pieceList)
{
   this.width = width;
   this.height = height;
   this.totalPieces = width * height;
   
   this.image = croppedImage;
   this.bitmapID = bitmapID;
   
   this.pieceImageSize = (float) croppedImage.getHeight() / height;
   
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
 * @param croppedImage the image
 * @param rng a random number generator
 * @return a new ImagePuzzle with the given size and image
 */
public static ImagePuzzle generateNewPuzzle(int pWidth, int pHeight, Bitmap croppedImage, int bitmapID, Random rng)
{
   LinkedList<GroupOrSinglePiece> singlePieces = new LinkedList<>();
   ImagePuzzle ret = new ImagePuzzle(pWidth, pHeight, croppedImage, bitmapID, singlePieces);
   HalfEdge[][] pool = PieceEdge.generateAllJigsawEdges();
   
   RandomEdge[] wests = new RandomEdge[pHeight];
   for (int x = 0; x < pWidth; x++) {
      RandomEdge north = null;
      for (int y = 0; y < pHeight; y++) {
         RandomEdge east, south;
         
         if (x == pWidth - 1)
            east = null;
         else
            east = new RandomEdge(rng);
         
         if (y == pHeight - 1)
            south = null;
         else
            south = new RandomEdge(rng);
         
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
