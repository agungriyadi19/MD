package com.example.smetracecare.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smetracecare.R
import com.example.smetracecare.data.ErrorResponse
import com.example.smetracecare.data.ResponseAddPembatik
import com.example.smetracecare.retrofit.ApiConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response

class PembatikAddViewModel : ViewModel() {
    var isError: Boolean = false

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    fun addPembatik(
        file: MultipartBody.Part,
        token: String,
        name: RequestBody,
        description: RequestBody,
        startedYear: RequestBody,
        smeId: RequestBody
    ) {
        _loading.value = true
        val api = ApiConfig.getApiService().addPembatik(file, name, description, startedYear, smeId,token)
        api.enqueue(object : retrofit2.Callback<ResponseAddPembatik> {
            override fun onResponse(call: Call<ResponseAddPembatik>, response: Response<ResponseAddPembatik>) {
                _loading.value = false

                Log.d("PembatikAddViewModel", "Response code: ${response.code()}")
                val responseBody = response.body()
                Log.d("add material success", response.isSuccessful.toString())
                responseBody?.let {
                    Log.d("PembatikAddViewModel", "Response body: $it")
                }
                if (response.isSuccessful) {
                    isError = false
                } else {
                    isError = true
                    Log.d("PembatikAddViewModel", "Error response: ${response.errorBody()}")

                    response.errorBody()?.let { errorBody ->
                        try {
                            val errorJson = JSONObject(errorBody.string())
                            Log.d("PembatikAddViewModel", "Error JSON: $errorJson")

                            val gson = Gson()
                            val type = object : TypeToken<ErrorResponse>() {}.type
                            val errorResponse: ErrorResponse? =
                                gson.fromJson(errorBody.charStream(), type)
                            Log.d("PembatikAddViewModel", "Error response body: $errorResponse")

                            errorResponse?.let {
                                Log.e("PembatikAddViewModel", "Error message: ${it.message}")
                            }
                        } catch (e: Exception) {
                            Log.e("PembatikAddViewModel", "Error parsing response: ${e.message}")
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResponseAddPembatik>, t: Throwable) {
                Log.d("material response", t.message.toString())

                isError = true
                _loading.value = false
                _message.value = R.string.error_message.toString() + t.message.toString()
            }
        })
    }
}