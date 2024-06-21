package com.example.smetracecare.retrofit

import com.example.smetracecare.data.DataAddMaterial
import com.example.smetracecare.data.DataLogin
import com.example.smetracecare.data.DataRegister
import com.example.smetracecare.data.DetailResponse
import com.example.smetracecare.data.LoginResponse
import com.example.smetracecare.data.GetMaterial
import com.example.smetracecare.data.GetPembatik
import com.example.smetracecare.data.GetProduct
import com.example.smetracecare.data.GetProfileResponse
import com.example.smetracecare.data.GetSmeMaterial
import com.example.smetracecare.data.GetSmeResponse
import com.example.smetracecare.data.ResponseAddMaterial
import com.example.smetracecare.data.ResponseAddPembatik
import com.example.smetracecare.data.ResponseAddProduct
import com.example.smetracecare.data.UpdateProfileRequest
import com.example.smetracecare.data.UpdateProfileResponse
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
    ): Call<GetProfileResponse>

    @GET("umkm/{userId}")
    fun getSmeProfile(
        @Header("authorization") token: String,
        @Path("userId") userId: String
    ): Call<GetSmeResponse>

    @PUT("suppliers/{userId}")
    fun updateSupplierProfile(
        @Header("authorization") token: String,
        @Path("userId") userId: String,
        @Body request: UpdateProfileRequest
    ): Call<UpdateProfileResponse>

    @PUT("umkm/{userId}")
    fun updateSmeProfile(
        @Header("authorization") token: String,
        @Path("userId") userId: String,
        @Body request: UpdateProfileRequest
    ): Call<UpdateProfileResponse>


    @GET("barang_mentah")
    fun getMaterialSme(
        @Header("authorization") token: String
    ): Call<GetSmeMaterial>

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


    @GET("profil_pembatik/umkm/{userId}")
    fun getPembatik(
        @Header("authorization") token: String,
        @Path("userId") userId: String
    ): Call<GetPembatik>

    @Multipart
    @POST("profil_pembatik")
    fun addPembatik(
        @Part file:MultipartBody.Part,
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("startedYear") startedYear: RequestBody,
        @Part("sme_id") smeId: RequestBody,
        @Header("authorization") token: String
    ): Call<ResponseAddPembatik>

    @Multipart
    @PUT("profil_pembatik/{pembatik_id}")
    fun editPembatik(
        @Path("pembatik_id") pembatikId: String,
        @Part file:MultipartBody.Part,
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("startedYear") startedYear: RequestBody,
        @Part("sme_id") smeId: RequestBody,
        @Header("authorization") token: String
    ): Call<ResponseAddPembatik>

    @DELETE("profil_pembatik/{pembatik_id}")
    fun deletePembatik(
        @Header("authorization") token: String,
        @Path("pembatik_id") pembatikId: String
    ): Call<DetailResponse>

    @GET("profil_pembatik/{pembatik_id}")
    fun getDetailPembatik(
        @Header("authorization") token: String,
        @Path("pembatik_id") pembatikId: String
    ): Call<ResponseAddPembatik>


    @GET("products/umkm/{userId}")
    fun getProduct(
        @Header("authorization") token: String,
        @Path("userId") userId: String
    ): Call<GetProduct>

    @Multipart
    @POST("products")
    fun addProduct(
        @Part file:MultipartBody.Part,
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("startedYear") startedYear: RequestBody,
        @Part("sme_id") smeId: RequestBody,
        @Header("authorization") token: String
    ): Call<ResponseAddProduct>

    @Multipart
    @PUT("products/{product_id}")
    fun editProduct(
        @Path("product_id") productId: String,
        @Part file:MultipartBody.Part,
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("startedYear") startedYear: RequestBody,
        @Part("sme_id") smeId: RequestBody,
        @Header("authorization") token: String
    ): Call<ResponseAddProduct>

    @DELETE("products/{product_id}")
    fun deleteProduct(
        @Header("authorization") token: String,
        @Path("product_id") productId: String
    ): Call<DetailResponse>

    @GET("products/{product_id}")
    fun getDetailProduct(
        @Header("authorization") token: String,
        @Path("product_id") productId: String
    ): Call<ResponseAddProduct>

}