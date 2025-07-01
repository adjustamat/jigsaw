package github.adjustamat.jigsawpuzzlefloss.db;

import android.app.Application;
import android.content.Context;

public class Global
 extends Application
{



private static Global global(Context ctx)
{
   if (ctx instanceof Global)
      return (Global) ctx;
   return (Global) ctx.getApplicationContext();
}

//public Context/*Global*/ getApplicationContext()
//{
//   return this;
//}

public static void initEverything(Context ctx)
{
   global(ctx).initEverything();
}

public void initEverything()
{
   Prefs.init(this);
}
}
