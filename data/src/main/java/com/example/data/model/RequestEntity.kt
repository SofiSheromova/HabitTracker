package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.data.Constants.REQUEST_TABLE_NAME
import com.example.data.base.EntityMapper
import com.example.data.base.ModelEntity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Entity(tableName = REQUEST_TABLE_NAME)
@TypeConverters(BodyConverter::class)
class RequestEntity(
    var url: String,
    var method: String,
    var body: RequestBody?,
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
) : ModelEntity

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

@Singleton
class RequestEntityMapper @Inject constructor() : EntityMapper<Request, RequestEntity> {
    override fun mapToDomain(entity: RequestEntity): Request {
        return Request.Builder()
            .url(entity.url)
            .method(
                entity.method,
                if (entity.method == "GET") null else entity.body
            )
            .build()
    }

    override fun mapToEntity(model: Request): RequestEntity {
        return RequestEntity(model.url.toString(), model.method, model.body)
    }
}
