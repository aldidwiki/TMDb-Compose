package com.aldiprahasta.tmdb.di

import com.aldiprahasta.tmdb.data.source.remote.MovieRemoteDataSource
import com.aldiprahasta.tmdb.data.source.remote.PersonRemoteDataSource
import com.aldiprahasta.tmdb.data.source.remote.TvRemoteDataSource
import com.aldiprahasta.tmdb.data.source.remote.network.RemoteService
import com.aldiprahasta.tmdb.utils.Constant
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

val remoteModule = module {
    singleOf(::provideRetrofit)
    singleOf(::provideRemoteService)

    singleOf(::MovieRemoteDataSource)
    singleOf(::PersonRemoteDataSource)
    singleOf(::TvRemoteDataSource)
}

private fun provideRetrofit(): Retrofit {
    val loggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)

    val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                        .addHeader(
                                "Authorization",
                                "Bearer ${Constant.getApiKey()}"
                        )
                        .addHeader("accept", "application/json")
                        .build()

                chain.proceed(request)
            }
            .build()

    val mediaType = "application/json".toMediaType()
    val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    return Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(json.asConverterFactory(mediaType))
            .client(client)
            .build()
}

private fun provideRemoteService(retrofit: Retrofit): RemoteService {
    return retrofit.create(RemoteService::class.java)
}