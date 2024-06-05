package com.example.smetracecare.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.google.gson.annotations.SerializedName

data class DataLogin(
	var email: String,
	var password: String,
	var role: String
)

data class LoginResponse(
	@field:SerializedName("error")
	var error: Boolean,

	@field:SerializedName("message")
	var message: String,

	@field:SerializedName("result")
	var result: LoginResult?
)

data class LoginResult(
	@field:SerializedName("userId")
	var userId: String,

	@field:SerializedName("name")
	var name: String,

	@field:SerializedName("email")
	var email: String,

	@field:SerializedName("description")
	var description: String,

	@field:SerializedName("address")
	var address: String,

	@field:SerializedName("phoneNumber")
	var phoneNumber: String,

	@field:SerializedName("token")
	var token: String
)

data class ErrorResponse(
	@field:SerializedName("error")
	var error: Boolean,

	@field:SerializedName("message")
	var message: String
)
data class DataRegister(
	var name: String,
	var email: String,
	var password: String,
	var role: String,
	var description: String,
	var address: String,
	var phoneNumber: String,
	var confirmPassword: String
)

data class DetailResponse(
	var error: Boolean,
	var message: String
)

data class ResponseStory(
	var error: String,
	var message: String,
	var listStory: List<StoryDetailResponse>
)

@Parcelize
data class StoryDetailResponse (
	var id: String,
	var name: String,
	var description: String,
	var photoUrl: String,
	var createdAt: String,
	var lat: Double,
	var lon: Double
) : Parcelable
