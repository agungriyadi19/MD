package com.example.smetracecare.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.stringPreferencesKey

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SharedPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    private val role = stringPreferencesKey("role_setting")
    private val token = stringPreferencesKey("token_session")
    private val name = stringPreferencesKey("login_name")

    fun getRoleData(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[role] ?: ""
        }
    }

    suspend fun saveRoleData(dataRole: String) {
        dataStore.edit { preferences ->
            preferences[role] = dataRole
        }

    }

    fun getToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[token] ?: ""
        }
    }

    suspend fun saveToken(dataToken: String) {
        dataStore.edit { preferences ->
            preferences[token] = dataToken
        }
    }

    suspend fun saveName(dataName: String){
        dataStore.edit { pref ->
            pref[name] = dataName
        }
    }

    suspend fun clearLogin() {
        dataStore.edit { preferences ->
            preferences.remove(role)
            preferences.remove(name)
            preferences.remove(token)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: SharedPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): SharedPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = SharedPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}