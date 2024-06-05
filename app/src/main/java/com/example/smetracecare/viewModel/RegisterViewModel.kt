package com.example.smetracecare.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smetracecare.R
import com.example.smetracecare.data.DataRegister
import com.example.smetracecare.data.DetailResponse
import com.example.smetracecare.data.ErrorResponse
import com.example.smetracecare.retrofit.ApiConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Response

class RegisterViewModel : ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    var isError: Boolean = false

    fun getRegisterResponse(dataRegister: DataRegister) {
        Log.d("data register", dataRegister.toString())
        _loading.value = true
        val api = ApiConfig.getApiService().userRegis(dataRegister)
        api.enqueue(object : retrofit2.Callback<DetailResponse> {
            override fun onResponse(call: Call<DetailResponse>, response: Response<DetailResponse>) {
                _loading.value = false
                Log.d("response regis", response.toString())
                if (response.isSuccessful){
                    isError = false
                    _message.value = "Berhasil membuat akun"
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

            override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                isError = true
                _loading.value = false
                _message.value = R.string.error_message.toString() + t.message.toString()
            }
        })
    }
}