package github.adjustamat.jigsawpuzzlefloss.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.regex.Pattern;

/**
 * Contains all user preferences for this game.
 */
public interface Prefs
{

static void init(Global ctx)
{
   SharedPreferences sharedPreferences = getSharedPreferences(ctx);
   if (!sharedPreferences.contains("initialized")) {
      Editor editor = sharedPreferences.edit();
      editor.putBoolean("initialized", true);
      // TODO: set default preferences!
      editor.apply();
   }
}

static SharedPreferences getSharedPreferences(Context ctx)
{
   return ctx.getSharedPreferences("Prefs", 0);
}

static SharedPreferences getGamePreferences(Context ctx, int gameID)
{
   return ctx.getSharedPreferences("Prefs", 0);
}

interface BoolPref
{
   String name();
}

interface IntPref
{
   String name();
}

interface LongPref
{
   String name();
}

interface FloatPref
{
   String name();
}

interface StrPref
{
   String name();
}

enum GeneratorStr
 implements StrPref
{
   sizeChoices
}

// TODO: only compile these if starting SettingsFragment
Pattern sizeChoicesFilter = Pattern.compile("(\\d+\\s*,\\s*)*\\d+");
Pattern sizeChoicesSeparator = Pattern.compile("\\s*,\\s*");
String sizeChoicesSplit = ", ";

static String filterSizeChoices(String input){
   if(sizeChoicesFilter.matcher(input).matches()){
      return sizeChoicesSeparator.matcher(input).replaceAll(sizeChoicesSplit);
   }
   return null;
}

//interface BgImagePrefs
//{
//}
//
//interface PlayPrefs
//{
//}

enum OtherStr
 implements StrPref
{
   downloadDir,
   downloadFromURL
}


static void save(Context ctx, StrPref strPref, String value)
{
   getSharedPreferences(ctx).edit().putString(strPref.name(), value).apply();
}
static void save(Context ctx, LongPref longPref, long value)
{
   getSharedPreferences(ctx).edit().putLong(longPref.name(), value).apply();
}
static void save(Context ctx, IntPref intPref, int value)
{
   getSharedPreferences(ctx).edit().putInt(intPref.name(), value).apply();
}
static void save(Context ctx, FloatPref floatPref, float value)
{
   getSharedPreferences(ctx).edit().putFloat(floatPref.name(), value).apply();
}
static void save(Context ctx, BoolPref boolPref, boolean value)
{
   getSharedPreferences(ctx).edit().putBoolean(boolPref.name(), value).apply();
}

static String get(Context ctx, StrPref strPref)
{
   return getSharedPreferences(ctx).getString(strPref.name(), "");
}
static long get(Context ctx, LongPref longPref)
{
   return getSharedPreferences(ctx).getLong(longPref.name(), 0L);
}
static int get(Context ctx, IntPref intPref)
{
   return getSharedPreferences(ctx).getInt(intPref.name(), 0);
}
static float get(Context ctx, FloatPref floatPref)
{
   return getSharedPreferences(ctx).getFloat(floatPref.name(), 0f);
}
static boolean get(Context ctx, BoolPref boolPref)
{
   return getSharedPreferences(ctx).getBoolean(boolPref.name(), false);
}
}
