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
   enum Strs{
      CaPiecesList
   }
}

interface BgImagePrefs
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
