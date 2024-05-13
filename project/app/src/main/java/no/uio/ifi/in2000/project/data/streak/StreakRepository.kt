package no.uio.ifi.in2000.project.data.streak

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

data class StreakStatus (
    // On format yyyy-MM-dd
    val streak: Int,
    val lastDate: String
)

class StreakRepository(private val dataStore: DataStore<Preferences>) {

    private val TAG: String = "StreakDataStore"

    private object PreferenceKeys {
        val STREAK = intPreferencesKey("streak")
        val LAST_DATE = stringPreferencesKey("last_date")
    }

    val userPreferencesFlow: Flow<StreakStatus> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            mapStreakStatus(preferences)
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