package com.example.smetracecare.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.stringPreferencesKey

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SharedPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    private val role = stringPreferencesKey("role_setting")
    private val userID = stringPreferencesKey("user_id")
    private val token = stringPreferencesKey("token_session")
    private val name = stringPreferencesKey("login_name")
    private val login = booleanPreferencesKey("login_session")

    fun getRoleData(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[role] ?: ""
        }
    }
    fun getuserIDData(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[userID] ?: ""
        }
    }
    fun getNameData(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[name] ?: ""
        }
    }

    suspend fun saveRoleData(dataRole: String) {
        dataStore.edit { preferences ->
            preferences[role] = dataRole
        }
    }
    suspend fun saveUserIDData(dataUserID: String) {
        dataStore.edit { preferences ->
            preferences[userID] = dataUserID
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


    fun getLoginSession(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[login] ?: false
        }
    }

    suspend fun saveLoginSession(dataSession: Boolean) {
        dataStore.edit { preferences ->
            preferences[login] = dataSession
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