package com.example.branchinternational

import com.example.branchinternational.model.Message

const val BASE_URL = "https://android-messaging.branch.co/"

var AUTH_TOKEN:String?=null
var threads:MutableMap<Int,MutableList<Message>> = mutableMapOf()

const val ADAPTER_THREAD=1
const val ADAPTER_MESSAGE=0

