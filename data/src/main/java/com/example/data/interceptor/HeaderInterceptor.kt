package com.example.data.interceptor

import com.example.data.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        var request = chain.request()
        request = request.newBuilder()
            .addHeader(
                "Authorization",
                BuildConfig.AUTHORIZATION_TOKEN
            )
            .build()
        return chain.proceed(request)
    }
}