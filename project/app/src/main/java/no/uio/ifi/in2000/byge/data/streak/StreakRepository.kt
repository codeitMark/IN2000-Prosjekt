package no.uio.ifi.in2000.byge.data.streak

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first

data class StreakStatus (
    // On format yyyy-MM-dd
    val streak: Int,
    val lastDate: String
)

class StreakRepository(private val dataStore: DataStore<Preferences>) {

    private object PreferenceKeys {
        val STREAK = intPreferencesKey("streak")
        val LAST_DATE = stringPreferencesKey("last_date")
    }

    suspend fun updateLastDate(today: String, streak: Int) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.STREAK] = streak
            preferences[PreferenceKeys.LAST_DATE] = today
        }
    }

    suspend fun resetStreak(today: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.STREAK] = 1
            preferences[PreferenceKeys.LAST_DATE] = today
        }
    }

    suspend fun fetchInitialPreferences() =
        mapStreakStatus(dataStore.data.first().toPreferences())

    private fun mapStreakStatus(preferences: Preferences): StreakStatus {

        val streak = preferences[PreferenceKeys.STREAK] ?: 1
        val lastDate = preferences[PreferenceKeys.LAST_DATE] ?: "9999-99-99"

        return StreakStatus(streak, lastDate)
    }

}