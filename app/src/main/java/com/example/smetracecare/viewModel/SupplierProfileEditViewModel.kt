package com.example.smetracecare.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smetracecare.data.ErrorResponse
import com.example.smetracecare.data.GetProfileResponse
import com.example.smetracecare.data.UpdateProfileRequest
import com.example.smetracecare.data.UpdateProfileResponse
import com.example.smetracecare.retrofit.ApiConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SupplierProfileEditViewModel : ViewModel() {
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _updateResponse = MutableLiveData<UpdateProfileResponse>()
    val updateResponse: LiveData<UpdateProfileResponse> = _updateResponse

    private val _supplierProfile = MutableLiveData<GetProfileResponse>()
    val supplierProfile: LiveData<GetProfileResponse> = _supplierProfile

    fun getSupplierProfile(token: String, userId: String) {
        _loading.value = true
        val api = ApiConfig.getApiService().getSupplierProfile(token, userId)
        api.enqueue(object : Callback<GetProfileResponse> {
            override fun onResponse(call: Call<GetProfileResponse>, response: Response<GetProfileResponse>) {
                _loading.value = false
                if (response.isSuccessful) {
                    _supplierProfile.value = response.body()
                } else {
                    val gson = Gson()
                    val type = object : TypeToken<ErrorResponse>() {}.type
                    val errorResponse: ErrorResponse? = gson.fromJson(response.errorBody()?.charStream(), type)
                    _message.value = errorResponse?.message ?: "Unknown error"
                }
            }

            override fun onFailure(call: Call<GetProfileResponse>, t: Throwable) {
                _loading.value = false
                _message.value = t.message
            }
        })
    }
    fun updateSupplierProfile(token: String, userId: String, request: UpdateProfileRequest) {
        _loading.value = true
        val api = ApiConfig.getApiService().updateSupplierProfile(token, userId, request)
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
