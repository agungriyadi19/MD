package com.example.smetracecare.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smetracecare.R
import com.example.smetracecare.data.ErrorResponse
import com.example.smetracecare.data.GetPembatik
import com.example.smetracecare.data.PembatikDetail
import com.example.smetracecare.retrofit.ApiConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Response

class PembatikViewModel : ViewModel() {
    var isError: Boolean = false

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _pembatik = MutableLiveData<List<PembatikDetail>>()
    val pembatik: LiveData<List<PembatikDetail>> get() = _pembatik

    fun GetPembatik(token:String, userId: String) {
        _loading.value = true
        val api = ApiConfig.getApiService().getPembatik(token, userId)
        api.enqueue(object : retrofit2.Callback<GetPembatik> {
            override fun onResponse(call: Call<GetPembatik>, response: Response<GetPembatik>) {
                _loading.value = false
                val responseBody = response.body()
                if (response.isSuccessful) {
                    isError = false
//                    _pembatik.value = responseBody!!
                    response.body()?.let {
                        _pembatik.value = it.result
                    }
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

            override fun onFailure(call: Call<GetPembatik>, t: Throwable) {
                isError = true
                _loading.value = false
                _message.value = R.string.error_message.toString() + t.message.toString()
            }
        })
    }
}