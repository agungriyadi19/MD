package com.example.smetracecare.retrofit

import com.example.smetracecare.data.DataLogin
import com.example.smetracecare.data.DataRegister
import com.example.smetracecare.data.DetailResponse
import com.example.smetracecare.data.LoginResponse
import com.example.smetracecare.data.GetSupplierProfileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("auth/login")
    fun userLogin(@Body requestLogin: DataLogin): Call<LoginResponse>

    @POST("auth/register")
    fun userRegis(@Body requestRegister: DataRegister): Call<DetailResponse>

    @Multipart
    @POST("stories")
    fun supplierUploadProduct(
        @Part file:MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Double?,
        @Part("lon") lon: Double?,
        @Header("Authorization") token: String
    ): Call<DetailResponse>

    @GET("suppliers/{userId}")
    fun getSupplierProfile(
        @Header("authorization") token: String,
        @Path("userId") userId: String
    ): Call<GetSupplierProfileResponse>

    @Multipart
    @POST("stories")
    fun supplierProfileEdit(
        @Part file:MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Double?,
        @Part("lon") lon: Double?,
        @Header("Authorization") token: String
    ): Call<DetailResponse>


}