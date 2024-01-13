package com.romainperrier.tp1.data

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.romainperrier.tp1.list.Task
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface UserWebService {
    @GET("/sync/v9/user/")
    suspend fun fetchUser(): Response<User>
    @Multipart
    @POST("sync/v9/update_avatar")
    suspend fun updateAvatar(@Part avatar: MultipartBody.Part): Response<User>
    @PATCH("sync/v9/sync")
    suspend fun update(@Body user: UserUpdate): Response<Unit>
}
interface TasksWebService {
    @GET("/rest/v2/tasks/")
    suspend fun fetchTasks(): Response<List<Task>>
    @POST("/rest/v2/tasks/")
    suspend fun create(@Body task: Task): Response<Task>

    @POST("/rest/v2/tasks/{id}")
    suspend fun update(@Body task: Task, @Path("id") id: String = task.id): Response<Task>

// Complétez avec les méthodes précédentes, la doc de l'API, et celle de Retrofit:
    @GET("/rest/v2/tasks/{id}")
    suspend fun delete(@Path("id") id: String): Response<Unit>
}
object API {
    private const val TOKEN = "c3a4ae0776523a0e265691c40da8ea7e49f16911"

    private val retrofit by lazy {
        // client HTTP
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor { chain ->
                // intercepteur qui ajoute le `header` d'authentification avec votre token:
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $TOKEN")
                    .build()
                chain.proceed(newRequest)
            }
            .build()

        // transforme le JSON en objets kotlin et inversement
        val jsonSerializer = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            encodeDefaults = true
        }

        // instance retrofit pour implémenter les webServices:
        Retrofit.Builder()
            .baseUrl("https://api.todoist.com/")
            .client(okHttpClient)
            .addConverterFactory(jsonSerializer.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    val userWebService : UserWebService by lazy {
        retrofit.create(UserWebService::class.java)
    }
    val taskWebService : TasksWebService by lazy {
        retrofit.create(TasksWebService::class.java)
    }
}
