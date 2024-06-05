package com.example.smetracecare.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smetracecare.R
import com.example.smetracecare.data.DataLogin
import com.example.smetracecare.data.LoginResponse
import com.example.smetracecare.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Response

class LoginViewModel : ViewModel() {
    var isError: Boolean = false

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _userLogin = MutableLiveData<LoginResponse>()
    val userLogin: LiveData<LoginResponse> = _userLogin

    fun getLoginResponse(dataLogin: DataLogin) {
        _loading.value = true
        val api = ApiConfig.getApiService().userLogin(dataLogin)
        api.enqueue(object : retrofit2.Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _loading.value = false
                val responseBody = response.body()

                if (response.isSuccessful) {
                    isError = false
                    _userLogin.value = responseBody!!
                    _message.value = "${_userLogin.value!!.loginResult?.name} berhasil login"
                } else {
                    isError = true
                    when (response.code()) {
                        401 -> _message.value = "Email atau password yang anda masukan salah, silahkan coba lagi"
                        408 -> _message.value = "Koneksi internet anda lambat, silahkan coba lagi"
                        else -> _message.value = R.string.error_message.toString() + response.message()
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                isError = true
                _loading.value = false
                _message.value = R.string.error_message.toString() + t.message.toString()
            }
        })
    }
}