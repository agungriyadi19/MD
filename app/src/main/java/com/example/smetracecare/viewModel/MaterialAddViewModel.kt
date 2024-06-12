package com.example.smetracecare.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smetracecare.R
import com.example.smetracecare.data.DataAddMaterial
import com.example.smetracecare.data.ErrorResponse
import com.example.smetracecare.data.GetMaterial
import com.example.smetracecare.data.MaterialDetail
import com.example.smetracecare.data.ResponseAddMaterial
import com.example.smetracecare.retrofit.ApiConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response

class MaterialAddViewModel : ViewModel() {
    var isError: Boolean = false

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    fun addMaterial(
        file: MultipartBody.Part,
        token: String,
        name: RequestBody,
        description: RequestBody,
        type: RequestBody,
        supplierId: RequestBody,
        price: RequestBody
    ) {
        _loading.value = true
        val api = ApiConfig.getApiService().addMaterial(file, name, description, type, supplierId, price,token)
        api.enqueue(object : retrofit2.Callback<ResponseAddMaterial> {
            override fun onResponse(call: Call<ResponseAddMaterial>, response: Response<ResponseAddMaterial>) {
                _loading.value = false
                val responseBody = response.body()
                Log.d("add material success", response.isSuccessful.toString())

                if (response.isSuccessful) {
                    isError = false
                } else {
                    isError = true
                    Log.d("add material error", response.toString())

                    response.errorBody()?.let { errorBody ->
                        val errorJson = JSONObject(errorBody.string())
                        Log.d("add material error 1", errorJson.toString())
                        val gson = Gson()
                        val type = object : TypeToken<ErrorResponse>() {}.type
                        val errorResponse: ErrorResponse? = gson.fromJson(errorBody.charStream(), type)
                        Log.d("add material error 2", errorResponse.toString())

                        errorResponse?.let {
                            Log.e("add material error 3", it.message)

                            when (response.code()) {
                                408 -> _message.value = "Koneksi internet anda lambat, silahkan coba lagi"
                                else -> _message.value = it.message
                            }
                        }
                    }

                }
            }

            override fun onFailure(call: Call<ResponseAddMaterial>, t: Throwable) {
                Log.d("material response", t.message.toString())

                isError = true
                _loading.value = false
                _message.value = R.string.error_message.toString() + t.message.toString()
            }
        })
    }
}