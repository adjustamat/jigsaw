package github.adjustamat.jigsawpuzzlefloss.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;

import github.adjustamat.jigsawpuzzlefloss.ui.PuzzleGraphics;

public class DB
 extends SQLiteOpenHelper
{
public static final String DB_FILENAME = "DB";

public static final String BITMAP_TABLE_NAME = "BM";
public static final String C_INT_BITMAP_ID = "BMID";
//public static final String C_STR_BITMAP_FILE = "LOCAL";
public static final String C_STR_BITMAP_URI = "URI";

public static final String GAME_TABLE_NAME = "PZ";
public static final String C_INT_GAME_ID = "PZID";
public static final String C_BLOB_GAME_DATA = "DATA";

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
     //     C_STR_BITMAP_FILE + "TEXT," +
     C_STR_BITMAP_URI + " TEXT)"
   );
   
}

public void loadOnBackgroundThread(){
   if(loaded)
      return;
   SQLiteDatabase sdb = getWritableDatabase();
   sdb.close();
   loaded = true;
}

public void onUpgrade(SQLiteDatabase sdb, int oldVersion, int newVersion)
{
   // no upgrade yet
   onCreate(sdb);
}

public int saveCroppedBitmap(Bitmap bitmap, Context ctx){
   
   // TODO: save bitmap with FileOutputStream - PNG
   //  Uri uri = Uri.fromFile(file);
   
   // TODO: insert, return bitmapID = new rowid
}

public int saveBitmap(Uri uri, Bitmap bitmap, Context ctx){
   // TODO: insert, return bitmapID = new rowid
}

public Bitmap getBitmap(int bitmapID, Context ctx)
{
   Bitmap ret = null;
   SQLiteDatabase sdb = getReadableDatabase();
   Cursor cursor = sdb.query(BITMAP_TABLE_NAME, new String[]{C_STR_BITMAP_URI
     // , C_STR_BITMAP_FILE
    },
    C_INT_BITMAP_ID + " = " + bitmapID, null, null, null, null);
   if (cursor.moveToFirst()) {
//      String filestr = cursor.getString(cursor.getColumnIndexOrThrow(C_STR_BITMAP_FILE));
//      if (filestr != null && !filestr.isEmpty()) {
//         Uri.fromFile(new File(filestr));
//      }
//      else {
      String uriString = cursor.getString(cursor.getColumnIndexOrThrow(C_STR_BITMAP_URI));
      ret = PuzzleGraphics.loadBitmapFromUri(Uri.parse(uriString), ctx);

//      }
   
   }
   cursor.close();
   sdb.close();
   return ret;
}
}
