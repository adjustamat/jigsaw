package github.adjustamat.jigsawpuzzlefloss.db;

import android.app.Application;
import android.content.Context;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Global
 extends Application
{

private static Global global(Context ctx)
{
   if (ctx instanceof Global)
      return (Global) ctx;
   return (Global) ctx.getApplicationContext();
}

public void onCreate()
{
   super.onCreate();
   backgroundThreads = new ThreadPoolExecutor(0, 10,
    2, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
}

private ThreadPoolExecutor backgroundThreads;

public static void runOnBgThread(Context ctx, Runnable runnable)
{
   // Run on our background thread pool executor:
   global(ctx).backgroundThreads.execute(runnable);
}

public static void initEverything(Context ctx)
{
   Prefs.init(global(ctx));
//   global(ctx).initEverything();
}

//private void initEverything()
//{
//   Prefs.init(this);
//}
}
