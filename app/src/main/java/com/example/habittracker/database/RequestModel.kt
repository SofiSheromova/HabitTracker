package com.example.habittracker.database

import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import java.util.*

@Entity(tableName = "request_table")
@TypeConverters(BodyConverter::class)
class RequestModel(
    var url: String,
    var method: String,
    var body: RequestBody?,
    var index: Int,
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
) {
    constructor(request: Request, index: Int) : this(
        request.url.toString(),
        request.method,
        request.body,
        index
    )

    fun toRequest(): Request? {
        return try {
            Request.Builder()
                .url(this.url)
                .method(
                    this.method,
                    if (this.method == "GET") null else this.body
                )
                .build()
        } catch (e: Exception) {
            Log.d("TAG-NETWORK", "Cast exception RequestModel to Request")
            null
        }
    }
}

class BodyConverter {
    @TypeConverter
    fun fromRequestBody(body: RequestBody?): String {
        val buffer = Buffer()
        body?.writeTo(buffer)
        return buffer.readUtf8()
    }

    @TypeConverter
    fun toRequestBody(json: String): RequestBody {
        return json.toRequestBody("application/json".toMediaTypeOrNull())
    }
}
