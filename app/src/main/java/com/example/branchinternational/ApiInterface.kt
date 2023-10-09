package com.example.branchinternational

import com.example.branchinternational.model.Login
import com.example.branchinternational.model.Message
import retrofit2.Call
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

}