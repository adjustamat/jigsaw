package github.adjustamat.jigsawpuzzlefloss.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Parcel;
import android.util.Log;

import java.io.File;
import java.security.MessageDigest;

import github.adjustamat.jigsawpuzzlefloss.game.ImagePuzzle;

public class DB
 extends SQLiteOpenHelper
{
private static final String DBG = "DB";

public static final String DB_FILENAME = "DB";

public static final String BITMAP_TABLE_NAME = "BM";
public static final String C_INT_BITMAP_ID = "BMID";
public static final String C_STR_BITMAP_URI = "URI";

//public static final String SCHEME_CROPPED_BITMAP = "cropped";
// TODO: instead of SCHEME_CROPPED_BITMAP, use glide cache or cropper's Uri output.

public static final String GAME_TABLE_NAME = "PZ";
public static final String C_INT_GAME_ID = "PZID";
public static final String C_STR_GAME_BITMAP_URI = C_STR_BITMAP_URI;
public static final String C_INT_GAME_PROGRESS = "DONE";
public static final String C_BLOB_GAME_DATA = "DATA";

public static final String[] GET_COUNT = {"count(*)"};

private boolean loaded;

public DB(Context context)
{
   super(context, DB_FILENAME, null, 1);
}

public void onCreate(SQLiteDatabase sdb)
{
   sdb.execSQL(
    "CREATE TABLE " + BITMAP_TABLE_NAME + "(" +
     C_INT_BITMAP_ID + " INTEGER PRIMARY KEY," +
     C_STR_BITMAP_URI + " TEXT)"
   );
   sdb.execSQL(
    "CREATE TABLE " + GAME_TABLE_NAME + "(" +
     C_INT_GAME_ID + " INTEGER PRIMARY KEY," +
     C_STR_GAME_BITMAP_URI + " TEXT," +
     C_INT_GAME_PROGRESS + " INTEGER," +
     C_BLOB_GAME_DATA + " BLOB)"
   );
}

public void onUpgrade(SQLiteDatabase sdb, int oldVersion, int newVersion)
{
   // no upgrade yet
   onCreate(sdb);
}

public boolean isLoaded()
{
   return loaded;
}

public void loadOnBackgroundThread()
{
   if (loaded)
      return;
   SQLiteDatabase sdb = getWritableDatabase();
   sdb.close();
   loaded = true;
}

public Uri makeCroppedPNGFilename(Context ctx)
{
   String pngFilename = System.currentTimeMillis() + ".png";
//   Uri croppedUri = new Uri.Builder().scheme(SCHEME_CROPPED_BITMAP)
//    .opaquePart(pngFilename).build();
   
   Uri fileUri = Uri.fromFile(new File(ctx.getFilesDir()/*ctx.getCacheDir()*/, pngFilename));
   // save in database before returning, so that the cropped image appears in bitmaps list also.
   saveBitmapUri(fileUri);
   return fileUri;
}

private static File makeNewPNGFilename(Uri originalUri, Context ctx)
{
   String tooLongName = originalUri.toString() + System.currentTimeMillis();
   StringBuilder sha256 = new StringBuilder();
   try {
      byte[] shabytes = MessageDigest.getInstance("SHA-256")
       .digest(tooLongName.getBytes("UTF-8"));
      for (byte b: shabytes) {
         String bytestr = Integer.toHexString(b);
         if (bytestr.length() == 1)
            sha256.append('0');
         sha256.append(bytestr);
      }
   }
   catch (Exception e) {
      Log.e(DBG, "makeNewPNGFilename() - Exception", e);
      sha256.append("error");
   }
   sha256.append(".png");
   return new File(ctx.getFilesDir()/*ctx.getCacheDir()*/, sha256.toString());
}

///**
// * Saves bitmap as PNG in ctx.getFilesDir(). Creates new bitmapUri and saves it in the database.
// * @param originalUri the bitmapUri of the uncropped bitmap
// * @param bitmap the cropped bitmap
// * @param ctx a Context
// * @return a new bitmapID
// */
//public int saveCroppedBitmap(Uri originalUri, Bitmap bitmap, Context ctx)
//{
//   File pngFilename = makeNewPNGFilename(originalUri, ctx);
//   try {
//      FileOutputStream outputStream = new FileOutputStream(pngFilename);
//      //ctx.openFileOutput(pngFilename, Context.MODE_PRIVATE);
//      // TODO: use cropper, or not glide library
//      bitmap.compress(CompressFormat.PNG, 100, outputStream); // write to file!
//      outputStream.flush();
//      outputStream.close();
//   }
//   catch (IOException e) {
//      Log.e("DBG", "saveCroppedBitmap() - IOException", e);
//   }
//   return saveBitmapUri(
//    new Uri.Builder().scheme(SCHEME_CROPPED_BITMAP)
//     .opaquePart(pngFilename.getName()).build()
//   ); // Uri is like "cropped:p8jzs5ob717v4hsh.png"
//   //return saveBitmapUri(Uri.fromFile(pngFilename)); // Uri has file: scheme. (if file moves, there will be an error later)
//}

/**
 * Saves a bitmapUri in the database.
 * @param newBitmapUri a new bitmapUri
 * @return a new bitmapID
 */
public int saveBitmapUri(Uri newBitmapUri)
{
   // getNewBitmapID() AKA saveBitmapUri()
   SQLiteDatabase sdb = getWritableDatabase();
   ContentValues values = new ContentValues();
   values.put(C_STR_BITMAP_URI, newBitmapUri.toString());
   
   long rowID = sdb.insert(BITMAP_TABLE_NAME, null, values);
   
   sdb.close();
   return (int) rowID; // returned bitmapID is new row ID
}

public int getBitmapID(Uri bitmapUri)
{
   int ret = -1;
   SQLiteDatabase sdb = getReadableDatabase();
   Cursor cursor = sdb.query(BITMAP_TABLE_NAME, new String[]{C_INT_BITMAP_ID},
    C_STR_BITMAP_URI + " = " + bitmapUri, null, null, null, null);
   if (cursor.moveToFirst()) {
      ret = cursor.getInt(cursor.getColumnIndexOrThrow(C_INT_BITMAP_ID));
   }
   cursor.close();
   sdb.close();
   return ret;
}

public Uri getBitmapUri(int bitmapID)
{
   Uri ret = null;
   SQLiteDatabase sdb = getReadableDatabase();
   Cursor cursor = sdb.query(BITMAP_TABLE_NAME, new String[]{C_STR_BITMAP_URI},
    C_INT_BITMAP_ID + " = " + bitmapID, null, null, null, null);
   if (cursor.moveToFirst()) {
      String uriString = cursor.getString(cursor.getColumnIndexOrThrow(C_STR_BITMAP_URI));
      ret = Uri.parse(uriString);
   }
   cursor.close();
   sdb.close();
   return ret;
}

public int getBitmapCount()
{
   int count = 0;
   SQLiteDatabase sdb = this.getReadableDatabase();
   try (
    Cursor cursor = sdb.query(BITMAP_TABLE_NAME, GET_COUNT,
     null, null,
     null, null, null)
   ) {
      if (cursor.moveToNext()) {
         count = cursor.getInt(0);
      }
   }
   return count;
}

public int getStartedGameCount()
{
   int count = 0;
   SQLiteDatabase sdb = this.getReadableDatabase();
   Cursor cursor =
    sdb.query(GAME_TABLE_NAME, GET_COUNT,
     null, null,
     null, null, null
    );
//   try {
   if (cursor.moveToNext()) {
      count = cursor.getInt(0);
   }
//   }
//   finally {
   cursor.close();
//   }
   return count;
}

public int createNewSaveGame()
{
   int count = 0;
   SQLiteDatabase sdb = getWritableDatabase();
   ContentValues values = new ContentValues();
   
   //values.put(C_STR_GAME_BITMAP_URI, gameBitmapUri.toString());
   values.put(C_INT_GAME_PROGRESS, 0);
   
   long rowID = sdb.insert(GAME_TABLE_NAME, null, values);
   
   
   
   sdb.close();
   return (int) rowID; // returned gameID is new row ID
}

public void saveGame(ImagePuzzle imagePuzzle)
{
   SQLiteDatabase sdb = getWritableDatabase();
   ContentValues values = new ContentValues();
   
   values.put(C_STR_GAME_BITMAP_URI, imagePuzzle.bitmapUri.toString());
   values.put(C_INT_GAME_PROGRESS, imagePuzzle.getProgressPercent());
   
   Parcel parcel = Parcel.obtain();
   imagePuzzle.saveInDatabase(parcel);
   values.put(C_BLOB_GAME_DATA, parcel.marshall());
   parcel.recycle();
   
   sdb.update(GAME_TABLE_NAME, values,
    C_INT_GAME_ID + " = " + imagePuzzle.gameID, null);
   
   sdb.close();
}

public Uri getGameBitmapUri(int gameID)
{
   Uri ret = null;
   SQLiteDatabase sdb = getReadableDatabase();
   Cursor cursor = sdb.query(GAME_TABLE_NAME, new String[]{C_STR_GAME_BITMAP_URI},
    C_INT_GAME_ID + " = " + gameID, null, null, null, null);
   if (cursor.moveToFirst()) {
      String uriString = cursor.getString(cursor.getColumnIndexOrThrow(C_STR_GAME_BITMAP_URI));
      ret = Uri.parse(uriString);
   }
   cursor.close();
   sdb.close();
   return ret;
}

public int getGameProgress(int gameID)
{
   int ret = 0;
   SQLiteDatabase sdb = getReadableDatabase();
   Cursor cursor = sdb.query(GAME_TABLE_NAME, new String[]{C_INT_GAME_PROGRESS},
    C_INT_GAME_ID + " = " + gameID, null, null, null, null);
   if (cursor.moveToFirst()) {
      ret = cursor.getInt(cursor.getColumnIndexOrThrow(C_INT_GAME_PROGRESS));
   }
   cursor.close();
   sdb.close();
   return ret;
}

public Parcel getGameData(int gameID)
{
   Parcel ret = Parcel.obtain();
   SQLiteDatabase sdb = getReadableDatabase();
   Cursor cursor = sdb.query(GAME_TABLE_NAME, new String[]{C_BLOB_GAME_DATA},
    C_INT_GAME_ID + " = " + gameID, null, null, null, null);
   if (cursor.moveToFirst()) {
      byte[] data = cursor.getBlob(cursor.getColumnIndexOrThrow(C_BLOB_GAME_DATA));
      ret.unmarshall(data, 0, data.length);
   }
   cursor.close();
   sdb.close();
   return ret;
}

// TODO: use image loader library instead - several methods: getThumbnail, getHighResBitmap
//public Bitmap getBitmap(int bitmapID, Context ctx)
//{
//   return loadBitmapFromUri(getBitmapUri(bitmapID), ctx);
//}

//public static Bitmap loadBitmapFromUri(Uri uri, Context ctx)
//{
//   Bitmap ret = null;
//
//   if (SCHEME_CROPPED_BITMAP.equals(uri.getScheme())) {
//      return loadCroppedBitmap(uri.getSchemeSpecificPart(), ctx);
//   }
//
//   try {
//      // API 28: ImageDecoder.createSource(ctx.getContentResolver(),uri);
//      ret = MediaStore.Images.Media.getBitmap(ctx.getContentResolver(), uri);
//   }
//   catch (IOException e) {
//      Log.d(PuzzleGraphics.DBG, "loadBitmapFromUri(" + uri + ") - IOException", e);
//   }
//   return ret;
//}

//private static Bitmap loadCroppedBitmap(String pngFilename, Context ctx)
//{
//   Bitmap ret = null;
//   try {
//      File file = new File(ctx.getFilesDir(), pngFilename);
//      if (file.isFile()) {
//         FileInputStream inputStream = new FileInputStream(file);
//         //ctx.openFileInput(file);
//         ret = BitmapFactory.decodeStream(inputStream);
//         inputStream.close();
//      }
//   }
//   catch (IOException e) {
//      Log.e(DBG, "loadCroppedBitmap(" + pngFilename + ") - IOException", e);
//   }
//   return ret;
//}
}
