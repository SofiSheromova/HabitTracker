package com.example.data.remote

import com.example.data.local.db.dao.RequestDao
import com.example.data.model.RequestEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.*


class RequestManager(
    private val requestDao: RequestDao,
    private val isConnected: () -> Boolean,
    private val client: OkHttpClient
) {
    private val requestQueue: Queue<Request> = LinkedList()

    val interceptor: Interceptor = Interceptor { chain ->
        tryToExecuteRequest(chain)
    }

    private fun tryToExecuteRequest(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (!isConnected()) {
            requestQueue.offer(request)
            throw IOException("No internet connection")
        }

        if (requestQueue.size != 0) {
            executeRequestQueue()

            if (requestQueue.size != 0)
                throw IOException("It is impossible to send a new request until the request queue is empty")
        }

        val response: Response = chain.proceed(request)

        if (response.isServerError())
            requestQueue.offer(request)

        return response
    }

    private fun executeRequestQueue(): List<Response> {
        if (!isConnected()) {
            throw IOException("No internet connection")
        }

        val responses: MutableList<Response> = mutableListOf()

        while (requestQueue.isNotEmpty()) {
            val request = requestQueue.element()
            val response = client.newCall(request).execute()
            responses.add(response)

            if (response.isSuccessful) {
                requestQueue.poll()
            } else if (response.isClientError()) {
                requestQueue.poll()
            } else {
                break
            }
        }

        return responses
    }

    suspend fun saveState() = withContext(Dispatchers.IO) {
        val requests = requestQueue
            .filter { it.method != "GET" }
            .mapIndexed { index, request -> RequestEntity(request, index) }
            .toTypedArray()

        // TODO вообще говоря плохо удалять все запросы, круто было бы их предобрабатывать
        //  чтобы если привычку создали, а потом поменяли, то был бы один запрос
        //  если создали, а потом удалили их бы не было
        requestDao.deleteAll()
        requestDao.insertAll(*requests)
    }
}

private fun Response.isClientError(): Boolean {
    return this.code in 400..499
}

private fun Response.isServerError(): Boolean {
    return this.code >= 500
}
