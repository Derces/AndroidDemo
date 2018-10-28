package com.example.user.androiddemdo
import kotlinx.coroutines.experimental.async
import java.net.URL
import java.net.URLConnection

class HttpURLCommunicate() {

    public suspend fun HttpGet(url: URL) :String{
        val deferred = async {
            val URLconnection: URLConnection = url.openConnection()
            return@async URLconnection.inputStream.bufferedReader().readText()
        }
        return  deferred.await()
    }
}



