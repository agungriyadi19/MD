package com.example.smetracecare.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smetracecare.data.ErrorResponse
import com.example.smetracecare.data.GetSupplierProfileResponse
import com.example.smetracecare.data.UpdateSupplierProfileRequest
import com.example.smetracecare.data.UpdateSupplierProfileResponse
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

    private val _updateResponse = MutableLiveData<UpdateSupplierProfileResponse>()
    val updateResponse: LiveData<UpdateSupplierProfileResponse> = _updateResponse

    private val _supplierProfile = MutableLiveData<GetSupplierProfileResponse>()
    val supplierProfile: LiveData<GetSupplierProfileResponse> = _supplierProfile

    fun getSupplierProfile(token: String, userId: String) {
        _loading.value = true
        val api = ApiConfig.getApiService().getSupplierProfile(token, userId)
        api.enqueue(object : Callback<GetSupplierProfileResponse> {
            override fun onResponse(call: Call<GetSupplierProfileResponse>, response: Response<GetSupplierProfileResponse>) {
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

            override fun onFailure(call: Call<GetSupplierProfileResponse>, t: Throwable) {
                _loading.value = false
                _message.value = t.message
            }
        })
    }
    fun updateSupplierProfile(token: String, userId: String, request: UpdateSupplierProfileRequest) {
        _loading.value = true
        val api = ApiConfig.getApiService().updateSupplierProfile(token, userId, request)
        api.enqueue(object : Callback<UpdateSupplierProfileResponse> {
            override fun onResponse(call: Call<UpdateSupplierProfileResponse>, response: Response<UpdateSupplierProfileResponse>) {
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

            override fun onFailure(call: Call<UpdateSupplierProfileResponse>, t: Throwable) {
                _loading.value = false
                _message.value = t.message
            }
        })
    }
}
