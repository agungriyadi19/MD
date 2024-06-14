package com.example.smetracecare.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smetracecare.R
import com.example.smetracecare.data.ErrorResponse
import com.example.smetracecare.data.GetSmeResponse
import com.example.smetracecare.retrofit.ApiConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Response

class SmeProfileViewModel : ViewModel() {
    var isError: Boolean = false

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _userProfile = MutableLiveData<GetSmeResponse>()
    val userProfile: LiveData<GetSmeResponse> = _userProfile

    fun getSmeProfileResponse(token:String, userId: String) {
        Log.d("userId", userId)
        _loading.value = true
        val api = ApiConfig.getApiService().getSmeProfile(token, userId)
        api.enqueue(object : retrofit2.Callback<GetSmeResponse> {
            override fun onResponse(call: Call<GetSmeResponse>, response: Response<GetSmeResponse>) {
                _loading.value = false
                val responseBody = response.body()
                Log.d("Profile response", responseBody.toString())
                if (response.isSuccessful) {
                    isError = false
                    _userProfile.value = responseBody!!
                } else {
                    isError = true

                    response.errorBody()?.let { errorBody ->
                        val gson = Gson()
                        val type = object : TypeToken<ErrorResponse>() {}.type
                        val errorResponse: ErrorResponse? = gson.fromJson(errorBody.charStream(), type)

                        errorResponse?.let {
                            Log.e("API Error", it.message)

                            when (response.code()) {
                                408 -> _message.value = "Koneksi internet anda lambat, silahkan coba lagi"
                                else -> _message.value = it.message
                            }
                        }
                    }

                }
            }

            override fun onFailure(call: Call<GetSmeResponse>, t: Throwable) {
                isError = true
                _loading.value = false
                _message.value = R.string.error_message.toString() + t.message.toString()
            }
        })
    }
}