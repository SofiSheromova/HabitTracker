package com.example.habittracker

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.SystemClock
import android.util.Log
import com.example.habittracker.database.HabitDao
import com.example.habittracker.database.RequestDao
import com.example.habittracker.database.RequestModel
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException


class RequestManager(
    private val requestDao: RequestDao,
    private val applicationContext: Context
) {
    private fun isConnected(): Boolean {
        val connectivityManager = applicationContext
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val capabilities = connectivityManager
            .getNetworkCapabilities(connectivityManager.activeNetwork)

        return capabilities != null
                && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    private val requestQueue: MutableList<Request> = mutableListOf()

    private fun execute(chain: Interceptor.Chain): List<Response> {
        Log.d("TAG-NETWORK", "size: ${requestQueue.size}")

        val responses: MutableList<Response> = mutableListOf()
        var lastResponse: Response? = null
        val isOnline = isConnected()

        while (requestQueue.isNotEmpty() && isOnline) {
            lastResponse?.close()
            val request = requestQueue[0]
            lastResponse = chain.proceed(request)
            responses.add(lastResponse)
            if (lastResponse.isSuccessful) {
                Log.d(
                    "TAG-NETWORK",
                    "Success: ${lastResponse.code}, ${lastResponse.message}"
                )
                requestQueue.removeAt(0)
            } else if (lastResponse.isClientError()) {
                // TODO это не ui thread, тут нельзя что-то сообщить пользователю :(
                Log.d(
                    "TAG-NETWORK",
                    "Client Error: ${lastResponse.code}, ${request.method} ${request.body.toString()}"
                )
                requestQueue.removeAt(0)
            } else {
                Log.d(
                    "TAG-NETWORK",
                    "Error: ${lastResponse.code}, ${lastResponse.message}"
                )
                break
            }
        }

        CoroutineScope(CoroutineName("requests_saving")).launch {
            requestDao.deleteAll()
            requestDao.insertAll(*requestQueue
                .filter { it.method != "GET" }
                .mapIndexed { index, request -> RequestModel(request, index) }
                .toTypedArray())
        }
        Log.d("NETWORK", "REQUESTS COUNT: ${requestDao.getAll().size}")

        if (!isOnline) {
            throw IOException("no internet connection")
        }

        return responses
    }

    val interceptor: Interceptor = Interceptor { chain ->
        requestQueue.add(chain.request())
        var lastResponse = execute(chain).last()

        var attemptsCount = 0

        while (lastResponse.isServerError() && attemptsCount < MAX_ATTEMPTS_COUNT) {
            try {
                lastResponse = execute(chain).last()
            } catch (e: Exception) {
                Log.d(
                    "TAG-NETWORK",
                    "Request is not successful  №$attemptsCount"
                )
            } finally {
                attemptsCount++
                SystemClock.sleep(DELAY_BETWEEN_ATTEMPTS)
            }
        }
        lastResponse
    }

    companion object {
        private const val MAX_ATTEMPTS_COUNT: Int = 3
        private const val DELAY_BETWEEN_ATTEMPTS: Long = 1000
    }
}

private fun Response.isClientError(): Boolean {
    return this.code in 400..499
}

private fun Response.isServerError(): Boolean {
    return this.code >= 500
}

private fun <T> List<T>.last(): T {
    return this[this.lastIndex]
}