package com.davidsinner.candidate.cleanarquitecture.data.network


import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import java.io.IOException

class NetworkInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Content-Type", "application/json")
            .build()

        println("Request URL: ${request.url}")
        println("Request Headers: ${request.headers}")

        val requestBody = request.body
        if (requestBody != null) {
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            println("Request Body: ${buffer.readUtf8()}")
        }

        val response = chain.proceed(request)

        println("Response Code: ${response.code}")
        println("Response Headers: ${response.headers}")

        val responseBody = response.body
        val responseBodyString = responseBody?.string()

        println("Response Body: $responseBodyString")

        val newResponseBody =
            (responseBodyString ?: "").toResponseBody(responseBody?.contentType())
        return response.newBuilder().body(newResponseBody).build()
    }
}

