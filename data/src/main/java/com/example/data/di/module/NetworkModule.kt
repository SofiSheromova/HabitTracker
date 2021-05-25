package com.example.data.di.module

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.data.di.interfaces.SimpleOkHttpClient
import com.example.data.di.interfaces.StorageRequestsOkHttpClient
import com.example.data.local.db.HabitDatabase
import com.example.data.remote.RequestManager
import com.example.data.remote.api.HabitApi
import com.example.data.remote.builder.RetrofitBuilder
import com.example.data.remote.interceptor.HeaderInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofit(
        retrofitBuilder: RetrofitBuilder,
        @StorageRequestsOkHttpClient client: OkHttpClient
    ): Retrofit = retrofitBuilder
        .setOkHttpClientBuilder(client.newBuilder())
//        .addInterceptors(headerInterceptor)
        .build()

    @Singleton
    @Provides
    fun provideHabitsApi(retrofit: Retrofit): HabitApi = retrofit.create(HabitApi::class.java)

    @SimpleOkHttpClient
    @Singleton
    @Provides
    fun provideSimpleClient(
        headerInterceptor: HeaderInterceptor,
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        val dispatcher = Dispatcher()
        dispatcher.maxRequests = 1

        return OkHttpClient.Builder()
            .addInterceptor(headerInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .dispatcher(dispatcher)
            .build()
    }

    @Singleton
    @Provides
    fun provideHeaderInterceptor(): HeaderInterceptor {
        return HeaderInterceptor()
    }

    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor { message -> Log.d("TAG-NETWORK", message) }
            .apply { level = HttpLoggingInterceptor.Level.BODY }
    }

    @Singleton
    @Provides
    fun provideRequestManager(
        habitDatabase: HabitDatabase,
        @SimpleOkHttpClient client: OkHttpClient,
        context: Context
    ): RequestManager {
        fun isConnected(): Boolean {
            val connectivityManager = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val capabilities = connectivityManager
                .getNetworkCapabilities(connectivityManager.activeNetwork)

            return capabilities != null
                    && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        }

        return RequestManager(habitDatabase.requestDao(), ::isConnected, client)
    }

    @StorageRequestsOkHttpClient
    @Singleton
    @Provides
    fun provideStorageRequestsClient(
        @SimpleOkHttpClient simpleClient: OkHttpClient,
        requestManager: RequestManager
    ): OkHttpClient {
        return simpleClient.newBuilder()
            .addInterceptor(requestManager.interceptor)
            .build()
    }
}