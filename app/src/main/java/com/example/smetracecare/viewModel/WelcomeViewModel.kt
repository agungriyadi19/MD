package com.example.smetracecare.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.smetracecare.data.SharedPreferences
import kotlinx.coroutines.launch

class WelcomeViewModel(private val pref: SharedPreferences): ViewModel() {

}