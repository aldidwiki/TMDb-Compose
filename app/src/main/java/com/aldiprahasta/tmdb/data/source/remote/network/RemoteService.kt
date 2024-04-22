package com.aldiprahasta.tmdb.data.source.remote.network

import com.aldiprahasta.tmdb.data.source.remote.response.movie.MovieResponse
import retrofit2.Response
import retrofit2.http.GET

interface RemoteService {
    @GET("movie/popular")
    suspend fun getPopularMovies(): Response<MovieResponse>
}