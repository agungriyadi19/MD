package com.example.smetracecare.retrofit

import com.example.smetracecare.data.DataAddMaterial
import com.example.smetracecare.data.DataLogin
import com.example.smetracecare.data.DataRegister
import com.example.smetracecare.data.DetailResponse
import com.example.smetracecare.data.LoginResponse
import com.example.smetracecare.data.GetSupplierProfileResponse
import com.example.smetracecare.data.GetMaterial
import com.example.smetracecare.data.ResponseAddMaterial
import com.example.smetracecare.data.UpdateSupplierProfileRequest
import com.example.smetracecare.data.UpdateSupplierProfileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("auth/login")
    fun userLogin(@Body requestLogin: DataLogin): Call<LoginResponse>

    @POST("auth/register")
    fun userRegis(@Body requestRegister: DataRegister): Call<DetailResponse>

    @GET("suppliers/{userId}")
    fun getSupplierProfile(
        @Header("authorization") token: String,
        @Path("userId") userId: String
    ): Call<GetSupplierProfileResponse>

    @PUT("suppliers/{userId}")
    fun updateSupplierProfile(
        @Header("authorization") token: String,
        @Path("userId") userId: String,
        @Body request: UpdateSupplierProfileRequest
    ): Call<UpdateSupplierProfileResponse>


    @GET("barang_mentah/supplier/{userId}")
    fun getMaterial(
        @Header("authorization") token: String,
        @Path("userId") userId: String
    ): Call<GetMaterial>

    @Multipart
    @POST("barang_mentah")
    fun addMaterial(
        @Part file:MultipartBody.Part,
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("type") type: RequestBody,
        @Part("supplier_id") supplierId: RequestBody,
        @Part("price") price: RequestBody,
        @Header("authorization") token: String
    ): Call<ResponseAddMaterial>

    @Multipart
    @PUT("barang_mentah/{materialId}")
    fun editMaterial(
        @Path("materialId") materialId: String,
        @Part file:MultipartBody.Part,
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("type") type: RequestBody,
        @Part("supplier_id") supplierId: RequestBody,
        @Part("price") price: RequestBody,
        @Header("authorization") token: String
    ): Call<ResponseAddMaterial>

    @DELETE("barang_mentah/{materialId}")
    fun deleteMaterial(
        @Header("authorization") token: String,
        @Path("materialId") materialId: String
    ): Call<DetailResponse>

    @GET("barang_mentah/{materialId}")
    fun getDetailMaterial(
        @Header("authorization") token: String,
        @Path("materialId") materialId: String
    ): Call<ResponseAddMaterial>


}