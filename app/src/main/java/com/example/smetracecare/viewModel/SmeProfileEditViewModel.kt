package com.example.smetracecare.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smetracecare.data.ErrorResponse
import com.example.smetracecare.data.GetSmeResponse
import com.example.smetracecare.data.UpdateProfileRequest
import com.example.smetracecare.data.UpdateProfileResponse
import com.example.smetracecare.retrofit.ApiConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SmeProfileEditViewModel : ViewModel() {
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _updateResponse = MutableLiveData<UpdateProfileResponse>()
    val updateResponse: LiveData<UpdateProfileResponse> = _updateResponse

    private val _Profile = MutableLiveData<GetSmeResponse>()
    val Profile: LiveData<GetSmeResponse> = _Profile

    fun getProfile(token: String, userId: String) {
        _loading.value = true
        val api = ApiConfig.getApiService().getSmeProfile(token, userId)
        api.enqueue(object : Callback<GetSmeResponse> {
            override fun onResponse(call: Call<GetSmeResponse>, response: Response<GetSmeResponse>) {
                _loading.value = false
                if (response.isSuccessful) {
                    _Profile.value = response.body()
                } else {
                    val gson = Gson()
                    val type = object : TypeToken<ErrorResponse>() {}.type
                    val errorResponse: ErrorResponse? = gson.fromJson(response.errorBody()?.charStream(), type)
                    _message.value = errorResponse?.message ?: "Unknown error"
                }
            }

            override fun onFailure(call: Call<GetSmeResponse>, t: Throwable) {
                _loading.value = false
                _message.value = t.message
            }
        })
    }
    fun updateProfile(token: String, userId: String, request: UpdateProfileRequest) {
        _loading.value = true
        val api = ApiConfig.getApiService().updateSmeProfile(token, userId, request)
        api.enqueue(object : Callback<UpdateProfileResponse> {
            override fun onResponse(call: Call<UpdateProfileResponse>, response: Response<UpdateProfileResponse>) {
                _loading.value = false
                if (response.isSuccessful) {
                    _updateResponse.value = response.body()
                } else {
                    val gson = Gson()
                    val type = object : TypeToken<ErrorResponse>() {}.type
                    val errorResponse: ErrorResponse? = gson.fromJson(response.errorBody()?.charStream(), type)
                    _message.value = errorResponse?.message ?: "Unknown error"
                }
            }

            override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                _loading.value = false
                _message.value = t.message
            }
        })
    }
}
