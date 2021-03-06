package com.mymikemiller.gamegrumpsplayer

import android.app.Activity
import android.os.Bundle
import android.preference.PreferenceFragment
import android.preference.PreferenceActivity
import android.preference.ListPreference
import android.content.SharedPreferences
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.widget.Toast


/**
 * The settings activity
 */
class PreferencesActivity : PreferenceActivity() {
    companion object {
        const val UNSKIP_ALL = "com.example.mymikemiller.UNSKIP_ALL"
        const val WATCH_HISTORY = "com.example.mymikemiller.WATCH_HISTORY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        fragmentManager.beginTransaction().replace(android.R.id.content, MyPreferenceFragment()).commit()
    }

    class MyPreferenceFragment : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.preferences)

            val pref = findPreference(getString(R.string.pref_playlistOrderKey))
            pref.summary = (pref as ListPreference).entry

            val unskipAllGamesButton = findPreference(getString(R.string.pref_unskipAllGamesKey))
            unskipAllGamesButton.setOnPreferenceClickListener({

                val intent = Intent()
                intent.action = UNSKIP_ALL
                LocalBroadcastManager.getInstance(activity).sendBroadcast(intent)

                Toast.makeText(getActivity(), getString(R.string.videosUnskipped),
                        Toast.LENGTH_SHORT).show()

                true
            })

            val watchHistoryButton = findPreference(getString(R.string.pref_watchHistoryKey))
            watchHistoryButton.setOnPreferenceClickListener({

                // Close the preferences pane so we go directly back to the main activity when we
                // select a video to play
                activity.finish()

                val intent = Intent()
                intent.action = WATCH_HISTORY
                LocalBroadcastManager.getInstance(activity).sendBroadcast(intent)

                true
            })
        }

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
            /* get preference */
            val preference = findPreference(key)

            /* update summary */
            if (activity != null) {
                if (key == getString(R.string.pref_playlistOrderKey)) {
                    preference.summary = (preference as ListPreference).entry
                }
            }
        }

        override fun onResume() {
            super.onResume()
            preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        }
    }

}