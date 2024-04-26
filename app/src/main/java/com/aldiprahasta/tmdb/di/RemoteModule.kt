package com.aldiprahasta.tmdb.di

import com.aldiprahasta.tmdb.data.source.remote.MovieRemoteDataSource
import com.aldiprahasta.tmdb.data.source.remote.PersonRemoteDataSource
import com.aldiprahasta.tmdb.data.source.remote.network.RemoteService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val remoteModule = module {
    singleOf(::provideRetrofit)
    singleOf(::provideRemoteService)

    singleOf(::MovieRemoteDataSource)
    singleOf(::PersonRemoteDataSource)
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
                    "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIxNTBlZjRkN2I0ZDNjOTk1MzUxOGE2ZTJlZDQ5OTI4ZSIsInN1YiI6IjVmZDZkYjUyZDhlMjI1MDA0MTFiMzZlMSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.ObyQC30cOIxcfWiFBzp4mFX3BMxsQky6BXnONZtrzQw"
                )
                .addHeader("accept", "application/json")
                .build()

            chain.proceed(request)
        }
        .build()

    return Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()
}

private fun provideRemoteService(retrofit: Retrofit): RemoteService {
    return retrofit.create(RemoteService::class.java)
}