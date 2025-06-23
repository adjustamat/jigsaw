package github.adjustamat.jigsawpuzzlefloss.prefs;

/**
 * Contains all user preferences for this game.
 */
public interface Pref
{
interface BoolPref { }

interface IntPref{ }

interface LongPref { }

interface StrPref{ }

interface GeneratorPrefs
{
}

interface BackgroundPrefs
{
}

interface PlayPrefs
{
}

interface OtherPrefs
{
   enum Str implements StrPref{
      downloadDir,
      downloadFromURL
   }
}



}
