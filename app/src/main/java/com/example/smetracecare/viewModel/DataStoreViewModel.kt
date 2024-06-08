package com.example.smetracecare.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.smetracecare.data.SharedPreferences
import kotlinx.coroutines.launch

class DataStoreViewModel(private val preferences: SharedPreferences) : ViewModel() {



    fun getToken(): LiveData<String> {
        return preferences.getToken().asLiveData()
    }

    fun getName(): LiveData<String> {
        return preferences.getNameData().asLiveData()
    }

    fun getUserID(): LiveData<String> {
        return preferences.getuserIDData().asLiveData()
    }

    fun saveToken(token: String) {
        viewModelScope.launch {
            preferences.saveToken(token)
        }
    }

    fun saveUserID(userID: String) {
        viewModelScope.launch {
            preferences.saveUserIDData(userID)
        }
    }

    fun getRole(): LiveData<String> {
        return preferences.getRoleData().asLiveData()
    }

    fun saveRole(role: String){
        viewModelScope.launch {
            preferences.saveRoleData(role)
        }
    }

    fun saveName(name: String) {
        viewModelScope.launch {
            preferences.saveName(name)
        }
    }
    fun clearLogin() {
        viewModelScope.launch {
            preferences.clearLogin()
        }
    }

    fun saveLogin(loginSes: Boolean){
        viewModelScope.launch {
            preferences.saveLoginSession(loginSes)
        }
    }
}