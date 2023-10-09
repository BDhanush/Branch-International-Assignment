package com.example.branchinternational

import com.example.branchinternational.model.Login
import com.example.branchinternational.model.Message
import retrofit2.Call
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface ApiInterface {

    @POST(value = "api/login")
    fun login(
        @Body login: Login
    ):Call<Map<String,String>>

    @GET(value = "api/messages")
    fun getMessages (
        @Header("X-Branch-Auth-Token") authToken:String
    ): Call<List<Message>>

    @POST(value = "api/messages")
    fun sendMessage(
        @Header("X-Branch-Auth-Token") authToken:String,
        @Body newMessage:Message
    ):Call<Message>
//
//    @Multipart
//    @POST(value = "add")
//    suspend fun upload(
//        @Part("image") image: RequestBody,
//        @Part("price") price: RequestBody,
//        @Part("product_name") productName: RequestBody,
//        @Part("product_type") productType: RequestBody,
//        @Part("tax") tax: RequestBody
//    ):ResponseBody
}