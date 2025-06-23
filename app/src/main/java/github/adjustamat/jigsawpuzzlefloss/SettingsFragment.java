package github.adjustamat.jigsawpuzzlefloss;

import android.content.Context;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreferenceCompat;

public class SettingsFragment
 extends PreferenceFragmentCompat
{

@Override
public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
{
   Context ctx = getPreferenceManager().getContext();
   
   PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(ctx);
   
   SwitchPreferenceCompat notificationPreference = new SwitchPreferenceCompat(ctx);
   notificationPreference.setKey("notifications");
   notificationPreference.setTitle("Enable message notifications");
   
   PreferenceCategory notificationCategory = new PreferenceCategory(ctx);
   notificationCategory.setKey("notifications_category");
   notificationCategory.setTitle("Notifications");
   screen.addPreference(notificationCategory);
   /*
Warning: Add the PreferenceCategory to the PreferenceScreen before adding children to it.
 Preferences can't be added to a PreferenceCategory that isn't attached to the root screen.
    */
   notificationCategory.addPreference(notificationPreference);
   
   Preference feedbackPreference = new Preference(ctx);
   feedbackPreference.setKey("feedback");
   feedbackPreference.setTitle("Send feedback");
   feedbackPreference.setSummary("Report technical issues or suggest new features");
   
   PreferenceCategory helpCategory = new PreferenceCategory(ctx);
   helpCategory.setKey("help");
   helpCategory.setTitle("Help");
   screen.addPreference(helpCategory);
   helpCategory.addPreference(feedbackPreference);
   
   setPreferenceScreen(screen);
   
   // TODO: see
   //  https://developer.android.com/develop/ui/views/components/settings/programmatic-hierarchy#java
}
}
