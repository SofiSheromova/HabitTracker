package com.example.habittracker

import android.os.SystemClock
import android.util.Log
import com.example.habittracker.database.RequestDao
import com.example.habittracker.database.RequestModel
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.*


class RequestManager(
    private val requestDao: RequestDao,
    private val isConnected: () -> Boolean
) {

    private val requestQueue: Queue<Request> = LinkedList()

    private fun execute(chain: Interceptor.Chain): List<Response> {
        val responses: MutableList<Response> = mutableListOf()
        var lastResponse: Response? = null
        val isOnline = isConnected()

        while (requestQueue.isNotEmpty() && isOnline) {
            lastResponse?.close()
            val request = requestQueue.element()
            // TODO кажется, что так делать неправильно и лучше отправлять запосы иначе
            lastResponse = chain.proceed(request)
            responses.add(lastResponse)

            if (lastResponse.isSuccessful) {
                Log.d("TAG-NETWORK", "Success: ${lastResponse.code}, ${lastResponse.message}")
                requestQueue.poll()
            } else if (lastResponse.isClientError()) {
                // TODO это не ui thread, тут нельзя что-то сообщить пользователю :(
                Log.d("TAG-NETWORK", "Client Error: ${request.method} ${lastResponse.code}")
                requestQueue.poll()
            } else {
                Log.d("TAG-NETWORK", "Error: ${lastResponse.code}, ${lastResponse.message}")
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

        if (!isOnline) {
            throw IOException("No internet connection")
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
                    "Interceptor: request is not successful  №$attemptsCount"
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