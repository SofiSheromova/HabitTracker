package com.example.habittracker

import android.os.SystemClock
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response


class RequestManager {
    private val requestQueue: MutableList<Request> = mutableListOf()

    private fun execute(chain: Interceptor.Chain): List<Response> {
        Log.d("TAG-NETWORK", "size: ${requestQueue.size}")

        val responses: MutableList<Response> = mutableListOf()
        var lastResponse: Response? = null

        while (requestQueue.isNotEmpty()) {
            lastResponse?.close()
            lastResponse = chain.proceed(requestQueue[0])
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
                    "Client Error: ${lastResponse.code}, ${lastResponse.body}"
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