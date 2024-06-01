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

    fun saveToken(token: String) {
        viewModelScope.launch {
            preferences.saveToken(token)
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
}